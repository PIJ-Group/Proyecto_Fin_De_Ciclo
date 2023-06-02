package com.example.reminder;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {
    Button btnRegister;
    EditText inputUserName, inputEmailText, inputPassText, inputConfirmedPassText;
    TextView haveAndAccount;
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    ProgressBar loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Register");
        }

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        inputEmailText = findViewById(R.id.emailBox);//Text email
        inputPassText = findViewById(R.id.passBox);//Text password
        inputConfirmedPassText = findViewById(R.id.confirmedPassBox);//Text confirm password
        inputUserName = findViewById(R.id.completeName);//Text name
        haveAndAccount = findViewById(R.id.haveAccount);//Text have account
        loading = findViewById(R.id.loading);//Animation loading

        btnRegister = findViewById(R.id.userRegister);//btn Register
        btnRegister.setOnClickListener(view -> {
            //Collection of the texts entered
            String email = inputEmailText.getText().toString();
            String password = inputPassText.getText().toString();
            String confirmedPassword = inputConfirmedPassText.getText().toString();
            String userName = inputUserName.getText().toString();

            //Validations
            if (userName.isEmpty()) {
                inputUserName.setError(getString(R.string.empty_field));
            } else if (email.isEmpty()) {
                inputEmailText.setError(getString(R.string.empty_field));
            } else if (!email.contains("@") || !email.contains(".") || email.contains(" ")) {
                inputEmailText.setError(getString(R.string.invalid_email));
            } else if (password.isEmpty()) {
                inputPassText.setError(getString(R.string.empty_field));
            } else if (password.length() < 6) {
                inputPassText.setError(getString(R.string.invalid_password));
            } else if (confirmedPassword.isEmpty()) {
                inputConfirmedPassText.setError(getString(R.string.not_match));
            } else if (!confirmedPassword.equals(password)) {
                inputConfirmedPassText.setError(getString(R.string.not_match));
            } else {
                //Create user in firebase
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                loading.setVisibility(View.VISIBLE);
                                FirebaseUser user = mAuth.getCurrentUser();
                                assert user != null;
                                String userId = user.getUid();
                                DocumentReference documentReference = db.collection("Users").document(userId);
                                Map<String, Object> dataUser = new HashMap<>();
                                dataUser.put("user_name", userName);
                                dataUser.put("email_user", email);
                                documentReference.set(dataUser).addOnSuccessListener(aVoid -> {
                                    Log.d("register", "User data created");
                                    toastOk(getString(R.string.created_user));
                                    Intent intent = new Intent(Register.this, MainActivity.class);
                                    startActivity(intent);
                                    Register.this.finish();

                                }).addOnFailureListener(e -> Log.d("register", "Error creating document"));

                            } else {
                                // If sign in fails, display a message to the user.
                                toastWarning(getString(R.string.already_user_registered));
                            }
                        });
            }
        });
        //return to login
        haveAndAccount.setOnClickListener(view -> {
            startActivity(new Intent(Register.this, Login.class));
            Register.this.finish();

        });
    }

    //Notification toast
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
        TextView txtMessage = view.findViewById(R.id.text_warning);
        txtMessage.setText(msg);

        Toast toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.CENTER_VERTICAL | Gravity.BOTTOM, 0, 200);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(view);
        toast.show();
    }

}