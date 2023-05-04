package com.example.reminder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

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
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AddNote extends AppCompatActivity {

    TextView Userid_User, Current_Date_time, Date, Hour, Status;
    EditText Title, Description;
    Button Calendar_btn, Hour_btn;

    int day, month, year, hour, minutes;
    String userMail;

    FirebaseFirestore db;
    FirebaseAuth nAuth;
    String dateRecover, userId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Add Note");
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }


        VarInit();
        DataObtent();
        CurrentDateTimeObtent();

        Calendar_btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                day = calendar.get(Calendar.DAY_OF_MONTH);
                month = calendar.get(Calendar.MONTH);
                year = calendar.get(Calendar.YEAR);

                DatePickerDialog datePickerDialog = new DatePickerDialog(AddNote.this, new DatePickerDialog.OnDateSetListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onDateSet(DatePicker view, int yearSelected, int monthSelected, int daySelected) {

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
                    }
                }
                        , year, month, day);
                datePickerDialog.show();
            }
        });

        Hour_btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar clock = Calendar.getInstance();
                hour = clock.get(Calendar.HOUR_OF_DAY);
                minutes = clock.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(AddNote.this, new TimePickerDialog.OnTimeSetListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

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
                    }
                },hour,minutes,false);
                timePickerDialog.show();
            }
        });

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
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                if (documentSnapshot != null) {
                    Userid_User.setText(documentSnapshot.getString("user_name"));
                }
            }
        });

        //Gets the date sent in the MainActivity intent.
        dateRecover = getIntent().getStringExtra("calendarDate");

        //Get the user mail from authentication
        userMail = nAuth.getCurrentUser().getEmail();

        Date.setText(dateRecover);

    }

    //Gets the system date and time
    private void CurrentDateTimeObtent() {
        String DateTimeReg = new SimpleDateFormat("dd-MM-yyyy/HH:mm:ss a",
                Locale.getDefault()).format(System.currentTimeMillis());
        Current_Date_time.setText(DateTimeReg);
    }


    //Add a note in Notes collection in Firebase Database
    private void AddNoteFireBase() {

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

            Note note = new Note(mail + "/" + dateTimeCurrent, userId,
                    mail, dateTimeCurrent, title, description, date, hour, status);

            db.collection("Notes").add(note);

            toastOk("Note successfully added");
            onBackPressed();

        } else {
            toastWarning("All fields must be filled");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_add_note, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.Add_Note_BD) {
            AddNoteFireBase();
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