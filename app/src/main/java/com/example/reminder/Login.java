package com.example.reminder;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Login extends AppCompatActivity {

    static final int RC_SIGN_IN = 1;
    GoogleSignInClient mGoogleSignInClient;

    Button btnGoogle, btnTwitter, btnGithub, btnLogin;
    TextView btnRegister;
    EditText emailText, passText;
    private FirebaseAuth mAuth;

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("¿Do you want to exit?")
                .setPositiveButton(getString(R.string.dialog_login_yes),(dialog, i) -> {
                    Intent intent = new Intent(Intent.ACTION_MAIN);
                    intent.addCategory(Intent.CATEGORY_HOME);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                })
                .setNegativeButton(getString(R.string.dialog_login_no),(dialog12, i) -> dialog12.dismiss());
        builder.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //
        checkAndRedirectToMain();

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        emailText = findViewById(R.id.boxMail); //Text email
        passText = findViewById(R.id.boxPass); //Text password


        btnGithub = findViewById(R.id.githubButton); //Github login button
        btnTwitter = findViewById(R.id.twitterButton); //Twitter login button
        btnGoogle = findViewById(R.id.googleButton); //Google login button
        btnLogin = findViewById(R.id.btnLogin); //User login button
        btnLogin.setOnClickListener(view -> {
            //LOGIN EN FIREBASE
            String email = emailText.getText().toString();
            String password = passText.getText().toString();

            if (email.isEmpty()) {
                emailText.setError(getString(R.string.empty_field));
            } else if (!email.contains("@") || !email.contains(".") || email.contains(" ")) {
                emailText.setError(getString(R.string.invalid_email));
            } else if (password.isEmpty()) {
                passText.setError(getString(R.string.empty_field));
            } else {

                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, task -> {
                            if (task.isSuccessful()) {
                                //Persist the session
                                persistSession();
                                // Sign in success, update UI with the signed-in user's information
                                Intent intent = new Intent(Login.this, MainActivity.class);
                                startActivity(intent);
                            } else {
                                // If sign in fails, display a message to the user.
                                toastWarning(getString(R.string.failed_authentication));

                            }
                        });
            }

        });
        //User registration button
        btnRegister = findViewById(R.id.createAccount);
        btnRegister.setOnClickListener(view ->
                startActivity(new Intent(Login.this, Register.class)));


        //-------------------GOOGLE------------------------//
        //Google login settings
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        btnGoogle.setOnClickListener(view -> signIn());
        // [END config_signin]

        //-------------------Github------------------------//
        //GitHub login settings
        btnGithub.setOnClickListener(v -> {
            Intent intent = new Intent(Login.this, GithubActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
        });

        //-------------------Twitter------------------------//
        //GitHub Twitter settings
        btnTwitter.setOnClickListener(v -> {
            Intent intent = new Intent(Login.this, TwitterActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
        });
    }

    // Method to persist the session
    private void persistSession() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isAuthen", true);
        editor.apply();
    }

    //Method to verify and redirect to the user
    private void checkAndRedirectToMain() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        boolean isAuthen = sharedPreferences.getBoolean("isAuthen", false);

        if (isAuthen) {
            //Verify if the user is authenticated in Firebase Authentication
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

            if (currentUser != null) {
                //If the user is authenticated, redirect to the MainActivity
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }
    }

    //-------------------Google------------------------//
    //Method for logging in
    @SuppressWarnings("deprecation")
    public void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    //Method to verify the result of the log in activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
            }
        }
    }

    //Method to check the token and log in if the token is valid
    public void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        FirebaseUser user = mAuth.getCurrentUser();
                        assert user != null;
                        String userId = user.getUid();
                        String name, email;
                        FirebaseFirestore db = FirebaseFirestore.getInstance();

                        name = user.getDisplayName();
                        email = user.getProviderData().get(1).getEmail();
                        DocumentReference documentReference = db.collection("Users").document(userId);
                        Map<String, Object> dataUser = new HashMap<>();
                        dataUser.put("email_user", email);
                        dataUser.put("user_name", name);
                        documentReference.set(dataUser).addOnSuccessListener(aVoid ->
                                Log.d("register", "Created user data")).addOnFailureListener(e ->
                                Log.d("register", "Error creating document"));
                        updateUI();

                    } else {
                        toastWarning("Authentication failed");
                        updateUI();
                    }
                });
    }

    //Method to check the status of the current user and move between activities
    private void updateUI() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            Intent intent = new Intent(Login.this, MainActivity.class);
            startActivity(intent);
        }

    }
    // [END auth_with_google]

    //Notification toast
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