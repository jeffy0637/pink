package m.mcoupledate;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import m.mcoupledate.classes.NavigationActivity;
import m.mcoupledate.classes.adapters.TravelDaySelectorAdapter;
import m.mcoupledate.classes.funcs.Actioner;
import m.mcoupledate.classes.mapClasses.TravelRoutesManager;


public class TravelMap extends NavigationActivity implements
        OnMapReadyCallback
{
    private Context context;

    private GoogleMap mMap;

    private TravelRoutesManager travelRoutesManager;

    private ListView dayListView;
    private TravelDaySelectorAdapter travelDaySelectorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_travel_map);

        this.context = this;


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        travelDaySelectorAdapter = new TravelDaySelectorAdapter(context);

        dayListView = (ListView) findViewById(R.id.dayListView);
        dayListView.setAdapter(travelDaySelectorAdapter);


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

        Intent intent = getIntent();
        try
        {
            initRoutes(new JSONObject(intent.getStringExtra("aStroke")), (intent.getIntExtra("day", 0)+1), intent.getIntExtra("seq", 0));
        }
        catch (JSONException e)
        {   e.printStackTrace();    }

    }


    private void initRoutes(JSONObject aStroke, int initDay, int initSiteSeq)
    {
        JSONArray sitesJSONArray = aStroke.optJSONArray("sites");

        for (int a=0; a<sitesJSONArray.length(); ++a)
        {
            ArrayList<String> aDaySites = new ArrayList<>();
            for (int b=0; b<sitesJSONArray.optJSONArray(a).length(); ++b)
                aDaySites.add(sitesJSONArray.optJSONArray(a).optString(b));

            if ((a+1)==initDay)
                travelRoutesManager.addADayRoute((a+1), aDaySites, initSiteSeq);
            else
                travelRoutesManager.addADayRoute((a+1), aDaySites, -1);
        }


        travelDaySelectorAdapter.setDayNum(sitesJSONArray.length(), initDay);
        travelDaySelectorAdapter.setTravelDaySwitcher(new Actioner() {
            @Override
            public void act(Object... args)
            {
                int dayId = (int) args[0];

                travelRoutesManager.showADayRoute(dayId);
            }
        });


    }
}
