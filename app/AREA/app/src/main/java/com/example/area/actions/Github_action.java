package com.example.area.actions;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;

import com.example.area.R;
import com.example.area.home.AddAREA;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class Github_action extends AppCompatActivity {
    String userInfos;
    String TriggerJSON;
    String ActionJSON;

    @Override
    public void onBackPressed() {
        ImageButton backBtn = findViewById(R.id.back);
        backBtn.setOnClickListener(v -> finish());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.github_action);

        JSONObject project = new JSONObject();
        JSONObject newCommit = new JSONObject();
        JSONObject pull_request = new JSONObject();
        JSONObject res = new JSONObject();
        JSONArray arr = new JSONArray();

        Intent prevInt = getIntent();
        userInfos = prevInt.getStringExtra("json");
        TriggerJSON = prevInt.getStringExtra("trigger");
        ActionJSON = prevInt.getStringExtra("action");

        Button valid = findViewById(R.id.valid_btn);
        valid.setOnClickListener(v -> {
            Intent intent = new Intent(Github_action.this, AddAREA.class);
            intent.putExtra("json", userInfos);

            Switch isNewProject = findViewById(R.id.new_project);
            EditText commit = findViewById(R.id.new_commit);
            EditText pullRequest = findViewById(R.id.new_pull_request);

            if (isNewProject.isChecked()) {
                try {
                    project.put("event", 1);
                } catch (JSONException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
                arr.put(project);
            }

            if (!commit.getText().toString().equals("")) {
                try {
                    newCommit.put("event", 2);
                    newCommit.put("condition", commit.getText().toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
                arr.put(newCommit);
            }

            if (!pullRequest.getText().toString().equals("")) {
                try {
                    pull_request.put("event", 3);
                    pull_request.put("condition", pullRequest.getText().toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
                arr.put(pull_request);
            }

            try {
                res.put("trigger", 4);
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
