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
    }

    private void VarInit() {
        Date_Detail = findViewById(R.id.Date_Detail);
        Hour_Detail = findViewById(R.id.Hour_Detail);
        Status_Detail = findViewById(R.id.Status_Detail);
        Title_Detail = findViewById(R.id.Title_Detail);
        Description_Detail = findViewById(R.id.Description_Detail);
    }

    private void GetData(){

        Date_Detail.setText(getIntent().getStringExtra("noteDate"));
        Hour_Detail.setText(getIntent().getStringExtra("noteHour"));
        Status_Detail.setText(getIntent().getStringExtra("status"));
        Title_Detail.setText(getIntent().getStringExtra("title"));
        Description_Detail.setText(getIntent().getStringExtra("description"));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}