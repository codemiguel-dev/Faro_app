package com.pack.faro;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.pack.faro.model.MapsPojo;

import java.util.Date;

public class AgregarSemaforo extends AppCompatActivity {

    //Craer variables
    public static final int REQUEST_CODE = 1;
    EditText lat, lon, detallesemaforo;
    Button btnagregarfaro, btnobtenercoordenada;

    ImageButton btnback;
    FusedLocationProviderClient fusedLocationProviderClient;
    DatabaseReference databaseReference;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_semaforo);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        //llamar los campos
        lat = findViewById(R.id.txtlatitud);
        lon = findViewById(R.id.txtlongitud);
        detallesemaforo = findViewById(R.id.txtdetalle);

        //llamar boton atras
        btnback = findViewById(R.id.backBtn);
        btnback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        //llamar boton
        btnagregarfaro = findViewById(R.id.btnIngresarSemaforo);
        btnagregarfaro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //llamar función

                String id = "semaforo" + new Date().getTime();
                String lati = lat.getText().toString();
                String longi = lon.getText().toString();
                String detalle = detallesemaforo.getText().toString();
                //validar campos
                if(lati.isEmpty())
                {
                    lat.setError("Error campo vacío debe presionar mostrar coordenada");
                }
                if(longi.isEmpty())
                {
                    lon.setError("Error campo vacío debe presionar mostrar coordenada");
                }
                // Validar que las coordenadas sean decimales
                if (!isValidDecimal(lati)) {
                    lat.setError("Latitud no es un decimal válido");
                    return;
                }

                if (!isValidDecimal(longi)) {
                    lon.setError("Longitud no es un decimal válido");
                    return;
                }
                if (detalle.isEmpty()){
                    detallesemaforo.setError("Error campo vacío debe al menos ingresar una descripción del semáforo, por ejemplo; Duración del semaforo 2 minutos");
                }
                else {
                    Double latitud = Double.parseDouble(lati);
                    Double longitud = Double.parseDouble(longi);
                    String detallefaro = detalle;

                    databaseReference.child("semaforo").child(id).setValue(new MapsPojo(latitud, longitud, detallefaro));
                    CharSequence text = "Agregó el semáforo correctamente :)";
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(AgregarSemaforo.this, text, duration);
                    toast.show();
                    startActivity(new Intent(AgregarSemaforo.this, MapsSemaforoActivity.class));
                    finish();
                }
            }
        });
        btnobtenercoordenada = findViewById(R.id.MostrarCoordenada);
        btnobtenercoordenada.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ObtenerCoordenadaActual();
            }
        });
    }

    private void ObtenerCoordenadaActual() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(AgregarSemaforo.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
        } else {
            // Verificar si el GPS está activado antes de intentar obtener la ubicación
            if (isLocationEnabled()) {
                getCoordenada();
            } else {
                // Si el GPS no está activado, mostrar un diálogo para que el usuario lo active
                Toast.makeText(this, "Por favor, activa el GPS", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        }
    }

    // Función para verificar si el GPS está activado
    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager != null && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    private void getCoordenada() {
        try {

            LocationRequest locationRequest = new LocationRequest();
            locationRequest.setInterval(10000);
            locationRequest.setFastestInterval(3000);
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                return;
            }
            LocationServices.getFusedLocationProviderClient(this).requestLocationUpdates(locationRequest, new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    super.onLocationResult(locationResult);
                    LocationServices.getFusedLocationProviderClient(AgregarSemaforo.this).removeLocationUpdates(this);
                    if (locationResult != null && locationResult.getLocations().size() > 0) {
                        int latestLocationIndex = locationResult.getLocations().size() - 1;
                        double latitud = locationResult.getLocations().get(latestLocationIndex).getLatitude();
                        double longitude = locationResult.getLocations().get(latestLocationIndex).getLongitude();
                        lat.setText(String.valueOf(latitud));
                        lon.setText(String.valueOf(longitude));
                    }

                }

            }, Looper.myLooper());

        }catch (Exception ex){
            System.out.println("Error es :" + ex);
        }
    }

    // Función para validar si una cadena es un decimal
    private boolean isValidDecimal(String str) {
        if (str.isEmpty()) {
            return false;
        }

        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}