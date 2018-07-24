package com.eajy.easybuy.service;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.ConditionVariable;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.eajy.easybuy.activity.MainActivity;
import com.eajy.easybuy.util.NetUtils;
import com.eajy.materialdesigndemo.R;
import com.eajy.easybuy.model.AppModel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 实现每5秒发一条状态栏通知的Service
 *
 * @description：
 * @author ldm
 * @date 2016-4-29 上午9:16:20
 */
public class NotifyService extends Service {
    // 状态栏通知的管理类对象，负责发通知、清楚通知等
    private NotificationManager mNM;
    // 使用Layout文件的对应ID来作为通知的唯一识别
//    private static int MOOD_NOTIFICATIONS = R.layout.status_bar_notifications;
    /**
     * Android给我们提供ConditionVariable类，用于线程同步。提供了三个方法block()、open()、close()。 void
     * block() 阻塞当前线程，直到条件为open 。 void block(long timeout)阻塞当前线程，直到条件为open或超时
     * void open()释放所有阻塞的线程 void close() 将条件重置为close。
     */
    private ConditionVariable mCondition;
    Timer timer = new Timer();
    Handler handler = new Handler();
    final Context context = this;

    static int notifyId = 1000;
    @Override
    public void onCreate() {
        // 状态栏通知的管理类对象，负责发通知、清楚通知等
        mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (AppModel.isLogin()) {
                    String token = AppModel.getToken();
                    JSONArray jsonArray = new JSONArray();
                    String response = "";
                    //获得所有设备信息
                    try {
                        //验证登陆
                        Log.d("WTF", "token = " + token);
                        response = NetUtils.get(AppModel.BASE_URL + "/search_d/?token=" + token);
                        Log.d("WTF", "后台" + response);
                        jsonArray = new JSONArray(response);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = (JSONObject) jsonArray.get(i);

                            int remain = jsonObject.optInt("state");
                            String did = jsonObject.optString("did");
                            String dName = jsonObject.optString("type");

                            if (remain < 20) {
                                boolean isNoticed = new JSONObject( NetUtils.get(AppModel.BASE_URL + "/is_noticed/?token=" + token + "&did=" + did)).optString("result").equals("true");
                                if (!isNoticed){
                                    response = NetUtils.get(AppModel.BASE_URL + "/set_noticed/?token=" + token + "&did=" + did);
                                    JSONObject jsonObject1 = new JSONObject(response);
                                    if (jsonObject1.optString("message").equals("Success")){
                                        NotificationCompat.Builder mBuilder =
                                                new NotificationCompat.Builder(context)
                                                        .setSmallIcon(R.drawable.ic_launch_round)
                                                        .setContentTitle("Easy Buy")
                                                        .setContentText( dName +  "快用完了!");
                                        mBuilder.setAutoCancel(true);//自己维护通知的消失
                                        Intent resultIntent = new Intent(context, MainActivity.class);
                                        PendingIntent resultPendingIntent = PendingIntent.getActivity(
                                                context, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                                        mBuilder.setContentIntent(resultPendingIntent);
                                        Notification notification = mBuilder.build();
                                        // Sets an ID for the notification
                                        int mNotificationId = notifyId++;

                                        // Gets an instance of the NotificationManager service
                                        NotificationManager mNotifyMgr =
                                                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

                                        // Builds the notification and issues it.
                                        mNotifyMgr.notify(mNotificationId, notification);
                                    }
                                }
                            } else {
                                NetUtils.get(AppModel.BASE_URL + "/set_not_noticed/?token=" + token + "&did=" + did);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        },0,2000);
    }
    @Override
    public void onDestroy() {
        // 取消通知功能
//        mNM.cancel(MOOD_NOTIFICATIONS);
        // 停止线程进一步生成通知
        mCondition.open();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
