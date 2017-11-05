package com.example.csaikia.cse535project;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final EditText userName = (EditText) findViewById(R.id.user);
        Button registerButton = (Button) findViewById(R.id.register);
        registerButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("chaynika", "Register clicked");
                String user = (userName.getText().toString());
                if (!checkUser(user)) {
                    Toast.makeText(MainActivity.this, "Enter Name", Toast.LENGTH_LONG).show();
                } else {

                }
            }
        });

        Button unlockButton = (Button) findViewById(R.id.unlock);
        unlockButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("chaynika", "Unlock clicked");
                String user = (userName.getText().toString());
                if (!checkUser(user)) {
                    Toast.makeText(MainActivity.this, "Enter Name", Toast.LENGTH_LONG).show();
                } else {

                }
            }
        });

    }

    private boolean checkUser(String user) {
        if (user.length() == 0) {
            return false;
        }
        return true;
    }
}
