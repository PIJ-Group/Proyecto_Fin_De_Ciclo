package com.example.reminder.ListEvents;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.reminder.R;

public class ListEvents extends AppCompatActivity {

    TextView Date_Detail, Hour_Detail, Status_Detail;
    EditText Title_Detail, Description_Detail;
    ImageView Event_finished, Event_not_finished;

    //Declare String for eventStatus
    String event_Status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_events);

        //Set Action Bar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Event");
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        VarInit();
        GetData();
        checkEventStatus();
    }
    //Initialize Views
    private void VarInit() {
        Date_Detail = findViewById(R.id.Date_Detail);
        Hour_Detail = findViewById(R.id.Hour_Detail);
        Status_Detail = findViewById(R.id.Status_Detail);
        Title_Detail = findViewById(R.id.Title_Detail);
        Description_Detail = findViewById(R.id.Description_Detail);
        Event_finished = findViewById(R.id.Event_finished_List);
        Event_not_finished = findViewById(R.id.Event_not_finished_List);
    }

    //Get Data from intent in Main Activity and display it in Views
    private void GetData(){
        Date_Detail.setText(getIntent().getStringExtra("eventDate"));
        Hour_Detail.setText(getIntent().getStringExtra("eventHour"));
        Status_Detail.setText(getIntent().getStringExtra("status"));
        Title_Detail.setText(getIntent().getStringExtra("title"));
        Description_Detail.setText(getIntent().getStringExtra("description"));
    }

    //Check event status
    private void checkEventStatus(){
        event_Status = Status_Detail.getText().toString();

        if(event_Status.equals("Not finished")){
            Event_not_finished.setVisibility(View.VISIBLE);
        }
        if(event_Status.equals("Finished")){
            Event_finished.setVisibility(View.VISIBLE);
        }
    }

    //Backward functionality
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}