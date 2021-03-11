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


public class Github_reaction extends AppCompatActivity {
    String userInfos;
    String TriggerJSON;
    String ActionJSON;

    @Override
    public void onBackPressed() {
        Intent prevInt = getIntent();

        userInfos = prevInt.getStringExtra("json");
        TriggerJSON = prevInt.getStringExtra("trigger");
        ActionJSON = prevInt.getStringExtra("action");
        finish();
        Intent intent = new Intent(Github_reaction.this, AddAREA.class);
        intent.putExtra("json", userInfos);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.github_reaction);
        JSONObject pullRequest = new JSONObject();
        JSONObject conditionPR = new JSONObject();
        JSONObject branch = new JSONObject();
        JSONObject conditionB = new JSONObject();
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
            Intent intent = new Intent(Github_reaction.this, AddAREA.class);
            intent.putExtra("json", userInfos);

            EditText idPullRequest = findViewById(R.id.id_pull_request);
            EditText head = findViewById(R.id.head);
            EditText base = findViewById(R.id.base);
            EditText titlePullRequest = findViewById(R.id.title_pull_request);
            EditText body = findViewById(R.id.body);
            EditText idBranch = findViewById(R.id.id_branch);
            EditText name = findViewById(R.id.name_of_branch);
            EditText sha = findViewById(R.id.sha);

            if (!idPullRequest.getText().toString().equals("") && !head.getText().toString().equals("") && !base.getText().toString().equals("")
                    && !titlePullRequest.getText().toString().equals("") && !body.getText().toString().equals("")) {
                try {
                    pullRequest.put("id", idPullRequest.getText().toString());
                    pullRequest.put("head", head.getText().toString());
                    pullRequest.put("base", base.getText().toString());
                    pullRequest.put("title", titlePullRequest.getText().toString());
                    pullRequest.put("body", body.getText().toString());
                    String obj = pullRequest.toString();
                    conditionPR.put("event", 1);
                    conditionPR.put("action", obj);
                } catch (JSONException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
                arr.put(conditionPR);
            }

            if (!idBranch.getText().toString().equals("") && !name.getText().toString().equals("") && !sha.getText().toString().equals("")) {
                try {
                    branch.put("id", idBranch.getText().toString());
                    branch.put("name", name.getText().toString());
                    branch.put("sha", sha.getText().toString());
                    String obj = branch.toString();
                    conditionB.put("event", 2);
                    conditionB.put("action", obj);
                } catch (JSONException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
                arr.put(conditionB);
            }

            try {
                res.put("service", 4);
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
