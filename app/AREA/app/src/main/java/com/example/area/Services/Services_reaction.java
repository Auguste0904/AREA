package com.example.area.Services;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.example.area.R;
import com.example.area.actions.Github_action;
import com.example.area.actions.Gitlab_action;
import com.example.area.actions.Trello_action;
import com.example.area.reactions.Github_reaction;
import com.example.area.reactions.Gitlab_reaction;
import com.example.area.reactions.Gmail_reaction;
import com.example.area.reactions.Google_drive_reaction;
import com.example.area.reactions.Trello_reaction;

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

public class Services_reaction extends AppCompatActivity {

    OkHttpClient client = new OkHttpClient();
    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

    public static final String USER_AGENT = "Mozilla/5.0 (Linux; Android 4.1.1; Galaxy Nexus Build/JRO03C) AppleWebKit/535.19 (KHTML, like Gecko) Chrome/18.0.1025.166 Mobile Safari/535.19";

    String userInfos;
    String token;
    String url;
    String TriggerJSON;
    String ActionJSON;

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.services_reaction);

        Intent prevInt = getIntent();
        userInfos = prevInt.getStringExtra("json");
        TriggerJSON = prevInt.getStringExtra("trigger");
        ActionJSON = prevInt.getStringExtra("action");

        try {
            JSONObject obj = new JSONObject(userInfos);
            token = obj.getString("token");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ImageButton githubBtn = findViewById(R.id.github);
        githubBtn.setOnClickListener(v -> {
            GitAuth();
        });

        ImageButton gmailBtn = findViewById(R.id.gmail);
        gmailBtn.setOnClickListener(v -> {
            GoogleAuth("gmail");
        });

        ImageButton driveBtn = findViewById(R.id.google_drive);
        driveBtn.setOnClickListener(v -> {
            GoogleAuth("drive");
        });

        ImageButton gitlabBtn = findViewById(R.id.gitlab);
        gitlabBtn.setOnClickListener(v -> {
            GitlabAuth();
        });

        ImageButton trelloBtn = findViewById(R.id.trello);
        trelloBtn.setOnClickListener(v -> {
            TrelloAuth();
        });

        ImageButton backBtn = findViewById(R.id.back);
        backBtn.setOnClickListener(v -> {onBackPressed();});
    }

    private void GitAuth() {
        Request request = new Request.Builder()
                .url("http://10.0.2.2:8080/api/connexion/getGithub")
                .get()
                .addHeader("Authorization", "Bearer " + token)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.e("TEST", "ERROR: failed to get github url");
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                url = response.body().string();
                url = url.replace("\"", "");

                // ----------------------------------------------
                runOnUiThread(() -> {
                    setContentView(R.layout.web_view);
                    WebView auth  = findViewById(R.id.web);

                    auth.loadUrl(url);
                    auth.getSettings().setJavaScriptEnabled(true);

                    auth.setWebViewClient(new WebViewClient() {
                        private static final String REDIRECT_URL = "http://localhost:8081";

                        @Override
                        public boolean shouldOverrideUrlLoading(WebView view, String urlNow) {

                            if (urlNow.contains(REDIRECT_URL)) {
                                Log.e("TEST", urlNow);
                                splitUrl(urlNow); // Permet de récupérer le token dans l'url. Pour l'intant je ne fait rien de ce token
                                Intent intent = new Intent(Services_reaction.this, Github_reaction.class);
                                intent.putExtra("json", userInfos);
                                intent.putExtra("trigger", TriggerJSON);
                                intent.putExtra("action", ActionJSON);
                                startActivity(intent);
                            } else {
                                view.loadUrl(urlNow);
                            }
                            return true;
                        }
                    });
                });
            }
        });
    }

    private void GitlabAuth() {
        Request request = new Request.Builder()
                .url("http://10.0.2.2:8080/api/connexion/getGitlab")
                .get()
                .addHeader("Authorization", "Bearer " + token)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.e("TEST", "ERROR: failed to get gitlab url");
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                url = response.body().string();
                url = url.replace("\"", "");

                // ------------------------------------------

                runOnUiThread(() -> {
                    setContentView(R.layout.web_view);
                    WebView auth  = findViewById(R.id.web);

                    auth.getSettings().setUserAgentString(USER_AGENT);
                    auth.getSettings().setJavaScriptEnabled(true);
                    auth.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
                    auth.loadUrl(url);

                    auth.setWebViewClient(new WebViewClient() {
                        private static final String REDIRECT_URL = "http://localhost:8081";

                        @Override
                        public boolean shouldOverrideUrlLoading(WebView view, String urlNow) {

                            if (urlNow.contains(REDIRECT_URL)) {
                                Log.e("TEST", urlNow);
                                splitUrl(urlNow); // Permet de récupérer le token dans l'url. Pour l'intant je ne fait rien de ce token
                                Intent intent = new Intent(Services_reaction.this, Gitlab_reaction.class);
                                intent.putExtra("json", userInfos);
                                intent.putExtra("trigger", TriggerJSON);
                                intent.putExtra("action", ActionJSON);
                                startActivity(intent);
                            } else {
                                view.loadUrl(urlNow);
                            }
                            return true;
                        }
                    });
                });
            }
        });
    }

    private void GoogleAuth(String redirect){
        Request request = new Request.Builder()
                .url("http://10.0.2.2:8080/api/user/getGoogle")
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                runOnUiThread(() -> {
                    setContentView(R.layout.web_view);
                    WebView auth  = findViewById(R.id.web);
                    auth.getSettings().setUserAgentString(USER_AGENT);
                    auth.getSettings().setJavaScriptEnabled(true);
                    auth.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);

                    try {

                        auth.setWebViewClient(new WebViewClient() {
                            private static final String REDIRECT_URL = "http://localhost:8081";

                            @Override
                            public boolean shouldOverrideUrlLoading(WebView view, String urlNow) {

                                if (urlNow.contains(REDIRECT_URL)) {
                                    splitUrl(urlNow); // Permet de récupérer le token dans l'url. Pour l'intant je ne fait rien de ce token

                                    JSONObject userInfos2 = new JSONObject();
                                    try {
                                        userInfos2.put("code", splitUrl(urlNow));
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                    RequestBody body = RequestBody.create(JSON, userInfos2.toString());

                                    Request request = new Request.Builder()
                                            .url("http://10.0.2.2:8080/api/user/loginGoogle")
                                            .addHeader("Authorization", "Bearer " + token)
                                            .post(body)
                                            .build();

                                    client.newCall(request).enqueue(new Callback() {
                                        @Override
                                        public void onFailure(@NotNull Call call, @NotNull IOException e) {

                                        }

                                        @Override
                                        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                                            Intent intent = null;
                                            if (redirect.equals("gmail"))
                                                intent = new Intent(Services_reaction.this, Gmail_reaction.class);
                                            else if (redirect.equals("drive"))
                                                intent = new Intent(Services_reaction.this, Google_drive_reaction.class);
                                            assert intent != null;
                                            intent.putExtra("json", userInfos);
                                            intent.putExtra("trigger", TriggerJSON);
                                            intent.putExtra("action", ActionJSON);
                                            startActivity(intent);
                                        }
                                    });
                                } else {
                                    view.loadUrl(urlNow);
                                }
                                return true;
                            }
                        });
                        JSONObject obj = new JSONObject(response.body().string());
                        String url = obj.getString("url");
                        Log.e("TEST", url);
                        auth.loadUrl(url);
                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                    }
                });
            }
        });
    }

    private void TrelloAuth() {
        Request request = new Request.Builder()
                .url("http://10.0.2.2:8080/api/connexion/getTrello ")
                .get()
                .addHeader("Authorization", "Bearer " + token)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.e("TEST", "ERROR: failed to get Trello url");
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                url = response.body().string();
                url = url.replace("\"", "");

                // -------------------------------------------

                runOnUiThread(() -> {
                    setContentView(R.layout.web_view);
                    WebView auth  = findViewById(R.id.web);
                    auth.getSettings().setUserAgentString(USER_AGENT);
                    auth.getSettings().setJavaScriptEnabled(true);
                    auth.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
                    auth.loadUrl(url);

                    auth.setWebViewClient(new WebViewClient() {
                        private static final String REDIRECT_URL = "http://localhost:8081";

                        @Override
                        public boolean shouldOverrideUrlLoading(WebView view, String urlNow) {

                            if (urlNow.contains(REDIRECT_URL)) {
                                Log.e("TEST1", urlNow);
                                splitUrl(urlNow); // Permet de récupérer le token dans l'url. Pour l'intant je ne fait rien de ce token

                                JSONObject userInfos2 = new JSONObject();
                                try {
                                    userInfos2.put("code", splitUrl(urlNow));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                Log.e("TEST", userInfos2.toString());

                                RequestBody body = RequestBody.create(JSON, userInfos2.toString());

                                Request request = new Request.Builder()
                                        .url("http://10.0.2.2:8080/api/connexion/loginTrello")
                                        .addHeader("Authorization", "Bearer " + token)
                                        .post(body)
                                        .build();

                                client.newCall(request).enqueue(new Callback() {
                                    @Override
                                    public void onFailure(@NotNull Call call, @NotNull IOException e) {

                                    }

                                    @Override
                                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                                        Intent intent = new Intent(Services_reaction.this, Trello_reaction.class);
                                        intent.putExtra("json", userInfos);
                                        intent.putExtra("trigger", TriggerJSON);
                                        intent.putExtra("action", ActionJSON);
                                        startActivity(intent);
                                    }
                                });
                            } else {
                                view.loadUrl(urlNow);
                            }
                            return true;
                        }
                    });
                });
            }
        });
    }

    private String splitUrl(String url) {
        String[] outerSplit = url.split("=");
        String accessToken = null;

        accessToken = outerSplit[1];
        accessToken = accessToken.split("&")[0];

        return accessToken;
    }
}