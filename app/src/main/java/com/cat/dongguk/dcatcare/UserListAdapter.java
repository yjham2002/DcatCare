package com.cat.dongguk.dcatcare;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class UserListAdapter extends BaseAdapter {

    private String mac;

    public static final String url = "http://yjham2002.woobi.co.kr/dcat/";
    public static final String delurl = "http://yjham2002.woobi.co.kr/dcat/host.php?tr=105&id=";

    public Context mContext = null;
    public ArrayList<UserData> mListData = new ArrayList<>();

    private String fName;

    public UserListAdapter(Context mContext) {
        super();
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return mListData.size();
    }
    @Override
    public Object getItem(int position) {
        return mListData.get(position);
    }
    @Override
    public long getItemId(int position) {
        return position;
    }

    public void addItem(int id, String mac, String date,String text, String img_url, int cat, String like){
        UserData addInfo = new UserData();
        addInfo.id = id;
        addInfo.mac = mac;
        addInfo.date = date;
        addInfo.cat = cat;
        addInfo.like = like;
        addInfo.text = text;
        addInfo.img_url = img_url;
        mListData.add(addInfo);
    }

    public void dataChange(){
        this.notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
            holder = new ViewHolder();

            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_item_02, null);

            WifiManager manager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
            WifiInfo info = manager.getConnectionInfo();
            mac = info.getMacAddress();

            holder.img = (ImageView) convertView.findViewById(R.id.img);
            holder.name = (TextView) convertView.findViewById(R.id.name);
            holder.desc = (TextView) convertView.findViewById(R.id.desc);
            holder.status = (TextView) convertView.findViewById(R.id.status);
            holder.del = (Button) convertView.findViewById(R.id.delete);
            convertView.setTag(holder);

        final UserData mData = mListData.get(position);
        Picasso.with(mContext).load(url + mData.img_url).into(holder.img);
        holder.name.setText(mData.text.replaceAll("_", " "));
        if(MainActivity.catNames.size()<=mData.cat) fName = "오류";
        else fName = MainActivity.catNames.get(mData.cat);
        holder.desc.setText(fName);

        java.text.SimpleDateFormat format = new java.text.SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        try{
            holder.status.setText(TIME_MAXIMUM.calculateTime(format.parse(mData.date)));
        }catch(java.text.ParseException e){
            holder.status.setText(mData.date);
        }
        holder.del.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v){
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setMessage("정말 삭제하실거예요...?");
                builder.setCancelable(true);
                builder.setPositiveButton("취소", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                })
                        .setNeutralButton("확인", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Communicator comm = new Communicator();
                                comm.getHttp(delurl+mData.id+"&mac="+mac, new Handler(){});
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });
        holder.del.setFocusable(false);
        holder.del.setVisibility(mac.equals(mData.mac)? View.VISIBLE : View.GONE);

        return convertView;
    }

    private class ViewHolder {
        public ImageView img;
        public Button del;
        public TextView name, desc, status;
    }

}