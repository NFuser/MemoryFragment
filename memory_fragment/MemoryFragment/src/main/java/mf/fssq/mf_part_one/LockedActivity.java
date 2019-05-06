package mf.fssq.mf_part_one;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;

import mf.fssq.mf_part_one.util.UserDatabaseHelper;

public class LockedActivity extends AppCompatActivity {

    private ImageView back;
    private Switch on_off;
    private Context mContext;
    private final int KEY=100;
    private UserDatabaseHelper mUserDatabaseHelper;
    private String password;
    private int sta=0;

    //region 开启沉浸模式
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus && Build.VERSION.SDK_INT >= 19) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext=this;
        setContentView(R.layout.activity_locked);
        findId();
        initData();
        clickListener();
    }

    private void initData() {
        //打开或创建数据库
        mUserDatabaseHelper = new UserDatabaseHelper(mContext, "User.db", null, 1);
        SQLiteDatabase db = mUserDatabaseHelper.getReadableDatabase("system");
        String sql="select statue from User where id=?";
        Cursor cursor= db.rawQuery(sql,new Object[]{1});
        if (cursor.getCount()!=0){
            if (cursor.moveToFirst()){
                sta=cursor.getInt(cursor.getColumnIndex("statue"));
                if (sta!=0){
                    on_off.setChecked(true);
                }else {
                    on_off.setChecked(false);
                }
            }
        }
    }

    private void clickListener() {
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        on_off.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.w("test ",isChecked+" ");
                if(isChecked){
                    //开关开启状态
                    Intent intent=new Intent(mContext,AliPayGestureLockActivity.class);
                    startActivityForResult(intent,KEY);
                }else {
                    //关闭开关
                    //打开或创建数据库
                    mUserDatabaseHelper = new UserDatabaseHelper(mContext, "User.db", null, 1);
                    SQLiteDatabase db = mUserDatabaseHelper.getReadableDatabase("system");
                    String sql="update User set statue=? where id=?";
                    db.execSQL(sql,new Object[]{0,1});
                    db.close();
                    Toast.makeText(mContext,"手势锁已关闭",Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode){
            case KEY:
                if (resultCode==666){
                    //密码设置成功，开启数据库，修改密码状态和密码
                    Intent intent=data;
                    Bundle bundle=intent.getBundleExtra("Data");
                    password=bundle.getString("pwd");
                    Log.w("test","接受到的password值："+password);
                    //打开或创建数据库
                    mUserDatabaseHelper = new UserDatabaseHelper(mContext, "User.db", null, 1);
                    SQLiteDatabase db = mUserDatabaseHelper.getReadableDatabase("system");
                    String sql="update User set statue=? ,password=? where id=?";
                    db.execSQL(sql,new Object[]{1,password,1});
                    db.close();
                    setResult(666);
                    finish();
                }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void findId() {
        back=findViewById(R.id.back);
        on_off=findViewById(R.id.on_off);
    }
}
