package mf.fssq.mf_part_one.util;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;

import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteOpenHelper;

import java.io.ByteArrayOutputStream;

import mf.fssq.mf_part_one.R;

public class UserDatabaseHelper extends SQLiteOpenHelper {

    private Context mContext;
    public static final String CREATE_USER = "create table User("
            + "id integer primary key autoincrement, "
            + "name text, "
            + "bio text, "
            + "icon blob,"
            +"statue integer,"
            +"password integer)";

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
        Resources res = mContext.getResources();
        Bitmap bmp = BitmapFactory.decodeResource(res, R.mipmap.men);
        final ByteArrayOutputStream os = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, os);
        contentValues.put("icon",os.toByteArray());
        contentValues.put("statue",0);
        contentValues.put("password",0);
       db.insert("User",null,contentValues);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists User");
        onCreate(db);
    }
}
