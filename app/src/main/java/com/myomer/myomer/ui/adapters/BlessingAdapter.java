package com.myomer.myomer.ui.adapters;


import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SwitchCompat;
import androidx.viewpager.widget.PagerAdapter;

import com.myomer.myomer.R;
import com.myomer.myomer.activities.HomeActivity;
import com.myomer.myomer.fragments.BlessingsFragment;
import com.myomer.myomer.helpers.SharedPreferenceHelper;
import com.myomer.myomer.models.RecordBlessing;
import com.myomer.myomer.plist_parser.PListDict;
import com.myomer.myomer.plist_parser.PListParser;
import com.myomer.myomer.realm.RealmController;
import com.myomer.myomer.utilty.Constants;
import com.myomer.myomer.utilty.Utilty;

import java.io.InputStream;
import java.util.Date;

import io.realm.Realm;


/**
 * Created by Sagar IPS
 */

public class BlessingAdapter extends PagerAdapter {
    Context mContext;
    LayoutInflater mLayoutInflater;
    int dayCount;
    BlessingsFragment blessingsFragment;
    String selectedLocale = "en";


    public BlessingAdapter(Context context, int dayCount, BlessingsFragment blessingsFragment) {
        mContext = context;
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.dayCount = dayCount;
        this.blessingsFragment = blessingsFragment;

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
    public Object instantiateItem(ViewGroup container, final int position) {
        View itemView = mLayoutInflater.inflate(R.layout.blessing_item_view, container, false);
        Log.e("TAG", "instantiateItem: " + dayCount);
        final RelativeLayout layoutLang = (RelativeLayout) itemView.findViewById(R.id.layoutLang);
        final TextView wvBlessings = (TextView) itemView.findViewById(R.id.wvBlessings);
        final TextView tvQuote = (TextView) itemView.findViewById(R.id.tvQuote);
        final TextView tvSefirah = (TextView) itemView.findViewById(R.id.tvSefirah);
        final SwitchCompat recordBlessing = (SwitchCompat) itemView.findViewById(R.id.simpleSwitch);
        final TextView tvLocale = (TextView) itemView.findViewById(R.id.tvLocale);
        final TextView tvBlessingRecord = (TextView) itemView.findViewById(R.id.tvBlessingRecord);

        Utilty.shareInfo(mContext, (LinearLayout) itemView.findViewById(R.id.layoutContent), (ImageView) itemView.findViewById(R.id.imgShare), null);


        wvBlessings.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                return (event.getAction() == MotionEvent.ACTION_MOVE);
            }
        });

        selectedLocale = SharedPreferenceHelper.getSharedPreferenceString(mContext, "selectedLocale", "en");

        wvBlessings.setScrollContainer(false);
        tvLocale.setText(selectedLocale);
        populateViews(tvQuote, tvSefirah, wvBlessings, tvLocale, layoutLang, tvBlessingRecord, recordBlessing, dayCount);


        RecordBlessing recordedBlessing = RealmController.with((Activity) mContext).getRecorderBlessing(dayCount);
        if (recordedBlessing != null) {
            recordBlessing.setChecked(recordedBlessing.isRecorded());
            switchColor(recordBlessing, layoutLang);
        }

        recordBlessing.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Realm realm = RealmController.with((Activity) mContext).getRealm();
                realm.beginTransaction();
                RecordBlessing rb = new RecordBlessing();
                rb.setId(position + 1);
                rb.setRecorded(b);
                rb.setYear(Utilty.getYear(new Date()));
                realm.copyToRealmOrUpdate(rb);
                realm.commitTransaction();
                switchColor(recordBlessing, layoutLang);
            }
        });

        /**
         * Language change
         */
        layoutLang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder builderSingle = new AlertDialog.Builder((Activity) mContext);
                builderSingle.setTitle("Language");

                final String[] arrayAdapter = {"Hebrew",
                        "English",
                        "Russian",
                        "Spanish",
                        "French"};

                int itemToBeSelected = 0;

                builderSingle.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                if (selectedLocale.equalsIgnoreCase("en")) {
                    itemToBeSelected = 1;
                } else if (selectedLocale.equalsIgnoreCase("hb")) {
                    itemToBeSelected = 0;
                } else if (selectedLocale.equalsIgnoreCase("ru")) {
                    itemToBeSelected = 2;
                } else if (selectedLocale.equalsIgnoreCase("es")) {
                    itemToBeSelected = 3;
                } else if (selectedLocale.equalsIgnoreCase("fr")) {
                    itemToBeSelected = 4;
                }
                builderSingle.setSingleChoiceItems(arrayAdapter, itemToBeSelected, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String strName = arrayAdapter[which];
                        if (strName.equalsIgnoreCase("English")) {
                            selectedLocale = "en";
                        } else if (strName.equalsIgnoreCase("Hebrew")) {
                            selectedLocale = "hb";
                        } else if (strName.equalsIgnoreCase("Russian")) {
                            selectedLocale = "ru";
                        } else if (strName.equalsIgnoreCase("Spanish")) {
                            selectedLocale = "es";
                        } else if (strName.equalsIgnoreCase("French")) {
                            selectedLocale = "fr";
                        }
                        SharedPreferenceHelper.setSharedPreferenceString(mContext, "selectedLocale", selectedLocale);
                        tvLocale.setText(selectedLocale);
                        dialog.dismiss();
                        ((HomeActivity) mContext).reloadBlessings();

                    }
                });
                builderSingle.show();
            }
        });

        container.addView(itemView);
        /**
         * Select item on first time open this fragment
         */
        if (dayCount == blessingsFragment.currentpageno) {
            blessingsFragment.mViewPager.setCurrentItem(dayCount - 1, true);
        }

        dayCount++;
        return itemView;
    }

    /**
     * Switch color change as per current week color
     * @param recordBlessing
     * @param layoutLang
     */
    private void switchColor(SwitchCompat recordBlessing, RelativeLayout layoutLang) {
        int color = Color.TRANSPARENT;
        Drawable background = layoutLang.getBackground();
        if (background instanceof ColorDrawable) {
            color = ((ColorDrawable) background).getColor();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            recordBlessing.getTrackDrawable().setColorFilter(recordBlessing.isChecked() ? color : Color.GRAY, PorterDuff.Mode.MULTIPLY);
            recordBlessing.getThumbDrawable().setColorFilter(recordBlessing.isChecked() ? Color.LTGRAY : Color.LTGRAY, PorterDuff.Mode.MULTIPLY);
        }
    }


    private void populateViews(TextView tvQuote, TextView tvSefirah, TextView wvBlessings, TextView tvLocale, RelativeLayout layoutLang, TextView tvBlessingRecord, SwitchCompat recordBlessing, int dayCount) {
        AssetManager assetManager = mContext.getAssets();
        try {
            InputStream stream = null;

            stream = assetManager.open("days/day" + dayCount + ".plist");

            PListDict dict = PListParser.parse(stream);
            int week = dict.getInt("week");
            PListDict quote = dict.getPListDict("quote");
            String quoteString = quote.getString(selectedLocale);
            PListDict sefirah = dict.getPListDict("sefirah");
            String sefirahText = sefirah.getString(selectedLocale);

            InputStream is = assetManager.open("blessings/blessings." + selectedLocale + ".html");

            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();



            String str = new String(buffer);
            str = str.replace("%%DYNAMIC1%%", quoteString);
            str = str.replace("%%DYNAMIC2%%", sefirahText);



            //Now instead of webview.loadURL(""), I needed to do something like -
            //wvBlessings.loadDataWithBaseURL("file:///android_asset/", str, "text/html", "UTF-8",null);
            Typeface type1 = Typeface.createFromAsset(mContext.getAssets(), "fonts/Biko_Regular.otf");
            wvBlessings.setTypeface(type1);
            InputStream inputStream = null;

            inputStream = assetManager.open("weeks/week" + week + ".plist");

            PListDict weekDict = PListParser.parse(inputStream);
            String color = weekDict.getString("color");
            tvLocale.setBackgroundColor(Color.parseColor("#" + color));
            layoutLang.setBackgroundColor(Color.parseColor("#" + color));
            tvBlessingRecord.setTextColor(Color.parseColor("#" + color));
            tvQuote.setTextColor(Color.parseColor("#" + color));
            tvSefirah.setTextColor(Color.parseColor("#" + color));

            Typeface type = Typeface.createFromAsset(mContext.getAssets(), "fonts/Biko_Bold.otf");
            tvQuote.setTypeface(type);
            tvSefirah.setTypeface(type);
            tvBlessingRecord.setTypeface(type);

            tvQuote.setText(quoteString);
            tvSefirah.setText(sefirahText);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                wvBlessings.setText(Html.fromHtml(str,Html.FROM_HTML_MODE_LEGACY));
            }else {
                wvBlessings.setText(Html.fromHtml(str));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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

