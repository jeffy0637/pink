package m.mcoupledate.classes.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import m.mcoupledate.R;

/**
 * Created by user on 2016/11/24.
 */

public class DynamicSearchAdapter extends BaseAdapter
{
    private LayoutInflater mInflater;

    private ArrayList<AResult> resultArrayList;


    public DynamicSearchAdapter(Context context)
    {
        mInflater = LayoutInflater.from(context);

        this.resultArrayList = new ArrayList<>();
    }



    @Override
    public int getCount()
    {
        return this.resultArrayList.size();
    }

    @Override
    public AResult getItem(int position)
    {
        return resultArrayList.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        if (convertView==null)
            convertView = mInflater.inflate(R.layout.item_dynamicsearch_result, null);


        ((TextView) convertView.findViewById(R.id.result)).setText(getItem(position).text);

        return convertView;
    }


    public void add(String aResultText, String aResultValue)
    {
        resultArrayList.add(new AResult(aResultText, aResultValue));

        notifyDataSetChanged();
    }

    public void refresh(JSONArray resultList)
    {
        resultArrayList.clear();

        for (int a=0; a<resultList.length(); ++a)
        {
            try
            {
                JSONObject aResultJSONObj = resultList.getJSONObject(a);

                resultArrayList.add(new AResult(aResultJSONObj.optString("text"), aResultJSONObj.optString("value")));
            }
            catch (JSONException e)
            {   e.printStackTrace();    }
        }

        notifyDataSetChanged();
    }

    public void clear()
    {
        resultArrayList.clear();

        notifyDataSetChanged();
    }

    public static class AResult
    {
        public String value;
        public String text;

        public  AResult(String text, String value)
        {
            this.text = text;
            this.value = value;
        }

    }


}
