package com.example.area.actions;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;

import com.example.area.R;
import com.example.area.home.AddAREA;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


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

        JSONObject day = new JSONObject();
        JSONObject recu = new JSONObject();
        JSONObject res = new JSONObject();
        JSONArray arr = new JSONArray();

        ImageButton backBtn = findViewById(R.id.back);
        backBtn.setOnClickListener(v -> {onBackPressed();});

        Intent prevInt = getIntent();
        userInfos = prevInt.getStringExtra("json");
        TriggerJSON = prevInt.getStringExtra("trigger");
        ActionJSON = prevInt.getStringExtra("action");

        Button valid = findViewById(R.id.valid_btn);
        valid.setOnClickListener(v -> {
            Intent intent = new Intent(Timer_action.this, AddAREA.class);
            intent.putExtra("json", userInfos);

            EditText nbDay = findViewById(R.id.number_of_days);
            EditText recurrence = findViewById(R.id.recurrence);
            RadioGroup recurrenceRadioGroup = findViewById(R.id.recurrence_radio_group);

            /*if (!nbDay.toString().equals("")) {
                try {
                    day.put("event", 1);
                    day.put("data", nbDay.getText().toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
                arr.put(day);
            }*/

            if (!(recurrenceRadioGroup.getCheckedRadioButtonId() == -1) && !recurrence.toString().equals("")) {
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
