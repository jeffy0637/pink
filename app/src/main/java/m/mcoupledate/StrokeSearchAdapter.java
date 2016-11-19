package m.mcoupledate;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class StrokeSearchAdapter extends BaseAdapter
{

    private List<Stroke> strokeList;
    private LayoutInflater mInflater;

    public StrokeSearchAdapter(Activity activity)
    {
        mInflater = LayoutInflater.from(activity);
        this.strokeList = new ArrayList<>();
    }

    public StrokeSearchAdapter(Activity activity, List<Stroke> strokeList)
    {
        mInflater = LayoutInflater.from(activity);
        this.strokeList = strokeList;
    }

    public StrokeSearchAdapter(Activity activity, JSONArray strokeJSONArray)
    {
        this.mInflater = LayoutInflater.from(activity);
        this.strokeList = new ArrayList<>();

        addAll(strokeJSONArray);
    }

    @Override
    public int getCount()
    {
        return strokeList.size();
    }

    @Override
    public Stroke getItem(int position)
    {
        return strokeList.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup)
    {
        if (convertView==null)
            convertView = mInflater.inflate(R.layout.trip_item,null);

        Stroke stroke = getItem(position);

        ((TextView) convertView.findViewById(R.id.travel_name)).setText(stroke.getTitle());
        ((TextView) convertView.findViewById(R.id.travel_date)).setText(stroke.getStarttime());


        return convertView ;
    }


    public void add(JSONObject aStroke)
    {
//        strokeList.add(new Stroke())
    }


    public void addAll(JSONArray strokeJSONArray)
    {
        if (getCount()>0)
            this.strokeList.clear();

        for (int i=0; i<strokeJSONArray.length(); ++i)
        {
            JSONObject aStroke = strokeJSONArray.optJSONObject(i);
            this.strokeList.add(new Stroke(aStroke.optString("tName"), aStroke.optString("startDate"), aStroke.optString("endDate"), aStroke.optString("tId")));
        }

        notifyDataSetChanged();
    }



}