package com.eajy.materialdesigndemo.activity;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.drm.DrmStore;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Looper;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.eajy.materialdesigndemo.R;
import com.eajy.materialdesigndemo.base.BaseNfcActivity;
import com.eajy.materialdesigndemo.model.AppModel;
import com.eajy.materialdesigndemo.model.DataStore;
import com.eajy.materialdesigndemo.util.NFCUtils;
import com.eajy.materialdesigndemo.util.NetUtils;

import org.json.JSONObject;

import java.util.List;

//右侧介绍
public class AddDeviceActivity extends BaseNfcActivity implements View.OnClickListener {

    private NfcAdapter _nfcAdapter;
    private PendingIntent _pendingIntent;
    private IntentFilter[] _intentFilters;
    private final String _MIME_TYPE = "text/plain";
    private Intent _intent;

    TextView name, idd, kind;
    String thingsName = "", thingsId="", thingsType="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_device);

        // init nfc
        _init();
//        // on new intent
//        _intent = getIntent();
//        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction()))
//        {
//            Log.d("hfr", "read called");
//
//            _readMessage();
//        }

        Log.d("oncreate", "readdddddd");


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
            TextView nameText = findViewById(R.id.order_info_name);
            nameText.setText(goodName + "    1包    ￥15.00");
        }

        findViewById(R.id.btn_add_device_confirm).setOnClickListener(this);
    }

    private void _init()
    {
        Log.d("intent read init", "readdddddd");

        _nfcAdapter = NfcAdapter.getDefaultAdapter(this);

        if (_nfcAdapter == null)
        {
            Toast.makeText(this, "This device does not support NFC.", Toast.LENGTH_LONG).show();
            return;
        }

        if (_nfcAdapter.isEnabled())
        {
            _pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

            IntentFilter ndefDetected = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
            try
            {
                ndefDetected.addDataType(_MIME_TYPE);
            } catch (IntentFilter.MalformedMimeTypeException e)
            {
                Log.e(this.toString(), e.getMessage());
            }

            _intentFilters = new IntentFilter[] { ndefDetected };
        }
    }


    @Override
    protected void onNewIntent(Intent intent)
    {
        super.onNewIntent(intent);
        Log.d("on new intent", "readdddddd");

        _intent = intent;

        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction()))
        {
            _readMessage();
        }
    }

    private void _readMessage()
    {
        Log.d("what", "readdddddd");

        List<String> msgs = NFCUtils.getStringsFromNfcIntent(_intent);
        Log.d("intent readmessage", "readdddddd");

        name = (TextView)findViewById(R.id.order_info_name);
        idd = (TextView)findViewById(R.id.order_info_idd);
        kind = (TextView)findViewById(R.id.order_info_kind);

        if (!DataStore.getglobal()) {
            // if buy
            String messages = msgs.get(0);
            messages = messages.substring(2, messages.length());
            Log.d("intent write", messages);

            final String info[];
            try {
                info = messages.split(" ");
                name.setText("物品名称: " + info[0]);
                idd.setText("ID: " + info[1]);
                kind.setText("类型: " + info[2]);

                thingsName = info[0];
                thingsId = info[1];
                thingsType = info[1];
            } catch (Exception e) {
            }

//            AlertDialog.Builder builder = new AlertDialog.Builder(this);
//            builder.setMessage("请输入充值金额");
//            builder.setPositiveButton(getString(R.string.dialog_ok), null);
//            builder.show();
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
            case R.id.btn_add_device_confirm:
                try {
                    String response = NetUtils.get(AppModel.BASE_URL + "/create_d/?did=" + thingsId + "&type=" + thingsName + "&gname="  + thingsType);
                    Log.d("WTF", "物品" + name + response);
                    JSONObject jsonObject = new JSONObject(response);
                    if (!jsonObject.optString("message").equals("Success")){
                        Toast.makeText(this,"添加失败 " + jsonObject.optString("message"),Toast.LENGTH_SHORT).show();
                    }else {
                        String address = ((AutoCompleteTextView)findViewById(R.id.text_add_device_address)).getText().toString();
                        String hint = ((AutoCompleteTextView)findViewById(R.id.text_add_device_hint)).getText().toString();

                        response = NetUtils.get(AppModel.BASE_URL + "/bond_d/?did=" + thingsId + "&token="  + AppModel.getToken() + "&address=" + address + "&hint=" + hint);
                        jsonObject = new JSONObject(response);
                        if (jsonObject.optString("message").equals("Success")){
                            Toast.makeText(this,"添加成功",Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(this,"添加失败 " + jsonObject.optString("message"),Toast.LENGTH_SHORT).show();
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        DataStore.setglobalfalse();
    }

}
