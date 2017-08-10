package org.techtown.ansehen;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.FaceDetector;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static android.content.ContentValues.TAG;

public class CameraActivity extends AppCompatActivity  {

    private static final int PICK_FROM_CAMERA =0;
    private Uri mImageCaptureUri;
    Button btn = null;
    ImageView iv = null;
    String url;
    String primaryKey;
    int status;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);



        Intent intent = getIntent();
        url= intent.getExtras().getString("RegisterActivity_phoneNum");
        primaryKey=intent.getExtras().getString("primaryKey");
        Log.e(TAG,"CameraActivity_phonenum : "+url);
        Log.e(TAG,"primaryKey : "+primaryKey);
        //iv =(ImageView)this.findViewById(R.id.iv);
        setup();
    }



    public void camButton(){
        Intent camintent = new Intent(CameraActivity.this, TMapActivity.class);
        startActivity(camintent);
    }

    private void setup()
    {
        btn = (Button)findViewById(R.id.cameraButton);
        // iv = (ImageView)findViewById(R.id.iv);






        btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                //url ="tmp_"+String.valueOf(System.currentTimeMillis())+".png";

                mImageCaptureUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(),url));

                Log.e(TAG, "before taking photo");

                intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT,mImageCaptureUri);
                startActivityForResult(intent,PICK_FROM_CAMERA);
                Log.e(TAG, "take photo");

            }
        });


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e(TAG,"on ActivityResult");
        if(requestCode==PICK_FROM_CAMERA) //?쇨뎬 ?몄떇 遺遺??⑥닔 ?ㅼ뼱媛?遺遺?
        {
            Log.e(TAG,"lala");
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig= Bitmap.Config.RGB_565;
            options.inScaled=false;
            options.inDither=false;
            Bitmap bitmap=BitmapFactory.decodeFile(mImageCaptureUri.getPath(),options);
            Log.e(TAG,"path~~ : "+mImageCaptureUri.getPath());
            //iv = (ImageView)findViewById(R.id.iv);
            //iv.setImageBitmap(bitmap);

            FaceDetector.Face[] faces = new FaceDetector.Face[2];
            FaceDetector detector = new FaceDetector(bitmap.getWidth(), bitmap.getHeight(), faces.length);
            int numFace =detector.findFaces(bitmap, faces);
            Log.e(TAG,"number of face : "+numFace);
            if(numFace>0)
            {

                new Thread(new Runnable() {

                    public void run() {

                        runOnUiThread(new Runnable() {

                            public void run() {
                            }
                        });

                        HttpClient httpClient = new HttpClient();
                        httpClient.HttpFileUpload(""+mImageCaptureUri.getPath());

                    }
                }).start();

                Intent CameraIntent = new Intent(CameraActivity.this, TMapActivity.class);
                CameraIntent.putExtra("primarykey",primaryKey);
                startActivity(CameraIntent);



                //
                //camButton();
            }
            else
                setup();
        }

        if(requestCode!=RESULT_OK)
            return;

    }

}
