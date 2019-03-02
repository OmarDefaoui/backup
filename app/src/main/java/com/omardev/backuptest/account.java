package com.omardev.backuptest;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class account extends AppCompatActivity {

    TextInputLayout et_username, et_email, et_password;
    Button btn_create_account, btn_sign_out;
    TextView tv_login, tv_info;
    LinearLayout ll_create_signin, ll_info_account;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        et_username = findViewById(R.id.et_username);
        et_email = findViewById(R.id.et_email);
        et_password = findViewById(R.id.et_password);
        btn_create_account = findViewById(R.id.btn_create_account);
        tv_login = findViewById(R.id.tv_login);
        btn_sign_out = findViewById(R.id.btn_sign_out);
        tv_info = findViewById(R.id.tv_info);
        ll_info_account = findViewById(R.id.ll_info_account);
        ll_create_signin = findViewById(R.id.ll_create_signin);

        updateUsername();

        btn_create_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (btn_create_account.getText().toString().trim()) {
                    case "create account":
                        check_insertion_to_create_account();
                        break;
                    case "sign in":
                        check_insertion_to_login();
                        break;
                }
            }
        });

        tv_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (btn_create_account.getText().toString().trim()) {
                    case "create account":
                        et_username.setVisibility(View.GONE);
                        btn_create_account.setText("sign in");
                        tv_login.setText("create an account");
                        break;
                    case "sign in":
                        et_username.setVisibility(View.VISIBLE);
                        btn_create_account.setText("create account");
                        tv_login.setText("have you an account ? Sign in");
                        break;
                }
            }
        });

        btn_sign_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences preferences = getSharedPreferences("account", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("username", "no");
                editor.apply();
                if (preferences.getString("username", "no").equals("no")) {
                    ll_info_account.setVisibility(View.GONE);
                    ll_create_signin.setVisibility(View.VISIBLE);
                } else
                    Toast.makeText(account.this, "Faild to sign out", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void updateUsername() {
        SharedPreferences preferences = getSharedPreferences("account", Context.MODE_PRIVATE);
        String username = preferences.getString("username", "no");
        if (!username.equals("no")) {
            ll_create_signin.setVisibility(View.GONE);
            ll_info_account.setVisibility(View.VISIBLE);
            tv_info.setText("Account informations: \n\nUsername: " + username + "\nEmail: " +
                    preferences.getString("email", "no"));
        }
    }

    private void check_insertion_to_login() {
        String email, password;
        email = et_email.getEditText().getText().toString().trim();
        password = et_password.getEditText().getText().toString().trim();

        if (!TextUtils.isEmpty(email) &&
                !TextUtils.isEmpty(password)) {

            if (email.length() >= 8 &&
                    email.contains("@") &&
                    email.contains(".") &&
                    password.length() >= 8) {

                signin(email, password);
            } else {
                if (email.length() < 8 || !email.contains("@") || !email.contains("."))
                    et_email.setError("email incorrect");
                else et_email.setError(null);
                if (password.length() < 8)
                    et_password.setError("min 8 char");
                else et_password.setError(null);
            }


        } else {
            if (TextUtils.isEmpty(email))
                et_email.setError("required");
            else et_email.setError(null);
            if (TextUtils.isEmpty(password))
                et_password.setError("required");
            else et_password.setError(null);
        }
    }

    private void signin(String email, String password) {
        ArrayList<String> login = new ArrayList<>();
        login.add(email);
        login.add(password);

        String url = "http://192.168.1.85/phpTuto/PHPOOP/backupAndroidWebService/login.php";
        new AsynTaskPost("login", this, login).execute(url);
    }

    private void check_insertion_to_create_account() {
        String username, email, password;
        username = et_username.getEditText().getText().toString().trim();
        email = et_email.getEditText().getText().toString().trim();
        password = et_password.getEditText().getText().toString().trim();

        if (!TextUtils.isEmpty(username) &&
                !TextUtils.isEmpty(email) &&
                !TextUtils.isEmpty(password)) {

            if (username.length() >= 4 &&
                    email.length() >= 8 &&
                    email.contains("@") &&
                    email.contains(".") &&
                    password.length() >= 8) {

                createAccount(username, email, password);
            } else {
                if (username.length() < 4)
                    et_username.setError("min 4");
                else et_username.setError(null);
                if (email.length() < 8 || !email.contains("@") || !email.contains("."))
                    et_email.setError("email incorrect");
                else et_email.setError(null);
                if (password.length() < 8)
                    et_password.setError("min 8 char");
                else et_password.setError(null);
            }

        } else {
            if (TextUtils.isEmpty(username))
                et_username.setError("required");
            else et_username.setError(null);
            if (TextUtils.isEmpty(email))
                et_email.setError("required");
            else et_email.setError(null);
            if (TextUtils.isEmpty(password))
                et_password.setError("required");
            else et_password.setError(null);
        }
    }

    private void createAccount(String username, String email, String password) {
        ArrayList<String> createAccount = new ArrayList<>();
        createAccount.add(username);
        createAccount.add(email);
        createAccount.add(password);

        String url = "http://192.168.1.85/phpTuto/PHPOOP/backupAndroidWebService/createAccount.php";
        new AsynTaskPost("createAccount", createAccount, this).execute(url);
    }

    @Override
    public void onBackPressed() {
        if (!getSharedPreferences("account", Context.MODE_PRIVATE).getString("username", "no").equals("no")) {
            finish();
            startActivity(new Intent(this, ContentActivity.class));
        } else
            super.onBackPressed();
    }
}
