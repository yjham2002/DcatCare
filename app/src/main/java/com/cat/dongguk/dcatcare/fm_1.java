package com.cat.dongguk.dcatcare;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;

public class fm_1 extends Fragment implements Button.OnClickListener{

    private static final String url = "http://yjham2002.woobi.co.kr/dcat/host.php?tr=101&mac=";
    private static final String url2 = "http://yjham2002.woobi.co.kr/dcat/host.php?tr=104&mac=";

    private ListView listview;
    private UserListAdapter mAdapter;

    private static String mac;

    private String content;

    private int catNum = 0;

    private ProgressBar pbar;

    private Uri mImageCaptureUri;
    private Button upload, refresh;

    private static final int PICK_FROM_ALBUM = 1;
    private static final int CROP_FROM_ALBUM = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View rootView = inflater.inflate(R.layout.fm_1, container, false);

        upload = (Button)rootView.findViewById(R.id.upload);
        refresh = (Button)rootView.findViewById(R.id.refresh);

        upload.setOnClickListener(this);
        refresh.setOnClickListener(this);

        pbar = (ProgressBar)rootView.findViewById(R.id.pbar);

        WifiManager manager = (WifiManager) getActivity().getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = manager.getConnectionInfo();
        mac = info.getMacAddress();

        listview = (ListView)rootView.findViewById(R.id.listView);
        mAdapter = new UserListAdapter(rootView.getContext());
        listview.setAdapter(mAdapter);
        listview.setEmptyView(rootView.findViewById(R.id.empty));
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
        loadList();
        super.onResume();
    }

    @Override
    public void onClick(View v)
    {
        switch(v.getId()){
            case R.id.upload:
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                final EditText input = new EditText(getActivity());
                final Spinner spinner = new Spinner(getActivity());
                ArrayAdapter<String> adt = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, MainActivity.catNames);
                adt.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(adt);

                LinearLayout layout = new LinearLayout(getActivity());
                LinearLayout.LayoutParams parms = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                layout.setOrientation(LinearLayout.VERTICAL);
                layout.setLayoutParams(parms);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                params.setMargins(10, 10, 10, 10);
                layout.addView(input, params);
                layout.addView(spinner, params);

                layout.setGravity(Gravity.CLIP_VERTICAL);
                layout.setPadding(10, 10, 10, 10);

                builder.setView(layout);

                builder.setMessage("제목을 입력하세요!");
                builder.setCancelable(true);
                builder.setPositiveButton("취소", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                })
                        .setNeutralButton("확인", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                if(input.getText().toString().length()<0 || input.getText().toString().equals("")) Toast.makeText(getActivity().getBaseContext(), "내용을 입력하세요", Toast.LENGTH_LONG).show();
                                else if(input.getText().toString().length()>50) Toast.makeText(getActivity().getBaseContext(), "50자 이내로 입력하세요!", Toast.LENGTH_LONG).show();
                                else if(MainActivity.catNames.size()<=1) Toast.makeText(getActivity().getBaseContext(), "인터넷에 연결할 수 없습니다.", Toast.LENGTH_LONG).show();
                                else {content = input.getText().toString().trim().replaceAll(" ", "_");
                                    catNum = spinner.getSelectedItemPosition();
                                doTakeAlbumAction();}
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
                break;
            case R.id.refresh: loadList(); break;
            default: break;
        }
    }

    public void doTakeAlbumAction(){
        Intent intent = new Intent();
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        intent.setAction(Intent.ACTION_PICK);
        mImageCaptureUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "tmp_img.jpg"));
        intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
        startActivityForResult(intent, PICK_FROM_ALBUM);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != getActivity().RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case PICK_FROM_ALBUM: {
                mImageCaptureUri = data.getData();
                Intent intent = new Intent("com.android.camera.action.CROP");
                intent.setDataAndType(mImageCaptureUri, "image/*");
                //intent.putExtra("outputX", 200);
                //intent.putExtra("outputY", 200);
                //intent.putExtra("aspectX", 1);
                //intent.putExtra("aspectY", 1);
                intent.putExtra("scale", true);
                intent.putExtra("return-data", true);

                startActivityForResult(intent, CROP_FROM_ALBUM);
                break;
            }
            case CROP_FROM_ALBUM: {
                final Bundle extras = data.getExtras();

                if (extras != null) {
                    Bitmap photo = extras.getParcelable("data");
                    try {
                        BufferedOutputStream out = new BufferedOutputStream(getActivity().openFileOutput("temp.jpg", 0));
                        photo.compress(Bitmap.CompressFormat.JPEG, 100, out);
                        out.flush();
                        out.close();
                    } catch (IOException e) {

                        e.printStackTrace();
                    }
                }

                try {
                    File f = new File(mImageCaptureUri.getPath());
                    if (f.exists()) f.delete();
                } catch (Exception e) {
                }

                Communicator.sendImg(url+mac+"&date="+TIME_MAXIMUM.getDate()+"&cat="+catNum+"&text="+content, getActivity().getFilesDir() + "/temp.jpg", new Handler() {
                    public void handleMessage(Message msg) {
                        String jsonString = msg.getData().getString("jsonString");
                        Log.d("test","jsonString upload "+jsonString);
                        Toast.makeText(getActivity().getBaseContext(), "업로드 완료", Toast.LENGTH_LONG).show();
                        loadList();
                    }
                });
                break;
            }
        }
    }

    public void loadList(){
        pbar.setVisibility(View.VISIBLE);
        Communicator comm = new Communicator();
        comm.getHttp(url2+mac, new Handler(){
            public void handleMessage(Message msg){
                String jsonString = msg.getData().getString("jsonString");
                mAdapter.mListData.clear();
                try {
                    JSONArray json_arr = new JSONArray(jsonString);
                    for(int i = json_arr.length()-1; i>=0; i--){
                        JSONObject json_list = json_arr.getJSONObject(i);
                        mAdapter.addItem(json_list.getInt("id"),json_list.getString("mac"),json_list.getString("dates"),json_list.getString("text"),json_list.getString("img_url"), json_list.getInt("cat"), json_list.getString("like"));
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
