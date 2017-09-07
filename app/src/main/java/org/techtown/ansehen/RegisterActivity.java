package org.techtown.ansehen;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import static android.content.ContentValues.TAG;

public class RegisterActivity extends AppCompatActivity {

    EditText nameText;
    EditText pwText;
    EditText phoneText;

    String loginName;
    String loginPw;
    String loginPhone;
    String filename;
    String phoneNum;
    String  primaryKey;
//

    String getPhoneNumber()
    {
        TelephonyManager mgr = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        phoneNum = mgr.getLine1Number();
        if(phoneNum.startsWith("+82"))
        {
            phoneNum = phoneNum.replace("+82","0");
            return phoneNum;
        }
        return phoneNum;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        nameText = (EditText)findViewById(R.id.nameText);
        pwText = (EditText)findViewById(R.id.pwText);
        phoneText = (EditText)findViewById(R.id.phoneText);
        primaryKey = String.valueOf(System.currentTimeMillis());


        Button submit = (Button)findViewById(R.id.registerButton);
        submit.setOnClickListener(new Button.OnClickListener(){

            @Override
            public void onClick(View v) {
                loginName = nameText.getText().toString().trim();
                loginPw = pwText.getText().toString().trim();
                loginPhone = phoneText.getText().toString().trim();
                filename=getPhoneNumber()+"__"+primaryKey+".jpg";

                new Thread(new Runnable() {

                    public void run() {

                        runOnUiThread(new Runnable() {

                            public void run() {
                                //messageText.setText("uploading started.....");
                            }
                        });
                        EditText nameText = (EditText)findViewById(R.id.nameText);
                        EditText pwText = (EditText)findViewById(R.id.pwText);
                        EditText phoneText = (EditText)findViewById(R.id.phoneText);

                        String name = nameText.getText().toString();
                        String password = pwText.getText().toString();
                        String inputPhone = phoneText.getText().toString();


                        String phoneNum = getPhoneNumber();

                        Log.e(TAG,"name : "+name);
                        Log.e(TAG,"passWord : "+password);
                        Log.e(TAG,"inputPhone : "+inputPhone);
                        Log.e(TAG,"phoneNum : "+phoneNum);
                        Log.e(TAG,"primaryKey : "+primaryKey);

                        HttpClient http = new HttpClient();
                        http.putUserInfo(name,password,phoneNum,inputPhone,filename,primaryKey);
                    }
                }).start();

                Intent registerIntent = new Intent(RegisterActivity.this, CameraActivity.class);
                registerIntent.putExtra("RegisterActivity_phoneNum",filename);
                registerIntent.putExtra("primaryKey",primaryKey);
                registerIntent.putExtra("password",loginPw);
                registerIntent.putExtra("phonenumber",loginPhone);
                RegisterActivity.this.startActivity(registerIntent);

                Log.e(TAG,"phoneNum : "+filename);

            }
        });
    }

}
