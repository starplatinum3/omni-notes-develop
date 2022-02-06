package it.feio.android.omninotes.fragment;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager.widget.ViewPager;


import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.feio.android.omninotes.R;
import it.feio.android.omninotes.activity.FileListActivity;
import it.feio.android.omninotes.databinding.ActivityListFileBinding;
import it.feio.android.omninotes.utils.ActivityUtil;
//import okhttp3.FormBody;
//import okhttp3.FormBody;

//@EqualsAndHashCode(callSuper = true)
//@Data
//@Builder
public class FileListFragment extends Fragment {

    public FileListFragment() {
    }
    ActivityListFileBinding binding;

    FragmentActivity activity;


    Button button;
    TextView textView;
    File[] currentfiles;
    String pathfile;
    File parentfile;
    File baseFile;

    public File getBaseFile() {
        return baseFile;
    }

    public void setBaseFile(File baseFile) {
        this.baseFile = baseFile;
    }

    public FileListFragment(File baseFile) {
        this.baseFile = baseFile;
    }

    void init(){
        //File file = Environment.getRootDirectory();
        File file=baseFile;
        pathfile = file.getAbsolutePath();
        //textView = findViewById(R.id.main_text_0);
        textView =binding.mainText0;
        textView.setText("当前路径为" + pathfile);
        parentfile = file;
        currentfiles = file.listFiles();
        this.inflateFiles(currentfiles);
        //button = findViewById(R.id.main_button_back);
        button=binding.mainButtonBack;
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (parentfile == Environment.getRootDirectory()) {
                    Toast.makeText(activity, "到顶啦", Toast.LENGTH_SHORT).show();
                } else {
                    //当点击上一级时，使用parentfile，来刷新listview,
                    parentfile = parentfile.getParentFile();
                    currentfiles = parentfile.listFiles();
                    //activity.inflateFiles(currentfiles);
                    inflateFiles(currentfiles);
                    textView.setText("当前路径为" + parentfile.getAbsolutePath());
                }


            }
        });
    }


    public void inflateFiles(final File[] files) {
        ListView listView;
        List<Map<String, Object>> lists;
        //listView = findViewById(R.id.main_listview);
        listView=binding.mainListview;
        lists = new ArrayList<>();
        Log.i("lenght", String.valueOf(files.length));
        for (File file : files) {
            Map<String, Object> list = new HashMap<>();
            list.put("path", file.getAbsolutePath());
            list.put("pathname", file.getName());
            Log.i("tagtag", String.valueOf(file));
            lists.add(list);
        }
        SimpleAdapter simpleAdapter = new SimpleAdapter(activity, lists, R.layout
                .item_one_file, new String[]{"path"}, new int[]{R.id.item_text});
        listView.setAdapter(simpleAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (files[position].isFile()) {
                    Toast.makeText(activity, "这是一个文件哦", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    if (files[position].listFiles() != null) {
                        File[] tmp = files[position].listFiles();
                        Log.i("12138", String.valueOf(tmp));
                        currentfiles = tmp;
                        inflateFiles(tmp);
                        pathfile = files[position].getAbsolutePath();
                        textView.setText("当前路径" + pathfile);
                        parentfile = files[position];
                    } else {
                        Toast.makeText(activity, "文件夹为空", Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });
    }

    //@RequiresApi(api = Build.VERSION_CODES.N)
    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        activity = getActivity();

        binding = ActivityListFileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
//binding.
        init();
        return root;
    }
    //public



}
