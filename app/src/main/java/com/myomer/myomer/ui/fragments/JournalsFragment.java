package com.myomer.myomer.ui.fragments;

import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.myomer.myomer.R;
import com.myomer.myomer.adapters.QuestionAdapter;
import com.myomer.myomer.event_bus.Events;
import com.myomer.myomer.event_bus.GlobalBus;
import com.myomer.myomer.plist_parser.PListArray;
import com.myomer.myomer.plist_parser.PListDict;
import com.myomer.myomer.plist_parser.PListException;
import com.myomer.myomer.plist_parser.PListParser;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.io.InputStream;

import me.relex.circleindicator.CircleIndicator;


public class JournalsFragment extends Fragment {

    private ViewPager mViewPager;
    CircleIndicator indicator;
    QuestionAdapter questionAdapter;
    TextView tvQuest,tvPageNumber;

    public JournalsFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_journals, container, false);
        mViewPager = (ViewPager) view.findViewById(R.id.viewpager);
        indicator = (CircleIndicator) view.findViewById(R.id.indicator);
        tvQuest = (TextView) view.findViewById(R.id.tvQuest);
        tvPageNumber = (TextView) view.findViewById(R.id.tvPageNumber);
        Typeface type1 = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Biko_Regular.otf");
        tvPageNumber.setTypeface(type1);
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
    private void populateViews(int dayCount){

        AssetManager assetManager = getActivity().getAssets();
        try {
            InputStream stream = null;

            stream = assetManager.open("days/day" + dayCount + ".plist");

            PListDict dict = PListParser.parse(stream);
            int day = dict.getInt("day");
            int week = dict.getInt("week");
            InputStream inputStream = null;

            inputStream = assetManager.open("weeks/week" + week + ".plist");

            PListDict weekDict = PListParser.parse(inputStream);
            String color = weekDict.getString("color");

            PListArray questions = dict.getPListArray("questions");

            String title = dict.getString("title");
            String subTitle = dict.getString("subtitle");


            questionAdapter = new QuestionAdapter(this.getContext(), questions, day, color, title,subTitle);
            mViewPager.setOffscreenPageLimit(questions.size());
            mViewPager.setAdapter(questionAdapter);
            indicator.setViewPager(mViewPager);
//            if(questions.size()>0) {
//                tvPageNumber.setVisibility(View.VISIBLE);
//                tvPageNumber.setText(1 + " of " + questions.size());
//            }else {
//                tvPageNumber.setVisibility(View.GONE);
//            }
            mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int i, float v, int i1) {

                }

                @Override
                public void onPageSelected(int i) {
                    tvPageNumber.setText(i+1+" of "+questions.size());
                }

                @Override
                public void onPageScrollStateChanged(int i) {

                }
            });
            questionAdapter.registerDataSetObserver(indicator.getDataSetObserver());
            if (questions.size() > 0) {
                tvQuest.setVisibility(View.GONE);

            }else {
                tvQuest.setVisibility(View.VISIBLE);

                tvQuest.setText("Please check back tomorrow for more journal questions.");
                tvQuest.setTextColor(Color.parseColor("#"+color));
            }


        } catch (IOException e) {
            e.printStackTrace();
        } catch (PListException e) {
            e.printStackTrace();
        }
    }

}
