package com.cat.dongguk.dcatcare;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

public class adminActivity extends Activity implements View.OnClickListener{

    private Button back;

    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.back: finish(); break;
            default: break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        back = (Button)findViewById(R.id.back);

        back.setOnClickListener(this);
    }
}
