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
import com.myomer.myomer.fragments.DailyFragment;
import com.myomer.myomer.plist_parser.PListDict;
import com.myomer.myomer.plist_parser.PListException;
import com.myomer.myomer.plist_parser.PListParser;
import com.myomer.myomer.utilty.Constants;
import com.myomer.myomer.utilty.Utilty;

import java.io.IOException;
import java.io.InputStream;


/**
 * Created by Sagar IPS
 */

public class DayAdapter extends PagerAdapter {
    Context mContext;
    LayoutInflater mLayoutInflater;
    int dayCount;
    DailyFragment dailyFragment;

    public DayAdapter(Context context, int dayCount, DailyFragment dailyFragment) {
        mContext = context;
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.dayCount = dayCount;
        this.dailyFragment = dailyFragment;

    }

    @Override
    public int getCount() {
        return Constants.MY_OMER_DAYS_COUNT;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((RelativeLayout) object);
    }

    /**
     * Load all data from day 1 to current day
     * @param container
     * @param position
     * @return
     */
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View itemView = mLayoutInflater.inflate(R.layout.day_item_view, container, false);

        TextView tvTitleOne = (TextView) itemView.findViewById(R.id.tvTitleOne);
        TextView tvDayLabel = (TextView) itemView.findViewById(R.id.tvDayLabel);
        TextView tvTitleTwo = (TextView) itemView.findViewById(R.id.tvTitleTwo);
        TextView tvContent =(TextView) itemView.findViewById(R.id.tvContent);

        TextView tvMeditation = (TextView) itemView.findViewById(R.id.tvMeditation);
        Utilty.shareInfo(mContext,(LinearLayout) itemView.findViewById(R.id.layoutContent),(ImageView) itemView.findViewById(R.id.imgShare),null);

        AssetManager assetManager = mContext.getAssets();
        try {
            InputStream stream = null;
            stream = assetManager.open("days/day" + dayCount + ".plist");
            PListDict dict = PListParser.parse(stream);

            String title = dict.getString("title");
            String subTitle = dict.getString("subtitle");
            String content = dict.getString("content");
            tvTitleOne.setText(title);
            tvTitleTwo.setText(subTitle);
            tvContent.setText(content.equals("null") ? "Get started with today’s journal questions and exercise.":content);
            tvDayLabel.setText("Day "+dayCount);

            int week = dict.getInt("week");
            InputStream inputStream = null;

            inputStream = assetManager.open("weeks/week" + week + ".plist");

            PListDict weekDict = PListParser.parse(inputStream);
            String color = weekDict.getString("color");
            tvTitleOne.setTextColor(Color.parseColor("#"+color));
            tvTitleTwo.setTextColor(Color.parseColor("#"+color));
            Typeface type = Typeface.createFromAsset(mContext.getAssets(),"fonts/Biko_Bold.otf");
            tvTitleOne.setTypeface(type);
            tvTitleTwo.setTypeface(type);
            Typeface type1 = Typeface.createFromAsset(mContext.getAssets(),"fonts/Biko_Regular.otf");
            tvContent.setTypeface(type1);
            Typeface type3 = Typeface.createFromAsset(mContext.getAssets(),"fonts/Biko_Bold.otf");
            tvMeditation.setTypeface(type3);
            Typeface type2 = Typeface.createFromAsset(mContext.getAssets(),"fonts/dino_bold.otf");
            tvDayLabel.setTypeface(type2);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (PListException e) {
            e.printStackTrace();
        }
        container.addView(itemView);
        /**
         * Select item on first time open this fragment
         */
        if(dayCount== dailyFragment.currentpageno){
            dailyFragment.mViewPager.setCurrentItem(dayCount-1,true);
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

