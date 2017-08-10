package org.techtown.ansehen;

import android.util.Log;

import java.io.FileInputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class CCTVBeaconManager{
    private String[] Array_CctvId = new String [20];
    private int num;
    private String primaryKey;
    public void CCTVBeaconManager(){
        num=0;
    }
    public void transportCctv(String beaconTemp){
        Log.i("transport","-----------------------------------------------------------------");
        final String urlPath_register = "http://13.124.164.203/BeaconSearch.php";
        URL connectUrl =null;
        primaryKey=beaconTemp;

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
            buffer.append("beaconTemp").append("=").append(beaconTemp).append("&");
            //buffer.append("userInputPhoneNum").append("=").append(inputPhone).append("&");
            //buffer.append("userName").append("=").append(name).append("&");
            //buffer.append("userPw").append("=").append(pw).append("&");
            //buffer.append("fileName").append("=").append(filename).append("&");
            buffer.append("uniqueKey").append("=").append(primaryKey);

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
    public void AddPrimaryKey(String temp){
        primaryKey=temp;
    }
    public void addCctvId(String temp){
        Array_CctvId[num++]=temp;
    }
    public void compareCctvId(String temp){
        int i;
        for(i=0;i<num;i++){
            if(temp.equals(Array_CctvId[i])){
                Log.i("Equal Beacon","!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                return;
            }
        }
        Log.i("add CCTV","-------------------------------------------");
        this.addCctvId(temp);
        this.transportCctv(temp);
    }
}
