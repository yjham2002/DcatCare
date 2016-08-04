package com.cat.dongguk.dcatcare;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.DynamicDrawableSpan;
import android.text.style.ImageSpan;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends FragmentActivity implements View.OnClickListener{

    private static final String url = "http://yjham2002.woobi.co.kr/dcat/host.php?tr=100";

    public static ArrayList<String> catNames;

    MainActivity.SectionsPagerAdapter mSectionsPagerAdapter;
    ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSectionsPagerAdapter = new MainActivity.SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        SharedPreferences prefs = getSharedPreferences("dcatcare", Context.MODE_PRIVATE);

        if(prefs.getBoolean("tutorial", true)){
            Intent i = new Intent(MainActivity.this, tutorialActivity.class);
            startActivity(i);
        }

    }

    @Override
    public void onResume(){
        loadList();
        super.onResume();
    }

    @Override
    public void onClick(View v){
        switch(v.getId()){
            default: break;
        }
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment temp;
            switch(position){
                case 0: temp = new fm_0(); break;
                case 1: temp = new fm_1(); break;
                case 2: temp = new fm_2(); break;
                case 3: temp = new fm_3(); break;
                default: temp = null; break;
            }
            return temp;
        }

        @Override
        public int getCount() {
            return 4;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Drawable myDrawable = null;
            String title = null;
            switch (position) {
                case 0:
                    myDrawable = getResources().getDrawable(R.drawable.pager_a);
                    title = "클라우드";
                    break;
                case 1:
                    myDrawable = getResources().getDrawable(R.drawable.pager_b);
                    title = "오는 김에";
                    break;
                case 2:
                    myDrawable = getResources().getDrawable(R.drawable.pager_c);
                    title = "가는 김에";
                    break;
                case 3:
                    myDrawable = getResources().getDrawable(R.drawable.pager_d);
                    title = "가는 김에";
                    break;
                default: break;
            }
            SpannableStringBuilder sb = new SpannableStringBuilder("   "); // space added before text for convenience
            try {
                myDrawable.setBounds(1, 1, myDrawable.getIntrinsicWidth(), myDrawable.getIntrinsicHeight());
                ImageSpan span = new ImageSpan(myDrawable, DynamicDrawableSpan.ALIGN_BASELINE);
                sb.setSpan(span, 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            } catch (Exception e) {
            }
            return sb;
        }
    }

    public boolean mFlag;

    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            if(msg.what == 0){
                mFlag=false;
            }
        }
    };

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)){
            if(!mFlag) {
                Toast.makeText(getApplicationContext(), "뒤로 버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT).show();
                mFlag = true;
                mHandler.sendEmptyMessageDelayed(0, 2000);
                return false;
            } else {
                finish();
                System.exit(0);
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    public void loadList(){
        Communicator comm = new Communicator();
        comm.getHttp(url, new Handler(){
            public void handleMessage(Message msg){
                String jsonString = msg.getData().getString("jsonString");
                catNames = new ArrayList<String>();
                catNames.clear();
                catNames.add("전체");
                try {
                    JSONArray json_arr = new JSONArray(jsonString);
                    for(int i = json_arr.length()-1; i>=0; i--){
                        JSONObject json_list = json_arr.getJSONObject(i);
                        catNames.add(json_list.getString("name"));
                    }
                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(), "냥이들 이름을 불러오는 중 오류가 발생했어요.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

}
