package mf.fssq.mf_part_one;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import mf.fssq.mf_part_one.entity.ListRecord;
import mf.fssq.mf_part_one.entity.LatelyTime;
import mf.fssq.mf_part_one.util.DiaryDatabaseHelper;
import mf.fssq.mf_part_one.util.MyAdapter;
import mf.fssq.mf_part_one.entity.SingleCurrentTime;
import mf.fssq.mf_part_one.fragment.HomeFragment;
import mf.fssq.mf_part_one.fragment.UserFragment;

public class MainActivity extends AppCompatActivity {

    private Context mContext;
    private DrawerLayout drawlayout;
    private ListView lv_left;

    private RadioGroup radiogroup;
    private Button button_add;
    //date_list ListRecord类型列表，用于存储ListView中总记录日期的记录日期和星期
    private List<ListRecord> date_list = new ArrayList<>();
    private MyAdapter mAdapter;
    //String类型列表，saveId用于存储数据库中各记录日期的id值
    private List<String> saveId = new ArrayList<>();

    private DiaryDatabaseHelper mDiaryDatabaseHelper;
    private final int LIST_VIEW = 1;

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
        setContentView(R.layout.activity_main);

        //region findViewById()
        initId();

        //region 初始化数据库相关操作
        initDataBase();

        //region 控件监听
        onClickListen();

        //region 初始化界面，添加fragment
        initView();
    }

    @Override
    public void onBackPressed() {
        //不起作用，需再调试
        // super.onBackPressed(); 	不要调用父类的方法
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == LIST_VIEW) {
            if (resultCode == 666) {

                HomeFragment.getInstance().refresh();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    //region 初始化界面，添加fragment
    private void initView() {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.framelayout, UserFragment.getInstance());
        ft.add(R.id.framelayout, HomeFragment.getInstance());
        ft.hide(UserFragment.getInstance());
        ft.commit();
    }

    //region 初始化数据库相关操作
    private void initDataBase() {
        //SQLCipher初始化时需要初始化库
        SQLiteDatabase.loadLibs(mContext);

        //打开或创建数据库
        mDiaryDatabaseHelper = new DiaryDatabaseHelper(this, "Diary.db", null, 1);
        SQLiteDatabase db = mDiaryDatabaseHelper.getReadableDatabase("token");
        db.beginTransaction();
        Cursor cursor = db.rawQuery("select * from Diary", null);
        int x = 0;
        //扫描数据库
        while (cursor.moveToNext()) {
            Log.w("test", " init 数据库不为空，开始查询数据");
            int id = cursor.getInt(0);
            String title = cursor.getString(cursor.getColumnIndex("title"));
            String week = cursor.getString(cursor.getColumnIndex("week"));

            //依次存储
            saveId.add(String.valueOf(id));
            Log.w("test", " init id: " + id);
            Log.w("test", " init id存储 " + "下标：" + x + " 值：" + saveId.get(x));
            Log.w("test", " init 输出saveId "  + saveId.toString());

            x++;

            //创建一个Dairy对象存储一条数据
            ListRecord listRecord = new ListRecord(title, week);
            date_list.add(listRecord);
            Log.w("test", " init dairy存储" + date_list.toString());
        }
        if (cursor.getCount()==0){
            LatelyTime.Uninstall();
        }
        else if (cursor.getCount() > 1) {
            //数据库数据不止一个，/**游标返回至最后一个*/
            if (cursor.moveToLast()){
                dataBaseToLatelyTime(cursor);
            }
        } else if (cursor.getCount() == 1) {
            //数据库只有一条数据，/**游标移至第一个*/
            if (cursor.moveToFirst()){
                dataBaseToLatelyTime(cursor);
            }
        }

        for (int z = 0; z < saveId.size(); z++) {
            //打印saveId
            Log.w("test", " init saveId输出: " + "下标：" + z + " 值：" + saveId.get(z));
        }
        db.setTransactionSuccessful();
        db.endTransaction();
        //关闭数据库
        db.close();
    }

    private void dataBaseToLatelyTime(Cursor cursor) {
        String title = cursor.getString(cursor.getColumnIndex("title"));
        String week = cursor.getString(cursor.getColumnIndex("week"));

        //分割字符串->日期
        String []a = title.split("\\.");

        //年月日
        String date = a[0];
        LatelyTime.getInstance().setLately_date(Integer.parseInt(date));
        String month = a[1];
        LatelyTime.getInstance().setLately_month(SingleCurrentTime.getInstance().transmonth(month));
        String year = a[2];
        LatelyTime.getInstance().setLately_year(Integer.parseInt(year));
        Log.w("test", " init date: " + date + " month: " + month + " year: " + year);

        //星期
        LatelyTime.getInstance().setLately_week(week);

        //关闭游标
        cursor.close();
    }

    //region 控件监听
    private void onClickListen() {
        //region 底部RadioGroup监听事件
        radiogroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.radiobutton_home:
                        //如果当前页面为home界面，则不跳转
                        if (!HomeFragment.getInstance().isVisible()) {
                            FragmentManager fm1 = getSupportFragmentManager();
                            FragmentTransaction ft1 = fm1.beginTransaction();
                            ft1.hide(UserFragment.getInstance()).show(HomeFragment.getInstance()).commit();
                        }
                        break;
                    case R.id.radiobutton_user:
                        //如果当前页面为home界面，则不跳转
                        if (!UserFragment.getInstance().isVisible()) {
                            FragmentManager fm2 = getSupportFragmentManager();
                            FragmentTransaction ft2 = fm2.beginTransaction();
                            ft2.hide(HomeFragment.getInstance()).show(UserFragment.getInstance()).commit();
                        }
                        break;
                }
            }
        });

        //region 左侧滑栏Button监听事件
        button_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //若时间不同则创建一个记录
                if (LatelyTime.getInstance().getLately_week() == null ||
                        LatelyTime.getInstance().getLately_year() != SingleCurrentTime.getInstance().getYear() ||
                        LatelyTime.getInstance().getLately_month()!= SingleCurrentTime.getInstance().getMonth()||
                        LatelyTime.getInstance().getLately_date() != SingleCurrentTime.getInstance().getDate()) {
                    //先刷新当前时间
                    SingleCurrentTime.getInstance().saveToLatelyTime();
                    //向数据库添加一条记录，存
                    String time = SingleCurrentTime.getInstance().getTime();
                    String title = SingleCurrentTime.getInstance().newFormatDate();
                    String week = SingleCurrentTime.getInstance().getWeek();
                    String content = "";
                    SQLiteDatabase db = mDiaryDatabaseHelper.getWritableDatabase("token");
                    //数据打包
                    ContentValues values = new ContentValues();
                    values.put("time", time);
                    values.put("title", title);
                    values.put("week", week);
                    values.put("content", content);
                    db.beginTransaction();
                    db.insert("Diary", null, values);
                    Log.w("test", " button_add button按下，成功插入一条数据");
                    values.clear();
                    Log.w("test", " button_add time: " + time);
                    Log.w("test", " button_add title: " + title);
                    Log.w("test", " button_add week: " + week);
                    Log.w("test", " button_add content: " + content);
                    db.setTransactionSuccessful();
                    db.endTransaction();
                    //Downloads data
                    ListRecord listRecord = new ListRecord(title, week);
                    date_list.add(listRecord);
                    Log.w("test", " button_add 触发Button点击事件，结果为打开时间不同。加入时间点：" + date_list.toString());
                    //刷新适配器
                    mAdapter.notifyDataSetChanged();
                    //刷新saveId，防止第一次创建时下标为0
                    db.beginTransaction();
                    Cursor cursor = db.rawQuery("select id from Diary", null);
                    int x = saveId.size();
                    //扫描数据库
                    if (cursor.moveToLast()) {
                        Log.w("test", " button_add 数据库不为空，开始查询数据");
                        int id = cursor.getInt(0);
                        saveId.add(String.valueOf(id));
                        Log.w("test", " button_add id: " + id);
                        Log.w("test", " button_add id存储 " + "下标：" + x + " 值：" + saveId.get(x));
                        Log.w("test", " init 输出saveId "  + saveId.toString());
                    }
                    cursor.close();
                    db.setTransactionSuccessful();
                    db.endTransaction();
                    db.close();
                    HomeFragment.getInstance().refresh();
                    Log.w("test", " button_add 添加成功，关闭时间： Date：" + LatelyTime.getInstance().newFormatDate());
                    Log.w("test", " button_add 添加成功，当前时间： Date：" + SingleCurrentTime.getInstance().oldFormatDate());
                } else {
                    Log.w("test", " button_add false 添加失败，关闭时间： Date：" + LatelyTime.getInstance().newFormatDate());
                    Log.w("test", " button_add false 添加失败，当前时间： Date：" + SingleCurrentTime.getInstance().oldFormatDate());
                }

            }
        });

        //region 左ListView监听事件
        lv_left.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.w("test", " listview_onclick listview点击事件，position: " + position + " id: " + id);

                // 实例化一个Bundle
                Bundle bundle = new Bundle();
                Intent intent = new Intent(MainActivity.this, RecordActivity.class);
                //把数据保存到Bundle里
                Log.w("test", " listview_onclick saveId.size：" + saveId.size());
                bundle.putString("id", saveId.get(position));
                Log.w("test", " listview_onclick 点击listview第 " + position + " 个item" + "传递值为：" + saveId.get(position));

                //把bundle放入intent里
                intent.putExtra("Database_id", bundle);

                startActivityForResult(intent,LIST_VIEW);
            }
        });

        //region 左ListView长按事件监听
        lv_left.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setIcon(R.mipmap.logo);
                builder.setTitle("珍重,珍重");
                builder.setMessage("是否要删除该条记录？");
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //确定删除
                        try {
                            SQLiteDatabase db = mDiaryDatabaseHelper.getWritableDatabase("token");
                            db.beginTransaction();
                            String sql = "delete from Diary where id=?";
                            db.execSQL(sql, new String[]{saveId.get(position)});
                            db.setTransactionSuccessful();
                            db.endTransaction();
                            db.close();
                            Toast.makeText(mContext, "沙扬娜拉~", Toast.LENGTH_LONG).show();
                            for (int z = 0; z < saveId.size(); z++) {
                                //打印saveId
                                Log.w("test", " longItem saveId输出: " + "下标：" + z + " 值：" + saveId.get(z));
                            }
                            //刷新 先刷新适配器，再刷新界面
                            date_list.clear();
                            int id=Integer.parseInt(saveId.get(position));
                            saveId.clear();
                            Log.w("test"," 清除saveId数据，saveId大小为："+ saveId.size());
                            deleteItem(id);
                            initDataBase();
                            mAdapter.notifyDataSetChanged();
                            HomeFragment.getInstance().refresh();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //取消
                    }
                });
                builder.show();
                return true;
            }
        });

        //侧滑推动效果
        drawlayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View view, float v) {
                //滑动过程中不断回调 v:0~1
                View content = drawlayout.getChildAt(0);
                float scale = 1 - v;//1~0
                content.setTranslationX(view.getMeasuredWidth() * (1 - scale));//0~width
            }

            @Override
            public void onDrawerOpened(@NonNull View view) {}

            @Override
            public void onDrawerClosed(@NonNull View view) {}

            @Override
            public void onDrawerStateChanged(int i) {}
        });
    }

    //region 从数据库中删除传入id数据
    private void deleteItem(int id) {
        //SQLCipher初始化时需要初始化库
        SQLiteDatabase.loadLibs(mContext);

        //打开或创建数据库
        mDiaryDatabaseHelper = new DiaryDatabaseHelper(this, "Diary.db", null, 1);
        SQLiteDatabase db = mDiaryDatabaseHelper.getReadableDatabase("token");
        String where="id=?";
        db.beginTransaction();
        db.delete("Diary",where,new String[]{String.valueOf(id)});
        Log.w("test","成功删除id为："+id+"的记录");
        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();
    }

    //region findViewById()
    private void initId() {
        drawlayout = findViewById(R.id.drawlayout);
        lv_left = findViewById(R.id.lv_left);
        radiogroup = findViewById(R.id.radiogroup);
        button_add = findViewById(R.id.button_add);
        mContext = this;
        mAdapter = new MyAdapter(this, date_list);
        lv_left.setAdapter(mAdapter);
    }

}