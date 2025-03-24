package com.pack.faro;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.pack.faro.databinding.ActivityMainBinding;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private Handler locationHandler;

    private final int LOCATION_UPDATE_INTERVAL = 60000;
    private int MY_PERMISSIONS_REQUEST_READ_CONTACTS;

    private FusedLocationProviderClient mFusedLocationClient;

    DatabaseReference mDatabase;

    private Button mBtnMaps;
    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;

    private FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        firebaseAuth = FirebaseAuth.getInstance();
        checkUser();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String userId = firebaseUser.getUid();
        subirLatLongFirebase(userId);
        setSupportActionBar(binding.appBarMain.toolbar);
        binding.appBarMain.imageViewSalir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.signOut();
                checkUser();
            }
        });

        binding.appBarMain.imageViewCronometro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, MainActivityCronometro.class));
            }
        });

        binding.appBarMain.imageViewFinanza.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, MainActivityFinanza.class));
            }
        });

        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;


        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_perfiles, R.id.nav_slideshow, R.id.nav_semaforo, R.id.nav_perfil)
                .setOpenableLayout(drawer)
                .build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

    }


    private void checkUser() {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser == null) {
            startActivity(new Intent(this, LoginRegisterActivity.class));
            finish();
        } else {
            String email = firebaseUser.getEmail();
            String nombre = firebaseUser.getDisplayName();
            binding.textEmail.setText(email);
            binding.textname.setText(nombre);

            // Obtén la referencia al ImageView de la imagen de perfil
            ImageView profileImageView = findViewById(R.id.profileImageView);

            // Cargar la imagen de perfil desde Firebase Storage
            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("profile_images").child(firebaseUser.getUid());
            storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    // Utiliza una biblioteca de carga de imágenes como Glide o Picasso para cargar la imagen
                    // Aquí se utiliza Glide como ejemplo
                    Glide.with(getApplicationContext())
                            .load(uri)
                            .circleCrop() // Esta función realiza el recorte circular
                            .placeholder(R.drawable.sombrerodebufon) // Drawable de placeholder
                            .error(R.drawable.sombrerodebufon) // Drawable de error en caso de falla
                            .into(profileImageView);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    // Manejar la falla al obtener la URL de la imagen
                    // Puedes mostrar una imagen de error o dejar el ImageView con la imagen predeterminada
                }
            });
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    private void subirLatLongFirebase(String userId) {

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_READ_CONTACTS);
            return;
        }
        mFusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                DatabaseReference userRef = mDatabase.child("Users").child(userId);
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                if (location != null) {
                    userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                // El usuario existe en la base de datos
                                String habilidad = snapshot.child("habilidad").getValue(String.class);
                                // Si habilidad no es null, la agregamos al mapa de datos
                                if (habilidad != null) {
                                    Map<String, Object> latlang = new HashMap<>();
                                    latlang.put("habilidad", habilidad);
                                    latlang.put("email", firebaseUser.getEmail());
                                    latlang.put("latitud", location.getLatitude());
                                    latlang.put("longitud", location.getLongitude());
                                    mDatabase.child("gpsusuario").push().setValue(latlang);
                                }
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.e("Firebase", "Error al obtener la habilidad del usuario desde Firebase", error.toException());
                        }
                    });
                }
            }
        });
    }
    private void subirLatLongFirebaseUpdate(){
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_READ_CONTACTS);
            return;
        }
        mFusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    Log.e("Latitud: ",+location.getLatitude()+"Longitud: "+location.getLatitude());
                    Map<String, Object> latlang = new HashMap<>();
                    latlang.put("latitud", location.getLatitude());
                    latlang.put("longitud",location.getLongitude());
                    mDatabase.child("usuario").push().setValue(latlang);

                }
            }
        });
    }
}