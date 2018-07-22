package com.eajy.materialdesigndemo.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.eajy.materialdesigndemo.R;
import com.eajy.materialdesigndemo.model.AppModel;
import com.eajy.materialdesigndemo.util.NetUtils;

import org.json.JSONObject;

/**
 * Created by zhang on 2016.08.07.
 */
public class MyPayFragment extends Fragment implements View.OnClickListener {
    private Button Recharge, Withdraw;
    private EditText text1;

    private TextView text_money, text_card;
    private Handler handler;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        NestedScrollView nestedScrollView = (NestedScrollView) inflater.inflate(R.layout.fragment_mypay, container, false);

        text_card= nestedScrollView.findViewById(R.id.mypay_card);
        text_money = nestedScrollView.findViewById(R.id.mypay_money);

        Recharge = nestedScrollView.findViewById(R.id.Recharge);
        Withdraw = nestedScrollView.findViewById(R.id.Withdraw);
        handler = new Handler();
        return nestedScrollView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Recharge.setOnClickListener(this);
        Withdraw.setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        refresh();
    }

    public void refresh(){
        Log.d("WTF", "刷新微支付信息");
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        new Thread(new Runnable(){
            @Override
            public void run() {
                if (AppModel.isLogin()){
                    String token = AppModel.getToken();
                    String response;
                    //获得所有设备信息
                    try {
                        //验证登陆
                        Log.d("WTF", "token = " + token);
                        response = NetUtils.get(AppModel.BASE_URL + "/user/?token=" + token);
                        Log.d("WTF", response);
                    }catch (Exception e){
                        e.printStackTrace();
//                        Looper.prepare();
//                        Toast.makeText(getActivity(),"网络链接失败，请检查网络",Toast.LENGTH_SHORT).show();
//                        Looper.loop();
                        response = AppModel.mCache.getAsString(AppModel.getUserName() + "_devices");
                    }
                    final String newResponse = response;
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                JSONObject jsonObject = new JSONObject(newResponse);
                                String account = jsonObject.optString("account");
                                String subString = "";
                                if (account.length()>5)
                                    subString = account.substring(account.length()-4, account.length()-1);
                                text_card.setText(account.replace(subString, "****"));
                                text_money.setText( String.valueOf(Integer.parseInt(jsonObject.optString("money"))/100.00));
                            }catch (Exception e){
                                e.printStackTrace();
                            }

                        }
                    });
                }
            }
        }).start();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.Recharge:
                if (!AppModel.isLogin())
                    break;
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage("请输入充值金额(整数)");
                builder.setPositiveButton(getString(R.string.dialog_ok), null);
                View view1 = LayoutInflater.from(getContext()).inflate(R.layout.dialog, null);
                builder.setView(view1);


                final EditText amount = (EditText)view1.findViewById(R.id.username);

                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String a = amount.getText().toString().trim();
                        if (AppModel.isNum(a) && Integer.parseInt(a)>0){
                            int money = Integer.parseInt(a);
                            try {
                                String response = NetUtils.get(AppModel.BASE_URL + "/charge/?token=" + AppModel.getToken() + "&money=" + money + "00");
                                Log.d("WTF", "充值 " + response);
                                JSONObject jsonObject = new JSONObject(response);
                                if (jsonObject.optString("message").equals("Success")){
                                    Toast.makeText(getActivity(),"充值成功",Toast.LENGTH_SHORT).show();
                                    refresh();
                                }else {
                                    Toast.makeText(getActivity(), jsonObject.optString("message"),Toast.LENGTH_SHORT).show();
                                }
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }else{
                            Toast.makeText(getActivity(),"请输入正确的金额",Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.show();
                break;

            case R.id.Withdraw:
                AlertDialog.Builder builder2 = new AlertDialog.Builder(getContext());
                builder2.setMessage("请输入提现金额(整数)");
                builder2.setPositiveButton(getString(R.string.dialog_ok), null);
                View view2 = LayoutInflater.from(getContext()).inflate(R.layout.dialog, null);
                builder2.setView(view2);


                final EditText amount2 = (EditText)view2.findViewById(R.id.username);

                builder2.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String a = amount2.getText().toString().trim();
                        if (AppModel.isNum(a) && Integer.parseInt(a)>0){
                            int money = -Integer.parseInt(a);
                            try {
                                String response = NetUtils.get(AppModel.BASE_URL + "/charge/?token=" + AppModel.getToken() + "&money=" + money + "00");
                                Log.d("WTF", "充值 " + response);
                                JSONObject jsonObject = new JSONObject(response);
                                if (jsonObject.optString("message").equals("Success")){
                                    Toast.makeText(getActivity(),"提现成功",Toast.LENGTH_SHORT).show();
                                    refresh();
                                }else {
                                    Toast.makeText(getActivity(), jsonObject.optString("message"),Toast.LENGTH_SHORT).show();
                                }
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }else{
                            Toast.makeText(getActivity(),"请输入正确的金额",Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                builder2.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder2.show();
                break;
        }
    }
}
