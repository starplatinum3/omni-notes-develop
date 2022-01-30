package it.feio.android.omninotes.utils;

import android.app.Activity;
import android.content.Intent;

public class ActivityUtil {
  public  static   void startActivity(Activity activity, Class<?> cls){
        Intent intent = new Intent(activity,cls);
        activity.startActivity(intent);
    }
}
