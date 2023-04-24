package com.example.reminder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    String emailUsuario;

    ListView listViewNotes;
    List<String> listNotes = new ArrayList<>();
    List<String> listNotesId = new ArrayList<>();
    ArrayAdapter<String> mAdapterNotes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        listViewNotes = findViewById(R.id.listView);

        //Actualizar la UI con las tareas del usuario logueado
        updateNotes();
    }

    //Insertar item en listview
    private void updateNotes() {
        //COMPROBRAR QUE LA COLECCIÓN ES LA CORRESPONDIENTE!!!
        db.collection("Tareas")
                .whereEqualTo("emailUsuario", emailUsuario)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            return;
                        }

                        listNotes.clear();
                        listNotesId.clear();
                        for (QueryDocumentSnapshot doc : value) {
                            listNotesId.add(doc.getId());
                            //COMPROBAR EL CAMPO!!!
                            listNotes.add(doc.getString("nombreTarea"));
                        }

                        if(listNotes.size() == 0){
                            listViewNotes.setAdapter(null);
                        }else{
                            mAdapterNotes = new ArrayAdapter<String>(MainActivity.this, R.layout.item_note, R.id.item_title, listNotes);
                            listViewNotes.setAdapter(mAdapterNotes);
                        }

                    }
                });
    }

    //Creación del menú superior del activity main
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return super.onCreateOptionsMenu(menu);
    }

    //Funcionalidad de los botones del menú
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.Add:
                //pasar a activity de add_notes
                return true;

            case R.id.Logout:
                mAuth.signOut();
                onBackPressed();
                finish();
                return true;

            default: return super.onOptionsItemSelected(item);

        }
    }
}