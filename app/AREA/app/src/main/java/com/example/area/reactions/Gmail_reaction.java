package com.example.area.reactions;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.example.area.R;
import com.example.area.home.AddAREA;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class Gmail_reaction extends AppCompatActivity {
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
        setContentView(R.layout.gmail_reaction);

        JSONObject JSON = new JSONObject();
        JSONObject condition = new JSONObject();
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
            Intent intent = new Intent(Gmail_reaction.this, AddAREA.class);
            intent.putExtra("json", userInfos);

            EditText email = findViewById(R.id.email);
            EditText object = findViewById(R.id.object);
            EditText body = findViewById(R.id.body);

            if (!email.getText().toString().equals("") && !object.getText().toString().equals("") && !body.getText().toString().equals("")) {
                try {
                    JSON.put("email", email.getText().toString());
                    JSON.put("object", object.getText().toString());
                    JSON.put("body", body.getText().toString());
                    String obj = JSON.toString();
                    condition.put("event", 1);
                    condition.put("action", obj);
                } catch (JSONException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
                arr.put(condition);
            }

            try {
                res.put("service", 1);
                res.put("actions", arr);
            } catch (JSONException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
            intent.putExtra("action", res.toString());
            intent.putExtra("trigger", TriggerJSON);
            startActivity(intent);
        });
    }
}
