package org.techtown.ansehen;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by bit on 2017-09-07.
 */

public class PopTriger extends AppCompatActivity {
    private static String TAG = "phptest_MainActivity";
    String password;
    String phonenumber;
    String primaryKey;
    //String PN="01064078205";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        primaryKey=intent.getExtras().getString("primaryKey");
        password=intent.getExtras().getString("password");
        phonenumber=intent.getExtras().getString("phonenumber");

        //mTextViewResult = (TextView)findViewById(R.id.textView_main_result);
        //mlistView = (ListView) findViewById(R.id.listView_main_list);

        GetData task = new GetData();
        task.execute("http://13.124.164.203/android_test.php");
    }


    private class GetData extends AsyncTask<String, Void, String>{
        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(PopTriger.this,
                    "Please Wait", null, true, true);
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();
            Log.d(TAG, "response  - " + result);
            //String temp=result.substring(indexOf,1);
            Log.i("P1","");
            int indexOf=result.indexOf("result");
            String return_result = result.substring(indexOf+10,indexOf+11);

            //result 반환값 설정 0(진행중) 1(이상없음) 오류(2)
            if(return_result.equals("0")) {
                startActivity(new Intent(PopTriger.this, Pop.class));
            }
            Log.i("반환된 result값",""+return_result);
        }


        @Override
        protected String doInBackground(String... params) {

            String serverURL = params[0];
            String uniqueKey = primaryKey;
            String postParameters = "unique_key=" + uniqueKey;
            try {

                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();


                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoInput(true);
                httpURLConnection.connect();

                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(postParameters.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();

                int responseStatusCode = httpURLConnection.getResponseCode();
                Log.d(TAG, "response code - " + responseStatusCode);

                InputStream inputStream;
                if(responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                }
                else{
                    inputStream = httpURLConnection.getErrorStream();
                }


                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line;

                while((line = bufferedReader.readLine()) != null){
                    sb.append(line);
                }


                bufferedReader.close();


                return sb.toString().trim();


            } catch (Exception e) {

                Log.d(TAG, "InsertData: Error ", e);
                errorString = e.toString();

                return null;
            }
        }
    }
}
