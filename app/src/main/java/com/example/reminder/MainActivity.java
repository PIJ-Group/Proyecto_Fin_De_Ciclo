package com.example.reminder;

import android.annotation.SuppressLint;
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

import com.example.reminder.AddEvents.AddEvent;
import com.example.reminder.ListEvents.ListEvents;
import com.example.reminder.UpdateEvents.UpdateEvents;
import com.example.reminder.model.Event;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    FirebaseUser user;
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    String emailUser;
    DataModal dataModal;
    Event event;

    ListView listViewEvents;
    List<String> listEventsTitle = new ArrayList<>();
    List<String> listEventsId = new ArrayList<>();
    ArrayList<DataModal> listEventsComplete = new ArrayList<>();

    CalendarView calendarView;
    Calendar calendar;
    String calendarDate;

    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        listViewEvents = findViewById(R.id.listView);
        calendarView = findViewById(R.id.calendar);
        calendar = Calendar.getInstance();

        //We initialize the gso variable that will get the necessary elements to the login user.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        //Method called to obtain the current date.
        actualDate();

        //1st Method called to update the current user's tasks.
        updateEvents();

        //Get the calendar date.
        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {

            calendar.set(Calendar.DAY_OF_MONTH,dayOfMonth);
            calendar.set(Calendar.MONTH,month);
            calendar.set(Calendar.YEAR,year);

            @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
            calendarDate = format.format(calendar.getTime());

            //Method called to update the current user's tasks.
            updateEvents();
        });

    }

    //Get current date.
    private void actualDate() {

        calendar.get(Calendar.DAY_OF_MONTH);
        calendar.get(Calendar.MONTH + 1);
        calendar.get(Calendar.YEAR);

        @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        calendarDate = format.format(calendar.getTime());

    }

    //Insert items in the listView.
    private void updateEvents() {
        user = mAuth.getCurrentUser();

        if (user != null) {
            emailUser = user.getEmail();

            //Verifies if the user is logged in with Google.
            if (user.getProviderData().size() > 1) {
                String provider = user.getProviderData().get(1).getEmail();
                getItemData(provider);
            } else { //If the user is not logged in with Google, uses the userMail variable.
                getItemData(emailUser);
            }
        }
    }

    //Obtain data of the database to show in the Item of the listView.
    public void getItemData(String userRegisterType) {
        //Object used to sort the events array.
        Comparator<DataModal> hoursComparator = Comparator.comparing(DataModal::getEventHour);

        db.collection("Events")
                .whereEqualTo("eventDate", calendarDate)
                .whereEqualTo("userMail", userRegisterType)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        return;
                    }

                    listEventsId.clear();
                    listEventsComplete.clear();
                    listEventsTitle.clear();

                    assert value != null;
                    for (QueryDocumentSnapshot doc : value) {
                        listEventsId.add(doc.getId());
                        dataModal = new DataModal(doc.getString("title"), doc.getString("eventHour"));
                        listEventsTitle.add(doc.getString("title"));
                        listEventsComplete.add(dataModal);
                        //Sorting the events array by hours.
                        listEventsComplete.sort(hoursComparator);
                    }

                    if (listEventsComplete.size() == 0) {
                        listViewEvents.setAdapter(null);

                    } else {
                        AdapterListView adapter = new AdapterListView(MainActivity.this, listEventsComplete);
                        listViewEvents.setAdapter(adapter);
                    }

                });
    }

    //Creating the top menú of activity main.
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    //Functionality of the menu buttons.
    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            //Go to the add event activity.
            case R.id.Add:
                Intent intent = new Intent(MainActivity.this, AddEvent.class);
                intent.putExtra("calendarDate", calendarDate);
                startActivity(intent);
                return true;

            //Disconnect the user and go to the login activity.
            case R.id.Logout:
                mAuth.signOut();
                //Log out of Google.
                onBackPressed();
                Intent intentMain = new Intent(MainActivity.this, Login.class);
                finish();
                startActivity(intentMain);
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }

    //AlertDialog of item ellipsis button with functionality.
    public void otherOptions(View view) {

        AlertDialog dialog = new AlertDialog.Builder(this)

                .setPositiveButton(getString(R.string.dialog_item_details), (dialog1, i) -> {
                    //Go to the event details activity.
                    getAndSendObject(view, ListEvents.class);
                })
                .setNeutralButton(getString(R.string.dialog_item_delete), (dialog12, i) -> {
                    //Delete event.
                    int position = listPosition(view);
                    db.collection("Events").document(listEventsId.get(position)).delete();

                    toastOk(getString(R.string.event_deleted));
                })
                .setNegativeButton(getString(R.string.dialog_item_edit), (dialog13, i) -> {
                    //Go to edit event activity.
                    getAndSendObject(view, UpdateEvents.class);
                })
                .create();
        dialog.show();
    }

    //Method to get the object of the database and send it to other activity.
    @SuppressWarnings("rawtypes")
    public void getAndSendObject(View view, Class activity) {
        int position = listPosition(view);
        event = new Event();

        DocumentReference docRef = db.collection("Events").document(listEventsId.get(position));
        docRef.get().addOnSuccessListener(documentSnapshot -> {
            event = documentSnapshot.toObject(Event.class);

            Intent intent = new Intent(MainActivity.this, activity);
            intent.putExtra("currentDate", event.getCurrentDate());
            intent.putExtra("description", event.getDescription());
            intent.putExtra("eventDate", event.getEventDate());
            intent.putExtra("eventHour", event.getEventHour());
            intent.putExtra("eventId", event.getEventId());
            intent.putExtra("status", event.getStatus());
            intent.putExtra("title", event.getTitle());
            intent.putExtra("userId", event.getUserId());
            intent.putExtra("userMail", event.getUserMail());
            startActivity(intent);
        });
    }

    //Obtain the position of the event in the database.
    public int listPosition(View view) {
        View parentDelete = (View) view.getParent();
        TextView eventTitleItem = parentDelete.findViewById(R.id.item_title);
        String eventContent = eventTitleItem.getText().toString();
        return listEventsTitle.indexOf(eventContent);
    }

    //Session closing of Google through the signOut method and go back to login activity.
    @Override
    public void onBackPressed() {
        mAuth.signOut();
        FirebaseAuth.getInstance().signOut();


        mGoogleSignInClient.signOut().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Intent loginActivity = new Intent(getApplicationContext(), Login.class);
                startActivity(loginActivity);
                MainActivity.this.finish();

            } else {
                Toast.makeText(getApplicationContext(), "No se pudo cerrar sesion con Google"
                        , Toast.LENGTH_LONG).show();
            }
        });
    }

    //Confirmation customized toast.
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

}