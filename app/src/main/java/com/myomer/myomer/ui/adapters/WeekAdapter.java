package com.myomer.myomer.ui.adapters;


import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.viewpager.widget.PagerAdapter;

import com.myomer.myomer.R;
import com.myomer.myomer.data.local.plist_parser.PListDict;
import com.myomer.myomer.data.local.plist_parser.PListException;
import com.myomer.myomer.data.local.plist_parser.PListParser;
import com.myomer.myomer.ui.fragments.WeekFragment;
import com.myomer.myomer.util.oldProjectFiles.Constants;
import com.myomer.myomer.util.oldProjectFiles.Utilty;

import java.io.IOException;
import java.io.InputStream;


/**
 * Created by Sagar IPS
 */

public class WeekAdapter extends PagerAdapter {
    Context mContext;
    LayoutInflater mLayoutInflater;
    int dayCount;
    WeekFragment weekFragment;

    public WeekAdapter(Context context, int dayCount, WeekFragment weekFragment) {
        mContext = context;
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.dayCount = dayCount;
        this.weekFragment = weekFragment;

    }

    @Override
    public int getCount() {
        return (int) Math.ceil((double) Constants.MY_OMER_DAYS_COUNT / 7.0);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((RelativeLayout) object);
    }

    /**
     * Load all weeks from week 1 to current week
     * @param container
     * @param position
     * @return
     */
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View itemView = mLayoutInflater.inflate(R.layout.week_item_view, container, false);

        TextView tvTitleOne = (TextView) itemView.findViewById(R.id.tvTitleOne);
        TextView tvTitleTwo = (TextView) itemView.findViewById(R.id.tvTitleTwo);
        TextView tvContent = (TextView) itemView.findViewById(R.id.tvContent);

        Utilty.shareInfo(mContext,(LinearLayout) itemView.findViewById(R.id.layoutContent),(ImageView) itemView.findViewById(R.id.imgShare),null);

        AssetManager assetManager = mContext.getAssets();
        try {
            InputStream stream = null;

            stream = assetManager.open("weeks/week" + dayCount + ".plist");

            PListDict dict = PListParser.parse(stream);

            String title = dict.getString("title");
            String subTitle = dict.getString("subtitle");
            String content = dict.getString("content");
            tvTitleOne.setText(title);
            tvTitleTwo.setText(subTitle);
            tvContent.setText(content);


            String color = dict.getString("color");
            tvTitleOne.setTextColor(Color.parseColor("#" + color));
            tvTitleTwo.setTextColor(Color.parseColor("#" + color));
            Typeface type = Typeface.createFromAsset(mContext.getAssets(), "fonts/Biko_Bold.otf");
            tvTitleOne.setTypeface(type);
            tvTitleTwo.setTypeface(type);
            Typeface type1 = Typeface.createFromAsset(mContext.getAssets(), "fonts/Biko-Regular.otf");
            tvContent.setTypeface(type1);

           /*Linkify.addLinks(tvContent, Linkify.WEB_URLS);
            tvContent.setMovementMethod(LinkMovementMethod.getInstance());
            tvContent.setLinkTextColor(Color.parseColor("#0000EE"));*/

        } catch (IOException e) {
            e.printStackTrace();
        } catch (PListException e) {
            e.printStackTrace();
        }

        container.addView(itemView);
        /**
         * Select item on first time open this fragment
         */
        if (dayCount == weekFragment.currentpageno) {
            weekFragment.mViewPager.setCurrentItem(dayCount - 1, true);
        }
        dayCount++;


        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((RelativeLayout) object);
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }
}

