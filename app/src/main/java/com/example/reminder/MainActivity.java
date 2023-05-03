package com.example.reminder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CalendarView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.example.reminder.ArchivedNotes.ArchivedNotes;
import com.example.reminder.ListNotes.ListNotes;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.core.View;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    FirebaseUser user;
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    String emailUser, noteTitle, noteHour;
    TextView itemTitle, itemHour;
    View item;

    ListView listViewNotes;
    List<String> listNotesTitle = new ArrayList<>();
    List<String> listNotesHour = new ArrayList<>();
    List<String> listNotesId = new ArrayList<>();
    ArrayAdapter<String> AdapterNotesTitle, AdapterNotesHour;

    CalendarView calendarView;
    String calendarDate;

    private GoogleSignInOptions gso;
    private GoogleSignInClient mGoogleSignInClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        user = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        listViewNotes = findViewById(R.id.listView);

        calendarView = (CalendarView) findViewById(R.id.calendar);

        //Recoger fecha del calendario
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {

                String dayFormatted, monthFormatted;

                //Get formatted day
                if (dayOfMonth < 10) {
                    dayFormatted = "0" + dayOfMonth;
                } else {
                    dayFormatted = String.valueOf(dayOfMonth);
                }

                //Get formatted month
                int Month = month + 1;

                if (Month < 10) {
                    monthFormatted = "0" + Month;
                } else {
                    monthFormatted = String.valueOf(Month);
                }

                calendarDate = dayFormatted + "/" + monthFormatted + "/" + year;
            }
        });

        /*Inicializamos la variable gso que recogerá los elementos necesarios para que el usuario
          inicie sesion*/
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);


        //Actualizar la UI con las tareas del usuario logueado
        updateNotes();
    }

    //Insertar item en listview
    //FALTA POR IMPLEMENTAR COMPLETAMENTE
    private void updateNotes() {
        if (user != null) {
            emailUser = user.getEmail();
        }
        db.collection("Users")
                .whereEqualTo("noteDate", calendarDate)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            return;
                        }

                        listNotesTitle.clear();
                        listNotesHour.clear();
                        listNotesId.clear();

                        for (QueryDocumentSnapshot doc : value) {
                            listNotesId.add(doc.getId());
                            listNotesTitle.add(doc.getString("title"));
                            listNotesHour.add(doc.getString("noteHour"));
                        }

                        if(listNotesTitle.size() == 0){
                            listViewNotes.setAdapter(null);
                        }else{
                            AdapterNotesTitle = new ArrayAdapter<String>(MainActivity.this, R.layout.item_note, R.id.item_title, listNotesTitle);
                            AdapterNotesHour = new ArrayAdapter<String>(MainActivity.this, R.layout.item_note, R.id.item_hour, listNotesHour);
                            listViewNotes.setAdapter(AdapterNotesTitle);
                            listViewNotes.setAdapter(AdapterNotesHour);

                        }

                    }
                });
    }

    //Creación del menú superior del activity main
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    //Funcionalidad de los botones del menú
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            //Pasamos a la activity de añadir nota
            case R.id.Add:
                Intent intent = new Intent(MainActivity.this, AddNote.class);
                intent.putExtra("calendarDate", calendarDate);
                startActivity(intent);
                return true;

            //Desconectamos el usuario y pasamos a la activity de login
            case R.id.Logout:
                mAuth.signOut();
                //Cierre de sesión de google
                onBackPressed();
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }

    //Dialog del botón elipsis del item con funcionalidad
    public void otherOptions(View view) {
        AlertDialog dialog = new AlertDialog.Builder(this)

            .setPositiveButton(R.string.dialog_item_details, new DialogInterface.OnClickListener() {
            @Override
                public void onClick(DialogInterface dialog, int i) {
                    //PASAR A ACTIVITY DE DETALLES (REVISAR)
                    Intent intent = new Intent(MainActivity.this, ArchivedNotes.class);
                    startActivity(intent);
                }
            })
            .setNeutralButton(R.string.dialog_item_edit, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int i) {
                    //PASAR A ACTIVITY DE EDITAR (REVISAR)
                    Intent intent = new Intent(MainActivity.this, ListNotes.class);
                    startActivity(intent);
                }
            })
            .setNegativeButton(R.string.dialog_item_delete, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int i) {
                    //ELIMIAR TAREA (REVISAR)
                    TextView noteTitleItem = findViewById(R.id.item_title);
                    String taskContent = noteTitleItem.getText().toString();
                    int position = listNotesTitle.indexOf(taskContent);

                    db.collection("Notes").document(listNotesId.get(position)).delete();

                    toastOk(getString(R.string.event_deleted));
                }
            })
            .create();
        dialog.show();
    }

    //Método para incluir un toast personalizado de confirmación.
    public void toastOk(String msg) {
        LayoutInflater layoutInflater = getLayoutInflater();
        android.view.View view = layoutInflater.inflate(R.layout.toast_ok, (ViewGroup) findViewById(R.id.custom_ok));
        TextView txtMensaje = view.findViewById(R.id.text_ok);
        txtMensaje.setText(msg);

        Toast toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.CENTER_VERTICAL | Gravity.BOTTOM, 0, 200);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(view);
        toast.show();
    }

    //Cierre de sesión de google a través del método signOut y transición al login
    @Override
    public void onBackPressed() {
        mAuth.signOut();

        mGoogleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Intent loginActivity = new Intent(getApplicationContext(), Login.class);
                    startActivity(loginActivity);
                    MainActivity.this.finish();

                } else {
                    Toast.makeText(getApplicationContext(), "No se pudo cerrar sesion con Google"
                            , Toast.LENGTH_LONG).show();
                }
            }
        });

    }
}