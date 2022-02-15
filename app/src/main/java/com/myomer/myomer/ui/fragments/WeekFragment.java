package com.myomer.myomer.ui.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.myomer.myomer.R;
import com.myomer.myomer.activities.HomeActivity;
import com.myomer.myomer.adapters.WeekAdapter;
import com.myomer.myomer.event_bus.Events;
import com.myomer.myomer.event_bus.GlobalBus;
import com.myomer.myomer.utilty.Constants;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link WeekFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WeekFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
/*
    TextView tvTopHeading, tvQuote,tvTitleOne,tvTitleTwo,tvContent;
*/
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public ViewPager mViewPager;
    WeekAdapter weekAdapter;
    int oldPosition = 0;
    public int currentpageno=1;

    public WeekFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment WeekFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static WeekFragment newInstance(String param1, String param2) {
        WeekFragment fragment = new WeekFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_week, container, false);

        mViewPager = (ViewPager) view.findViewById(R.id.viewpager);
        weekAdapter = new WeekAdapter(this.getContext(), 1,this);
        mViewPager.setOffscreenPageLimit((int) Math.ceil((double) Constants.MY_OMER_DAYS_COUNT / 7.0));
        mViewPager.setAdapter(weekAdapter);
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

      /*  tvTitleOne = (TextView) view.findViewById(R.id.tvTitleOne);
        tvTitleTwo = (TextView) view.findViewById(R.id.tvTitleTwo);
        tvContent =(TextView) view.findViewById(R.id.tvContent);

        */

        GlobalBus.getBus().register(this);

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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

//        if(mViewPager.getAdapter()!=null && mViewPager.getAdapter().getCount()==(int) Math.round((double) Constants.MY_OMER_DAYS_COUNT / 7)){
//            mViewPager.setCurrentItem(dayCount - 1, true);
//        }else {
            currentpageno=dayCount ;
//        }

        /*AssetManager assetManager = getActivity().getAssets();
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
            tvTitleOne.setTextColor(Color.parseColor("#"+color));
            tvTitleTwo.setTextColor(Color.parseColor("#"+color));
            Typeface type = Typeface.createFromAsset(getActivity().getAssets(),"font/Biko_Bold.otf");
            tvTitleOne.setTypeface(type);
            tvTitleTwo.setTypeface(type);
            Typeface type1 = Typeface.createFromAsset(getActivity().getAssets(),"font/Biko-Regular.otf");
            tvContent.setTypeface(type1);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (PListException e) {
            e.printStackTrace();
        }*/
    }

    public void moveto(int weekCount) {
        try {
            mViewPager.setCurrentItem(weekCount-1,true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void movetoNoAnimation(int weekCount) {
        mViewPager.setCurrentItem(weekCount-1,false);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
