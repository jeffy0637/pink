package m.mcoupledate.classes.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
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

/**
 * Created by user on 2016/8/26.
 */
public class CommentAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater mInflater;
    private JSONArray sites;



    public CommentAdapter (Context context, JSONArray sites)
    {
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
        if (sites==null)
        {
            try {
                this.sites = new JSONArray("[]");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else
            this.sites = sites;

//        Log.d("HFSITEJSON", sites.toString() + " = = = " + sites.length());
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
            convertView = mInflater.inflate(R.layout.item_comment, null);

        JSONObject o = (JSONObject)getItem(position);
//        Log.d("HFCOMMENTJSON", o.toString());

        ((TextView) convertView.findViewById(R.id.mName)).setText(o.optString("name"));
        ((TextView) convertView.findViewById(R.id.text)).setText(o.optString("text"));

        RatingBar loveRateBar = (RatingBar) convertView.findViewById(R.id.loveRateBar);
        loveRateBar.setRating(Float.valueOf(o.optString("person_love")));
        LayerDrawable stars = (LayerDrawable) loveRateBar.getProgressDrawable();
        stars.getDrawable(2).setColorFilter(context.getResources().getColor(R.color.pinkpink), PorterDuff.Mode.SRC_ATOP); // for filled stars
        stars.getDrawable(1).setColorFilter(context.getResources().getColor(R.color.pinkpink), PorterDuff.Mode.SRC_ATOP); // for half filled stars
        stars.getDrawable(0).setColorFilter(Color.LTGRAY, PorterDuff.Mode.SRC_ATOP); // for empty stars

        Glide.with(context)
            .load("https://graph.facebook.com/" + o.optString("mId") + "/picture")
            .error(R.drawable.ic_landscape_black_48dp)
                .into((ImageView) convertView.findViewById(R.id.profilePic));

        return convertView;
    }


    public void changeData(JSONArray sites)
    {
        this.sites = sites;
        this.notifyDataSetChanged();
    }
}
