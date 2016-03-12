package com.cs160.joleary.catnip;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

/**
 * Created by Terry T. Tsai on 3/3/2016.
 */
public class MyFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks {

    public static final String EXTRA_MESSAGE = "EXTRA_MESSAGE";
    public static final String BACKGROUND = "BACKGROUND";
    public static final String PARTY = "PARTY";

    private GoogleApiClient mApiClient;
    private static final String START_ACTIVITY = "/start_activity";
    private static final String WEAR_MESSAGE_PATH = "/message";

    public static final MyFragment newInstance(String message, int background)

    {

        MyFragment f = new MyFragment();

        Bundle bdl = new Bundle(1);

        bdl.putString(EXTRA_MESSAGE, message);

        bdl.putInt(BACKGROUND, background);

        f.setArguments(bdl);

        return f;

    }

    public static final MyFragment newInstance(String message, int background, int partyicon)

    {

        MyFragment f = new MyFragment();

        Bundle bdl = new Bundle(1);

        bdl.putString(EXTRA_MESSAGE, message);

        bdl.putInt(BACKGROUND, background);

        bdl.putInt(PARTY, partyicon);

        f.setArguments(bdl);

        return f;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,

                             Bundle savedInstanceState) {

        final String message = getArguments().getString(EXTRA_MESSAGE);

        Integer background = getArguments().getInt(BACKGROUND);

        Integer partyicon = getArguments().getInt(PARTY);

        View v = inflater.inflate(R.layout.myfragment_layout, container, false);

        TextView messageTextView = (TextView)v.findViewById(R.id.textView);

        ImageView backgroundView = (ImageView)v.findViewById(R.id.background);

        ImageView partyView = (ImageView)v.findViewById(R.id.partyicon);

        messageTextView.setText(message);

        backgroundView.setBackgroundResource(background);

//        backgroundView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(getActivity(), "Tapped! " + message, Toast.LENGTH_SHORT).show();
//                //insert send to phone function here
//                sendMessage(WEAR_MESSAGE_PATH, message);
//            }
//        });

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Tapped! " + message, Toast.LENGTH_SHORT).show();
                //insert send to phone function here
                sendMessage(WEAR_MESSAGE_PATH, message);
            }
        });

        if (partyicon != null) {
            partyView.setImageResource(partyicon);
        }

        initGoogleApiClient();

        return v;

    }

    private void initGoogleApiClient() {
        mApiClient = new GoogleApiClient.Builder( getActivity() )
                .addApi( Wearable.API )
                .build();

        mApiClient.connect();
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
    public void onDestroy() {
        super.onDestroy();
        mApiClient.disconnect();
    }

    public void onConnected(Bundle bundle) {
        sendMessage( START_ACTIVITY, "" );
    }

    //@Override
    public void onConnectionSuspended(int i) {

    }
}
