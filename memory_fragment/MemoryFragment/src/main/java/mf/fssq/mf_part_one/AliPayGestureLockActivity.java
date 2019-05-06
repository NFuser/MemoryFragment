package mf.fssq.mf_part_one;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.wangnan.library.GestureLockThumbnailView;
import com.wangnan.library.GestureLockView;
import com.wangnan.library.listener.OnGestureLockListener;
import com.wangnan.library.painter.AliPayPainter;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;

import mf.fssq.mf_part_one.util.UserDatabaseHelper;

public class AliPayGestureLockActivity extends AppCompatActivity implements OnGestureLockListener {
    GestureLockThumbnailView mGestureLockThumbnailView;

    GestureLockView mGestureLockView;
    private ImageView image;
    private TextView name,bio,point;
    private Context mContext;
    private UserDatabaseHelper mUserDatabaseHelper;
    private int AGAIN=1;
    private final int IS_OK=666;
    private String word1,word2;

    TextView mCurrentPassword;

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
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alipay);

        findId();
        initData();
    }

    private void initData() {
        //SQLCipher初始化时需要初始化库
        SQLiteDatabase.loadLibs(mContext);

        //打开或创建数据库
        mUserDatabaseHelper = new UserDatabaseHelper(mContext, "User.db", null, 1);
        SQLiteDatabase db = mUserDatabaseHelper.getReadableDatabase("system");
        String sql="select name,bio,icon from User where id=?";
        Cursor cursor=db.rawQuery(sql,new Object[1]);
        if (cursor.moveToFirst()){
            String getname=cursor.getString(cursor.getColumnIndex("name"));
            String getbio=cursor.getString(cursor.getColumnIndex("bio"));
            //第一步，从数据库中读取出相应数据，并保存在字节数组中
            byte[] blob=cursor.getBlob(cursor.getColumnIndex("icon"));
            //第二步，调用BitmapFactory的解码方法decodeByteArray把字节数组转换为Bitmap对象
            Bitmap bmp =BitmapFactory.decodeByteArray(blob,0,blob.length);
            //第三步，调用BitmapDrawable构造函数生成一个BitmapDrawable对象，该对象继承Drawable对象，所以在需要处直接使用该对象即可
            BitmapDrawable icon = new BitmapDrawable(mContext.getResources(),bmp);

            name.setText(getname);
            bio.setText(getbio);
            image.setImageDrawable(icon);
        }
        cursor.close();
        db.close();
    }

    private void findId() {
        mContext=this;
        mGestureLockView = findViewById(R.id.glv);
        mGestureLockView.setPainter(new AliPayPainter());
        mGestureLockView.setGestureLockListener(this);
        image=findViewById(R.id.image);
        name=findViewById(R.id.name);
        bio=findViewById(R.id.bio);
        mCurrentPassword =  findViewById(R.id.tv_current_passord);
        mGestureLockThumbnailView =  findViewById(R.id.gltv);
        point=findViewById(R.id.point);
    }

    /**
     * 解锁开始监听方法
     */
    @Override
    public void onStarted() {

    }

    /**
     * 解锁密码改变监听方法
     */
    @Override
    public void onProgress(String progress) {
//        mCurrentPassword.setText("当前密码: " + progress);
//        mGestureLockThumbnailView.setThumbnailView(progress, 0xFF1E8BDE);
    }

    /**
     * 解锁完成监听方法
     */
    @Override
    public void onComplete(String result) {
        if (TextUtils.isEmpty(result)) {
            return;
        }
//        else if ("012345678".equals(result)) {
//            Toast.makeText(AliPayGestureLockActivity.this, "密码正确O(∩_∩)O~", Toast.LENGTH_SHORT).show();
//            mGestureLockView.clearView();
//        } else {
//            Toast.makeText(AliPayGestureLockActivity.this, "密码错误o(╯□╰)o~", Toast.LENGTH_SHORT).show();
//            mGestureLockView.showErrorStatus(400);
//        }
        else if (AGAIN==1){
            AGAIN+=1;
            word1=result;
            Log.w("test"," word1:"+word1);
            mGestureLockView.clearView();
        }else {
            word2=result;
            Log.w("test"," word2:"+word2);
            if (word1.equals(word2)){
                point.setText("密码设置成功，密码为:"+word1);
                Intent intent=new Intent(this,LockedActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("pwd",word1);
                intent.putExtra("Data",bundle);
                setResult(IS_OK,intent);
                finish();
            }else {
                Toast.makeText(this,"密码不一致，请重新输入",Toast.LENGTH_LONG).show();
                AGAIN=1;
            }
            mGestureLockView.clearView();
        }
    }


}
