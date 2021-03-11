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


public class Google_drive_reaction extends AppCompatActivity {

    OkHttpClient client = new OkHttpClient();
    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

    String userInfos;
    String token;
    String email;
    String TriggerJSON;
    String ActionJSON;
    String file;

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.google_drive_reaction);

        JSONObject JSON = new JSONObject();
        JSONObject res = new JSONObject();
        JSONArray arr = new JSONArray();

        ImageButton backBtn = findViewById(R.id.back);
        backBtn.setOnClickListener(v -> {onBackPressed();});

        Intent prevInt = getIntent();
        userInfos = prevInt.getStringExtra("json");
        TriggerJSON = prevInt.getStringExtra("trigger");
        ActionJSON = prevInt.getStringExtra("action");

        Spinner spinner = findViewById(R.id.spinner);
        List fileList = new ArrayList();

        try {
            JSONObject obj = new JSONObject(userInfos);
            token = obj.getString("token");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Request request = new Request.Builder()
                .url("http://10.0.2.2:8080/api/drive/getFiles")
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
                JSONObject obj = null;
                try {
                    obj = new JSONObject(res);
                    //Log.e("RES", res);
                    //Log.e("OBJ", obj.toString());
                    JSONArray cast = obj.getJSONArray("items");
                    for (int i = 0; i < cast.length(); ++i) {
                        JSONObject rec = cast.getJSONObject(i);
                        String title = rec.getString("title");
                        Log.e("title", title);
                        fileList.add(title);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        ArrayAdapter adapter = new ArrayAdapter(
                this,
                android.R.layout.simple_spinner_item,
                fileList
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String myRegion = String.valueOf(spinner.getSelectedItem());
                Toast.makeText(Google_drive_reaction.this,
                        "OnClickListener : " +
                                "\nSpinner 1 : " + myRegion,
                        Toast.LENGTH_SHORT).show();
                file = myRegion;
                Log.e("FILE", file);
                Log.e("MYREGION", myRegion);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        Log.e("DEBUG 1", "DEBUGDEBUGDEBUGDEBUG");

        Button valid = findViewById(R.id.valid_btn);
        valid.setOnClickListener(v -> {
            Intent intent = new Intent(Google_drive_reaction.this, AddAREA.class);
            intent.putExtra("json", userInfos);

            EditText email = findViewById(R.id.email);

            //file = String.valueOf(spinner.getSelectedItem());
            //Log.e("FILE", file);
            //Log.e("FILELIST", fileList.toString());

            if (!email.getText().toString().equals("")) {
                try {
                    JSON.put("event", 1);
                    JSON.put("email", email.getText().toString());
                    JSON.put("Files", fileList.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
                arr.put(JSON);
            }

            try {
                res.put("action", 2);
                res.put("conditions", arr);
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
