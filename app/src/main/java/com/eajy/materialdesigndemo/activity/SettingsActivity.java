package com.eajy.materialdesigndemo.activity;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;

import com.eajy.materialdesigndemo.R;
import com.eajy.materialdesigndemo.model.AppModel;
import com.eajy.materialdesigndemo.util.NetUtils;

import org.json.JSONObject;

//public class SettingsActivity extends AppCompatPreferenceActivity implements  View.OnClickListener {
public class SettingsActivity extends AppCompatPreferenceActivity {
    /**
     * A preference value change listener that updates the preference's summary to reflect its new value.
     */
    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();
            String key = preference.getKey();

            //修改用户名和手机号码
            if (preference instanceof SwitchPreference) {
                Log.e("WTF",String.valueOf( ((SwitchPreference) preference).isChecked() ));
            }
            else {
                // For all other preferences, set the summary to the value's simple string representation.

//                Log.e("WTF", key + " " + value);
                if (key.equals("Limit number") && !TextUtils.isEmpty((String)value) && AppModel.isNum((String) value)){
                    AppModel.mCache.put(AppModel.getUserName() + "_limit", (int)Integer.parseInt((String) value)*100);
                    preference.setSummary((String)value + "元");
                } else if (key.equals("Phone Number") && !TextUtils.isEmpty((String)value) && AppModel.isPhoneValid((String) value)){
//                    AppModel.mCache.put(AppModel.getUserName() + "_phone", (String) value);
                    AppModel.setPhone((String) value);
                    preference.setSummary(stringValue);
                } else if (key.equals("Card Number") && !TextUtils.isEmpty((String)value) && AppModel.isCardNumValid(stringValue)){
                    AppModel.setCard(stringValue);
                    preference.setSummary(stringValue);
                }else{
                    preference.setSummary("修改失败，请输入合法的值");
                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String response = NetUtils.get(AppModel.BASE_URL + "/change_profile/?token=" + AppModel.getToken() +  "&mobile=" + AppModel.getPhone() + "&account=" + AppModel.getCard() +  "&limit=" + String.valueOf(AppModel.getLimitNum()));
                            Log.d("WTF", "修改" + response);
                            JSONObject jsonObject = new JSONObject(response);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
            return true;
        }
    };


    /**
     * Binds a preference's summary to its value. More specifically, when the preference's value is changed,
     * its summary (line of text below the preference title) is updated to reflect the value. The summary is also
     * immediately updated upon calling this method. The exact display format is dependent on the type of preference.
     */
    private static void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        // Trigger the listener immediately with the preference's current value.
        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager.getDefaultSharedPreferences(preference.getContext()).getString(preference.getKey(), ""));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        addPreferencesFromResource(R.xml.preferences_settings);

//        bindPreferenceSummaryToValue(findPreference("example_text"));
//        bindPreferenceSummaryToValue(findPreference("Account ID"));

        if (AppModel.isLogin()){
//            bindPreferenceSummaryToValue(findPreference("Username"));
            bindPreferenceSummaryToValue(findPreference("Phone Number"));
            bindPreferenceSummaryToValue(findPreference("Limit number"));
            bindPreferenceSummaryToValue(findPreference("Card Number"));
            bindPreferenceSummaryToValue(findPreference("notifications_new_message_ringtone"));
        }

        String userName = AppModel.getUserName();
        if (userName.equals(""))
            userName = "请先登录";
        findPreference("Username").setSummary(userName);
        findPreference("Username").setDefaultValue(userName);

        String phoneNum = AppModel.getPhone();
        if (phoneNum.equals(""))
            phoneNum = "请先登录";
        findPreference("Phone Number").setSummary(phoneNum);
        findPreference("Phone Number").setDefaultValue(phoneNum);

        int limitNum =AppModel.getLimitNum();
        if (limitNum==-1){
            findPreference("Limit number").setDefaultValue(limitNum);
            findPreference("Limit number").setSummary("请先登录");
        }
        findPreference("Limit number").setDefaultValue(limitNum);
        findPreference("Limit number").setSummary(String.valueOf(limitNum/100)+"元");

        String card = AppModel.getCard();
        if (card.equals(""))
            card = "请先登陆";
        findPreference("Card Number").setSummary(card);
        findPreference("Card Number").setDefaultValue(card);

//        bindPreferenceSummaryToValue(findPreference("notifications_new_message_ringtone"));
//        bindPreferenceSummaryToValue(findPreference("sync_frequency"));

//        Preference pref = findPreference("clear_cache");
//        pref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
//            @Override
//            public boolean onPreferenceClick(Preference preference) {
//                Toast.makeText(SettingsActivity.this, getString(R.string.pref_on_preference_click), Toast.LENGTH_SHORT).show();
//                return false;
//            }
//        });

    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            if (!super.onMenuItemSelected(featureId, item)) {
                NavUtils.navigateUpFromSameTask(this);
                //startActivity(new Intent(this, SettingsActivity.class));
                //OR USING finish();
            }
            return true;
        }
        return super.onMenuItemSelected(featureId, item);
    }

}
