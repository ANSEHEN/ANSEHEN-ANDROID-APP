package org.techtown.ansehen;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.estimote.sdk.SystemRequirementsChecker;
import com.skp.Tmap.TMapData;
import com.skp.Tmap.TMapMarkerItem;
import com.skp.Tmap.TMapPOIItem;
import com.skp.Tmap.TMapPoint;
import com.skp.Tmap.TMapPolyLine;
import com.skp.Tmap.TMapView;
import com.skp.Tmap.TMapGpsManager;


import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.techtown.ansehen.CCTVBeaconManager;

import static android.content.ContentValues.TAG;

public class TMapActivity extends AppCompatActivity implements BeaconConsumer {

    TMapView mapView=null;

    LocationManager mLM;
    String mProvider = LocationManager.NETWORK_PROVIDER;

    EditText keywordView;
    ListView listView;
    ArrayAdapter<POI> mAdapter;

    TMapPoint start, end;
    RadioGroup typeView;
    private TMapGpsManager tmapgps = null;

    private BeaconManager beaconManager;
    // 감지된 비콘들을 임시로 담을 리스트
    private List<Beacon> beaconList = new ArrayList<>();
    String dataString="\0";

    String primaryKey;
    CCTVBeaconManager CBM = new CCTVBeaconManager();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tmap);
        Intent intent = new Intent(this.getIntent());
        primaryKey=intent.getExtras().getString("primarykey");
        Log.e(TAG,"TMAP primaryKey : "+primaryKey);
        //Intent Cameraintent=new Intent(this.getIntent());

        //
        Log.i("Point 1.","----------------------------------------------------------#########################");
        new Thread(new Runnable() {

            public void run() {

                runOnUiThread(new Runnable() {

                    public void run() {
                        //CBM.beaconTimeCheck();
                    }

                });
                CBM.beaconTimeCheck();
            }
        }).start();
        //CBM.beaconTimeCheck();
        Log.i("primaryKey",primaryKey);
        CBM.AddPrimaryKey(primaryKey);
        Log.i("Point 2.","----------------------------------------------------------#########################");
        // 실제로 비콘을 탐지하기 위한 비콘매니저 객체를 초기화
        beaconManager = BeaconManager.getInstanceForApplication(this);
        //textView = (TextView)findViewById(R.id.Textview);

        // 여기가 중요한데, 기기에 따라서 setBeaconLayout 안의 내용을 바꿔줘야 하는듯 싶다.
        // 필자의 경우에는 아래처럼 하니 잘 동작했음.
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25"));

        // 비콘 탐지를 시작한다. 실제로는 서비스를 시작하는것.
        beaconManager.bind(this);
        handler.sendEmptyMessage(0);

        /*
        new Thread(new Runnable() {

            public void run() {

                runOnUiThread(new Runnable() {

                    public void run() {
                        Log.i("Test","---------------------------------------");

                        new Thread(new Runnable() {

                            public void run() {

                                runOnUiThread(new Runnable() {

                                    public void run() {
                                        BeaconThread beaconThread = new BeaconThread();
                                        //beaconThread.testBeacon();
                                    }
                                });

                                //HttpClient httpClient = new HttpClient();
                                //httpClient.HttpFileUpload(""+mImageCaptureUri.getPath());

                            }
                        }).start();
                        Log.i("Test end","-----------------------------------");
                    }
                });

                //HttpClient httpClient = new HttpClient();
                //httpClient.HttpFileUpload(""+mImageCaptureUri.getPath());

            }
        }).start();

        Intent beaconIntent = new Intent(CameraActivity.this, BeaconThread.class);
        startActivity(beaconIntent);
        */

        typeView = (RadioGroup) findViewById(R.id.group_type);
        keywordView = (EditText) findViewById(R.id.edit_keyword);
        listView = (ListView) findViewById(R.id.listView);
        mAdapter = new ArrayAdapter<POI>(this, android.R.layout.simple_list_item_1);
        listView.setAdapter(mAdapter);

        mLM = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mapView = (TMapView) findViewById(R.id.map_view);
        mapView.setOnApiKeyListener(new TMapView.OnApiKeyListenerCallback() {
            @Override
            public void SKPMapApikeySucceed() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setupMap();
                    }
                });
            }

            @Override
            public void SKPMapApikeyFailed(String s) {

            }
        });
        mapView.setSKPMapApiKey("0964bcd8-f1f6-325c-9903-0210ac72ef61");
        mapView.setLanguage(TMapView.LANGUAGE_KOREAN);

        Button btn = (Button) findViewById(R.id.btn_add_marker);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               /* 현재 보는 방향 */
                mapView.setCompassMode(true);
                /* 현위치 아이콘표시 */
                mapView.setIconVisibility(true);
                /* 줌레벨 */
                mapView.setZoomLevel(15);
                mapView.setMapType(TMapView.MAPTYPE_STANDARD);
                mapView.setLanguage(TMapView.LANGUAGE_KOREAN);

                tmapgps = new TMapGpsManager(TMapActivity.this);
                tmapgps.setMinTime(1000);
                tmapgps.setMinDistance(5);
                tmapgps.setProvider(tmapgps.NETWORK_PROVIDER);//연결된 인터넷으로 현 위치를 받습니다.
                tmapgps.OpenGps();
                mapView.setTrackingMode(true);
                mapView.setSightVisible(true);

                //                  TMapPoint point = mapView.getCenterPoint();
                //          addMarker(point.getLatitude(), point.getLongitude(), "My Marker");
            }
        });

        btn = (Button) findViewById(R.id.btn_search);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchPOI();
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                POI poi = (POI) listView.getItemAtPosition(position);
                moveMap(poi.item.getPOIPoint().getLatitude(), poi.item.getPOIPoint().getLongitude());
            }
        });

        btn = (Button) findViewById(R.id.btn_route);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (start != null && end != null) {
                    searchRoute(start, end);
                    Log.i("******Start",""+start.toString());
                    Log.i("******End",""+end.toString());
                    start = end = null;
                } else {
                    Toast.makeText(TMapActivity.this, "start or end is null", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void searchRoute(TMapPoint start, final TMapPoint end) {
        TMapData data = new TMapData();
        data.findPathData(start, end, new TMapData.FindPathDataListenerCallback() {
            @Override
            public void onFindPathData(final TMapPolyLine path) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        path.setLineWidth(5);
                        path.setLineColor(Color.RED);
                        mapView.addTMapPath(path);
                        Bitmap s = ((BitmapDrawable) ContextCompat.getDrawable(TMapActivity.this, android.R.drawable.ic_input_delete)).getBitmap();
                        Bitmap e = ((BitmapDrawable) ContextCompat.getDrawable(TMapActivity.this, android.R.drawable.ic_input_get)).getBitmap();
                        mapView.setTMapPathIcon(s, e);
                        final MapPath mapManager=new MapPath();
                        Log.i("primaryKey",""+primaryKey);
                        mapManager.addPrimaryKey(primaryKey);
                        mapManager.addPoint(path.getLinePoint());

                        mapManager.addSizeNum();
                        mapManager.addEndPoint(end);
                        mapManager.setAllPath();
                        new Thread(new Runnable() {

                            public void run() {

                                runOnUiThread(new Runnable() {

                                    public void run() {
                                    }
                                });
                                mapManager.allPathTransport();
                            }
                        }).start();
                    }
                });
            }
        });
    }

    private void searchPOI() {
        TMapData data = new TMapData();
        String keyword = keywordView.getText().toString();
        if (!TextUtils.isEmpty(keyword)) {
            data.findAllPOI(keyword, new TMapData.FindAllPOIListenerCallback() {
                @Override
                public void onFindAllPOI(final ArrayList<TMapPOIItem> arrayList) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mapView.removeAllMarkerItem();
                            mAdapter.clear();

                            for (TMapPOIItem poi : arrayList) {
                                addMarker(poi);
                                mAdapter.add(new POI(poi));
                            }

                            if (arrayList.size() > 0) {
                                TMapPOIItem poi = arrayList.get(0);
                                moveMap(poi.getPOIPoint().getLatitude(), poi.getPOIPoint().getLongitude());
                            }
                        }
                    });
                }
            });
        }
    }

    public void addMarker(TMapPOIItem poi) {
        TMapMarkerItem item = new TMapMarkerItem();
        item.setTMapPoint(poi.getPOIPoint());
        Bitmap icon = ((BitmapDrawable) ContextCompat.getDrawable(this, android.R.drawable.ic_input_add)).getBitmap();
        item.setIcon(icon);
        item.setPosition(0.5f, 1);
        item.setCalloutTitle(poi.getPOIName());
        item.setCalloutSubTitle(poi.getPOIContent());
        Bitmap left = ((BitmapDrawable) ContextCompat.getDrawable(this, android.R.drawable.ic_dialog_alert)).getBitmap();
        item.setCalloutLeftImage(left);
        Bitmap right = ((BitmapDrawable) ContextCompat.getDrawable(this, android.R.drawable.ic_input_get)).getBitmap();
        item.setCalloutRightButtonImage(right);
        item.setCanShowCallout(true);
        mapView.addMarkerItem(poi.getPOIID(), item);
    }

    private void addMarker(double lat, double lng, String title) {
        TMapMarkerItem item = new TMapMarkerItem();
        TMapPoint point = new TMapPoint(lat, lng);
        item.setTMapPoint(point);
        Bitmap icon = ((BitmapDrawable) ContextCompat.getDrawable(this, android.R.drawable.ic_input_add)).getBitmap();
        item.setIcon(icon);
        item.setPosition(0.5f, 1);
        item.setCalloutTitle(title);
        item.setCalloutSubTitle("s  ub " + title);
        Bitmap left = ((BitmapDrawable) ContextCompat.getDrawable(this, android.R.drawable.ic_dialog_alert)).getBitmap();
        item.setCalloutLeftImage(left);
        Bitmap right = ((BitmapDrawable) ContextCompat.getDrawable(this, android.R.drawable.ic_input_get)).getBitmap();
        item.setCalloutRightButtonImage(right);
        item.setCanShowCallout(true);
        mapView.addMarkerItem("m" + id, item);
        id++;
    }

    int id = 0;

    boolean isInitialized = false;

    private void setupMap() {
        isInitialized = true;
        mapView.setMapType(TMapView.MAPTYPE_STANDARD);
        //        mapView.setSightVisible(true);
        //        mapView.setCompassMode(true);
        //        mapView.setTrafficInfo(true);
        //        mapView.setTrackingMode(true);
        if (cacheLocation != null) {
            moveMap(cacheLocation.getLatitude(), cacheLocation.getLongitude());
            setMyLocation(cacheLocation.getLatitude(), cacheLocation.getLongitude());
        }
        mapView.setOnCalloutRightButtonClickListener(new TMapView.OnCalloutRightButtonClickCallback() {
            @Override
            public void onCalloutRightButton(TMapMarkerItem tMapMarkerItem) {
                String message = null;
                switch (typeView.getCheckedRadioButtonId()) {
                    case R.id.radio_start:
                        start = tMapMarkerItem.getTMapPoint();
                        message = "start";
                        break;
                    case R.id.radio_end:
                        end = tMapMarkerItem.getTMapPoint();
                        message = "end";
                        break;
                }
                Toast.makeText(TMapActivity.this, message + " setting", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location location = mLM.getLastKnownLocation(mProvider);
        if (location != null) {
            mListener.onLocationChanged(location);
        }
        mLM.requestSingleUpdate(mProvider, mListener, null);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mLM.removeUpdates(mListener);
    }

    Location cacheLocation = null;

    private void moveMap(double lat, double lng) {
        mapView.setCenterPoint(lng, lat);
    }

    private void setMyLocation(double lat, double lng) {
        Bitmap icon = ((BitmapDrawable) ContextCompat.getDrawable(this, android.R.drawable.ic_dialog_map)).getBitmap();
        mapView.setIcon(icon);
        mapView.setLocationPoint(lng, lat);
        mapView.setIconVisibility(true);
    }

    LocationListener mListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            if (isInitialized) {
                moveMap(location.getLatitude(), location.getLongitude());
                setMyLocation(location.getLatitude(), location.getLongitude());
            } else {
                cacheLocation = location;
            }
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    };

    //
    @Override
    protected void onResume(){
        super.onResume();
        SystemRequirementsChecker.checkWithDefaultDialogs(this);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        beaconManager.unbind(this);
    }

    @Override
    public void onBeaconServiceConnect() {
        beaconManager.setRangeNotifier(new RangeNotifier() {
            @Override
            // 비콘이 감지되면 해당 함수가 호출된다. Collection<Beacon> beacons에는 감지된 비콘의 리스트가,
            // region에는 비콘들에 대응하는 Region 객체가 들어온다.
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
                if (beacons.size() > 0) {
                    beaconList.clear();
                    for (Beacon beacon : beacons) {
                        beaconList.add(beacon);
                    }
                }
            }

        });

        try {
            beaconManager.startRangingBeaconsInRegion(new Region("myRangingUniqueId", null, null, null));
        } catch (RemoteException e) {   }
    }

    // 버튼이 클릭되면 textView 에 비콘들의 정보를 뿌린다.
    /*
    public void OnButtonClicked(View view){
        // 아래에 있는 handleMessage를 부르는 함수. 맨 처음에는 0초간격이지만 한번 호출되고 나면
        // 1초마다 불러온다.
        handler.sendEmptyMessage(0);
    }
    */
    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            dataString="";
            String temp="";
            //textView.setText("");
            //Log.i("Handler Start","@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
            // 비콘의 아이디와 거리를 측정하여 textView에 넣는다.
            for(Beacon beacon : beaconList){
                int x=0;
                //Log.i("beaconList","--------------------------------------------------------------------");
                dataString.concat("ID : " + beacon.getId2() + " / " + "Distance : " + Double.parseDouble(String.format("%.3f", beacon.getDistance())) + "m\n");
                Log.i("data: ",beacon.getId2()+"/"+"Distance: "+ Double.parseDouble(String.format("%.3f", beacon.getDistance())));

                temp=(""+beacon.getId2());
                Log.i("in for temp:",temp);
                x=CBM.compareCctvId(temp);

                final String tempp=temp;
                if(x==1){
                    new Thread(new Runnable() {

                        public void run() {

                            runOnUiThread(new Runnable() {

                                public void run() {
                                }
                            });
                            CBM.transportCctv(tempp);

                        }
                    }).start();
                }
                //textView.append("ID : " + beacon.getId2() + " / " + "Distance : " + Double.parseDouble(String.format("%.3f", beacon.getDistance())) + "m\n");
            }
            //Log.i("Handler End","@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
            // 자기 자신을 1초마다 호출
            handler.sendEmptyMessageDelayed(0, 5000);
        }
    };
}



