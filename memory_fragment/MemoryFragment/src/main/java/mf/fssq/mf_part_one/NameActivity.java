package mf.fssq.mf_part_one;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import net.sqlcipher.database.SQLiteDatabase;

import mf.fssq.mf_part_one.util.UserDatabaseHelper;

public class NameActivity extends AppCompatActivity {

    private Context mContext;
    private EditText edit_name;
    private ImageView back,save;
    private UserDatabaseHelper mUserDatabaseHelper;

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
        setContentView(R.layout.activity_name);
        mContext=this;

        findId();

        clickListener();
    }

    private void clickListener() {
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //SQLCipher初始化时需要初始化库
                SQLiteDatabase.loadLibs(mContext);

                //打开或创建数据库
                mUserDatabaseHelper = new UserDatabaseHelper(mContext, "User.db", null, 1);
                SQLiteDatabase db = mUserDatabaseHelper.getReadableDatabase("system");
                String sql="update User set name=? where id=?";
                db.beginTransaction();
                db.execSQL(sql,new Object[]{edit_name.getText().toString(),1});
                db.setTransactionSuccessful();
                db.endTransaction();

                Intent intent=new Intent();
                intent.putExtra("name",edit_name.getText().toString());
                setResult(66,intent);
                finish();
            }
        });
    }

    private void findId() {
        edit_name=findViewById(R.id.edit_name);
        back=findViewById(R.id.back);
        save=findViewById(R.id.save);
    }
}
