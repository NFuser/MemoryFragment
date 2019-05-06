package mf.fssq.mf_part_one.fragment;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import mf.fssq.mf_part_one.R;
import mf.fssq.mf_part_one.RecordActivity;
import mf.fssq.mf_part_one.entity.SingleCurrentTime;
import mf.fssq.mf_part_one.util.DiaryDatabaseHelper;
import mf.fssq.mf_part_one.util.ImageUtils;
import mf.fssq.mf_part_one.util.ScreenUtils;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    private TextView home_time;
    private TextView home_week;
    private TextView home_date;
    private TextView home_diary;
    private DiaryDatabaseHelper mDiaryDatabaseHelper;
    private String title;
    private int id;
    private String content;
    private View view;

    private static final int msgKey1 = 1;

    public HomeFragment() {
        // Required empty public constructor
    }

    //    创建唯一实例对象，使用一个私有静态成员变量保存
    private static HomeFragment homeFragment = null;

    //    对外提供一个公开成员变量获取方法
    public static HomeFragment getInstance() {
        if (homeFragment == null) {
            homeFragment = new HomeFragment();
        }
        return homeFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_home, container, false);

        //region 初始化控件
        initId();

        //region 初始化视图
        initView();

        return view;
    }

    public void refresh(){
        initView();
    }

    //region 初始化视图
    private void initView() {
        String string = "Today is " + SingleCurrentTime.getInstance().getWeek();
        home_week.setText(string);
        Log.w("test", string);
        home_date.setText(SingleCurrentTime.getInstance().getMonth() + " " + SingleCurrentTime.getInstance().getDate());

        //SQLCipher初始化时需要初始化库
        SQLiteDatabase.loadLibs(getActivity());
        mDiaryDatabaseHelper = new DiaryDatabaseHelper(getActivity(), "Diary.db", null, 1);
        SQLiteDatabase db = mDiaryDatabaseHelper.getWritableDatabase("token");
        //获取数据库最后一条记录，查询
        String sql="select id,title from Diary";
        Cursor cursor=db.rawQuery(sql,null);
        if (cursor.getCount()!=0){
            //如果数据库不为空，则移动光标至末尾
            cursor.moveToLast();
            int may_id=cursor.getInt(0);
            String may_title=cursor.getString(cursor.getColumnIndex("title"));
            title=may_title;
            id=may_id;
            cursor.close();
            //获取最后一条记录id和日期
            String []a=title.split("\\.");

            int date=Integer.parseInt(a[0]);
            Log.w("test","date: "+date);
            String month=a[1];
            Log.w("test","month: "+month);
            int year=Integer.parseInt(a[2]);
            Log.w("test","year: "+year);
            //与当前日期比较，即判断今日是否有创建记录
            if (date==SingleCurrentTime.getInstance().getDate()&&
                    month.equals(SingleCurrentTime.getInstance().getMonth())&&
                    year==SingleCurrentTime.getInstance().getYear()){
                //如今日已创建记录，则匹配id读取content内容
                String sql_again="select content from Diary where id="+id;
                Log.w("test"," sql_again: "+sql_again);
                Cursor cursor_again=db.rawQuery(sql_again,null);

                if (cursor_again.moveToFirst()){
                    Log.w("test",String.valueOf(cursor_again.moveToFirst()));
                    String may_content=cursor_again.getString(cursor_again.getColumnIndex("content"));
                    Log.w("test"," may_content: "+may_content);
                    if (may_content.equals("")){
                        //虽已添加记录，但没添加内容，显示默认内容
                        home_diary.setText("今天你什么都没有留下...");
                    }else {
                        content=(may_content);
                        initContent();
                        Log.w("test"," home_diary: "+home_diary.getText());
                        Log.w("test"," content: "+content);
                    }
                }
                cursor_again.close();
            }
            //今日无创建记录，显示默认内容
//            home_diary.setText("今天你什么都没有留下...");
        }else {//数据库为空，显示默认内容（第一次打开应用）
            home_diary.setText("今天你什么都没有留下...");
        }
        new TimeThread().start();
    }

    //region 初始化控件
    private void initId() {
        home_time = view.findViewById(R.id.home_time);
        home_week = view.findViewById(R.id.home_week);
        home_date = view.findViewById(R.id.home_date);
        home_diary = view.findViewById(R.id.home_diary);
    }

    //region 初始化文本内容
    private void initContent(){
        //input是获取将被解析的字符串
        String input = content;
        //将图片那一串字符串解析出来,即<img src=="xxx" />
        Pattern p = Pattern.compile("\\<img src=\".*?\"\\/>");
        Matcher m = p.matcher(input);

        SpannableString spannable = new SpannableString(input);
        //如果输入中存在另一个匹配项，则从给定位置开始返回true
        while(m.find()){
            //这里s保存的是整个式子，即<img src="xxx"/>，start和end保存的是下标
            String s = m.group();
            int start = m.start();
            int end = m.end();
            //path是去掉<img src=""/>的中间的图片路径
            String path = s.replaceAll("\\<img src=\"|\"\\/>","").trim();

            //利用spannableString和ImageSpan来替换掉这些图片
            int width = ScreenUtils.getScreenWidth(getActivity());

            Bitmap bitmap = BitmapFactory.decodeFile(path, null);
            bitmap = ImageUtils.zoomImage(bitmap, (width - 36) * 0.93, (bitmap.getHeight() * ((width - 36) * 0.93)) / bitmap.getWidth());
            ImageSpan imageSpan = new ImageSpan(getActivity(), bitmap);
            spannable.setSpan(imageSpan,start,end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        }
        home_diary.setText(spannable);
        home_diary.append("\n");
    }

    public class TimeThread extends Thread {
        @Override
        public void run() {
            super.run();
            do {
                try {
                    Thread.sleep(1000);
                    Message msg = new Message();
                    msg.what = msgKey1;
                    mHandler.sendMessage(msg);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } while (true);
        }
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case msgKey1:
                    long time = System.currentTimeMillis();
                    Date date = new Date(time);
                    SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
                    home_time.setText(format.format(date));
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        homeFragment = null;
    }
}
