package com.eajy.materialdesigndemo.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
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

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world"
    };

    // Keep track of the login task to ensure we can cancel it if requested.

    private AutoCompleteTextView mUserNameView, mPhoneView, mCardView;
    private TextInputLayout input_user_name, input_password, input_card_num, input_phone;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private Button login_register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initView();
    }

    public void initView() {
        mLoginFormView = findViewById(R.id.register_form_login);
        mProgressView = findViewById(R.id.register_progress_login);

        mUserNameView = findViewById(R.id.tv_register_user_name);
        mPasswordView = findViewById(R.id.tv_register_password);
        mCardView = findViewById(R.id.tv_user_card_num);
        mPhoneView = findViewById(R.id.tv_user_phone);

        input_user_name = findViewById(R.id.input_register_user_name);
        input_password = findViewById(R.id.input_rehister_password);
        input_phone = findViewById(R.id.input_register_phone);
        input_card_num = findViewById(R.id.input_register_card_num);

        login_register = findViewById(R.id.btn_register);
        login_register.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_register:
//                Toast.makeText(this,"网络连接错误，请重试",Toast.LENGTH_SHORT).show();
                attemptRegister();
                break;
        }
    }


    /**
     * Attempts to sign in or register the account specified by the login form. If there are form errors
     * (invalid email, missing fields, etc.), the errors are presented and no actual login attempt is made.
     */
    private void attemptRegister() {
        login_register.setVisibility(View.INVISIBLE);

        // Reset errors.
        input_user_name.setError(null);
        input_password.setError(null);
        input_phone.setError(null);
        input_card_num.setError(null);


        String userName = mUserNameView.getText().toString();
        String password = mPasswordView.getText().toString();
        String cardNum = mCardView.getText().toString();
        String phone = mPhoneView.getText().toString();

        boolean cancel = false;
        View focusView = null;


        if (TextUtils.isEmpty(userName)) {
            input_user_name.setError("input your name!");
            focusView = mUserNameView;
            cancel = true;
        } else if (!AppModel.isNameValid(userName)) {
            input_user_name.setError(getString(R.string.error_invalid_name));
            focusView = mUserNameView;
            cancel = true;
        } else if(TextUtils.isEmpty(password)){
            input_card_num.setError("input your password!");
            focusView = mPasswordView;
            cancel = true;
        } else if (!AppModel.isPasswordValid(password)) {
            input_password.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        } else if(TextUtils.isEmpty(phone)){
            input_phone.setError("input your phone number!");
            focusView = mPhoneView;
            cancel = true;
        } else if (!AppModel.isPhoneValid(phone)) {
            input_phone.setError("input valid phone num");
            focusView = mPhoneView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            try {
                String response = NetUtils.get(AppModel.BASE_URL + "/create_u/?type=True&name="+ userName+ "&mobile="+phone+"&password=" + password + "&account=" + cardNum);
                JSONObject jsonObject = new JSONObject(response);
                if (jsonObject.optString("message").equals("Success")){
                    Toast.makeText(this,"注册成功",Toast.LENGTH_SHORT).show();
                    response = NetUtils.get(AppModel.BASE_URL + "/auth/?username=" + userName +  "&password=" + password);
                    jsonObject = new JSONObject(response);
                    Log.d("WTF", response);
                    if (jsonObject.optString("message").equals("Success")){
                        String token = jsonObject.optString("token");
                        Log.d("WTF", "token:" + token);
                        if (token!=null){
                            response = NetUtils.get(AppModel.BASE_URL + "/user/?token=" + token);
                            jsonObject = new JSONObject(response);
                            AppModel.login(userName, jsonObject.optString("mobile"), jsonObject.optString("account"), token, Integer.parseInt(jsonObject.optString("money")), jsonObject.optString("uid"), Integer.parseInt(jsonObject.optString("limit")));
                            this.finish();
                        }else{
//                            Toast.makeText(this,"网络连接错误，请重试",Toast.LENGTH_SHORT).show();
                        }
                    }
                }else{
                    Toast.makeText(this,"注册失败, 用户名已存在",Toast.LENGTH_SHORT).show();
                }
            }catch (Exception e){
//                Toast.makeText(this,"网络连接错误，请重试",Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }

        login_register.setVisibility(View.VISIBLE);
    }
    public void hideInput(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) this.getSystemService(INPUT_METHOD_SERVICE);
        if (inputMethodManager != null) {
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void showProgress(final boolean show) {
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        mLoginFormView.animate().setDuration(shortAnimTime).alpha(show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });

        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        mProgressView.animate().setDuration(shortAnimTime).alpha(show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }

}
