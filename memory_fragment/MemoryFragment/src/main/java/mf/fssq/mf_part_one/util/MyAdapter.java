package mf.fssq.mf_part_one.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.res.ResourcesCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;
import mf.fssq.mf_part_one.R;
import mf.fssq.mf_part_one.entity.ListRecord;
import mf.fssq.mf_part_one.entity.SingleCurrentTime;

public class MyAdapter extends BaseAdapter {

    private Context mContext;
    private List<ListRecord> list;
//    private String nString;
//    private ArrayList mString=new ArrayList();

    public MyAdapter(Context context,List<ListRecord> string){
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

    @SuppressLint("InflateParams")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
//        获取视图
        if (convertView==null){
            convertView=LayoutInflater.from(mContext).inflate(R.layout.item_layout,null);
        }

        TextView item_textview=convertView.findViewById(R.id.item_textview);
        String wordDate=list.get(position).getTitle();
        String numberDte=transDate(wordDate);
        item_textview.setText(numberDte);

//        图片绑定
//        Resources res=convertView.getResources();
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
            case "saturday":
                Drawable dra7=ResourcesCompat.getDrawable(convertView.getResources(),R.mipmap.saturday,null);
                item_textview.setCompoundDrawablesWithIntrinsicBounds(dra7,null,null,null);
                break;
        }
        return convertView;
    }

    private String transDate(String wordDate) {
        String transed;
        String[] a=wordDate.split("\\.");
        String date=a[0];
        String month=a[1];
        String year=a[2];
        String newMonth=String.valueOf(SingleCurrentTime.getInstance().transmonth(month)) ;
        transed=year+"."+newMonth+"."+date;
        return transed;
    }

}

