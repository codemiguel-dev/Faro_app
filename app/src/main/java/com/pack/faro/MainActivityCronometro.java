package com.pack.faro;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivityCronometro extends AppCompatActivity {

    private TextView tvTimer;
    private Button startButton, stopButton;
    private ImageButton btnback;
    private Handler handler = new Handler();
    private long startTime, elapsedTime;
    private boolean isRunning = false;
    private String uid;

    private Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            elapsedTime = System.currentTimeMillis() - startTime;
            int seconds = (int) (elapsedTime / 1000);
            int minutes = seconds / 60;
            int hours = minutes / 60;
            seconds = seconds % 60;
            minutes = minutes % 60;

            tvTimer.setText(String.format("%02d:%02d:%02d", hours, minutes, seconds));
            handler.postDelayed(this, 1000);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main_cronometro);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tvTimer = findViewById(R.id.tvTimer);
        startButton = findViewById(R.id.startButton);
        stopButton = findViewById(R.id.stopButton);

        // Llamar botón atrás
        btnback = findViewById(R.id.backBtn);
        btnback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isRunning) {
                    startTime = System.currentTimeMillis() - elapsedTime;
                    handler.post(timerRunnable);
                    isRunning = true;
                }
            }
        });

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isRunning) {
                    handler.removeCallbacks(timerRunnable);
                    isRunning = false;
                    saveWorkingHours(elapsedTime);
                }
            }
        });

        // Obtener UID del usuario autenticado
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            uid = currentUser.getUid();
        } else {
            Toast.makeText(this, "No hay usuario autenticado", Toast.LENGTH_SHORT).show();
            // Manejar el caso cuando no hay usuario autenticado
        }
    }

    private void saveWorkingHours(long elapsedTime) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users").child(uid).child("workinghours");

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String existingWorkingHours = dataSnapshot.getValue(String.class);
                long totalElapsedTime = elapsedTime;

                if (existingWorkingHours != null && !existingWorkingHours.isEmpty()) {
                    // Parse the existing working hours
                    String[] timeParts = existingWorkingHours.split(":");
                    int existingHours = Integer.parseInt(timeParts[0]);
                    int existingMinutes = Integer.parseInt(timeParts[1]);
                    int existingSeconds = Integer.parseInt(timeParts[2]);

                    // Convert existing working hours to milliseconds
                    long existingElapsedTime = (existingHours * 3600 + existingMinutes * 60 + existingSeconds) * 1000;

                    // Sum the existing elapsed time with the new elapsed time
                    totalElapsedTime += existingElapsedTime;
                }

                // Convert the total elapsed time back to hours, minutes, and seconds
                int seconds = (int) (totalElapsedTime / 1000);
                int minutes = seconds / 60;
                int hours = minutes / 60;
                seconds = seconds % 60;
                minutes = minutes % 60;

                String newWorkingHours = String.format("%02d:%02d:%02d", hours, minutes, seconds);

                ref.setValue(newWorkingHours)
                        .addOnSuccessListener(aVoid -> Toast.makeText(MainActivityCronometro.this, "Horas de trabajo guardadas", Toast.LENGTH_SHORT).show())
                        .addOnFailureListener(e -> Toast.makeText(MainActivityCronometro.this, "Error al guardar horas de trabajo", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(MainActivityCronometro.this, "Error al obtener horas de trabajo", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
