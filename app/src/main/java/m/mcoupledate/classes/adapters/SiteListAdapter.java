package m.mcoupledate.classes.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import m.mcoupledate.R;

/**
 * Created by user on 2016/8/26.
 */
public class SiteListAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater mInflater;
    private JSONArray sites;

    private String pinkCon = "http://140.117.71.216/pinkCon/";


    public SiteListAdapter(Context context, JSONArray sites)
    {
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
        this.sites = sites;
    }

    @Override
    public int getCount() {
        return sites.length();
    }

    @Override
    public Object getItem(int arg0) {
        try {
            return sites.getJSONObject(arg0);
        } catch (JSONException e) {
            return null;
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        if (convertView==null)
            convertView = mInflater.inflate(R.layout.attraction_init, null);

        JSONObject o = (JSONObject)getItem(position);

        ((TextView) convertView.findViewById(R.id.title)).setText(o.optString("sName"));
        ((TextView) convertView.findViewById(R.id.location)).setText(o.optString("address"));

        Glide.with(context)
                .load(pinkCon + "images/sitePic/" + o.optString("picId") + "a.jpg")
                .error(R.drawable.ic_landscape_black_48dp)
                .into((ImageView) convertView.findViewById(R.id.attraction_image));

        return convertView;
    }


    public void changeData(JSONArray sites)
    {
        this.sites = sites;
        this.notifyDataSetChanged();
    }
}
