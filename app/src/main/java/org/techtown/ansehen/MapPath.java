package org.techtown.ansehen;

import android.util.Log;

import com.skp.Tmap.TMapPoint;

import java.io.FileInputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by bit on 2017-08-28.
 */

public class MapPath {
    private String primaryKey;
    private ArrayList<TMapPoint> map_Path;
    private String end_p;
    private int size_num;
    private String allPath = new String();
    public void addPrimaryKey(String temp){
        primaryKey=temp;
    }
    public void addEndPoint(TMapPoint temp){end_p=(temp.getLongitude()+","+temp.getLatitude());}
    public void addPoint(ArrayList<TMapPoint> temp){
        map_Path=temp;
    }
    public void addSizeNum(){
        size_num=map_Path.size();
    }
    public void setAllPath(){
        Log.i("SetAllPath","Start: -----------------------------");
        for(int i=0;i<size_num;i++){
            if(i==size_num-1){
                allPath=allPath.concat(""+map_Path.get(i).getLatitude()+","+map_Path.get(i).getLongitude());
                break;
            }
            allPath=allPath.concat(""+map_Path.get(i).getLatitude()+","+map_Path.get(i).getLongitude()+"##");
        }
        Log.i("SetAllPath","End: ------------------------------");
    }
    public void allPathTransport(){
        Log.i("transport","-----------------------------------------------------------------");
        final String urlPath_register = "http://13.124.164.203/Cctv_Location.php";
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
            buffer.append("uniqueKey").append("=").append(primaryKey).append("&");
            buffer.append("endPoint").append("=").append(end_p).append("&");
            //buffer.append("sizeNum").append("=").append(size_num).append("&");
            //buffer.append("userPw").append("=").append(pw).append("&");
            //buffer.append("fileName").append("=").append(filename).append("&");
            buffer.append("allPath").append("=").append(allPath);
            Log.i("transport","~ing-------------------------------");
            Log.i("test: ",""+buffer);
            Log.i("allPath: ",allPath);

            OutputStreamWriter osw = new OutputStreamWriter(conn.getOutputStream());
            PrintWriter wr = new PrintWriter(osw);
            wr.write(buffer.toString());
            wr.flush();
            Log.i("*","--------------------------------------------");
            String serverResponseMessage = conn.getResponseMessage();
            Log.i("BeaconId", "HTTP Response is : "
                    + serverResponseMessage + ": " + serverResponseCode);

            fileInputStream.close();
            Log.i("**","--------------------------------------------");
        } catch (MalformedURLException ex) {
            Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
            Log.e("aaa","aaa");
        } catch (Exception e) {
            Log.d("Test", "exception " + e.getMessage());
            Log.e("bbb","bbb");
        }
        Log.i("transport end","-----------------------------------------------------------------");
    }
}
