package com.example.area.home;

import android.app.Notification;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.example.area.R;
import com.example.area.Services.Services_action;
import com.example.area.Services.Services_reaction;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class AddAREA extends AppCompatActivity {

    @Override
    public void onBackPressed() {
        finish();
    }

    OkHttpClient client = new OkHttpClient();
    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

    String token;
    String TriggerJSON;
    String ActionJSON;
    String tmpTrigger;
    String tmpAction;
    String res;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_area);

        Intent prevInt = getIntent();
        String userInfos = prevInt.getStringExtra("json");
        TriggerJSON = prevInt.getStringExtra("trigger");
        ActionJSON = prevInt.getStringExtra("action");

        Log.e("TEST", "user: " + userInfos);
        Log.e("TEST", "TriggerJSON: " + TriggerJSON);
        Log.e("TEST", "ActionJSON: " + ActionJSON);

        if (TriggerJSON != null)
            tmpTrigger = TriggerJSON.substring(0,TriggerJSON.length() - 1);
        if (ActionJSON != null)
            tmpAction = ActionJSON.substring(0,0) + ActionJSON.substring(1);

        res = tmpTrigger + "," + tmpAction;
        Log.e("TEST", "Res: " + res);

        try {
            JSONObject obj = new JSONObject(userInfos);
            token = obj.getString("token");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ImageButton backBtn = findViewById(R.id.back);
        backBtn.setOnClickListener(v -> {onBackPressed();});

        Button action_btn = findViewById(R.id.action);
        action_btn.setOnClickListener(v -> {
            Intent intent = new Intent(AddAREA.this, Services_action.class);
            intent.putExtra("json", userInfos);
            intent.putExtra("trigger", TriggerJSON);
            intent.putExtra("action", ActionJSON);
            startActivity(intent);
        });

        Button reaction_btn = findViewById(R.id.reaction);
        reaction_btn.setOnClickListener(v -> {
            Intent intent = new Intent(AddAREA.this, Services_reaction.class);
            intent.putExtra("json", userInfos);
            intent.putExtra("trigger", TriggerJSON);
            intent.putExtra("action", ActionJSON);
            startActivity(intent);
        });

        // bouton valider avec le call des infos passé et le post create.
        Button confirm = findViewById(R.id.valid);
        confirm.setOnClickListener(v -> {
            if (!(tmpTrigger == null) && !(tmpAction == null)) {
                try {
                    Log.e("test", token);
                    Log.e("test", res);
                    // post
                    RequestBody body = RequestBody.create(JSON, res);

                    Request request = new Request.Builder()
                            .url("http://10.0.2.2:8080/api/service/create")
                            .addHeader("Authorization", "Bearer " + token)
                            .post(body)
                            .build();

                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(@NotNull Call call, @NotNull IOException e) {
                            Log.e("ERR ", "REQ FAILED **********************************************");
                            e.printStackTrace();
                        }

                        @Override
                        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                            if (!response.isSuccessful()) {
                                throw new IOException("Unexpected code " + response);
                            } else {
                                Log.e("TEST", "**********************************************" + response.body().string());
                                runOnUiThread(() -> {
                                    Intent intent = new Intent(AddAREA.this, HomePage.class);
                                    intent.putExtra("json", userInfos);
                                    startActivity(intent);
                                });
                            }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}