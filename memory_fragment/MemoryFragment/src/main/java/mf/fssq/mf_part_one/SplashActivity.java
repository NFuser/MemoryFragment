package mf.fssq.mf_part_one;

import android.content.Intent;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;
import mf.fssq.mf_part_one.util.UserDatabaseHelper;

public class SplashActivity extends AppCompatActivity {

    private UserDatabaseHelper mUserDatabaseHelper;
    private int statue = 0;
    private boolean isFirst=false;

    //region 开启沉浸模式
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
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
        setContentView(R.layout.activity_splash);

        //获取手势锁和应用打开状态
        getstatue();

        if (isFirst){
            Intent intent = new Intent(SplashActivity.this, IntroActivity.class);
            editFirst();
            startActivity(intent);
            finish();
        }else {
            //进入应用程序主界面
            if (statue == 0) {
                //未开启手势锁，直接进入应用
                enterapp(500);
            } else {
                //已开启手势锁，需解锁后方可进入应用
                enterlocked();
            }
        }
    }

    private void editFirst() {
        //SQLCipher初始化时需要初始化库
        SQLiteDatabase.loadLibs(this);

        //打开或创建数据库
        mUserDatabaseHelper = new UserDatabaseHelper(this, "User.db", null, 1);
        SQLiteDatabase db = mUserDatabaseHelper.getReadableDatabase("system");
        String sql="update User set first=? where id=?";
        db.beginTransaction();
        db.execSQL(sql,new Object[]{1,1});
        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();
    }

    private void getstatue() {
        //SQLCipher初始化时需要初始化库
        SQLiteDatabase.loadLibs(this);

        //打开或创建数据库
        mUserDatabaseHelper = new UserDatabaseHelper(this, "User.db", null, 1);
        SQLiteDatabase db = mUserDatabaseHelper.getReadableDatabase("system");
        String sql = "select statue,first from User where id=?";
        db.beginTransaction();
        Cursor cursor = db.rawQuery(sql, new Object[]{1});
        if (cursor.moveToFirst()) {
            statue = cursor.getInt(cursor.getColumnIndex("statue"));
            int first=cursor.getInt(cursor.getColumnIndex("first"));
            Log.w("test"," 打开状态"+first);
            isFirst= first == 0;
        }
        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();
    }

    private void enterlocked() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, AliPayGestureLockActivity.class);
                startActivityForResult(intent, 111);
            }
        }, 500);
    }

    private void enterapp(int x) {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }, x);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 111) {
            if (resultCode == 666) {
                enterapp(200);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
