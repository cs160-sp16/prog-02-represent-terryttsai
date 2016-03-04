package com.cs160.joleary.catnip;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class DetailsActivity extends Activity {

    TextView candName;
    ImageView candicon;
    TextView candParty;
    TextView candTerm;

    String selectedCand;
    Integer selectedImg;
    String selectedPartyText;
    String selectedTerm;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        Bundle extras = getIntent().getExtras();

        selectedCand = extras.getString("SelectedCand");
        selectedImg = extras.getInt("SelectedImg");
        selectedPartyText = extras.getString("SelectedPartyText");
        selectedTerm = extras.getString("SelectedTerm");

        candName = (TextView) findViewById(R.id.nameText);
        candicon = (ImageView) findViewById(R.id.detailView);
        candParty = (TextView) findViewById(R.id.partyText);
        candTerm = (TextView) findViewById(R.id.termText);

        candName.setText(selectedCand);
        candicon.setImageResource(selectedImg);
        candParty.setText(selectedPartyText);
        candTerm.setText(selectedTerm);
    }
}
