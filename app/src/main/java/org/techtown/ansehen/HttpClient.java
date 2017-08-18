package org.techtown.ansehen;

/**
 * Created by bit on 2017-07-06.
 */

import android.util.Log;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import static android.content.ContentValues.TAG;

/**
 * Created by bit on 2017-07-05.
 */
public class HttpClient {
    private static final String urlPath_register = "http://13.124.164.203/register.php";
    private static final String urlPath_upload = "http://13.124.164.203/UploadToServer.php";
    private FileInputStream fileInputStream =null;
    private URL connectUrl =null;
    int serverResponseCode = 0;
    String lineEnd = "\r\n";
    String twoHyphens = "--";
    String boundary ="*****";


    public void putUserInfo(String name, String pw,String phoneNum, String inputPhone,String filename,String primaryKey)
    {
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
            buffer.append("userPhoneNum").append("=").append(phoneNum).append("&");
            buffer.append("userInputPhoneNum").append("=").append(inputPhone).append("&");
            buffer.append("userName").append("=").append(name).append("&");
            buffer.append("userPw").append("=").append(pw).append("&");
            buffer.append("fileName").append("=").append(filename).append("&");
            buffer.append("uniqueKey").append("=").append(primaryKey);

            OutputStreamWriter osw = new OutputStreamWriter(conn.getOutputStream());
            PrintWriter wr = new PrintWriter(osw);
            wr.write(buffer.toString());
            wr.flush();

            Log.i("aaaaaaaa","aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
            String serverResponseMessage = conn.getResponseMessage();
            Log.i("uploadFile", "HTTP Response is : "
                    + serverResponseMessage + ": " + serverResponseCode);

            fileInputStream.close();
        } catch (MalformedURLException ex) {
            Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
        } catch (Exception e) {
            Log.d("Test", "exception " + e.getMessage());
        }
    }



    public void HttpFileUpload(String fileName)
    {
        try
        {
            File sourceFile = new File(fileName);
            fileInputStream=new FileInputStream(sourceFile);
            connectUrl=new URL(urlPath_upload );


            Log.d("Test","fileInputStream is "+fileInputStream);

            HttpURLConnection conn = (HttpURLConnection) connectUrl.openConnection();
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Connection","Keep-Alive");
            conn.setRequestProperty("ENCTYPE", "multipart/form-data");

            conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
            conn.setRequestProperty("uploaded_file", fileName);




            // write data
            DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\"" + fileName + "\"" + lineEnd);
            dos.writeBytes(lineEnd);


            int bytesAvailable = fileInputStream.available();
            int maxBufferSize = 100*1024*1024;
            int bufferSize = Math.min(bytesAvailable, maxBufferSize);

            byte[] buffer = new byte[bufferSize];
            int bytesRead = fileInputStream.read(buffer, 0, bufferSize);

            Log.e(TAG,"image byte is " + bytesRead);

// read image
            int i=0;
            while (bytesRead > 0) {
                dos.write(buffer, 0, bufferSize);
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                if(i==0)
                    Log.d("Test","start");
                i++;
            }
            dos.writeBytes(lineEnd);
            dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
// close streams
            Log.e("Test" , "File is written");
            serverResponseCode = conn.getResponseCode();

            String serverResponseMessage = conn.getResponseMessage();

            Log.i("uploadFile", "HTTP Response is : "

                    + serverResponseMessage + ": " + serverResponseCode);


            //close the streams //

            fileInputStream.close();

            dos.flush();

            dos.close();



        } catch (MalformedURLException ex) {

            Log.e("Upload file to server", "error: " + ex.getMessage(), ex);

        } catch (Exception e) {
            Log.d("Test", "exception " + e.getMessage());
        }



    }
}

