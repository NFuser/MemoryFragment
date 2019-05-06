package mf.fssq.mf_part_one.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import mf.fssq.mf_part_one.R;
import mf.fssq.mf_part_one.entity.Dairy;

public class DetectAdapter extends MyAdapter {

    private Context mContext;
    private List<Dairy> list;

    public DetectAdapter(Context context, List<Dairy> string) {
        super(context, string);
        this.mContext=context;
        this.list=string;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final MyHoder hoder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.check_item, null);
            hoder = new MyHoder();
            hoder.month =  convertView.findViewById(R.id.month);
            hoder.date = convertView.findViewById(R.id.date);
            hoder.year = convertView.findViewById(R.id.year);
            convertView.setTag(hoder);
        } else {
            hoder = (MyHoder) convertView.getTag();
        }
        String str=list.get(position).getTitle();
        String[] title = str.split("\\.");

        String date=title[0];
        String month=title[1];
        String year=title[2];

        hoder.month.setText(month);
        hoder.date.setText(date);
        hoder.year.setText(year);

        return convertView;
    }
    private static class MyHoder {
        TextView month,date,year;
    }
}
