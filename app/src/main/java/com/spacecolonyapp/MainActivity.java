package com.spacecolonyapp;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loadFragment(new QuartersFragment());

        findViewById(R.id.navQuarters).setOnClickListener(v ->
                loadFragment(new QuartersFragment()));

        findViewById(R.id.navSimulator).setOnClickListener(v ->
                loadFragment(new SimulatorFragment()));

        findViewById(R.id.navMission).setOnClickListener(v ->
                loadFragment(new MissionFragment()));
    }

    public void loadFragment(Fragment f) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, f)
                .commit();
    }
}