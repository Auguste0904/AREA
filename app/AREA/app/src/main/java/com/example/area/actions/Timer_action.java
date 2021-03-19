package com.example.area.actions;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;

import com.example.area.R;
import com.example.area.home.AddAREA;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.Format;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;


public class Timer_action extends AppCompatActivity {
    String userInfos;
    String TriggerJSON;
    String ActionJSON;

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.timer_action);

        JSONObject daysUntil = new JSONObject();
        JSONObject recu = new JSONObject();
        JSONObject res = new JSONObject();
        JSONArray arr = new JSONArray();

        ImageButton back = findViewById(R.id.back);
        back.setOnClickListener(v -> onBackPressed());

        Intent prevInt = getIntent();
        userInfos = prevInt.getStringExtra("json");
        TriggerJSON = prevInt.getStringExtra("trigger");
        ActionJSON = prevInt.getStringExtra("action");

        Button valid = findViewById(R.id.valid_btn);
        valid.setOnClickListener(v -> {
            Intent intent = new Intent(Timer_action.this, AddAREA.class);
            intent.putExtra("json", userInfos);

            EditText nbDay = findViewById(R.id.number_of_days);
            DatePicker date = findViewById(R.id.date);
            EditText recurrence = findViewById(R.id.recurrence);
            RadioGroup recurrenceRadioGroup = findViewById(R.id.recurrence_radio_group);

            /* Log.e("Recup date", date.getText().toString());
            Log.e("Modified date", date.getText().toString().replace('/', '-')); */

            date.setBackgroundColor(Color.WHITE);
            Log.e("Get Day", String.valueOf(date.getDayOfMonth()));
            Log.e("Get month", String.valueOf(date.getMonth()));
            Log.e("Get year", String.valueOf(date.getYear()));

            String day = String.valueOf(date.getDayOfMonth());
            String month = String.valueOf(date.getMonth());
            String year = String.valueOf(date.getYear());

            if (!nbDay.getText().toString().equals("") && !day.equals("") && !month.equals("") && !year.equals("")) {
                String dateFormated = day + '-' + month + '-' + year;
                DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
                Date parsedDate = null;
                try {
                    parsedDate = (Date)formatter.parse(dateFormated);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                String timestamp = String.valueOf(parsedDate.getTime() / 1000);
                Log.e("Timestamp", timestamp);
                try {
                    daysUntil.put("event", 1);
                    daysUntil.put("condition", nbDay.getText().toString() + ' ' + timestamp);
                } catch (JSONException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
                arr.put(daysUntil);
            }

            if (!(recurrenceRadioGroup.getCheckedRadioButtonId() == -1) && !recurrence.getText().toString().equals("")) {
                String radioTime = recurrence.getText().toString();
                if (recurrenceRadioGroup.getCheckedRadioButtonId() == R.id.week)
                    radioTime += " week";
                else if (recurrenceRadioGroup.getCheckedRadioButtonId() == R.id.month)
                    radioTime += " month";
                else
                    radioTime += " day";
                try {
                    recu.put("event", 2);
                    recu.put("condition", radioTime);
                } catch (JSONException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
                arr.put(recu);
            }

            try {
                res.put("trigger", 7);
                res.put("conditions", arr);
            } catch (JSONException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }

            intent.putExtra("trigger", res.toString());
            intent.putExtra("action", ActionJSON);
            startActivity(intent);
        });
    }
}
