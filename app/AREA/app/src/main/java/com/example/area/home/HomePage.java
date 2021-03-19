package com.example.area.home;

import android.content.Intent;
import android.os.Bundle;
import android.os.LocaleList;
import android.util.Log;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.area.Card;
import com.example.area.CardAdaptater;
import com.example.area.R;
import com.example.area.Services.Services_action;
import com.example.area.Services.Services_reaction;
import com.example.area.login_register.Login;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HomePage extends AppCompatActivity {

    OkHttpClient client = new OkHttpClient();
    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

    String token;
    ArrayList<Card> cards = new ArrayList<Card>();
    String TriggerJSON;
    String ActionJSON;

    @Override
    public void onBackPressed() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);

        Intent prevInt = getIntent();
        String userInfos = prevInt.getStringExtra("json");

        try {
            JSONObject obj = new JSONObject(userInfos);
            token = obj.getString("token");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Request request = new Request.Builder()
                .url("http://10.0.2.2:8080/api/user/me")
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
                    JSONArray cast = obj.getJSONArray("services");

                    for (int i=0; i<cast.length(); i++) {
                        JSONObject actor = cast.getJSONObject(i);
                        String id = actor.getString("_id");
                        String serviceTrigger = actor.getString("serviceTrigger");
                        String serviceAction = actor.getString("serviceAction");
                        String conditions = actor.getString("conditions");
                        String actions = actor.getString("actions");
                        String Trigger;
                        String Action;
                        String eventConditions;
                        String conditionString = "";
                        String conditionString1 = "";
                        String conditionString2 = "";
                        String conditionString3 = "";
                        JSONArray conditionsJSON;
                        JSONArray actionsJSON;
                        String actionString = "";
                        String actionString1 = "";
                        String actionString2 = "";

                        switch (serviceTrigger) {
                            case "1":
                                Trigger = "Gmail";
                                conditionsJSON = new JSONArray(conditions);
                                for (int j=0; j<conditionsJSON.length(); j++) {
                                    JSONObject cond = conditionsJSON.getJSONObject(j);
                                    eventConditions = cond.getString("condition");
                                    String event = cond.getString("event");
                                    switch (event) {
                                    case "1":
                                        conditionString1 = "Contains " + eventConditions + " in Object";
                                        break;
                                    case "2":
                                        conditionString2 = "Receive from " + eventConditions;
                                        break;
                                    case "3":
                                        conditionString3 = "Receive in time " + eventConditions;
                                        break;
                                    }
                                }
                                conditionString = conditionString1 + "\n" + conditionString2 + "\n" + conditionString3;
                                break;
                            case "3":
                                Trigger = "Epitech";
                                conditionsJSON = new JSONArray(conditions);
                                for (int j=0; j<conditionsJSON.length(); j++) {
                                    JSONObject cond = conditionsJSON.getJSONObject(j);
                                    String event = cond.getString("event");
                                    switch (event) {
                                        case "1":
                                            conditionString1 = "New meeting";
                                            break;
                                        case "2":
                                            conditionString2 = "New alert";
                                            break;
                                        case "3":
                                            conditionString3 = "New missing";
                                            break;
                                    }
                                }
                                conditionString = conditionString1 + "\n" + conditionString2 + "\n" + conditionString3;
                                break;
                            case "4":
                                Trigger = "Github";
                                conditionsJSON = new JSONArray(conditions);
                                for (int j=0; j<conditionsJSON.length(); j++) {
                                    JSONObject cond = conditionsJSON.getJSONObject(j);
                                    eventConditions = cond.getString("condition");
                                    String event = cond.getString("event");
                                    switch (event) {
                                        case "1":
                                            conditionString1 = "New project";
                                            break;
                                        case "2":
                                            conditionString2 = "New commit " + eventConditions;
                                            break;
                                        case "3":
                                            conditionString3 = "New pull request " + eventConditions;
                                            break;
                                    }
                                }
                                conditionString = conditionString1 + "\n" + conditionString2 + "\n" + conditionString3;
                                break;
                            case "5":
                                Trigger = "Trello";
                                conditionString = "Check activities on list";
                                break;
                            case "6":
                                Trigger = "Gitlab";
                                conditionString = "New project";
                                break;
                            case "7":
                                Trigger = "Agenda";
                                conditionsJSON = new JSONArray(conditions);
                                for (int j=0; j<conditionsJSON.length(); j++) {
                                    JSONObject cond = conditionsJSON.getJSONObject(j);
                                    eventConditions = cond.getString("condition");
                                    String event = cond.getString("event");
                                    switch (event) {
                                        case "1":
                                            conditionString1 = "Days until: " + eventConditions;
                                            break;
                                        case "2":
                                            conditionString2 = "Every: " + eventConditions;
                                            break;
                                    }
                                }
                                conditionString = conditionString1 + "\n" + conditionString2;
                                break;
                            default:
                                Trigger = "";
                        }
                        switch (serviceAction) {
                            case "1":
                                Action = "Gmail";
                                actionString = "Send mail";
                                break;
                            case "2":
                                Action = "Google Drive";
                                actionString = "Share file";
                                break;
                            case "4":
                                Action = "Github";
                                actionsJSON = new JSONArray(actions);
                                for (int j=0; j<actionsJSON.length(); j++) {
                                    JSONObject cond = actionsJSON.getJSONObject(j);
                                    String event = cond.getString("event");
                                    switch (event) {
                                        case "1":
                                            actionString1 = "Pull request";
                                            break;
                                        case "2":
                                            actionString2 = "Create branch";
                                            break;
                                    }
                                }
                                actionString = actionString1 + "\n" + actionString2;
                                break;
                            case "5":
                                Action = "Trello";
                                actionString = "Create list";
                                break;
                            case "6":
                                Action = "Gitlab";
                                actionString = "Create branch";
                                break;
                            default:
                                Action = "";
                        }

                        Card card = new Card(Trigger, conditionString, Action, actionString, id, token);
                        cards.add(card);
                    }
                    displayCards();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        FloatingActionButton mainBtn = findViewById(R.id.addBtn);
        mainBtn.setOnClickListener(v -> {
            Intent intent = new Intent(HomePage.this, AddAREA.class);
            intent.putExtra("json", userInfos);
            intent.putExtra("trigger", TriggerJSON);
            intent.putExtra("action", ActionJSON);
            startActivity(intent);
        });

        Button refresh = findViewById(R.id.refresh);
        refresh.setOnClickListener(v -> {
            Intent intent = new Intent(HomePage.this, HomePage.class);
            intent.putExtra("json", userInfos);
            startActivity(intent);
        });

        Button logout = findViewById(R.id.logout);
        logout.setOnClickListener(v -> {
            Intent intent = new Intent(HomePage.this, Login.class);
            startActivity(intent);
        });
    }

    protected void displayCards() {
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        CardAdaptater adapter = new CardAdaptater(cards);
        runOnUiThread(() -> {
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
        });
    }
}