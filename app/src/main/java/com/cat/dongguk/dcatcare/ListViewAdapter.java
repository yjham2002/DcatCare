package com.cat.dongguk.dcatcare;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ListViewAdapter extends BaseAdapter {

    public static final String url = "http://yjham2002.woobi.co.kr/dcat/img/";

    public Context mContext = null;
    public ArrayList<CatData> mListData = new ArrayList<>();

    public ListViewAdapter(Context mContext) {
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

    public void addItem(int id, String name, String desc,int status, String img_url){
        CatData addInfo = new CatData();
        addInfo.id = id;
        addInfo.name = name;
        addInfo.desc = desc;
        addInfo.status = status;
        addInfo.img_url = img_url;
        mListData.add(addInfo);
    }

    public void dataChange(){
        this.notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();

            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_item_01, null);

            holder.img = (ImageView) convertView.findViewById(R.id.img);
            holder.name = (TextView) convertView.findViewById(R.id.name);
            holder.desc = (TextView) convertView.findViewById(R.id.desc);
            holder.status = (TextView) convertView.findViewById(R.id.status);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }
        final CatData mData = mListData.get(position);
        Picasso.with(mContext).load(url + mData.img_url).into(holder.img);
        holder.name.setText(mData.name);
        holder.desc.setText(mData.desc);
        String stat;
        switch(mData.status){
            case 0: stat = "건강"; break;
            case 1: stat = "예민"; break;
            case 2: stat = "영양과다"; break;
            case 3: stat = "영양부족"; break;
            case 4: stat = "▶◀"; break;
            case 5: stat = "임신"; break;
            case 6: stat = "발정기"; break;
            case 7: stat = "부상"; break;
            default: stat = "오류"; break;
        }
        holder.status.setText(stat);
        return convertView;
    }

    private class ViewHolder {
        public ImageView img;
        public TextView name, desc, status;
    }

}