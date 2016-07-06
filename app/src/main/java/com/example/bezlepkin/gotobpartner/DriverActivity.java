package com.example.bezlepkin.gotobpartner;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Teacher on 02.07.2016.
 */
public class DriverActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.driver_activity);

        if(savedInstanceState == null){
            getFragmentManager().beginTransaction()
                    .add(R.id.driver_activity_container, new DriverFragment())
                    .commit();
        }

    }
}
