package org.techtown.ansehen;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
/**
 * Created by bit on 2017-09-20.
 */

public class TestService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Log.i("onTaskRemoved - ",""+rootIntent);
        // 여기에 필요한 코드를 추가한다.
        /*
        ((TMapActivity)TMapActivity.mContext).ghandler.removeMessages(0);
        ((TMapActivity)TMapActivity.mContext).handler.removeMessages(0);
        ((TMapActivity)TMapActivity.mContext).mhandler.removeMessages(0);
        ((endActivity)endActivity.mContext).dataClear(((TMapActivity)TMapActivity.mContext).primaryKey);
        */
        stopSelf();
    }
}