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

public class Gmail_action extends AppCompatActivity {
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
        setContentView(R.layout.gmail_action);

        JSONObject key_word = new JSONObject();
        JSONObject jsonTime = new JSONObject();
        JSONObject receive_from = new JSONObject();
        JSONObject res = new JSONObject();
        JSONArray arr = new JSONArray();

        Intent prevInt = getIntent();
        userInfos = prevInt.getStringExtra("json");
        TriggerJSON = prevInt.getStringExtra("trigger");
        ActionJSON = prevInt.getStringExtra("action");

        ImageButton backBtn = findViewById(R.id.back);
        backBtn.setOnClickListener(v -> {onBackPressed();});

        Button valid = findViewById(R.id.valid_btn);
        valid.setOnClickListener(v -> {
            Intent intent = new Intent(Gmail_action.this, AddAREA.class);
            intent.putExtra("json", userInfos);

            EditText keyWord = findViewById(R.id.key_word);
            EditText receiveFrom = findViewById(R.id.receive_from);
            RadioGroup whichRadioButton = findViewById(R.id.receive);
            EditText time = findViewById(R.id.time);

            if (!keyWord.getText().toString().equals("")) {
                try {
                    key_word.put("event", 1);
                    key_word.put("condition", keyWord.getText().toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
                arr.put(key_word);
            }

            if (!receiveFrom.getText().toString().equals("")) {
                try {
                    receive_from.put("event", 2);
                    receive_from.put("condition", receiveFrom.getText().toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
                arr.put(receive_from);
            }

            if (!(whichRadioButton.getCheckedRadioButtonId() == -1) && !time.getText().toString().equals("")) {
                String radioTime = "";
                if (whichRadioButton.getCheckedRadioButtonId() == R.id.before)
                    radioTime += "before ";
                else
                    radioTime += "after ";
                radioTime += time.getText().toString();
                try {
                    jsonTime.put("event", 3);
                    jsonTime.put("condition", radioTime);
                } catch (JSONException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
                arr.put(jsonTime);
            }

            try {
                res.put("trigger", 1);
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
