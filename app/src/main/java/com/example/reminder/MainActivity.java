package com.example.reminder;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.reminder.ListNotes.ListNotes;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    FirebaseUser user;
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    String emailUser;
    DataModal dataModal;
    Note note;

    ListView listViewNotes;
    List<String> listNotesTitle = new ArrayList<>();
    List<String> listNotesId = new ArrayList<>();
    ArrayList<DataModal> listNotesComplete = new ArrayList<>();

    CalendarView calendarView;
    String calendarDate;


    private GoogleSignInOptions gso;
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        listViewNotes = findViewById(R.id.listView);
        calendarView = findViewById(R.id.calendar);

        /*Inicializamos la variable gso que recogerá los elementos necesarios para que el usuario
          inicie sesion*/
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        //Llamada al método para obtener la fecha actual.
        actualDate();

        //Recoger fecha del calendario
        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {

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

            //Actualizar la UI con las tareas del usuario logueado
            updateNotes();
        });

    }

    //Recoger fecha acutal
    private String actualDate(){
        int actualDay, actualMonth, actualYear;
        String actualDayFormatted, actualMonthFormatted;

        Calendar calendario = Calendar.getInstance();

        //Obtener día completo
        actualDay = calendario.get(Calendar.DAY_OF_MONTH);
        if (actualDay < 10) {
            actualDayFormatted = "0" + actualDay;
        } else {
            actualDayFormatted = String.valueOf(actualDay);
        }

        //Obtener mes completo
        actualMonth = calendario.get(Calendar.MONTH) + 1;
        if (actualMonth < 10) {
            actualMonthFormatted = "0" + actualMonth;
        } else {
            actualMonthFormatted = String.valueOf(actualMonth);
        }

        //Obtener año
        actualYear = calendario.get(Calendar.YEAR);

        calendarDate = actualDayFormatted + "/" + actualMonthFormatted + "/" + actualYear;

        return calendarDate;
    }

    //Insertar item en listView
    private void updateNotes() {
        user = mAuth.getCurrentUser();
        //Objeto utilizado para poder ordenar el array de eventos
        Comparator<DataModal> hoursComparator = Comparator.comparing(DataModal::getNoteHour);

        if (user != null) {
            emailUser = user.getEmail();

            // Verifica si el usuario ha iniciado sesión con Google
            if (user.getProviderData().size() > 1) {
                String provider = user.getProviderData().get(1).getEmail();
                db.collection("Notes")
                        .whereEqualTo("noteDate", calendarDate)
                        .whereEqualTo("userMail", provider)
                        .addSnapshotListener((value, error) -> {
                            if (error != null) {
                                return;
                            }

                            listNotesId.clear();
                            listNotesComplete.clear();
                            listNotesTitle.clear();

                            for (QueryDocumentSnapshot doc : value) {
                                listNotesId.add(doc.getId());
                                dataModal = new DataModal(doc.getString("title"), doc.getString("noteHour"));
                                listNotesTitle.add(doc.getString("title"));
                                listNotesComplete.add(dataModal);
                                //Ordenación del array de eventos por horas
                                Collections.sort(listNotesComplete, hoursComparator);
                            }

                            if (listNotesComplete.size() == 0) {
                                listViewNotes.setAdapter(null);
                                toastWarning("No hay eventos");

                            } else {
                                AdapterListView adapter = new AdapterListView(MainActivity.this, listNotesComplete);
                                listViewNotes.setAdapter(adapter);
                            }

                        });

            } else { // Si el usuario no ha iniciado sesión con Google, utiliza el userMail
                db.collection("Notes")
                        .whereEqualTo("noteDate", calendarDate)
                        .whereEqualTo("userMail", emailUser)
                        .addSnapshotListener((value, error) -> {
                            if (error != null) {
                                return;
                            }

                            listNotesId.clear();
                            listNotesComplete.clear();
                            listNotesTitle.clear();

                            for (QueryDocumentSnapshot doc : value) {
                                listNotesId.add(doc.getId());
                                dataModal = new DataModal(doc.getString("title"), doc.getString("noteHour"));
                                listNotesTitle.add(doc.getString("title"));
                                listNotesComplete.add(dataModal);
                            }

                            if (listNotesComplete.size() == 0) {
                                listViewNotes.setAdapter(null);
                                toastWarning("No hay eventos");

                            } else {
                                AdapterListView adapter = new AdapterListView(MainActivity.this, listNotesComplete);
                                listViewNotes.setAdapter(adapter);
                            }

                        });
            }
        }
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

                .setPositiveButton(R.string.dialog_item_details, (dialog1, i) -> {
                    //PASAR A ACTIVITY DE DETALLES
                    View parentDelete = (View) view.getParent();
                    TextView noteTitleItem = parentDelete.findViewById(R.id.item_title);
                    String noteContent = noteTitleItem.getText().toString();
                    int position = listNotesTitle.indexOf(noteContent);
                    note = new Note();

                    DocumentReference docRef = db.collection("Notes").document(listNotesId.get(position));
                    docRef.get().addOnSuccessListener(documentSnapshot -> {
                        note = documentSnapshot.toObject(Note.class);

                        Intent intent = new Intent(MainActivity.this, ListNotes.class);
                        intent.putExtra("currentDate", note.getCurrentDate());
                        intent.putExtra("description", note.getDescription());
                        intent.putExtra("noteDate", note.getNoteDate());
                        intent.putExtra("noteHour", note.getNoteHour());
                        intent.putExtra("noteId", note.getNoteId());
                        intent.putExtra("status", note.getStatus());
                        intent.putExtra("title", note.getTitle());
                        intent.putExtra("userId", note.getUserId());
                        intent.putExtra("userMail", note.getUserMail());
                        startActivity(intent);
                    });
                })
                .setNeutralButton(R.string.dialog_item_edit, (dialog12, i) -> {
                    //PASAR A ACTIVITY DE EDITAR
                    View parentDelete = (View) view.getParent();
                    TextView noteTitleItem = parentDelete.findViewById(R.id.item_title);
                    String noteContent = noteTitleItem.getText().toString();
                    int position = listNotesTitle.indexOf(noteContent);
                    note = new Note();

                    DocumentReference docRef = db.collection("Notes").document(listNotesId.get(position));
                    docRef.get().addOnSuccessListener(documentSnapshot -> {
                        note = documentSnapshot.toObject(Note.class);

                        Intent intent = new Intent(MainActivity.this, UpdateNote.class);
                        intent.putExtra("currentDate", note.getCurrentDate());
                        intent.putExtra("description", note.getDescription());
                        intent.putExtra("noteDate", note.getNoteDate());
                        intent.putExtra("noteHour", note.getNoteHour());
                        intent.putExtra("noteId", note.getNoteId());
                        intent.putExtra("status", note.getStatus());
                        intent.putExtra("title", note.getTitle());
                        intent.putExtra("userId", note.getUserId());
                        intent.putExtra("userMail", note.getUserMail());
                        startActivity(intent);
                    });
                })
                .setNegativeButton(R.string.dialog_item_delete, (dialog13, i) -> {
                    //ELIMIAR TAREA (REVISAR)
                    View parentDelete = (View) view.getParent();
                    TextView noteTitleItem = parentDelete.findViewById(R.id.item_title);
                    String noteContent = noteTitleItem.getText().toString();
                    int position = listNotesTitle.indexOf(noteContent);

                    db.collection("Notes").document(listNotesId.get(position)).delete();

                    toastOk(getString(R.string.event_deleted));
                })
                .create();
        dialog.show();
    }

    /*SE INTENTA REFACTORIZAR LA RECOGIDA Y ENVIO DEL OBJETO, PROBAR DE NUEVO
    //Método para recoger el objeto y enviarlo a otra activity.
    public void getAndSendObject(View view, Activity activity){
        View parentDelete = (View) view.getParent();
        TextView noteTitleItem = parentDelete.findViewById(R.id.item_title);
        String noteContent = noteTitleItem.getText().toString();
        int position = listNotesTitle.indexOf(noteContent);
        note = new Note();

        DocumentReference docRef = db.collection("Notes").document(listNotesId.get(position));
        docRef.get().addOnSuccessListener(documentSnapshot -> {
            note = documentSnapshot.toObject(Note.class);

            Intent intent = new Intent(MainActivity.this, ListNotes.class);
            intent.putExtra("currentDate", note.getCurrentDate());
            intent.putExtra("description", note.getDescription());
            intent.putExtra("noteDate", note.getNoteDate());
            intent.putExtra("noteHour", note.getNoteHour());
            intent.putExtra("noteId", note.getNoteId());
            intent.putExtra("status", note.getStatus());
            intent.putExtra("title", note.getTitle());
            intent.putExtra("userId", note.getUserId());
            intent.putExtra("userMail", note.getUserMail());
            startActivity(intent);
        });
    }
    */

    //Cierre de sesión de google a través del método signOut y transición al login
    @Override
    public void onBackPressed() {
        mAuth.signOut();
        FirebaseAuth.getInstance().signOut();


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

    //Método para incluir un toast personalizado de advertencia.
    public void toastWarning(String msg) {
        LayoutInflater layoutInflater = getLayoutInflater();
        android.view.View view = layoutInflater.inflate(R.layout.toast_warning, findViewById(R.id.custom_warning));
        TextView txtMensaje = view.findViewById(R.id.text_warning);
        txtMensaje.setText(msg);

        Toast toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.CENTER_VERTICAL | Gravity.BOTTOM, 0, 200);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(view);
        toast.show();
    }
}