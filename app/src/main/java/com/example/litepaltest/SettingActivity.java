package com.example.litepaltest;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.githang.statusbar.StatusBarCompat;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;

public class SettingActivity extends AppCompatActivity {

    private Button back;
    private CircleImageView mImage;
    private TextView username;
    private TextView signature;
    private RelativeLayout setting;
    private RelativeLayout ranking;
    private RelativeLayout aboutUs;

    private SharedPreferences pref;
    private String name;
    private String sign;

    private SharedPreferences prefpic;

    private Bitmap mBitmap;
    protected static final int CHOOSE_PICTURE = 0;
    protected static final int TAKE_PICTURE = 1;
    protected static Uri tempUri;
    private static final int CROP_SMALL_PICTURE = 2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting);

        StatusBarCompat.setStatusBarColor(this, Color.parseColor("#ffffff"),true );

        ActionBar actionbar = getSupportActionBar();
        if(actionbar != null){
            actionbar.hide();
        }

        init();
        initListeners();

        //按下确认键
        backListener();

        //按下设置键
        settingListener();
    }

    private void settingListener() {
        //对"设置"按钮的响应，跳转至登陆页面
        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!name.equals("")){
                    Intent intent = new Intent(SettingActivity.this, RegulateActivity.class);
                    startActivity(intent);
                }
                else{
                    Intent intent = new Intent(SettingActivity.this, LoginActivity.class);
                    startActivity(intent);
                }

            }
        });

        //好友排行
        ranking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingActivity.this, RankingActivity.class);
                startActivity(intent);
            }
        });

        //关于我们
        aboutUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingActivity.this, AboutActivity.class);
                startActivity(intent);
            }
        });

    }

    private void init() {
        pref = getSharedPreferences("LoginData", MODE_PRIVATE);
        mImage= (CircleImageView) findViewById(R.id.iv_image);
        username = (TextView)findViewById(R.id.in_name);
        signature = (TextView)findViewById(R.id.sign_in);
        setting = (RelativeLayout)findViewById(R.id.setting);
        ranking = (RelativeLayout)findViewById(R.id.ranking_list);
        aboutUs = (RelativeLayout)findViewById(R.id.about);

        name = pref.getString("userName", "");
        sign = pref.getString("signiture", "");

        if (!name.equals("")) {
            username.setText(name);
            signature.setText(sign);
        }
        else{
            username.setText("未登录");
            signature.setText("未登录");
        }

        SharedPreferences sharedPreferences=getSharedPreferences("picture",MODE_PRIVATE);
        String temp = sharedPreferences.getString("pic", null);
        if (temp != null){
            ByteArrayInputStream bais = new ByteArrayInputStream(Base64.decode(temp.getBytes(), Base64.DEFAULT));
            mImage.setImageDrawable(Drawable.createFromStream(bais, ""));
        }
    }

    private void initListeners() {
        mImage.setOnClickListener(new View.OnClickListener() {
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
        AlertDialog.Builder builder = new AlertDialog.Builder(SettingActivity.this);

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
                        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()
                                + "/test/" + System.currentTimeMillis() + ".jpg");
                        file.getParentFile().mkdirs();

                        //改变Uri  com.xykj.customview.fileprovider注意和xml中的一致
                        tempUri = FileProvider.getUriForFile(SettingActivity.this, "com.example.litepaltest.fileprovider", file);

                        // 将拍照所得的相片保存到SD卡根目录
                        openCameraIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
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
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
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
            mImage.setImageBitmap(mBitmap);//显示图片

            SharedPreferences sharedPreferences=getSharedPreferences("picture",MODE_PRIVATE);
            SharedPreferences.Editor editor=sharedPreferences.edit();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            mBitmap.compress(Bitmap.CompressFormat.PNG, 50, baos);
            String imageBase64 = new String(Base64.encodeToString(baos.toByteArray(),Base64.DEFAULT));
            editor.putString("pic",imageBase64 );
            editor.apply();
        }
    }

    //返回键监听
    private void backListener(){
        back = (Button)findViewById(R.id.title_back);
        back.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //返回
                finish();
            }
        });
    }

    @Override
    protected void onResume(){
        super.onResume();

        SharedPreferences sharedPreferences=getSharedPreferences("picture",MODE_PRIVATE);
        String temp = sharedPreferences.getString("pic", null);
        if (temp != null){
            ByteArrayInputStream bais = new ByteArrayInputStream(Base64.decode(temp.getBytes(), Base64.DEFAULT));
            mImage.setImageDrawable(Drawable.createFromStream(bais, ""));
        }
    }
}
