package com.eajy.easybuy.activity;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.widget.VideoView;

import com.eajy.easybuy.component.QuantityView;
import com.eajy.easybuy.info.ThingsInfo;
import com.eajy.easybuy.util.NetUtils;
import com.eajy.materialdesigndemo.R;
import com.eajy.easybuy.model.AppModel;
import com.eajy.easybuy.model.DataStore;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//一键购买
public class StartBuyActivity extends AppCompatActivity {

    private VideoView video_fullscreen;
    private RelativeLayout relative_fullscreen;
    private ProgressBar progress_fullscreen;

    static private ListView listView;
    private List<Map<String,Object>> list_map = new ArrayList<Map<String,Object>>(); //定义一个适配器对象
//    private String[] name = {"盐", "水", "沐浴露"};
//    private int[] price = {1000,2000,3000};
    private ArrayList<String> name = new ArrayList<>();
    private ArrayList<Integer> price = new ArrayList<>();

    private Handler handler=new Handler();
    Thread thread;
    SimpleAdapter simpleAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_buy);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.hide();
        }

        progress_fullscreen = findViewById(R.id.progress_fullscreen);
        relative_fullscreen = findViewById(R.id.relative_fullscreen);
        video_fullscreen = findViewById(R.id.video_fullscreen);

        listView = findViewById(R.id.listview_shooping_list);


//        for(int i=0;i<name.length;i++){
//            Map<String,Object> items = new HashMap<String, Object>(); //创建一个键值对的Map集合，用来存放名字和头像
//            items.put("price", String.valueOf((float)price[i]/100)+"元");
//            items.put("name", name[i]);
//            list_map.add(items);
//        }
        simpleAdapter = new SimpleAdapter(this,list_map,R.layout.shopping_list_line, new String[]{"price","name"}, new int[]{R.id.text_shopping_list_price,R.id.text_shopping_list_name});

        //3、为listView加入适配器
        listView.setAdapter(simpleAdapter);

        String info = getIntent().getStringExtra("str_price");

        if (info != null) {
            if (info.substring(0, 3).equals("nfc")) {
                try {
                    String str = info.substring(3, info.length());
                    Log.d("intent read succ", str);

                    String s[] = str.split(" ");
                    Integer pri = Integer.valueOf(s[1]);
                    if (true) {
                        name.add(s[0]);
                        price.add(pri);
                    }
                } catch (Exception e) {
                }
            }
            for(int i=0;i<name.size();i++){
                Map<String,Object> items = new HashMap<String, Object>(); //创建一个键值对的Map集合，用来存放名字和头像
                items.put("price", String.valueOf((float)price.get(i)/100)+"元");
                items.put("name", name.get(i));
                list_map.add(items);
            }
            simpleAdapter.notifyDataSetChanged();
        }
        else {
            // from the list
            new Thread(new Runnable() {
                @Override
                public void run() {
                    list_map.clear();
                    ArrayList<ThingsInfo> allThings = DataStore.getAllThings();
                    for (int i=0; i< allThings.size(); i++){
                        if (allThings.get(i).isChoosen()){
                            Map<String,Object> items = new HashMap<String, Object>();
                            String name = allThings.get(i).getGoodName();
                            items.put("price", String.valueOf((float)AppModel.getGoodsPrice(name, 10)/100)+"元");
                            items.put("name", name);
                            items.put("address", allThings.get(i).getAddress());
                            list_map.add(items);
                        }
                    }
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                simpleAdapter.notifyDataSetChanged();
                            }catch (Exception e){
                                e.printStackTrace();
                            }

                        }
                    });
                }
            }).start();
        }

        final Context context = this;
        FloatingActionButton btnConfirm = findViewById(R.id.btn_start_buy);
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        float totalNum = 0;
                        for (int i=0; i< list_map.size(); i++){
                            try {
                                //修改
                                String name = (String) list_map.get(i).get("name");

                                float price = Float.parseFloat(((String) list_map.get(i).get("price")).replace("元",""));
                                LinearLayout layout = (LinearLayout) getViewByPosition(i, listView);
                                QuantityView quantityView = layout.findViewById(R.id.quantity_buy_num);
                                int num = quantityView.getQuantity();

                                String response = NetUtils.get(AppModel.BASE_URL + "/create_o/?token=" + AppModel.getToken() + "&gname=" + name + "&address=" + (String) list_map.get(i).get("address")+ "&signer_name=" + AppModel.getUserName() + "&signer_mobile=" +  AppModel.getPhone() + "&number=" + String.valueOf(num));
                                JSONObject jsonObject = new JSONObject(response);
                                Log.e("WTF", response);
                                if (!jsonObject.optString("message").equals("Success")){
                                    Looper.prepare();
                                    Toast.makeText(context,"购买" + name + "失败  " + jsonObject.optString("message"),Toast.LENGTH_SHORT).show();
                                    Looper.loop();
                                }else{
                                    totalNum += num * price;
                                }
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                        Looper.prepare();
                        Toast.makeText(context,"购买成功， 共花费" + String.valueOf((float) totalNum) + "元",Toast.LENGTH_SHORT).show();
                        Looper.loop();
//                        context.finish();
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                finish();
                            }
                        });
                    }
                }).start();

//                "购买成功， 共花费" + String.valueOf((float) totalNum/100) + "元"
//                Toast.makeText(context,"购买成功， 共花费" + String.valueOf((float) totalNum) + "元",Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.fab_exit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    public static View getViewByPosition(int pos, ListView listView) {
        final int firstListItemPosition = listView.getFirstVisiblePosition();
        final int lastListItemPosition = firstListItemPosition + listView.getChildCount() - 1;

        if (pos < firstListItemPosition || pos > lastListItemPosition ) {
            return listView.getAdapter().getView(pos, null, listView);
        } else {
            final int childIndex = pos - firstListItemPosition;
            return listView.getChildAt(childIndex);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        hideBars();
    }

    @Override
    protected void onStop() {
        super.onStop();
        relative_fullscreen.setVisibility(View.VISIBLE);
    }

    private void showBars() {
        video_fullscreen.setSystemUiVisibility(View.VISIBLE);

        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.GRAY);
        window.setNavigationBarColor(Color.GRAY);

        /*mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, 2000);*/
    }

    private void hideBars() {
        video_fullscreen.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        thread.destroy();
    }
//
//    class RefreshTotalPrice implements Runnable {
//        private Thread t;
//
//        public void run() {
//            while (true){
//                int num = name.length;
//                int totalPrice = 0;
//                for(int i=0; i<num; i++) {
//                    try {
//                        LinearLayout layout = (LinearLayout) getViewByPosition(0, listView);
//                        QuantityView quantityView = layout.findViewById(R.id.quantity_buy_num);
//                        int selectedNum = quantityView.getQuantity();
//                        totalPrice += selectedNum * price[i];
//                    } catch (Exception e) {
//                        Log.e("WTF", e.toString());
//                        return;
//                    }
//                }
//                final int finalPrice = totalPrice;
//                final TextView title = findViewById(R.id.text_shopping_list_title);
//                handler.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        try {
//                            title.setText("您的购物清单    共计：" + String.valueOf((float)finalPrice/100) + "元");
//                            wait(100);
//                        }catch (Exception e){
//
//                        }
//                    }
//                });
//                try {
//                    wait(100);
//                }catch (Exception e){
//
//                }
//            }
//        }
//    }
}

