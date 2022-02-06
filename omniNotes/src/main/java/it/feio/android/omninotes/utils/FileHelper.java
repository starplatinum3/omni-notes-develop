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

package it.feio.android.omninotes.utils;

import static java.lang.Long.parseLong;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.text.TextUtils;
import it.feio.android.omninotes.factory.MediaStoreFactory;
import it.feio.android.omninotes.helpers.LogDelegate;
import java.io.File;


public class FileHelper {

  private FileHelper() {
    // hides public constructor
  }

  /**
   * Get a file path from a Uri. This will get the the path for Storage Access Framework Documents,
   * as well as the _data field for the MediaStore and other file-based ContentProviders.
   *
   * @param context The context.
   * @param uri     The Uri to query.
   */
  public static String getPath(final Context context, final Uri uri) {

    if (uri == null) {
      return null;
    }

    // DocumentProvider
    if (DocumentsContract.isDocumentUri(context, uri)) {
      // ExternalStorageProvider
      if (isExternalStorageDocument(uri)) {
        final String docId = DocumentsContract.getDocumentId(uri);
        final String[] split = docId.split(":");
        final String type = split[0];

        if ("primary".equalsIgnoreCase(type)) {
          return Environment.getExternalStorageDirectory() + "/" + split[1];
        }

        // TODO handle non-primary volumes
      }
      // DownloadsProvider
      else if (isDownloadsDocument(uri)) {
        final Uri contentUri = ContentUris
            .withAppendedId(Uri.parse("content://downloads/public_downloads"),
                parseLong(DocumentsContract.getDocumentId(uri)));
        return getDataColumn(context, contentUri, null, null);
      }
      // MediaProvider
      else if (isMediaDocument(uri)) {
        final String docId = DocumentsContract.getDocumentId(uri);
        final String[] split = docId.split(":");
        final String type = split[0];
        MediaStoreFactory mediaStoreFactory = new MediaStoreFactory();
        Uri contentUri = mediaStoreFactory.createURI(type);

        final String selection = "_id=?";
        final String[] selectionArgs = new String[]{split[1]};

        return getDataColumn(context, contentUri, selection, selectionArgs);
      }
    }
    // MediaStore (and general)
    else if ("content".equalsIgnoreCase(uri.getScheme())) {
      return getDataColumn(context, uri, null, null);
    }
    // File
    else if ("file".equalsIgnoreCase(uri.getScheme())) {
      return uri.getPath();
    }

    return null;
  }


  /**
   * Get the value of the data column for this Uri. This is useful for MediaStore Uris, and other
   * file-based ContentProviders.
   *
   * @param context       The context.
   * @param uri           The Uri to query.
   * @param selection     (Optional) Filter used in the query.
   * @param selectionArgs (Optional) Selection arguments used in the query.
   * @return The value of the _data column, which is typically a file path.
   */
  public static String getDataColumn(Context context, Uri uri,
      String selection, String[] selectionArgs) {

    final String column = "_data";
    final String[] projection = {column};
    try (Cursor cursor = context.getContentResolver()
        .query(uri, projection, selection, selectionArgs, null)) {
      if (cursor != null && cursor.moveToFirst()) {
        final int column_index = cursor.getColumnIndexOrThrow(column);
        return cursor.getString(column_index);
      }
    } catch (Exception e) {
      LogDelegate.e("Error retrieving uri path", e);
    }
    return null;
  }


  /**
   * @param uri The Uri to check.
   * @return Whether the Uri authority is ExternalStorageProvider.
   */
  public static boolean isExternalStorageDocument(Uri uri) {
    return "com.android.externalstorage.documents".equals(uri.getAuthority());
  }


  /**
   * @param uri The Uri to check.
   * @return Whether the Uri authority is DownloadsProvider.
   */
  public static boolean isDownloadsDocument(Uri uri) {
    return "com.android.providers.downloads.documents".equals(uri.getAuthority());
  }


  /**
   * @param uri The Uri to check.
   * @return Whether the Uri authority is MediaProvider.
   */
  public static boolean isMediaDocument(Uri uri) {
    return "com.android.providers.media.documents".equals(uri.getAuthority());
  }

  /**
   * Trying to retrieve file name from content resolver
   */
  public static String getNameFromUri(Context mContext, Uri uri) {
    String fileName = "";
    Cursor cursor = null;
    try {
      cursor = mContext.getContentResolver()
          .query(uri, new String[]{"_display_name"}, null, null, null);
      if (cursor != null) {
        try {
          if (cursor.moveToFirst()) {
            fileName = cursor.getString(0);
          }

        } catch (Exception e) {
          LogDelegate.e("Error managing diskk cache", e);
        }
      } else {
        fileName = uri.getLastPathSegment();
      }
    } catch (SecurityException e) {
      return null;
    } finally {
      if (cursor != null) {
        cursor.close();
      }
    }
    return fileName;
  }


  public static String getFilePrefix(File file) {
    return getFilePrefix(file.getName());
  }


  public static String getFilePrefix(String fileName) {
    String prefix = fileName;
    int index = fileName.indexOf('.');
    if (index != -1) {
      prefix = fileName.substring(0, index);
    }
    return prefix;
  }


  public static String getFileExtension(File file) {
    return getFileExtension(file.getName());
  }


  public static String getFileExtension(String fileName) {
    if (TextUtils.isEmpty(fileName)) {
      return "";
    }
    String extension = "";
    int index = fileName.lastIndexOf('.');
    if (index != -1) {
      extension = fileName.substring(index);
    }
    return extension;
  }



  /**
   * 打开文件
   * @param file
   */
  public static Intent getOpenFileIntent(File file){


    Intent intent = new Intent();
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    //设置intent的Action属性
    intent.setAction(Intent.ACTION_VIEW);
    if(file.isDirectory()){
      intent.setDataAndType(/*uri*/Uri.fromFile(file), DocumentsContract.Document.MIME_TYPE_DIR);
    }else{
      //获取文件file的MIME类型
      String type = getMIMEType(file);
      //设置intent的data和Type属性。
      intent.setDataAndType(/*uri*/Uri.fromFile(file), type);
    }

    //跳转
    //startActivity(intent);
    //ActivityUtil.startActivity();
    return  intent;

  }

  /**
   * 根据文件后缀名获得对应的MIME类型。
   * @param file
   */
  public static  String  getMIMEType(File file) {

    String type="*/*";
    String fName = file.getName();
    //获取后缀名前的分隔符"."在fName中的位置。
    int dotIndex = fName.lastIndexOf(".");
    if(dotIndex < 0){
      return type;
    }
    /* 获取文件的后缀名 */
    String end=fName.substring(dotIndex,fName.length()).toLowerCase();
    if(end.equals(""))return type;
    //在MIME和文件类型的匹配表中找到对应的MIME类型。
    for (String[] strings : MIME_MapTable) { //MIME_MapTable??在这里你一定有疑问，这个MIME_MapTable是什么？
      if (end.equals(strings[0]))
        type = strings[1];
    }
    return type;
  }

  //有好多 行，每个都是 0 1
  //i 0----i , j 0 1
  //MIME_MapTable是所有文件的后缀名所对应的MIME类型的一个String数组：
  public static final String[][] MIME_MapTable={
          //{后缀名， MIME类型}
          {".3gp",    "video/3gpp"},
          {".apk",    "application/vnd.android.package-archive"},
          {".asf",    "video/x-ms-asf"},
          {".avi",    "video/x-msvideo"},
          {".bin",    "application/octet-stream"},
          {".bmp",    "image/bmp"},
          {".c",  "text/plain"},
          {".class",  "application/octet-stream"},
          {".conf",   "text/plain"},
          {".cpp",    "text/plain"},
          {".doc",    "application/msword"},
          {".docx",   "application/vnd.openxmlformats-officedocument.wordprocessingml.document"},
          {".xls",    "application/vnd.ms-excel"},
          {".xlsx",   "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"},
          {".exe",    "application/octet-stream"},
          {".gif",    "image/gif"},
          {".gtar",   "application/x-gtar"},
          {".gz", "application/x-gzip"},
          {".h",  "text/plain"},
          {".htm",    "text/html"},
          {".html",   "text/html"},
          {".jar",    "application/java-archive"},
          {".java",   "text/plain"},
          {".jpeg",   "image/jpeg"},
          {".jpg",    "image/jpeg"},
          {".js", "application/x-javascript"},
          {".log",    "text/plain"},
          {".m3u",    "audio/x-mpegurl"},
          {".m4a",    "audio/mp4a-latm"},
          {".m4b",    "audio/mp4a-latm"},
          {".m4p",    "audio/mp4a-latm"},
          {".m4u",    "video/vnd.mpegurl"},
          {".m4v",    "video/x-m4v"},
          {".mov",    "video/quicktime"},
          {".mp2",    "audio/x-mpeg"},
          {".mp3",    "audio/x-mpeg"},
          {".mp4",    "video/mp4"},
          {".mpc",    "application/vnd.mpohun.certificate"},
          {".mpe",    "video/mpeg"},
          {".mpeg",   "video/mpeg"},
          {".mpg",    "video/mpeg"},
          {".mpg4",   "video/mp4"},
          {".mpga",   "audio/mpeg"},
          {".msg",    "application/vnd.ms-outlook"},
          {".ogg",    "audio/ogg"},
          {".pdf",    "application/pdf"},
          {".png",    "image/png"},
          {".pps",    "application/vnd.ms-powerpoint"},
          {".ppt",    "application/vnd.ms-powerpoint"},
          {".pptx",   "application/vnd.openxmlformats-officedocument.presentationml.presentation"},
          {".prop",   "text/plain"},
          {".rc", "text/plain"},
          {".rmvb",   "audio/x-pn-realaudio"},
          {".rtf",    "application/rtf"},
          {".sh", "text/plain"},
          {".tar",    "application/x-tar"},
          {".tgz",    "application/x-compressed"},
          {".txt",    "text/plain"},
          {".wav",    "audio/x-wav"},
          {".wma",    "audio/x-ms-wma"},
          {".wmv",    "audio/x-ms-wmv"},
          {".wps",    "application/vnd.ms-works"},
          {".xml",    "text/plain"},
          {".z",  "application/x-compress"},
          {".zip",    "application/x-zip-compressed"},
          {"",        "*/*"}
  };
  //https://developer.aliyun.com/article/13470

        //————————————————
        //版权声明：本文为CSDN博主「Work_Times」的原创文章，遵循CC 4.0 BY-SA版权协议，转载请附上原文出处链接及本声明。
        //原文链接：https://blog.csdn.net/qq_34099401/article/details/64439869
}
