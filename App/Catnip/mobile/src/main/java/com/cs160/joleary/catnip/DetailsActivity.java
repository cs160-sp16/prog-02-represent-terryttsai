package com.cs160.joleary.catnip;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
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

public class DetailsActivity extends Activity {

    TextView candName;
    ImageView candicon;
    TextView candParty;
    TextView candTerm;
    //ListView candCommittees;
    LinearLayout linearLay;


    String selectedCand;
    String selectedTwitterID;
    String selectedPartyText;
    String selectedTerm;
    String selectedBioguide;

    ArrayList<CommitteeBill> committees = new ArrayList<CommitteeBill>();

    private String urlJsonObj2 = "http://congress.api.sunlightfoundation.com/legislators/locate?latitude=40.7127&longitude=-74.0059&apikey=9de99a9d3052497182d1ae51b0b880a2";

    private static String TAG = LocationActivity.class.getSimpleName();

    // Progress dialog
    private ProgressDialog pDialog;


    // temporary string to show the parsed response
    private JSONArray results;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_listview);

        ListView listView = (ListView) findViewById(R.id.listCommittees);
        View header = getLayoutInflater().inflate(R.layout.activity_details_header, null);
        listView.addHeaderView(header, null, false);

        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);

        Bundle extras = getIntent().getExtras();

        selectedCand = extras.getString("SelectedCand");
        selectedTwitterID = extras.getString("SelectedTwitterID");
        selectedPartyText = extras.getString("SelectedPartyText");
        selectedTerm = extras.getString("SelectedTerm");
        selectedBioguide = extras.getString("SelectedBioguide");

        candName = (TextView) findViewById(R.id.nameText);
        candicon = (ImageView) findViewById(R.id.detailView);
        candParty = (TextView) findViewById(R.id.partyText);
        candTerm = (TextView) findViewById(R.id.termText);
        //candCommittees = (ListView) findViewById(R.id.listCommittees);
        linearLay = (LinearLayout) findViewById(R.id.linearLay);

        candName.setText(selectedCand);
        if (selectedPartyText.equals("D")) {
            candParty.setText("Democrat");
        } else if (selectedPartyText.equals("R")) {
            candParty.setText("Republican");
        } else {
            candParty.setText("Independent");
        }

        candTerm.setText("Term end: " + selectedTerm);

        TwitterApiClient twitterApiClient = TwitterCore.getInstance().getApiClient();
        StatusesService statusesService = twitterApiClient.getStatusesService();
        //statusesService.show(524971209851543553L, null, null, null, new Callback<Tweet>() {
        statusesService.userTimeline(null, selectedTwitterID, 1, null, null, null, null, null, null, new Callback<List<Tweet>>() {
            @Override
            public void success(Result<List<Tweet>> result) {
                //Do something with result, which provides a Tweet inside of result.data
                for (Tweet tweet : result.data) {
                    new ImageLoadTask(tweet.user.profileImageUrl.replace("_normal", ""), candicon).execute();
                }

            }

            public void failure(TwitterException exception) {
                //Do something on failure
            }
        });

        makeJsonObjectRequest();
        makeJsonObjectRequestBills();
        //ListView listView = (ListView) getLayoutInflater().inflate(R.layout.activity_details_listview, null);

        //ArrayAdapter<String> committeesAdapter = new ArrayAdapter<String>(this, R.layout.list_item_view1, committees);
        CommiteeBillAdapter committeesAdapter = new CommiteeBillAdapter(this, committees);
        listView.setAdapter(committeesAdapter);
        //candCommittees.setAdapter(committeesAdapter);
//        for (int i = 0; i < committeesAdapter.getCount(); i++) {
//            View item = committeesAdapter.getView(i, null, null);
//            linearLay.addView(item);
//        }

    }

    private void makeJsonObjectRequest() {

        showpDialog();

        urlJsonObj2 = "http://congress.api.sunlightfoundation.com/committees?member_ids="+selectedBioguide+"&apikey=9de99a9d3052497182d1ae51b0b880a2";

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                urlJsonObj2, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());

                try {
                    int header = 1;
                    committees.add(new CommitteeBill("Committees", header));
                    results = response.getJSONArray("results");
                    for (int i = 0; i < results.length(); i++) {
                        JSONObject rep = results.getJSONObject(i);

                        String name = rep.getString("name");
                        committees.add(new CommitteeBill(name, 0));
                    }
                    committees.add(new CommitteeBill("Bills", header));


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

    private void makeJsonObjectRequestBills() {

        showpDialog();

        urlJsonObj2 = "http://congress.api.sunlightfoundation.com/bills?sponsor_id="+selectedBioguide+"&apikey=9de99a9d3052497182d1ae51b0b880a2";

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                urlJsonObj2, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());

                try {
                    results = response.getJSONArray("results");
                    for (int i = 0; i < results.length(); i++) {
                        JSONObject rep = results.getJSONObject(i);

                        String name = rep.getString("short_title");
                        if (name != null && !name.equals("null")) {
                            committees.add(new CommitteeBill(name, 0));
                        }
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


}
