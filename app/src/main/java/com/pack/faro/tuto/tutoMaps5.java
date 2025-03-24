package com.pack.faro.tuto;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.pack.faro.MainActivity;
import com.pack.faro.R;

public class tutoMaps5 extends AppCompatActivity {

    ImageButton btnizquierda, btnderecha;
    Button btnsaltartuto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tuto_maps5);

        btnizquierda = findViewById(R.id.previousButton);
        btnderecha = findViewById(R.id.nextButton);
        btnsaltartuto = findViewById(R.id.skipButton);

        btnizquierda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(tutoMaps5.this, tutoMaps4.class);
                startActivity(intent);
            }
        });

        btnderecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(tutoMaps5.this, tutoMaps6.class);
                startActivity(intent);
            }
        });

        btnsaltartuto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(tutoMaps5.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
}