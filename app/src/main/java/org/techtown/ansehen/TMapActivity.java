package org.techtown.ansehen;

import android.Manifest;
import android.app.ProgressDialog;
import android.app.Service;
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
import android.os.IBinder;
import android.os.Message;
import android.os.Parcelable;
import android.os.RemoteException;
import android.os.SystemClock;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
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


import junit.framework.Test;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static android.content.ContentValues.TAG;

public class TMapActivity extends AppCompatActivity implements BeaconConsumer {
    public static Context mContext;
    private GpsInfo gps;

    String temp_s="NO";
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
    String password;
    String phonenumber;

    String result_p="NULL";

    String t_name;
    String t_phnoenum;
    String filename;
    CCTVBeaconManager CBM = new CCTVBeaconManager();
    public void changeState(String s_temp){
        final String urlPath_register = "http://13.124.164.203/ChangeState.php";
        URL connectUrl =null;

        FileInputStream fileInputStream =null;
        int serverResponseCode = 0;
        try
        {
            connectUrl=new URL(urlPath_register);
            HttpURLConnection conn = (HttpURLConnection) connectUrl.openConnection();
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("content-type", "application/x-www-form-urlencoded");


            StringBuffer buffer = new StringBuffer();
            buffer.append("state").append("=").append(s_temp).append("&");
            buffer.append("uniqueKey").append("=").append(primaryKey);
            Log.i("beaconTemp,stateTemp",s_temp+","+primaryKey);

            OutputStreamWriter osw = new OutputStreamWriter(conn.getOutputStream());
            PrintWriter wr = new PrintWriter(osw);
            wr.write(buffer.toString());
            wr.flush();

            String serverResponseMessage = conn.getResponseMessage();
            Log.i("BeaconId", "HTTP Response is : "
                    + serverResponseMessage + ": " + serverResponseCode);

            fileInputStream.close();
        } catch (MalformedURLException ex) {
            Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
        } catch (Exception e) {
            Log.d("Test", "exception " + e.getMessage());
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1111){
            if(resultCode==1234) {
                temp_s=data.getStringExtra("return");
                if(temp_s.equals("OK")) {
                    Log.i("test_s","-----------------");
                    new Thread(new Runnable() {

                        public void run() {

                            runOnUiThread(new Runnable() {

                                public void run() {
                                }
                            });
                            changeState("state");
                        }
                    }).start();
                    SystemClock.sleep(1000);
                    mhandler.sendEmptyMessage(0);
                }
            }
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tmap);
        mContext=this;
        ghandler.sendEmptyMessage(0);

        startService(new Intent(this, TestService.class));

        Intent intent = new Intent(this.getIntent());
        primaryKey=intent.getExtras().getString("primarykey");
        password=intent.getExtras().getString("password");
        phonenumber=intent.getExtras().getString("phonenumber");
        t_phnoenum=intent.getExtras().getString("myphonenum");
        t_name=intent.getExtras().getString("name");
        filename=intent.getExtras().getString("filename");
        Log.i("In TMap: phonenum",""+phonenumber);
        Log.e(TAG,"TMAP primaryKey : "+primaryKey);
        new Thread(new Runnable() {

            public void run() {

                runOnUiThread(new Runnable() {

                    public void run() {
                    }
                });
                CBM.beaconTimeCheck();
            }
        }).start();

        Log.i("primaryKey",primaryKey);
        CBM.AddPrimaryKey(primaryKey);
        // 실제로 비콘을 탐지하기 위한 비콘매니저 객체를 초기화
        beaconManager = BeaconManager.getInstanceForApplication(this);
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25"));

        // 비콘 탐지를 시작한다. 실제로는 서비스를 시작하는것.
        beaconManager.bind(this);
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
        mapView.setSKPMapApiKey("9d4ebb05-ddfa-3c1a-bcf8-02c57bce3503"); //0964bcd8-f1f6-325c-9903-0210ac72ef61
        mapView.setLanguage(TMapView.LANGUAGE_KOREAN);

        Button btn = (Button) findViewById(R.id.btn_search);
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
                    handler.sendEmptyMessage(0);
                    mhandler.sendEmptyMessage(0);
                    Log.i("test1111",t_name+","+password+","+t_phnoenum+","+phonenumber+","+filename+","+primaryKey);
                    new Thread(new Runnable() {

                        public void run() {

                            runOnUiThread(new Runnable() {

                                public void run() {
                                    //messageText.setText("uploading started.....");
                                }
                            });
                            HttpClient http = new HttpClient();
                            http.putUserInfo(t_name,password,t_phnoenum,phonenumber,filename,primaryKey);
                        }
                    }).start();
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

    private void setMyLocation(double lat, double lng) {//ic_dialog_map
        Bitmap icon = ((BitmapDrawable) ContextCompat.getDrawable(this, android.R.drawable.ic_menu_mylocation)).getBitmap();
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
    public void dataClear(String temp_key){
        Log.i("end_t start","-----------------------------------------------------------------");
        final String urlPath_register = "http://13.124.164.203/server_clear.php";
        URL connectUrl =null;
        FileInputStream fileInputStream =null;
        int serverResponseCode = 0;
        try
        {
            connectUrl=new URL(urlPath_register);
            HttpURLConnection conn = (HttpURLConnection) connectUrl.openConnection();
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("content-type", "application/x-www-form-urlencoded");


            StringBuffer buffer = new StringBuffer();
            buffer.append("uniqueKey").append("=").append(temp_key);

            OutputStreamWriter osw = new OutputStreamWriter(conn.getOutputStream());
            PrintWriter wr = new PrintWriter(osw);
            wr.write(buffer.toString());
            wr.flush();
            String serverResponseMessage = conn.getResponseMessage();
            Log.i("uploadFile", "HTTP Response is : "
                    + serverResponseMessage + ": " + serverResponseCode);

            fileInputStream.close();
        } catch (MalformedURLException ex) {
            Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
        } catch (Exception e) {
            Log.d("Test", "exception " + e.getMessage());
        }
        Log.i("end_t end","-----------------------------------------------------------------");
    }

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
        ghandler.removeMessages(0);
        mhandler.removeMessages(0);
        handler.removeMessages(0);
        new Thread(new Runnable() {

            public void run() {

                runOnUiThread(new Runnable() {

                    public void run() {
                    }
                });

            }
        }).start();
        Log.i("Destroy","@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
        Thread t_thread = new Thread(){
            public void run(){
                changeState("exit");
                Log.i("test",primaryKey);
                dataClear(primaryKey);
            }
        };
        t_thread.start();
        try{
            t_thread.join();
        }
        catch (InterruptedException e){
            e.printStackTrace();
        }
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
    Handler ghandler = new Handler(){
        public void handleMessage(Message msg){
            /* 현재 보는 방향 */
            mapView.setCompassMode(true);
            /* 현위치 아이콘표시 */
            //mapView.setIconVisibility(true);
            setMyLocation(cacheLocation.getLatitude(), cacheLocation.getLongitude());
            /* 줌레벨 */
            //mapView.setZoomLevel(15);
            mapView.setMapType(TMapView.MAPTYPE_STANDARD);
            mapView.setLanguage(TMapView.LANGUAGE_KOREAN);

            tmapgps = new TMapGpsManager(TMapActivity.this);
            tmapgps.setMinTime(1000);
            tmapgps.setMinDistance(5);
            tmapgps.setProvider(tmapgps.NETWORK_PROVIDER);//연결된 인터넷으로 현 위치를 받습니다.
            tmapgps.OpenGps();
            mapView.setTrackingMode(true);
            mapView.setSightVisible(true);
            ghandler.sendEmptyMessageDelayed(0, 6000);
        }
    };
    Handler mhandler = new Handler() {
        public void handleMessage(Message msg) {

            GetData task = new GetData();
            task.execute("http://13.124.164.203/android_test.php");
            mhandler.sendEmptyMessageDelayed(0, 10000);
        }
    };
    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            dataString="";
            String temp="";
            // 비콘의 아이디와 거리를 측정하여 textView에 넣는다.
            for(Beacon beacon : beaconList){
                int x=0;
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
            }
            handler.sendEmptyMessageDelayed(0, 5000);
        }
    };
    private class GetData extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            //progressDialog = ProgressDialog.show(TMapActivity.this,"Please Wait", null, true, true);
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            //progressDialog.dismiss();
            Log.d(TAG, "response  - " + result);
            //String temp=result.substring(indexOf,1);
            Log.i("P2",""+result);
            int indexOf=result.indexOf("result");
            result_p= result.substring(indexOf+10,indexOf+11);

            //result 반환값 설정 0(진행중) 1(진행완료) 2(CCTV 얼굴 미확인,팝업발생)
            if(result_p.equals("0")||result_p.equals("1")) {
                /*
                Log.i("result 0","진행중");
                TMapPoint test_aa= tmapgps.getLocation();
                Log.i("test_aa",""+test_aa);
                Log.i("e_lat,e_lon",end.getLatitude()+","+end.getLongitude());
                double t_lat=Math.abs((test_aa.getLatitude()-end.getLatitude())*100000d)/100000d;
                double t_lon=Math.abs((test_aa.getLongitude()-end.getLongitude())*100000d)/100000d;
                Log.i("Distence from end_p",""+((t_lat+t_lon*100000d)/100000d));
                */
                Log.i("result 0","진행중");
                gps = new GpsInfo(TMapActivity.this);
                // GPS 사용유무 가져오기
                if (gps.isGetLocation()) {
                    double latitude = (gps.getLatitude()*100000d)/100000d;
                    double longitude = (gps.getLongitude()*100000d)/100000d;
                    Log.i("lat,lon",latitude+","+longitude);
                    Log.i("e_lat,e_lon",end.getLatitude()+","+end.getLongitude());
                    double t_lat=Math.abs((latitude-end.getLatitude())*100000d)/100000d;
                    double t_lon=Math.abs((longitude-end.getLongitude())*100000d)/100000d;
                    Log.i("Distence from end_p",""+((t_lat+t_lon*100000d)/100000d));
                    if((t_lat+t_lon)<0.0004) {
                        ghandler.removeMessages(0);
                        mhandler.removeMessages(0);
                        handler.removeMessages(0);
                        Intent endIntent = new Intent(TMapActivity.this, endActivity.class);
                        Log.i("TMAP_primaryKey",""+primaryKey);
                        endIntent.putExtra("primaryKey", primaryKey);
                        TMapActivity.this.startActivity(endIntent);
                    }
                }else {
                    // GPS 를 사용할수 없으므로
                    gps.showSettingsAlert();
                }
            }
            else if(result_p.equals("2")){
                final Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                mhandler.removeMessages(0);
                Log.i("result 2","CCTV 얼굴 미확인, 팝업 실행");
                vibrator.vibrate(500);
                Intent tmapIntent = new Intent(TMapActivity.this, Pop.class);
                tmapIntent.putExtra("password", password);
                tmapIntent.putExtra("phonenumber", phonenumber);
                TMapActivity.this.startActivityForResult(tmapIntent,1111);
                //웹서버 상태 0으로 변경
            }
            else{
                mhandler.removeMessages(0);
                Log.i("result "+result_p,"CCTV 값 전송 에러, 팝업 실행");
                Intent tmapIntent = new Intent(TMapActivity.this, Pop.class);
                tmapIntent.putExtra("password", password);
                tmapIntent.putExtra("phonenumber", phonenumber);
                TMapActivity.this.startActivityForResult(tmapIntent,1111);
            }
        }


        @Override
        protected String doInBackground(String... params) {
            String serverURL = params[0];
            String uniqueKey = primaryKey;
            String postParameters = "unique_key=" + uniqueKey;
            try {

                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoInput(true);
                httpURLConnection.connect();

                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(postParameters.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();

                int responseStatusCode = httpURLConnection.getResponseCode();
                Log.d(TAG, "response code - " + responseStatusCode);

                InputStream inputStream;
                if(responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                }
                else{
                    inputStream = httpURLConnection.getErrorStream();
                }


                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line;

                while((line = bufferedReader.readLine()) != null){
                    sb.append(line);
                }


                bufferedReader.close();


                return sb.toString().trim();


            } catch (Exception e) {

                Log.d(TAG, "InsertData: Error ", e);
                errorString = e.toString();

                return null;
            }
        }
    }
}



