package m.mcoupledate.funcs;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by user on 2016/8/19.
 */
public class AuthChecker6 extends Activity
{
    private Activity activity;

    private GoogleMap mMap;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private DialogInterface.OnClickListener checkLocatableListener;
    private final int REQ_LOCATION_PERMISSION = 234;
    private final int REQ_INIT_MYLOCATION = 235;
    private final int REQ_GET_MYPOSITION = 236;
    protected Location mLocation = null;


    public AuthChecker6(Activity activity)
    {
        this.activity = activity;
    }

    public void onResult(Object result) {}



    public void checkMapMyLocation(GoogleMap mMap)
    {
        this.mMap = mMap;

        //  手機內的目前位置manager
        locationManager = (LocationManager) (activity.getSystemService(Context.LOCATION_SERVICE));
        locationListener = new LocationListener()
        {
            @Override
            public void onLocationChanged(Location location) {}
            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {}
            @Override
            public void onProviderEnabled(String s) {}
            @Override
            public void onProviderDisabled(String s) {}
        };



        //  檢查目前位置權限，成功則檢查定位功能，失敗且為android 6，跳出確認權限視窗
        if (ActivityCompat.checkSelfPermission(activity, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED )
            ActivityCompat.requestPermissions(activity, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQ_LOCATION_PERMISSION);
        else
            checkLocatableFor(REQ_INIT_MYLOCATION);

    }

    //  若使用者同意權限就初始相關設定，否則就不做事~~ (for android 6)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        if (requestCode == REQ_LOCATION_PERMISSION) {
            if (permissions.length == 1 && permissions[0] == android.Manifest.permission.ACCESS_FINE_LOCATION
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                checkLocatableFor(REQ_INIT_MYLOCATION);
            }
            else
            {   // Permission was denied. Display an error message.
            }
        }
    }

    //  先檢查定位功能有否打開，有則初始或抓資料，否則前往設定頁(透過StartActivityForResult)
    private void checkLocatableFor(final int requestCode)
    {
        if( !locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) && !locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
        {
            //  宣告"打開定位功能"的跳窗
            checkLocatableListener = new DialogInterface.OnClickListener()
            {   @Override
                public void onClick(DialogInterface dialogInterface, int i)
                {
                    if (i==-1)
                    {   activity.startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), requestCode); }
                }
            };
            new AlertDialog.Builder(activity)
                    .setTitle("定位功能未開啟")
                    .setMessage("目前未開啟定位功能，要開啟定位嗎?")
                    .setPositiveButton("前往設定頁", checkLocatableListener)
                    .setNegativeButton("先還不要", checkLocatableListener)
                    .show();

            return ;
        }

        switch (requestCode)
        {
            case REQ_INIT_MYLOCATION:
                initMyPositionFunc();
                break;
            case REQ_GET_MYPOSITION:
                getMyLocation();
                break;
        }
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        switch (requestCode)
//        {
//            case REQ_INIT_MYLOCATION:
//                checkLocatableFor(REQ_INIT_MYLOCATION);
//                break;
//            case REQ_GET_MYPOSITION:
//                checkLocatableFor(REQ_GET_MYPOSITION);
//                break;
//        }
//
//    }

    //  設定map的我的位置按鈕
    private void initMyPositionFunc()
    {
//        mMap.setMyLocationEnabled(true);
//        mMap.getUiSettings().setMyLocationButtonEnabled(true);

        getMyLocation();
    }

    //  獲取目前位置的座標
    private void getMyLocation()
    {
//        Location location = null;

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
        {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            mLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            locationManager.removeUpdates(locationListener);
        }

        if (mLocation==null && locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
        {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
            mLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            locationManager.removeUpdates(locationListener);
        }

        if (mLocation!=null)
//        {
//            Toast.makeText(activity, "暫時無法取得目前位置", Toast.LENGTH_LONG).show();
//
//            return ;
//        }
//        else
        {
            LatLng y = new LatLng(mLocation.getLatitude(), mLocation.getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(y, 10));
        }

        onResult(mLocation);
    }



}
