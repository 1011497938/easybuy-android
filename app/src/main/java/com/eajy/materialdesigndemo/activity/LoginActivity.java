package com.eajy.materialdesigndemo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.eajy.materialdesigndemo.R;
import com.eajy.materialdesigndemo.model.AppModel;
import com.eajy.materialdesigndemo.util.NetUtils;

import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world"
    };

    // Keep track of the login task to ensure we can cancel it if requested.
    private AutoCompleteTextView mUserNameView;
    private TextInputLayout input_user_name, input_password;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private Button login_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if ((AppModel.isLogin()))
            this.finish();
    }

    public void initView() {
        mLoginFormView = findViewById(R.id.form_login);
        mProgressView = findViewById(R.id.progress_login);
        mUserNameView = findViewById(R.id.tv_user_name);
        mPasswordView = findViewById(R.id.tv_password);
        input_user_name = findViewById(R.id.input_user_name);
        input_password = findViewById(R.id.input_password);

        login_button = findViewById(R.id.btn_login);
        login_button.setOnClickListener(this);
        Button register = findViewById(R.id.btn_to_register);
        register.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:
                attemptLogin();
                break;

            case R.id.btn_to_register:
//                Snackbar.make(v, getString(R.string.snackbar_register), Snackbar.LENGTH_LONG)
//                        .setAction("^_^", null).show();
                Intent intent = new Intent(this, RegisterActivity.class);
                startActivity(intent);
                break;
        }
    }


    /**
     * Attempts to sign in or register the account specified by the login form. If there are form errors
     * (invalid email, missing fields, etc.), the errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
//        hideInput(login_button);   //不然疯狂点击会出错
        login_button.setVisibility(View.INVISIBLE);
        // Reset errors.
        input_user_name.setError(null);
        input_password.setError(null);

        String userName = mUserNameView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(userName)) {
            input_user_name.setError(getString(R.string.error_no_name));
            focusView = mUserNameView;
            cancel = true;
        } else if (!AppModel.isNameValid(userName)) {
            input_user_name.setError(getString(R.string.error_invalid_name));
            focusView = mUserNameView;
            cancel = true;
        } else if (!TextUtils.isEmpty(password) && !AppModel.isPasswordValid(password)) {
            input_password.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        } else if (AppModel.isNameValid(userName) && TextUtils.isEmpty(password)) {
            input_password.setError(getString(R.string.error_no_password));
            focusView = mPasswordView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            //验证登陆
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            try {
                String response = NetUtils.get(AppModel.BASE_URL + "/auth/?username=" + userName +  "&password=" + password);
                JSONObject jsonObject = new JSONObject(response);
//                Log.d("WTF", jsonObject.optString("message"));
                if (jsonObject.optString("message").equals("Success")){
                    String token = jsonObject.optString("token");
                    Log.d("WTF", "token:" + token);
                    if (token!=null){
                        response = NetUtils.get(AppModel.BASE_URL + "/user/?token=" + token);
                        jsonObject = new JSONObject(response);
                        AppModel.login(userName, jsonObject.optString("mobile"), jsonObject.optString("account"), token, Integer.parseInt(jsonObject.optString("money")), jsonObject.optString("uid"), Integer.parseInt(jsonObject.optString("limit")));
                        this.finish();
                    }else{
                        Toast.makeText(this,"网络连接错误，请重试",Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(this,"用户名或者密码错误",Toast.LENGTH_SHORT).show();
                }
            }catch (Exception e){
//                Toast.makeText(this,"网络连接错误，请重试",Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }

        }
        login_button.setVisibility(View.VISIBLE);
    }


    public void hideInput(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) this.getSystemService(INPUT_METHOD_SERVICE);
        if (inputMethodManager != null) {
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
