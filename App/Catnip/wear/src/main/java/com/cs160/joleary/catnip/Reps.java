package com.cs160.joleary.catnip;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.app.Activity;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.wearable.view.GridViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class Reps extends FragmentActivity {

    MyPageAdapter pageAdapter;
    String zipcode;

    @Override

    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reps);

        Bundle extras = getIntent().getExtras();
        zipcode = extras.getString("zipNum");
        Toast.makeText(Reps.this, "Passed zip: " + zipcode, Toast.LENGTH_SHORT).show();

        List<Fragment> fragments = getFragments(zipcode);

        pageAdapter = new MyPageAdapter(getSupportFragmentManager(), fragments);

        ViewPager pager = (ViewPager)findViewById(R.id.viewpager);

        pager.setAdapter(pageAdapter);

    }

    private List<Fragment> getFragments(String zipcode) {

        if (zipcode.equals("94704")) {

            List<Fragment> fList = new ArrayList<Fragment>();
            fList.add(MyFragment.newInstance("Virgil Goode", R. drawable.virgil1, R.drawable.ind));
            fList.add(MyFragment.newInstance("Kevin McCarthy", R.drawable.kevin1, R.drawable.rep));
            fList.add(MyFragment.newInstance("Barbara Boxer", R.drawable.hillary1, R.drawable.dem));
            fList.add(MyFragment.newInstance("2012 election results in Orange County: 25% Obama, 75% Romney", R.drawable.sotu_s2));

            return fList;
        } else if (zipcode.equals("99999")) {
            List<Fragment> fList = new ArrayList<Fragment>();
            fList.add(MyFragment.newInstance("Batman", R.drawable.batman, R.drawable.dem));
            fList.add(MyFragment.newInstance("Joker", R. drawable.joker, R.drawable.rep));
            fList.add(MyFragment.newInstance("Bane", R.drawable.bane, R.drawable.ind));
            fList.add(MyFragment.newInstance("2012 election results in Random County: 50% Obama, 50% Romney", R.drawable.sotu_s2));

            return fList;
        }
        List<Fragment> fList = new ArrayList<Fragment>();
        fList.add(MyFragment.newInstance("Albus Dumbledore", R.drawable.dumbledore, R.drawable.dem));
        fList.add(MyFragment.newInstance("Severus Snape", R.drawable.snape2, R.drawable.ind));
        fList.add(MyFragment.newInstance("Lord Voldemort", R. drawable.voldemort, R.drawable.rep));
        fList.add(MyFragment.newInstance("2012 election results in Alameda County: 67% Obama, 33% Romney", R.drawable.sotu_s2));

        return fList;
    }

}
