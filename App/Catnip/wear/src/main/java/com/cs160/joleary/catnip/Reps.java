package com.cs160.joleary.catnip;

import android.app.ProgressDialog;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.app.Activity;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.wearable.view.GridViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

public class Reps extends FragmentActivity {

    MyPageAdapter pageAdapter;
    String zipcode;

    private JSONArray results;
    private ProgressDialog pDialog;
    private String urlJsonObj2;
    private String county;
    private String msg;
    List<String> itemList;

    private static String TAG = "json";

    @Override

    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reps);

        Bundle extras = getIntent().getExtras();
        zipcode = extras.getString("zipNum");
        msg = extras.getString("toSend");
        if (msg != null) {
            String[] items = msg.split(",");
            itemList = Arrays.asList(items);
        }


        Toast.makeText(Reps.this, "Passed msg: " + msg, Toast.LENGTH_SHORT).show();

        List<Fragment> fragments = getFragments();

        pageAdapter = new MyPageAdapter(getSupportFragmentManager(), fragments);

        ViewPager pager = (ViewPager)findViewById(R.id.viewpager);

        pager.setAdapter(pageAdapter);

//        pDialog = new ProgressDialog(this);
//        pDialog.setMessage("Please wait...");
//        pDialog.setCancelable(false);
//
//        makeJsonObjectRequestCountyZip();

    }

    private void makeJsonObjectRequestCountyZip() {

        showpDialog();

        urlJsonObj2 = "https://maps.googleapis.com/maps/api/geocode/json?address="+zipcode+"&key=AIzaSyCm9Ord2SEPOhLLxC0FlSmx4s33hYE8mnw";

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                urlJsonObj2, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());

                try {
                    results = response.getJSONArray("results");

                    JSONObject rep = results.getJSONObject(0);
                    JSONArray addr = rep.getJSONArray("address_components");
                    for (int i = 0; i < results.length(); i++) {
                        JSONObject component = addr.getJSONObject(i);
                        String long_name = component.getString("long_name");
                        JSONArray types = component.getJSONArray("types");
                        Log.i("JSON COUNTY", types.getString(0));
                        if (types.getString(0).equals("administrative_area_level_2")) {
                            county = long_name;
                            Log.i("MY COUNTY", county);
                            //txtLat.append("COUNTY: " + county);
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

    private void showpDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hidepDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    private List<Fragment> getFragments() {

//        if (zipcode.equals("88888")) {
//
//            List<Fragment> fList = new ArrayList<Fragment>();
//            fList.add(MyFragment.newInstance("Virgil Goode", R. drawable.virgil1, R.drawable.ind));
//            fList.add(MyFragment.newInstance("Kevin McCarthy", R.drawable.kevin1, R.drawable.rep));
//            fList.add(MyFragment.newInstance("Barbara Boxer", R.drawable.hillary1, R.drawable.dem));
//            fList.add(MyFragment.newInstance(county, R.drawable.sotu_s2));
//
//            return fList;
//        } else if (zipcode.equals("99999")) {
//            List<Fragment> fList = new ArrayList<Fragment>();
//            fList.add(MyFragment.newInstance("Batman", R.drawable.batman, R.drawable.dem));
//            fList.add(MyFragment.newInstance("Joker", R. drawable.joker, R.drawable.rep));
//            fList.add(MyFragment.newInstance("Bane", R.drawable.bane, R.drawable.ind));
//            fList.add(MyFragment.newInstance(county, R.drawable.sotu_s2));
//
//            return fList;
//        }
        if (zipcode != null && zipcode.equals("21221")) {
            List<Fragment> fList = new ArrayList<Fragment>();
            fList.add(MyFragment.newInstance("C. Ruppersberger", R.drawable.sotu_s2, R.drawable.dem));
            fList.add(MyFragment.newInstance("Barbara Mikulski", R.drawable.sotu_s2, R.drawable.dem));
            fList.add(MyFragment.newInstance("Benjamin Cardin", R.drawable.sotu_s2, R.drawable.dem));
            fList.add(MyFragment.newInstance("Baltimore County  -> Obama: 87.4% Romney: 11.1%", R.drawable.sotu_s2));
            return fList;
        } else {
            List<Fragment> fList = new ArrayList<Fragment>();

            int k = itemList.size() / 2;
            Log.i("itemList", itemList.toString());
            Log.i("itemList Size", Integer.toString(itemList.size()));

            for (int i = 0; i <= k; i = i + 2) {
                String first = itemList.get(i).toString();
                String second = itemList.get(i + 1).toString();
                if (second.contains("D")) {
                    fList.add(MyFragment.newInstance(first,
                            R.drawable.sotu_s2, R.drawable.dem));
                } else if (second.contains("R")) {
                    fList.add(MyFragment.newInstance(first,
                            R.drawable.sotu_s2, R.drawable.rep));
                } else {
                    fList.add(MyFragment.newInstance(first,
                            R.drawable.sotu_s2, R.drawable.ind));
                }
            }
            fList.add(MyFragment.newInstance(itemList.get(itemList.size() - 1) + " -> Obama: 78.5% Romney: 18.7%", R.drawable.sotu_s2));
//        fList.add(MyFragment.newInstance("Albus Dumbledore", R.drawable.dumbledore, R.drawable.dem));
//        fList.add(MyFragment.newInstance("Severus Snape", R.drawable.snape2, R.drawable.ind));
//        fList.add(MyFragment.newInstance("Lord Voldemort", R. drawable.voldemort, R.drawable.rep));
            //fList.add(MyFragment.newInstance(zipcode, R.drawable.sotu_s2));

            return fList;
        }
    }

}
