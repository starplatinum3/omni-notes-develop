package it.feio.android.omninotes.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class ButtonReceiver extends BroadcastReceiver {
    private static final String NOTIFICATION_BUTTON1_CLICK= "notification_button1_click";
    private static final String NOTIFICATION_BUTTON2_CLICK= "notification_button2_click";
    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(NOTIFICATION_BUTTON1_CLICK)&&intent!=null){
            Toast.makeText(context, "you click btn1", Toast.LENGTH_SHORT).show();
        }else if(intent.getAction().equals(NOTIFICATION_BUTTON2_CLICK)&&intent!=null){
            Toast.makeText(context, "you click btn2", Toast.LENGTH_SHORT).show();
        }
    }
}
