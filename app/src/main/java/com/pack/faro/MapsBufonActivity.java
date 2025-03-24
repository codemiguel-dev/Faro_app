package com.pack.faro;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pack.faro.databinding.ActivityMapsBufonBinding;
import com.pack.faro.model.MapPojoUser;

import java.util.ArrayList;

public class MapsBufonActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsBufonBinding binding;

    private FirebaseAuth mAuth;

    private DatabaseReference mDatabase;

    private ArrayList<Marker> tmpRealTimeMarkers = new ArrayList<>();

    private ArrayList<Marker> realTimeMarkers = new ArrayList<>();

    private int MY_PERMISSIONS_REQUEST_READ_CONTACTS;

    private FusedLocationProviderClient mFusedLocationClient;

    private LinearLayout infoBarLayout;

    private TextView infoTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBufonBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        mAuth = FirebaseAuth.getInstance();

        checkLocationEnabled();

        // Inflar la barra de información y ocultarla inicialmente
        infoBarLayout = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.layout_detalle_bufon, null);
        infoBarLayout.setVisibility(View.GONE);

        infoTextView = infoBarLayout.findViewById(R.id.text_info);

        // Agregar la barra de información al layout principal
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        params.gravity = android.view.Gravity.BOTTOM;
        addContentView(infoBarLayout, params);

    }

    private void checkLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            // El GPS no está activado, muestra un diálogo para activarlo
            buildAlertMessageNoGps();
        }
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("El GPS está desactivado. ¿Deseas activarlo?")
                .setCancelable(false)
                .setPositiveButton("Sí", (dialog, id) -> startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)))
                .setNegativeButton("No", (dialog, id) -> dialog.cancel());
        final AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        //ubicación gps
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Solicitar permisos si no están concedidos
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    MY_PERMISSIONS_REQUEST_READ_CONTACTS);
            return;
        }
        mMap.setMyLocationEnabled(true);

        //habilitar botón para ubicacion
        mMap.getUiSettings().setMyLocationButtonEnabled(true);

        //cambiar tipo de mapa
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        // Habilitar la capa de tráfico
        mMap.setTrafficEnabled(true);


        // Agregar un oyente para tocar un marcador en el mapa
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                // Manejar el evento de tocar un marcador aquí
                mostrarDetalleMarcador(marker);
                return true; // Devolver true si deseas consumir el evento, de lo contrario, false
            }
        });

        binding.mapSemaforo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MapsBufonActivity.this, MapsSemaforoActivity.class));
                finish();
            }
        });

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eliminarUbicacionUsuario();
                onBackPressed();
            }
        });


        LocationManager locationManager = (LocationManager) MapsBufonActivity.this.getSystemService(Context.LOCATION_SERVICE);

        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        if (location != null) {
            LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));
        }


        LocationListener locationListener = new LocationListener() {

            @Override
            public void onLocationChanged(Location location) {

            }
            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 20, 0, locationListener);

        mostrarusuarios();

    }

    private void mostrarDetalleMarcador(Marker marker) {
        String titulo = marker.getTitle();

        // Actualizar el texto en la barra de información
        infoTextView.setText("Usuario: " + titulo);
      ;


        // Mostrar la barra de información
        infoBarLayout.setVisibility(View.VISIBLE);

        // Ocultar la barra de información después de 3 segundos (3000 milisegundos)
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                infoBarLayout.setVisibility(View.GONE);
            }
        }, 20000); // Cambia el tiempo según tus necesidades
    }
    private void eliminarUbicacionUsuario() {
        String uid = mAuth.getCurrentUser().getUid();
        mDatabase.child("gpsusuario").child(uid).removeValue();
    }
    private void mostrarusuarios() {

        mDatabase.child("gpsusuario").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (Marker marker:realTimeMarkers){
                    marker.remove();
                }

          // Obtén el correo electrónico del usuario


                    for(DataSnapshot snapshot1: snapshot.getChildren()){
                        MapPojoUser mp = snapshot1.getValue(MapPojoUser.class);
                        String email = mp.getEmail();
                        String habilidad = mp.getHabilidad();
                        Double latitud = mp.getLatitud();
                        Double longitud = mp.getLongitud();

                        MarkerOptions markerOptions = new MarkerOptions();
                        markerOptions.position(new LatLng(latitud,longitud));
                        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.bufon));
                        markerOptions.title(email+"\n"+"Habilidad: "+habilidad); // Agrega el título al marcador

                        tmpRealTimeMarkers.add(mMap.addMarker(markerOptions));
                    }


                realTimeMarkers.clear();
                realTimeMarkers.addAll(tmpRealTimeMarkers);



            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


}