package com.example.litepaltest;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.githang.statusbar.StatusBarCompat;

import java.io.ByteArrayInputStream;

/**
 * Created by ASUS on 2018/3/10.
 */

public class LoginActivity extends AppCompatActivity {
    private Button login;
    private Button register;
    private EditText name;
    private EditText passw;
    private CircleImageView pic;

    private String userName;
    private String password;
    private String phone;

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        StatusBarCompat.setStatusBarColor(this, Color.parseColor("#ffffff"), true);

        ActionBar actionbar = getSupportActionBar();
        if (actionbar != null) {
            actionbar.hide();
        }

        init();

        LoginListener();

        RegisterListener();
    }

    //对登录按钮的响应
    private void LoginListener(){
        //对"登录按钮的响应，跳转至待办页面
        login.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                String user = name.getText().toString();
                if ((userName.equals(user) || phone.equals(user)) && passw.getText().toString().equals(password)) {
                    Toast.makeText(LoginActivity.this, "登录成功~", Toast.LENGTH_SHORT).show();
                    editor.putBoolean("isLogin", true);
                    editor.apply();

                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
                else{
                    Toast.makeText(LoginActivity.this, "登录名或密码有误哦~", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //对注册文字的响应
    private void RegisterListener(){
        //对"注册"文字的响应，跳转至注册页面
        register.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if (userName.equals("")) {
                    Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                    startActivity(intent);
                }
                else{
                    Toast.makeText(LoginActivity.this, "一人只能注册一个账号哟~", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private  void init(){
        login = (Button)findViewById(R.id.button_Login);
        name = (EditText)findViewById(R.id.Tologin);
        passw = (EditText)findViewById(R.id.password);
        register = (Button) findViewById(R.id.txt_Register);
        pic = (CircleImageView) findViewById(R.id.login_image);

        pref = getSharedPreferences("LoginData", MODE_PRIVATE);
        editor = pref.edit();
        editor.apply();
        userName = pref.getString("userName", "");
        password = pref.getString("password", "");
        phone = pref.getString("cellphone", "");

        if (!userName.equals("")) {
            name.setText(userName);
            passw.setText(password);
        }

        SharedPreferences sharedPreferences=getSharedPreferences("picture",MODE_PRIVATE);
        String temp = sharedPreferences.getString("pic", "");
        ByteArrayInputStream bais = new ByteArrayInputStream(Base64.decode(temp.getBytes(), Base64.DEFAULT));
        if (Drawable.createFromStream(bais, "") != null) {
            pic.setImageDrawable(Drawable.createFromStream(bais, ""));
        }
    }

    @Override
    protected void onResume(){
        super.onResume();
        userName = pref.getString("userName", "");
        password = pref.getString("password", "");
        if (!userName.equals("")) {
            name.setText(userName);
            passw.setText(password);
        }

        SharedPreferences sharedPreferences=getSharedPreferences("picture",MODE_PRIVATE);
        String temp = sharedPreferences.getString("pic", null);
        if (temp != null){
            ByteArrayInputStream bais = new ByteArrayInputStream(Base64.decode(temp.getBytes(), Base64.DEFAULT));
            pic.setImageDrawable(Drawable.createFromStream(bais, ""));
        }
    }
}
