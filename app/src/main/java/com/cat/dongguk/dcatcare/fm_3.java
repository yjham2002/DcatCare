package com.cat.dongguk.dcatcare;


import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class fm_3 extends Fragment implements Button.OnClickListener{

    private static final String notice_url = "http://yjham2002.woobi.co.kr/dcat/host.php?tr=111";
    private static final String admin_url = "http://yjham2002.woobi.co.kr/dcat/host.php?tr=106&mac=";

    private TextView mac, ver, ver2, notice;

    private Button manage;

    private String vername, vercode, notice_c, maca;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View rootView = inflater.inflate(R.layout.fm_3, container, false);

        WifiManager manager = (WifiManager) getActivity().getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = manager.getConnectionInfo();
        vername = "오류";
        try {
            PackageInfo pinfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
            vername = pinfo.versionName;
            vercode = Integer.toString(pinfo.versionCode);
        }catch(Exception e){}

        mac = (TextView)rootView.findViewById(R.id.textView2);
        ver = (TextView)rootView.findViewById(R.id.textView3);
        ver2 = (TextView)rootView.findViewById(R.id.textView4);
        notice = (TextView)rootView.findViewById(R.id.notice);
        manage = (Button)rootView.findViewById(R.id.info);

        maca = info.getMacAddress();
        mac.setText(maca);
        ver.setText(vername);
        ver2.setText(vercode);

        manage.setOnClickListener(this);

        Communicator comm = new Communicator();
        comm.getHttp(notice_url, new Handler() {
            @Override
            public void handleMessage(Message msg) {
                notice_c = msg.getData().getString("jsonString");
                notice.setText(notice_c);
            }
        });

        return rootView;
    }

    public void onClick(View v)
    {
        switch(v.getId()){
            case R.id.info:
                Communicator comm = new Communicator();
                comm.getHttp(admin_url+maca, new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        if(Integer.parseInt(msg.getData().getString("jsonString"))==1){
                            Intent i = new Intent(getActivity(), adminActivity.class);
                            startActivity(i);
                        }
                    }
                });
                break;
            default: break;
        }
    }
}
