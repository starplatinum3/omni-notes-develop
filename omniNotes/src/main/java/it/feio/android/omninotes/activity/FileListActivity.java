package it.feio.android.omninotes.activity;

import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.feio.android.omninotes.R;
//
//public class FileListActivity {
//}
//
//package com.example.sd1;

import android.widget.Button;
import android.widget.TextView;

public class FileListActivity extends AppCompatActivity {
    Button button;
    TextView textView;
    File[] currentfiles;
    String pathfile;
    File parentfile;
    File baseFile;

    void init(){
        File file = Environment.getRootDirectory();
        pathfile = file.getAbsolutePath();
        textView = findViewById(R.id.main_text_0);
        textView.setText("当前路径为" + pathfile);
        parentfile = file;
        currentfiles = file.listFiles();
        this.inflateFiles(currentfiles);
        button = findViewById(R.id.main_button_back);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (parentfile == Environment.getRootDirectory()) {
                    Toast.makeText(FileListActivity.this, "到顶啦", Toast.LENGTH_SHORT).show();
                } else {
                    //当点击上一级时，使用parentfile，来刷新listview,
                    parentfile = parentfile.getParentFile();
                    currentfiles = parentfile.listFiles();
                    FileListActivity.this.inflateFiles(currentfiles);
                    textView.setText("当前路径为" + parentfile.getAbsolutePath());
                }


            }
        });
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
        setContentView(R.layout.activity_list_file);
        File file = Environment.getRootDirectory();
        pathfile = file.getAbsolutePath();
        textView = findViewById(R.id.main_text_0);
        textView.setText("当前路径为" + pathfile);
        parentfile = file;
        currentfiles = file.listFiles();
        this.inflateFiles(currentfiles);
        button = findViewById(R.id.main_button_back);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (parentfile == Environment.getRootDirectory()) {
                    Toast.makeText(FileListActivity.this, "到顶啦", Toast.LENGTH_SHORT).show();
                } else {
                    //当点击上一级时，使用parentfile，来刷新listview,
                    parentfile = parentfile.getParentFile();
                    currentfiles = parentfile.listFiles();
                    FileListActivity.this.inflateFiles(currentfiles);
                    textView.setText("当前路径为" + parentfile.getAbsolutePath());
                }


            }
        });

    }

    public void inflateFiles(final File[] files) {
        ListView listView;
        List<Map<String, Object>> lists;
        listView = findViewById(R.id.main_listview);
        lists = new ArrayList<>();
        Log.i("lenght", String.valueOf(files.length));
        for (int i = 0; i < files.length; i++) {
            Map<String, Object> list = new HashMap<>();
            list.put("path", files[i].getAbsolutePath());
            list.put("pathname", files[i].getName());
            Log.i("tagtag", String.valueOf(files[i]));
            lists.add(list);
        }
        SimpleAdapter simpleAdapter = new SimpleAdapter(this, lists, R.layout
                .item_one_file, new String[]{"path"}, new int[]{R.id.item_text});
        listView.setAdapter(simpleAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (files[position].isFile()) {
                    Toast.makeText(FileListActivity.this, "这是一个文件哦", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    if (files[position].listFiles() != null) {
                        File[] tmp = files[position].listFiles();
                        Log.i("12138", String.valueOf(tmp));
                        currentfiles = tmp;
                        FileListActivity.this.inflateFiles(tmp);
                        pathfile = files[position].getAbsolutePath();
                        textView.setText("当前路径" + pathfile);
                        parentfile = files[position];
                    } else {
                        Toast.makeText(FileListActivity.this, "文件夹为空", Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });
    }
}

