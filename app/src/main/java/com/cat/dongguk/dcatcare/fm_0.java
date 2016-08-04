package com.cat.dongguk.dcatcare;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class fm_0 extends Fragment implements Button.OnClickListener{

    private static final String url = "http://yjham2002.woobi.co.kr/dcat/host.php?tr=102";

    private ProgressBar pbar;

    private Button filter, refresh;

    private ListView listview;
    private UserListAdapter mAdapter;

    private int catNum = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View rootView = inflater.inflate(R.layout.fm_0, container, false);

        filter = (Button)rootView.findViewById(R.id.filter);
        refresh = (Button)rootView.findViewById(R.id.refresh);

        filter.setOnClickListener(this);
        refresh.setOnClickListener(this);

        pbar = (ProgressBar)rootView.findViewById(R.id.pbar);

        listview = (ListView)rootView.findViewById(R.id.listView);
        mAdapter = new UserListAdapter(rootView.getContext());
        listview.setAdapter(mAdapter);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                UserData mData = mAdapter.mListData.get(position);
                Intent i = new Intent(getActivity(), CatDetailActivity.class);
                i.putExtra("id", mData.id);
                i.putExtra("desc", mData.date);
                i.putExtra("name", mData.text);
                i.putExtra("status", mData.text);
                i.putExtra("status", mData.like);
                i.putExtra("cat", mData.cat);
                i.putExtra("mode", true);
                i.putExtra("img_url", mData.img_url);
                startActivity(i);
                getActivity().overridePendingTransition(R.anim.fade, R.anim.hold);
            }
        });

        return rootView;
    }

    @Override
    public void onResume(){
        catNum = 0;
        loadList();
        super.onResume();
    }

    @Override
    public void onClick(View v)
    {
        switch(v.getId()){
            case R.id.refresh: loadList(); break;
            case R.id.filter:

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                final Spinner spinner = new Spinner(getActivity());
                ArrayAdapter<String> adt = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, MainActivity.catNames);
                adt.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(adt);

                final LinearLayout layout = new LinearLayout(getActivity());
                LinearLayout.LayoutParams parms = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                layout.setOrientation(LinearLayout.VERTICAL);
                layout.setLayoutParams(parms);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                params.setMargins(10, 10, 10, 10);
                layout.addView(spinner, params);

                layout.setGravity(Gravity.CLIP_VERTICAL);
                layout.setPadding(10, 10, 10, 10);

                builder.setView(layout);

                builder.setMessage("필터를 선택하세요!");
                builder.setCancelable(true);
                builder.setPositiveButton("취소", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                })
                        .setNeutralButton("확인", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                catNum = spinner.getSelectedItemPosition();
                                loadList();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
                break;
            default: break;
        }
    }

    public void loadList(){
        pbar.setVisibility(View.VISIBLE);
        Communicator comm = new Communicator();
        comm.getHttp(url, new Handler(){
            public void handleMessage(Message msg){
                String jsonString = msg.getData().getString("jsonString");
                mAdapter.mListData.clear();
                try {
                    JSONArray json_arr = new JSONArray(jsonString);
                    for(int i = json_arr.length()-1; i>=0; i--){
                        JSONObject json_list = json_arr.getJSONObject(i);
                        if(catNum == 0) mAdapter.addItem(json_list.getInt("id"),json_list.getString("mac"),json_list.getString("dates"),json_list.getString("text"),json_list.getString("img_url"), json_list.getInt("cat"), json_list.getString("like"));
                        else if(catNum == json_list.getInt("cat")) mAdapter.addItem(json_list.getInt("id"),json_list.getString("mac"),json_list.getString("dates"),json_list.getString("text"),json_list.getString("img_url"), json_list.getInt("cat"), json_list.getString("like"));
                        else continue;
                    }
                } catch (JSONException e) {
                    //Toast.makeText(getActivity().getBaseContext(), "목록을 불러오는 중 오류가 발생하였습니다.", Toast.LENGTH_LONG).show();
                }finally {
                    mAdapter.dataChange();
                    pbar.setVisibility(View.GONE);
                }
            }
        });
    }

}
