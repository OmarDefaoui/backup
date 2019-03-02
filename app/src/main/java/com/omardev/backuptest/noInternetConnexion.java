package com.omardev.backuptest;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class noInternetConnexion extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_internet_connexion);

        Button buttonRetry = findViewById(R.id.buttonRetry);
        buttonRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkInternetConnexion connexion = new checkInternetConnexion(noInternetConnexion.this);
                if (connexion.isConnectedToInternet()) {
                    startActivity(new Intent(getApplicationContext(), ContentActivity.class));
                    finish();
                }else
                    Toast.makeText(noInternetConnexion.this, "Aucune Connexion !", Toast.LENGTH_SHORT).show();
            }
        });

    }
}
