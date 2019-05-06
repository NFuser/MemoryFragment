package mf.fssq.mf_part_one;

import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import mf.fssq.mf_part_one.entity.Compare;
import mf.fssq.mf_part_one.entity.Dairy;
import mf.fssq.mf_part_one.util.DetectAdapter;
import mf.fssq.mf_part_one.util.DiaryDatabaseHelper;
import mf.fssq.mf_part_one.util.MyAdapter;

public class AchievementActivity extends AppCompatActivity {

    private GridView mGridview;
    private ImageView back;
    private List<Dairy> date_list = new ArrayList();
    private MyAdapter mAdapter;
    private DiaryDatabaseHelper mDiaryDatabaseHelper;
    private TextView sum,con;

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
        setContentView(R.layout.activity_achievement);
        initId();
        initDateBase();
        initListener();
        initData();
    }

    private void initData() {
        sum.setText("累计记录： "+String.valueOf(date_list.size())+"天");

        if (date_list.size()==0){
            con.setText("连续记录： 0天");
        }else {
            int max=1;
            int temp=1;
            for (int i=1;i<date_list.size();i++) {
                Compare compare1=parting(date_list.get(i).getTitle());
                int date1=compare1.getDate();
                int month1=compare1.getMonth();
                int year1=compare1.getYear();

                Calendar calendar1=Calendar.getInstance();
                calendar1.clear();
                calendar1.set(date1,month1,year1);
                long millis1=calendar1.getTimeInMillis();
                Log.w("test"," millis1:"+millis1);

                Compare compare2=parting(date_list.get(i-1).getTitle());
                int date2=compare2.getDate();
                int month2=compare2.getMonth();
                int year2=compare2.getYear();

                Calendar calendar2=Calendar.getInstance();
                calendar2.clear();
                calendar2.set(date2,month2,year2);
                long millis2=calendar2.getTimeInMillis();
                Log.w("test"," millis2:"+millis2);

                if (((millis1-millis2)/1000000)<=31536){
                    temp++;
                }else {
                    if (temp>max){
                        max=temp;
                    }
                    temp=1;
                }
            }
            int x=max>temp?max:temp;
            con.setText("连续记录： "+x+"天");
        }
    }

    private Compare parting(String str){
        String []title=str.split("\\.");

        int month=0;
        int date=Integer.parseInt(title[0]);
        switch (title[1]){
            case "January":month= 1;break;
            case "February":month= 2;break;
            case "March":month= 3;break;
            case "April":month= 4;break;
            case "May":month= 5;break;
            case "June":month= 6;break;
            case "July":month= 7;break;
            case "August":month= 8;break;
            case "September":month= 9;break;
            case "October":month= 10;break;
            case "November":month= 11;break;
            case "December":month= 12;break;
        }
        int year=Integer.parseInt(title[2]);

        Compare compare=new Compare(date,month,year);
        return compare;
    }

    private void initListener() {
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initId() {
        mGridview = findViewById(R.id.main_gridview);
        back=findViewById(R.id.back);
        sum=findViewById(R.id.sum);
        con=findViewById(R.id.con);
    }

    private void initDateBase() {
        //SQLCipher初始化时需要初始化库
        SQLiteDatabase.loadLibs(this);

        //打开或创建数据库
        mDiaryDatabaseHelper = new DiaryDatabaseHelper(this, "Diary.db", null, 1);
        SQLiteDatabase db = mDiaryDatabaseHelper.getReadableDatabase("token");
        Cursor cursor = db.rawQuery("select * from Diary", null);
        //扫描数据库
        while (cursor.moveToNext()) {
            Log.w("init", "数据库不为空，开始查询数据");
            int id = cursor.getInt(0);
            String time = cursor.getString(cursor.getColumnIndex("time"));
            String title = cursor.getString(cursor.getColumnIndex("title"));
            String week = cursor.getString(cursor.getColumnIndex("week"));
            String content = cursor.getString(cursor.getColumnIndex("content"));

            //创建一个Dairy对象存储一条数据
            Dairy dairy = new Dairy(time, title, week, content);
            date_list.add(dairy);
            Log.w("init", "dairy存储形式" + date_list.toString());
        }
        //关闭数据库
        db.close();

        mAdapter = new DetectAdapter(this,date_list);

        mGridview.setAdapter(mAdapter);
    }
}
