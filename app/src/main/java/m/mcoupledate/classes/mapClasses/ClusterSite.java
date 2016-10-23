package m.mcoupledate.classes.mapClasses;

import android.graphics.Bitmap;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

import org.json.JSONObject;

/**
 * Created by user on 2016/7/20.
 */
public class ClusterSite implements ClusterItem {

    public String sId;
    public String picId;
    public String name;
    public int siteType;
    public Bitmap pic = null;
    public LatLng position;

    public static final int SITETYPE_ATTRACTION = 0, SITETYPE_RESTAURANT = 1;


    public ClusterSite(LatLng position, String sId, String picId, String name)
    {
        this.position = position;
        this.sId = sId;
        this.picId = picId;
        this.name = name;
    }


    public ClusterSite(JSONObject site)
    {
        this.position = new LatLng(site.optDouble("Py"), site.optDouble("Px"));
        this.sId = site.optString("sId");
        this.picId = site.optString("picId");
        this.name = site.optString("sName");


        if (site.optString("siteType").compareTo("a")==0)
            this.siteType = SITETYPE_ATTRACTION;
        else if (site.optString("siteType").compareTo("r")==0)
            this.siteType = SITETYPE_RESTAURANT;

    }

    @Override
    public LatLng getPosition() {
        return position;
    }
}