package com.eajy.materialdesigndemo.fragment;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.Toast;

import com.eajy.materialdesigndemo.R;
import com.eajy.materialdesigndemo.activity.OrderInfoActivity;
import com.eajy.materialdesigndemo.adapter.ShoppingHistoryRecyclerViewAdapter;
import com.eajy.materialdesigndemo.info.OrderInfo;
import com.eajy.materialdesigndemo.model.AppModel;
import com.eajy.materialdesigndemo.model.DataStore;
import com.eajy.materialdesigndemo.util.NetUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 查看历史记录
 */
public class ShoppingHistoryFragment extends Fragment implements View.OnClickListener, View.OnTouchListener {
    //    这三个经常是同时出现的
    private List<OrderInfo> list = new ArrayList<OrderInfo>();

    private ShoppingHistoryRecyclerViewAdapter rvAdapter;
    private  RecyclerView recyclerViewOrder;
    private LinearLayoutManager mLayoutManager;

    //    定义数据
//    private String[] name = {"盐","水","沐浴露"};
//    private String[] date ={"2018/3/3","2018/3/4","2018/3/5"};
//    private String[] price = {"19元", "20元", "21元"};

    private Handler handler = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final NestedScrollView nestedScrollView = (NestedScrollView) inflater.inflate(R.layout.fragment_shopping_history, container, false);

        recyclerViewOrder = (RecyclerView)  nestedScrollView.findViewById(R.id.recycler_view_orders);
//        for (int i = 0; i < name.length; i++) {
//            OrderInfo tf = new OrderInfo();
//            tf.setGoodName(name[i]);
//            tf.setPrice(price[i]);
//            tf.setDate(date[i]);
//            list.add(tf);
//        }
        mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerViewOrder.setLayoutManager(mLayoutManager);
        rvAdapter = new ShoppingHistoryRecyclerViewAdapter(DataStore.getAllOrder());
        recyclerViewOrder.setAdapter(rvAdapter);

        rvAdapter.setBtnInfoClickListener(new ShoppingHistoryRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
//                Toast.makeText(getActivity(),"the number"+position+"has been clicked",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getActivity(), OrderInfoActivity.class);
                intent.putExtra("name", DataStore.getAllOrder().get(position).getGoodName());
                startActivity(intent);
            }
        });
        rvAdapter.setBtnConfirmClickListener(new ShoppingHistoryRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
//                Toast.makeText(getActivity(), name[position]+" 确认收货",Toast.LENGTH_SHORT).show();
                final OrderInfo orderInfo = DataStore.getAllOrder().get(position);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String response = NetUtils.get(AppModel.BASE_URL + "/complete_o/?oid=" + orderInfo.getId() + "&token=" + AppModel.getToken());
                            JSONObject jsonObject = new JSONObject(response);
                            if (!jsonObject.optString("message").equals("Success")){
                                Looper.prepare();
                                Toast.makeText(getActivity(),"失败  " + jsonObject.optString("message"),Toast.LENGTH_SHORT).show();
                                Looper.loop();
                            }else{
                                orderInfo.setArrived(true);
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        rvAdapter.notifyDataSetChanged();
                                    }
                                });
                            }
                        }catch (Exception e){
                            e.printStackTrace();
//                            Looper.prepare();
//                            Toast.makeText(getActivity(),"网络连接错误",Toast.LENGTH_SHORT).show();
//                            Looper.loop();
                        }
                    }
                }).start();
            }
        });

        handler = new Handler();
        return nestedScrollView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        refresh();
        rvAdapter.notifyDataSetChanged();
    }

    public void refresh(){
        Log.d("WTF", "刷新历史记录信息");
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
                        response = NetUtils.get(AppModel.BASE_URL + "/search_o/?token=" + token);
                    }catch (Exception e){
                        e.printStackTrace();
//                        Looper.prepare();
//                        Toast.makeText(getActivity(),"网络连接错误",Toast.LENGTH_SHORT).show();
//                        Looper.loop();
                        response = AppModel.mCache.getAsString(AppModel.getUserName() + "_history");
                    }
//                    Log.e("WTF", "cache: " + response);
                    try {
                        jsonArray = new JSONArray(response);
                        ArrayList<OrderInfo> allOrder = DataStore.getAllOrder();
                        allOrder.clear();
                        for (int i = 0; i <  jsonArray.length(); i++) {
                            JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                            OrderInfo orderInfo = new OrderInfo();

                            orderInfo.setGoodName(jsonObject.optString("goodsname"));

                            orderInfo.setDate(jsonObject.optString("add_time").replace("T", " ").replace("Z", " "));
                            orderInfo.setAmount(Integer.parseInt(jsonObject.optString("amount")));
                            orderInfo.setId(jsonObject.optString("oid"));
                            orderInfo.setReceiver(jsonObject.optString("signer_name"));
                            orderInfo.setrPhone(jsonObject.optString("signer_mobile"));
                            orderInfo.setAddress(jsonObject.optString("address"));

                            orderInfo.setArrived(jsonObject.optBoolean("complete"));
                            allOrder.add(orderInfo);
                        }
                        DataStore.setAllOrder(allOrder);
                        Log.d("WTF", response);
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                rvAdapter.notifyDataSetChanged();
                            }
                        });
                        AppModel.mCache.put(AppModel.getUserName() + "_history", response);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
        }
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
