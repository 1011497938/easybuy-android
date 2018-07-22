package com.eajy.materialdesigndemo.activity;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.eajy.materialdesigndemo.Constant;
import com.eajy.materialdesigndemo.R;
import com.eajy.materialdesigndemo.info.OrderInfo;
import com.eajy.materialdesigndemo.model.DataStore;
import com.eajy.materialdesigndemo.util.AppUtils;

import java.util.ArrayList;

//右侧介绍
public class OrderInfoActivity extends AppCompatActivity implements View.OnClickListener {

    private OrderInfo orderInfo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_info);

        Toolbar toolbar = findViewById(R.id.toolbar_order_info);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        getWindow().setNavigationBarColor(getResources().getColor(R.color.colorPrimary));

        initView();

        Intent intent = getIntent();
        String goodName = intent.getStringExtra("name");

        if (goodName != null){
            ArrayList<OrderInfo> allOrder = DataStore.getAllOrder();
            for(int i=0; i<allOrder.size();i++){
                if (allOrder.get(i).getGoodName().equals(goodName)){
                    orderInfo = allOrder.get(i);
                }
            }

            TextView nameText = findViewById(R.id.order_info_name);
            nameText.setText(goodName + "    数量: " + orderInfo.getAmount() + "    ￥15.00");

            TextView secondText = findViewById(R.id.order_info_price);
            secondText.setText("收货人:" + orderInfo.getReceiver() + "    手机号: " + orderInfo.getrPhone());

            ((TextView)findViewById(R.id.order_info_address)).setText("地址: " + orderInfo.getAddress());
        }


    }

    public void initView() {
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.anim_about_card_show);
        ScrollView scroll_about = findViewById(R.id.scroll_about);
        scroll_about.startAnimation(animation);

        AlphaAnimation alphaAnimation = new AlphaAnimation(0.0f, 1.0f);
        alphaAnimation.setDuration(300);
        alphaAnimation.setStartOffset(600);

    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent();

        switch (view.getId()) {

        }
    }

}
