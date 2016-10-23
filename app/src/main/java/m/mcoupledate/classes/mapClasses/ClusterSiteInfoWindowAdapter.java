package m.mcoupledate.classes.mapClasses;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.model.Marker;

import m.mcoupledate.R;

/**
 * Created by user on 2016/10/22.
 */

public class ClusterSiteInfoWindowAdapter implements InfoWindowAdapter {

    private final View infoWindow;

    public ClusterSiteInfoWindowAdapter(Context context, Boolean ifNeedAroundsInfo)
    {
        if (ifNeedAroundsInfo)
            infoWindow = LayoutInflater.from(context).inflate(R.layout.map_infowindow_aroundsite, null);
        else
            infoWindow = LayoutInflater.from(context).inflate(R.layout.map_infowindow_notaround, null);
    }

    @Override
    public View getInfoContents(Marker marker) {


        ((TextView) infoWindow.findViewById(R.id.siteTitle)).setText(marker.getTitle());



        return infoWindow;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        // TODO Auto-generated method stub


//        ((TextView) infoWindow.findViewById(R.id.title)).setText(marker.getTitle());

        return null;
//        return infoWindow;
    }
}

