package com.example.admin.browser;
import android.content.*;
import android.support.v4.widget.*;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.*;
import android.widget.*;

public class SettingsActivity extends AppCompatActivity{
    private View frame;
    private View shezhizhuye;
    private View popupWindow_view;
    private PopupWindow popupWindow;
    private EditText homepage;
    private SharedPreferences preferences;
    private Button confirm;
    private Button cancel;
    private String homepageurl = "http://www.baidu.com";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        preferences = getSharedPreferences("homepage",MODE_PRIVATE);
        frame = (View)findViewById(R.id.frame);
        shezhizhuye = (View)findViewById(R.id.shezhizhuye);
        shezhizhuye.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow_view = getLayoutInflater().inflate(R.layout.shezhizhuye_layout, null, false);
                popupWindow = new PopupWindow(popupWindow_view, LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT , true);
                popupWindow.setOutsideTouchable(false);
                popupWindow.setAnimationStyle(R.style.anim_menu_bottombar);
                popupWindow.showAtLocation(frame, Gravity.CENTER,0,0);
                homepage = (EditText)popupWindow_view.findViewById(R.id.homepage);
                homepage.setText(preferences.getString("homepage",homepageurl));
                confirm = (Button)popupWindow_view.findViewById(R.id.confirm);
                cancel=(Button)popupWindow_view.findViewById(R.id.cancel);
                confirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        homepageurl = homepage.getText().toString();
                        if(!homepageurl.startsWith("http://")){
                            homepageurl = "http://"+homepageurl;
                        }
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("homepage",homepageurl);
                        editor.commit();
                        popupWindow.dismiss();
                    }
                });
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popupWindow.dismiss();
                    }
                });
            }
        });
        shezhizhuye.setOnTouchListener(new View.OnTouchListener() {
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
    }
    public void onBackPressed(){
        Intent intent=new Intent();
        intent.putExtra("homepage",homepageurl);
        setResult(RESULT_OK,intent);
        finish();
    }
}
