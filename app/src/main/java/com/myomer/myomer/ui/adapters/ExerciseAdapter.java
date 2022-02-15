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
import com.myomer.myomer.data.local.plist_parser.PListParser;
import com.myomer.myomer.ui.fragments.ExerciseFragment;
import com.myomer.myomer.util.oldProjectFiles.Constants;
import com.myomer.myomer.util.oldProjectFiles.Utilty;

import java.io.InputStream;


/**
 * Created by Sagar IPS
 */

public class ExerciseAdapter extends PagerAdapter {
    Context mContext;
    LayoutInflater mLayoutInflater;
    int dayCount;
    ExerciseFragment exerciseFragment;

    public ExerciseAdapter(Context context, int dayCount, ExerciseFragment exerciseFragment) {
        mContext = context;
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.dayCount = dayCount;
        this.exerciseFragment = exerciseFragment;

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
        View itemView = mLayoutInflater.inflate(R.layout.exercise_item_view, container, false);

        TextView tvExercise = (TextView) itemView.findViewById(R.id.tvExercise);
        TextView tvTitleOne = (TextView) itemView.findViewById(R.id.tvTitleOne);
        TextView tvDayLabel = (TextView) itemView.findViewById(R.id.tvDayLabel);
        TextView tvTitleTwo = (TextView) itemView.findViewById(R.id.tvTitleTwo);

        Utilty.shareInfo(mContext,(LinearLayout) itemView.findViewById(R.id.layoutContent),(ImageView) itemView.findViewById(R.id.imgShare),null);
        AssetManager assetManager =mContext.getAssets();
        try {
            InputStream stream = null;

            stream = assetManager.open("days/day" + dayCount + ".plist");

            PListDict dict = PListParser.parse(stream);

            String exercise = dict.getString("exercise");
            String title = dict.getString("title");
            String subTitle = dict.getString("subtitle");

            tvDayLabel.setText("Day "+dayCount);
            tvTitleOne.setText(title);
            tvTitleTwo.setText(subTitle);
            tvExercise.setText(exercise);

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

            Typeface type1 = Typeface.createFromAsset(mContext.getAssets(),"fonts/Biko-Regular.otf");
            tvExercise.setTypeface(type1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        container.addView(itemView);
        /**
         * Select item on first time open this fragment
         */
        if(dayCount== exerciseFragment.currentpageno){
            exerciseFragment.mViewPager.setCurrentItem(dayCount-1,true);
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

