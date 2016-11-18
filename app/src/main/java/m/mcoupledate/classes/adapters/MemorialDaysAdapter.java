package m.mcoupledate.classes.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

import m.mcoupledate.R;

/**
 * Created by user on 2016/11/8.
 */

public class MemorialDaysAdapter extends BaseAdapter
{
    protected LayoutInflater mInflater;
    protected ArrayList<MemorialDay> memorialDaysList;


    public MemorialDaysAdapter (Context context, ArrayList<MemorialDay> memorialDaysList)
    {
        this.mInflater = LayoutInflater.from(context);
        this.memorialDaysList = memorialDaysList;
        sortListAsc();
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


        ((TextView) convertView.findViewById(R.id.mDateName)).setText(theDay.name);
        String[] dateSplitArr = theDay.date.split("-");
        ((TextView) convertView.findViewById(R.id.mDate)).setText(dateSplitArr[1] + "/" + dateSplitArr[2]);
        ((TextView) convertView.findViewById(R.id.offsetDays)).setText(theDay.getOffsetDays());


        return convertView;
    }


    public void changeData(ArrayList<MemorialDay> memorialDaysList)
    {
        this.memorialDaysList = memorialDaysList;
        sortListAsc();
        notifyDataSetChanged();
    }


    protected void sortListAsc()
    {
        Collections.sort(memorialDaysList);
    }









    public static class MemorialDay implements Comparable
    {
        public String name, date;
        private String offsetDays;
        private String nowDate = "";

        private SimpleDateFormat formatter;
        private Calendar calendar;

        public MemorialDay(SimpleDateFormat formatter, Calendar calendar)
        {
            this.formatter = formatter;
            this.calendar = calendar;

            this.nowDate = calendar.get(Calendar.YEAR) + "- " + (calendar.get(Calendar.MONTH)+1) + "-" + calendar.get(Calendar.DAY_OF_MONTH);
        }

        public MemorialDay(String name, String date, SimpleDateFormat formatter, Calendar calendar)
        {
            this.name = name;
            this.date = date;

            this.formatter = formatter;
            this.calendar = calendar;

            this.nowDate = calendar.get(Calendar.YEAR) + "- " + (calendar.get(Calendar.MONTH)+1) + "-" + calendar.get(Calendar.DAY_OF_MONTH);
            this.offsetDays = getOffsetDays();
        }

        public String getOffsetDays()
        {
            String calNowDate = formatter.format(new Date());

            if (calNowDate.compareTo(nowDate)==0)
            {
                return offsetDays;
            }
            else
            {
                nowDate = calNowDate;

                String[] dateSplitArr = date.split("-");
                int offsetDaysInt = 0;
                try
                {
                    Date thisYearDate = formatter.parse(calendar.get(Calendar.YEAR) + "-" + dateSplitArr[1] + "-" + dateSplitArr[2]);
                    offsetDaysInt =  (int) ((thisYearDate.getTime() - formatter.parse(calNowDate).getTime()) / (1000 * 60 * 60 * 24));

                    if (offsetDaysInt<0)
                    {
                        thisYearDate = formatter.parse((calendar.get(Calendar.YEAR)+1) + "-" + dateSplitArr[1] + "-" + dateSplitArr[2]);
                        offsetDaysInt =  (int) ((thisYearDate.getTime() - formatter.parse(calNowDate).getTime()) / (1000 * 60 * 60 * 24));
                    }

                    offsetDays = String.valueOf(offsetDaysInt);
                }
                catch (Exception e)
                {   e.printStackTrace();    }

                return offsetDays;
            }

        }

        @Override
        public int compareTo(Object obj)
        {
            //  -1: earlier
            //  1: late
            //  0: equal

            MemorialDay anotherMDay = (MemorialDay) obj;

            return (Integer.valueOf(this.getOffsetDays()) - Integer.valueOf(anotherMDay.getOffsetDays()));

//            if (this.ifPast && !anotherMDay.ifPast)
//            {
//                return -1;
//            }
//            else if (!this.ifPast && anotherMDay.ifPast)
//            {
//                return 1;
//            }
//            else
//            {
//                int minus = Integer.valueOf(this.offsetDays) - Integer.valueOf(anotherMDay.offsetDays);
//
//                if (minus==0)
//                    return 0;
//                else if (this.ifPast && minus>0)
//                    return -1;
//                else if (this.ifPast && minus<0)
//                    return 1;
//                else if (!this.ifPast && minus<0)
//                    return -1;
//                else
//                    return 1;
//            }

        }
    }
}
