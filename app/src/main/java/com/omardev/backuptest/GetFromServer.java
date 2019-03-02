package com.omardev.backuptest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

public class GetFromServer extends AppCompatActivity {

    String PHP_URL = "http://192.168.1.85/phpTuto/PHPOOP/test.php";
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_from_server);

        listView = findViewById(R.id.listView);
        //new AsynTask(this,listView).execute(PHP_URL);

    }
}
