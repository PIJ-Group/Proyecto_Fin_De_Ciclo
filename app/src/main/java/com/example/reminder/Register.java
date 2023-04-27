package com.example.reminder;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {
    Button btnRegister;
    EditText inputUserName,inputEmailText, inputPassText, inputConfirmedPassText;
    private FirebaseAuth mAuth;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore db;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        inputEmailText = findViewById(R.id.emailBox); //Texto del correo
        inputPassText = findViewById(R.id.passBox);
        inputConfirmedPassText = findViewById(R.id.confirmedPassBox);
        inputUserName = findViewById(R.id.completeName);


        btnRegister = findViewById(R.id.userRegister);
        btnRegister.setOnClickListener(view -> {
            //CREAR USUARIO EN FIREBASE
            String email = inputEmailText.getText().toString();
            String password = inputPassText.getText().toString();
            String confirmedPassword = inputConfirmedPassText.getText().toString();
            String userName = inputUserName.getText().toString();

            if (email.isEmpty()) {
                inputEmailText.setError(getString(R.string.empty_field));
            } else if (!email.contains("@") || !email.contains(".") || email.contains(" ")) {
                inputEmailText.setError(getString(R.string.invalid_email));
            } else if (password.isEmpty()) {
                inputPassText.setError(getString(R.string.empty_field));
            } else if (password.length() < 6) {
                inputPassText.setError(getString(R.string.invalid_password));
            } else if (!confirmedPassword.equals(password) || confirmedPassword.isEmpty()) {
                inputConfirmedPassText.setError(getString(R.string.not_match));
            } else {

                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            String userId = user.getUid();
                            DocumentReference documentReference = db.collection("Users").document(userId);
                            Map<String,Object> dataUser = new HashMap<>();
                            dataUser.put("user_name",userName);
                            dataUser.put("email_user",email);
                            documentReference.set(dataUser).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d("register", "Datos de usuario creados");
                                    toastOk(getString(R.string.created_user));
                                    Intent intent = new Intent(Register.this, MainActivity.class);
                                    startActivity(intent);
                                    Register.this.finish();

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d("register", "Error al crear documento");
                                }
                            });



                            } else {
                                // If sign in fails, display a message to the user.

                                toastWarning(getString(R.string.already_user_registered));

                            }
                        });
            }
        });
    }

    public void toastOk(String msg) {
        LayoutInflater layoutInflater = getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.toast_ok, findViewById(R.id.custom_ok));
        TextView txtMensaje = view.findViewById(R.id.text_ok);
        txtMensaje.setText(msg);

        Toast toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.CENTER_VERTICAL | Gravity.BOTTOM, 0, 200);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(view);
        toast.show();
    }

    public void toastWarning(String msg) {
        LayoutInflater layoutInflater = getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.toast_warning, findViewById(R.id.custom_warning));
        TextView txtMensaje = view.findViewById(R.id.text_warning);
        txtMensaje.setText(msg);

        Toast toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.CENTER_VERTICAL | Gravity.BOTTOM, 0, 200);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(view);
        toast.show();
    }

}