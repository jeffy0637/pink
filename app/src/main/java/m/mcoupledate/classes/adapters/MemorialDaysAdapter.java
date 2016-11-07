package m.mcoupledate.classes.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.json.JSONArray;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import m.mcoupledate.R;

/**
 * Created by user on 2016/11/8.
 */

public class MemorialDaysAdapter extends BaseAdapter
{
    private Context context;
    private LayoutInflater mInflater;
    private ArrayList<MemorialDay> memorialDaysList;
    private String nowDate;
    private SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");



    public MemorialDaysAdapter (Context context, ArrayList<MemorialDay> memorialDaysList, String nowDate)
    {
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
        this.memorialDaysList = memorialDaysList;
        this.nowDate = nowDate;
    }

    @Override
    public int getCount()
    {
        return memorialDaysList.size();
    }

    @Override
    public MemorialDay getItem(int arg0)
    {
        return memorialDaysList.get(arg0);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        if (convertView==null)
            convertView = mInflater.inflate(R.layout.item_memorialday, null);

        MemorialDay theDay = getItem(position);


        ((TextView) convertView.findViewById(R.id.mContext)).setText(theDay.name);
        ((TextView) convertView.findViewById(R.id.mTime)).setText(theDay.date);
        ((TextView) convertView.findViewById(R.id.diffTime)).setText(calDiffDays(theDay.date));



        return convertView;
    }


    public void changeData(JSONArray sites)
    {
    }

    private String calDiffDays(String date)
    {
        Date d1 = null;
        try
        {   d1 = formatter.parse(date);  }
        catch (ParseException e)
        {   e.printStackTrace();    }

        Date d2 = new Date();

        return  String.valueOf((d2.getTime() - d1.getTime()) / (1000*60*60*24));
    }


    public static class MemorialDay
    {
        public String name, date;

        public MemorialDay(String name, String date)
        {
            this.name = name;
            this.date = date;
        }
    }
}
