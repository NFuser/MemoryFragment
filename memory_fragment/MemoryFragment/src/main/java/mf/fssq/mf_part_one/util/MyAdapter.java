package mf.fssq.mf_part_one.util;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.v4.content.res.ResourcesCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;
import mf.fssq.mf_part_one.R;
import mf.fssq.mf_part_one.entity.Dairy;

public class MyAdapter extends BaseAdapter {

    private Context mContext;
    private List<Dairy> list;
//    private String nString;
//    private ArrayList mString=new ArrayList();

    public MyAdapter(Context context,List<Dairy> string){
        super();
        this.mContext=context;
        this.list=string;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

//        获取视图
        if (convertView==null){
            convertView=LayoutInflater.from(mContext).inflate(R.layout.item_layout,null);
        }

        TextView item_textview=convertView.findViewById(R.id.item_textview);
        item_textview.setText(list.get(position).getTitle());

//        图片绑定
        Resources res=convertView.getResources();
        switch (list.get(position).getWeek()){//SingleCurrentTime.getInstance().getWeek()
            case "Sunday":
                Drawable dra1=ResourcesCompat.getDrawable(convertView.getResources(),R.mipmap.sunday,null);
                item_textview.setCompoundDrawablesWithIntrinsicBounds(dra1,null,null,null);
                break;
            case "Monday":
                Drawable dra2=ResourcesCompat.getDrawable(convertView.getResources(),R.mipmap.monday,null);
                item_textview.setCompoundDrawablesWithIntrinsicBounds(dra2,null,null,null);
                break;
            case "Tuesday":
                Drawable dra3=ResourcesCompat.getDrawable(convertView.getResources(),R.mipmap.tuesday,null);
                item_textview.setCompoundDrawablesWithIntrinsicBounds(dra3,null,null,null);
                break;
            case "Wednesday":
                Drawable dra4=ResourcesCompat.getDrawable(convertView.getResources(),R.mipmap.wednesday,null);
                item_textview.setCompoundDrawablesWithIntrinsicBounds(dra4,null,null,null);
                break;
            case "Thursday":
                Drawable dra5=ResourcesCompat.getDrawable(convertView.getResources(),R.mipmap.thursday,null);
                item_textview.setCompoundDrawablesWithIntrinsicBounds(dra5,null,null,null);
                break;
            case "Friday":
                Drawable dra6=ResourcesCompat.getDrawable(convertView.getResources(),R.mipmap.friday,null);
                item_textview.setCompoundDrawablesWithIntrinsicBounds(dra6,null,null,null);
                break;
            case "Saturday":
                Drawable dra7=ResourcesCompat.getDrawable(convertView.getResources(),R.mipmap.saturday,null);
                item_textview.setCompoundDrawablesWithIntrinsicBounds(dra7,null,null,null);
                break;
        }
        return convertView;
    }
    public void add(Dairy dairy){
        list.add(dairy);
    }
}

