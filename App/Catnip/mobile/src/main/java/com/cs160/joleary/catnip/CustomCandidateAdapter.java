package com.cs160.joleary.catnip;

/**
 * Created by Terry T. Tsai on 3/1/2016.
 */
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.core.services.StatusesService;
import com.twitter.sdk.android.tweetui.TweetUtils;
import com.twitter.sdk.android.tweetui.TweetView;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class CustomCandidateAdapter extends ArrayAdapter<String> {

    private final Activity context;
    private final ArrayList itemname;
    private ArrayList candidateEmails;
    private ArrayList candidateTwitterIDs;
    private ArrayList candidateParties;
    private ArrayList candidateTerms;
    private ArrayList candidateWebsites;


    public CustomCandidateAdapter(Activity context, ArrayList itemname, ArrayList candidateEmails,
                                  ArrayList candidateTwitterIDs, ArrayList candidateParties,
                                  ArrayList candidateTerms, ArrayList candidateWebsites) {
        super(context, R.layout.candidate_item, itemname);
        // TODO Auto-generated constructor stub

        this.context=context;
        this.itemname=itemname;
        this.candidateEmails=candidateEmails;
        this.candidateTwitterIDs=candidateTwitterIDs;
        this.candidateParties=candidateParties;
        this.candidateTerms=candidateTerms;
        this.candidateWebsites=candidateWebsites;

    }

    public View getView(int position,View view,ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.candidate_item, null, true);

        TextView txtTitle = (TextView) rowView.findViewById(R.id.candidateName);
        final ImageView imageView = (ImageView) rowView.findViewById(R.id.candicon);
        final TextView extratxt = (TextView) rowView.findViewById(R.id.twitter);
        ImageView partyView = (ImageView) rowView.findViewById(R.id.partyView);
        TextView emailLink = (TextView) rowView.findViewById(R.id.email);
        TextView webLink = (TextView) rowView.findViewById(R.id.web);

        txtTitle.setText(itemname.get(position).toString());

        String candParty = candidateParties.get(position).toString();
        if (candParty.contains("D")) {
            partyView.setImageResource(R.drawable.dem);
        } else if (candParty.contains("R")) {
            partyView.setImageResource(R.drawable.rep);
        } else {
            partyView.setImageResource(R.drawable.ind);
        }



        final String candEmail = "<a href=\"mailto:"+candidateEmails.get(position).toString()+" target=\"_blank\""+"\">EMAIL</a>";
        final String candEmail1 = candidateEmails.get(position).toString();
        emailLink.setLinksClickable(true);
        emailLink.setMovementMethod(LinkMovementMethod.getInstance());
        emailLink.setText(Html.fromHtml(candEmail));
        emailLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* Create the Intent */
//                final Intent emailIntent = new Intent(android.content.Intent.ACTION_SENDTO);
//
//                /* Fill it with Data */
//                emailIntent.setType("message/rfc822");
//                emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{candEmail1});
//                emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Subject");
//                emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "Text");

                /* Send it off to the Activity-Chooser */
                //context.startActivity(Intent.createChooser(emailIntent, "Send mail..."));
                final Intent i = new Intent(context, LocationActivity.class);
                context.startActivity(i);
            }
        });

        final String candWeb = "<a href=\""+candidateWebsites.get(position).toString()+"\">WEB</a>";
        webLink.setLinksClickable(true);
        webLink.setMovementMethod(LinkMovementMethod.getInstance());
        webLink.setText(Html.fromHtml(candWeb));

//        extratxt.setText(candParty);
//        extratxt.append("\n" + candEmail);


        TwitterApiClient twitterApiClient = TwitterCore.getInstance().getApiClient();
        StatusesService statusesService = twitterApiClient.getStatusesService();
        //statusesService.show(524971209851543553L, null, null, null, new Callback<Tweet>() {
        statusesService.userTimeline(null, candidateTwitterIDs.get(position).toString(), 1, null, null, null, null, null, null, new Callback<List<Tweet>>() {
            @Override
            public void success(Result<List<Tweet>> result) {
                //Do something with result, which provides a Tweet inside of result.data
                for (Tweet tweet : result.data) {
                    extratxt.setText(tweet.text);
                    new ImageLoadTask(tweet.user.profileImageUrl.replace("_normal",""), imageView).execute();
                }

            }

            public void failure(TwitterException exception) {
                //Do something on failure
            }
        });






        return rowView;

    };

}