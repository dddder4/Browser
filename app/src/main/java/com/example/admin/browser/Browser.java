package com.example.admin.browser;
import android.app.*;
import android.content.*;
import android.content.pm.*;
import android.database.*;
import android.database.sqlite.*;
import android.graphics.*;
import android.os.*;
import android.support.v4.app.*;
import android.support.v4.widget.*;
import android.support.v7.app.*;
import android.support.v7.app.AlertDialog;
import android.text.*;
import android.view.*;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.*;
import android.webkit.*;
import android.webkit.CookieManager;
import android.widget.*;
import java.io.*;
import java.net.*;
import java.util.*;

public class Browser extends AppCompatActivity implements Runnable{
    private EditText weburl;
    private WebView webshow;
    private Button searchurl;
    private ProgressBar pg1;
    private SharedPreferences preferences;
    private View background;
    private PopupWindow popupWindow;
    private PopupWindow popupWindow2;
    private PopupWindow popupWindow3;
    private ImageView menu;
    private ImageView home;
    private ImageView back;
    private ImageView forward;
    private ImageView settings;
    private ImageView computer;
    private ImageView save;
    private ImageView download;
    private ImageView star;
    private ImageView windows;
    private ImageView clean;
    private ImageView add;
    private ImageView sback;
    private View popupWindow_view;
    private View popupWindow_view2;
    private View popupWindow_view3;
    private View menubar;
    private View main;
    private String homepage;
    private String geturl;
    private String gettitle;
    private String lasttitle = "";
    private String burl;
    private String fileName;
    private boolean isClick = true;
    private boolean isFirst = true;
    private boolean shalladd = true;
    private boolean ischecked = false;
    private Download[] dl = new Download[3000];
    private int num = 0;
    private File savedir;
    private File historydir;
    private File dldir;
    private ListView listView;
    private ListView dlistView;
    private List<MMyfile> fileList = new LinkedList<MMyfile>();
    private List<DMyfile> dfileList = new LinkedList<DMyfile>();
    private MFileAdapter adapter = null;
    private DFileAdapter dadapter = null;
    private MyDSQLite sql;
    private SQLiteDatabase mDbWriter;
    private Handler handler;
    private String[] PERMISSIONS_STORAGE = {
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE" };
    private final int REQUEST_EXTERNAL_STORAGE = 1;
    public void verifyStoragePermissions(Activity activity) {
        try {
            int permission = ActivityCompat.checkSelfPermission(activity,
                    "android.permission.WRITE_EXTERNAL_STORAGE");
            if (permission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE,REQUEST_EXTERNAL_STORAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browser);
        verifyStoragePermissions(Browser.this);
        handler = new Handler();
        popupWindow_view3 = getLayoutInflater().inflate(R.layout.activity_download, null, false);
        popupWindow3 = new PopupWindow(popupWindow_view3, LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT , true);
        popupWindow3.setOutsideTouchable(false);
        dlistView=(ListView) popupWindow_view3.findViewById(R.id.list);
        dadapter =new DFileAdapter((LinkedList<DMyfile>)dfileList,Browser.this);
        Refresh r = new Refresh();
        r.start();
        dlistView.setAdapter(dadapter);
        sql = DlDbHelpBussiness.getInstance(Browser.this).getDbHelper();
        mDbWriter = sql.getWritableDatabase();
        Cursor cursor = mDbWriter.query("download_db",null,null,null,null,null,null);
        if(cursor.moveToFirst()) {
            for(int i=0;i<cursor.getCount();i+=3){
                cursor.move(i);
                String filepath=cursor.getString(2);
                filepath = filepath.substring(filepath.lastIndexOf("/")+1);
                dadapter.add(new DMyfile(filepath));
            }
        }
        savedir = new File(Environment.getExternalStorageDirectory() + "/Mybrowser/save/");
        savedir.mkdirs();
        historydir = new File(Environment.getExternalStorageDirectory() + "/Mybrowser/history/");
        historydir.mkdirs();
        dldir = new File(Environment.getExternalStorageDirectory() + "/Mybrowser/download/");
        dldir.mkdirs();
        preferences = getSharedPreferences("data",MODE_PRIVATE);
        String url = preferences.getString("url","");
        homepage = preferences.getString("homepage","http://www.baidu.com");
        background = (View)findViewById(R.id.gray_layout);
        webshow = (WebView) findViewById(R.id.webshow);
        pg1=(ProgressBar) findViewById(R.id.progressBar1);
        menu = (ImageView)findViewById(R.id.caidan);
        home = (ImageView)findViewById(R.id.zhuye);
        back = (ImageView)findViewById(R.id.houtui);
        forward = (ImageView)findViewById(R.id.qianjin);
        windows = (ImageView)findViewById(R.id.chuangkou);
        menubar = (View)findViewById(R.id.menubar);
        main = (View)findViewById(R.id.main);
        background.setAlpha(0);
        weburl = (EditText) findViewById(R.id.weburl);
        weburl.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        weburl.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((actionId == 0 || actionId == 3) && event != null) {
                    String url = weburl.getText().toString();
                    String surl = url;
                    if (!url.startsWith("http")) {
                        url = "http://" + url;
                    }
                    Getcode g = new Getcode(url);
                    g.start();
                    try {
                        g.join();
                        if(g.getRspcode()==0){
                            url = "https://www.baidu.com/s?wd=" + surl;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    webshow.loadUrl(url);
                }
                return false;
            }
        });
        weburl.setOnFocusChangeListener(new android.view.View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    searchurl.setText("前往");
                    weburl.setText(webshow.getUrl());
                    weburl.selectAll();
                } else {
                    searchurl.setText("刷新");
                    ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(weburl.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    weburl.setText(webshow.getTitle());
                }
            }
        });
        webshow.getSettings().setJavaScriptEnabled(true);
        webshow.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webshow.getSettings().setBuiltInZoomControls(true);
        webshow.getSettings().setSupportZoom(true);
        webshow.getSettings().setUseWideViewPort(true);
        webshow.getSettings().setLoadWithOverviewMode(true);
        webshow.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        webshow.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                weburl.setText(webshow.getUrl());
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("url",weburl.getText().toString());
                editor.commit();
                return true;
            }
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                gettitle = webshow.getTitle();
                try {
                    geturl = webshow.getUrl();
                    Getcode g = new Getcode(geturl);
                    g.start();
                    g.join();
                    MySQLite msql = DbHelpBussiness.getInstance(Browser.this).getDbHelper();
                    SQLiteDatabase mDbWriter = msql.getWritableDatabase();
                    Cursor mCursor = mDbWriter.query("history_db", null, null, null, null, null, null);
                    if(mCursor.moveToLast()) {
                        int nameIndex = mCursor.getColumnIndex("title");
                        lasttitle = mCursor.getString(nameIndex);
                    }
                    if(!gettitle.equals(lasttitle)&&g.getRspcode()>=200&&g.getRspcode()<300){
                        File history = new File(Environment.getExternalStorageDirectory() + "/Mybrowser/history/" + webshow.getTitle());
                        history.mkdirs();
                        Thread t = new Thread(Browser.this);
                        t.start();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        webshow.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100) {
                    pg1.setVisibility(View.GONE);
                    searchurl.setText("刷新");
                } else {
                    pg1.setVisibility(View.VISIBLE);
                    pg1.setProgress(newProgress);
                    searchurl.setText("停止");
                    weburl.setText(webshow.getUrl());
                    if(webshow.canGoForward()){
                        forward.setVisibility(View.VISIBLE);
                    }else{
                        forward.setVisibility(View.INVISIBLE);
                    }
                    if(webshow.canGoBack()){
                        back.setVisibility(View.VISIBLE);
                    }else{
                        back.setVisibility(View.INVISIBLE);
                    }
                    weburl.setText(webshow.getTitle());
                }
            }
        });
        webshow.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                try {
                    dlistView=(ListView) popupWindow_view3.findViewById(R.id.list);
                    dadapter =new DFileAdapter((LinkedList<DMyfile>)dfileList,Browser.this);
                    dlistView.setAdapter(dadapter);
                    burl = url;
                    URL durl = new URL(url);
                    HttpURLConnection conn = (HttpURLConnection) durl.openConnection();
                    conn.setConnectTimeout(5000);
                    conn.setRequestMethod("GET");
                    final int length;
                    final int Threadnum = 3;
                    Getcode g = new Getcode(burl);
                    g.start();
                    g.join();
                    if(g.getRspcode()==200){
                        length = g.getLength()/Threadnum;
                        fileName = durl.getFile();
                        fileName = fileName.substring(fileName.lastIndexOf("/") + 1);
                        android.support.v7.app.AlertDialog.Builder builder = new AlertDialog.Builder(Browser.this);
                        builder.setTitle("下载文件");
                        builder.setMessage(fileName);
                        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                for(int i=0;i<Threadnum;i++){
                                    int start,end;
                                    start = i * length;
                                    end = (i+1) * length -1;
                                    if ((i + 1) == Threadnum)
                                        end = end * 2;
                                    dl[dadapter.getCount()*3+i] = new Download(burl,dldir.getAbsolutePath() + "/" + fileName, start,end,0);
                                    dl[dadapter.getCount()*3+i].start();
                                    sql.insert(mDbWriter,burl,dldir.getAbsolutePath() + "/" + fileName,start,end,0);
                                }
                                dadapter.add(new DMyfile(fileName));
                            }
                        });
                        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                        builder.show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        if(!url.equals("")){
            webshow.loadUrl(url);
            weburl.setText(webshow.getUrl());
        }
        searchurl = (Button) findViewById(R.id.searchurl);
        searchurl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(searchurl.getText().toString().equals("前往")) {
                    String url = weburl.getText().toString();
                    String surl = url;
                    if (!url.startsWith("http")) {
                        url = "http://" + url;
                    }
                    Getcode g = new Getcode(url);
                    g.start();
                    try {
                        g.join();
                        if(g.getRspcode()==0){
                            url = "https://www.baidu.com/s?wd=" + surl;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    webshow.loadUrl(url);
                }else if(searchurl.getText().toString().equals("刷新")){
                    webshow.reload();
                }else if(searchurl.getText().toString().equals("停止")){
                    webshow.stopLoading();
                }
                webshow.requestFocus();
            }
        });
        int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        menubar.measure(w, h);
        popupWindow_view = getLayoutInflater().inflate(R.layout.menu_layout, null, false);
        popupWindow_view.measure(w,h);
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int height2 = popupWindow_view.getMeasuredHeight();
                popupWindow = new PopupWindow(popupWindow_view, LinearLayout.LayoutParams.MATCH_PARENT,height2 , true);
                PopupWindowCompat.showAsDropDown(popupWindow, menu, 0, 0, Gravity.START);
                popupWindow_view.requestFocus();
                weburl.clearFocus();
                background.setAlpha(0.4f);
                menu.setImageResource(R.drawable.arrow);
                popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        background.setAlpha(0);
                        menu.setImageResource(R.drawable.menu);
                    }
                });
                settings = (ImageView)popupWindow_view.findViewById(R.id.settings);
                settings.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popupWindow.dismiss();
                        Intent intent=new Intent(Browser.this,SettingsActivity.class);
                        startActivityForResult(intent,1);
                    }
                });
                download = (ImageView)popupWindow_view.findViewById(R.id.download);
                download.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popupWindow.dismiss();
                        popupWindow3.setAnimationStyle(R.style.anim_menu_bottombar);
                        popupWindow3.showAtLocation(main, Gravity.CENTER,0,0);
                        ImageView clean = (ImageView)popupWindow_view3.findViewById(R.id.delete);
                        dlistView=(ListView) popupWindow_view3.findViewById(R.id.list);
                        dadapter =new DFileAdapter((LinkedList<DMyfile>)dfileList,Browser.this);
                        dlistView.setAdapter(dadapter);
                        clean.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                android.support.v7.app.AlertDialog.Builder builder = new AlertDialog.Builder(Browser.this);
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
                                            Cursor cursor = mDbWriter.query("download_db",null,null,null,null,null,null);
                                            if(cursor.moveToFirst()){
                                                for(int i=0;i<cursor.getCount();i++){
                                                    File file = new File(cursor.getString(2));
                                                    file.delete();
                                                }
                                            }
                                        }
                                        for(int i=0;i<dadapter.getCount()*3;i++){
                                            if(dl[i]!=null){
                                                dl[i].cancel();
                                                dl[i]=null;
                                            }
                                        }
                                        sql.clean(mDbWriter);
                                        dadapter.clear();
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
                });
                computer = (ImageView)popupWindow_view.findViewById(R.id.computer);
                if(preferences.getString("computerua","true").equals("false")&&isFirst){
                    System.out.println(111);
                    computer.setImageResource(R.drawable.computer_green);
                    ((TextView)popupWindow_view.findViewById(R.id.computertext)).setTextColor(Color.parseColor("#1abc9c"));
                    webshow.getSettings().setUserAgentString("Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US) AppleWebKit/534.20 (KHTML, like Gecko) Chrome/11.0.672.0 Safari/534.20");
                    isClick = !isClick;
                    isFirst = !isFirst;
                }else if(preferences.getString("computerua","true").equals("true")&&isFirst){
                    System.out.println(222);
                    computer.setImageResource(R.drawable.computer);
                    ((TextView)popupWindow_view.findViewById(R.id.computertext)).setTextColor(Color.parseColor("#000000"));
                    webshow.getSettings().setUserAgentString("Mozilla/5.0 (Linux; U; Android 6.0; zh-cn; PLK-TL00 Build/HONORPLK-TL00) AppleWebKit/537.36 (KHTML, like Gecko)Version/4.0 Chrome/37.0.0.0 MQQBrowser/6.0 Mobile Safari/537.36");
                    isFirst = !isFirst;
                }
                computer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popupWindow.dismiss();
                        if(isClick){
                            computer.setImageResource(R.drawable.computer_green);
                            ((TextView)popupWindow_view.findViewById(R.id.computertext)).setTextColor(Color.parseColor("#1abc9c"));
                            webshow.getSettings().setUserAgentString("Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US) AppleWebKit/534.20 (KHTML, like Gecko) Chrome/11.0.672.0 Safari/534.20");
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putString("computerua","false");
                            editor.commit();
                        }else{
                            computer.setImageResource(R.drawable.computer);
                            ((TextView)popupWindow_view.findViewById(R.id.computertext)).setTextColor(Color.parseColor("#000000"));
                            webshow.getSettings().setUserAgentString("Mozilla/5.0 (Linux; U; Android 6.0; zh-cn; PLK-TL00 Build/HONORPLK-TL00) AppleWebKit/537.36 (KHTML, like Gecko)Version/4.0 Chrome/37.0.0.0 MQQBrowser/6.0 Mobile Safari/537.36");
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putString("computerua","true");
                            editor.commit();
                        }
                        isClick = !isClick;
                        CookieSyncManager.createInstance(Browser.this);
                        CookieManager.getInstance().removeAllCookie();
                        webshow.clearCache(true);
                        webshow.loadUrl(webshow.getOriginalUrl());
                    }
                });
                save = (ImageView)popupWindow_view.findViewById(R.id.save);
                save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popupWindow.dismiss();
                        Intent intent=new Intent(Browser.this,SaveActivity.class);
                        intent.putExtra("url",webshow.getUrl());
                        intent.putExtra("websitename",webshow.getTitle());
                        startActivity(intent);
                    }
                });
                star = (ImageView)popupWindow_view.findViewById(R.id.star);
                star.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popupWindow.dismiss();
                        Intent intent=new Intent(Browser.this,StarActivity.class);
                        startActivityForResult(intent,2);
                    }
                });
            }
        });
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                webshow.requestFocus();
                webshow.loadUrl(homepage);
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                webshow.requestFocus();
                webshow.goBack();
            }
        });
        forward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                webshow.requestFocus();
                webshow.goForward();
            }
        });
        popupWindow_view2 = getLayoutInflater().inflate(R.layout.multiwindows, null, false);
        popupWindow2 = new PopupWindow(popupWindow_view2, LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT , true);
        windows.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow2.setOutsideTouchable(false);
                popupWindow2.showAtLocation(main, Gravity.CENTER,0,0);
                clean = (ImageView)popupWindow_view2.findViewById(R.id.clean);
                add = (ImageView)popupWindow_view2.findViewById(R.id.add);
                sback = (ImageView)popupWindow_view2.findViewById(R.id.back);
                adapter=new MFileAdapter((LinkedList<MMyfile>)fileList,Browser.this);
                listView=(ListView) popupWindow_view2.findViewById(R.id.multilist);
                listView.setAdapter(adapter);
                if(shalladd) {
                    webshow.buildDrawingCache();
                    adapter.add(new MMyfile(webshow.getTitle(), webshow.getDrawingCache(),webshow.getUrl()));
                //    webshow.destroyDrawingCache();
                    shalladd = false;
                }
                clean.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        adapter=new MFileAdapter((LinkedList<MMyfile>)fileList,Browser.this);
                        listView=(ListView) popupWindow_view2.findViewById(R.id.multilist);
                        listView.setAdapter(adapter);
                        adapter.clear();
                        popupWindow2.dismiss();
                        webshow.loadUrl(homepage);
                        shalladd = true;
                    }
                });
                add.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popupWindow2.dismiss();
                        webshow.loadUrl(homepage);
                        shalladd = true;
                    }
                });
                sback.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popupWindow2.dismiss();
                        if(adapter.getCount()>0) {
                            MMyfile mfile = (MMyfile) adapter.getItem(adapter.getCount() - 1);
                            webshow.loadUrl(mfile.getUrl());
                            adapter.del(mfile);
                        }else{
                            webshow.loadUrl(homepage);
                        }
                        shalladd = true;
                    }
                });
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Adapter adpter=parent.getAdapter();
                        MMyfile mfile = (MMyfile)adpter.getItem(position);
                        webshow.loadUrl(mfile.getUrl());
                        popupWindow2.dismiss();
                        adapter.del(mfile);
                        shalladd = true;
                    }
                });
            }
        });
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && webshow.canGoBack()) {
            webshow.goBack();
            return true;
        }
        if ((keyCode == KeyEvent.KEYCODE_BACK) && popupWindow2.isShowing()) {
            popupWindow2.dismiss();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    protected void onActivityResult(int requestCode,int resultCode,Intent data){
        if(requestCode==1){
            if(resultCode==RESULT_OK){
                homepage = data.getStringExtra("homepage");
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("homepage",data.getStringExtra("homepage"));
                editor.commit();
            }
        }else if(requestCode==2){
            if(resultCode==RESULT_OK){
                webshow.loadUrl(data.getStringExtra("url"));
            }
        }
    }
    @Override
    public void run() {
        try {
            URL url = new URL(geturl);
            URL iconurl = new URL("http://" + url.getHost() + "/favicon.ico");
            URLConnection conn = iconurl.openConnection();
            conn.setConnectTimeout(5000);
            File icon = new File(Environment.getExternalStorageDirectory() + "/Mybrowser/history/" + gettitle + "/favicon.ico");
            FileOutputStream fos = new FileOutputStream(icon);
            InputStream is = conn.getInputStream();
            int i;
            while((i = is.read())!=-1){
                fos.write(i);
            }
            File furl = new File(Environment.getExternalStorageDirectory() + "/Mybrowser/history/" + gettitle + "/url.txt");
            BufferedWriter bw = new BufferedWriter(new FileWriter(furl));
            bw.write(url.toString());
            bw.close();
            MySQLite msql = DbHelpBussiness.getInstance(Browser.this).getDbHelper();
            SQLiteDatabase mDbWriter = msql.getWritableDatabase();
            msql.insert(mDbWriter,gettitle);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private class DMyfile {
        private String name;
        private boolean isCilck = true;
        public DMyfile(String name){
            this.name= name;
        }
        public String getName(){
            return name;
        }
        public boolean getCilck(){
            return isCilck;
        }
        public void setCilck(){
            isCilck = !isCilck;
        }
        public void setCilckfalse(){
            isCilck = false;
        }
    }
    private class DFileAdapter extends BaseAdapter {
        private Context mContext;
        private LinkedList<DMyfile> mData;
        private ViewHolder holder = null;
        private boolean isCilck = false;
        private boolean up;
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
        public View getView(final int position, View convertView, ViewGroup parent) {
            if(convertView == null){
                convertView = LayoutInflater.from(mContext).inflate(R.layout.downloaditem,parent,false);
                holder = new ViewHolder();
                holder.txt_content = (TextView) convertView.findViewById(R.id.name);
                holder.icon_content = (ImageView) convertView.findViewById(R.id.use);
                holder.cancel = (ImageView)convertView.findViewById(R.id.delete);
                holder.pg = (ProgressBar)convertView.findViewById(R.id.progress);
                holder.pg_txt = (TextView)convertView.findViewById(R.id.pg_txt);
                final int position1 = position;
                holder.pg.setIndeterminate(false);
                up = false;
                Cursor cursor = mDbWriter.query("download_db",null,null,null,null,null,null);
                if(cursor.moveToFirst()) {
                    String url = "";
                    int now = 0;
                    for(int i=position1*3;i<position1*3+3;i++){
                        cursor.moveToFirst();
                        cursor.move(i);
                        url=cursor.getString(1);
                        now += cursor.getInt(5);
                    }
                    Getcode g = new Getcode(url);
                    g.start();
                    try {
                        g.join();
                        holder.pg.setProgress((int) ((float) now / (float) g.getLength() * 100));
                        holder.pg_txt.setText(holder.pg.getProgress() + "%");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                if(holder.pg.getProgress()>0&&holder.pg.getProgress()<100&&dl[position1*3]==null){
                    holder.icon_content.setImageResource(R.drawable.paly);
                    mData.get(position1).setCilckfalse();
                }else if(holder.pg.getProgress()==100){
                    holder.icon_content.setVisibility(View.INVISIBLE);
                    holder.icon_content.setClickable(false);
                }
                holder.icon_content.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ImageView vv = (ImageView)v;
                        if(mData.get(position1).getCilck()){
                            vv.setImageResource(R.drawable.paly);
                            for(int i = 0; i<3 ;i++){
                                dl[position1*3+i].cancel();
                                dl[position1*3+i]=null;
                            }
                        }else{
                            vv.setImageResource(R.drawable.pause);
                            Cursor cursor = mDbWriter.query("download_db",null,null,null,null,null,null);
                            if(cursor.moveToFirst()) {
                                for(int i=position1*3;i<position1*3+3;i++){
                                    cursor.moveToFirst();
                                    cursor.move(i);
                                    String url=cursor.getString(1);
                                    String filepath=cursor.getString(2);
                                    int start = cursor.getInt(3);
                                    int fin = cursor.getInt(4);
                                    int now = cursor.getInt(5);
                                    dl[i] = new Download(url,filepath,start,fin,now);
                                    dl[i].start();
                                }
                            }
                        }
                        mData.get(position1).setCilck();
                    }
                });
                holder.cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        android.support.v7.app.AlertDialog.Builder builder = new AlertDialog.Builder(Browser.this);
                        builder.setTitle("删除记录？");
                        builder.setMultiChoiceItems(new String[]{"同时删除文件"}, null, new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                                isCilck = isChecked;
                            }
                        });
                        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which){
                                Cursor cursor = mDbWriter.query("download_db",null,null,null,null,null,null);
                                String filepath = "";
                                int id=0;
                                if(cursor.moveToFirst()) {
                                    cursor.move(position1 * 3);
                                    filepath = cursor.getString(2);
                                    id = cursor.getInt(0);
                                }
                                if(isCilck){
                                    File file = new File(filepath);
                                    file.delete();
                                }
                                for(int i=0;i<3;i++){
                                    if(dl[position1*3+i]!=null){
                                        dl[position1*3+i].cancel();
                                        dl[position1*3+i]=null;
                                    }
                                    sql.del(mDbWriter,id+i);
                                }
                                del(mData.get(position1));
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
            TextView pg_txt;
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
        public void update(){
            up = true;
            notifyDataSetChanged();
        }
    }
    private class Download extends Thread{
        private String url;
        private String dlpath;
        private int start;
        private int end;
        private int now;
        private int count;
        private boolean goon = true;
        public Download(String url,String dlpath,int start,int end,int now){
            this.url = url;
            this.dlpath = dlpath;
            this.start = start;
            this.end = end;
            this.now = now;
        }
        public void run(){
            try {
                URL durl = new URL(url);
                HttpURLConnection conn = (HttpURLConnection) durl.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Range", "bytes=" + (start+now) + "-" + end);
                conn.setConnectTimeout(5000);
                if (conn.getResponseCode() == 206) {
                    InputStream is = conn.getInputStream();
                    RandomAccessFile raf = new RandomAccessFile(dlpath, "rwd");
                    raf.seek(start+now);
                    int len;
                    byte[] buffer = new byte[1024];
                    Cursor cursor = mDbWriter.query("download_db",null,null,null,null,null,null);
                    if(cursor.moveToFirst()){
                        for(int i=0;i<cursor.getCount();i++){
                            if(cursor.getString(1).equals(url)&&cursor.getInt(3)==start){
                                count = cursor.getInt(0);
                                break;
                            }
                            cursor.moveToNext();
                        }
                    }
                    while ((len = is.read(buffer)) != -1) {
                        if(!goon){
                            break;
                        }
                        raf.write(buffer, 0, len);
                        now+=len;
                        sql.change(mDbWriter,now,count);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        public void cancel(){
            goon = false;
        }
    }
    private Runnable updatelist = new Runnable() {
        @Override
        public void run() {
            dlistView=(ListView) popupWindow_view3.findViewById(R.id.list);
            dadapter =new DFileAdapter((LinkedList<DMyfile>)dfileList,Browser.this);
            dlistView.setAdapter(dadapter);
            dadapter.update();
        }
    };
    private class Refresh extends Thread{
        private long start = System.currentTimeMillis();
        private long end;
        public void run(){
            while(true){
                end=System.currentTimeMillis();
                if(end-start>3000){
                    start = end;
                    handler.post(updatelist);
                }
            }
        }
    }
}
class Getcode extends Thread{
    private String urlstring;
    private int rspcode = 0;
    private int length = 0;
    public Getcode(String url){
        this.urlstring = url;
    }
    @Override
    public void run() {
        try {
            URL url = new URL(urlstring);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.connect();
            rspcode = conn.getResponseCode();
            length = conn.getContentLength();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public int getRspcode(){
        return rspcode;
    }
    public int getLength(){
        return length;
    }
}
class MMyfile {
    private String name;
    private Bitmap icon;
    private String url;
    public MMyfile(String name,Bitmap icon,String url){
        this.name= name;
        this.icon = icon;
        this.url = url;
    }
    public String getName(){
        return name;
    }
    public Bitmap getIcon(){
        return icon;
    }
    public String getUrl(){
        return url;
    }
}
class MFileAdapter extends BaseAdapter {
    private Context mContext;
    private LinkedList<MMyfile> mData;
    private ViewHolder holder = null;
    public MFileAdapter() {}
    public MFileAdapter(LinkedList<MMyfile> mData, Context mContext) {
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.multiitem,parent,false);
            holder = new ViewHolder();
            holder.txt_content = (TextView) convertView.findViewById(R.id.title);
            holder.icon_content = (ImageView) convertView.findViewById(R.id.pic);
            holder.cancel = (ImageView)convertView.findViewById(R.id.cancel);
            final int position1 = position;
            holder.cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final MMyfile file = mData.get(position1);
                    del(file);
                }
            });
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }
        holder.txt_content.setText(mData.get(position).getName());
        holder.icon_content.setImageBitmap(mData.get(position).getIcon());
        return convertView;
    }
    private class ViewHolder{
        TextView txt_content;
        ImageView icon_content;
        ImageView cancel;
    }
    public void add(MMyfile data) {
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
    public void del(MMyfile file){
        if(mData!=null){
            mData.remove(file);
        }
        notifyDataSetChanged();
    }
}