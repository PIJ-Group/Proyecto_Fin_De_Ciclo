package com.example.reminder;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class Register extends AppCompatActivity {
    Button botonRegistro;
    EditText emailText, passText, confirmedPassText;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        emailText = findViewById(R.id.emailBox); //Texto del correo
        passText = findViewById(R.id.passBox);
        confirmedPassText = findViewById(R.id.confirmedPassBox);

        botonRegistro = findViewById(R.id.userRegister);
        botonRegistro.setOnClickListener(view -> {
            //CREAR USUARIO EN FIREBASE
            String email = emailText.getText().toString();
            String password = passText.getText().toString();
            String confirmedPassword = confirmedPassText.getText().toString();

            if (email.isEmpty()) {
                emailText.setError(getString(R.string.empty_field));
            } else if (!email.contains("@") || !email.contains(".") || email.contains(" ")) {
                emailText.setError(getString(R.string.invalid_email));
            } else if (password.isEmpty()) {
                passText.setError(getString(R.string.empty_field));
            } else if (password.length() < 6) {
                passText.setError(getString(R.string.invalid_password));
            } else if (!confirmedPassword.equals(password)) {
                confirmedPassText.setError(getString(R.string.not_match));
            } else {

                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {

                                toastOk(getString(R.string.created_user));

                                Intent intent = new Intent(Register.this, MainActivity.class);
                                startActivity(intent);

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