package com.example.reminder;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.OAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GithubActivity extends AppCompatActivity {

    EditText inputEmail;
    Button btnLogin;
    FirebaseAuth mAuth;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_github);

        inputEmail = findViewById(R.id.boxEmail); //email text field.
        btnLogin = findViewById(R.id.botonLogin); //login button.
        mAuth = FirebaseAuth.getInstance(); //get instance of FirebaseAuth.

        btnLogin.setOnClickListener(v -> {
            String email = inputEmail.getText().toString();
            if(!email.matches(emailPattern)){
                Toast.makeText(GithubActivity.this, "Enter a proper Email", Toast.LENGTH_SHORT).show();
            }else{
                //Login with valid email.
                OAuthProvider.Builder provider = OAuthProvider.newBuilder("github.com");
                //Target specific email with login hint.
                provider.addCustomParameter("login", email);

                //Request read access to a user's email addresses.
                //This must be preconfigured in the app's API permissions.
                List<String> scopes =
                        new ArrayList<String>() {
                            {
                                add("user:email");
                            }
                        };
                provider.setScopes(scopes);

                //Start the sign-in.
                Task<AuthResult> pendingResultTask = mAuth.getPendingAuthResult();
                if (pendingResultTask != null) {
                    //There's something already here! Finish the sign-in for your user.
                    pendingResultTask
                            .addOnSuccessListener(
                                    authResult -> {

                                    })
                            .addOnFailureListener(
                                    e -> Toast.makeText(GithubActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show());
                } else {
                    mAuth
                            .startActivityForSignInWithProvider(GithubActivity.this, provider.build())
                            .addOnSuccessListener(
                                    authResult -> {
                                        // Sign in success, update UI with the signed-in user's information.
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        assert user != null;
                                        String userId = user.getUid();
                                        String name, email1;
                                        FirebaseFirestore db = FirebaseFirestore.getInstance();

                                        name = user.getDisplayName();
                                        email1 = user.getProviderData().get(1).getEmail();
                                        DocumentReference documentReference = db.collection("Users").document(userId);
                                        Map<String, Object> dataUser = new HashMap<>();
                                        dataUser.put("email_user", email1);
                                        dataUser.put("user_name", name);
                                        documentReference.set(dataUser).addOnSuccessListener(aVoid -> Log.d("register", "Datos de usuario creados")).addOnFailureListener(e -> Log.d("register", "Error al crear documento"));
                                        openNextActivity();
                                    })
                            .addOnFailureListener(
                                    e -> Toast.makeText(GithubActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show());
                }
            }
        });
    }
    //Method to open the next activity.
    private void openNextActivity() {
        Intent intent = new Intent(GithubActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}