package org.techtown.ansehen;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class CameraActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
    }

    public void camButton(View v){
        Intent camintent = new Intent(CameraActivity.this, RegisterActivity.class);
        startActivity(camintent);
    }
}
