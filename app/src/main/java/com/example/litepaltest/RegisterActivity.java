package com.example.litepaltest;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.githang.statusbar.StatusBarCompat;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.prefs.PreferenceChangeEvent;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

import static com.example.litepaltest.SettingActivity.CHOOSE_PICTURE;
import static com.example.litepaltest.SettingActivity.TAKE_PICTURE;

/**
 * Created by ASUS on 2018/3/10.
 */

public class RegisterActivity extends AppCompatActivity {
    private String userName;
    private String sign;
    private String phone;
    private String password;

    private Button back;
    private Button submit;
    private EditText name;
    private EditText signiture;
    private EditText call;
    private EditText passw;
    private CircleImageView picture;

    //SharedPreferences pref;
    private SharedPreferences.Editor editor;

    //public static String obgID;
    private Bitmap mBitmap;
    protected static final int CHOOSE_PICTURE = 0;
    protected static final int TAKE_PICTURE = 1;
    protected static Uri tempUri;
    private static final int CROP_SMALL_PICTURE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        StatusBarCompat.setStatusBarColor(this, Color.parseColor("#ffffff"),true );

        ActionBar actionbar = getSupportActionBar();
        if(actionbar != null){
            actionbar.hide();
        }

        init();

        submitListener();

        initListeners();

        //按下返回键
        backListener();

        Bmob.initialize(this, "75c88783b55ef9650c609d100ef036b4" );
    }

    //返回键监听
    private void backListener(){
        back.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //返回登录界面
                finish();
            }
        });
    }

    private void submitListener(){
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userName = name.getText().toString();
                sign = signiture.getText().toString();
                phone = call.getText().toString();
                password = passw.getText().toString();
                final MyUser bu = new MyUser();
                bu.setUsername(userName);
                bu.setPassword(password);
                bu.setMobilePhoneNumber(phone);
                bu.setSign(sign);

                bu.signUp(new SaveListener<MyUser>() {
                    @Override
                    public void done(MyUser s, BmobException e) {
                        if (e == null) {
                            Toast.makeText(RegisterActivity.this, "注册成功啦~", Toast.LENGTH_SHORT).show();
                            //obgID = bu.getObjectId();
                            editor.putString("ID", bu.getObjectId());
                            editor.putString("userName", userName);
                            editor.putString("signiture", sign);
                            editor.putString("cellphone", phone);
                            editor.putString("password", password);
                            editor.putBoolean("isLogin", true);
                            editor.apply();

                            finish();
                        } else {
                            Toast.makeText(RegisterActivity.this, "注册失败，用户名或手机号码已存在", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });
    }

    private void init() {
        back = (Button) findViewById(R.id.register_title_back);
        submit = (Button) findViewById(R.id.register_title_submit);
        name = (EditText) findViewById(R.id.in_name);
        signiture = (EditText) findViewById(R.id.sign_in);
        call = (EditText) findViewById(R.id.phone_number);
        passw = (EditText) findViewById(R.id.password);

        //pref = PreferenceManager.getDefaultSharedPreferences(this);
        editor = getSharedPreferences("LoginData", MODE_PRIVATE).edit();
        editor.apply();

        picture = (CircleImageView)findViewById(R.id.iv_image);
    }

    private void initListeners() {
        picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showChoosePicDialog();
            }
        });
    }

    /**
     * 显示修改图片的对话框
     */
    protected void showChoosePicDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);

        builder.setTitle("添加图片");
        String[] items = { "选择本地照片", "拍照" };

        builder.setNegativeButton("取消", null);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case CHOOSE_PICTURE: // 选择本地照片
                        Intent openAlbumIntent = new Intent (Intent.ACTION_GET_CONTENT);

                        openAlbumIntent.setType("image/*");
                        //用startActivityForResult方法，待会儿重写onActivityResult()方法，拿到图片做裁剪操作
                        startActivityForResult(openAlbumIntent, CHOOSE_PICTURE);
                        break;
                    case TAKE_PICTURE: // 拍照
                        Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        tempUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "temp_image.jpg"));

                        // 将拍照所得的相片保存到SD卡根目录
                        openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, tempUri);
                        startActivityForResult(openCameraIntent, TAKE_PICTURE);
                        break;
                }
            }

        });
        builder.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == MainActivity.RESULT_OK) {
            switch (requestCode) {
                case TAKE_PICTURE:
                    cutImage(tempUri); // 对图片进行裁剪处理
                    break;
                case CHOOSE_PICTURE:
                    cutImage(data.getData()); // 对图片进行裁剪处理
                    break;
                case CROP_SMALL_PICTURE:
                    if (data != null) {
                        setImageToView(data); // 让刚才选择裁剪得到的图片显示在界面上
                    }
                    break;
            }
        }
    }

    /**
     * 裁剪图片方法实现
     */
    protected void cutImage(Uri uri) {
        if (uri == null) {
            Log.i("alanjet", "The uri is not exist.");
        }
        tempUri = uri;
        Intent intent = new Intent("com.android.camera.action.CROP");
        //com.android.camera.action.CROP这个action是用来裁剪图片用的
        intent.setDataAndType(uri, "image/*");
        // 设置裁剪
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 150);
        intent.putExtra("outputY", 150);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, CROP_SMALL_PICTURE);
    }

    /**
     * 保存裁剪之后的图片数据
     */
    protected void setImageToView(Intent data) {
        Bundle extras = data.getExtras();
        if (extras != null) {
            mBitmap = extras.getParcelable("data");
            picture.setImageBitmap(mBitmap);//显示图片

            SharedPreferences sharedPreferences=getSharedPreferences("picture",MODE_PRIVATE);
            SharedPreferences.Editor editor=sharedPreferences.edit();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            mBitmap.compress(Bitmap.CompressFormat.PNG, 50, baos);
            String imageBase64 = new String(Base64.encodeToString(baos.toByteArray(),Base64.DEFAULT));
            editor.putString("pic",imageBase64 );
            editor.apply();
        }
    }
}
