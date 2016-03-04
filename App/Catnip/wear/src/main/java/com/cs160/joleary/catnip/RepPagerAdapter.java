package com.cs160.joleary.catnip;

//import android.app.Fragment;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.wearable.view.CardFragment;
import android.support.wearable.view.FragmentGridPagerAdapter;
import android.support.wearable.view.GridPagerAdapter;

import java.util.List;

/**
 * Created by Terry T. Tsai on 3/3/2016.
 */
public class RepPagerAdapter extends FragmentPagerAdapter {

    private Context mContext;
    private List mRows;

    public RepPagerAdapter(Context ctx, FragmentManager fm) {
        super(fm);
        mContext = ctx;
    }

    static final int[] BG_IMAGES = new int[] {
        R.drawable.hillary1,
            R.drawable.kevin1,
            R.drawable.virgil1
    };

    // Create a static set of pages in a 2D array
//    private final Page[][] PAGES = { };

    @Override
    public Fragment getItem(int position) {

        return null;
    }

    @Override
    public int getCount() {
        return 0;
    }

//    // Obtain the UI fragment at the specified position
//    @Override
//    public Fragment getFragment(int row, int col) {
//        Page page = PAGES[row][col];
//        String title =
//                page.titleRes != 0 ? mContext.getString(page.titleRes) : null;
//        String text =
//                page.textRes != 0 ? mContext.getString(page.textRes) : null;
//        CardFragment fragment = CardFragment.create(title, text, page.iconRes);
//
//        // Advanced settings (card gravity, card expansion/scrolling)
////        fragment.setCardGravity(page.cardGravity);
////        fragment.setExpansionEnabled(page.expansionEnabled);
////        fragment.setExpansionDirection(page.expansionDirection);
////        fragment.setExpansionFactor(page.expansionFactor);
//        return fragment;
//    }
//
//    // Obtain the background image for the row
//    @Override
//    public Drawable getBackgroundForRow(int row) {
//        return mContext.getResources().getDrawable(
//                (BG_IMAGES[row % BG_IMAGES.length]), null);
//    }
//
//    // Obtain the background image for the specific page
//    @Override
//    public Drawable getBackgroundForPage(int row, int column) {
//        if( row == 2 && column == 1) {
//            // Place image at specified position
//            return mContext.getResources().getDrawable(R.drawable.virgil1, null);
//        } else {
//            // Default to background image for row
//            return GridPagerAdapter.BACKGROUND_NONE;
//        }
//    }
//
//    // Obtain the number of pages (vertical)
//    @Override
//    public int getRowCount() {
//        return PAGES.length;
//    }
//
//    // Obtain the number of pages (horizontal)
//    @Override
//    public int getColumnCount(int rowNum) {
//        return PAGES[rowNum].length;
//    }

    // A simple container for static data in each page
    private static class Page {
        // static resources
        int titleRes;
        int textRes;
        int iconRes;
    }

    // Override methods in FragmentGridPagerAdapter
}
