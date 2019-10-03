package com.example.admin.browser;

import android.content.*;
import android.graphics.*;
import android.os.*;
import android.support.v7.app.*;
import android.view.*;
import android.widget.*;
import java.io.*;
import java.util.*;

public class StarActivity extends AppCompatActivity {
    private View history;
    private ImageView add;
    private ImageView save;
    private ImageView delete;
    private ImageView decide;
    private Button del_txt;
    private Button save_txt;
    private Button add_txt;
    private List<Myfile> fileList = new LinkedList<Myfile>();
    private FileAdapter adapter = null;
    private String url;
    private ListView listView;
    private boolean isCilck = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_star);
        history = (View)findViewById(R.id.history);
        add = (ImageView)findViewById(R.id.add);
        save = (ImageView)findViewById(R.id.save);
        history.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction()==MotionEvent.ACTION_DOWN){
                    v.setAlpha(.2f);
                }else if(event.getAction()==MotionEvent.ACTION_UP){
                    v.setAlpha(1);
                }
                return false;
            }
        });
        history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(StarActivity.this,HistoryActivity.class);
                startActivityForResult(intent,1);
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(StarActivity.this,SaveActivity.class);
                startActivity(intent);
            }
        });
    }
    protected void onResume() {
        adapter=new FileAdapter((LinkedList<Myfile>)fileList,StarActivity.this);
        listView=(ListView) findViewById(R.id.list);
        delete = (ImageView)findViewById(R.id.delete);
        decide = (ImageView)findViewById(R.id.decide);
        del_txt = (Button)findViewById(R.id.del_txt);
        add_txt = (Button)findViewById(R.id.add_txt);
        save_txt = (Button)findViewById(R.id.save_txt);
        delete.setVisibility(View.GONE);
        decide.setVisibility(View.GONE);
        del_txt.setVisibility(View.GONE);
        add_txt.setVisibility(View.VISIBLE);
        save_txt.setVisibility(View.VISIBLE);
        save.setVisibility(View.VISIBLE);
        add.setVisibility(View.VISIBLE);
        isCilck = true;
        listView.setAdapter(adapter);
        adapter.clear();
        File file = new File(Environment.getExternalStorageDirectory() + "/Mybrowser/save");
        for (File file2 : file.listFiles()) {
            adapter.add(new Myfile(file2.getName(),Environment.getExternalStorageDirectory() + "/Mybrowser/save/" + file2.getName() + "/favicon.ico"));
        }
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Adapter adpter=parent.getAdapter();
                Myfile mfile = (Myfile)adpter.getItem(position);
                File file = new File(Environment.getExternalStorageDirectory() + "/Mybrowser/save/" + mfile.getName() + "/url.txt");
                try {
                    BufferedReader br = new BufferedReader(new FileReader(file));
                    url = br.readLine();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Intent intent=new Intent();
                intent.putExtra("url",url);
                setResult(RESULT_OK,intent);
                StarActivity.this.finish();
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Adapter adpter=parent.getAdapter();
                Myfile mfile = (Myfile)adpter.getItem(position);
                adapter=new FileAdapter((LinkedList<Myfile>)fileList,StarActivity.this);
                listView.setAdapter(adapter);
                adapter.show();
                if(isCilck) {
                    delete.setVisibility(View.VISIBLE);
                    decide.setVisibility(View.VISIBLE);
                    del_txt.setVisibility(View.VISIBLE);
                    add_txt.setVisibility(View.GONE);
                    save_txt.setVisibility(View.GONE);
                    save.setVisibility(View.GONE);
                    add.setVisibility(View.GONE);
                }else{
                    delete.setVisibility(View.GONE);
                    decide.setVisibility(View.GONE);
                    del_txt.setVisibility(View.GONE);
                    add_txt.setVisibility(View.VISIBLE);
                    save_txt.setVisibility(View.VISIBLE);
                    save.setVisibility(View.VISIBLE);
                    add.setVisibility(View.VISIBLE);
                }
                isCilck = !isCilck;
                return true;
            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(StarActivity.this);
                builder.setTitle("删除书签？");
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        adapter=new FileAdapter((LinkedList<Myfile>)fileList,StarActivity.this);
                        listView.setAdapter(adapter);
                        adapter.del();
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
        decide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        super.onResume();
    }
    protected void onActivityResult(int requestCode,int resultCode,Intent data){
        if(requestCode==1){
            if(resultCode==RESULT_OK){
                Intent intent=new Intent();
                intent.putExtra("url",data.getStringExtra("url"));
                setResult(RESULT_OK,intent);
                StarActivity.this.finish();
            }
        }
    }
    public void onBackPressed(){
        if(isCilck) {
            super.onBackPressed();
        }else{
            adapter=new FileAdapter((LinkedList<Myfile>)fileList,StarActivity.this);
            listView.setAdapter(adapter);
            adapter.show();
            delete.setVisibility(View.GONE);
            decide.setVisibility(View.GONE);
            del_txt.setVisibility(View.GONE);
            add_txt.setVisibility(View.VISIBLE);
            save_txt.setVisibility(View.VISIBLE);
            save.setVisibility(View.VISIBLE);
            add.setVisibility(View.VISIBLE);
        }
        isCilck = !isCilck;
    }
}
class Myfile {
    private String name;
    private String icon;
    private boolean isCilck = true;
    private boolean isSelect = true;
    public Myfile(String name,String icon){
        this.name= name;
        this.icon = icon;
    }
    public String getName(){
        return name;
    }
    public String getIcon(){
        return icon;
    }
    public boolean getisCilck(){
        return isCilck;
    }
    public void setCilck(){
        isCilck = !isCilck;
    }
    public boolean getisSelect(){
        return isSelect;
    }
    public void setSelect(){
        isSelect=!isSelect;
    }
}
class FileAdapter extends BaseAdapter {
    private Context mContext;
    private LinkedList<Myfile> mData;
    private ViewHolder holder = null;
    public FileAdapter() {}
    public FileAdapter(LinkedList<Myfile> mData, Context mContext) {
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
    public LinkedList<Myfile> getmData(){
        return mData;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            convertView = LayoutInflater.from(mContext).inflate(R.layout.listitem,parent,false);
            final Myfile file = mData.get(position);
            holder = new ViewHolder();
            holder.txt_content = (TextView) convertView.findViewById(R.id.save_name);
            holder.icon_content = (ImageView) convertView.findViewById(R.id.save_icon);
            holder.edit = (ImageView) convertView.findViewById(R.id.edit);
            holder.ok = (ImageView) convertView.findViewById(R.id.ok);
            final ViewHolder finalHolder = holder;
            if(file.getisCilck()){
                holder.edit.setVisibility(View.GONE);
                holder.ok.setVisibility(View.GONE);
            }else{
                holder.edit.setVisibility(View.VISIBLE);
                holder.ok.setVisibility(View.VISIBLE);
            }
            holder.edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    File file = new File(Environment.getExternalStorageDirectory() + "/Mybrowser/save/" + finalHolder.txt_content.getText() + "/url.txt");
                    try {
                        BufferedReader br = new BufferedReader(new FileReader(file));
                        Intent intent=new Intent(mContext,SaveActivity.class);
                        intent.putExtra("url",br.readLine());
                        intent.putExtra("websitename", finalHolder.txt_content.getText());
                        mContext.startActivity(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            holder.ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ImageView vv = (ImageView)v;
                    if(file.getisSelect()){
                        vv.setColorFilter(Color.parseColor("#1abc9c"));
                    }else{
                        vv.setColorFilter(Color.parseColor("#000000"));
                    }
                    file.setSelect();
                }
            });
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }
        holder.txt_content.setText(mData.get(position).getName());
        Bitmap bitmap = getLoacalBitmap(mData.get(position).getIcon());
        holder.icon_content.setImageBitmap(bitmap);
        return convertView;
    }
    private class ViewHolder{
        TextView txt_content;
        ImageView icon_content;
        ImageView edit;
        ImageView ok;
    }
    public void add(Myfile data) {
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
    public void show(){
        if(mData!=null){
            for(Myfile file : mData){
                file.setCilck();
            }
        }
        notifyDataSetChanged();
    }
    public void del(){
        if(mData!=null){
            Iterator<Myfile> it=mData.iterator();
            while(it.hasNext()) {
                Myfile mf = it.next();
                if(!mf.getisSelect()) {
                    File file = new File(Environment.getExternalStorageDirectory() + "/Mybrowser/save/" + mf.getName());
                    for(File file2:file.listFiles()){
                        file2.delete();
                    }
                    file.delete();
                    it.remove();
                }
            }
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