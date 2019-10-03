package com.example.admin.browser;

import android.app.Activity;
import android.content.*;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import java.util.*;

public class DownloadActivity extends AppCompatActivity {
    private ImageView clean;
    private ListView listView;
    private List<DMyfile> fileList = new LinkedList<DMyfile>();
    private DFileAdapter adapter = null;
    private boolean ischecked = false;
    private MyDSQLite msql;
    private SQLiteDatabase mDbWriter;
    private String dlpath;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);
        dlpath = Environment.getExternalStorageDirectory() + "/Mybrowser/download/";
        clean = (ImageView)findViewById(R.id.delete);
        adapter=new DFileAdapter((LinkedList<DMyfile>)fileList,DownloadActivity.this);
        listView=(ListView) findViewById(R.id.list);
        listView.setAdapter(adapter);
     //   dlf = DlBussiness.getInstance(DownloadActivity.this,)
        msql = DlDbHelpBussiness.getInstance(DownloadActivity.this).getDbHelper();
        mDbWriter = msql.getWritableDatabase();
        Cursor mCursor = mDbWriter.query("download_db", null, null, null, null, null, null);
        while(mCursor.moveToNext()){
            adapter.add(new DMyfile(mCursor.getString(2),mCursor.getString(1)));
        }
        clean.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android.support.v7.app.AlertDialog.Builder builder = new AlertDialog.Builder(DownloadActivity.this);
                builder.setTitle("清空记录？");
                builder.setMultiChoiceItems(new String[]{"同时删除文件"}, null, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        ischecked = isChecked;
                    }
                });
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(ischecked){
                        }
                        msql.clean(mDbWriter);
                        adapter.clear();
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                builder.show();
            }
        });
    }
    private class DMyfile {
        private String name;
        private String url;
        private boolean isCilck = true;
        public DMyfile(String name,String url){
            this.name= name;
            this.url = url;
        }
        public String getName(){
            return name;
        }
        public String getUrl(){
            return url;
        }
        public boolean getCilck(){
            return isCilck;
        }
        public void setCilck(){
            isCilck = !isCilck;
        }
    }
    private class DFileAdapter extends BaseAdapter {
        private Context mContext;
        private LinkedList<DMyfile> mData;
        private ViewHolder holder = null;
        private boolean isCilck = false;
        public DFileAdapter() {}
        public DFileAdapter(LinkedList<DMyfile> mData, Context mContext) {
            this.mData = mData;
            this.mContext = mContext;
        }
        @Override
        public int getCount() {
            return mData.size();
        }
        @Override
        public Object getItem(int position) {
            return mData.get(position);
        }
        @Override
        public long getItemId(int position) {
            return position;
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null){
                convertView = LayoutInflater.from(mContext).inflate(R.layout.downloaditem,parent,false);
                holder = new ViewHolder();
                holder.txt_content = (TextView) convertView.findViewById(R.id.name);
                holder.icon_content = (ImageView) convertView.findViewById(R.id.use);
                holder.cancel = (ImageView)convertView.findViewById(R.id.delete);
                holder.pg = (ProgressBar)convertView.findViewById(R.id.progress);
                holder.pg.setProgress(0);
                final int position1 = position;
            //    final DownLoadFile downLoadFile = DlBussiness.getInstance().create(DownloadActivity.this,mData.get(position).getUrl(), dlpath + mData.get(position).getName(), 3);
                System.out.println(position1);
                holder.cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        android.support.v7.app.AlertDialog.Builder builder = new AlertDialog.Builder(DownloadActivity.this);
                        builder.setTitle("删除记录？");
                        builder.setMultiChoiceItems(new String[]{"同时删除文件"}, null, new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                                isCilck = isChecked;
                            }
                        });
                        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if(isCilck){
                                }
                                msql.clean(mDbWriter);
                            }
                        });
                        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                        builder.show();
                    }
                });
                convertView.setTag(holder);
            }else{
                holder = (ViewHolder) convertView.getTag();
            }
            holder.txt_content.setText(mData.get(position).getName());
            return convertView;
        }
        private class ViewHolder{
            TextView txt_content;
            ImageView icon_content;
            ImageView cancel;
            ProgressBar pg;
        }
        public void add(DMyfile data) {
            if (mData == null) {
                mData = new LinkedList<>();
            }
            mData.add(data);
            notifyDataSetChanged();
        }
        public void clear() {
            if(mData != null) {
                mData.clear();
            }
            notifyDataSetChanged();
        }
        public void del(DMyfile file){
            if(mData!=null){
                mData.remove(file);
            }
            notifyDataSetChanged();
        }
    }
}
