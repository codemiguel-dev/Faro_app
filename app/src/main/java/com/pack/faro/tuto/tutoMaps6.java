package com.pack.faro.tuto;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.pack.faro.MainActivity;
import com.pack.faro.R;

public class tutoMaps6 extends AppCompatActivity {

    ImageButton btnizquierda;
    Button btnsaltartuto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tuto_maps6);

        btnizquierda = findViewById(R.id.previousButton);
        btnsaltartuto = findViewById(R.id.skipButton);

        btnizquierda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(tutoMaps6.this, tutoMaps1.class);
                startActivity(intent);
            }
        });


        btnsaltartuto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(tutoMaps6.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
}