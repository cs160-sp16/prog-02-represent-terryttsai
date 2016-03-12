package com.cs160.joleary.catnip;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.app.ListActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Wearable;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.core.services.StatusesService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CandidatesActivity extends Activity implements MessageApi.MessageListener, GoogleApiClient.ConnectionCallbacks {

    ListView list;
    String zipcode;
    TextView yourZip;
    private GoogleApiClient mApiClient;
    private static final String WEAR_MESSAGE_PATH = "/message";
    Integer [] candidateImages = {R.drawable.hillary1,R.drawable.kevin1,R.drawable.virgil1};
    Integer [] candidateImages2 = {R.drawable.dumbledore,R.drawable.snape2,R.drawable.voldemort};
    Integer [] candidateImages3 = {R.drawable.batman,R.drawable.joker,R.drawable.bane};
    //String [] candidateNames = {"Barbara Boxer", "Kevin McCarthy", "Virgil Goode"};

    String [] candidateNames2 = {"Albus Dumbledore", "Severus Snape", "Lord Voldemort"};
    String [] candidateNames3 = {"Batman", "Joker", "Bane"};
    String [] candidateTwitter = {
            "Putting the country first means Obama nominating a justice and the...",
            "America reaffirms its support for our Turkish allies during this...",
            "Please vote for me. Thanks to @kingsthings and @freeandqual for..."};

    //Integer [] candidateParty = {R.drawable.dem, R.drawable.rep, R.drawable.ind};
    Integer [] candidateParty = {R.drawable.dem, R.drawable.rep, R.drawable.ind};
    Integer [] candidateParty2 = {R.drawable.dem, R.drawable.ind, R.drawable.rep};
    Integer [] candidateParty3 = {R.drawable.dem, R.drawable.ind, R.drawable.rep};
    String [] candidatePartyText = {"Democrat", "Republican", "Independent"};
    String [] candidatePartyText2 = {"Democrat", "Independent", "Republican"};
    String [] candidatePartyText3 = {"Democrat", "Independent", "Republican"};
    //String [] candidateTerms = {"2/3/2017", "5/6/2018", "6/6/2006"};
    ArrayList<String> candidateNames = new ArrayList<>();
    ArrayList<String> candidateEmails = new ArrayList<>();;
    ArrayList<String> candidateTwitterIDs = new ArrayList<>();
    ArrayList<String> candidateParties = new ArrayList<>();
    ArrayList<String> candidateTerms = new ArrayList<>();
    ArrayList<String> candidateWebsites = new ArrayList<>();
    ArrayList<String> candidateBioguides = new ArrayList<>();


    protected Double latitude, longitude;
    private String urlJsonObj2 = "http://congress.api.sunlightfoundation.com/legislators/locate?latitude=40.7127&longitude=-74.0059&apikey=9de99a9d3052497182d1ae51b0b880a2";

    private static String TAG = LocationActivity.class.getSimpleName();
    private Button btnMakeObjectRequest;

    // Progress dialog
    private ProgressDialog pDialog;

    private TextView txtResponse;

    // temporary string to show the parsed response
    private String jsonResponse;
    private JSONArray results;

    //Activity myActivity = this;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.candidates);

        Bundle extras = getIntent().getExtras();
        zipcode = extras.getString("zipNum");
        latitude = extras.getDouble("latitude");
        longitude = extras.getDouble("longitude");

//        yourZip = (TextView) findViewById(R.id.zip);
//        if (zipcode != null) {
//            yourZip.setText("Your zipcode is: " + zipcode + "\n");
//        }
//        if (latitude != null) {
//            yourZip.setText("Your location is: " + latitude + " " + longitude + "\n");
//        }


        initGoogleApiClient();



        //json stuff
        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);

        if (zipcode != null) {
            makeJsonObjectRequestZip();
        } else {
            makeJsonObjectRequest();
        }
        CustomCandidateAdapter adapter =
                new CustomCandidateAdapter(this, candidateNames, candidateEmails, candidateTwitterIDs,
                        candidateParties, candidateTerms, candidateWebsites);
        list = (ListView) findViewById(R.id.list);
        list.setAdapter(adapter);

        list.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // TODO Auto-generated method stub
                String Slecteditem = candidateNames.get(position).toString();
                //Toast.makeText(getApplicationContext(), Slecteditem, Toast.LENGTH_SHORT).show();
                String SelectedCand = candidateNames.get(position).toString();
                String SelectedTwitterID = candidateTwitterIDs.get(position).toString();
                String SelectedPartyText = candidateParties.get(position).toString();
                String SelectedTerm = candidateTerms.get(position);
                String SelectedBioguide = candidateBioguides.get(position).toString();
                Intent i = new Intent(getApplicationContext(), DetailsActivity.class);
                i.putExtra("SelectedCand", SelectedCand);
                i.putExtra("SelectedTwitterID", SelectedTwitterID);
                i.putExtra("SelectedPartyText", SelectedPartyText);
                i.putExtra("SelectedTerm", SelectedTerm);
                i.putExtra("SelectedBioguide", SelectedBioguide);
                startActivity(i);
            }
        });
    }



    //json stuff
    private void makeJsonObjectRequestZip() {

        showpDialog();

        urlJsonObj2 = "http://congress.api.sunlightfoundation.com/legislators/locate?zip="+zipcode+"&apikey=9de99a9d3052497182d1ae51b0b880a2";

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
                        String email = rep.getString("oc_email");
                        String party = rep.getString("party");
                        String twitter_id = rep.getString("twitter_id");
                        String term_end = rep.getString("term_end");
                        String website = rep.getString("website");
                        String bioguide_id = rep.getString("bioguide_id");

                        candidateNames.add(name);
                        candidateEmails.add(email);
                        candidateTwitterIDs.add(twitter_id);
                        candidateParties.add(party);
                        candidateTerms.add(term_end);
                        candidateWebsites.add(website);
                        candidateBioguides.add(bioguide_id);

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


    private void makeJsonObjectRequest() {

        showpDialog();

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
                        String email = rep.getString("oc_email");
                        String party = rep.getString("party");
                        String twitter_id = rep.getString("twitter_id");
                        String term_end = rep.getString("term_end");
                        String website = rep.getString("website");
                        String bioguide_id = rep.getString("bioguide_id");

                        candidateNames.add(name);
                        candidateEmails.add(email);
                        candidateTwitterIDs.add(twitter_id);
                        candidateParties.add(party);
                        candidateTerms.add(term_end);
                        candidateWebsites.add(website);
                        candidateBioguides.add(bioguide_id);
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



    private void initGoogleApiClient() {
        mApiClient = new GoogleApiClient.Builder( this )
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .build();

        if (mApiClient != null && !(mApiClient.isConnected() || mApiClient.isConnecting()))
            mApiClient.connect();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if( mApiClient != null && !( mApiClient.isConnected() || mApiClient.isConnecting() ) )
            mApiClient.connect();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    //@Override
    public void onMessageReceived(final MessageEvent messageEvent) {
        final Context context = this;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (messageEvent.getPath().equalsIgnoreCase(WEAR_MESSAGE_PATH)) {
//                    mAdapter.add(new String(messageEvent.getData()));
//                    mAdapter.notifyDataSetChanged();
                    String representative = new String(messageEvent.getData());

                    Toast.makeText(CandidatesActivity.this, "Received watch tap: " + representative, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(context, DetailsActivity.class);
                    //intent.putExtra("representative", representative);
                    intent.putExtra("SelectedCand", representative);
                    intent.putExtra("SelectedImg", candidateImages[2]);
                    intent.putExtra("SelectedPartyText", candidatePartyText[2]);
                    //intent.putExtra("SelectedTerm", candidateTerms[2]);
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    public void onConnected( Bundle bundle ) {
        Wearable.MessageApi.addListener(mApiClient, this);
    }

    @Override
    protected void onStop() {
        if ( mApiClient != null ) {
            Wearable.MessageApi.removeListener( mApiClient, this );
            if ( mApiClient.isConnected() ) {
                mApiClient.disconnect();
            }
        }
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        if( mApiClient != null )
            mApiClient.unregisterConnectionCallbacks( this );
        super.onDestroy();
        //mApiClient.disconnect(); //added
    }

    @Override
    public void onConnectionSuspended(int i) {

    }
}
