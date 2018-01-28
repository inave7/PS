package com.belaku.naveenprakash.streetMap;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.identity.intents.Address;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.vision.barcode.Barcode;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.Series;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import static com.belaku.naveenprakash.streetMap.MainActivity.mLatLng;


public class NavigationActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = "NAct";
    private TextView TxFrom, TxTo;
    private LatLng fromLatLng, toLatLng;
    private float Distance;
    private SupportMapFragment mSupportMapFragment;
    private GoogleMap MyGmap;
    private MarkerOptions markerOptionsFrom, markerOptionsTo;
    private Marker markerFrom, markerTo;
    private LatLng mFromLatLng, mToLatLng;
    private int called = 0;
    private GraphView graph;
    private LineGraphSeries<DataPoint> series;
    private ArrayList<Integer> x, y;
    private Handler handler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        TxFrom = (TextView) findViewById(R.id.tx_from);
        TxTo = (TextView) findViewById(R.id.tx_to);

        graph = (GraphView) findViewById(R.id.graph);

        x = new ArrayList<>();
        y = new ArrayList<>();


        mSupportMapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);


        Intent intent = getIntent();

        if (intent != null) {
            if (intent.getExtras().get("FromLoc") != null) {
                Toast.makeText(getApplicationContext(), "NA-from " + intent.getExtras().get("FromLoc"), Toast.LENGTH_SHORT).show();
                TxFrom.setText(intent.getExtras().get("FromLoc").toString());
                fromLatLng = getLocationFromAddress(getApplicationContext(), intent.getExtras().get("FromLoc").toString());
            }
            if (intent.getExtras().get("ToLoc") != null) {
                TxTo.setText(intent.getExtras().get("ToLoc").toString());
                toLatLng = getLocationFromAddress(getApplicationContext(), intent.getExtras().get("ToLoc").toString());
                Toast.makeText(getApplicationContext(), "NA-to " + intent.getExtras().get("ToLoc"), Toast.LENGTH_SHORT).show();
            }

            if (fromLatLng != null && toLatLng != null) {
                Distance = distanceFrom(fromLatLng.latitude, fromLatLng.longitude, toLatLng.latitude, toLatLng.longitude);
                mFromLatLng = fromLatLng;
                mToLatLng = toLatLng;
            }
            Toast.makeText(getApplicationContext(), "Distance from " + String.valueOf(fromLatLng.latitude) + "," + String.valueOf(fromLatLng.longitude) + "-"
                    + String.valueOf(toLatLng.latitude) + "," + String.valueOf(toLatLng.longitude) +
                    "is : " + String.valueOf(Distance), Toast.LENGTH_LONG).show();

            getTime(fromLatLng.latitude, fromLatLng.longitude, toLatLng.latitude, toLatLng.longitude);


            GraphView graph = (GraphView) findViewById(R.id.graph);
            /*LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(new DataPoint[] {
                    new DataPoint(x.get(0), y.get(0)),
                    new DataPoint(x.get(1), y.get(1)),
                    new DataPoint(x.get(2), y.get(2)),
                    new DataPoint(x.get(3), y.get(3)),
                    new DataPoint(x.get(4), y.get(4)),

                    new DataPoint(x.get(5), y.get(5)),
                    new DataPoint(x.get(6), y.get(6)),
                    new DataPoint(x.get(7), y.get(7)),
                    new DataPoint(x.get(8), y.get(8)),
                    new DataPoint(x.get(9), y.get(9)),

                    new DataPoint(x.get(10), y.get(10)),
                    new DataPoint(x.get(11), y.get(11)),
                    new DataPoint(x.get(12), y.get(12)),
                    new DataPoint(x.get(13), y.get(13)),
                    new DataPoint(x.get(14), y.get(14)),

                    new DataPoint(x.get(15), y.get(15)),
                    new DataPoint(x.get(16), y.get(16)),
                    new DataPoint(x.get(17), y.get(17)),
                    new DataPoint(x.get(18), y.get(18)),
                    new DataPoint(x.get(19), y.get(19)),

                    new DataPoint(x.get(20), y.get(20)),
                    new DataPoint(x.get(21), y.get(21)),
                    new DataPoint(x.get(22), y.get(22)),
                    new DataPoint(x.get(23), y.get(23))

            });
            graph.addSeries(series);*/

            // http://maps.google.com/maps/api/directions/json?origin=13.0543331,77.4990821&destination=13.0279661,77.54091559999999&sensor=false&units=metric

            // https://maps.googleapis.com/maps/api/directions/json?
            // origin=13.0543331,77.4990821
            // &destination=13.0279661,77.54091559999999
            // &departure_time=1541202457
            // &traffic_model=best_guess
            // &key=AIzaSyB4hJ-5vcOeTOsAiK8CpQ5uPD4D7LPArIE


        }
    }


    private Date getDate() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        //    makeToast(yesterday().toString());
        return yesterday();
    }

    private Date yesterday() {
        final Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, +7);
        return cal.getTime();
    }

    private void getTime(double latitude1, double longitude1, double latitude2, double longitude2) {

        ArrayList<Long> HoursInMillis = new ArrayList<>();
        getHoursInMillis(HoursInMillis);


        // https://maps.googleapis.com/maps/api/directions/json?origin=13.0543331,77.4990821&destination=13.0279661,77.54091559999999&departure_time=1541202457&traffic_model=best_guess&mode=driving&key=AIzaSyB4hJ-5vcOeTOsAiK8CpQ5uPD4D7LPArIE
        // mode: driving - &mode=driving

        for (int i = 0; i < 86400; i += 3600) {
            //      makeToast("TIME - " +String.valueOf((getDate().getTime() + (i * 3600 ) / 1000l)));
            new JsonTask().execute("https://maps.googleapis.com/maps/api/directions/json?origin=" + latitude1 + "," + longitude1
                    + "&destination=" + latitude2 + "," + longitude2 + "&departure_time=" + (getDate().getTime() + (i * 3600) / 1000l) // System.currentTimeMillis() / 1000l   // ((getDate().getTime() + i) / 1000l)
                    + "&traffic_model=best_guess&key=AIzaSyB4hJ-5vcOeTOsAiK8CpQ5uPD4D7LPArIE");


        }

        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //Do something after 100ms
                LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(new DataPoint[]{
                        new DataPoint(x.get(0), y.get(0)),
                        new DataPoint(x.get(1), y.get(1)),
                        new DataPoint(x.get(2), y.get(2)),
                        new DataPoint(x.get(3), y.get(3)),
                        new DataPoint(x.get(4), y.get(4)),

                        new DataPoint(x.get(5), y.get(5)),
                        new DataPoint(x.get(6), y.get(6)),
                        new DataPoint(x.get(7), y.get(7)),
                        new DataPoint(x.get(8), y.get(8)),
                        new DataPoint(x.get(9), y.get(9)),

                        new DataPoint(x.get(10), y.get(10)),
                        new DataPoint(x.get(11), y.get(11)),
                        new DataPoint(x.get(12), y.get(12)),
                        new DataPoint(x.get(13), y.get(13)),
                        new DataPoint(x.get(14), y.get(14)),

                        new DataPoint(x.get(15), y.get(15)),
                        new DataPoint(x.get(16), y.get(16)),
                        new DataPoint(x.get(17), y.get(17)),
                        new DataPoint(x.get(18), y.get(18)),
                        new DataPoint(x.get(19), y.get(19)),

                        new DataPoint(x.get(20), y.get(20)),
                        new DataPoint(x.get(21), y.get(21)),
                        new DataPoint(x.get(22), y.get(22)),
                        new DataPoint(x.get(23), y.get(23))

                });
                graph.addSeries(series);
            }
        }, 30000);


    }

    public void makeToast(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
    }


    private void getHoursInMillis(ArrayList<Long> hrsInms) {

        for (int i = 0; i < 24; i++) {
            hrsInms.add(Long.valueOf(i * 3600000));
        }
    }

    public LatLng getLocationFromAddress(Context context, String strAddress) {

        try {
            Geocoder geoCoder = new Geocoder(NavigationActivity.this);
            ;
            geoCoder.getFromLocationName(strAddress, 1);
            if (geoCoder.getFromLocationName(strAddress, 1) != null && geoCoder.getFromLocationName(strAddress, 1).size() > 0) {
                double lat = geoCoder.getFromLocationName(strAddress, 1).get(0).getLatitude();
                double lng = geoCoder.getFromLocationName(strAddress, 1).get(0).getLongitude();
                return new LatLng(lat, lng);
            } else return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }


    }


    public float distanceFrom(double lat1, double lng1, double lat2, double lng2) {
        double earthRadius = 3958.75;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.sin(dLng / 2) * Math.sin(dLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double dist = earthRadius * c;
        int meterConversion = 1609;

        mSupportMapFragment.getMapAsync(NavigationActivity.this);
        return new Float(dist * meterConversion).floatValue();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        MyGmap = googleMap;

        markerOptionsFrom = new MarkerOptions()
                .position(mFromLatLng).title("From").icon(BitmapDescriptorFactory.fromResource(R.drawable.marker));
        markerFrom = googleMap.addMarker(markerOptionsFrom);
        markerFrom.showInfoWindow();

        markerOptionsTo = new MarkerOptions()
                .position(mToLatLng).title("To").icon(BitmapDescriptorFactory.fromResource(R.drawable.marker));
        markerTo = googleMap.addMarker(markerOptionsTo);
        markerTo.showInfoWindow();

        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mFromLatLng, 7));
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mToLatLng, 7));
    }


    private class JsonTask extends AsyncTask<String, String, String> {

        private ProgressDialog pd;

        protected void onPreExecute() {
            super.onPreExecute();

            pd = new ProgressDialog(NavigationActivity.this);
            pd.setMessage("Please wait");
            pd.setCancelable(false);
            pd.show();
        }

        protected String doInBackground(String... params) {


            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(params[0]);
                Log.d("URL", url.toString());
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();


                InputStream stream = connection.getInputStream();

                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();
                String line = "";

                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                    Log.d("Response: ", "> " + line);   //here u ll get whole response...... :-)

                }

                return buffer.toString();


            } catch (MalformedURLException e) {
                e.printStackTrace();
                toastExcp(e.toString());
            } catch (IOException e) {
                e.printStackTrace();
                toastExcp(e.toString());
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        private void toastExcp(String string) {
//            Toast.makeText(getApplicationContext(), "toastingExcp : \n "+ string, Toast.LENGTH_SHORT).show();
            Log.d("EXCP", string);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParseJson(result);


            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    //Do something after 100ms
                    if (pd.isShowing()) {
                        pd.dismiss();
                    }
                }
            }, 60000);
        }


        private void ParseJson(String result) {
            //     Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
            Log.d("JSONresponse", result);

            try {
                // Getting JSON Array

                JSONObject json = new JSONObject(result);

                JSONArray routes = json.getJSONArray("routes");

                if (routes.length() > 0) {
                    JSONObject j = routes.getJSONObject(0); //.getString("legs");
                    JSONArray c = j.getJSONArray("legs");
                    String duration = c.getJSONObject(0).getString("duration_in_traffic");
                    String time = (new JSONObject(duration)).getString("text");

                    called++;
               //     makeToast(called + " - hour of the Day" + "\n  Time needed - " + time);
                    Log.d("SMACKDOWN", called + " - hour of the Day" + "\n  Time needed - " + time);

                    x.add(called);
                    y.add(Integer.valueOf(time.replaceAll("[^0-9]", "")));


                    //eeeee

                } else {
                    makeToast("No routes fetched ");
                    pd.dismiss();
                }
                /*makeToast(routes.toString());
                makeToast(j.toString());
                makeToast(c.toString());

                makeToast("ACTUAL - " + duration);*/


            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "duration_in_traffic EXCP- " + e, Toast.LENGTH_LONG).show();
            }
        }


    }
}
