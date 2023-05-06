package com.example.reminder;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class UpdateNote extends AppCompatActivity {

    TextView Id_Note_Update, Userid_User_Update, Registration_Date_Update, Date_Update,
            Hour_Update, Status_Update;
    EditText Title_Update, Description_Update;
    Button Calendar_btn_Update, Hour_btn_Update;

    //Declare strings to store the data of the main activity
    String id_note_R, user_id_R, registration_date_R, title_R, description_R, date_R, hour_R, status_R;

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
}