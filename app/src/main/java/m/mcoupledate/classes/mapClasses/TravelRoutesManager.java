package m.mcoupledate.classes.mapClasses;

import android.content.Context;

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
import java.util.List;
import java.util.Map;

import m.mcoupledate.classes.funcs.PinkCon;

/**
 * Created by user on 2016/10/25.
 */

public class TravelRoutesManager implements
        GoogleMap.OnPolylineClickListener
{

    private GoogleMap mMap;
    private RequestQueue mQueue;
    private HashMap<String, ArrayList<RouteSite>> rSitesManager  = new HashMap<String, ArrayList<RouteSite>>();


    public TravelRoutesManager(Context context, GoogleMap mMap)
    {
        this.mMap = mMap;

        mQueue = Volley.newRequestQueue(context);

        mMap.setOnPolylineClickListener(this);

    }


    public void addADayRoute(final String dayRouteName, final List<String> routeSites, final Boolean ifShow)
    {
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

                            setADayRoute(dayRouteName, rSitesDetails, ifShow);
                        }
                        catch (JSONException e)
                        {   e.printStackTrace();    }
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


    private void setADayRoute(final String dayRouteName, final JSONArray rSitesDetails, final Boolean ifShow)
    {
        final ArrayList<RouteSite> aDayRSites = new ArrayList<RouteSite>();
        rSitesManager.put(dayRouteName, aDayRSites);



        if (rSitesDetails.length()==1)
        {
            JSONObject aRSitesDetail = rSitesDetails.optJSONObject(0);
            aDayRSites.add(new RouteSite(aRSitesDetail.optString("sId"), aRSitesDetail.optString("sName"), new LatLng(aRSitesDetail.optDouble("Py"), aRSitesDetail.optDouble("Px"))));

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

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
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
                                    aDayRSites.add(new RouteSite(aRSitesDetail.optString("sId"), aRSitesDetail.optString("sName"), legs.optJSONObject(a)));
                                else
                                    aDayRSites.add(new RouteSite(aRSitesDetail.optString("sId"), aRSitesDetail.optString("sName"), new LatLng(aRSitesDetail.optDouble("Py"), aRSitesDetail.optDouble("Px"))));
                            }

                            if (ifShow)
                                showADayRoute(dayRouteName);
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


    public void showADayRoute(String dayRouteName)
    {
        for (RouteSite routeSite : rSitesManager.get(dayRouteName))
        {
            routeSite.showRoute(mMap);
        }
    }


    @Override
    public void onPolylineClick(Polyline polyline)
    {
        for (String dayRouteName : rSitesManager.keySet())
        {
            for (RouteSite routeSite : rSitesManager.get(dayRouteName))
            {
                if (routeSite.ifContainsRouteLine(polyline))
                    moveToRoute(routeSite);
            }
        }
    }

    public void moveToRoute(RouteSite routeSite)
    {
        LatLngBounds.Builder builder = LatLngBounds.builder();

        builder.include(routeSite.start);
        builder.include(routeSite.end);

        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 100));
    }
}
