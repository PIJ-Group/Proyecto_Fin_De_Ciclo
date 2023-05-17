package com.example.reminder.ListNotes;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import com.example.reminder.R;

public class ListNotes extends AppCompatActivity {

    TextView Date_Detail, Hour_Detail, Status_Detail;
    EditText Title_Detail, Description_Detail;

    String date_D, hour_D, status_D, title_D, description_D;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_notes);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Event");
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        VarInit();
        GetData();
        SetData();
    }

    private void VarInit() {
        Date_Detail = findViewById(R.id.Date_Detail);
        Hour_Detail = findViewById(R.id.Hour_Detail);
        Status_Detail = findViewById(R.id.Status_Detail);
        Title_Detail = findViewById(R.id.Title_Detail);
        Description_Detail = findViewById(R.id.Description_Detail);
    }

    private void GetData(){
        date_D =getIntent().getStringExtra("noteDate");
        hour_D =getIntent().getStringExtra("noteHour");
        status_D =getIntent().getStringExtra("status");
        title_D =getIntent().getStringExtra("title");
        description_D =getIntent().getStringExtra("description");
//        Date_Detail.setText(getIntent().getStringExtra("noteDate"));
//        Hour_Detail.setText(getIntent().getStringExtra("noteHour"));
//        Status_Detail.setText(getIntent().getStringExtra("status"));
//        Title_Detail.setText(getIntent().getStringExtra("title"));
//        Description_Detail.setText(getIntent().getStringExtra("description"));
    }
    private void SetData(){
        Date_Detail.setText(date_D);
        Hour_Detail.setText(hour_D);
        Status_Detail.setText(status_D);
        Title_Detail.setText(title_D);
        //Te hemos añadido la descripción porque no estaba y no sabíamos qué pasaba, luego borra el comentario
        Description_Detail.setText(description_D);
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}