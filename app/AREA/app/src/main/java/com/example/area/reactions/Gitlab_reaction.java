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


public class Gitlab_reaction extends AppCompatActivity {
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
        setContentView(R.layout.gitlab_reaction);

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

        EditText idBranch = findViewById(R.id.id_branch);
        EditText name = findViewById(R.id.name_of_branch);
        EditText sha = findViewById(R.id.sha);

        Button valid = findViewById(R.id.valid_btn);
        valid.setOnClickListener(v -> {
            Intent intent = new Intent(Gitlab_reaction.this, AddAREA.class);
            intent.putExtra("json", userInfos);

            if (!idBranch.getText().toString().equals("") && !name.getText().toString().equals("") && !sha.getText().toString().equals("")) {
                try {
                    JSON.put("id", idBranch.getText().toString());
                    JSON.put("name", name.getText().toString());
                    JSON.put("sha", sha.getText().toString());
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
                res.put("service", 6);
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
