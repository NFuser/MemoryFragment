package mf.fssq.mf_part_one.util;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteOpenHelper;

import java.io.ByteArrayOutputStream;

import mf.fssq.mf_part_one.R;

public class UserDatabaseHelper extends SQLiteOpenHelper {

    private Context mContext;
    private static final String CREATE_USER = "create table User("
            + "id integer primary key autoincrement, "
            + "name text, "
            + "bio text, "
            + "icon blob,"
            +"statue integer,"
            + "first integer,"
            +"password text)";

    public UserDatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        mContext=context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_USER);
        ContentValues contentValues=new ContentValues();
        contentValues.put("id",1);
        contentValues.put("name","用户名");
        contentValues.put("bio","四海八荒，始于足下");
        //第一步，将Drawable对象转化为Bitmap对象
        Resources res = mContext.getResources();
        Bitmap bmp = BitmapFactory.decodeResource(res, R.mipmap.men);
        //第二步，声明并创建一个输出字节流对象
        final ByteArrayOutputStream os = new ByteArrayOutputStream();
        //第三步，调用compress将Bitmap对象压缩为PNG格式，第二个参数为PNG图片质量，第三个参数为接收容器，即输出字节流os
        bmp.compress(Bitmap.CompressFormat.PNG, 100, os);
        //第四步，将输出字节流转换为字节数组，并直接进行存储数据库操作，注意，所对应的列的数据类型应该是BLOB类型
        contentValues.put("icon",os.toByteArray());
        contentValues.put("statue",0);
        contentValues.put("first",0);
        contentValues.put("password",0);
       db.insert("User",null,contentValues);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists User");
        onCreate(db);
    }
}
