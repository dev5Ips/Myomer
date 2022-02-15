package com.myomer.myomer.ui.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.myomer.myomer.R;
import com.myomer.myomer.activities.HomeActivity;
import com.myomer.myomer.adapters.VideoAdapter;
import com.myomer.myomer.event_bus.Events;
import com.myomer.myomer.event_bus.GlobalBus;
import com.myomer.myomer.utilty.Constants;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class VideoFragment extends Fragment {

    public ViewPager mViewPager;
    VideoAdapter videoAdapter;
    int oldPosition = 0;
    public int currentpageno=1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_video, container, false);
        mViewPager = (ViewPager) view.findViewById(R.id.viewpager);
        videoAdapter = new VideoAdapter(this.getContext(), 1,this);
        mViewPager.setOffscreenPageLimit(Constants.MY_OMER_DAYS_COUNT);
        mViewPager.setAdapter(videoAdapter);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if(position>oldPosition){
                    //+
                    Log.e("TAG", "onPageSelected: +");
                    ((HomeActivity)getActivity()).nextFromViewpager(position+1);
                }else if(position<oldPosition){
                    //-
                    Log.e("TAG", "onPageSelected: -");
                    ((HomeActivity)getActivity()).previousFromViewPager(position+1);
                }
                oldPosition=position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        GlobalBus.getBus().register(this);

        return view;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Unregister the registered event.
        GlobalBus.getBus().unregister(this);
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void getDayChangeEvent(Events.DayChangeEvent event) {


        populateViews(event.getDayChangeEvent());
    }
    private void populateViews(int dayCount) {
        currentpageno = dayCount;
    }

    public void moveto(int count) {
        try {
            mViewPager.setCurrentItem(count-1,true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
