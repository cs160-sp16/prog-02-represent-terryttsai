package com.cs160.joleary.catnip;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.app.ListActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Wearable;

import java.util.ArrayList;

public class CandidatesActivity extends Activity implements MessageApi.MessageListener, GoogleApiClient.ConnectionCallbacks {

    ListView list;
    String zipcode;
    TextView yourZip;
    private GoogleApiClient mApiClient;
    private static final String WEAR_MESSAGE_PATH = "/message";
    Integer [] candidateImages = {R.drawable.hillary1,R.drawable.kevin1,R.drawable.virgil1};
    Integer [] candidateImages2 = {R.drawable.dumbledore,R.drawable.snape2,R.drawable.voldemort};
    Integer [] candidateImages3 = {R.drawable.batman,R.drawable.joker,R.drawable.bane};
    String [] candidateNames = {"Barbara Boxer", "Kevin McCarthy", "Virgil Goode"};
    String [] candidateNames2 = {"Albus Dumbledore", "Severus Snape", "Lord Voldemort"};
    String [] candidateNames3 = {"Batman", "Joker", "Bane"};
    String [] candidateTwitter = {
            "Putting the country first means Obama nominating a justice and the...",
            "America reaffirms its support for our Turkish allies during this...",
            "Please vote for me. Thanks to @kingsthings and @freeandqual for..."};
    Integer [] candidateParty = {R.drawable.dem, R.drawable.rep, R.drawable.ind};
    Integer [] candidateParty2 = {R.drawable.dem, R.drawable.ind, R.drawable.rep};
    Integer [] candidateParty3 = {R.drawable.dem, R.drawable.ind, R.drawable.rep};
    String [] candidatePartyText = {"Democrat", "Republican", "Independent"};
    String [] candidatePartyText2 = {"Democrat", "Independent", "Republican"};
    String [] candidatePartyText3 = {"Democrat", "Independent", "Republican"};
    String [] candidateTerms = {"2/3/2017", "5/6/2018", "6/6/2006"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.candidates);

        Bundle extras = getIntent().getExtras();
        zipcode = extras.getString("zipNum");
        yourZip = (TextView) findViewById(R.id.zip);
        yourZip.setText("Your zipcode is: " + zipcode);

        initGoogleApiClient();

        if (zipcode.equals("94704")) {

            CustomCandidateAdapter adapter = new CustomCandidateAdapter(this, candidateNames, candidateImages, candidateTwitter, candidateParty);
            list = (ListView) findViewById(R.id.list);
            list.setAdapter(adapter);
            list.setOnItemClickListener(new OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    // TODO Auto-generated method stub
                    //                String Slecteditem = candidateParty[+position].toString();
                    //                Toast.makeText(getApplicationContext(), Slecteditem, Toast.LENGTH_SHORT).show();
                    String SelectedCand = candidateNames[+position];
                    Integer SelectedImg = candidateImages[+position];
                    String SelectedPartyText = candidatePartyText[+position];
                    String SelectedTerm = candidateTerms[+position];
                    Intent i = new Intent(getApplicationContext(), DetailsActivity.class);
                    i.putExtra("SelectedCand", SelectedCand);
                    i.putExtra("SelectedImg", SelectedImg);
                    i.putExtra("SelectedPartyText", SelectedPartyText);
                    i.putExtra("SelectedTerm", SelectedTerm);
                    startActivity(i);
                }
            });
        } else if (zipcode.equals("99999")) {
            CustomCandidateAdapter adapter = new CustomCandidateAdapter(this, candidateNames3, candidateImages3, candidateTwitter, candidateParty3);
            list = (ListView) findViewById(R.id.list);
            list.setAdapter(adapter);
            list.setOnItemClickListener(new OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    // TODO Auto-generated method stub
                    //                String Slecteditem = candidateParty[+position].toString();
                    //                Toast.makeText(getApplicationContext(), Slecteditem, Toast.LENGTH_SHORT).show();
                    String SelectedCand = candidateNames3[+position];
                    Integer SelectedImg = candidateImages3[+position];
                    String SelectedPartyText = candidatePartyText3[+position];
                    String SelectedTerm = candidateTerms[+position];
                    Intent i = new Intent(getApplicationContext(), DetailsActivity.class);
                    i.putExtra("SelectedCand", SelectedCand);
                    i.putExtra("SelectedImg", SelectedImg);
                    i.putExtra("SelectedPartyText", SelectedPartyText);
                    i.putExtra("SelectedTerm", SelectedTerm);
                    startActivity(i);
                }
            });
        } else {
            CustomCandidateAdapter adapter = new CustomCandidateAdapter(this, candidateNames2, candidateImages2, candidateTwitter, candidateParty2);
            list = (ListView) findViewById(R.id.list);
            list.setAdapter(adapter);
            list.setOnItemClickListener(new OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    // TODO Auto-generated method stub
                    //                String Slecteditem = candidateParty[+position].toString();
                    //                Toast.makeText(getApplicationContext(), Slecteditem, Toast.LENGTH_SHORT).show();
                    String SelectedCand = candidateNames2[+position];
                    Integer SelectedImg = candidateImages2[+position];
                    String SelectedPartyText = candidatePartyText2[+position];
                    String SelectedTerm = candidateTerms[+position];
                    Intent i = new Intent(getApplicationContext(), DetailsActivity.class);
                    i.putExtra("SelectedCand", SelectedCand);
                    i.putExtra("SelectedImg", SelectedImg);
                    i.putExtra("SelectedPartyText", SelectedPartyText);
                    i.putExtra("SelectedTerm", SelectedTerm);
                    startActivity(i);
                }
            });
        }
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
                    intent.putExtra("SelectedTerm", candidateTerms[2]);
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
