package com.example.area.login_register;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import com.example.area.R;
import com.example.area.Services.Services_action;
import com.example.area.actions.Github_action;
import com.example.area.home.HomePage;

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
import okhttp3.ResponseBody;

public class Login extends AppCompatActivity {

    OkHttpClient client = new OkHttpClient();
    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

    public static final String USER_AGENT = "Mozilla/5.0 (Linux; Android 4.1.1; Galaxy Nexus Build/JRO03C) AppleWebKit/535.19 (KHTML, like Gecko) Chrome/18.0.1025.166 Mobile Safari/535.19";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        ImageButton google_login = findViewById(R.id.google_btn);
        google_login.setOnClickListener(v -> {

            // http://localhost:8080/api/user/getGoogle
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

                                        //localhost:8080/api/user/authgoogle
                                        // new request for get the token
                                        // code: jksrfggrk  <- in JSON
                                        JSONObject userInfos = new JSONObject();
                                        try {
                                            userInfos.put("code", splitUrl(urlNow));
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }

                                        RequestBody body = RequestBody.create(JSON, userInfos.toString());

                                        Request request = new Request.Builder()
                                                .url("http://10.0.2.2:8080/api/user/authgoogle")
                                                .post(body)
                                                .build();

                                        client.newCall(request).enqueue(new Callback() {
                                            @Override
                                            public void onFailure(@NotNull Call call, @NotNull IOException e) {

                                            }

                                            @Override
                                            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                                                String responseBodyString = response.body().string();
                                                Intent intent = new Intent(Login.this, HomePage.class);
                                                intent.putExtra("json", responseBodyString);
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
        });

        Button login = findViewById(R.id.login_btn);
        login.setOnClickListener(v -> {
            try {
                LogIn();
            } catch (IOException e) {
                Log.e("ERR ", "POST SingUp Request FAILED **********************************************");
                e.printStackTrace();
            }
        });

        Button login_to_register = findViewById(R.id.login_to_register_btn);
        login_to_register.setOnClickListener(v -> {
            Intent intent = new Intent(Login.this, Register.class);
            startActivity(intent);
        });
    }

    public void LogIn() throws IOException{
        EditText eMail = findViewById(R.id.e_mail);
        EditText password = findViewById(R.id.pswd);

        if (!eMail.getText().toString().equals("") || !password.getText().toString().equals("")) {
            JSONObject userInfos = new JSONObject();
            try {
                userInfos.put("email", eMail.getText().toString());
                userInfos.put("password", password.getText().toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            RequestBody body = RequestBody.create(JSON, userInfos.toString());

            Request request = new Request.Builder()
                    .url("http://10.0.2.2:8080/api/user/login")
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
                        String responseBodyString = response.body().string();
                        Intent intent = new Intent(Login.this, HomePage.class);
                        intent.putExtra("json", responseBodyString);
                        startActivity(intent);
                    }
                }
            });
        }
    }

    private String splitUrl(String url) {
        String[] outerSplit = url.split("=");
        String accessToken = null;

        accessToken = outerSplit[1];
        accessToken = accessToken.split("&")[0];
        Log.e("TEST", accessToken);

        return accessToken;
    }
}
