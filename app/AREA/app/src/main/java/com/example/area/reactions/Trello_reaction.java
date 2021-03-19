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
    Spinner spinner;
    String shareID;

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

        spinner = findViewById(R.id.board_id);
        List<String> fileList = new ArrayList<String>();
        List<String> idList = new ArrayList<String>();

        try {
            JSONObject obj = new JSONObject(userInfos);
            token = obj.getString("token");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Request request = new Request.Builder()
                .url("http://10.0.2.2:8080/api/trello/get_board")
                .get()
                .addHeader("Authorization", "Bearer " + token)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.e("TEST", "ERROR: failed to get user infos");
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String res = response.body().string();
                Log.e("JSON", res);
                try {
                    JSONArray cast = new JSONArray(res);
                    for (int i = 0; i < cast.length(); ++i) {
                        JSONObject rec = cast.getJSONObject(i);
                        String title = rec.getString("name");
                        String id = rec.getString("id");
                        fileList.add(title);
                        idList.add(id);
                    }
                    runOnUiThread(() -> {
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(Trello_reaction.this, android.R.layout.simple_spinner_item, fileList);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinner.setAdapter(adapter);
                        spinner.setSelection(1);
                        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                String text = parent.getItemAtPosition(position).toString();
                                shareID = idList.get(position);
                                Log.e("File", text);
                                Log.e("File ID", shareID);
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {
                                Log.e("PAS SUPER", "TROP NULL");
                            }
                        });
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        Button valid = findViewById(R.id.valid_btn);
        valid.setOnClickListener(v -> {
            Intent intent = new Intent(Trello_reaction.this, AddAREA.class);
            intent.putExtra("json", userInfos);

            EditText listName = findViewById(R.id.list_name);

            if (!listName.getText().toString().equals("")) {
                try {
                    JSONid.put("event", 1);
                    JSONid.put("action", shareID + ' ' + listName.getText().toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
                arr.put(JSONid);
            }

            try {
                res.put("service", 5);
                res.put("actions", arr);
            } catch (JSONException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }

            intent.putExtra("trigger", TriggerJSON);
            intent.putExtra("action", res.toString());
            startActivity(intent);
        });
    }
}
