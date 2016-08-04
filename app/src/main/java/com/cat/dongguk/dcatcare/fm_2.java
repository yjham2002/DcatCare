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
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import uk.co.senab.photoview.PhotoViewAttacher;

public class fm_2 extends Fragment implements Button.OnClickListener{

    private static final String url = "http://yjham2002.woobi.co.kr/dcat/host.php?tr=100";
    private static final String Requrl = "http://yjham2002.woobi.co.kr/dcat/host.php?tr=107&phone=";

    private ProgressBar pbar;

    private ListView listview;
    private Button add;

    private ListViewAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View rootView = inflater.inflate(R.layout.fm_2, container, false);

        add = (Button)rootView.findViewById(R.id.add);
        add.setOnClickListener(this);

        pbar = (ProgressBar)rootView.findViewById(R.id.pbar);

        listview = (ListView)rootView.findViewById(R.id.listView);
        mAdapter = new ListViewAdapter(rootView.getContext());
        listview.setAdapter(mAdapter);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                CatData mData = mAdapter.mListData.get(position);
                Intent i = new Intent(getActivity(), CatDetailActivity.class);
                i.putExtra("id", mData.id);
                i.putExtra("desc", mData.desc);
                i.putExtra("name", mData.name);
                i.putExtra("status", mData.status);
                i.putExtra("img_url", mData.img_url);
                i.putExtra("mode", false);
                startActivity(i);
                getActivity().overridePendingTransition(R.anim.fade, R.anim.hold);
            }
        });

        return rootView;
    }

    @Override
    public void onResume(){
        loadList();
        super.onResume();
    }

    @Override
    public void onClick(View v)
    {
        switch(v.getId()){
            case R.id.add:
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                final TextView tv1 = new TextView(getActivity());
                final TextView tv2 = new TextView(getActivity());
                final EditText phone = new EditText(getActivity());
                final EditText desc = new EditText(getActivity());

                LinearLayout layout = new LinearLayout(getActivity());
                LinearLayout.LayoutParams parms = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                layout.setOrientation(LinearLayout.VERTICAL);
                layout.setLayoutParams(parms);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                params.setMargins(10, 10, 10, 10);
                tv1.setText("연락처(휴대폰/이메일 등)");
                tv2.setText("제보 내용");
                layout.addView(tv1, params);
                layout.addView(phone, params);
                layout.addView(tv2, params);
                layout.addView(desc, params);

                layout.setGravity(Gravity.CLIP_VERTICAL);
                layout.setPadding(10, 10, 10, 10);

                builder.setView(layout);

                builder.setMessage("추가 요청");
                builder.setCancelable(true);
                builder.setPositiveButton("취소", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                })
                        .setNeutralButton("확인", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                if(desc.getText().toString().length()<=0 || desc.getText().toString().equals("") || phone.getText().toString().length()<=0 || phone.getText().toString().equals(""))
                                    Toast.makeText(getActivity().getBaseContext(), "내용을 입력하세요", Toast.LENGTH_LONG).show();
                                else if(phone.getText().toString().length()<=3) Toast.makeText(getActivity().getBaseContext(), "올바른 연락처를 입력하세요!", Toast.LENGTH_LONG).show();
                                else if(desc.getText().toString().length()>50 || phone.getText().toString().length()>50) Toast.makeText(getActivity().getBaseContext(), "50자 이내로 입력하세요!", Toast.LENGTH_LONG).show();
                                else if(MainActivity.catNames.size()<=1) Toast.makeText(getActivity().getBaseContext(), "인터넷에 연결할 수 없습니다.", Toast.LENGTH_LONG).show();
                                else {
                                    Communicator comm = new Communicator();
                                    comm.getHttp(Requrl+phone.getText().toString().trim().replaceAll(" ","_")+"&desc="+desc.getText().toString().trim().replaceAll(" ","_"), new Handler(){
                                        @Override
                                        public void handleMessage(Message msg){
                                            Toast.makeText(getActivity().getBaseContext(), "요청 완료", Toast.LENGTH_LONG).show();
                                        }
                                    });
                                }
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
                        mAdapter.addItem(json_list.getInt("id"),json_list.getString("name"),json_list.getString("desc"),json_list.getInt("status"),json_list.getString("img_url"));
                    }
                } catch (JSONException e) {
                    Toast.makeText(getActivity().getBaseContext(), "목록을 불러오는 중 오류가 발생하였습니다.", Toast.LENGTH_LONG).show();
                }finally {
                    mAdapter.dataChange();
                    pbar.setVisibility(View.GONE);
                }
            }
        });
    }

}
