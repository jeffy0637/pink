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
import java.util.Collections;
import java.util.List;

public class StrokeSearchAdapter extends BaseAdapter
{

    private List<Stroke> allStrokeList;  //  存放所有行程 (for search)
    private List<Stroke> resultStrokeList;  //  存放要顯示出來的行程
    private LayoutInflater mInflater;

    public StrokeSearchAdapter(Activity activity)
    {
        mInflater = LayoutInflater.from(activity);
        this.allStrokeList = new ArrayList<>();
        this.resultStrokeList = new ArrayList<>();
    }

    public StrokeSearchAdapter(Activity activity, List<Stroke> strokeList)
    {
        mInflater = LayoutInflater.from(activity);
        this.allStrokeList = strokeList;
        this.resultStrokeList = strokeList;
    }

    public StrokeSearchAdapter(Activity activity, JSONArray strokeJSONArray)
    {
        this.mInflater = LayoutInflater.from(activity);
        this.allStrokeList = new ArrayList<>();
        this.resultStrokeList = new ArrayList<>();

        addAll(strokeJSONArray);
    }

    @Override
    public int getCount()
    {
        return resultStrokeList.size();
    }

    @Override
    public Stroke getItem(int position)
    {
        return resultStrokeList.get(position);
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
        ((TextView) convertView.findViewById(R.id.travel_date)).setText(stroke.getStartDate());


        return convertView ;
    }


    public void add(JSONObject aStrokeJSONObj)
    {
        Stroke aStroke = new Stroke(aStrokeJSONObj);

        this.allStrokeList.add(aStroke);
        this.resultStrokeList.add(aStroke);
    }


    public void addAll(JSONArray strokeJSONArray)
    {
        this.allStrokeList.clear();
        this.resultStrokeList.clear();

        for (int i=0; i<strokeJSONArray.length(); ++i)
        {
            Stroke aStroke = new Stroke(strokeJSONArray.optJSONObject(i));

            this.allStrokeList.add(aStroke);
            this.resultStrokeList.add(aStroke);
        }

        notifyDataSetChanged();
    }



    public void search(String query)
    {
        if (query.compareTo("")==0 || query.matches("[\\s]*"))
            return ;

        this.resultStrokeList.clear();

        String pattern = ".*"+query+".*";

        for (Stroke aStroke : this.allStrokeList)
        {
            if (aStroke.title.matches(pattern))
                this.resultStrokeList.add(aStroke);
            else if (aStroke.containsFeature(query))
                this.resultStrokeList.add(aStroke);
        }

        sortResult(query);

        notifyDataSetChanged();
    }

    private void sortResult(String query)
    {
        for (Stroke aStroke : this.resultStrokeList)
            aStroke.setSearchQuery(query);

        Collections.sort(this.resultStrokeList, Collections.reverseOrder());
    }


    public void closeSearch()
    {
        this.resultStrokeList.clear();
        this.resultStrokeList.addAll(this.allStrokeList);

        notifyDataSetChanged();
    }



}