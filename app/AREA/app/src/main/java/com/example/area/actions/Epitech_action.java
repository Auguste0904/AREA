package com.example.area.actions;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;

import com.example.area.R;
import com.example.area.home.AddAREA;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class Epitech_action extends AppCompatActivity {
    String userInfos;
    Boolean isNewMeeting;
    Boolean isNewAlert;
    Boolean isNewMissing;
    String TriggerJSON;
    String ActionJSON;

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.epitech_action);

        JSONObject newMeeting = new JSONObject();
        JSONObject newAlert = new JSONObject();
        JSONObject newMissing = new JSONObject();
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
            Intent intent = new Intent(Epitech_action.this, AddAREA.class);
            intent.putExtra("json", userInfos);

            isNewMeeting = getIsNewMeeting();
            isNewAlert = getIsNewAlert();
            isNewMissing = getIsNewMissing();

            if (isNewMeeting) {
                try {
                    newMeeting.put("event", 1);
                } catch (JSONException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
                arr.put(newMeeting);
            }

            if (isNewAlert) {
                try {
                    newAlert.put("event", 2);
                } catch (JSONException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
                arr.put(newAlert);
            }

            if (isNewMissing) {
                try {
                    newMissing.put("event", 3);
                } catch (JSONException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
                arr.put(newMissing);
            }

            try {
                res.put("trigger", 3);
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

    private Boolean getIsNewMeeting() {
        Switch meeting = findViewById(R.id.new_meeting);
        return meeting.isChecked();
    }

    private Boolean getIsNewAlert() {
        Switch alert = findViewById(R.id.new_alert);
        return alert.isChecked();
    }

    private Boolean getIsNewMissing() {
        Switch missing = findViewById(R.id.new_missing);
        return missing.isChecked();
    }
}
