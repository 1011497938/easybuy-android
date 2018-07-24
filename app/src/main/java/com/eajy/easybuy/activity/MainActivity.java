package com.eajy.easybuy.activity;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.eajy.easybuy.base.BaseNfcActivity;
import com.eajy.easybuy.fragment.MyPayFragment;
import com.eajy.easybuy.fragment.ShoppingHistoryFragment;
import com.eajy.easybuy.fragment.ThingsFragment;
import com.eajy.easybuy.util.NFCUtils;
import com.eajy.materialdesigndemo.R;
import com.eajy.easybuy.adapter.FragmentAdapter;
import com.eajy.easybuy.model.AppModel;
import com.eajy.easybuy.model.DataStore;
//import com.eajy.materialdesigndemo.service.QueryService;
import com.eajy.easybuy.service.NotifyService;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseNfcActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {
    private DrawerLayout drawer;
    private FloatingActionButton fab_add, fab_buy, fab_refresh, fab_delete;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private RelativeLayout relative_main;
    private ImageView img_page_start;

    private static boolean isShowPageStart = true;
    private final int MESSAGE_SHOW_DRAWER_LAYOUT = 0x001;
    private final int MESSAGE_SHOW_START_PAGE = 0x002;

    private Intent service_intent;
    Fragment first, second, third;

    public Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_SHOW_DRAWER_LAYOUT:
                    drawer.openDrawer(GravityCompat.START);
                    SharedPreferences sharedPreferences = getSharedPreferences("app", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("isFirst", false);
                    editor.apply();
                    break;

                case MESSAGE_SHOW_START_PAGE:
                    AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.0f);
                    alphaAnimation.setDuration(300);
                    alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            relative_main.setVisibility(View.GONE);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                    relative_main.startAnimation(alphaAnimation);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // init nfc
        _init();
        Log.d("init end", "readdddddd");


        initView();
        initViewPager();
        AppModel.init(this);

        // init the preferences data of Settings
        try {
            PreferenceManager.setDefaultValues(this, R.xml.preferences_settings, false);
        } catch (Exception e) {
        }

        SharedPreferences sharedPreferences = getSharedPreferences("app", MODE_PRIVATE);

        if (isShowPageStart) {
            relative_main.setVisibility(View.VISIBLE);
            Glide.with(MainActivity.this).load(R.drawable.ic_launcher_big).into(img_page_start);
            if (sharedPreferences.getBoolean("isFirst", true)) {
                mHandler.sendEmptyMessageDelayed(MESSAGE_SHOW_START_PAGE, 2000);
            } else {
                mHandler.sendEmptyMessageDelayed(MESSAGE_SHOW_START_PAGE, 1000);
            }
            isShowPageStart = false;
        }

        if (sharedPreferences.getBoolean("isFirst", true)) {
            mHandler.sendEmptyMessageDelayed(MESSAGE_SHOW_DRAWER_LAYOUT, 2500);
        }

        loadData();

       service_intent = new Intent(MainActivity.this, NotifyService.class);
        startService(service_intent);
//        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl("https://api.github.com").build();
    }

    private NfcAdapter _nfcAdapter;
    private PendingIntent _pendingIntent;
    private IntentFilter[] _intentFilters;
    private final String _MIME_TYPE = "text/plain";
    private Intent _intent;

    private void _init()
    {
        Log.d("init begin", "readdddddd");

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

        _intent = intent;

        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction()))
        {
            _readMessage();
        }

//        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction()))
//        {
//            AlertDialog.Builder builder = new AlertDialog.Builder(this);
//            builder.setMessage(R.string.title_alert_dialog)
//                    .setPositiveButton(R.string.write_button_label,
//                            new DialogInterface.OnClickListener() {
//                                public void onClick(DialogInterface dialog, int id)
//                                {
//                                    _writeMessage();
//                                }
//                            })
//                    .setNegativeButton(R.string.read_button_label,
//                            new DialogInterface.OnClickListener() {
//                                public void onClick(DialogInterface dialog, int id)
//                                {
//                                    _readMessage();
//                                }
//                            });
//
//            AlertDialog dialog = builder.create();
//            dialog.show();
//        }
    }

    private void _readMessage()
    {
        Log.d("read begin", "readdddddd");

        List<String> msgs = NFCUtils.getStringsFromNfcIntent(_intent);

        if (DataStore.getglobal()) {
            // if buy
            String messages = "nfc";
            messages += msgs.get(0).substring(2, msgs.get(0).length());
            Log.d("intent write", messages);
            Intent intent = new Intent(MainActivity.this, StartBuyActivity.class);
            intent.putExtra("str_price", messages);
            startActivity(intent);
        }
        Log.d("read end", "readdddddd");
    }

    private void loadData(){
        //初始化数据
        NavigationView navigationView = findViewById(R.id.nav_view);
        View head = navigationView.getHeaderView(0);

        TextView loginStatus =  head.findViewById(R.id.text_nav_header);
        LinearLayout accountAction = head.findViewById(R.id.nav_show_account);
        ImageView logo = head.findViewById(R.id.imageView_nav_header);
        if (AppModel.isLogin()){
            loginStatus.setText(AppModel.getUserInfo().getUserName());   //换成用户名
            logo.setImageResource(R.drawable.logo);
            accountAction.setVisibility(View.VISIBLE);
        }else{
            loginStatus.setText("点击登陆");
            logo.setImageResource(R.drawable.ic_launch_round);
            accountAction.setVisibility(View.INVISIBLE);
        }
    }


    private void initView() {
        Toolbar toolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setItemIconTintList(null);

        View headerView = navigationView.getHeaderView(0);
        LinearLayout nav_header = headerView.findViewById(R.id.nav_header);
        nav_header.setOnClickListener(this);

//      修改密码和退出
        nav_header.findViewById(R.id.text_change_password).setOnClickListener(this);
        nav_header.findViewById(R.id.text_exit).setOnClickListener(this);

        fab_add = findViewById(R.id.fab_add);
//        fab_add.setOnClickListener(this);

        fab_buy = findViewById(R.id.fab_confirm);
//        fab_buy.setOnClickListener(this);

        fab_refresh = findViewById(R.id.fab_refresh);
        fab_refresh.setOnClickListener(this);

        fab_delete = findViewById(R.id.fab_delete);

        relative_main = findViewById(R.id.relative_main);
        img_page_start = findViewById(R.id.img_page_start);


    }


    private void initViewPager() {
        mTabLayout = findViewById(R.id.tab_layout_main);
        mViewPager = findViewById(R.id.view_pager_main);

        List<String> titles = new ArrayList<>();

        titles.add(getString(R.string.tab_title_main_1));
        titles.add(getString(R.string.tab_title_main_2));
        titles.add(getString(R.string.tab_title_main_3));

        mTabLayout.addTab(mTabLayout.newTab().setText(titles.get(0)));
        mTabLayout.addTab(mTabLayout.newTab().setText(titles.get(1)));
        mTabLayout.addTab(mTabLayout.newTab().setText(titles.get(2)));

        List<Fragment> fragments = new ArrayList<>();

        first = new ThingsFragment();
        second = new ShoppingHistoryFragment();
        third = new MyPayFragment();
        fragments.add(first);
        fragments.add(second);
        fragments.add(third);
//        fragments.add(new DialogsFragment());

        mViewPager.setOffscreenPageLimit(2);

        FragmentAdapter mFragmentAdapter = new FragmentAdapter(getSupportFragmentManager(), fragments, titles);
        mViewPager.setAdapter(mFragmentAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.setTabsFromPagerAdapter(mFragmentAdapter);

        mViewPager.addOnPageChangeListener(pageChangeListener);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadData();
        DataStore.setglobaltrue();
    }

    public static boolean fb_state = true;
    private ViewPager.OnPageChangeListener pageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            if (position == 0) {
                if (fb_state){
                    fab_add.show();
                    fab_buy.show();
//                    fab_refresh.show();
                    ((ThingsFragment)first).refresh();
                }else{
                    fab_delete.show();
                    fab_refresh.hide();
                }
            } else {
                fab_refresh.show();
                if (position==1){
                    ((ShoppingHistoryFragment)second).refresh();
                }else{
                    ((MyPayFragment)third).refresh();
                }
                fab_add.hide();
                fab_buy.hide();
                fab_delete.hide();
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.nav_header:
                if (!AppModel.isLogin()){
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                    DrawerLayout drawer = findViewById(R.id.drawer_layout);
                    drawer.closeDrawer(GravityCompat.START);
                    Log.d("WTF", "start Login");
                }
                break;
            case R.id.text_change_password:
                if (AppModel.isLogin()){
                    Intent intent = new Intent(MainActivity.this, ChangePasswordActivity.class);
                    startActivity(intent);
                    DrawerLayout drawer = findViewById(R.id.drawer_layout);
                    drawer.closeDrawer(GravityCompat.START);
                    Log.d("WTF", "start ChangePassword");
                }
                break;
            case R.id.text_exit:
                if (AppModel.isLogin()){
//                    Toast.makeText(getActivity(),"the number"+position+"has been clicked",Toast.LENGTH_SHORT).show();
                    AppModel.logout();
                    loadData();   
                    Log.d("WTF", "exit");
                }
                break;
            case R.id.fab_refresh:
//                NotificationCompat.Builder mBuilder =
//                        new NotificationCompat.Builder(this)
//                                .setSmallIcon(R.drawable.ic_launch_round)
//                                .setContentTitle("My notification")
//                                .setContentText("Hello World!");
//                Intent resultIntent = new Intent(this, MainActivity.class);
//                PendingIntent resultPendingIntent = PendingIntent.getActivity(
//                        this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//                mBuilder.setContentIntent(resultPendingIntent);
//                Notification notification = mBuilder.build();
//                // Sets an ID for the notification
//                int mNotificationId = 001;
//
//                // Gets an instance of the NotificationManager service
//                NotificationManager mNotifyMgr =
//                        (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//
//                // Builds the notification and issues it.
//                mNotifyMgr.notify(mNotificationId, notification);
//
//                Log.e("WTF","通知");
                if (AppModel.isLogin()){
//                    loadData();
                    ((ThingsFragment)first).refresh();
                    ((ShoppingHistoryFragment)second).refresh();
                    ((MyPayFragment)third).refresh();
                    Toast.makeText(this, "已刷新" ,Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(this, "请先登陆" ,Toast.LENGTH_SHORT).show();
                }
        }
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.main, menu);
//        return true;
//    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.action_menu_main_1:
//                Intent intent = new Intent(this, AboutActivity.class);
//                startActivity(intent);
//                break;
//        }
//        return super.onOptionsItemSelected(item);
//    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        Intent intent = new Intent();

        switch (item.getItemId()) {
            case R.id.nav_settings:
                intent.setClass(this, SettingsActivity.class);
                startActivity(intent);
                break;

            case R.id.nav_about:
                intent.setClass(this, AboutActivity.class);
                startActivity(intent);
                break;
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        mHandler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }

}
