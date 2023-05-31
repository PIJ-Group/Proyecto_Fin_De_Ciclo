package com.example.reminder.UpdateEvents;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.reminder.MainActivity;
import com.example.reminder.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.Calendar;
import java.util.Map;

public class UpdateEvents extends AppCompatActivity implements AdapterView.OnItemSelectedListener {


    TextView Id_Event_Update, Userid_User_Update, Registration_Date_Update, Date_Update,
            Hour_Update, Status_Update, New_Status;
    EditText Title_Update, Description_Update;
    Button Calendar_btn_Update, Hour_btn_Update;
    ImageView Event_finished, Event_not_finished;
    Spinner Spinner_Status;

    //Declare strings to store the data of the main activity
    String id_event_R, user_id_R, registration_date_R, title_R, description_R,
            date_R, hour_R, status_R, user_mail_R;

    //Declare strings to get data for update the event in firebase
    String titleUpdate, descriptionUpdate, dateUpdate, hourUpdate, statusUpdate;

    //Declare String for eventStatus
    String event_Status;

    //Declare variables for Firebase actions
    FirebaseFirestore db;
    FirebaseAuth nAuth;

    //Declare variables for formatting  date and time
    int year, month, day, hour, minutes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_events);

        //Set Action Bar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Update Event");
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        VarInit();
        GetData();
        SetData();
        checkEventStatus();
        spinner_Status();
        Calendar_btn_Update.setOnClickListener(v -> dateSelect());
        Hour_btn_Update.setOnClickListener(v -> selectHour());
    }

    //Initialize Views
    private void VarInit() {
        Id_Event_Update = findViewById(R.id.Id_Event_Update);
        Userid_User_Update = findViewById(R.id.Userid_User_Update);
        Registration_Date_Update = findViewById(R.id.Registration_Date_Update);
        Date_Update = findViewById(R.id.Date_Update);
        Hour_Update = findViewById(R.id.Hour_Update);
        Status_Update = findViewById(R.id.Status_Update);
        Title_Update = findViewById(R.id.Title_Update);
        Description_Update = findViewById(R.id.Description_Update);
        Calendar_btn_Update = findViewById(R.id.Calendar_btn_Update);
        Hour_btn_Update = findViewById(R.id.Hour_btn_Update);
        Event_finished = findViewById(R.id.Event_finished);
        Event_not_finished = findViewById(R.id.Event_not_finished);
        Spinner_Status = findViewById(R.id.Spinner_Status);
        New_Status = findViewById(R.id.New_Status);

        db = FirebaseFirestore.getInstance();
        nAuth = FirebaseAuth.getInstance();

    }

    //Get data from intent in Main Activity
    private void GetData() {
        id_event_R = getIntent().getStringExtra("eventId");
        user_id_R = getIntent().getStringExtra("userId");
        registration_date_R = getIntent().getStringExtra("currentDate");
        title_R = getIntent().getStringExtra("title");
        description_R = getIntent().getStringExtra("description");
        date_R = getIntent().getStringExtra("eventDate");
        hour_R = getIntent().getStringExtra("eventHour");
        status_R = getIntent().getStringExtra("status");
        user_mail_R = getIntent().getStringExtra("userMail");
    }

    //Setting Data
    private void SetData() {
        Id_Event_Update.setText(id_event_R);
        Userid_User_Update.setText(user_id_R);
        Registration_Date_Update.setText(registration_date_R);
        Date_Update.setText(date_R);
        Hour_Update.setText(hour_R);
        Status_Update.setText(status_R);
        Title_Update.setText(title_R);
        Description_Update.setText(description_R);

    }

    //method to select a date
    private void dateSelect() {
        final Calendar calendar = Calendar.getInstance();
        day = calendar.get(Calendar.DAY_OF_MONTH);
        month = calendar.get(Calendar.MONTH);
        year = calendar.get(Calendar.YEAR);

        //Dialog with Calendar Picker
        @SuppressLint("SetTextI18n") DatePickerDialog datePickerDialog =
                new DatePickerDialog(UpdateEvents.this, (view, yearSelected,
                                                         monthSelected, daySelected) -> {

            String dayFormatted, monthFormatted;

            //Format day
            if (daySelected < 10) {
                dayFormatted = "0" + daySelected;
            } else {
                dayFormatted = String.valueOf(daySelected);
            }

            //Format month
            int Month = monthSelected + 1;

            if (Month < 10) {
                monthFormatted = "0" + Month;
            } else {
                monthFormatted = String.valueOf(Month);
            }

            // Set Date on TextView
            Date_Update.setText(dayFormatted + "/" + monthFormatted + "/" + yearSelected);
        }
                , year, month, day);

        datePickerDialog.show();
    }

    //method to select an hour
    private void selectHour() {
        final Calendar clock = Calendar.getInstance();
        hour = clock.get(Calendar.HOUR_OF_DAY);
        minutes = clock.get(Calendar.MINUTE);

        @SuppressLint("SetTextI18n") TimePickerDialog timePickerDialog =
                new TimePickerDialog(UpdateEvents.this, (view, hourOfDay, minute) -> {

            String hourFormatted, minutesFormatted;

            //Format hour
            if (hourOfDay < 10) {
                hourFormatted = "0" + hourOfDay;
            } else {
                hourFormatted = String.valueOf(hourOfDay);
            }

            //Format minutes
            if (minute < 10) {
                minutesFormatted = "0" + minute;
            } else {
                minutesFormatted = String.valueOf(minute);
            }

            // Set Time on TextView
            Hour_Update.setText(hourFormatted + ":" + minutesFormatted);
        }, hour, minutes, false);

        timePickerDialog.show();
    }

    //Update a Event in firebase
    private void updateEventFirebase() {

        titleUpdate = Title_Update.getText().toString();
        descriptionUpdate = Description_Update.getText().toString();
        dateUpdate = Date_Update.getText().toString();
        hourUpdate = Hour_Update.getText().toString();
        statusUpdate = New_Status.getText().toString();
        String userId = Userid_User_Update.getText().toString();

        CollectionReference collectionRef = db.collection("Events");
        Query query = collectionRef.whereEqualTo("eventId", id_event_R);

        query.get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        // Accesses document data using documentSnapshot.getData()
                        Map<String, Object> data = documentSnapshot.getData();

                        // Makes the necessary changes to the data
                        data.put("title", titleUpdate);
                        data.put("description", descriptionUpdate);
                        data.put("eventDate", dateUpdate);
                        data.put("eventHour", hourUpdate);
                        data.put("status", statusUpdate);
                        data.put("userId", userId);
                        data.put("userMail", user_mail_R);
                        data.put("eventId", id_event_R);

                        // Save changes to the document
                        documentSnapshot.getReference().set(data)
                                .addOnSuccessListener(aVoid -> {
                                    // Successful modification for document
                                    Log.d(TAG, "Document successfully modified.");
                                    toastOk("Updated event");
                                })
                                .addOnFailureListener(e -> {
                                    // Error when modifying the document
                                    Log.e(TAG, "Error when modifying the document.", e);
                                    toastWarning("Failure to update event");
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error obtaining the document.", e);
                    toastWarning("Event not found");
                });
    }

    //Check event status
    private void checkEventStatus() {
        event_Status = Status_Update.getText().toString();

        if (event_Status.equals("Not finished")) {
            Event_not_finished.setVisibility(View.VISIBLE);
        }
        if (event_Status.equals("Finished")) {
            Event_finished.setVisibility(View.VISIBLE);
        }
    }

    private void spinner_Status() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.Event_Status, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner_Status.setAdapter(adapter);
        Spinner_Status.setOnItemSelectedListener(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
        String status_Selected = adapterView.getItemAtPosition(position).toString();
        New_Status.setText(status_Selected);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.Update_Event) {
            updateEventFirebase();
            Intent intent = new Intent(UpdateEvents.this, MainActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_update_event, menu);
        return super.onCreateOptionsMenu(menu);
    }

    //Backward functionality
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

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
        View view = layoutInflater.inflate(R.layout.toast_warning,
                findViewById(R.id.custom_warning));
        TextView txtMensaje = view.findViewById(R.id.text_warning);
        txtMensaje.setText(msg);

        Toast toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.CENTER_VERTICAL | Gravity.BOTTOM, 0, 200);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(view);
        toast.show();
    }

}