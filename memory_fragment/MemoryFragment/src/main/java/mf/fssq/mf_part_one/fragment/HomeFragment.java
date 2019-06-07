package mf.fssq.mf_part_one.fragment;


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import mf.fssq.mf_part_one.R;
import mf.fssq.mf_part_one.entity.SingleCurrentTime;
import mf.fssq.mf_part_one.util.DiaryDatabaseHelper;
import mf.fssq.mf_part_one.util.ImageUtils;
import mf.fssq.mf_part_one.util.ScreenUtils;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    private TextView home_week;
    private TextView home_date;
    private TextView home_diary;
    private String content;
    private View view;

    //双击事件属性字段
    private int count = 0;
    private int firClick = 0;
    private int secClick = 0;
    private int flag = 0;
    private boolean on_off=false;
    private String secondRecord;
    private String fakeDiary="云朗风清，诸事相宜。";

    public HomeFragment() {
        // Required empty public constructor
    }

    //    创建唯一实例对象，使用一个私有静态成员变量保存
    @SuppressLint("StaticFieldLeak")
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

        //region 控件监听
        initListen();

        //region 初始化视图
        initView();

        return view;
    }

    //region 控件监听
    @SuppressLint("ClickableViewAccessibility")
    private void initListen() {
        home_week.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (MotionEvent.ACTION_DOWN == motionEvent.getAction()) {
                    if (on_off){
                        count++;
                        if (count == 1) {
                            firClick = (int) System.currentTimeMillis();
                        } else if (count == 2) {
                            secClick = (int) System.currentTimeMillis();
                            switch (flag) {
                                case 0:
                                    if (secClick - firClick < 1000) {// 双击事件
                                        //隐藏当前记录内容，使用伪记录显示
                                        if (content!=null){
                                            home_diary.setText(fakeDiary);
                                        }else {
                                            secondRecord= home_diary.getText().toString();
                                            home_diary.setText(fakeDiary);
                                        }
                                        flag++;
                                    }
                                    count = 0;
                                    firClick = 0;
                                    secClick = 0;
                                    return true;
                                case 1:
                                    if (secClick - firClick < 1000) {// 双击事件
                                        //还原原记录内容显示
                                        if (content!=null){
                                            initContent();
                                        }else {
                                            home_diary.setText(secondRecord);
                                        }
                                        flag--;
                                    }
                                    count = 0;
                                    firClick = 0;
                                    secClick = 0;
                                    return true;
                                default:
                                    return true;
                            }
                        }
                    }
                }
                return true;
            }
        });
    }

    public void refresh(){
        flag=0;
        count = 0;
        firClick = 0;
        secClick = 0;
        on_off=false;
        secondRecord="";
        initView();
    }

    //region 初始化视图
    private void initView() {
        String week = "Today is " + SingleCurrentTime.getInstance().getWeek();
        home_week.setText(week);
        Log.w("test", week);
        String day_date=SingleCurrentTime.getInstance().BackMonth(SingleCurrentTime.getInstance().getMonth()) + " " + SingleCurrentTime.getInstance().getDate();
        home_date.setText(day_date);

        //SQLCipher初始化时需要初始化库
        SQLiteDatabase.loadLibs(Objects.requireNonNull(getActivity()));
        DiaryDatabaseHelper mDiaryDatabaseHelper = new DiaryDatabaseHelper(getActivity(), "Diary.db", null, 1);
        SQLiteDatabase db = mDiaryDatabaseHelper.getWritableDatabase("token");
        //获取数据库最后一条记录，查询
        String sql="select id,title from Diary";
        db.beginTransaction();
        Cursor cursor=db.rawQuery(sql,null);
        if (cursor.getCount()!=0){
            //如果数据库不为空，则移动光标至末尾
            cursor.moveToLast();
            int may_id=cursor.getInt(0);
            String may_title=cursor.getString(cursor.getColumnIndex("title"));

            //获取最后一条记录id和日期
            String []a= may_title.split("\\.");

            int date=Integer.parseInt(a[0]);
            Log.w("test","date: "+date);
            int month=SingleCurrentTime.getInstance().transmonth(a[1]) ;
            Log.w("test","month: "+month);
            int year=Integer.parseInt(a[2]);
            Log.w("test","year: "+year);
            //与当前日期比较，即判断今日是否有创建记录
            if (date==SingleCurrentTime.getInstance().getDate()&&
                    month==SingleCurrentTime.getInstance().getMonth()&&
                    year==SingleCurrentTime.getInstance().getYear()){
                on_off=true;
                //如今日已创建记录，则匹配id读取content内容
                String sql_again="select content from Diary where id="+ may_id;
                Log.w("test"," sql_again: "+sql_again);
                db.beginTransaction();
                Cursor cursor_again=db.rawQuery(sql_again,null);
                if (cursor_again.moveToFirst()){
                    Log.w("test"," home 游标移动至第一个："+ cursor_again.moveToFirst());
                    String may_content=cursor_again.getString(cursor_again.getColumnIndex("content"));
                    Log.w("test"," may_content: "+may_content);
                    if (may_content.equals("")){
                        //虽已添加记录，但没添加内容，显示默认内容
                        home_diary.setText("今天你什么都没有留下...");
                    }else {
                        content=may_content;
                        initContent();
                        Log.w("test"," home_diary: "+home_diary.getText());
                        Log.w("test"," content: "+content);
                    }
                }
                cursor_again.close();
            }else{
                //今日无创建记录，显示默认内容
            home_diary.setText("今天你什么都没有留下...");
            }
        }else {//数据库为空，显示默认内容（第一次打开应用）
            home_diary.setText("今天你什么都没有留下...");
        }
        cursor.close();
        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();
    }

    //region 初始化控件
    private void initId() {
        home_week = view.findViewById(R.id.home_week);
        home_date = view.findViewById(R.id.home_date);
        home_diary = view.findViewById(R.id.home_diary);
    }

    //region 初始化文本内容
    private void initContent(){
        //input是获取将被解析的字符串
        String input = content;
        //将图片那一串字符串解析出来,即<img src=="xxx" />
        Pattern p = Pattern.compile("<img src=\".*?\"/>");
        Matcher m = p.matcher(input);

        SpannableString spannable = new SpannableString(input);
        //如果输入中存在另一个匹配项，则从给定位置开始返回true
        while(m.find()){
            //这里s保存的是整个式子，即<img src="xxx"/>，start和end保存的是下标
            String s = m.group();
            int start = m.start();
            int end = m.end();
            //path是去掉<img src=""/>的中间的图片路径
            String path = s.replaceAll("<img src=\"|\"/>","").trim();

            //利用spannableString和ImageSpan来替换掉这些图片
            int width = ScreenUtils.getScreenWidth(Objects.requireNonNull(getActivity()));

            Bitmap bitmap = BitmapFactory.decodeFile(path, null);
            bitmap = ImageUtils.zoomImage(bitmap, (width - 36) * 0.93, (bitmap.getHeight() * ((width - 36) * 0.93)) / bitmap.getWidth());
            ImageSpan imageSpan = new ImageSpan(getActivity(), bitmap);
            spannable.setSpan(imageSpan,start,end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        }
        home_diary.setText(spannable);
        home_diary.append("\n");
    }

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
