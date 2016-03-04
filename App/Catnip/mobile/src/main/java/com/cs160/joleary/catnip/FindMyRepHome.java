package com.cs160.joleary.catnip;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

public class FindMyRepHome extends Activity implements GoogleApiClient.ConnectionCallbacks {

    Button button;
    EditText edit;

    private GoogleApiClient mApiClient;
    private static final String START_ACTIVITY = "/start_activity";
    private static final String WEAR_MESSAGE_PATH = "/message";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        edit = (EditText) findViewById(R.id.editText);
        addListenerOnButton();
        initGoogleApiClient();
    }

    private void initGoogleApiClient() {
        mApiClient = new GoogleApiClient.Builder( this )
                .addApi( Wearable.API )
                //.addConnectionCallbacks( this )
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
                    //Toast.makeText(FindMyRepHome.this, "You did not enter a number", Toast.LENGTH_SHORT).show();
                    Toast.makeText(FindMyRepHome.this, "Sending location", Toast.LENGTH_SHORT).show();
                    sendMessage(WEAR_MESSAGE_PATH, "94704");

                    Intent intent = new Intent(context, CandidatesActivity.class);
                    edit = (EditText) findViewById(R.id.editText);
                    intent.putExtra("zipNum", "94704");
                    startActivity(intent);
                } else {
                    Toast.makeText(FindMyRepHome.this, "Sending zip: " + getEditString(), Toast.LENGTH_SHORT).show();
                    edit = (EditText) findViewById(R.id.editText);
                    sendMessage( WEAR_MESSAGE_PATH, getEditString() );

                    Intent intent = new Intent(context, CandidatesActivity.class);
                    intent.putExtra("zipNum", getEditString());
                    startActivity(intent);
                }
                //overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
            }
        });
    }

}
