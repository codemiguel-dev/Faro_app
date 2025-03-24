package com.pack.faro;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pack.faro.adapters.AdapterUser;
import com.pack.faro.databinding.ActivityUserListBinding;
import com.pack.faro.model.ModelUser;

import java.util.ArrayList;

public class UserListActivity extends AppCompatActivity {

    private ActivityUserListBinding binding;

    private ArrayList<ModelUser> userArrayList;

    private AdapterUser adapterUser;

    private String uid, correo, nombre, habilidad, image;

    private static final String TAG = "pdf_list_tag";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = getIntent();
        uid = intent.getStringExtra("uid");
        correo = intent.getStringExtra("email");
        nombre = intent.getStringExtra("name");
        habilidad = intent.getStringExtra("habilidad");
        image = intent.getStringExtra("profileimage");

        loaduserList();

        binding.searchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    adapterUser.getFilter().filter(s);
                }
                catch (Exception e){
                    Log.d(TAG, "onTextChanged"+e.getMessage());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    private void loaduserList() {

        userArrayList = new ArrayList<>();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userArrayList.clear();
                for (DataSnapshot ds : snapshot.getChildren()){
                    ModelUser modelUser = ds.getValue(ModelUser.class);
                    userArrayList.add(modelUser);
                }
                adapterUser = new AdapterUser(UserListActivity.this, userArrayList);
                binding.userRv.setAdapter(adapterUser);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}