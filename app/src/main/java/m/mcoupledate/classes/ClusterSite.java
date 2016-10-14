package m.mcoupledate.classes;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

import m.mcoupledate.R;

/**
 * Created by user on 2016/7/20.
 */
public class ClusterSite implements ClusterItem {

    public String name;
    public Bitmap pic;
    public LatLng position;

    public ClusterSite(LatLng position, String name, Bitmap pic)
    {
        this.position = position;
        this.name = name;
        this.pic = pic;
    }

    public ClusterSite(LatLng position, String name, Context context)
    {
        this.position = position;
        this.name = name;
        this.pic = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_mountain);
    }

    public ClusterSite(LatLng position, String name)
    {
        this.position = position;
        this.name = name;
        this.pic = null;
    }

    @Override
    public LatLng getPosition() {
        return position;
    }
}