package com.example.admin.browser;

import android.app.*;
import android.content.*;
import android.content.pm.*;
import android.os.*;
import android.support.v4.app.*;
import android.support.v7.app.AppCompatActivity;
import android.text.*;
import android.view.*;
import android.widget.*;
import java.io.*;
import java.net.*;

public class SaveActivity extends AppCompatActivity implements Runnable{
    private EditText title;
    private EditText url;
    private ImageView cancel;
    private ImageView ok;
    private ImageView bcancel;
    private ImageView wcancel;
    private Intent intent;
    private File save;
    private String ptitle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save);
        title = (EditText)findViewById(R.id.title);
        url = (EditText)findViewById(R.id.url);
        cancel = (ImageView)findViewById(R.id.cancel);
        ok = (ImageView)findViewById(R.id.ok);
        bcancel = (ImageView)findViewById(R.id.bcancel);
        wcancel = (ImageView)findViewById(R.id.wcancel);
        intent = getIntent();
        title.setText(intent.getStringExtra("websitename"));
        ptitle = title.getText().toString();
        url.setText(intent.getStringExtra("url"));
        url.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        title.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    bcancel.setVisibility(View.VISIBLE);
                }else{
                    bcancel.setVisibility(View.GONE);
                }
            }
        });
        url.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    wcancel.setVisibility(View.VISIBLE);
                }else{
                    wcancel.setVisibility(View.GONE);
                }
            }
        });
        bcancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                title.setText("");
            }
        });
        wcancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                url.setText("");
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SaveActivity.this.finish();
            }
        });
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(url.getText().toString()!=null&&!url.getText().toString().equals("")&&title.getText().toString()!=null&&!title.getText().toString().equals("")) {
                    try {
                        File file = new File(Environment.getExternalStorageDirectory() + "/Mybrowser/save/" + ptitle);
                        for (File file2 : file.listFiles()) {
                            file2.delete();
                        }
                        file.delete();
                    }catch (Exception e){
                    }
                    save = new File(Environment.getExternalStorageDirectory() + "/Mybrowser/save/" + title.getText());
                    try {
                        save.mkdirs();
                        Thread t = new Thread(SaveActivity.this);
                        t.start();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                SaveActivity.this.finish();
            }
        });
    }
    @Override
    public void run() {
        try {
            String s = url.getText().toString();
            if(!s.startsWith("http")){
                s = "http://" + s;
            }
            URL website = new URL(s);
            URL iconurl = new URL("http://" + website.getHost() + "/favicon.ico");
            URLConnection conn = iconurl.openConnection();
            File icon = new File(Environment.getExternalStorageDirectory() + "/Mybrowser/save/" + title.getText() + "/favicon.ico");
            FileOutputStream fos = new FileOutputStream(icon);
            InputStream is = conn.getInputStream();
            int i;
            while((i = is.read())!=-1){
                fos.write(i);
            }
            File furl = new File(Environment.getExternalStorageDirectory() + "/Mybrowser/save/" + title.getText() + "/url.txt");
            BufferedWriter bw = new BufferedWriter(new FileWriter(furl));
            bw.write(s);
            bw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
