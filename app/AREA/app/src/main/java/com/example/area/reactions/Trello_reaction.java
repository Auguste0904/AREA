package com.example.area.reactions;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.area.R;
import com.example.area.home.AddAREA;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class Trello_reaction extends AppCompatActivity {
    OkHttpClient client = new OkHttpClient();
    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

    String userInfos;
    String TriggerJSON;
    String ActionJSON;
    String token;

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trello_reaction);

        JSONObject JSONid = new JSONObject();
        JSONObject res = new JSONObject();
        JSONArray arr = new JSONArray();

        Intent prevInt = getIntent();
        userInfos = prevInt.getStringExtra("json");
        TriggerJSON = prevInt.getStringExtra("trigger");
        ActionJSON = prevInt.getStringExtra("action");

        ImageButton backBtn = findViewById(R.id.back);
        backBtn.setOnClickListener(v -> onBackPressed());

        Button valid = findViewById(R.id.valid_btn);
        valid.setOnClickListener(v -> {
            Intent intent = new Intent(Trello_reaction.this, AddAREA.class);
            intent.putExtra("json", userInfos);

            EditText boardId = findViewById(R.id.board_id);
            EditText listName = findViewById(R.id.list_name);

            if (!boardId.getText().toString().equals("") && !listName.getText().toString().equals("")) {
                try {
                    JSONid.put("event", 1);
                    JSONid.put("data", boardId.getText().toString() + ' ' + listName.getText().toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
                arr.put(JSONid);
            }

            try {
                res.put("trigger", 5);
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
