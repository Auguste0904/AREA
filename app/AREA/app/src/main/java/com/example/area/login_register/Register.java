package com.example.area.login_register;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import com.example.area.R;

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

public class Register extends AppCompatActivity {

    OkHttpClient client = new OkHttpClient();
    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        Button register = findViewById(R.id.register_btn);
        register.setOnClickListener(v -> {
            try {
                SingUp();
            } catch (IOException e) {
                Log.e("ERR ", "POST SingUp Request FAILED **********************************************");
                e.printStackTrace();
            }
        });

        Button register_to_login = findViewById(R.id.register_to_login_btn);
        register_to_login.setOnClickListener(v -> {
            Intent intent = new Intent(Register.this, Login.class);
            startActivity(intent);
        });
    }

    // Docu pour le http://10.0.2.2:8080 (localhost)
    // Docu android : https://developer.android.com/studio/run/emulator-networking.html
    // Recherche StackOverFlow : https://stackoverflow.com/questions/5806220/how-to-connect-to-my-http-localhost-web-server-from-android-emulator
    public void SingUp() throws IOException{
        EditText firstName = findViewById(R.id.fst_name);
        EditText lastName = findViewById(R.id.lst_name);
        EditText eMail = findViewById(R.id.e_mail);
        EditText password = findViewById(R.id.pswd);

        if (!firstName.getText().toString().equals("") || !lastName.getText().toString().equals("")
        || !eMail.getText().toString().equals("") || !password.getText().toString().equals("")) {
            JSONObject userInfos = new JSONObject();
            try {
                userInfos.put("firstname", firstName.getText().toString());
                userInfos.put("lastname", lastName.getText().toString());
                userInfos.put("email", eMail.getText().toString());
                userInfos.put("password", password.getText().toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            RequestBody body = RequestBody.create(JSON, userInfos.toString());

            Request request = new Request.Builder()
                    .url("http://10.0.2.2:8080/api/user/signup")
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
                    }
                }
            });
        }
    }
}