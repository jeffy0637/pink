package m.mcoupledate.classes.DropDownMenu;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

import m.mcoupledate.R;


public class ConstellationAdapter extends BaseAdapter {

    private Context context;
    public List<String> list;
    private List<Integer> checkedList;


    private int selectType;
    public static int RADIO = 1, CHECK = 2, CHECK_CAN_SELECTALL = 3;


    public ConstellationAdapter(Context context, List<String> list, int selectType) {
        this.context = context;
        this.list = list;
        this.checkedList = new ArrayList<>();

        this.selectType = selectType;
    }


    public void setCheckItem(int position, Boolean ifNeedNotify) {

        if (this.selectType==this.CHECK || this.selectType==this.CHECK_CAN_SELECTALL)
        {
            try
            {
                this.checkedList.remove(this.checkedList.indexOf(position));
            }
            catch (Exception e)
            {
                if (this.selectType==this.CHECK_CAN_SELECTALL)
                {
                    if (position==0)
                    {
                        this.checkedList.clear();
                    }
                    else
                    {
                        try {   this.checkedList.remove(this.checkedList.indexOf(0));    }
                        catch (Exception e2) {}
                    }
                }

                this.checkedList.add(position);
            }

        }
        else // if (this.selectType==this.RADIO)
        {
            this.checkedList.clear();
            this.checkedList.add(position);
        }


        if (ifNeedNotify)
            notifyDataSetChanged();
    }

    public void setCheckItem(String item, Boolean ifNeedNotify)
    {
        int position = this.list.indexOf(item);

//        Log.d("HFsetList", position + "  " + item);

        if (position!=-1)
            setCheckItem(position, ifNeedNotify);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public String getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView != null) {
            viewHolder = (ViewHolder) convertView.getTag();
        } else {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_constellation_layout, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }

        fillValue(position, viewHolder);

        return convertView;
    }

    private void fillValue(int position, ViewHolder viewHolder) {
        viewHolder.mText.setText(list.get(position));

        Boolean ifMatch = false;
        for (int checked : this.checkedList)
        {
            if (checked==position)
            {
                ifMatch = true;
                break;
            }
        }

        if (ifMatch) {
            viewHolder.mText.setTextColor(context.getResources().getColor(R.color.drop_down_selected));
            viewHolder.mText.setBackgroundResource(R.drawable.check_bg);
        } else {
            viewHolder.mText.setTextColor(context.getResources().getColor(R.color.drop_down_unselected));
            viewHolder.mText.setBackgroundResource(R.drawable.uncheck_bg);
        }

    }

    static class ViewHolder {
        TextView mText;

        ViewHolder(View view) {
            mText= (TextView) view.findViewById(R.id.text);
        }
    }

    public void changeData(List<String> list)
    {
        this.list = list;
        this.checkedList.clear();
        this.notifyDataSetChanged();
    }


    public JSONArray getCheckedListJSONArray()
    {
        JSONArray jArr = new JSONArray();

        for (int i : this.checkedList)
        {
            jArr.put(this.list.get(i));
        }

        return jArr;
    }

    public String getCheckedListJSONString()
    {
        return getCheckedListJSONArray().toString();
    }


    public void setCheckedList(JSONArray setList)
    {
        try
        {
            if (this.selectType == RADIO)
            {
                throw new Exception("Should only be used for CHECK or CHECK_CAN_SELECTALL");
            }
            else
            {
                for (int a=0; a<setList.length(); ++a)
                {
                    int position = this.list.indexOf(setList.optString(a));

                    Log.d("HFsetList", position + "  " + setList.optString(a));

                    if (position!=-1)
                        setCheckItem(position, false);
                }

                notifyDataSetChanged();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

}
