package com.pack.faro;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.pack.faro.databinding.ActivityRegisterBinding;
import com.pack.faro.tuto.tutoPerfil;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding binding;

    private static final int GALLERY_REQUEST_CODE = 100;
    private FirebaseAuth firebaseAuth;
    private Uri selectedImageUri;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Espere por favor");
        progressDialog.setCanceledOnTouchOutside(false);

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, LoginRegisterActivity.class));
                finish();
            }
        });

        binding.profileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE);
            }
        });

        binding.btnregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateData();
            }
        });
    }

    private String name = "", email = "", password = "", habilidad = "";

    private void validateData() {
        name = binding.nameEt.getText().toString().trim();
        email = binding.emailEt.getText().toString().trim();
        password = binding.passwordEt.getText().toString().trim();
        habilidad = binding.habilidadEt.getText().toString().trim();
        String cPassword = binding.cPasswordEt.getText().toString().trim();

        //validar campos
        if (name.isEmpty()) {
            binding.nameEt.setError("Error campo vacío debe ingresar un nombre");
        }
        if (email.isEmpty()) {
            binding.emailEt.setError("Error campo vacío debe ingresar un correo");
        }
        if (password.isEmpty()) {
            binding.passwordEt.setError("Error campo vacío debe ingresar una contraseña");
        }
        if (cPassword.isEmpty()) {
            binding.passwordEt.setError("Error campo vacío debe repetir la contraseña");
        }
        if (habilidad.isEmpty()) {
            binding.habilidadEt.setError("Error campo vacío menciones cualquier habilidad ejemplos: equilibrio monociclo, lanzamiento de clavas ,etc");
        } else {
            registerUser();
        }
    }

    private void registerUser() {
        progressDialog.setMessage("Creando cuenta");
        progressDialog.show();

        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                updateUserInfo();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(RegisterActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUserInfo() {
        progressDialog.setMessage("Guardando información del usuario");

        long timestamp = System.currentTimeMillis();

        String uid = firebaseAuth.getUid();

        // Subir la imagen a Firebase Storage
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("profile_images").child(uid);
        storageReference.putFile(selectedImageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Obtener la URL de la imagen subida
                        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String imageUrl = uri.toString();

                                // Actualizar la información del usuario en la base de datos
                                HashMap<String, Object> hashMap = new HashMap<>();
                                hashMap.put("uid", uid);
                                hashMap.put("email", email);
                                hashMap.put("name", name);
                                hashMap.put("habilidad", habilidad);
                                hashMap.put("profileimage", imageUrl);
                                hashMap.put("userType", "user");
                                hashMap.put("workinghours", "");
                                hashMap.put("personalincome", "");
                                hashMap.put("timestamp", timestamp);

                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
                                ref.child(uid)
                                        .setValue(hashMap)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                progressDialog.dismiss();
                                                Toast.makeText(RegisterActivity.this, "Cuenta registrada", Toast.LENGTH_SHORT).show();
                                                startActivity(new Intent(RegisterActivity.this, tutoPerfil.class));
                                                finish();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                progressDialog.dismiss();
                                                Toast.makeText(RegisterActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(RegisterActivity.this, "Error al subir la imagen: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            // Obtener la URI de la imagen seleccionada
            selectedImageUri = data.getData();

            // Cargar la imagen en tu ImageView (opcional)
            binding.profileImageView.setImageURI(selectedImageUri);
        }
    }
}
