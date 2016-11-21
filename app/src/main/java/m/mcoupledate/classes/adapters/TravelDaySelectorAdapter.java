package m.mcoupledate.classes.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;

import java.util.ArrayList;

import m.mcoupledate.R;
import m.mcoupledate.classes.funcs.Actioner;

/**
 * Created by user on 2016/11/20.
 */

public class TravelDaySelectorAdapter extends BaseAdapter
{
    private LayoutInflater mInflater;
    private ArrayList<TravelDaySelector> travelDays;
    private int nowPosition = -1;

    private int[] travelDayBtn = {R.drawable.shape_roundbtn_1, R.drawable.shape_roundbtn_2, R.drawable.shape_roundbtn_3};
    private int[] travelDayBtn_selected = {R.drawable.shape_roundbtn_1_selected, R.drawable.shape_roundbtn_2_selected, R.drawable.shape_roundbtn_3_selected};

    private Actioner travelDaySwitcher = null;


    public TravelDaySelectorAdapter(Context context)
    {
        this.mInflater = LayoutInflater.from(context);
        this.travelDays = new ArrayList<>();
    }


    @Override
    public int getCount()
    {   return travelDays.size();   }

    @Override
    public TravelDaySelector getItem(int position)
    {   return travelDays.get(position);    }

    @Override
    public long getItemId(int position)
    {   return position;   }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        if (convertView==null)
            convertView = mInflater.inflate(R.layout.item_travelday_selector, null);

        TravelDaySelector travelDaySelector = getItem(position);


        final Button travelDaySelectorBtn = (Button) convertView.findViewById(R.id.travelDaySelectorBtn);

        travelDaySelectorBtn.setText(travelDaySelector.dayName);
        travelDaySelectorBtn.setOnClickListener(travelDaySelector.onClickListener);

        int colorIndex = position%3;
        if (travelDaySelector.dayId==nowPosition)
            travelDaySelectorBtn.setBackgroundResource(travelDayBtn_selected[colorIndex]);
        else
            travelDaySelectorBtn.setBackgroundResource(travelDayBtn[colorIndex]);

        return convertView;
    }


    public void setTravelDaySwitcher(Actioner travelDaySwitcher)
    {   this.travelDaySwitcher = travelDaySwitcher; }


    public void setDayNum(int dayNum)
    {   setDayNum(dayNum, -1);    }

    public void setDayNum(int dayNum, int initDay)
    {
        this.travelDays.clear();

        for (int a=1; a<=dayNum; ++a)
        {
            this.travelDays.add(new TravelDaySelector(a));
        }

        if (initDay>0 && initDay<=dayNum)
            nowPosition = initDay;

        notifyDataSetChanged();
    }




    private class TravelDaySelector
    {
        public int dayId;
        public String dayName;
        public View.OnClickListener onClickListener;

        public TravelDaySelector(final int dayId)
        {
            this.dayId = dayId;
            this.dayName = String.valueOf(dayId);

            onClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    if (nowPosition==dayId)
                    {
                        nowPosition = -1;
                    }
                    else
                    {
                        nowPosition = dayId;

                        if (travelDaySwitcher != null)
                            travelDaySwitcher.act(dayId);
                    }

                    notifyDataSetChanged();
                }
            };
        }
    }


}
