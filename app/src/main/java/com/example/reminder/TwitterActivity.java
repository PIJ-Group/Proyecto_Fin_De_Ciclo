package com.example.reminder;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

        firebaseAuth = FirebaseAuth.getInstance();

        OAuthProvider.Builder provider = OAuthProvider.newBuilder("twitter.com");

        provider.addCustomParameter("lang", "es");


        Task<AuthResult> pendingResultTask = firebaseAuth.getPendingAuthResult();
        if (pendingResultTask != null) {
            // There's something already here! Finish the sign-in for your user.
            pendingResultTask
                    .addOnSuccessListener(
                            new OnSuccessListener<AuthResult>() {
                                @Override
                                public void onSuccess(AuthResult authResult) {
                                    startActivity(new Intent(TwitterActivity.this,MainActivity.class));
                                    Toast.makeText(TwitterActivity.this, "", Toast.LENGTH_LONG).show();
                                }
                            })
                    .addOnFailureListener(
                            new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(TwitterActivity.this, ""+e.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            });
        } else {
            firebaseAuth
                    .startActivityForSignInWithProvider(TwitterActivity.this, provider.build())
                    .addOnSuccessListener(
                            new OnSuccessListener<AuthResult>() {
                                @Override
                                public void onSuccess(AuthResult authResult) {
                                    FirebaseUser user = firebaseAuth.getCurrentUser();
                                    assert user != null;
                                    String userId = user.getUid();
                                    String name, email, identifier;
                                    FirebaseFirestore db = FirebaseFirestore.getInstance();

                                    name = user.getDisplayName();
                                    email = user.getProviderData().get(1).getEmail();


                                    if(email == null){
                                        email = user.getProviderData().get(1).getUid();
                                    }



                                    DocumentReference documentReference = db.collection("Users").document(userId);
                                    Map<String, Object> dataUser = new HashMap<>();

                                    dataUser.put("email_user", email);
                                    dataUser.put("user_name", name);

                                    documentReference.set(dataUser).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d("register", "Datos de usuario creados");
                                            toastWarning(getString(R.string.created_user));

                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.d("register", "Error al crear documento");
                                        }
                                    });
                                    startActivity(new Intent(TwitterActivity.this,MainActivity.class));
                                    Toast.makeText(TwitterActivity.this, "", Toast.LENGTH_LONG).show();
                                }
                            })
                    .addOnFailureListener(
                            new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(TwitterActivity.this, ""+e.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            });
        }

    }

}