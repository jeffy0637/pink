package m.mcoupledate.classes.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import m.mcoupledate.R;
import m.mcoupledate.funcs.PinkCon;

/**
 * Created by user on 2016/8/26.
 */
public class SiteListAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater mInflater;
    private JSONArray sites;


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
    public JSONObject getItem(int arg0) {
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
        JSONObject o = getItem(position);

        if (o.optBoolean("noResult"))
        {
            TextView noResultText = new TextView(context);
            noResultText.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            noResultText.setText("查無結果");
            return noResultText;
        }


        if (convertView==null)
            convertView = mInflater.inflate(R.layout.item_sitelist_onesite, null);


        try {
            ((TextView) convertView.findViewById(R.id.title)).setText(o.optString("sName"));
            ((TextView) convertView.findViewById(R.id.location)).setText(o.optString("address"));


            RatingBar loveRateBar = ((RatingBar) convertView.findViewById(R.id.ratingBar));
            LayerDrawable stars = (LayerDrawable) loveRateBar.getProgressDrawable();
            stars.getDrawable(2).setColorFilter(ContextCompat.getColor(context, R.color.pinkpink), PorterDuff.Mode.SRC_ATOP); // for filled stars
            stars.getDrawable(1).setColorFilter(ContextCompat.getColor(context, R.color.pinkpink), PorterDuff.Mode.SRC_ATOP); // for half filled stars
            stars.getDrawable(0).setColorFilter(Color.LTGRAY, PorterDuff.Mode.SRC_ATOP); // for empty stars

            if (o.optString("love") == null || o.optString("love").compareTo("null") == 0)
                loveRateBar.setRating(0f);
            else
                loveRateBar.setRating(Float.valueOf(o.optString("love")));


            Glide.with(context)
                    .load(PinkCon.URL + "images/sitePic/" + o.optString("picId") + "a.jpg")
                    .error(R.drawable.ic_landscape_black_48dp)
                    .into((ImageView) convertView.findViewById(R.id.attraction_image));
        }
        catch(Exception e)
        {
            return getView(position, null, parent);
        }

        return convertView;
    }


    public void changeData(JSONArray sites) throws JSONException {
        this.sites = sites;
        if (sites.length()==0)
           this.sites.put(new JSONObject("{'noResult':'true'}"));
        this.notifyDataSetChanged();
    }

    public String getSiteId(int position)
    {
        return getItem(position).optString("sId");
    }
}
