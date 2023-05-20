package com.example.reminder;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.OAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class TwitterActivity extends Login {

    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        firebaseAuth = FirebaseAuth.getInstance();//Get a reference to the FirebaseAuth instance.

        OAuthProvider.Builder provider = OAuthProvider.newBuilder("twitter.com");// Set the provider name.

        provider.addCustomParameter("lang", "en");//Language english.


        Task<AuthResult> pendingResultTask = firebaseAuth.getPendingAuthResult();
        if (pendingResultTask != null) {
            //There's something already here! Finish the sign-in for your user.
            pendingResultTask
                    .addOnSuccessListener(
                            authResult -> {
                                openNextActivity();
                                Toast.makeText(TwitterActivity.this, "", Toast.LENGTH_LONG).show();
                            })
                    .addOnFailureListener(
                            e -> Toast.makeText(TwitterActivity.this, "" + e.getMessage(), Toast.LENGTH_LONG).show());
        } else {
            //Start the sign-in flow.
            firebaseAuth
                    .startActivityForSignInWithProvider(TwitterActivity.this, provider.build())
                    .addOnSuccessListener(
                            authResult -> {
                                FirebaseUser user = firebaseAuth.getCurrentUser();
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

                                documentReference.set(dataUser).addOnSuccessListener(aVoid -> Log.d("registered", "User data created")).addOnFailureListener(e -> Log.d("register", "Error al crear documento"));
                                openNextActivity();
                            })
                    .addOnFailureListener(
                            e -> Toast.makeText(TwitterActivity.this, "" + e.getMessage(), Toast.LENGTH_LONG).show());
        }

    }
    //Method to open the next activity.
    private void openNextActivity() {
        Intent intent = new Intent(TwitterActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

}