package com.example.map.map;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
/**
 * Created by gandhali on 11/25/15.
 */
public class GCMReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // assumes WordService is a registered service
        Intent i = new Intent(context, GCMServiceListener.class);
        context.startService(i);
    }
}
