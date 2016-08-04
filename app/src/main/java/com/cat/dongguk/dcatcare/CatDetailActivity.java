package com.cat.dongguk.dcatcare;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import uk.co.senab.photoview.PhotoViewAttacher;

public class CatDetailActivity extends Activity implements View.OnClickListener{

    public static final String url = "http://yjham2002.woobi.co.kr/dcat/img/";

    public static final String url2 = "http://yjham2002.woobi.co.kr/dcat/";

    public PhotoViewAttacher mAttacher;

    private Button back;

    private ImageView iv;

    private TextView name;

    @Override
    public void onClick(View v){
        switch(v.getId()){
            case R.id.button: finish(); break;
            default: break;
        }
    }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            requestWindowFeature(Window.FEATURE_NO_TITLE);

            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_cat);

            back = (Button)findViewById(R.id.button);

            back.setOnClickListener(this);

            iv = (ImageView)findViewById(R.id.imageView);

            name = (TextView)findViewById(R.id.name);

            Intent intent = getIntent();
            Bundle set = intent.getExtras();
            name.setText(set.getString("name").replaceAll("_"," "));
            if(set.getBoolean("mode")) Picasso.with(this).load(url2 + set.getString("img_url")).into(iv);
            else Picasso.with(this).load(url + set.getString("img_url")).into(iv);

            mAttacher = new PhotoViewAttacher(iv);
            mAttacher.update();
        }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

}
