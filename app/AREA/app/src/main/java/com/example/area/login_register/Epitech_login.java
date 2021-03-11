package com.example.area.login_register;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.example.area.R;
import com.example.area.actions.Epitech_action;

public class Epitech_login extends AppCompatActivity {
    String userInfos;

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_epitech);

        EditText mailEpitech = findViewById(R.id.email_epitech);
        EditText autologinLink = findViewById(R.id.autoLogin);

        Intent prevInt = getIntent();
        userInfos = prevInt.getStringExtra("json");

        ImageButton backBtn = findViewById(R.id.back);
        backBtn.setOnClickListener(v -> onBackPressed());

        Button valid = findViewById(R.id.valid_btn);
        valid.setOnClickListener(v -> {
            Intent intent = new Intent(Epitech_login.this, Epitech_action.class);
            intent.putExtra("json", userInfos);

            intent.putExtra("mailEpitech", mailEpitech.getText().toString());
            intent.putExtra("autologinLink", autologinLink.getText().toString());

            startActivity(intent);
        });
    }
}
