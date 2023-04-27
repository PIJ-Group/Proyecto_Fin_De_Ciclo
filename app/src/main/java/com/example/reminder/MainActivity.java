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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.core.View;

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

    CalendarView calendarView;
    String calendarDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        listViewNotes = findViewById(R.id.listView);

        calendarView = (CalendarView) findViewById(R.id.calendar);

        //Recoger fecha del calendario
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                calendarDate = dayOfMonth + "/" + (month + 1) + "/" + year;
            }
        });

        //Actualizar la UI con las tareas del usuario logueado
        updateNotes();
    }

    //Insertar item en listview
    private void updateNotes() {
        db.collection("Tasks")
                .whereEqualTo("Email", emailUsuario)
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
                            listNotes.add(doc.getString("taskName"));
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
            //Pasamos a la activity de añadir nota
            case R.id.Add:
                Intent intent = new Intent(MainActivity.this, AddNote.class);
                intent.putExtra("calendarDate", calendarDate);
                startActivity(intent);
                return true;

            //Desconectamos el usuario y pasamos a la activity de login
            case R.id.Logout:
                mAuth.signOut();
                onBackPressed();
                finish();
                return true;

            default: return super.onOptionsItemSelected(item);

        }
    }

    //Dialog del botón elipsis del item con funcionalidad
    public void otherOptions(View view) {
        AlertDialog dialog = new AlertDialog.Builder(this)
            .setPositiveButton(R.string.dialog_item_details, new DialogInterface.OnClickListener() {
            @Override
                public void onClick(DialogInterface dialog, int i) {
                    //PASAR A ACTIVITY DE DETALLES
                }
            })
            .setNeutralButton(R.string.dialog_item_edit, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int i) {
                    //PASAR A ACTIVITY DE EDITAR
                }
            })
            .setNegativeButton(R.string.dialog_item_delete, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int i) {
                    //ELIMIAR TAREA
                    //deleteNote();
                }
            })
            .create();
        dialog.show();
    }
    
    //Método para poder borrar la tarea ya realizada
    /*
    public void deleteNote(View view) {
        View parent = (View) view.getParent();
        TextView taskTv = parent.findViewById(R.id.item_title);
        String taskContent = taskTv.getText().toString();
        int position = listNotes.indexOf(taskContent);

        db.collection("Tasks").document(listNotesId.get(position)).delete();

        toastOk(getString(R.string.event_deleted));
    }
    */
    //Método para incluir un toast personalizado de confirmación.
    public void toastOk(String msg){
        LayoutInflater layoutInflater = getLayoutInflater();
        android.view.View view = layoutInflater.inflate(R.layout.toast_ok, (ViewGroup) findViewById(R.id.custom_ok));
        TextView txtMensaje = view.findViewById(R.id.text_ok);
        txtMensaje.setText(msg);

        Toast toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.CENTER_VERTICAL | Gravity.BOTTOM, 0,200);
        toast.setDuration (Toast.LENGTH_LONG);
        toast.setView(view);
        toast.show();
    }
}