package com.cs160.joleary.catnip;

/**
 * Created by Terry T. Tsai on 3/1/2016.
 */
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CustomCandidateAdapter extends ArrayAdapter<String> {

    private final Activity context;
    private final String[] itemname;
    private final Integer[] imgid;
    private final String[] tweet;
    private final Integer[] partyid;

    public CustomCandidateAdapter(Activity context, String[] itemname, Integer[] imgid, String[] tweet, Integer[] partyid) {
        super(context, R.layout.candidate_item, itemname);
        // TODO Auto-generated constructor stub

        this.context=context;
        this.itemname=itemname;
        this.imgid=imgid;
        this.tweet=tweet;
        this.partyid=partyid;
    }

    public View getView(int position,View view,ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.candidate_item, null, true);

        TextView txtTitle = (TextView) rowView.findViewById(R.id.candidateName);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.candicon);
        TextView extratxt = (TextView) rowView.findViewById(R.id.twitter);
        ImageView partyView = (ImageView) rowView.findViewById(R.id.partyView);

        txtTitle.setText(itemname[position]);
        imageView.setImageResource(imgid[position]);
        extratxt.setText("Twitter: "+tweet[position]);
        partyView.setImageResource(partyid[position]);
        return rowView;

    };
}