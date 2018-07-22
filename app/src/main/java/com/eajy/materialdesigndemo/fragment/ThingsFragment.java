package com.eajy.materialdesigndemo.fragment;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.Toast;

import com.eajy.materialdesigndemo.R;
import com.eajy.materialdesigndemo.activity.AddDeviceActivity;
import com.eajy.materialdesigndemo.activity.MainActivity;
import com.eajy.materialdesigndemo.activity.ScrollingActivity;
import com.eajy.materialdesigndemo.activity.StartBuyActivity;
import com.eajy.materialdesigndemo.adapter.ThingsRecyclerViewAdapter;
import com.eajy.materialdesigndemo.info.ThingsInfo;
import com.eajy.materialdesigndemo.model.AppModel;
import com.eajy.materialdesigndemo.model.DataStore;
import com.eajy.materialdesigndemo.util.NetUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class ThingsFragment extends Fragment implements View.OnClickListener, View.OnTouchListener {
    //    这三个经常是同时出现的
//    private  List<ThingsInfo> list = new ArrayList<ThingsInfo>();

    private SwipeRefreshLayout swipeRefreshLayout;  //刷新
    private ThingsRecyclerViewAdapter rvAdapter;
    private  RecyclerView recyclerViewThings;
    private LinearLayoutManager mLayoutManager;

    private boolean loading;
    //    定义数据
//    private String[] name = {"盐","水","沐浴露"};
//    private String[] pos ={"厨房","厨房","浴室"};
//    private int[] remain = {10, 50, 100};
//    private boolean[] isChoosen = {false, false, false};

    Handler handler = new Handler();
    private AlphaAnimation  alphaAnimationShowIcon;
    private  boolean isEdit = false;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final NestedScrollView nestedScrollView = (NestedScrollView) inflater.inflate(R.layout.fragment_things, container, false);

        recyclerViewThings = (RecyclerView)  nestedScrollView.findViewById(R.id.recycler_view_things);
//        for (int i = 0; i < name.length; i++) {
//            ThingsInfo tf = new ThingsInfo();
//            tf.setName(name[i]);
//            tf.setPos(pos[i]);
//            tf.setRemain(remain[i]);
//
//            if (tf.getRemain()<20)
//                tf.setChoosen(true);
//            else
//                tf.setChoosen(false);
//
//            list.add(tf);
//        }

        mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerViewThings.setLayoutManager(mLayoutManager);

        rvAdapter = new ThingsRecyclerViewAdapter(getActivity(), DataStore.getAllThings());
//        rvAdapter = new ThingsRecyclerViewAdapter(getActivity(), list);
        rvAdapter.setOnCheckClickListener(new ThingsRecyclerViewAdapter.OnCheckClickListener() {
            @Override
            public void onCheckClick(View itemView, int pos, boolean isChecked){
//                list.get(pos).setChoosen(isChecked);
                DataStore.getAllThings().get(pos).setChoosen(isChecked);
//                Log.e("WTF", String.valueOf(pos) + "is" + String.valueOf(isChecked));
            }
        });

        rvAdapter.setOnBtnClickListener(new ThingsRecyclerViewAdapter.OnBtnClickListener() {
            @Override
            public void onBtnClick(View itemView, int pos){
                FloatingActionButton fb_add = getActivity().findViewById(R.id.fab_add);
                FloatingActionButton fb_confirm = getActivity().findViewById(R.id.fab_confirm);
                FloatingActionButton fb_delete = getActivity().findViewById(R.id.fab_delete);
                FloatingActionButton fb_refresh = getActivity().findViewById(R.id.fab_refresh);
                Button btnManager = getActivity().findViewById(R.id.btn_things_delete);
                if (!isEdit){
                    for(ThingsInfo element:DataStore.getAllThings()){
                        element.setChoosen(false);
                    }
                    rvAdapter.notifyDataSetChanged();

                    fb_confirm.hide();
                    fb_add.hide();
                    fb_delete.show();
                    fb_refresh.hide();
                    MainActivity.fb_state = false;
                    btnManager.setText("取消管理");
                    isEdit = true;
                }else{
                    for(ThingsInfo element:DataStore.getAllThings()){
                        element.reset();
                    }
                    rvAdapter.notifyDataSetChanged();
                    fb_confirm.show();
                    fb_add.show();
                    fb_refresh.show();
                    fb_delete.hide();
                    MainActivity.fb_state = true;
                    btnManager.setText("管理");
                    isEdit = false;
                }

            }
        });

        rvAdapter.setOnCardClickListener(new ThingsRecyclerViewAdapter.OnBtnClickListener() {
            @Override
            public void onBtnClick(View itemView, int pos) {
                String name = DataStore.getAllThings().get(pos).getName();
                Intent intent = new Intent(getActivity(), ScrollingActivity.class);
                intent.putExtra("name", name);
                startActivity(intent);
            }
        });

        View view = mLayoutManager.findViewByPosition(2);	//2为recyclerView中item位置，
        recyclerViewThings.setAdapter(rvAdapter);

        FloatingActionButton fab_confirm = getActivity().findViewById(R.id.fab_confirm);
        fab_confirm.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if(AppModel.isLogin()){
                    boolean hasGood = false;
                    for (ThingsInfo thingsInfo : DataStore.getAllThings()){
                        if (thingsInfo.isChoosen()){
                            hasGood = true;
                            break;
                        }
                    }
                    if (hasGood){
                        Intent confirmIntent = new Intent(getActivity(), StartBuyActivity.class);
                        startActivity(confirmIntent);
                    }else {
                        Toast.makeText(getActivity(), "请选择商品" ,Toast.LENGTH_SHORT).show();
                    }

                }else{
                    Toast.makeText(getActivity(), "请先登陆" ,Toast.LENGTH_SHORT).show();
                }
            }
        });

        FloatingActionButton fab_add = getActivity().findViewById(R.id.fab_add);
        fab_add.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if(AppModel.isLogin()){
                    Intent add_intent = new Intent(getActivity(), AddDeviceActivity.class);
                    startActivity(add_intent);
                }else{
                    Toast.makeText(getActivity(), "请先登陆" ,Toast.LENGTH_SHORT).show();
                }
            }
        });

        FloatingActionButton fab_delete = getActivity().findViewById(R.id.fab_delete);
        fab_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final ArrayList<ThingsInfo> allThings = DataStore.getAllThings();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        for (int i = 0; i < allThings.size(); i++) {
                            if (allThings.get(i).isChoosen()){
                                final String did = allThings.get(i).getId();
                                final String name =allThings.get(i).getName();
                                try {
                                    String response = NetUtils.get(AppModel.BASE_URL + "/delete_d/?did=" + did + "&token=" + AppModel.getToken());
                                    JSONObject jsonObject = new JSONObject(response);
                                    if (!jsonObject.optString("message").equals("Success")){
                                        Looper.prepare();
                                        Toast.makeText(getActivity(),"删除" + name + "失败  " + jsonObject.optString("message"),Toast.LENGTH_SHORT).show();
                                        Looper.loop();
                                    }else{
                                        allThings.remove(i);
                                        i--;
                                        handler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                rvAdapter.notifyDataSetChanged();
                                            }
                                        });
                                    }
                                }catch (Exception e){
                                    e.printStackTrace();
//                                    Looper.prepare();
//                                    Toast.makeText(getActivity(),"网络连接错误,删除失败",Toast.LENGTH_SHORT).show();
//                                    Looper.loop();
                                }
                            }
                        }
                    }
                }).start();
            }
        });

        return nestedScrollView;
    }


    @Override
    public void onResume() {
        super.onResume();
        refresh();
        rvAdapter.notifyDataSetChanged();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

//        btn_card_main1_action1.setOnClickListener(this);

        alphaAnimationShowIcon = new AlphaAnimation(0.2f, 1.0f);
        alphaAnimationShowIcon.setDuration(500);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
//            case R.id.btn_card_main1_action1:
//                Snackbar.make(view, getString(R.string.main_flat_button_1_clicked), Snackbar.LENGTH_SHORT).show();
//                break;
        }
    }

    public void refresh(){
        Log.d("WTF", "刷新设备信息");
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        new Thread(new Runnable(){
            @Override
            public void run() {
                if (AppModel.isLogin()){
                    String token = AppModel.getToken();
                    JSONArray jsonArray= new JSONArray();
                    String response;
                    //获得所有设备信息
                    try {
                        //验证登陆
                        Log.d("WTF", "token = " + token);
                        response = NetUtils.get(AppModel.BASE_URL + "/search_d/?token=" + token);
                    }catch (Exception e){
                        e.printStackTrace();
//                        Looper.prepare();
//                        Toast.makeText(getActivity(),"网络连接错误",Toast.LENGTH_SHORT).show();
//                        Looper.loop();
                        response = AppModel.mCache.getAsString(AppModel.getUserName() + "_devices");
                    }
                    try {
                        jsonArray = new JSONArray(response);
                        ArrayList<ThingsInfo> allThings = DataStore.getAllThings();
                        allThings.clear();
                        for (int i = 0; i <  jsonArray.length(); i++) {
                            JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                            ThingsInfo thingsInfo = new ThingsInfo();

                            thingsInfo.setPos(jsonObject.optString("hint"));
                            thingsInfo.setId(jsonObject.optString("did"));
                            thingsInfo.setGoodName(jsonObject.optString("goodname"));
                            thingsInfo.setAddress(jsonObject.optString("address"));
                            thingsInfo.setName(jsonObject.optString("type"));
                            int remain = jsonObject.optInt("state");
                            thingsInfo.setRemain(remain);
                            if (remain<20){
                                thingsInfo.setChoosen(true);
                            }else{
                                thingsInfo.setChoosen(false);
                            }
                            allThings.add(thingsInfo);
                        }
                        DataStore.setAllThings(allThings);
                        Log.d("WTF", response);
                        AppModel.mCache.put(AppModel.getUserName() + "_devices", response);
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                rvAdapter.notifyDataSetChanged();
                            }
                        });
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }
    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                ObjectAnimator upAnim = ObjectAnimator.ofFloat(view, "translationZ", 16);
                upAnim.setDuration(150);
                upAnim.setInterpolator(new DecelerateInterpolator());
                upAnim.start();
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                ObjectAnimator downAnim = ObjectAnimator.ofFloat(view, "translationZ", 0);
                downAnim.setDuration(150);
                downAnim.setInterpolator(new AccelerateInterpolator());
                downAnim.start();
                break;
        }
        return false;
    }

}
