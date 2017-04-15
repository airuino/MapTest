package com.maptest.com.maptest;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class RecommendationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommendation);


        //SetUp tabs
        SetUpTaps();

    }



    // Tabs action listener
    public void SetUpTaps()
    {

        Button Hactvity=(Button)findViewById(R.id.button3);
        Button Mactivity= (Button)findViewById(R.id.button4);
        Button VLactivity=(Button)findViewById(R.id.button2);
        Button Ractivity=(Button)findViewById(R.id.button);

        //Home Tab
        Hactvity.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(i);
                finish();

            }
        });

        //Map tab
        Mactivity.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), MapsActivity.class);
                startActivity(i);
                finish();

            }
        });

        //Visited Location List tab
        VLactivity.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), MapsActivity.class);
                startActivity(i);
                finish();

            }
        });


        //Recommandition tab
        Ractivity.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), RecommendationActivity.class);
                startActivity(i);
                finish();

            }
        });

    }


}