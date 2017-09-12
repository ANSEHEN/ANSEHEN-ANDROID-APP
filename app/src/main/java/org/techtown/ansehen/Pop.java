package org.techtown.ansehen;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by bit on 2017-09-06.
 */

public class Pop extends Activity {
    //Handler test_temp;
    EditText popPassword;
    TextView timeText;
    String passwordTemp;
    String password = "1234";
    int value = 0;
    String tel = "tel:01075651050";
    String temp_s=new String("tel:");
    String tel_temp;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.pop);

        timeText = (TextView) findViewById(R.id.timeText);
        popPassword = (EditText) findViewById(R.id.popPassword);

        Intent intent = new Intent(this.getIntent());
        password=intent.getExtras().getString("password");
        tel_temp=intent.getExtras().getString("phonenumber");
        //test_temp=intent.getExtras().getParcelable("handler");
        tel=temp_s.concat(tel_temp);

        final Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        mHandler.sendEmptyMessage(0);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int) (width * 0.9), (int) (height * 0.85));
        final Button certain = (Button) findViewById(R.id.certain_button);
        certain.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    passwordTemp = popPassword.getText().toString().trim();
                    if (passwordTemp.equals(password)) {
                        mHandler.removeMessages(0);
                        //test_temp.sendEmptyMessage(0);
                        Intent intent_test=new Intent();
                        intent_test.putExtra("return","OK");
                        setResult(1234,intent_test);
                        Log.i("in Pop","String: "+"OK");
                        finish();
                    } else {
                        vibrator.vibrate(500);
                        Toast.makeText(getApplicationContext(), "다시 입력해주세요.", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Log.i("Exception execute", "------------------------------");
                }
            }


        });
    }

    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            value++;
            timeText.setText("Timer: " + value + "/60");
            mHandler.sendEmptyMessageDelayed(0, 1000);
            if (value == 60) {
                startActivity(new Intent("android.intent.action.CALL", Uri.parse(tel)));
                mHandler.removeMessages(0);
            }
        }
    };

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return false;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}