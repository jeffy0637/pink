package m.mcoupledate;

import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;

import java.util.ArrayList;

import m.mcoupledate.classes.NavigationActivity;
import m.mcoupledate.classes.mapClasses.TravelRoutesManager;


public class TravelMap extends NavigationActivity implements
        OnMapReadyCallback
{

    private GoogleMap mMap;

    private TravelRoutesManager travelRoutesManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_travel_map);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        mMap = googleMap;

        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(22.627659714392056,120.27963057160379), 13));

        travelRoutesManager = new TravelRoutesManager(this, mMap);
        travelRoutesManager.addADayRoute("day1", (ArrayList<String>)this.getIntent().getSerializableExtra("routeSites"), true);


//        drawTravelRoutes((ArrayList<String>)this.getIntent().getSerializableExtra("travelSites"));
    }
}
