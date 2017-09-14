package org.techtown.ansehen;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.io.FileInputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by bit on 2017-09-11.
 */

public class endActivity extends AppCompatActivity {
    private String primaryKey;
    public void changeState(String s_temp){
        Log.i("transport","-----------------------------------------------------------------");
        final String urlPath_register = "http://13.124.164.203/StateChange.php";
        URL connectUrl =null;

        FileInputStream fileInputStream =null;
        int serverResponseCode = 0;
        try
        {
            connectUrl=new URL(urlPath_register);
            HttpURLConnection conn = (HttpURLConnection) connectUrl.openConnection();
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("content-type", "application/x-www-form-urlencoded");


            StringBuffer buffer = new StringBuffer();
            buffer.append("state").append("=").append(s_temp).append("&");
            //buffer.append("result").append("=").append(inputPhone).append("&");
            //buffer.append("userName").append("=").append(name).append("&");
            //buffer.append("userPw").append("=").append(pw).append("&");
            //buffer.append("fileName").append("=").append(filename).append("&");
            buffer.append("uniqueKey").append("=").append(primaryKey);
            Log.i("beaconTemp,stateTemp",s_temp+","+primaryKey);

            OutputStreamWriter osw = new OutputStreamWriter(conn.getOutputStream());
            PrintWriter wr = new PrintWriter(osw);
            wr.write(buffer.toString());
            wr.flush();

            String serverResponseMessage = conn.getResponseMessage();
            Log.i("BeaconId", "HTTP Response is : "
                    + serverResponseMessage + ": " + serverResponseCode);

            fileInputStream.close();
        } catch (MalformedURLException ex) {
            Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
        } catch (Exception e) {
            Log.d("Test", "exception " + e.getMessage());
        }
        Log.i("transport end","-----------------------------------------------------------------");
    }
    public void dataClear(String temp_key){
        Log.i("end_t start","-----------------------------------------------------------------");
        final String urlPath_register = "http://13.124.164.203/server_clear.php";
        URL connectUrl =null;
        FileInputStream fileInputStream =null;
        int serverResponseCode = 0;
        try
        {
            connectUrl=new URL(urlPath_register);
            HttpURLConnection conn = (HttpURLConnection) connectUrl.openConnection();
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("content-type", "application/x-www-form-urlencoded");


            StringBuffer buffer = new StringBuffer();
            buffer.append("uniqueKey").append("=").append(temp_key);

            OutputStreamWriter osw = new OutputStreamWriter(conn.getOutputStream());
            PrintWriter wr = new PrintWriter(osw);
            wr.write(buffer.toString());
            wr.flush();
            String serverResponseMessage = conn.getResponseMessage();
            Log.i("uploadFile", "HTTP Response is : "
                    + serverResponseMessage + ": " + serverResponseCode);

            fileInputStream.close();
        } catch (MalformedURLException ex) {
            Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
        } catch (Exception e) {
            Log.d("Test", "exception " + e.getMessage());
        }
        Log.i("end_t end","-----------------------------------------------------------------");
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end);

        Intent intent = new Intent(this.getIntent());
        primaryKey=intent.getExtras().getString("primaryKey");
        Log.i("end P1",""+primaryKey);
        new Thread(new Runnable() {

            public void run() {

                runOnUiThread(new Runnable() {

                    public void run() {
                    }
                });
                dataClear(primaryKey);
                changeState("result");
            }
        }).start();
        TextView exit = (TextView) findViewById(R.id.exitButton);
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityCompat.finishAffinity(endActivity.this);
                System.runFinalization();
                System.exit(0);
            }
        });
    }
}
