package mf.fssq.mf_part_one;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;
//import android.database.Cursor;
//import android.database.sqlite.SQLiteDatabase;
//import android.database.sqlite.SQLiteOpenHelper;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import mf.fssq.mf_part_one.util.AndroidBug5497Workaround;
import mf.fssq.mf_part_one.util.DiaryDatabaseHelper;
import mf.fssq.mf_part_one.util.ImageUtils;
import mf.fssq.mf_part_one.util.ScreenUtils;

public class RecordActivity extends AppCompatActivity {

    private TextView tv_week, tv_date;
    private ScrollView sl_edit;
    private DiaryDatabaseHelper mDiaryDatabaseHelper;
    private SQLiteDatabase db;
    private Map<String, String> map = new HashMap<String, String>();
    private EditText et_content;
    private ImageView iv_picture;
    private ImageView iv_save;
    private int id;
    private String fragment;
    private Context context;


    //请求外部存储，请求码
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    //外部读写权限
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};
    //插入图片的Activity的返回的code
    static final int IMAGE_CODE = 99;
    public final int HOME = 3;
    public final int USER = 4;


    //开启沉浸模式
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
        setContentView(R.layout.activity_record);

        //打开或关闭软键盘，使底部标签栏复位
//        RecordActivity.this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
//        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        //获取数据
        Intent intent = getIntent();
        //从intent取出bundle
        Bundle bundle = intent.getBundleExtra("Database_id");
        //获取id
        id = Integer.parseInt(bundle.getString("id"));
        Log.w("test", "查询id " + id);
        //获取页面跳转前显示fragment
        fragment=bundle.getString("fragment");
        Log.w("test", "页面跳转前fragment： " + fragment);

        //初始化控件
        findId();

        /***
         * 解决全屏模式下android:windowSoftInputMode="adjustPan"失效问题
         * 参考网址：https://www.diycode.cc/topics/383
         */
        AndroidBug5497Workaround.assistActivity(this);

        //返回界面数据
        returnData();

        //初始化控件点击事件
        initWidge();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //参考网址：http://blog.csdn.net/abc__d/article/details/51790806
//        Bitmap bm = null;
        // 外界的程序访问ContentProvider所提供数据 可以通过ContentResolver接口
        ContentResolver resolver = getContentResolver();
        if (requestCode == IMAGE_CODE) {
            try {
                // 获得图片的uri
                Uri originalUri = data.getData();
//                bm = MediaStore.Images.Media.getBitmap(resolver, originalUri);
                String[] proj = {MediaStore.Images.Media.DATA};
                // 好像是android多媒体数据库的封装接口，具体的看Android文档
                android.database.Cursor cursor = getContentResolver().query(originalUri, proj, null, null, null);
                // 按我个人理解 这个是获得用户选择的图片的索引值
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                // 将光标移至开头 ，这个很重要，不小心很容易引起越界
                cursor.moveToFirst();
                // 最后根据索引值获取图片路径
                String path = cursor.getString(column_index);
                Log.w("picture"," path:"+path);
                //插入图片
                insertImg(path);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(RecordActivity.this, "图片插入失败", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //region 插入图片
    private void insertImg(String path) {
        //为图片路径加上<img>标签
        String tagPath = "<img src=\"" + path + "\"/>";
        Log.w("picture"," tagPath:"+tagPath);
        //图片解码->bitmap
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        if (bitmap != null) {
            //如果不为空，加载图片
            SpannableString ss = getBitmapMime(path, tagPath);
            //调用方法，将图片插入到edit text中
            insertPhotoToEditText(ss);
            //输出一个换行符
            et_content.append("\n");
        } else {
            Toast.makeText(RecordActivity.this, "插入失败，无读写存储权限，请到权限中心开启", Toast.LENGTH_LONG).show();
        }
    }

    //region 将图片插入到EditText中
    private void insertPhotoToEditText(SpannableString ss) {
        //先获取edit text内容
        Editable et = et_content.getText();
        //设置起始坐标
        int start = et_content.getSelectionStart();
        //设置插入位置
        et.insert(start, ss);
        //把et添加到edit text中
        et_content.setText(et);
        //设置光标在最后显示
        et_content.setSelection(start + ss.length());
        //Touch获取焦点
        et_content.setFocusableInTouchMode(true);
        //获取焦点
        et_content.setFocusable(true);
    }

    //region 根据图片路径利用SpannableString和ImageSpan来加载图片
    private SpannableString getBitmapMime(String path, String tagPath) {
        //这里使用加了<img>标签的图片路径
        SpannableString ss = new SpannableString(tagPath);
        //获取屏幕宽度
        int width = ScreenUtils.getScreenWidth(RecordActivity.this);
        //获取屏幕高度
        int height = ScreenUtils.getScreenHeight(RecordActivity.this);

        BitmapFactory.Options options = new BitmapFactory.Options();
        //对图片进行解码
        //↓等同于：Bitmap bitmap = BitmapFactory.decodeFile(path);
        Bitmap bitmap = BitmapFactory.decodeFile(path, options);
        Log.w("picture", "缩放前图片宽：" + bitmap.getWidth() + "高：" + bitmap.getHeight());
        //缩放比例，设置插入图片占满屏宽，高自适应调整
        bitmap = ImageUtils.zoomImage(bitmap, (width - 36) * 0.93, (bitmap.getHeight() * ((width - 36) * 0.93)) / bitmap.getWidth());
        Log.w("picture", "缩放后图片宽：" + bitmap.getWidth() + "高：" + bitmap.getHeight());

        /**
         * setSpan(what, start, end, flags)
         * - what传入各种Span类型的实例;
         * - start和end标记要替代的文字内容的范围；
         * - flags是用来标识在Span范围内的文本前后输入新的字符时是否把它们也应用这个效果
         * */
        ImageSpan imageSpan = new ImageSpan(this, bitmap);
        ss.setSpan(imageSpan, 0, tagPath.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return ss;
    }

    //region 初始化控件点击事件
    private void initWidge() {
        //图片点击事件
        iv_picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //调用图库
                callGallery();
            }
        });

        //保存点击事件
        iv_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sql="update Diary set content=? where id=?";
                db.beginTransaction();
                db.execSQL(sql,new Object[]{et_content.getText().toString(),id});
                db.setTransactionSuccessful();
                db.endTransaction();

                Intent intent=new Intent();
                setResult( HOME,intent);
                finish();
            }
        });
    }

    //region 监听返回事件
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            saveData();
            Intent intent=new Intent();
            setResult( HOME,intent);
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    //region 保存数据
    private void saveData() {
        String sql="update Diary set content=? where id=?";
        db.beginTransaction();
        db.execSQL(sql,new Object[]{et_content.getText().toString(),id});
        db.setTransactionSuccessful();
        db.endTransaction();
//        Intent intent = new Intent(context, MainActivity.class);
//        startActivity(intent);
    }

    //region 调用图库
    private void callGallery() {
        //检查读写权限->Intent打开相册
        int permission_WRITE = ActivityCompat.checkSelfPermission(RecordActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int permission_READ = ActivityCompat.checkSelfPermission(RecordActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (permission_WRITE != PackageManager.PERMISSION_GRANTED || permission_READ != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(RecordActivity.this, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
        }
        //读取相册图片
        Intent getAlbum = new Intent(Intent.ACTION_PICK);
        getAlbum.setType("image/*");
        startActivityForResult(getAlbum, IMAGE_CODE);
    }

    //region 返回界面数据
    private void returnData() {
        mDiaryDatabaseHelper = new DiaryDatabaseHelper(this, "Diary.db", null, 1);
        db = mDiaryDatabaseHelper.getWritableDatabase("token");
        //查询
        String sql = "select title,week,content from Diary where id =?" ;
        Cursor cursor = db.rawQuery(sql, new String[]{String.valueOf(id)});
        if (cursor.moveToFirst()) {
            String date = cursor.getString(cursor.getColumnIndex("title"));
            String week = cursor.getString(cursor.getColumnIndex("week"));
            String content = cursor.getString(cursor.getColumnIndex("content"));
            map.put("title", date);
            map.put("week", week);
            map.put("content", content);
        }
        tv_date.setText(map.get("title"));
        tv_week.setText(map.get("week"));
        initContent();
    }

    private void initContent(){
        //input是获取将被解析的字符串
        String input = map.get("content");
        //将图片那一串字符串解析出来,即<img src=="xxx" />
        Pattern p = Pattern.compile("\\<img src=\".*?\"\\/>");
        Matcher m = p.matcher(input);

        //使用SpannableString了，这个不会可以看这里哦：http://blog.sina.com.cn/s/blog_766aa3810100u8tx.html#cmt_523FF91E-7F000001-B8CB053C-7FA-8A0
        SpannableString spannable = new SpannableString(input);
        while(m.find()){
            //这里s保存的是整个式子，即<img src="xxx"/>，start和end保存的是下标
            String s = m.group();
            int start = m.start();
            int end = m.end();
            //path是去掉<img src=""/>的中间的图片路径
            String path = s.replaceAll("\\<img src=\"|\"\\/>","").trim();

            //利用spannableString和ImageSpan来替换掉这些图片
            int width = ScreenUtils.getScreenWidth(RecordActivity.this);
            int height = ScreenUtils.getScreenHeight(RecordActivity.this);

            Bitmap bitmap = BitmapFactory.decodeFile(path, null);
            bitmap = ImageUtils.zoomImage(bitmap, (width - 36) * 0.93, (bitmap.getHeight() * ((width - 36) * 0.93)) / bitmap.getWidth());
//            Bitmap bitmap = ImageUtils.getSmallBitmap(path,width,480);
            ImageSpan imageSpan = new ImageSpan(this, bitmap);
            spannable.setSpan(imageSpan,start,end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        }
        et_content.setText(spannable);
        et_content.append("\n");
        //Log.d("YYPT_RGX_SUCCESS",content.getText().toString());
    }

    //region 初始化控件
    private void findId() {
        tv_week = findViewById(R.id.tv_week);
        tv_date = findViewById(R.id.tv_date);
        et_content = findViewById(R.id.et_content);
        iv_picture = findViewById(R.id.iv_picture);
        sl_edit = findViewById(R.id.sl_edit);
        iv_save = findViewById(R.id.iv_save);
        context = this;
    }

}
