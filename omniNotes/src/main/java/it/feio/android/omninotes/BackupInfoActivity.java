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
package it.feio.android.omninotes;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;

import androidx.appcompat.widget.Toolbar;

import java.io.File;

import it.feio.android.omninotes.databinding.ActivityBackupInfoBinding;
import it.feio.android.omninotes.databinding.ActivitySettingsBinding;
import it.feio.android.omninotes.utils.FileChooseUtil;
import it.feio.android.omninotes.utils.FileHelper;


public class BackupInfoActivity extends BaseActivity {

   public  static  final  String KEY_INFO="KEY_INFO";
  private ActivityBackupInfoBinding binding;
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    binding = ActivityBackupInfoBinding.inflate(getLayoutInflater());
    View view = binding.getRoot();
    setContentView(view);
    //intent.putExtra(String name, String value);
    Intent intent = getIntent();
    //intent.geex
    String info = intent.getStringExtra(KEY_INFO);
    //setContentView(R.layout.activity_about);
    //setContentView(R.layout.activity_about);

    //WebView webview = findViewById(R.id.webview);
    //webview.loadUrl("file:///android_asset/html/about.html");

    //initUI();
      Log.i("info", "onCreate: "+info);
    binding.info.setText(info);
    binding.info.setOnClickListener(view1 -> {
 openDir(info);
    });
      binding.btnOpen.setOnClickListener(view1 -> {
          Log.i("open", "onCreate: open  "+info);
          openDir(info);
      });
      binding.btnCopy.setOnClickListener(view1 -> {
          Log.i("open", "onCreate: btnCopy  "+info);
          //openDir(info);
          String text = binding.info.getText().toString();
          ClipboardManager mClipboardManager  =
                  (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
          ClipData   mClipData = ClipData.newPlainText("test", text);
          mClipboardManager.setPrimaryClip(mClipData);
      });


      //ClipData mClipData;
      //String text = "hello world";
      //mClipData = ClipData.newPlainText("test", text);
      //mClipboardManager.setPrimaryClip(mClipData);
  }

  @Override
  protected void onActivityResult(int requestCode,int resultCode,Intent data){//选择文件返回
    super.onActivityResult(requestCode,resultCode,data);
    if(resultCode==RESULT_OK){
      switch(requestCode){
        case REQUEST_CHOOSEFILE:
          Uri uri=data.getData();
          String  chooseFilePath= FileChooseUtil.getInstance(this).getChooseFileResultPath(uri);
          Log.d("选择文件返回","选择文件返回："+chooseFilePath);
          //sendFileMessage(chooseFilePath);
          break;
      }
    }
  }

 static final int REQUEST_CHOOSEFILE=1;
    void openDir(String  dirStr){
    if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
      //showToast(R.string.msg_storage_nosdcard);
      Log.i("sdCard", "openDir: 没有 sdCard");
      return;
    }
    //获取文件下载路径
    //String compName = AppString.getCompanyName();
    //String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + compName + "/OSC_DATA/";
    //File dir = new File(path);
    File dir = new File(dirStr);

    if (!dir.exists()) {
      //dir.mkdirs();
      Log.i("不存在", "openDir: 不存在");
      return;
    }
        if (dir.isDirectory()) {
            Log.i("是文件夹", "openDir: 是文件夹");
            Log.i("dir", "openDir: "+dir);
            //dir.ge
            //File java 遍历
            File[] listFiles = dir.listFiles();	//遍历path下的文件和目录，放在File数组中
            if(listFiles==null){
                Log.i("没有里面的文件", "openDir: 没有里面的文件");
                return;
            }
            for (File listFile : listFiles) {
                for (int i = 0; i < FileHelper.MIME_MapTable.length; i++) {
                    String suffix = FileHelper.MIME_MapTable[i][0];
                    //Log.i("suffix", "openDir: suffix  "+suffix);
                    if(suffix.equals("")){
                        continue;
                    }
                    if (listFile.getName().endsWith(suffix)) {
                        Log.i("suffix", "openDir: 这个可以 "+suffix);
                        //打开里面的一个可以打开的文件
                        Log.i("listFile", "openDir: "+listFile);
                        //dir.
                        //java file join
                        //Intent openFileIntent = FileHelper.getOpenFileIntent(dir);
                        Intent openFileIntent = FileHelper.getOpenFileIntent(listFile);
                        startActivityForResult(openFileIntent, REQUEST_CHOOSEFILE);
                        return;
                    }
                }
                //for (int i = 0; i < FileHelper.MIME_MapTable[0].length; i++) {
                //    FileHelper.MIME_MapTable[i]
                //}
                //for (String s : FileHelper.MIME_MapTable[0]) {
                //
                //}
            }
            Log.i("没有找到合适的", "openDir: ");
            return;
        }
        //if (dir.isDirectory()) {
        //    DocumentsContract.Document.MIME_TYPE_DIR
        //}
    //调用系统文件管理器打开指定路径目录
    //Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
    //这里是父亲啊
        File parentFile = dir.getParentFile();
        Log.i("parentFile", "openDir: "+parentFile);
    //    intent.setDataAndType(Uri.fromFile(dir.getParentFile()), "file/*.txt");
    //intent.addCategory(Intent.CATEGORY_OPENABLE);
    //    startActivityForResult(intent, REQUEST_CHOOSEFILE);
    //    setDataAndType 文件夹
        Intent openFileIntent = FileHelper.getOpenFileIntent(dir);
        startActivityForResult(openFileIntent, REQUEST_CHOOSEFILE);

  }


  @Override
  public void onStart() {
    ((OmniNotes) getApplication()).getAnalyticsHelper().trackScreenView(getClass().getName());
    super.onStart();
  }


  @Override
  public boolean onNavigateUp() {
    onBackPressed();
    return true;
  }


  private void initUI() {
    Toolbar toolbar = findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    getSupportActionBar().setHomeButtonEnabled(true);
    toolbar.setNavigationOnClickListener(v -> onNavigateUp());
  }

}
