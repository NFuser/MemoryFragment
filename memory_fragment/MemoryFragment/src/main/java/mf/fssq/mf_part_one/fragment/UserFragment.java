package mf.fssq.mf_part_one.fragment;


import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;

import java.io.ByteArrayOutputStream;

import mf.fssq.mf_part_one.AboutActivity;
import mf.fssq.mf_part_one.AchievementActivity;
import mf.fssq.mf_part_one.AliPayGestureLockActivity;
import mf.fssq.mf_part_one.BioActivity;
import mf.fssq.mf_part_one.LockedActivity;
import mf.fssq.mf_part_one.NameActivity;
import mf.fssq.mf_part_one.R;
import mf.fssq.mf_part_one.util.DensityUtil;
import mf.fssq.mf_part_one.util.UserDatabaseHelper;


/**
 * A simple {@link Fragment} subclass.
 */
public class UserFragment extends Fragment {

    private Context mContext;
    private View mView;
    private ImageView user_image;
    private TextView user_name,user_signature;
    private Button about,achievement,locked;
    private TextView edit,cancel,men,women;
    private UserDatabaseHelper mUserDatabaseHelper;
    private final int LOCKED=5;

    public final int Bio_Edit = 1;
    public final int Name_Edit = 2;



    public UserFragment() {
        // Required empty public constructor
    }

    //    创建唯一实例对象，使用一个私有静态成员变量保存
    private static UserFragment userFragment = null;

    //    对外提供一个公开成员变量获取方法
    public static UserFragment getInstance() {
        if (userFragment == null) {
            userFragment = new UserFragment();
        }
        return userFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user, container, false);
        mView=view;

        initId();

        initData();

        initOnclickListener();

        return view;
    }

    public void refresh(){
        initData();
    }

    private void initData() {
        //SQLCipher初始化时需要初始化库
        SQLiteDatabase.loadLibs(mContext);

        //打开或创建数据库
        mUserDatabaseHelper = new UserDatabaseHelper(mContext, "User.db", null, 1);
        SQLiteDatabase db = mUserDatabaseHelper.getReadableDatabase("system");
        String sql="select name,bio,icon from User where id=1";
        Cursor cursor=db.rawQuery(sql,null);
        cursor.moveToFirst();
        String name=cursor.getString(cursor.getColumnIndex("name"));
        String bio=cursor.getString(cursor.getColumnIndex("bio"));
        //第一步，从数据库中读取出相应数据，并保存在字节数组中
        byte[] blob=cursor.getBlob(cursor.getColumnIndex("icon"));
        //第二步，调用BitmapFactory的解码方法decodeByteArray把字节数组转换为Bitmap对象
        Bitmap bmp =BitmapFactory.decodeByteArray(blob,0,blob.length);
        //第三步，调用BitmapDrawable构造函数生成一个BitmapDrawable对象，该对象继承Drawable对象，所以在需要处直接使用该对象即可
        BitmapDrawable icon = new BitmapDrawable(mContext.getResources(),bmp);

        user_name.setText(name);
        user_signature.setText(bio);
//        user_image.setImageBitmap(bmp);
        user_image.setImageDrawable(icon);
    }

    private void initOnclickListener() {
        about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(),AboutActivity.class);
                startActivity(intent);
            }
        });
        achievement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(),AchievementActivity.class);
                startActivity(intent);
            }
        });
        user_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editImage();
            }
        });
        user_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editName();
            }
        });
        user_signature.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editBio();
            }
        });
        locked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(),LockedActivity.class);
                startActivityForResult(intent,LOCKED);
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case Bio_Edit:
                if (resultCode==66){
                    String str=data.getStringExtra("bio");
                    user_signature.setText(str);
                    initData();
                }
                break;
            case Name_Edit:
                if (resultCode==66){
                    String str=data.getStringExtra("name");
                    user_name.setText(str);
                    initData();
                }
                break;
            case LOCKED:
                if (resultCode==666){
                    Toast.makeText(getActivity(),"开启手势锁成功",Toast.LENGTH_LONG).show();
                }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void editImage() {
        final Dialog bottomDialog = new Dialog(mContext, R.style.BottomDialog);
        View contentView = LayoutInflater.from(mContext).inflate(R.layout.dialog_image, null);
        bottomDialog.setContentView(contentView);
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) contentView.getLayoutParams();
        params.width = getResources().getDisplayMetrics().widthPixels - DensityUtil.dp2px(mContext, 16f);
        params.bottomMargin = DensityUtil.dp2px(mContext, 8f);
        contentView.setLayoutParams(params);
        bottomDialog.getWindow().setGravity(Gravity.BOTTOM);
        bottomDialog.getWindow().setWindowAnimations(R.style.BottomDialog_Animation);
        bottomDialog.show();

        men=contentView.findViewById(R.id.men);
        women=contentView.findViewById(R.id.women);
        cancel=contentView.findViewById(R.id.cancel);

        men.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //第一步，将Drawable对象转化为Bitmap对象
                Bitmap bitmap=BitmapFactory.decodeResource(mContext.getResources(),R.mipmap.men);
                //第二步，声明并创建一个输出字节流对象
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                //第三步，调用compress将Bitmap对象压缩为PNG格式，第二个参数为PNG图片质量，第三个参数为接收容器，即输出字节流os
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
                //第四步，将输出字节流转换为字节数组，并直接进行存储数据库操作，注意，所对应的列的数据类型应该是BLOB类型

                //SQLCipher初始化时需要初始化库
                SQLiteDatabase.loadLibs(mContext);

                //打开或创建数据库
                mUserDatabaseHelper = new UserDatabaseHelper(mContext, "User.db", null, 1);
                SQLiteDatabase db = mUserDatabaseHelper.getReadableDatabase("system");
                String sql="update User set icon=? where id=?";
                db.beginTransaction();
                db.execSQL(sql,new Object[]{os.toByteArray(),1});
                db.setTransactionSuccessful();
                db.endTransaction();

                user_image.setImageResource(R.mipmap.men);

                UserFragment.getInstance().refresh();

                bottomDialog.dismiss();
            }
        });

        women.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //第一步，将Drawable对象转化为Bitmap对象
                Bitmap bitmap=BitmapFactory.decodeResource(mContext.getResources(),R.mipmap.women);
                //第二步，声明并创建一个输出字节流对象
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                //第三步，调用compress将Bitmap对象压缩为PNG格式，第二个参数为PNG图片质量，第三个参数为接收容器，即输出字节流os
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
                //第四步，将输出字节流转换为字节数组，并直接进行存储数据库操作，注意，所对应的列的数据类型应该是BLOB类型

                //SQLCipher初始化时需要初始化库
                SQLiteDatabase.loadLibs(mContext);

                //打开或创建数据库
                mUserDatabaseHelper = new UserDatabaseHelper(mContext, "User.db", null, 1);
                SQLiteDatabase db = mUserDatabaseHelper.getReadableDatabase("system");
                String sql="update User set icon=? where id=?";
                db.beginTransaction();
                db.execSQL(sql,new Object[]{os.toByteArray(),1});
                db.setTransactionSuccessful();
                db.endTransaction();

                user_image.setImageResource(R.mipmap.women);

                UserFragment.getInstance().refresh();

                bottomDialog.dismiss();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomDialog.dismiss();
            }
        });
    }

    private void editName() {
        final Dialog bottomDialog = new Dialog(mContext, R.style.BottomDialog);
        View contentView = LayoutInflater.from(mContext).inflate(R.layout.dialog_name, null);
        bottomDialog.setContentView(contentView);
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) contentView.getLayoutParams();
        params.width = getResources().getDisplayMetrics().widthPixels - DensityUtil.dp2px(mContext, 16f);
        params.bottomMargin = DensityUtil.dp2px(mContext, 8f);
        contentView.setLayoutParams(params);
        bottomDialog.getWindow().setGravity(Gravity.BOTTOM);
        bottomDialog.getWindow().setWindowAnimations(R.style.BottomDialog_Animation);
        bottomDialog.show();

        edit=contentView.findViewById(R.id.edit);
        cancel=contentView.findViewById(R.id.cancel);

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(mContext,NameActivity.class);
                startActivityForResult(intent,Name_Edit);
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomDialog.dismiss();
            }
        });

    }

    private void editBio() {
        final Dialog bottomDialog = new Dialog(mContext, R.style.BottomDialog);
        View contentView = LayoutInflater.from(mContext).inflate(R.layout.dialog_bio, null);
        bottomDialog.setContentView(contentView);
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) contentView.getLayoutParams();
        params.width = getResources().getDisplayMetrics().widthPixels - DensityUtil.dp2px(mContext, 16f);
        params.bottomMargin = DensityUtil.dp2px(mContext, 8f);
        contentView.setLayoutParams(params);
        bottomDialog.getWindow().setGravity(Gravity.BOTTOM);
        bottomDialog.getWindow().setWindowAnimations(R.style.BottomDialog_Animation);
        bottomDialog.show();

        edit=contentView.findViewById(R.id.edit);
        cancel=contentView.findViewById(R.id.cancel);

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(mContext,BioActivity.class);
                startActivityForResult(intent,Bio_Edit);
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomDialog.dismiss();
            }
        });

    }

    private void initId() {
        user_name = mView.findViewById(R.id.user_name);
        user_signature=mView.findViewById(R.id.user_signature);
        about=mView.findViewById(R.id.about);
        achievement=mView.findViewById(R.id.achievement);
        locked=mView.findViewById(R.id.locked);
        user_image=mView.findViewById(R.id.user_image);
    }

    @Override
    public void onAttach(Context context) {
        mContext=context;
        super.onAttach(context);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        userFragment = null;
    }
}
