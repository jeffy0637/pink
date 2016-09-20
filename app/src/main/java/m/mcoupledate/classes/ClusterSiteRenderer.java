package m.mcoupledate.classes;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;

import java.util.ArrayList;
import java.util.List;

import m.mcoupledate.R;

/**
 * Created by user on 2016/7/22.
 */
public class ClusterSiteRenderer extends DefaultClusterRenderer<m.mcoupledate.classes.ClusterSite> {

    private final IconGenerator mIconGenerator;
    private final IconGenerator mClusterIconGenerator;
    private final ImageView mImageView;
    private final ImageView mClusterImageView;
    private final int mDimension;
    private LayoutInflater mInflater;
    private Context context;
    private GoogleMap mMap;

    public ClusterSiteRenderer(Context context, GoogleMap mMap, ClusterManager<m.mcoupledate.classes.ClusterSite> mClusterManager) {

        super(context, mMap, mClusterManager);
        this.context = context;
        this.mMap = mMap;

        mIconGenerator = new IconGenerator(context);
        mClusterIconGenerator = new IconGenerator(context);
        mInflater = LayoutInflater.from(context);

        View multiProfile = mInflater.inflate(R.layout.map_multi_profile, null);
        mClusterIconGenerator.setContentView(multiProfile);
        mClusterImageView = (ImageView) multiProfile.findViewById(R.id.image);


            /*
             *  設定小圖
             */
        mImageView = new ImageView(context);
        mDimension = (int) context.getResources().getDimension(R.dimen.map_custom_profile_image);       // 設定尺寸
        mImageView.setLayoutParams(new ViewGroup.LayoutParams(mDimension, mDimension));
        int padding = (int) context.getResources().getDimension(R.dimen.map_custom_profile_padding);    // 設定padding
        mImageView.setPadding(padding, padding, padding, padding);

        mIconGenerator.setContentView(mImageView);

    }

    /*
     *  地點render前
     */
    @Override
    protected void onBeforeClusterItemRendered(m.mcoupledate.classes.ClusterSite site, MarkerOptions markerOptions) {
        // Draw a single person.
        // Set the info window to show their name.
        mImageView.setImageBitmap(site.pic);
        Bitmap icon = mIconGenerator.makeIcon();
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon)).title(site.name);
    }

    /*
     *  群集render前
     */
    @Override
    protected void onBeforeClusterRendered(Cluster<m.mcoupledate.classes.ClusterSite> cluster, MarkerOptions markerOptions) {
        // Draw multiple people.
        // Note: this method runs on the UI thread. Don't spend too much time in here (like in this example).
        List<Drawable> profilePhotos = new ArrayList<Drawable>(Math.min(4, cluster.getSize()));
        int width = mDimension;
        int height = mDimension;

        for (m.mcoupledate.classes.ClusterSite p : cluster.getItems()) {
            // Draw 4 at most.
            if (profilePhotos.size() == 4) break;
            Drawable drawable = new BitmapDrawable(null, p.pic);
            drawable.setBounds(0, 0, width, height);
            profilePhotos.add(drawable);
        }
        m.mcoupledate.classes.MultiDrawable multiDrawable = new m.mcoupledate.classes.MultiDrawable(profilePhotos, cluster.getSize());
        multiDrawable.setBounds(0, 0, width, height);

        mClusterImageView.setImageDrawable(multiDrawable);
        Bitmap icon = mClusterIconGenerator.makeIcon(String.valueOf(cluster.getSize()));
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon));

        if (mMap.getCameraPosition().zoom==mMap.getMaxZoomLevel())
        {
            String clusterTitles = "";

            for (m.mcoupledate.classes.ClusterSite c : cluster.getItems())
                clusterTitles = clusterTitles + c.name + "\n";

            markerOptions.title(clusterTitles);
        }

    }

    @Override
    protected boolean shouldRenderAsCluster(Cluster cluster) {
        // Always render clusters.
        return cluster.getSize() > 1;
    }

//    @Override
//    protected void onClusterRendered(Cluster<ClusterSite> cluster, Marker marker)
//    {
//        Toast.makeText(context, marker.getTitle(), Toast.LENGTH_SHORT).show();
//        marker.showInfoWindow();
//
//    }

}