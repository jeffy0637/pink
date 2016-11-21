package m.mcoupledate.classes.mapClasses;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 2016/10/25.
 */

public class RouteSite
{
    public String sId;
    public String sName;
    public LatLng start = null, end = null;

    private Marker startMarker = null;

    private ArrayList<RouteStep> routeSteps = new ArrayList<>();

    private GoogleMap mMap = null;

    private Boolean ifFocused= false;
    private int[] routeLineColorPair;


    public RouteSite(GoogleMap mMap, String sId, String sName, JSONObject leg, int[] routeLineColorPair)
    {
        this.sId = sId;
        this.sName = sName;

        this.mMap = mMap;
        this.start = new LatLng(leg.optJSONObject("start_location").optDouble("lat"), leg.optJSONObject("start_location").optDouble("lng"));
        this.end = new LatLng(leg.optJSONObject("end_location").optDouble("lat"), leg.optJSONObject("end_location").optDouble("lng"));

        JSONArray steps = leg.optJSONArray("steps");
        for (int a=0; a<steps.length(); ++a)
            this.routeSteps.add(new RouteStep(steps.optJSONObject(a)));

        this.routeLineColorPair = routeLineColorPair;
    }

    public RouteSite(GoogleMap mMap, String sId, String sName, LatLng start, int[] routeLineColorPair)
    {
        this.sId = sId;
        this.sName = sName;

        this.mMap = mMap;
        this.start = start;

        this.routeLineColorPair = routeLineColorPair;
    }

    public void showRoute()
    {
        if (startMarker!=null)
            startMarker.setVisible(true);
        else
            startMarker = mMap.addMarker(new MarkerOptions().position(start).title(sName));

        for (RouteStep routeStep : routeSteps)
            routeStep.draw();
    }

    public void hideRoute()
    {
        if (startMarker!=null)
            startMarker.setVisible(false);

        for (RouteStep routeStep : routeSteps)
            routeStep.hide();

        unFocus();
    }

    public Boolean ifContainsRouteLine(Polyline polyline)
    {
        for (RouteStep routeStep : routeSteps)
        {
            if (routeStep.routLine!=null && routeStep.routLine.equals(polyline))
                return true;
        }
        return false;
//        return routeSteps.contains(polyline);
    }

    public void focus()
    {
        if (ifFocused)
            return ;

        for (RouteStep routeStep : routeSteps)
        {
            routeStep.routLine.setColor(routeLineColorPair[1]);
        }

        ifFocused = true;
    }

    public void unFocus()
    {
        if (!ifFocused)
            return ;

        for (RouteStep routeStep : routeSteps)
        {
            routeStep.routLine.setColor(routeLineColorPair[0]);
        }

        ifFocused = false;
    }



    private List<LatLng> decodePolylineString(String polylineString)
    {
        //  http://stackoverflow.com/questions/14702621/answer-draw-path-between-two-points-using-google-maps-android-api-v2

        List<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = polylineString.length();
        int lat = 0, lng = 0;

        while (index < len)
        {
            int b, shift = 0, result = 0;

            do
            {
                b = polylineString.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            }
            while (b >= 0x20);

            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;

            do
            {
                b = polylineString.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            }
            while (b >= 0x20);

            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng(((double)lat/1E5),((double)lng/1E5));
            poly.add(p);
        }

        return poly;
    }


    private class RouteStep
    {
        public String distance;
        public String duration;
        public String note;

        public String polylineCode;
        public Polyline routLine = null;

        public RouteStep(JSONObject step)
        {
            this.distance = step.optJSONObject("distance").optString("text");
            this.duration = step.optJSONObject("duration").optString("text");
            this.note = step.optString("html_instructions");

            this.polylineCode = step.optJSONObject("polyline").optString("points");
        }

        public void draw()
        {
            if (routLine!=null)
            {
                routLine.setVisible(true);
            }
            else
            {
                this.routLine = mMap.addPolyline(new PolylineOptions()
                        .addAll(decodePolylineString(polylineCode))
                        .width(12)
                        .color(routeLineColorPair[0])//Google maps blue color
                        .geodesic(true)
                        .clickable(true)
                        .visible(true));
            }
        }

        public void hide()
        {
            if (routLine!=null)
            {
                routLine.setVisible(false);
            }
//            else
//            {
//                this.routLine = mMap.addPolyline(new PolylineOptions()
//                        .addAll(decodePolylineString(polylineCode))
//                        .width(12)
//                        .color(Color.parseColor("#05b1fb"))//Google maps blue color
//                        .geodesic(true)
//                        .clickable(true)
//                        .visible(false));
//            }
        }
    }


}
