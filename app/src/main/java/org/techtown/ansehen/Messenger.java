package org.techtown.ansehen;

import android.content.Context;
import android.telephony.SmsManager;
import android.widget.Toast;

/**
 * Created by bit on 2017-09-13.
 */

public class Messenger {
    private Context mContext;
    public Messenger(Context mContext) {
        this.mContext = mContext;
    }
    public void sendMessageTo(String phoneNum, String message) {
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(phoneNum, null, message,null, null);
        Toast.makeText(mContext,"안심문자 전송완료",Toast.LENGTH_SHORT).show();
    }
}
