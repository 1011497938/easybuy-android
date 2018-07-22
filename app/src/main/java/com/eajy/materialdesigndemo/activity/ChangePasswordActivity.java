package com.eajy.materialdesigndemo.activity;

import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.eajy.materialdesigndemo.R;
import com.eajy.materialdesigndemo.model.AppModel;
import com.eajy.materialdesigndemo.util.NetUtils;

import org.json.JSONObject;

public class ChangePasswordActivity extends AppCompatActivity implements View.OnClickListener {

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world"
    };

    private TextInputLayout input_new_password, input_former_password;
    private EditText formerPasswordView, newPasswordView;
    private Button btn_changepw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        initView();
    }

    public void initView() {
        formerPasswordView = findViewById(R.id.tv_old_password);
        newPasswordView = findViewById(R.id.tv_new_password);
        input_former_password = findViewById(R.id.layoyt_former_password);
        input_new_password = findViewById(R.id.layoyt_new_password);

        btn_changepw = findViewById(R.id.btn_change_password);
        btn_changepw.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_change_password:
                attemptLogin();
                break;
        }
    }


    private void attemptLogin() {

        // Reset errors.
        input_new_password.setError(null);
        input_former_password.setError(null);

        String newPw = newPasswordView.getText().toString();
        String oldPw = formerPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(oldPw)) {
            input_new_password.setError("请填写旧的密码");
            focusView = formerPasswordView;
            cancel = true;
        } else if (TextUtils.isEmpty(newPw)) {
            input_former_password.setError("请填写新的密码");
            focusView = newPasswordView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            //验证登陆
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            try {
                Log.d("WTF", "Token=" + AppModel.getToken());
                String response = NetUtils.get(AppModel.BASE_URL + "/change_pswd/?new_password=" + newPw +  "&old_password=" + oldPw + "&token=" + AppModel.getToken());
                JSONObject jsonObject = new JSONObject(response);
//                Log.d("WTF", jsonObject.optString("message"));
                if (jsonObject.optString("message").equals("Success")){
                    Toast.makeText(this,"修改成功",Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(this,jsonObject.optString("message"),Toast.LENGTH_SHORT).show();
                }
            }catch (Exception e){
                Toast.makeText(this,"网络连接错误，请重试",Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
    }
}
