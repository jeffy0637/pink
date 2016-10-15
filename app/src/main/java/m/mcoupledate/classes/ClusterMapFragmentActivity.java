package m.mcoupledate.classes;

import android.content.Context;
import android.support.annotation.IdRes;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;

/**
 * Created by user on 2016/10/8.
 */
public class ClusterMapFragmentActivity extends FragmentActivity implements
        ClusterManager.OnClusterClickListener<ClusterSite>,
        ClusterManager.OnClusterInfoWindowClickListener<ClusterSite>,
        ClusterManager.OnClusterItemClickListener<ClusterSite>,
        ClusterManager.OnClusterItemInfoWindowClickListener<ClusterSite>
{
    public Context context;
    public GoogleMap mMap;

    public ClusterManager<ClusterSite> mClusterManager;

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


    public void setUpClusterMap(Context context, GoogleMap mMap)
    {
        this.context = context;
        this.mMap = mMap;

        LatLng y = new LatLng(23.9036873,121.0793705);  // 預設台灣
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(y, 6));

        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);


        mClusterManager = new ClusterManager<ClusterSite>(context, mMap);

//        mClusterManager.setRenderer(new ClusterSiteRenderer(context, mMap, mClusterManager));

        mMap.setOnMarkerClickListener(mClusterManager);
        mMap.setOnCameraIdleListener(mClusterManager);
        //mMap.setOnInfoWindowClickListener(mClusterManager); //  當點擊資訊視窗時引發事件

        // 當點擊群集時引發事件
        mClusterManager.setOnClusterClickListener(this);
        mClusterManager.setOnClusterInfoWindowClickListener(this);
        mClusterManager.setOnClusterItemClickListener(this);
        mClusterManager.setOnClusterItemInfoWindowClickListener(this);
    }

    public void addClusterMarker(ClusterSite clusterSite, Boolean ifCluster)
    {
        mClusterManager.addItem(clusterSite);

        if (ifCluster)
            mClusterManager.cluster();
    }




    @Override
    public boolean onClusterClick(Cluster<ClusterSite> cluster)
    {
        // Show a toast with some info when the cluster is clicked.
        String firstName = cluster.getItems().iterator().next().name;
        Toast.makeText(context,  firstName+"和"+(cluster.getSize()-1)+"個景點AA", Toast.LENGTH_SHORT).show();

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
    public void onClusterItemInfoWindowClick(ClusterSite clusterSite) {
        try
        {
            Toast.makeText(context, clusterSite.name, Toast.LENGTH_SHORT).show();
        }
        catch(Exception e)
        {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
