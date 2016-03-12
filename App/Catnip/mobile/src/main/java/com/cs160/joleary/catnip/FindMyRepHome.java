package com.cs160.joleary.catnip;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.app.Activity;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.core.services.StatusesService;
import com.twitter.sdk.android.tweetui.CompactTweetView;
import com.twitter.sdk.android.tweetui.TweetUtils;
import com.twitter.sdk.android.tweetui.TweetView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class FindMyRepHome extends Activity implements LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    Button button;
    EditText edit;
    Location mLastLocation;
    String mLatitudeText;
    String mLongitudeText;

    private GoogleApiClient mApiClient;
    private static final String START_ACTIVITY = "/start_activity";
    private static final String WEAR_MESSAGE_PATH = "/message";

    private Button myLocation;
    private TextView myAddress;

    protected LocationManager locationManager;
    protected LocationListener locationListener;
    protected Context context;
    TextView txtLat;
    String lat;
    String provider;
    protected Double latitude, longitude;
    protected boolean gps_enabled, network_enabled;

    private TwitterLoginButton loginButton;

    private JSONArray results;
    private ProgressDialog pDialog;
    private String urlJsonObj2;
    private String county;
    private String zipcode;
    private String toSend;

    ArrayList<String> candidateNames = new ArrayList<>();
    ArrayList<String> candidateParties = new ArrayList<>();

    private static String TAG = LocationActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        final LinearLayout myLayout = (LinearLayout) findViewById(R.id.linear);


        loginButton = (TwitterLoginButton) findViewById(R.id.twitter_login_button);
        loginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                // The TwitterSession is also available through:
                // Twitter.getInstance().core.getSessionManager().getActiveSession()
                TwitterSession session = result.data;
                // TODO: Remove toast and use the TwitterSession's userID
                // with your app's user model
                String msg = "@" + session.getUserName() + " logged in! (#" + session.getUserId() + ")";
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
            }

            @Override
            public void failure(TwitterException exception) {
                Log.d("TwitterKit", "Login with Twitter failure", exception);
            }
        });




//        // TODO: Use a more specific parent
//        //final ViewGroup parentView = (ViewGroup) getWindow().getDecorView().getRootView();
//        // TODO: Base this Tweet ID on some data from elsewhere in your app
//        long tweetId = 631879971628183552L;
//        TweetUtils.loadTweet(tweetId, new Callback<Tweet>() {
//            @Override
//            public void success(Result<Tweet> result) {
//                CompactTweetView tweetView = new CompactTweetView(FindMyRepHome.this, result.data);
//                myLayout.addView(tweetView);
//            }
//
//            @Override
//            public void failure(TwitterException exception) {
//                Log.d("TwitterKit", "Load Tweet failure", exception);
//            }
//        });




        edit = (EditText) findViewById(R.id.editText);
        addListenerOnButton();
        addListenerOnButton2();
        initGoogleApiClient();

        txtLat = (TextView) findViewById(R.id.locationText);


//        TwitterApiClient twitterApiClient = TwitterCore.getInstance().getApiClient();
//        StatusesService statusesService = twitterApiClient.getStatusesService();
//        statusesService.show(524971209851543553L, null, null, null, new Callback<Tweet>() {
//            @Override
//            public void success(Result<Tweet> result) {
//                //Do something with result, which provides a Tweet inside of result.data
//                Tweet tweet = result.data;
//                txtLat.setText(tweet.text);
//            }
//
//            public void failure(TwitterException exception) {
//                //Do something on failure
//            }
//        });



        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);

//        myLocation= (Button) findViewById(R.id.location);
//        myAddress = (TextView)findViewById(R.id.address);
//
//        myLocation.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                // TODO Auto-generated method stub
//                getMyLocationAddress();
//            }
//        });

        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);

//        makeJsonObjectRequestCounty();
    }




    private void makeJsonObjectRequest() {

        showpDialog();
        final Context context = this;

        urlJsonObj2 = "http://congress.api.sunlightfoundation.com/legislators/locate?latitude="+latitude+"&longitude="+longitude+"&apikey=9de99a9d3052497182d1ae51b0b880a2";

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                urlJsonObj2, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());

                try {
                    results = response.getJSONArray("results");
                    for (int i = 0; i < results.length(); i++) {
                        JSONObject rep = results.getJSONObject(i);

                        String name = rep.getString("first_name") + " " + rep.getString("last_name");
                        String party = rep.getString("party");

                        candidateNames.add(name);
                        candidateParties.add(party);
                    }
                    toSend = "";
                    Log.i("candSize", Integer.toString(candidateNames.size()));
                    for (int i = 0; i < candidateNames.size(); i++) {
                        toSend += candidateNames.get(i).toString() + "," + candidateParties.get(i).toString() + ",";
                    }
                    toSend += county;
                    Toast.makeText(FindMyRepHome.this, "Sending: " + toSend, Toast.LENGTH_SHORT).show();
                    Log.i("TOSEND", toSend);
                    sendMessage(WEAR_MESSAGE_PATH, toSend);
                    Intent intent = new Intent(context, CandidatesActivity.class);
                    //intent.putExtra("toSend", toSend);
                    intent.putExtra("latitude", latitude);
                    intent.putExtra("longitude", longitude);
                    startActivity(intent);


                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(),
                            "Error: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                }
                hidepDialog();
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_SHORT).show();
                // hide the progress dialog
                hidepDialog();
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq);
    }





    private void makeJsonObjectRequestZip() {

        showpDialog();
        final Context context = this;

        urlJsonObj2 = "http://congress.api.sunlightfoundation.com/legislators/locate?zip="+getEditString()+"&apikey=9de99a9d3052497182d1ae51b0b880a2";

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                urlJsonObj2, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());

                try {
                    results = response.getJSONArray("results");
                    for (int i = 0; i < results.length(); i++) {
                        JSONObject rep = results.getJSONObject(i);

                        String name = rep.getString("first_name") + " " + rep.getString("last_name");
                        String party = rep.getString("party");
                        Log.i("REQUEST", name + " " + party);
                        candidateNames.add(name);
                        candidateParties.add(party);

                    }

                    toSend = "";
                    Log.i("candSize", Integer.toString(candidateNames.size()));
                    for (int i = 0; i < candidateNames.size(); i++) {
                        toSend += candidateNames.get(i).toString() + "," + candidateParties.get(i).toString() + ",";
                    }
                    toSend += county;
                    Toast.makeText(FindMyRepHome.this, "Sending: " + toSend, Toast.LENGTH_SHORT).show();
                    Log.i("TOSEND", toSend);
                    sendMessage(WEAR_MESSAGE_PATH, toSend);
                    Intent intent = new Intent(context, CandidatesActivity.class);
                    //intent.putExtra("toSend", toSend);
                    intent.putExtra("zipNum", getEditString());
                    startActivity(intent);


                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(),
                            "Error: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                }
                hidepDialog();
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_SHORT).show();
                // hide the progress dialog
                hidepDialog();
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq);
    }




    private void makeJsonObjectRequestCountyZip() {

        showpDialog();
        final Context context = this;
        edit = (EditText) findViewById(R.id.editText);
        Log.i("TT", getEditString());
        urlJsonObj2 = "https://maps.googleapis.com/maps/api/geocode/json?address=" + getEditString() + "&key=AIzaSyCm9Ord2SEPOhLLxC0FlSmx4s33hYE8mnw";

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                urlJsonObj2, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());

                try {
                    results = response.getJSONArray("results");

                    JSONObject rep = results.getJSONObject(0);
                    JSONArray addr = rep.getJSONArray("address_components");
                    for (int i = 0; i < addr.length(); i++) {
                        JSONObject component = addr.getJSONObject(i);
                        String long_name = component.getString("long_name");
                        JSONArray types = component.getJSONArray("types");
                        //Log.i("JSON COUNTY", types.getString(0));
                        if (types.getString(0).equals("administrative_area_level_2")) {
                            county = long_name;
                            Log.i("MY COUNTY", county);
                            //txtLat.append("COUNTY: " + county);
                            txtLat.setText("My location: " + county);
                            //sendMessage(WEAR_MESSAGE_PATH, getEditString() + "," + county);
                        }

                        //String county = rep.getString("administrative_area_level_2");
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(),
                            "Error: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                }
                hidepDialog();
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_SHORT).show();
                // hide the progress dialog
                hidepDialog();
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq);
    }



    private void makeJsonObjectRequestCounty() {

        showpDialog();

        urlJsonObj2 = "https://maps.googleapis.com/maps/api/geocode/json?latlng="+latitude+","+longitude+"&key=AIzaSyCm9Ord2SEPOhLLxC0FlSmx4s33hYE8mnw";

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                urlJsonObj2, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());

                try {
                    results = response.getJSONArray("results");

                    JSONObject rep = results.getJSONObject(0);
                    JSONArray addr = rep.getJSONArray("address_components");
                    for (int i = 0; i < addr.length(); i++) {
                        JSONObject component = addr.getJSONObject(i);
                        String long_name = component.getString("long_name");
                        JSONArray types = component.getJSONArray("types");
                        if (types.getString(0).equals("administrative_area_level_2")) {
                            county = long_name;
                            txtLat.setText("My location: " + county);
                        }
                        //sendMessage(WEAR_MESSAGE_PATH, latitude + "," + longitude + "," + county);
                        //String county = rep.getString("administrative_area_level_2");
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(),
                            "Error: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                }
                hidepDialog();
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_SHORT).show();
                // hide the progress dialog
                hidepDialog();
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq);
    }

    private void showpDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hidepDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }



    //add location
    public void getMyLocationAddress() {

        Geocoder geocoder= new Geocoder(this, Locale.ENGLISH);

        try {

            //Place your latitude and longitude
            //Log.d("location", latitude + " " + longitude);
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);

            if(addresses != null) { //&& addresses.size() != 0?

                Address fetchedAddress = addresses.get(0);
                StringBuilder strAddress = new StringBuilder();

                for(int i=0; i<fetchedAddress.getMaxAddressLineIndex(); i++) {
                    strAddress.append(fetchedAddress.getSubAdminArea()).append("\n");
                }

                //myAddress.setText("I am at: " +strAddress.toString());
                txtLat.append("County: "+ strAddress.toString());

            }

            else
                myAddress.setText("No location found..!");

        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Toast.makeText(getApplicationContext(),"Could not get address..!", Toast.LENGTH_LONG).show();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Make sure that the loginButton hears the result from any
        // Activity that it triggered.
        loginButton.onActivityResult(requestCode, resultCode, data);
    }




    @Override
    public void onLocationChanged(Location location) {
        txtLat = (TextView) findViewById(R.id.locationText);
        //txtLat.setText("My location is: Latitude:" + location.getLatitude() + ", Longitude:" + location.getLongitude());
        latitude = location.getLatitude();
        longitude = location.getLongitude();
//        getMyLocationAddress();
//        makeJsonObjectRequestCounty();
//        makeJsonObjectRequestCountyZip();
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.d("Latitude", "disable");
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.d("Latitude", "enable");
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.d("Latitude", "status");
    }



    //before location

    private void initGoogleApiClient() {
        mApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)

                .build();
//        if (mApiClient != null && !(mApiClient.isConnected() || mApiClient.isConnecting()))
//            mApiClient.connect();
        mApiClient.connect();
    }

    @Override
    protected void onDestroy() {
//        if( mApiClient != null )
//            mApiClient.unregisterConnectionCallbacks( this );
        super.onDestroy();
        mApiClient.disconnect();
    }

    public void onConnected(Bundle bundle) {
        //Wearable.MessageApi.addListener(mApiClient, this);
        sendMessage(START_ACTIVITY, "");

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mApiClient);
        if (mLastLocation != null) {
            mLatitudeText = (String.valueOf(mLastLocation.getLatitude()));
            mLongitudeText = (String.valueOf(mLastLocation.getLongitude()));
            Toast.makeText(this, mLatitudeText + " " + mLongitudeText, Toast.LENGTH_SHORT);
        }

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    private void sendMessage( final String path, final String text ) {
        new Thread( new Runnable() {
            @Override
            public void run() {
                NodeApi.GetConnectedNodesResult nodes = Wearable.NodeApi.getConnectedNodes( mApiClient ).await();
                for(Node node : nodes.getNodes()) {
                    MessageApi.SendMessageResult result = Wearable.MessageApi.sendMessage(
                            mApiClient, node.getId(), path, text.getBytes() ).await();
                }

            }
        }).start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if( mApiClient != null && !( mApiClient.isConnected() || mApiClient.isConnecting() ) )
            mApiClient.connect();
    }

    @Override
    protected void onStart() {
        mApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        if ( mApiClient != null ) {
            //Wearable.MessageApi.removeListener( mApiClient, this );
            if ( mApiClient.isConnected() ) {
                mApiClient.disconnect();
            }
        }
        super.onStop();
    }

    public double getEditDouble() {
        double result;
        try {
            result = Double.parseDouble(edit.getText().toString());
        } catch (Exception e) {
            result = 0;
        }
        return result;
    }

    public String getEditString() {
        String result;
        try {
            result = edit.getText().toString();
        } catch (Exception e) {
            result = "";
        }
        return result;
    }

    public void addListenerOnButton() {
        final Context context = this;

        button = (Button) findViewById(R.id.go);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (edit.getText().toString().matches("")){
                    makeJsonObjectRequest();
//                    makeJsonObjectRequestCounty();
//                    //Toast.makeText(FindMyRepHome.this, "You did not enter a number", Toast.LENGTH_SHORT).show();
//                    Toast.makeText(FindMyRepHome.this, "Lat: " + latitude + ", Long: " + longitude, Toast.LENGTH_SHORT).show();
//
//                    //sendMessage(WEAR_MESSAGE_PATH, latitude + "," + longitude + "," + county);
//
//                    Intent intent = new Intent(context, CandidatesActivity.class);
//                    edit = (EditText) findViewById(R.id.editText);
//                    //intent.putExtra("zipNum", "94704");
//                    intent.putExtra("latitude", latitude);
//                    intent.putExtra("longitude", longitude);
//                    startActivity(intent);
                } else {
                    makeJsonObjectRequestZip();
                    //makeJsonObjectRequestCountyZip();


                    //edit = (EditText) findViewById(R.id.editText);
                    //sendMessage( WEAR_MESSAGE_PATH, getEditString() + "," + county );
                }
                //overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
            }
        });
    }

    public void addListenerOnButton2() {
        final Context context = this;

        button = (Button) findViewById(R.id.countyButton);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edit.getText().toString().matches("")){
                    makeJsonObjectRequestCounty();
                } else {
                    makeJsonObjectRequestCountyZip();
                }
            }
        });
    }

}
