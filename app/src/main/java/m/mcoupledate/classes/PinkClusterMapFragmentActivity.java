package m.mcoupledate.classes;

import android.content.Intent;
import android.support.annotation.CallSuper;
import android.support.annotation.IdRes;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;

import org.json.JSONArray;

import m.mcoupledate.R;
import m.mcoupledate.SiteInfo;
import m.mcoupledate.classes.mapClasses.ClusterSite;
import m.mcoupledate.classes.mapClasses.ClusterSiteInfoWindowAdapter;
import m.mcoupledate.classes.mapClasses.ClusterSiteRenderer;
import m.mcoupledate.classes.mapClasses.WorkaroundMapFragment;
import m.mcoupledate.classes.funcs.PinkCon;


/**
 * Created by user on 2016/10/8.
 */
public class PinkClusterMapFragmentActivity extends NavigationActivity implements
        OnMapReadyCallback,
        ClusterManager.OnClusterClickListener<ClusterSite>,
        ClusterManager.OnClusterInfoWindowClickListener<ClusterSite>,
        ClusterManager.OnClusterItemClickListener<ClusterSite>,
        ClusterManager.OnClusterItemInfoWindowClickListener<ClusterSite>
{
//    public Context context;
    public GoogleMap mMap;

    public ClusterManager<ClusterSite> mClusterManager;

    private Marker theSiteMarker = null;


    public WorkaroundMapFragment getMapFragment(@IdRes int mapId)
    {
        WorkaroundMapFragment mapFragment = (WorkaroundMapFragment) getSupportFragmentManager().findFragmentById(mapId);

        if (mapFragment == null)
        {
            mapFragment = new WorkaroundMapFragment(); // (WorkaroundMapFragment) WorkaroundMapFragment.newInstance();
            getSupportFragmentManager().beginTransaction().replace(mapId, mapFragment).commit();
        }

        return mapFragment;
    }

    @Override@CallSuper
    public void onMapReady(GoogleMap mMap)
    {
        this.mMap = mMap;

        LatLng y = new LatLng(23.9036873,121.0793705);  // 預設台灣
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(y, 6));

        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);


        mClusterManager = new ClusterManager<ClusterSite>(this, mMap);

        mClusterManager.setRenderer(new ClusterSiteRenderer(this, mMap, mClusterManager));

        mMap.setOnCameraIdleListener(mClusterManager);
        mMap.setOnMarkerClickListener(mClusterManager);
        mMap.setOnInfoWindowClickListener(mClusterManager);
        mMap.setInfoWindowAdapter(mClusterManager.getMarkerManager());

        // 當點擊群集時引發事件
        mClusterManager.setOnClusterClickListener(this);
        mClusterManager.setOnClusterInfoWindowClickListener(this);
        mClusterManager.setOnClusterItemClickListener(this);
        mClusterManager.setOnClusterItemInfoWindowClickListener(this);
        mClusterManager.getMarkerCollection().setOnInfoWindowAdapter(new ClusterSiteInfoWindowAdapter(this, this.getIntent().getBooleanExtra("ifNeedAroundsInfo", true)));
    }

    public void addClusterMarker(ClusterSite clusterSite, Boolean ifCluster)
    {
        mClusterManager.addItem(clusterSite);

        if (ifCluster)
            mClusterManager.cluster();
    }

    public void setTheSiteMarker(LatLng inputLatLng, String title)
    {
        if (theSiteMarker !=null)
            theSiteMarker.remove();

        float zoomLevel = 0;
        if (mMap.getCameraPosition().zoom<(mMap.getMaxZoomLevel()-8))
            zoomLevel = mMap.getMaxZoomLevel()-8;
        else
            zoomLevel = mMap.getCameraPosition().zoom;

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(inputLatLng, zoomLevel));
        try
        {
            theSiteMarker = mMap.addMarker(new MarkerOptions().position(inputLatLng).title(title).icon(BitmapDescriptorFactory.fromResource(R.drawable.map_thesite)));
            theSiteMarker.showInfoWindow();
        }
        catch (Exception e)
        {}
    }

    protected Marker getTheSite()
    {
        return theSiteMarker;
    }


    public void loadPinkClusterSites(final RequestQueue mQueue, final PinkCon.InitErrorBar initErrorBar)
    {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, PinkCon.URL+"pinkClusterMap_getPinkSites.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try
                        {
                            JSONArray jArr = new JSONArray(response);

                            for (int a=0; a<jArr.length(); ++a)
                            {
                                if ((a%35)==34 || a==(jArr.length()-1))
                                    addClusterMarker(new ClusterSite(jArr.getJSONObject(a)), true);
                                else
                                    addClusterMarker(new ClusterSite(jArr.getJSONObject(a)), false);
                            }
                        }
                        catch (Exception e)
                        {
                            PinkCon.retryConnect(getRootView(), PinkCon.LOAD_PINKSITES_FAIL, initErrorBar,
                                new View.OnClickListener()
                                {
                                    @Override
                                    public void onClick(View view)
                                    {   loadPinkClusterSites(mQueue, initErrorBar);  }
                                });
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        PinkCon.retryConnect(getRootView(), PinkCon.LOAD_PINKSITES_FAIL, initErrorBar,
                            new View.OnClickListener()
                            {
                                @Override
                                public void onClick(View view)
                                {   loadPinkClusterSites(mQueue, initErrorBar);  }
                            });
                    }
                });

        mQueue.add(stringRequest);
    }




    @Override
    public boolean onClusterClick(Cluster<ClusterSite> cluster)
    {
        // Show a toast with some info when the cluster is clicked.
        String firstName = cluster.getItems().iterator().next().name;
        Toast.makeText(this,  firstName+"和"+(cluster.getSize()-1)+"個景點", Toast.LENGTH_SHORT).show();

        // Zoom in the cluster. Need to create LatLngBounds and including all the cluster items
        // inside of bounds, then animate to center of the bounds.

        // Create the builder to collect all essential cluster items for the bounds.
        LatLngBounds.Builder builder = LatLngBounds.builder();
        for (ClusterSite item : cluster.getItems()) {
            builder.include(item.getPosition());
        }
        // Get the LatLngBounds
        final LatLngBounds bounds = builder.build();

        // Animate camera to the bounds
        try {
            mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }

    @Override
    public void onClusterInfoWindowClick(Cluster<ClusterSite> cluster) {
        // Does nothing, but you could go to a list of the users.
    }

    @Override
    public boolean onClusterItemClick(ClusterSite clusterSite) {
        // Does nothing, but you could go into the user's profile page, for example.
        return false;
    }

    @Override
    public void onClusterItemInfoWindowClick(ClusterSite clusterSite)
    {
        if (!this.getIntent().getBooleanExtra("ifNeedAroundsInfo", true))
            return;

        Intent intent = new Intent(this, SiteInfo.class);
        intent.putExtra("sId", clusterSite.sId);
        intent.putExtra("ifNeedAroundsInfo", false);
        startActivity(intent);
    }
}
