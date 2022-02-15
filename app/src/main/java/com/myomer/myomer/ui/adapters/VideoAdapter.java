package com.myomer.myomer.ui.adapters;


import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.myomer.myomer.R;
import com.myomer.myomer.data.local.plist_parser.PListDict;
import com.myomer.myomer.data.local.plist_parser.PListException;
import com.myomer.myomer.data.local.plist_parser.PListParser;
import com.myomer.myomer.ui.fragments.VideoFragment;
import com.myomer.myomer.util.oldProjectFiles.Constants;
import com.myomer.myomer.util.oldProjectFiles.Utilty;

import java.io.IOException;
import java.io.InputStream;


/**
 * Created by Sagar IPS
 */

public class VideoAdapter extends PagerAdapter {
    Context mContext;
    LayoutInflater mLayoutInflater;
    int dayCount;
    VideoFragment videoFragment;

    public VideoAdapter(Context context, int dayCount, VideoFragment videoFragment) {
        mContext = context;
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.dayCount = dayCount;
        this.videoFragment = videoFragment;

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
        View itemView = mLayoutInflater.inflate(R.layout.videos_item_view, container, false);


        TextView tvTitleOne = (TextView) itemView.findViewById(R.id.tvTitleOne);
        TextView tvTitleTwo = (TextView) itemView.findViewById(R.id.tvTitleTwo);
        TextView tvContent =(TextView) itemView.findViewById(R.id.tvContent);
        ImageView ivThumbnail = (ImageView) itemView.findViewById(R.id.ivThumbnail);
        ImageView ivThumbnail2 = (ImageView) itemView.findViewById(R.id.ivThumbnail2);
        TextView tvDayLabel = (TextView) itemView.findViewById(R.id.tvDayLabel);
        TextView tvSubTitle = (TextView) itemView.findViewById(R.id.tvSubTitle);


        AssetManager assetManager = mContext.getAssets();
        try {
            InputStream stream = null;

            stream = assetManager.open("days/day" + dayCount + ".plist");

            PListDict dict = PListParser.parse(stream);

            String title = dict.getString("title");
            tvTitleOne.setText(title);

            String subTitle = dict.getString("subtitle");

            tvDayLabel.setText("Day "+dayCount);
            tvSubTitle.setText(subTitle);

            PListDict video = dict.getPListDict("video");
            if (video.size() > 0) {
                tvContent.setVisibility(View.VISIBLE);
                ivThumbnail.setVisibility(View.VISIBLE);
                ivThumbnail2.setVisibility(View.VISIBLE);
                String videoLink = video.getString("youtube_url");
                String videoTitle = video.getString("title");
                String videoDescription = video.getString("description");
                tvTitleTwo.setText(videoTitle);
                tvContent.setText(videoDescription);
                int resID = mContext.getResources().getIdentifier("a" + dayCount, "drawable", "com.myomer.myomer");
                Glide.with(mContext).load(resID).into(ivThumbnail);
//                ivThumbnail.setBackgroundResource(resID);
                ivThumbnail.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(videoLink)));
                    }
                });
                ivThumbnail2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(videoLink)));
                    }
                });
                Utilty.shareInfo(mContext,(LinearLayout) itemView.findViewById(R.id.layoutContent),(ImageView) itemView.findViewById(R.id.imgShare),(RelativeLayout) itemView.findViewById(R.id.layoutVideo),videoLink);

            }else {
                tvTitleTwo.setText("Please check back tomorrow for more videos.");
                tvContent.setVisibility(View.GONE);
                ivThumbnail.setVisibility(View.GONE);
                ivThumbnail2.setVisibility(View.GONE);
                ((ImageView) itemView.findViewById(R.id.imgShare)).setVisibility(View.GONE);

            }
            int week = dict.getInt("week");
            InputStream inputStream = null;

            inputStream = assetManager.open("weeks/week" + week + ".plist");

            PListDict weekDict = PListParser.parse(inputStream);
            String color = weekDict.getString("color");
            tvTitleOne.setTextColor(Color.parseColor("#"+color));
            tvSubTitle.setTextColor(Color.parseColor("#"+color));
            Typeface type = Typeface.createFromAsset(mContext.getAssets(),"fonts/Biko_Bold.otf");
            tvTitleOne.setTypeface(type);
            tvSubTitle.setTypeface(type);
            Typeface type1 = Typeface.createFromAsset(mContext.getAssets(),"fonts/Biko_Regular.otf");
            tvContent.setTypeface(type1);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (PListException e) {
            tvTitleTwo.setText("Please check back tomorrow for more videos.");
            tvContent.setVisibility(View.GONE);
            ivThumbnail.setVisibility(View.GONE);
            ivThumbnail2.setVisibility(View.GONE);
            ((ImageView) itemView.findViewById(R.id.imgShare)).setVisibility(View.GONE);
            e.printStackTrace();
        }

        container.addView(itemView);
        /**
         * Select item on first time open this fragment
         */
        if(dayCount== videoFragment.currentpageno){
            videoFragment.mViewPager.setCurrentItem(dayCount-1,true);
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

