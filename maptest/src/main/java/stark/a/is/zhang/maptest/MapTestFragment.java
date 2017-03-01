package stark.a.is.zhang.maptest;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.model.LatLng;

import java.util.ArrayList;
import java.util.List;

import static android.support.v4.content.ContextCompat.checkSelfPermission;

public class MapTestFragment extends Fragment{
    MapView mMapView = null;
    BaiduMap mBaiduMap = null;

    private LocationClient mLocationClient = null;
    private BDLocationListener mListener = new MyLocationListener();

    private static final String HAS_SHOWN_TRAFFIC = "HasShownTraffic";
    private static final String HAS_SHOWN_HEAT = "HasShownHeat";

    private boolean mHasShowTraffic;
    private boolean mHasShowHeat;

    public static MapTestFragment newInstance() {
        return new MapTestFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_map_test, container, false);

        final Button showLocation = (Button)v.findViewById(R.id.show_location);
        showLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLocation();
            }
        });

        final Button clearLocation = (Button)v.findViewById(R.id.clear_location);
        clearLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearLocation();
            }
        });

        mMapView = (MapView) v.findViewById(R.id.bmapView);

        mBaiduMap = mMapView.getMap();
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);

        mBaiduMap.setMyLocationEnabled(true);

        mLocationClient = new LocationClient(getActivity().getApplicationContext());
        mLocationClient.registerLocationListener(mListener);
        initLocationClient();

        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(getActivity());

        mHasShowTraffic = sharedPreferences.getBoolean(HAS_SHOWN_TRAFFIC, false);
        mHasShowHeat = sharedPreferences.getBoolean(HAS_SHOWN_HEAT, false);

        return v;
    }

    private void initLocationClient() {
        LocationClientOption option = new LocationClientOption();

        //可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);

        //可选，默认gcj02，设置返回的定位结果坐标系
        option.setCoorType("bd0911");

        //可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setScanSpan(10000);

        //可选，设置是否需要地址信息，默认不需要
        option.setIsNeedAddress(true);

        //可选，默认false,设置是否使用gps
        option.setOpenGps(true);

        //可选，默认false，设置是否当GPS有效时按照1次/s,频率输出GPS结果
        option.setLocationNotify(true);

        //可选，默认false，设置是否需要位置语义化结果
        //可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationDescribe(true);

        //可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.setIsNeedLocationPoiList(true);

        //可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        option.setIgnoreKillProcess(false);

        //可选，默认false，设置是否收集CRASH信息，默认收集
        option.SetIgnoreCacheException(false);

        //可选，默认false，设置是否需要过滤GPS仿真结果，默认需要
        option.setEnableSimulateGps(false);

        mLocationClient.setLocOption(option);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.map_test_menu, menu);

        MenuItem trafficItem = menu.findItem(R.id.show_traffic);
        if (mHasShowTraffic) {
            trafficItem.setTitle(R.string.menu_hide_traffic);
        } else {
            trafficItem.setTitle(R.string.menu_show_traffic);
        }

        MenuItem heatItem = menu.findItem(R.id.show_heat);
        if (mHasShowHeat) {
            heatItem.setTitle(R.string.menu_hide_heat);
        } else {
            heatItem.setTitle(R.string.menu_show_heat);
        }

        mBaiduMap.setTrafficEnabled(mHasShowTraffic);
        mBaiduMap.setBaiduHeatMapEnabled(mHasShowHeat);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.show_traffic:
                mHasShowTraffic = !mHasShowTraffic;
                mBaiduMap.setTrafficEnabled(mHasShowTraffic);

                PreferenceManager.getDefaultSharedPreferences(getActivity())
                        .edit().putBoolean(HAS_SHOWN_TRAFFIC, mHasShowTraffic).apply();

                getActivity().invalidateOptionsMenu();

                break;

            case R.id.show_heat:
                mHasShowHeat = !mHasShowHeat;
                mBaiduMap.setBaiduHeatMapEnabled(mHasShowHeat);

                PreferenceManager.getDefaultSharedPreferences(getActivity())
                        .edit().putBoolean(HAS_SHOWN_HEAT, mHasShowHeat).apply();

                getActivity().invalidateOptionsMenu();

                break;

            default:
                return super.onOptionsItemSelected(item);
        }

        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    private void showLocation() {
        if (hasAllThePermissions()) {
            mLocationClient.start();
        }
    }

    private static final int REQUEST_MULTI_PERMISSIONS = 0;
    private boolean hasAllThePermissions() {
        List<String> permissionNeeded = new ArrayList<>();

        checkPermission(permissionNeeded, Manifest.permission.READ_PHONE_STATE);
        checkPermission(permissionNeeded, Manifest.permission.ACCESS_COARSE_LOCATION);
        checkPermission(permissionNeeded, Manifest.permission.ACCESS_FINE_LOCATION);
        checkPermission(permissionNeeded, Manifest.permission.READ_EXTERNAL_STORAGE);
        checkPermission(permissionNeeded, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permissionNeeded.size() == 0) {
            return true;
        } else {
            requestPermissions(permissionNeeded.toArray(new String[permissionNeeded.size()]),
                    REQUEST_MULTI_PERMISSIONS);

            return false;
        }
    }

    private void checkPermission(List<String> needed, String permission) {
        if (checkSelfPermission(getContext(), permission)
                != PackageManager.PERMISSION_GRANTED) {
            needed.add(permission);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_MULTI_PERMISSIONS:
                for (int i = 0; i < permissions.length; ++i) {
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    mLocationClient.start();
                }
                break;
        }
    }

    private Marker mMarker;
    private class MyLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            Log.d("ZJTest", "onReceiveLocation: " + bdLocation.getLocType());

            LatLng point = new LatLng(bdLocation.getLatitude(),
                    bdLocation.getLongitude());

            MapStatus mapStatus = new MapStatus.Builder()
                    .target(point).zoom(16).build();
            MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mapStatus);
            mBaiduMap.setMapStatus(mapStatusUpdate);

            Bitmap origin = BitmapFactory.decodeResource(
                    getResources(), R.drawable.icon_marker);

            Matrix matrix = new Matrix();
            matrix.postScale((float) 0.1, (float) 0.1);
            Bitmap resize = Bitmap.createBitmap(origin, 0, 0,
                    origin.getWidth(), origin.getHeight(), matrix, true);

            //构建Marker图标
            BitmapDescriptor bitmap = BitmapDescriptorFactory
                    .fromBitmap(resize);

            //构建MarkerOption，用于在地图上添加Marker
            MarkerOptions option = new MarkerOptions()
                    .position(point)
                    .icon(bitmap);

            option.animateType(MarkerOptions.MarkerAnimateType.grow);

            //在地图上添加Marker，并显示
            if (mMarker != null) {
                mMarker.remove();
            }

            mMarker = (Marker)(mBaiduMap.addOverlay(option));
        }

        @Override
        public void onConnectHotSpotMessage(String s, int i) {
        }
    }

    private void clearLocation() {
        if (mMarker != null) {
            mLocationClient.stop();
            mMarker.remove();
            mMarker = null;
            mMapView.invalidate();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mLocationClient.stop();
        mBaiduMap.setMyLocationEnabled(false);

        mMapView.onDestroy();
    }
}
