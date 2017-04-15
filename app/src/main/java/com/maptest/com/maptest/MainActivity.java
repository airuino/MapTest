package com.maptest.com.maptest;

/*import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
http://stackoverflow.com/questions/14475059/custom-infowindow-in-google-map-android-v2*/

/*premkumarnew80@gmail.com*/

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;


public class  MainActivity extends AppCompatActivity{


    int value ;
    static String[] Pollutant_ID ,Sensor_ID,Pollutant_Value;
    TextView status;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //Set up Tabs
        SetUpTaps();

        //
        status = (TextView)findViewById(R.id.textView2);

        new pmValue().execute();

    }


    class pmValue extends AsyncTask<String,String,String>
    {

        public pmValue() {
            super();
        }

        protected String doInBackground(String... params) {

            List<NameValuePair> list= new ArrayList<NameValuePair>();
            JSONObject jsonObject=JSONParser.makeHttpRequest("http://airuino.com/airapp/poll_value.php","POST",list);
            try {
                if(jsonObject!=null){
                    value=jsonObject.getInt("value1");
                    JSONArray jsonArray=jsonObject.getJSONArray("Pollutant_Value"); //To Get the Values
                    Pollutant_ID = new String[jsonArray.length()];
                    Sensor_ID = new String[jsonArray.length()];
                    Pollutant_Value = new String[jsonArray.length()];

                    for(int i=0;i<jsonArray.length();i++){
                        JSONObject objcet=jsonArray.getJSONObject(i);
                        Pollutant_ID[i] = objcet.getString("Pollutant_ID");
                        Sensor_ID[i]=objcet.getString("Sensor_ID");
                        Pollutant_Value[i] = objcet.getString("Pollutant_Value");
                    }

                }else{
                    value=0;
                }

            }catch (Exception e){
                Log.d("ERROR", e.getMessage());

            }


            return null;
        }


        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            ArrayList<Float> values1 = new ArrayList<Float>();
            if (value ==1) {
                float sum =0;
                status.setTextColor(Color.RED);
                //int id = Integer.parseInt(Pollutant_ID[0]);

            } else{
                Toast.makeText(getApplicationContext(),"Data retrieval failure...", Toast.LENGTH_LONG).show();
            }
        }

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