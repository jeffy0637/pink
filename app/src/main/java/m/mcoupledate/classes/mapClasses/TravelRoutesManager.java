package m.mcoupledate.classes.mapClasses;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.util.SparseArray;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Polyline;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import m.mcoupledate.R;
import m.mcoupledate.classes.funcs.PinkCon;

/**
 * Created by user on 2016/10/25.
 */

public class TravelRoutesManager implements
        GoogleMap.OnPolylineClickListener
{

    private GoogleMap mMap;
    private RequestQueue mQueue;
    private SparseArray<ArrayList<RouteSite>> rSitesManager  = new SparseArray<>();
    //  天數從1開始，景點從0開始

    private int[][] travelDayColorPair;


    public TravelRoutesManager(Context context, GoogleMap mMap)
    {
        this.mMap = mMap;

        mQueue = Volley.newRequestQueue(context);

        mMap.setOnPolylineClickListener(this);

        travelDayColorPair = new int[][]{
            {ContextCompat.getColor(context, R.color.travelDayColor1), ContextCompat.getColor(context, R.color.travelDayColor1_selected)},
            {ContextCompat.getColor(context, R.color.travelDayColor2), ContextCompat.getColor(context, R.color.travelDayColor2_selected)},
            {ContextCompat.getColor(context, R.color.travelDayColor3), ContextCompat.getColor(context, R.color.travelDayColor3_selected)}
        };
    }


    public void addADayRoute(final int dayRouteId, final ArrayList<String> routeSites, final int initSiteSeq)
    {
        //  若不需init顯示哪段的話，initSiteSeq可設負數

        if (routeSites.size()==0)
            return ;

        //  負責抓景點的細節資訊，抓完後丟給setADayRoute()
        StringRequest stringRequest = new StringRequest(Request.Method.POST, PinkCon.URL + "travelMap_getRouteSitesDetails.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response)
                    {
                        try
                        {
                            JSONArray rSitesDetails = new JSONArray(response);

                            setADayRoute(dayRouteId, rSitesDetails, initSiteSeq);
                        }
                        catch (JSONException e)
                        {   Log.d("HFjsonerr", e.getMessage());    }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
//                        PinkCon.retryConnect(getRootView(), PinkCon.LOAD_PINKSITES_FAIL, initErrorBar,
//                                new View.OnClickListener()
//                                {
//                                    @Override
//                                    public void onClick(View view)
//                                    {   loadPinkClusterSites(mQueue, initErrorBar);  }
//                                });
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();

                map.put("routeSites", routeSites.toString());

                return map;
            }
        };

        mQueue.add(stringRequest);
    }


    private void setADayRoute(final int dayRouteId, final JSONArray rSitesDetails, final int initSiteSeq)
    {
        final ArrayList<RouteSite> aDayRSites = new ArrayList<RouteSite>();
        rSitesManager.put(dayRouteId, aDayRSites);


        final int colorPairIndex = (dayRouteId-1)%3;

        if (rSitesDetails.length()==1)
        {
            JSONObject aRSitesDetail = rSitesDetails.optJSONObject(0);
            aDayRSites.add(new RouteSite(mMap, aRSitesDetail.optString("sId"), aRSitesDetail.optString("sName"), new LatLng(aRSitesDetail.optDouble("Py"), aRSitesDetail.optDouble("Px")), travelDayColorPair[colorPairIndex]));

            return ;
        }



        LatLng origin = new LatLng(rSitesDetails.optJSONObject(0).optDouble("Py"), rSitesDetails.optJSONObject(0).optDouble("Px"));
        LatLng dest = new LatLng(rSitesDetails.optJSONObject(rSitesDetails.length()-1).optDouble("Py"), rSitesDetails.optJSONObject(rSitesDetails.length()-1).optDouble("Px"));

        String wayPointsDesc = "";
        if (rSitesDetails.length()>2)
        {
            for (int a=1; a<(rSitesDetails.length()-1); ++a)
            {
                wayPointsDesc += rSitesDetails.optJSONObject(a).optDouble("Py") + "%2C" +rSitesDetails.optJSONObject(a).optDouble("Px") + "|";
            }
            wayPointsDesc = "&waypoints=" + wayPointsDesc.substring(0, (wayPointsDesc.length()-1));
        }

        String url = "https://maps.googleapis.com/maps/api/directions/json?origin="+origin.latitude+"%2C"+origin.longitude+"&destination="+dest.latitude+"%2C"+dest.longitude+wayPointsDesc+"&key=AIzaSyBn1wKXTrwBl2qZRVY9feOZC3aeklAnZXg";

        final StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response)
                    {
                        try
                        {
                            JSONArray legs = new JSONObject(response).optJSONArray("routes").optJSONObject(0).optJSONArray("legs");

                            for (int a=0; a<rSitesDetails.length(); ++a)
                            {
                                JSONObject aRSitesDetail = rSitesDetails.optJSONObject(a);

                                if (legs.optJSONObject(a)!=null)
                                    aDayRSites.add(new RouteSite(mMap, aRSitesDetail.optString("sId"), aRSitesDetail.optString("sName"), legs.optJSONObject(a), travelDayColorPair[colorPairIndex]));
                                else
                                    aDayRSites.add(new RouteSite(mMap, aRSitesDetail.optString("sId"), aRSitesDetail.optString("sName"), new LatLng(aRSitesDetail.optDouble("Py"), aRSitesDetail.optDouble("Px")), travelDayColorPair[colorPairIndex]));
                            }

                            if (initSiteSeq>=0 && initSiteSeq<=aDayRSites.size())
                                showADayRoute(dayRouteId, initSiteSeq);
                        }
                        catch (Exception e)
                        {
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
//                        PinkCon.retryConnect(getRootView(), PinkCon.LOAD_PINKSITES_FAIL, initErrorBar,
//                                new View.OnClickListener()
//                                {
//                                    @Override
//                                    public void onClick(View view)
//                                    {   loadPinkClusterSites(mQueue, initErrorBar);  }
//                                });
                    }
                });

        mQueue.add(stringRequest);
    }

    public void showADayRoute(int dayRouteId)
    {   showADayRoute(dayRouteId, -1);   }

    public void showADayRoute(int dayRouteId, int siteSeq)
    {
        for (int a=0; a<rSitesManager.size(); ++a)
        {
            if (rSitesManager.keyAt(a)==dayRouteId)
            {
                for (int b=0; b< rSitesManager.valueAt(a).size(); ++b)
                {
                    rSitesManager.valueAt(a).get(b).showRoute();

                    if (b == siteSeq)
                        moveToRoute(rSitesManager.valueAt(a).get(b));
                }

                if (siteSeq==-1)
                    displayAllDayRoutes(rSitesManager.valueAt(a));
            }
            else
            {
                for (int b=0; b< rSitesManager.valueAt(a).size(); ++b)
                    rSitesManager.valueAt(a).get(b).hideRoute();
            }
        }
    }


    @Override
    public void onPolylineClick(Polyline polyline)
    {
        for (int a=0; a<rSitesManager.size(); ++a)
        {
            int dayRouteId = rSitesManager.keyAt(a);

            for (RouteSite routeSite : rSitesManager.get(dayRouteId))
            {
                if (routeSite.ifContainsRouteLine(polyline))
                    moveToRoute(routeSite);
                else
                    routeSite.unFocus();
            }
        }

    }

    private void moveToRoute(RouteSite routeSite)
    {
        routeSite.focus();

        LatLngBounds.Builder builder = LatLngBounds.builder();

        builder.include(routeSite.start);
        builder.include(routeSite.end);

        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 100));
    }

    private void displayAllDayRoutes(ArrayList<RouteSite> aDayRSites)
    {
        LatLngBounds.Builder builder = LatLngBounds.builder();

        for (RouteSite routeSite : aDayRSites)
        {
            builder.include(routeSite.start);
            if (routeSite.end!=null)
                builder.include(routeSite.end);
        }

        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 100));
    }


}
