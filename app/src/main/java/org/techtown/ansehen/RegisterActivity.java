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
import android.widget.Toast;

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
                if(loginName.equals("")||loginName.equals("")||loginPw.equals("")) {
                    Toast.makeText(RegisterActivity.this, "입력 미완료", Toast.LENGTH_SHORT).show();
                    return;
                }
                else if(loginPhone.equals(getPhoneNumber())) {
                    String message="본인 핸드폰 번호가 아닌\n";
                    Toast.makeText(RegisterActivity.this, message + "연락받을 사람의 번호를 입력해주세요", Toast.LENGTH_LONG).show();
                    return;
                }
                Intent registerIntent = new Intent(RegisterActivity.this, CameraActivity.class);
                registerIntent.putExtra("RegisterActivity_phoneNum",filename);
                registerIntent.putExtra("primaryKey",primaryKey);
                registerIntent.putExtra("password",loginPw);
                registerIntent.putExtra("phonenumber",loginPhone);
                registerIntent.putExtra("myphonenum",phoneNum);
                registerIntent.putExtra("name",loginName);
                RegisterActivity.this.startActivity(registerIntent);

                Log.e(TAG,"phoneNum : "+filename);

            }
        });
    }
}
