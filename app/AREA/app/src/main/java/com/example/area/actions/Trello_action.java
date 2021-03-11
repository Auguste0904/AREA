package com.example.area.actions;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.area.R;
import com.example.area.home.AddAREA;
import com.example.area.reactions.Google_drive_reaction;

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


public class Trello_action extends AppCompatActivity {
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
        setContentView(R.layout.trello_action);

        JSONObject JSONid = new JSONObject();
        JSONObject res = new JSONObject();
        JSONArray arr = new JSONArray();

        Intent prevInt = getIntent();
        userInfos = prevInt.getStringExtra("json");
        TriggerJSON = prevInt.getStringExtra("trigger");
        ActionJSON = prevInt.getStringExtra("action");

        ImageButton backBtn = findViewById(R.id.back);
        backBtn.setOnClickListener(v -> onBackPressed());

        Spinner boardId = findViewById(R.id.board_id);
        List boardIdList = new ArrayList();

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
                JSONArray obj = null;
                try {
                    obj = new JSONArray(res);
                    Log.e("RES", res);
                    Log.e("OBJ", obj.toString());
                    for (int i = 0; i < obj.length(); ++i) {
                        JSONObject rec = obj.getJSONObject(i);
                        String title = rec.getString("name");
                        String id = rec.getString("id");
                        boardIdList.add(title);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        ArrayAdapter adapter = new ArrayAdapter(
                this,
                android.R.layout.simple_spinner_item,
                boardIdList
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        boardId.setAdapter(adapter);

        boardId.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String myRegion = String.valueOf(boardId.getSelectedItem());
                Toast.makeText(Trello_action.this,
                        "OnClickListener : " +
                                "\nSpinner 1 : " + myRegion,
                        Toast.LENGTH_SHORT).show();
                Log.e("MYREGION", myRegion);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        Spinner listId = findViewById(R.id.list_id);
        List listIdList = new ArrayList();

        try {
            JSONObject obj = new JSONObject(userInfos);
            token = obj.getString("token");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Request request2 = new Request.Builder()
                .url("http://10.0.2.2:8080/api/trello/get_list/601addfeb153854a716301d0")
                .get()
                .addHeader("Authorization", "Bearer " + token)
                .build();

        client.newCall(request2).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.e("TEST", "ERROR: failed to get user infos");
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String res = response.body().string();
                JSONArray obj = null;
                try {
                    obj = new JSONArray(res);
                    Log.e("RES", res);
                    Log.e("OBJ", obj.toString());
                    for (int i = 0; i < obj.length(); ++i) {
                        JSONObject rec = obj.getJSONObject(i);
                        String title = rec.getString("name");
                        Log.e("name", title);
                        listIdList.add(title);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        ArrayAdapter adapter2 = new ArrayAdapter(
                this,
                android.R.layout.simple_spinner_item,
                listIdList
        );
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        listId.setAdapter(adapter2);

        listId.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String myRegion = String.valueOf(listId.getSelectedItem());
                Toast.makeText(Trello_action.this,
                        "OnClickListener : " +
                                "\nSpinner 1 : " + myRegion,
                        Toast.LENGTH_SHORT).show();
                Log.e("MYREGION", myRegion);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        Button valid = findViewById(R.id.valid_btn);
        valid.setOnClickListener(v -> {
            Intent intent = new Intent(Trello_action.this, AddAREA.class);
            intent.putExtra("json", userInfos);

            if (!boardId.toString().equals("") && !listId.toString().equals("")) {
                try {
                    JSONid.put("event", 1);
                    JSONid.put("data", boardIdList.toString() + ' ' + listIdList.toString());
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
