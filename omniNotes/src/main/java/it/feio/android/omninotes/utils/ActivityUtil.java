package it.feio.android.omninotes.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class ActivityUtil {
  public  static   void startActivity(Activity activity, Class<?> cls){
        Intent intent = new Intent(activity,cls);
        activity.startActivity(intent);
    }

    public  static   void startActivity(Context context, Class<?> cls){
        Intent intent = new Intent(context,cls);
        context.startActivity(intent);
    }

    public  static  void startBrowser(Context context,String url){
        //Uri uri = Uri.parse("http://www.baidu.com");
        Uri uri = Uri.parse(url);
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        intent.setData(uri);
        context.startActivity(intent);
    }
}
