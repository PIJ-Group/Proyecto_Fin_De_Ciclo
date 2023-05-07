package com.example.reminder;

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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.Map;

public class UpdateNote extends AppCompatActivity {

    TextView Id_Note_Update, Userid_User_Update, Registration_Date_Update, Date_Update,
            Hour_Update, Status_Update;
    EditText Title_Update, Description_Update;
    Button Calendar_btn_Update, Hour_btn_Update;

    //Declare strings to store the data of the main activity
    String id_note_R, user_id_R, registration_date_R, title_R, description_R, date_R, hour_R, status_R;

    //Declare strings to get data for update the note in firebase
    String titleUpdate, descriptionUpdate, dateUpdate, hourUpdate, statusUpdate;

    FirebaseFirestore db;
    FirebaseAuth nAuth;

    int year, month, day, hour,minutes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_note);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Update Note");
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        VarInit();
        GetData();
        SetData();
        Calendar_btn_Update.setOnClickListener(v -> dateSelect());
        Hour_btn_Update.setOnClickListener(v -> selectHour());
    }

    //Initialize Views
    private void VarInit(){
        Id_Note_Update = findViewById(R.id.Id_Note_Update);
        Userid_User_Update = findViewById(R.id.Userid_User_Update);
        Registration_Date_Update = findViewById(R.id.Registration_Date_Update);
        Date_Update = findViewById(R.id.Date_Update);
        Hour_Update = findViewById(R.id.Hour_Update);
        Status_Update = findViewById(R.id.Status_Update);
        Title_Update = findViewById(R.id.Title_Update);
        Description_Update = findViewById(R.id.Description_Update);
        Calendar_btn_Update = findViewById(R.id.Calendar_btn_Update);
        Hour_btn_Update = findViewById(R.id.Hour_btn_Update);

        db = FirebaseFirestore.getInstance();
        nAuth = FirebaseAuth.getInstance();

    }

    //Get data from intent in Main Activity
    private void GetData(){
        Bundle intent = getIntent().getExtras();
        id_note_R = intent.getString("nombre de la clave usada en main");
        user_id_R = intent.getString("nombre la clave usada en main");
        registration_date_R = intent.getString("nombre la clave usada en main");
        title_R = intent.getString("nombre la clave usada en main");
        description_R = intent.getString("nombre de la clave usada en main");
        date_R = intent.getString("nombre de la clave usada en main");
        hour_R = intent.getString("nombre de la clave usada en main");
        status_R = intent.getString("nombre de la clave usada en main");
    }

    //Setting Data
    private void SetData(){
        Id_Note_Update.setText(id_note_R);
        Userid_User_Update.setText(user_id_R);
        Registration_Date_Update.setText(registration_date_R);
        Date_Update.setText(date_R);
        Hour_Update.setText(hour_R);
        Status_Update.setText(status_R);
        Title_Update.setText(title_R);
        Description_Update.setText(description_R);
    }

    //method to select a date
    private void dateSelect(){
        final Calendar calendar = Calendar.getInstance();
        day = calendar.get(Calendar.DAY_OF_MONTH);
        month = calendar.get(Calendar.MONTH);
        year = calendar.get(Calendar.YEAR);

        DatePickerDialog datePickerDialog = new DatePickerDialog(UpdateNote.this, new DatePickerDialog.OnDateSetListener() {
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
                Date_Update.setText(dayFormatted + "/" + monthFormatted + "/" + yearSelected);
            }
        }
                , year, month, day);
        datePickerDialog.show();
    }

    //method to select an hour
    private void selectHour(){
        final Calendar clock = Calendar.getInstance();
        hour = clock.get(Calendar.HOUR_OF_DAY);
        minutes = clock.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(UpdateNote.this, new TimePickerDialog.OnTimeSetListener() {
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
                Hour_Update.setText(hourFormatted + ":" + minutesFormatted);
            }
        },hour,minutes,false);
        timePickerDialog.show();
    }

    private void updateNoteFirebase(){

        String userMail = nAuth.getCurrentUser().getEmail();
        titleUpdate = Title_Update.getText().toString();
        descriptionUpdate = Description_Update.getText().toString();
        dateUpdate = Date_Update.getText().toString();
        hourUpdate = Hour_Update.getText().toString();
        statusUpdate = Status_Update.getText().toString();
        String userId = Userid_User_Update.getText().toString();


        Note note = new Note(userMail + "/" + registration_date_R , userId,
                userMail, registration_date_R, titleUpdate, descriptionUpdate, dateUpdate, hourUpdate, statusUpdate);

        DocumentReference documentReference = db.collection("Notes").document("noteId");

        documentReference
                .update((Map<String, Object>) note)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        toastOk("Updated Note");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        toastWarning("Failure to update note");
                    }
                });



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_update_note, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.Update_Note) {
            updateNoteFirebase();
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