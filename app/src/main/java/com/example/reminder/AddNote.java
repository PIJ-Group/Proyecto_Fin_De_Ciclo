package com.example.reminder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
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
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AddNote extends AppCompatActivity {

    TextView Userid_User, User_mail, Current_Date_time, Date, Status;
    EditText Title, Description;
    Button Calendar_btn;

    int day, month, year;

    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        ActionBar actionBar= getSupportActionBar();
        actionBar.setTitle("");
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        VarInit();
        DataObtent();
        CurrentDateTimeObtent();

        Calendar_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                day = calendar.get(Calendar.DAY_OF_MONTH);
                month = calendar.get(Calendar.MONTH);
                year = calendar.get(Calendar.YEAR);

                DatePickerDialog datePickerDialog = new DatePickerDialog(AddNote.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int yearSelected, int monthSelected, int daySelected) {

                        String dayFormatted, monthFormatted;

                        //Get day
                        if (daySelected < 10){
                            dayFormatted = "0" + daySelected;
                        }else {
                            dayFormatted = String.valueOf(daySelected);
                        }
                        //Get month

                        int Month = monthSelected + 1;

                        if (Month < 10){
                            monthFormatted = "0" + Month;
                        }else {
                            monthFormatted = String.valueOf(Month);
                        }

                        // Set Date on TextView
                        Date.setText(dayFormatted + "/" + monthFormatted + "/" + yearSelected);
                    }
                }
                ,year,month,day);
                datePickerDialog.show();
            }
        });

    }

    private void VarInit(){
        Userid_User = findViewById(R.id.Userid_User);
        User_mail = findViewById(R.id.User_mail);
        Current_Date_time = findViewById(R.id.Current_Date_time);
        Date = findViewById(R.id.Date);
        Status = findViewById(R.id.Status);

        Title = findViewById(R.id.Title);
        Description = findViewById((R.id.Description));

        Calendar_btn = findViewById(R.id.Calendar_btn);

        db = FirebaseFirestore.getInstance();
    }
    //Obtiene los datos de usuario del menú ppal
    //OJO tiene que hacer Jorge lo del video 29
    private void DataObtent(){
        // OJO poner dentro de los paréntesis el nombre del dato enviado en el menu ppal
        String userIdRecover = getIntent().getStringExtra("Uid");
        String userMailRecover = getIntent().getStringExtra("Mail");

        Userid_User.setText(userIdRecover);
        User_mail.setText(userMailRecover);
    }

    private void CurrentDateTimeObtent(){
        String DateTimeReg = new SimpleDateFormat("dd-MM-yyyy/HH:mm:ss a",
                Locale.getDefault()).format(System.currentTimeMillis());
        Current_Date_time.setText(DateTimeReg);
    }

    private void AddNoteFireBase(){

        //Get data
        String userId = Userid_User.getText().toString();
        String eMail = User_mail.getText().toString();
        String dateTimeCurrent = Current_Date_time.getText().toString();
        String title = Title.getText().toString();
        String description = Description.getText().toString();
        String date = Date.getText().toString();
        String status = Status.getText().toString();

        //Data validation
        if(!userId.equals("") || !eMail.equals("") || !dateTimeCurrent.equals("") ||
                !title.equals("") || !description.equals("") || date.equals("") ||
                !status.equals("")){

            Note note = new Note(eMail + "/" + dateTimeCurrent, userId,
                    eMail,dateTimeCurrent,title, description, date, status);

            db.collection("Tasks").document("userId").set(note);

            toastOk("Note successfully added");
            onBackPressed();

            //Video 36 min 8

        }else{
            toastWarning("All fields must be filled");
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_add_note,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.Add_Note_BD:
                AddNoteFireBase();
            break;
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