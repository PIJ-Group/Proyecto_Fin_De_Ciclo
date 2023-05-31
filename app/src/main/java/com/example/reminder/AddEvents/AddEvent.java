package com.example.reminder.AddEvents;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.reminder.R;
import com.example.reminder.model.Event;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AddEvent extends AppCompatActivity {

    TextView Userid_User, Current_Date_time, Date, Hour, Status;
    EditText Title, Description;
    Button Calendar_btn, Hour_btn;

    int day, month, year, hour, minutes;
    String userMail;

    //Declare variables for Firebase actions
    FirebaseFirestore db;
    FirebaseAuth nAuth;
    String dateRecover, userId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        //Set Action Bar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Add Event");
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }


        VarInit();
        DataObtent();
        CurrentDateTimeObtent();
        Calendar_btn.setOnClickListener(v -> dateSelect());
        Hour_btn.setOnClickListener(v -> hourSelect());

    }

    //Inicialize variables
    private void VarInit() {
        Userid_User = findViewById(R.id.Userid_User);
        Current_Date_time = findViewById(R.id.Current_Date_time);
        Date = findViewById(R.id.Date);
        Hour = findViewById(R.id.Hour);
        Status = findViewById(R.id.Status);
        Title = findViewById(R.id.Title);
        Description = findViewById((R.id.Description));
        Calendar_btn = findViewById(R.id.Calendar_btn);
        Hour_btn = findViewById(R.id.Hour_btn);

        db = FirebaseFirestore.getInstance();
        nAuth = FirebaseAuth.getInstance();
    }

    //Gets the data from the main menu and from the Users collection in Firestore Database.
    private void DataObtent() {
        //Gets the user name from Users collection.
        FirebaseUser user = nAuth.getCurrentUser();
        if (user != null) {
            userId = user.getUid();
        }
        DocumentReference documentReference = db.collection("Users").document(userId);
        documentReference.addSnapshotListener(this, (documentSnapshot, error) -> {
            if (documentSnapshot != null) {
                Userid_User.setText(documentSnapshot.getString("user_name"));
            }
        });

        //Gets the date sent in the MainActivity intent.
        dateRecover = getIntent().getStringExtra("calendarDate");

        //Get the user mail from authentication
        userMail = nAuth.getCurrentUser().getEmail();
        if (userMail == null) {
            assert user != null;
            userMail = user.getProviderData().get(1).getEmail();
        }
        if (userMail == null) {
            userMail = user.getProviderData().get(1).getUid();
        }

        Date.setText(dateRecover);

    }

    //Gets the system date and time
    private void CurrentDateTimeObtent() {
        String DateTimeReg = new SimpleDateFormat("dd-MM-yyyy/HH:mm:ss a",
                Locale.getDefault()).format(System.currentTimeMillis());
        Current_Date_time.setText(DateTimeReg);
    }

    //method to select a date
    private void dateSelect() {
        final Calendar calendar = Calendar.getInstance();
        day = calendar.get(Calendar.DAY_OF_MONTH);
        month = calendar.get(Calendar.MONTH);
        year = calendar.get(Calendar.YEAR);

        @SuppressLint("SetTextI18n") DatePickerDialog datePickerDialog =
                new DatePickerDialog(AddEvent.this, (view, yearSelected,
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
                    Date.setText(dayFormatted + "/" + monthFormatted + "/" + yearSelected);
                }, year, month, day);

        datePickerDialog.show();
    }

    //method to select an hour
    private void hourSelect() {
        final Calendar clock = Calendar.getInstance();
        hour = clock.get(Calendar.HOUR_OF_DAY);
        minutes = clock.get(Calendar.MINUTE);

        @SuppressLint("SetTextI18n") TimePickerDialog timePickerDialog =
                new TimePickerDialog(AddEvent.this, (view, hourOfDay, minute) -> {
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
                    Hour.setText(hourFormatted + ":" + minutesFormatted);
                }, hour, minutes, false);

        timePickerDialog.show();
    }


    //Add a event in Events collection in Firebase Database
    private void AddEventFireBase() {

        //Get data
        String userId = Userid_User.getText().toString();
        String mail = userMail;
        String dateTimeCurrent = Current_Date_time.getText().toString();
        String title = Title.getText().toString();
        String description = Description.getText().toString();
        String date = Date.getText().toString();
        String hour = Hour.getText().toString();
        String status = Status.getText().toString();

        //Data validation
        if (!dateTimeCurrent.equals("") && !title.equals("") &&
                !description.equals("") && !date.equals("") &&
                !hour.equals("") && !status.equals("")) {

            Event event = new Event(mail + "/" + dateTimeCurrent, userId,
                    mail, dateTimeCurrent, title, description, date, hour, status);

            db.collection("Events").add(event);

            toastOk("Event successfully added");
            onBackPressed();

        } else {
            toastWarning("All fields must be filled");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_add_event, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.Add_Event_BD) {
            AddEventFireBase();
        }
        return super.onOptionsItemSelected(item);
    }

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