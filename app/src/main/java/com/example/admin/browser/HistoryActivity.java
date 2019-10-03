package com.example.admin.browser;

import android.content.*;
import android.database.*;
import android.database.sqlite.*;
import android.graphics.*;
import android.support.v7.app.*;
import android.os.*;
import android.view.*;
import android.widget.*;
import java.io.*;
import java.util.*;

public class HistoryActivity extends AppCompatActivity {
    private List<HMyfile> fileList = new LinkedList<HMyfile>();
    private HFileAdapter adapter = null;
    private View clean;
    private MySQLite msql;
    private SQLiteDatabase mDbWriter;
    private String url;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
    }
    protected void onResume() {
        adapter=new HFileAdapter((LinkedList<HMyfile>)fileList,HistoryActivity.this);
        ListView listView=(ListView) findViewById(R.id.list);
        listView.setAdapter(adapter);
        adapter.clear();
        msql = DbHelpBussiness.getInstance(HistoryActivity.this).getDbHelper();
        mDbWriter = msql.getWritableDatabase();
        Cursor mCursor = mDbWriter.query("history_db", null, null, null, null, null, null);
        while(mCursor.moveToNext()){
            File file = new File(Environment.getExternalStorageDirectory() + "/Mybrowser/history/" + mCursor.getString(1) + "/url.txt");
            try {
                BufferedReader br = new BufferedReader(new FileReader(file));
                adapter.add(new HMyfile(mCursor.getString(1),Environment.getExternalStorageDirectory() + "/Mybrowser/history/" + mCursor.getString(1) + "/favicon.ico",br.readLine()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Adapter adpter=parent.getAdapter();
                HMyfile mfile = (HMyfile)adpter.getItem(position);
                File file = new File(Environment.getExternalStorageDirectory() + "/Mybrowser/history/" + mfile.getName() + "/url.txt");
                try {
                    BufferedReader br = new BufferedReader(new FileReader(file));
                    url = br.readLine();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Intent intent=new Intent();
                intent.putExtra("url",url);
                setResult(RESULT_OK,intent);
                HistoryActivity.this.finish();
            }
        });
        clean = (View)findViewById(R.id.clean);
        clean.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(HistoryActivity.this);
                builder.setTitle("清空历史记录？");
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Cursor mCursor = mDbWriter.query("history_db", null, null, null, null, null, null);
                        if(mCursor.moveToFirst()) {
                            msql.delete(mDbWriter);
                            File file = new File(Environment.getExternalStorageDirectory() + "/Mybrowser/history/");
                            for(File file2:file.listFiles()){
                                fdel(file2);
                            }
                            adapter.clear();
                        }
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
        super.onResume();
    }
    public void fdel(File file){
        if(file.isDirectory()) {
            for (File file2 : file.listFiles()) {
                fdel(file2);
            }
        }
        file.delete();
    }
}
class HMyfile {
    private String name;
    private String icon;
    private String url;
    public HMyfile(String name,String icon,String url){
        this.name= name;
        this.icon = icon;
        this.url = url;
    }
    public String getName(){
        return name;
    }
    public String getIcon(){
        return icon;
    }
    public String geturl(){
        return url;
    }
}
class HFileAdapter extends BaseAdapter {
    private Context mContext;
    private LinkedList<HMyfile> mData;
    public HFileAdapter() {}
    public HFileAdapter(LinkedList<HMyfile> mData, Context mContext) {
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
        ViewHolder holder = null;
        if(convertView == null){
            convertView = LayoutInflater.from(mContext).inflate(R.layout.historyitem,parent,false);
            holder = new ViewHolder();
            holder.txt_content = (TextView) convertView.findViewById(R.id.history_name);
            holder.icon_content = (ImageView) convertView.findViewById(R.id.history_icon);
            holder.txt_content2 = (TextView) convertView.findViewById(R.id.history_url);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }
        holder.txt_content.setText(mData.get(position).getName());
        holder.txt_content2.setText(mData.get(position).geturl());
        Bitmap bitmap = getLoacalBitmap(mData.get(position).getIcon());
        holder.icon_content.setImageBitmap(bitmap);
        return convertView;
    }
    private class ViewHolder{
        TextView txt_content;
        ImageView icon_content;
        TextView txt_content2;
    }
    public void add(HMyfile data) {
        if (mData == null) {
            mData = new LinkedList<>();
        }
        mData.addFirst(data);
        notifyDataSetChanged();
    }
    public void clear() {
        if(mData != null) {
            mData.clear();
        }
        notifyDataSetChanged();
    }
    private Bitmap getLoacalBitmap(String url) {
        try {
            FileInputStream fis = new FileInputStream(url);
            return BitmapFactory.decodeStream(fis);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}