/*
 * Copyright (C) 2013-2020 Federico Iosue (federico@iosue.it)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package it.feio.android.omninotes.async;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static it.feio.android.omninotes.utils.ConstantsBase.ACTION_RESTART_APP;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.Button;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;

import com.pixplicity.easyprefs.library.Prefs;

import it.feio.android.omninotes.BackupInfoActivity;
import it.feio.android.omninotes.MainActivity;
import it.feio.android.omninotes.OmniNotes;
import it.feio.android.omninotes.R;
import it.feio.android.omninotes.SettingsActivity;
import it.feio.android.omninotes.activity.HolderActivity;
import it.feio.android.omninotes.db.DbHelper;
import it.feio.android.omninotes.helpers.BackupHelper;
import it.feio.android.omninotes.helpers.LogDelegate;
import it.feio.android.omninotes.helpers.SpringImportHelper;
import it.feio.android.omninotes.helpers.notifications.NotificationChannels.NotificationChannelNames;
import it.feio.android.omninotes.helpers.notifications.NotificationsHelper;
import it.feio.android.omninotes.models.Attachment;
import it.feio.android.omninotes.models.Note;
import it.feio.android.omninotes.models.listeners.OnAttachingFileListener;
import it.feio.android.omninotes.receiver.ButtonReceiver;
import it.feio.android.omninotes.utils.ActivityUtil;
import it.feio.android.omninotes.utils.NotificationUtils;
import it.feio.android.omninotes.utils.ReminderHelper;
import it.feio.android.omninotes.utils.StorageHelper;
import java.io.File;

public class DataBackupIntentService extends IntentService implements OnAttachingFileListener {

  public static final String INTENT_BACKUP_NAME = "backup_name";
  public static final String ACTION_DATA_EXPORT = "action_data_export";
  public static final String ACTION_DATA_IMPORT = "action_data_import";
  public static final String ACTION_DATA_IMPORT_LEGACY = "action_data_import_legacy";
  public static final String ACTION_DATA_DELETE = "action_data_delete";

  private NotificationsHelper mNotificationsHelper;

//    {
//        File autoBackupDir = StorageHelper.getBackupDir(Constants.AUTO_BACKUP_DIR);
//        BackupHelper.exportNotes(autoBackupDir);
//        BackupHelper.exportAttachments(autoBackupDir);
//    }


  public DataBackupIntentService() {
    super("DataBackupIntentService");
  }

  @Override
  protected void onHandleIntent(Intent intent) {
    mNotificationsHelper = new NotificationsHelper(this).start(NotificationChannelNames.BACKUPS,
        R.drawable.ic_content_save_white_24dp, getString(R.string.working));

    // If an alarm has been fired a notification must be generated
    if (ACTION_DATA_EXPORT.equals(intent.getAction())) {
      exportData(intent);
      //怎么让他在界面上有显示呢
      //service 在 handler 向着Activity 发送
    } else if (ACTION_DATA_IMPORT.equals(intent.getAction()) || ACTION_DATA_IMPORT_LEGACY
        .equals(intent.getAction())) {
      importData(intent);
    } else if (SpringImportHelper.ACTION_DATA_IMPORT_SPRINGPAD.equals(intent.getAction())) {
      importDataFromSpringpad(intent, mNotificationsHelper);
    } else if (ACTION_DATA_DELETE.equals(intent.getAction())) {
      deleteData(intent);
    }
  }

  private void importDataFromSpringpad(Intent intent, NotificationsHelper mNotificationsHelper) {
    new SpringImportHelper(OmniNotes.getAppContext())
        .importDataFromSpringpad(intent, mNotificationsHelper);
    String title = getString(R.string.data_import_completed);
    String text = getString(R.string.click_to_refresh_application);
    createNotification(intent, this, title, text, null);
  }

  private static final String TAG  = "DataBackupIntentService";
  private synchronized void exportData(Intent intent) {

    boolean result;

    // Gets backup folder
    String backupName = intent.getStringExtra(INTENT_BACKUP_NAME);
//    这个是传入的 但是他的目录是
    File backupDir = StorageHelper.getOrCreateBackupDir(backupName);

    // Directory clean in case of previously used backup name
//    如果是以前使用的备份名称，则清除目录
    StorageHelper.delete(this, backupDir.getAbsolutePath());

    // Directory is re-created in case of previously used backup name (removed above)
//    如果是以前使用的备份名称，则会重新创建目录（上面已删除）
    backupDir = StorageHelper.getOrCreateBackupDir(backupName);

    //result=false;
    result=true;
    //BackupHelper.exportNotes(backupDir);
    //result = BackupHelper.exportAttachments(backupDir, mNotificationsHelper);
    //result = result  && BackupHelper.exportSettings(backupDir);

    String successStr=getString(R.string.data_export_completed)+"\nat: "+backupDir.toString();
//    String notificationMessage =
//        result ? getString(R.string.data_export_completed) : getString(R.string.data_export_failed);
    String notificationMessage =
            result ? successStr: getString(R.string.data_export_failed);

    Log.i(TAG,notificationMessage);
    //startBackupInfoActivity(backupDir,notificationMessage);
    startBackupInfo(backupDir,notificationMessage);
  }

  void startBackupInfoActivity(File backupDir, String notificationMessage){
    //    消息没有弹出
    Intent intentToBackupInfoActivity = new Intent(this, BackupInfoActivity.class);
    //activity.startActivity(intent);
    //backupDir.getParent()
    String parent = backupDir.getParent();
    Log.i(TAG, "exportData: parent  "+parent);
    //这里需要传入的是这个文件而不是他的父亲  activity里面不要打开他的 父亲啊 因为他自己也是文件夹
    intentToBackupInfoActivity.putExtra(BackupInfoActivity.KEY_INFO,backupDir.toString());
    //intentToBackupInfoActivity.putExtra(BackupInfoActivity.KEY_INFO,backupDir.getParent());
    intentToBackupInfoActivity.setFlags(FLAG_ACTIVITY_NEW_TASK);

    startActivity(intentToBackupInfoActivity);
    //ActivityUtil.startActivity(this,);
    //Intent intentTo = new Intent(this, MainActivity.class);
    Intent intentTo = new Intent(this, SettingsActivity.class);

    //设置TaskStackBuilder
    TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
    stackBuilder.addParentStack(MainActivity.class);
    stackBuilder.addNextIntent(intentTo);


    //NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    //Notification notification = new NotificationCompat.Builder(this)
    //        .setSmallIcon(R.mipmap.ic_launcher)        //设置图标
    //        .setContentTitle("标题")                    //设置标题
    //        .setContentText("这是内容，点击我可以跳转")                 //消息内容
    //        .setWhen(System.currentTimeMillis())         //发送时间
    //        .setDefaults(Notification.DEFAULT_ALL)      //设置默认的提示音，振动方式，灯光
    //        .setAutoCancel(true)                         //打开程序后图标消失
    //        .setContentIntent(pendingIntent)              //设置点击响应
    //        .build();
    //manager.notify(1, notification)

    //mNotificationsHelper.finish(intent, notificationMessage);
    PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

    mNotificationsHelper.finish(1,pendingIntent, notificationMessage);
    sendNotification();
  }
  void startBackupInfo(File backupDir, String notificationMessage){
    //    消息没有弹出
    //Intent intentToBackupInfoActivity = new Intent(this, BackupInfoActivity.class);
    Intent intentToBackupInfoActivity = new Intent(this, HolderActivity.class);
    //activity.startActivity(intent);
    //backupDir.getParent()
    String parent = backupDir.getParent();
    Log.i(TAG, "exportData: parent  "+parent);
    //这里需要传入的是这个文件而不是他的父亲  activity里面不要打开他的 父亲啊 因为他自己也是文件夹
    //intentToBackupInfoActivity.putExtra(BackupInfoActivity.KEY_INFO,backupDir.toString());
    //intentToBackupInfoActivity.putExtra(BackupInfoActivity.KEYY_INFO,backupDir.getParent());
    //intentToBackupInfoActivity.putExtra(HolderActivity.KEY_INFO,backupDir.getParent());
    intentToBackupInfoActivity.putExtra(HolderActivity.KEY_INFO,backupDir.toString());
    intentToBackupInfoActivity.setFlags(FLAG_ACTIVITY_NEW_TASK);

    startActivity(intentToBackupInfoActivity);
    //ActivityUtil.startActivity(this,);
    //Intent intentTo = new Intent(this, MainActivity.class);
    Intent intentTo = new Intent(this, SettingsActivity.class);

    //设置TaskStackBuilder
    TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
    stackBuilder.addParentStack(MainActivity.class);
    stackBuilder.addNextIntent(intentTo);


    //NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    //Notification notification = new NotificationCompat.Builder(this)
    //        .setSmallIcon(R.mipmap.ic_launcher)        //设置图标
    //        .setContentTitle("标题")                    //设置标题
    //        .setContentText("这是内容，点击我可以跳转")                 //消息内容
    //        .setWhen(System.currentTimeMillis())         //发送时间
    //        .setDefaults(Notification.DEFAULT_ALL)      //设置默认的提示音，振动方式，灯光
    //        .setAutoCancel(true)                         //打开程序后图标消失
    //        .setContentIntent(pendingIntent)              //设置点击响应
    //        .build();
    //manager.notify(1, notification)

    //mNotificationsHelper.finish(intent, notificationMessage);
    PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

    mNotificationsHelper.finish(1,pendingIntent, notificationMessage);
    sendNotification();
  }

  private static final String NOTIFICATION_BUTTON1_CLICK= "notification_button1_click";
  private static final String NOTIFICATION_BUTTON2_CLICK= "notification_button2_click";
  private ButtonReceiver receiver;
//————————————————
//  版权声明：本文为CSDN博主「安之若素丶c」的原创文章，遵循CC 4.0 BY-SA版权协议，转载请附上原文出处链接及本声明。
//  原文链接：https://blog.csdn.net/qq_34414005/article/details/71336136

  /*
   * 发送一个通知
   * */
  public void sendNotification(){
    //默认是工作中
    NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    NotificationCompat.Builder builder = new NotificationCompat
            .Builder(this,"noti_back").setSmallIcon(R.mipmap.ic_launcher);
    //@layout/notification_layout includes views not allowed in a RemoteView: androidx.constraintlayout.widget.ConstraintLayout

    RemoteViews remoteViews = new RemoteViews(getPackageName(),R.layout.notification_layout);
    Intent intent = new Intent(this, ButtonReceiver.class);
    intent.setAction(NOTIFICATION_BUTTON1_CLICK);
    PendingIntent pendingIntent = PendingIntent.getBroadcast(this,0,intent,0);
    remoteViews.setOnClickPendingIntent(R.id.btn_1,pendingIntent);
    //Intent intent = new Intent(this, ButtonReceiver.class);
    //PendingIntent pendingIntent2;
    intent.setAction(NOTIFICATION_BUTTON2_CLICK);
    PendingIntent pendingIntent2 = PendingIntent.getBroadcast(this,0,intent,0);
    remoteViews.setOnClickPendingIntent(R.id.btn_2,pendingIntent2);
    remoteViews.setTextViewText(R.id.text_view,"啦啦啦德玛西亚");
    /*
     * 获取到系统默认通知颜色，并设置通知字体颜色
     * */

    //remoteViews.setInt(R.id.text_view,"setTextColor",
    //        NotificationUtils.isDarkNotificationTheme(OmniNotes.getAppContext())==true? Color.WHITE:Color.BLACK);

    /*
     * 判断SDK后使用bigContentView让通知显示高度变大
     * */
    Notification notification = new Notification();
    if(android.os.Build.VERSION.SDK_INT >= 16) {
      notification = builder.build();
      notification.bigContentView = remoteViews;
    }
    notification.contentView = remoteViews;
    manager.notify(1,notification);
  }
//————————————————
//  版权声明：本文为CSDN博主「安之若素丶c」的原创文章，遵循CC 4.0 BY-SA版权协议，转载请附上原文出处链接及本声明。
//  原文链接：https://blog.csdn.net/qq_34414005/article/details/71336136
//
//  void openDir(){
//    if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
//      showToast(R.string.msg_storage_nosdcard);
//      return;
//    }
//    //获取文件下载路径
//    String compName = AppString.getCompanyName();
//    String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + compName + "/OSC_DATA/";
//    File dir = new File(path);
//    if (!dir.exists()) {
//      dir.mkdirs();
//    }
//    //调用系统文件管理器打开指定路径目录
//    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//    intent.setDataAndType(Uri.fromFile(dir.getParentFile()), "file/*.txt");
//    intent.addCategory(Intent.CATEGORY_OPENABLE);
//    startActivityForResult(intent, REQUEST_CHOOSEFILE);
//  }

  private synchronized void importData(Intent intent) {

    boolean importLegacy = ACTION_DATA_IMPORT_LEGACY.equals(intent.getAction());

    // Gets backup folder
    String backupName = intent.getStringExtra(INTENT_BACKUP_NAME);
    File backupDir = importLegacy ? new File(backupName) : StorageHelper.getOrCreateBackupDir(backupName);

    BackupHelper.importSettings(backupDir);

    if (importLegacy) {
      BackupHelper.importDB(this, backupDir);
    } else {
      BackupHelper.importNotes(backupDir);
    }

    BackupHelper.importAttachments(backupDir, mNotificationsHelper);

    resetReminders();

    mNotificationsHelper.cancel();

    createNotification(intent, this, getString(R.string.data_import_completed),
        getString(R.string.click_to_refresh_application), backupDir);

    // Performs auto-backup filling after backup restore
//        if (Prefs.getBoolean(Constants.PREF_ENABLE_AUTOBACKUP, false)) {
//            File autoBackupDir = StorageHelper.getBackupDir(Constants.AUTO_BACKUP_DIR);
//            BackupHelper.exportNotes(autoBackupDir);
//            BackupHelper.exportAttachments(autoBackupDir);
//        }
  }

  private synchronized void deleteData(Intent intent) {

    // Gets backup folder
    String backupName = intent.getStringExtra(INTENT_BACKUP_NAME);
    File backupDir = StorageHelper.getOrCreateBackupDir(backupName);

    // Backups directory removal
    StorageHelper.delete(this, backupDir.getAbsolutePath());

    String title = getString(R.string.data_deletion_completed);
    String text = backupName + " " + getString(R.string.deleted);
    createNotification(intent, this, title, text, backupDir);
  }


  /**
   * Creation of notification on operations completed
   */
  private void createNotification(Intent intent, Context mContext, String title, String message,
      File backupDir) {

    // The behavior differs depending on intent action
    Intent intentLaunch;
    if (DataBackupIntentService.ACTION_DATA_IMPORT.equals(intent.getAction())
        || SpringImportHelper.ACTION_DATA_IMPORT_SPRINGPAD.equals(intent.getAction())) {
      intentLaunch = new Intent(mContext, MainActivity.class);
      intentLaunch.setAction(ACTION_RESTART_APP);
    } else {
      intentLaunch = new Intent();
    }
    // Add this bundle to the intent
    intentLaunch.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    intentLaunch.addFlags(FLAG_ACTIVITY_NEW_TASK);
    // Creates the PendingIntent
    PendingIntent notifyIntent = PendingIntent.getActivity(mContext, 0, intentLaunch,
        PendingIntent.FLAG_UPDATE_CURRENT);

    NotificationsHelper notificationsHelper = new NotificationsHelper(mContext);
    notificationsHelper.createStandardNotification(NotificationChannelNames.BACKUPS,
        R.drawable.ic_content_save_white_24dp, title, notifyIntent)
        .setMessage(message).setRingtone(Prefs.getString("settings_notification_ringtone", null))
        .setLedActive();
    if (Prefs.getBoolean("settings_notification_vibration", true)) {
      notificationsHelper.setVibration();
    }
    notificationsHelper.show();
  }


  /**
   * Schedules reminders
   */
  private void resetReminders() {
    LogDelegate.d("Resettings reminders");
    for (Note note : DbHelper.getInstance().getNotesWithReminderNotFired()) {
      ReminderHelper.addReminder(OmniNotes.getAppContext(), note);
    }
  }


  @Override
  public void onAttachingFileErrorOccurred(Attachment mAttachment) {
    // TODO Auto-generated method stub
  }


  @Override
  public void onAttachingFileFinished(Attachment mAttachment) {
    // TODO Auto-generated method stub
  }

}
