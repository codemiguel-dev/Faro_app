package com.pack.faro;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class MainActivityFinanza extends AppCompatActivity {

    private ImageButton btnback;
    private Button btnguardar;

    private EditText txtIngreso;
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main_finanza);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Inicializar Firebase Database
        firebaseAuth = FirebaseAuth.getInstance(); // Inicializar Firebase Auth
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        // Inicializar componentes de UI
        btnback = findViewById(R.id.backBtn);
        btnguardar = findViewById(R.id.guardaringresoButton);
        txtIngreso = findViewById(R.id.txtingreso);

        btnback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        btnguardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardarIngreso();
            }
        });
    }

    private void guardarIngreso() {
        String ingresoStr = txtIngreso.getText().toString().trim();
        if (!ingresoStr.isEmpty()) {
            double ingreso = Double.parseDouble(ingresoStr);
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user != null) {
                String userId = user.getUid();
                databaseReference.child(userId).child("personalincome").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        double currentIncome = 0;
                        if (snapshot.exists()) {
                            Object incomeObj = snapshot.getValue();
                            if (incomeObj instanceof String) {
                                currentIncome = Double.parseDouble((String) incomeObj);
                            } else if (incomeObj instanceof Double) {
                                currentIncome = (Double) incomeObj;
                            }
                        }
                        double newIncome = currentIncome + ingreso;
                        databaseReference.child(userId).child("personalincome").setValue(newIncome)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(MainActivityFinanza.this, "Ingreso guardado exitosamente", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(MainActivityFinanza.this, "Error al guardar ingreso: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(MainActivityFinanza.this, "Error al recuperar ingreso: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(MainActivityFinanza.this, "Usuario no logueado", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(MainActivityFinanza.this, "Por favor ingrese un valor", Toast.LENGTH_SHORT).show();
        }
    }
}