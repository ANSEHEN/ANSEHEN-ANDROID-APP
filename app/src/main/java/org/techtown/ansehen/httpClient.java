package org.techtown.ansehen;

/**
 * Created by bit on 2017-07-06.
 */

import android.util.Log;

import java.io.FileInputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by bit on 2017-07-05.
 */
public class httpClient {
    private static final String urlPath = "http://13.124.164.203/register.php";
    private FileInputStream fileInputStream =null;
    private URL connectUrl =null;
    int serverResponseCode = 0;
    String lineEnd = "\r\n";
    String twoHyphens = "--";
    String boundary ="*****";
    public void HttpFileUpload(String name, String pw,String phoneNum, String inputPhone)
    {
        try
        {
            connectUrl=new URL(urlPath);
            HttpURLConnection conn = (HttpURLConnection) connectUrl.openConnection();
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("content-type", "application/x-www-form-urlencoded");


            StringBuffer buffer = new StringBuffer();
            buffer.append("userPhoneNum").append("=").append(phoneNum).append("&");
            buffer.append("userInputPhoneNum").append("=").append(inputPhone).append("&");
            buffer.append("userName").append("=").append(name).append("&");
            buffer.append("userPw").append("=").append(pw);

            // write data
            // DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
            // serverResponseCode = conn.getResponseCode();
            OutputStreamWriter osw = new OutputStreamWriter(conn.getOutputStream());
            PrintWriter wr = new PrintWriter(osw);
            wr.write(buffer.toString());
            wr.flush();


            String serverResponseMessage = conn.getResponseMessage();
            Log.i("uploadFile", "HTTP Response is : "
                    + serverResponseMessage + ": " + serverResponseCode);

            //close the streams //
            fileInputStream.close();
            // dos.flush();
            // dos.close();
        } catch (MalformedURLException ex) {
            Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
        } catch (Exception e) {
            Log.d("Test", "exception " + e.getMessage());
        }
    }
}

