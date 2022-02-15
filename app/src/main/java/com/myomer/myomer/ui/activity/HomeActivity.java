package com.myomer.myomer.ui.activity;


import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.myomer.myomer.R;
import com.myomer.myomer.event_bus.Events;
import com.myomer.myomer.event_bus.GlobalBus;
import com.myomer.myomer.fragments.BlessingsFragment;
import com.myomer.myomer.fragments.DailyFragment;
import com.myomer.myomer.fragments.ExerciseFragment;
import com.myomer.myomer.fragments.JournalsFragment;
import com.myomer.myomer.fragments.VideoFragment;
import com.myomer.myomer.fragments.WeekFragment;
import com.myomer.myomer.helpers.SharedPreferenceHelper;
import com.myomer.myomer.models.JournalQuestionModelNew;
import com.myomer.myomer.models.MyOmerPeriod;
import com.myomer.myomer.plist_parser.PListDict;
import com.myomer.myomer.plist_parser.PListException;
import com.myomer.myomer.plist_parser.PListParser;
import com.myomer.myomer.realm.RealmController;
import com.myomer.myomer.utilty.Constants;
import com.myomer.myomer.utilty.CustomAlert;
import com.myomer.myomer.utilty.Utilty;
import com.myomer.myomer.utilty.gota.Gota;
import com.myomer.myomer.utilty.gota.GotaResponse;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.Days;
import org.joda.time.LocalDate;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.Date;

import butterknife.ButterKnife;
import butterknife.OnClick;
import co.ceryle.radiorealbutton.RadioRealButton;
import co.ceryle.radiorealbutton.RadioRealButtonGroup;
import io.realm.RealmResults;
import io.realm.Sort;

public class HomeActivity extends AppCompatActivity implements
        WeekFragment.OnFragmentInteractionListener,
        NavigationView.OnNavigationItemSelectedListener, Gota.OnRequestPermissionsBack {

    TextView tvTopHeading, tvQuote, tvTitleOne, tvTitleTwo, tvContent, tvDay1, tvDay2, tvDay3, tvDay4, tvDay5, tvDay6, tvDay7;

    RadioRealButtonGroup buttonGroup;
    ImageView ivNext, ivPrevious, ivMenu;
    public int count = 1;
    public int dayCounter = 1;
    int weekCounter = 0;
    int weekCount = 0;
    int buttonClicked = 1;
    LinearLayout llParentPanel;
    Intent intent;
    RelativeLayout layoutCount;
    boolean isWeektab;
    String action = "";
    DailyFragment dailyFragment;
    WeekFragment weekFragment;
    BlessingsFragment blessingsFragment;
    ExerciseFragment exerciseFragment;
    VideoFragment videoFragment;
    int currenttab = 0;
    BottomNavigationView navigation;
    NavigationView navigationView;
    LinearLayout headerContainer;
    DrawerLayout drawer;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            ivPrevious.setVisibility(View.VISIBLE);
            ivNext.setVisibility(View.VISIBLE);
            refreshArrows();
            updateBottomviewcolor();
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    if (currenttab == 3 || currenttab == 4 || currenttab == 5) {
                        showTabs(true);
                        currenttab = 0;
                        isWeektab = false;
                        buttonGroup.setPosition(0);
                        dailyFragment = new DailyFragment();
                        Utilty.replaceFragment(HomeActivity.this, dailyFragment, R.id.frame_container);
                        count = dayCounter;
                        weekCount = weekCounter;
                        GlobalBus.getBus().postSticky(new Events.DayChangeEvent(count));
                        populateViews(count);
                    }
                    return true;
                case R.id.navigation_journal:
                    if (currenttab != 5) {
                        isWeektab = false;
                        currenttab = 5;
                        Utilty.replaceFragment(HomeActivity.this, new JournalsFragment(), R.id.frame_container);
                        GlobalBus.getBus().postSticky(new Events.DayChangeEvent(count));
                        showTabs(false);
                    }
                    return true;
                case R.id.navigation_exercise:
                    if (currenttab != 3) {
                        isWeektab = false;
                        currenttab = 3;
                        exerciseFragment = new ExerciseFragment();
                        Utilty.replaceFragment(HomeActivity.this, exerciseFragment, R.id.frame_container);
                        GlobalBus.getBus().postSticky(new Events.DayChangeEvent(count));
                        showTabs(false);
                    }
                    return true;
                case R.id.navigation_video:
                    if (currenttab != 4) {
                        isWeektab = false;
                        currenttab = 4;
                        videoFragment = new VideoFragment();
                        Utilty.replaceFragment(HomeActivity.this, videoFragment, R.id.frame_container);
                        GlobalBus.getBus().postSticky(new Events.DayChangeEvent(count));
                        showTabs(false);
                    }
                    return true;
            }
            return false;
        }
    };

    private void updateBottomviewcolor() {
        try {
            int color = Color.TRANSPARENT;
            Drawable background = llParentPanel.getBackground();
            if (background instanceof ColorDrawable) {
                color = ((ColorDrawable) background).getColor();
            }

            ColorStateList iconsColorStates = new ColorStateList(
                    new int[][]{
                            new int[]{-android.R.attr.state_checked},
                            new int[]{android.R.attr.state_checked}
                    },
                    new int[]{
                            Color.parseColor("#123456"),
                            Color.parseColor(String.format("#%06X", (0xFFFFFF & color)))
                    });

            ColorStateList textColorStates = new ColorStateList(
                    new int[][]{
                            new int[]{-android.R.attr.state_checked},
                            new int[]{android.R.attr.state_checked}
                    },
                    new int[]{
                            Color.parseColor("#123456"),
                            Color.parseColor(String.format("#%06X", (0xFFFFFF & color)))
                    });

            navigation.setItemIconTintList(iconsColorStates);
            navigation.setItemTextColor(textColorStates);
           /* if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setNavigationBarColor(color);
            }*/

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showTabs(boolean show) {
        if (show) {
            buttonGroup.setVisibility(View.VISIBLE);
            layoutCount.setVisibility(View.VISIBLE);
        } else {
            buttonGroup.setVisibility(View.GONE);
            layoutCount.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);
        initViews();

        int currentYear = Utilty.getYear(new Date());
        MyOmerPeriod myOmerPeriod = RealmController.with(this).getPeriodByYear(currentYear);


        // Old logic start here
        Date startDateOfOmer = myOmerPeriod.getStartDate();
        Date currentDate = new Date();
        Constants.MY_OMER_DAYS_COUNT = Days.daysBetween(new LocalDate(startDateOfOmer), new LocalDate(currentDate)).getDays();
        // old logic ends here

        // for night fall
//        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
//        int minutes = Calendar.getInstance().get(Calendar.MINUTE);
//        int nightFallMinutes =SharedPreferenceHelper.getSharedPreferenceInt(this,"NightfallHour",20)*60;
//        if(((hour*60)+minutes) >= nightFallMinutes){
//            Constants.MY_OMER_DAYS_COUNT = Constants.MY_OMER_DAYS_COUNT + 1;
//        }

        // new nightfall logic +1 day
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        int minutes = Calendar.getInstance().get(Calendar.MINUTE);
        int nightFallHours =SharedPreferenceHelper.getSharedPreferenceInt(this,"NightfallHour",0);
        int nightFallMinutes = SharedPreferenceHelper.getSharedPreferenceInt(this,"NightfallMin",0);

        if(hour>=nightFallHours && minutes>=nightFallMinutes){
            Constants.MY_OMER_DAYS_COUNT = Constants.MY_OMER_DAYS_COUNT + 1;
        }


        intent = getIntent();
        int dayNumber = intent.getIntExtra("DayNumber", 0);
        if (dayNumber != 0) {
            count = dayNumber;
        } else {
            count = Constants.MY_OMER_DAYS_COUNT;
        }


        if (dayNumber != 0) {
            dayCounter = dayNumber;
        } else {
            dayCounter = Constants.MY_OMER_DAYS_COUNT;
        }

        navigation = findViewById(R.id.navigation);
        Utilty.removeShiftMode(navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

//        Utilty.replaceFragment(this, new DailyFragment(), R.id.frame_container);
        populateViews(count);

        /*int currentYear = Utilty.getYear(new Date());
        MyOmerPeriod myOmerPeriod = RealmController.with(this).getPeriodByYear(currentYear);
        Date startDateOfOmer = myOmerPeriod.getStartDate();
        Date currentDate = new Date();

        Constants.MY_OMER_DAYS_COUNT = Days.daysBetween(new LocalDate(startDateOfOmer), new LocalDate(currentDate)).getDays();*/

        currenttab = 0;
        dailyFragment = new DailyFragment();
        Utilty.replaceFragment(this, dailyFragment, R.id.frame_container);
        GlobalBus.getBus().postSticky(new Events.DayChangeEvent(count));
        refreshArrows();

        ivNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isWeektab) {
                    if (count < 49) {
                        count++;
                    }
                }
                action = "+";
                refreshArrows();
                if (currenttab == 5) {
                    GlobalBus.getBus().postSticky(new Events.DayChangeEvent(count));
                }
                ivPrevious.setVisibility(View.VISIBLE);
                populateViews(count);
                if (currenttab == 0) {
                    dailyFragment.moveto(count);
                } else if (currenttab == 1) {
                    weekFragment.moveto(weekCount);
                } else if (currenttab == 2) {
                    blessingsFragment.moveto(count);
                } else if (currenttab == 3) {
                    exerciseFragment.moveto(count);
                } else if (currenttab == 4) {
                    videoFragment.moveto(count);
                }

            }
        });
        ivPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isWeektab) {
                    count--;
                }
                action = "-";
                if (currenttab == 5) {
                    GlobalBus.getBus().postSticky(new Events.DayChangeEvent(count));
                }
                ivNext.setVisibility(View.VISIBLE);
                populateViews(count);
                if (currenttab == 0) {
                    dailyFragment.moveto(count);
                } else if (currenttab == 1) {
                    weekFragment.moveto(weekCount);
                } else if (currenttab == 2) {
                    blessingsFragment.moveto(count);
                } else if (currenttab == 3) {
                    exerciseFragment.moveto(count);
                } else if (currenttab == 4) {
                    videoFragment.moveto(count);
                }
                if (count == 1 && !isWeektab) {
                    ivPrevious.setVisibility(View.GONE);
                }
            }
        });
        buttonGroup.setOnClickedButtonListener(new RadioRealButtonGroup.OnClickedButtonListener() {
            @Override
            public void onClickedButton(RadioRealButton button, int position) {
                action = "";
                ivPrevious.setVisibility(View.VISIBLE);
                ivNext.setVisibility(View.VISIBLE);
                buttonGroup.setClickable(false);
                if (position == 0 && currenttab != 0) {
                    isWeektab = false;
                    currenttab = 0;
                    dailyFragment = new DailyFragment();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Utilty.replaceFragment(HomeActivity.this, dailyFragment, R.id.frame_container);
                            GlobalBus.getBus().postSticky(new Events.DayChangeEvent(count));
                        }
                    }, 500);
                }/* else if (position == 1 && currenttab != 1) {
                    isWeektab = true;
                    currenttab = 1;
                    weekFragment = new WeekFragment();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Utilty.replaceFragment(HomeActivity.this, weekFragment, R.id.frame_container);
                            GlobalBus.getBus().postSticky(new Events.DayChangeEvent(weekCount));
                        }
                    }, 500);
                }*/ else if (position == 1 && currenttab != 1) {
                    isWeektab = false;
                    currenttab = 2;
                    blessingsFragment = new BlessingsFragment();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Utilty.replaceFragment(HomeActivity.this, blessingsFragment, R.id.frame_container);
                            GlobalBus.getBus().postSticky(new Events.DayChangeEvent(count));
                        }
                    }, 500);


                }
                refreshArrows();
                populateViews(count);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        buttonGroup.setClickable(true);
                    }
                }, 600);
            }
        });

        //from notification
       /* int dayNumber = intent.getIntExtra("DayNumber", 0);
        if (dayNumber != 0) {
            count = dayNumber;
        }

        GlobalBus.getBus().postSticky(new Events.DayChangeEvent(count));*/

    }

    /**
     * Reload blessing fragment on language change
     */
    public void reloadBlessings() {
        isWeektab = false;
        currenttab = 2;
        blessingsFragment = new BlessingsFragment();
        Utilty.replaceFragment(HomeActivity.this, blessingsFragment, R.id.frame_container);
        GlobalBus.getBus().postSticky(new Events.DayChangeEvent(count));
        refreshArrows();
        populateViews(count);
    }

    /**
     * Manual swipe detection of viewpager
     *
     * @param currentday
     */
    public void nextFromViewpager(int currentday) {
        Log.e("TAG", "nextFromViewpager: " + currentday + " " + count);
        if (currentday != count || isWeektab) {
            if (!isWeektab) {
                if (count < 49) {
                    count++;
                }
            } else if (isWeektab && currentday == weekCount) {
                action = "";
            } else {
                action = "+";
            }
            refreshArrows();
//            GlobalBus.getBus().postSticky(new Events.DayChangeEvent(count));
            ivPrevious.setVisibility(View.VISIBLE);
            populateViews(count);
        }
    }

    /**
     * Manual swipe detection of viewpager
     *
     * @param currentday
     */
    public void previousFromViewPager(int currentday) {
        Log.e("TAG", "previousFromViewPager: " + currentday + " " + count);

        if (currentday != count || isWeektab) {
            if (!isWeektab) {
                count--;
            } else if (isWeektab && currentday == weekCount) {
                action = "";
            } else {
                action = "-";
            }
//            GlobalBus.getBus().postSticky(new Events.DayChangeEvent(count));
            ivNext.setVisibility(View.VISIBLE);
            populateViews(count);
            if (count == 1 && !isWeektab) {
                ivPrevious.setVisibility(View.GONE);
            }
        }

    }

    /**
     * Refresh arrow functions for all tabs, except weekly
     */
    private void refreshArrows() {

        if (count == 1) {
            ivPrevious.setVisibility(View.GONE);
        } else {
            ivPrevious.setVisibility(View.VISIBLE);
        }

        if (count == Constants.MY_OMER_DAYS_COUNT) {
            ivNext.setVisibility(View.GONE);
        } else {
            ivNext.setVisibility(View.VISIBLE);
        }

    }

    private void initViews() {
        layoutCount = findViewById(R.id.layoutCount);
        tvTopHeading = findViewById(R.id.tvTopHeading);
        tvQuote = findViewById(R.id.tvQuote);
        tvTitleOne = findViewById(R.id.tvTitleOne);
        tvTitleTwo = findViewById(R.id.tvTitleTwo);
        tvContent = findViewById(R.id.tvContent);
        ivNext = findViewById(R.id.ivNext);
        ivPrevious = findViewById(R.id.ivPrevious);
        buttonGroup = findViewById(R.id.buttonGroup);
        llParentPanel = findViewById(R.id.parentPanel);
        ivMenu = findViewById(R.id.ivMenu);
        tvDay1 = findViewById(R.id.tvDay1);
        tvDay2 = findViewById(R.id.tvDay2);
        tvDay3 = findViewById(R.id.tvDay3);
        tvDay4 = findViewById(R.id.tvDay4);
        tvDay5 = findViewById(R.id.tvDay5);
        tvDay6 = findViewById(R.id.tvDay6);
        tvDay7 = findViewById(R.id.tvDay7);
        headerContainer = findViewById(R.id.headerContainer);

        headerContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new CustomAlert().showWeeklyAlert(HomeActivity.this, weekCount);
            }
        });



        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        ivMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    drawer.openDrawer(GravityCompat.START);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        Typeface type = Typeface.createFromAsset(getAssets(), "fonts/Biko_Bold.otf");
        tvTopHeading.setTypeface(type);
        Typeface type1 = Typeface.createFromAsset(getAssets(), "fonts/Biko_Regular.otf");
        tvQuote.setTypeface(type1);
    }


    /**
     * Home screen background color change logic
     * Week tab logic for weekarrows and weekcount
     *
     * @param dayCount
     */
    private void populateViews(int dayCount) {
        AssetManager assetManager = getAssets();
        try {
            InputStream stream = null;

            stream = assetManager.open("days/day" + dayCount + ".plist");

            PListDict dict = PListParser.parse(stream);

            int day = dict.getInt("day");
            if (isWeektab) {
                if (weekCount == 0) {
                    weekCount = dict.getInt("week");
                }
            } else {
                weekCount = dict.getInt("week");
            }

            if (isWeektab) {
                if (weekCounter == 0) {
                    weekCounter = dict.getInt("week");
                }
            } else {
                weekCounter = dict.getInt("week");
            }

            int dayOfWeek = dict.getInt("dayOfWeek");

            if (isWeektab) {


                if (TextUtils.isEmpty(action)) {

                } else {
                    if (action.equalsIgnoreCase("+")) {
                        weekCount++;
                    } else if (action.equalsIgnoreCase("-")) {
                        weekCount--;
                    }
                }
                if (weekCount == 1) {
                    ivPrevious.setVisibility(View.GONE);
                } else {
                    ivPrevious.setVisibility(View.VISIBLE);
                }

                if (weekCount >= Math.ceil((double) Constants.MY_OMER_DAYS_COUNT / 7.0)) {
                    ivNext.setVisibility(View.GONE);
                } else {
                    ivNext.setVisibility(View.VISIBLE);
                }
//                GlobalBus.getBus().postSticky(new Events.DayChangeEvent(weekCount));

            }

            InputStream is = null;

            is = assetManager.open("weeks/week" + weekCount + ".plist");

            PListDict dict2 = PListParser.parse(is);
            String color = dict2.getString("color");
            String title = dict2.getString("title");
            String subTitle = dict2.getString("subtitle");
            if (weekCount == 1) {


                tvTopHeading.setText(title);
                tvQuote.setText(subTitle);
                llParentPanel.setBackgroundColor(Color.parseColor("#" + color));

            } else if (weekCount == 2) {


                tvTopHeading.setText(title);
                tvQuote.setText(subTitle);
                llParentPanel.setBackgroundColor(Color.parseColor("#" + color));

            } else if (weekCount == 3) {


                tvTopHeading.setText(title);
                tvQuote.setText(subTitle);
                llParentPanel.setBackgroundColor(Color.parseColor("#" + color));

            } else if (weekCount == 4) {


                tvTopHeading.setText(title);
                tvQuote.setText(subTitle);
                llParentPanel.setBackgroundColor(Color.parseColor("#" + color));

            } else if (weekCount == 5) {


                tvTopHeading.setText(title);
                tvQuote.setText(subTitle);
                llParentPanel.setBackgroundColor(Color.parseColor("#" + color));

            } else if (weekCount == 6) {


                tvTopHeading.setText(title);
                tvQuote.setText(subTitle);
                llParentPanel.setBackgroundColor(Color.parseColor("#" + color));

            } else if (weekCount == 7) {


                tvTopHeading.setText(title);
                tvQuote.setText(subTitle);
                llParentPanel.setBackgroundColor(Color.parseColor("#" + color));

            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                try {
                    Window window = getWindow();
                    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                    window.setStatusBarColor(Color.parseColor("#" + color));
//                    window.setNavigationBarColor(Color.parseColor("#" + color));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            int currentYear = Utilty.getYear(new Date());
            MyOmerPeriod myOmerPeriod = RealmController.with(this).getPeriodByYear(currentYear);
            Date startDateOfOmer = myOmerPeriod.getStartDate();
            Date currentDate = new Date();

            if (Days.daysBetween(new LocalDate(startDateOfOmer), new LocalDate(currentDate)).getDays() > 0) {
                InputStream inputStream = null;

                inputStream = assetManager.open("weeks/week" + 1 + ".plist");

                PListDict dict3 = PListParser.parse(inputStream);
                String col = dict3.getString("color");
                tvDay1.setBackgroundColor(Color.parseColor("#" + col));
                tvDay1.setTextColor(Color.parseColor("#ffffff"));
                tvDay1.setEnabled(true);
            }
            if (Days.daysBetween(new LocalDate(startDateOfOmer), new LocalDate(currentDate)).getDays() > 7) {
                InputStream inputStream = null;

                inputStream = assetManager.open("weeks/week" + 2 + ".plist");

                PListDict dict3 = PListParser.parse(inputStream);
                String col = dict3.getString("color");
                tvDay2.setBackgroundColor(Color.parseColor("#" + col));
                tvDay2.setTextColor(Color.parseColor("#ffffff"));
                tvDay2.setEnabled(true);
            }
            if (Days.daysBetween(new LocalDate(startDateOfOmer), new LocalDate(currentDate)).getDays() > 14) {
                InputStream inputStream = null;

                inputStream = assetManager.open("weeks/week" + 3 + ".plist");

                PListDict dict3 = PListParser.parse(inputStream);
                String col = dict3.getString("color");
                tvDay3.setBackgroundColor(Color.parseColor("#" + col));
                tvDay3.setTextColor(Color.parseColor("#ffffff"));
                tvDay3.setEnabled(true);
            }
            if (Days.daysBetween(new LocalDate(startDateOfOmer), new LocalDate(currentDate)).getDays() > 21) {
                InputStream inputStream = null;

                inputStream = assetManager.open("weeks/week" + 4 + ".plist");

                PListDict dict3 = PListParser.parse(inputStream);
                String col = dict3.getString("color");
                tvDay4.setBackgroundColor(Color.parseColor("#" + col));
                tvDay4.setTextColor(Color.parseColor("#ffffff"));
                tvDay4.setEnabled(true);
            }
            if (Days.daysBetween(new LocalDate(startDateOfOmer), new LocalDate(currentDate)).getDays() > 28) {
                InputStream inputStream = null;

                inputStream = assetManager.open("weeks/week" + 5 + ".plist");

                PListDict dict3 = PListParser.parse(inputStream);
                String col = dict3.getString("color");
                tvDay5.setBackgroundColor(Color.parseColor("#" + col));
                tvDay5.setTextColor(Color.parseColor("#ffffff"));
                tvDay5.setEnabled(true);
            }
            if (Days.daysBetween(new LocalDate(startDateOfOmer), new LocalDate(currentDate)).getDays() > 35) {
                InputStream inputStream = null;

                inputStream = assetManager.open("weeks/week" + 6 + ".plist");

                PListDict dict3 = PListParser.parse(inputStream);
                String col = dict3.getString("color");
                tvDay6.setBackgroundColor(Color.parseColor("#" + col));
                tvDay6.setTextColor(Color.parseColor("#ffffff"));
                tvDay6.setEnabled(true);
            }
            if (Days.daysBetween(new LocalDate(startDateOfOmer), new LocalDate(currentDate)).getDays() > 42) {
                InputStream inputStream = null;

                inputStream = assetManager.open("weeks/week" + 7 + ".plist");

                PListDict dict3 = PListParser.parse(inputStream);
                String col = dict3.getString("color");
                tvDay7.setBackgroundColor(Color.parseColor("#" + col));
                tvDay7.setTextColor(Color.parseColor("#ffffff"));
                tvDay7.setEnabled(true);
            }

            updateBottomviewcolor();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (PListException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        HomeActivity.this.finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Register this fragment to listen to event.

    }


    @Override
    protected void onStop() {
        super.onStop();
        GlobalBus.getBus().unregister(this);
    }


    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();


        if (id == R.id.omer_chart) {
            startActivity(new Intent(HomeActivity.this, com.myomer.myomer.activities.OmerChartActivity.class));
        } else if (id == R.id.settings) {
            startActivity(new Intent(HomeActivity.this, com.myomer.myomer.activities.SettingsActivity.class));
        } else if (id == R.id.goToBook) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.BOOK_URL)));
        } else if (id == R.id.donate) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.DONATE_URL)));
        } else if (id == R.id.techSupport) {
            // Sending to admin
            Intent testIntent = new Intent(Intent.ACTION_VIEW);
            Uri data = Uri.parse("mailto:?subject=" + Constants.TECH_SUPPORT_SUBJECT + "&body=" + Constants.TECH_SUPPORT_BODY + "&to=" + Constants.TECH_SUPPORT_EMAIL);
            testIntent.setData(data);
            startActivity(testIntent);
        } else if (id == R.id.whatIsOmer) {
            startActivity(new Intent(HomeActivity.this, com.myomer.myomer.activities.AboutUsActivity.class));
        } else if (id == R.id.nav_share) {
            Intent sharingIntent = new Intent(Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            sharingIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
//            String text = getString(R.string.download_now)
//                    + "https://www.meaningfullife.com/myomer";
            String text = getString(R.string.download_now)
                    + "http://bit.ly/myomer";
            sharingIntent.putExtra(Intent.EXTRA_TEXT, text);
            startActivity(Intent.createChooser(sharingIntent, "Share"));
        } else if (id == R.id.exportjournals) {
            if(drawer!=null) {
                drawer.closeDrawer(GravityCompat.START);
            }
            new Gota.Builder(this)
                    .withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .requestId(101)
                    .setListener(HomeActivity.this)
                    .check();

        }
        return true;
    }

    @OnClick({R.id.tvDay1, R.id.tvDay2, R.id.tvDay3, R.id.tvDay4, R.id.tvDay5, R.id.tvDay6, R.id.tvDay7})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tvDay1:
                weekSelection(tvDay1.isEnabled(), Integer.parseInt(tvDay1.getText().toString()));
                break;
            case R.id.tvDay2:
                weekSelection(tvDay2.isEnabled(), Integer.parseInt(tvDay2.getText().toString()));
                break;
            case R.id.tvDay3:
                weekSelection(tvDay3.isEnabled(), Integer.parseInt(tvDay3.getText().toString()));
                break;
            case R.id.tvDay4:
                weekSelection(tvDay4.isEnabled(), Integer.parseInt(tvDay4.getText().toString()));
                break;
            case R.id.tvDay5:
                weekSelection(tvDay5.isEnabled(), Integer.parseInt(tvDay5.getText().toString()));
                break;
            case R.id.tvDay6:
                weekSelection(tvDay6.isEnabled(), Integer.parseInt(tvDay6.getText().toString()));
                break;
            case R.id.tvDay7:
                weekSelection(tvDay7.isEnabled(), Integer.parseInt(tvDay7.getText().toString()));
                break;
        }
    }

    private void weekSelection(boolean enabled, int week) {
        if (enabled) {
            action = "";
            weekCount = week;

            new CustomAlert().showWeeklyAlert(HomeActivity.this,weekCount);
           /* if (weekCount == 1) {
                count = 1;
            } else if (weekCount == 2) {
                count = 8;
            } else if (weekCount == 3) {
                count = 15;
            } else if (weekCount == 4) {
                count = 22;
            } else if (weekCount == 5) {
                count = 29;
            } else if (weekCount == 6) {
                count = 36;
            } else if (weekCount == 7) {
                count = 43;
            }

            refreshArrows();
            populateViews(count);

            if (currenttab != 1) {
                buttonGroup.setPosition(1, true);
                buttonGroup.setClickable(false);
                isWeektab = true;
                currenttab = 1;
                weekFragment = new WeekFragment();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Utilty.replaceFragment(HomeActivity.this, weekFragment, R.id.frame_container);
                        GlobalBus.getBus().postSticky(new Events.DayChangeEvent(weekCount));
                    }
                }, 500);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        buttonGroup.setClickable(true);
                    }
                }, 600);
            } else {
                weekFragment.movetoNoAnimation(weekCount);
            }*/


        }
    }

    @Override
    public void onRequestBack(int requestId, @NonNull GotaResponse gotaResponse) {
        if(requestId==101 && gotaResponse.isAllGranted()){
            try {
                File root= new File(Environment.getExternalStorageDirectory()+"/myOmer");
                if(!root.exists()){
                    root.mkdir();
                }
                RealmResults<JournalQuestionModelNew> alljournals = RealmController.with((Activity) HomeActivity.this).getAllFilledJournals();
                alljournals = alljournals.sort("id", Sort.ASCENDING);
                String data="";
                for (int i = 0; i <alljournals.size() ; i++) {
                    if(StringUtils.isNotEmpty(alljournals.get(i).getAnswer())){
                        data=data+"Day: "+alljournals.get(i).getId()+"\nQuestion: "+alljournals.get(i).getQuestion()+"\nAnswer: "+ alljournals.get(i).getAnswer()+"\n\n"/*Answered on: "+alljournals.get(i).getDate()+", "+alljournals.get(i).getTime()*/;
                    }
                }

                String yearInString = String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
                File exportfile=new File(root.getAbsolutePath()+ "/myomer_journals_"+yearInString+".txt");
                FileOutputStream f = new FileOutputStream(exportfile);
                PrintWriter pw = new PrintWriter(f);
                pw.println(data);
                pw.flush();
                pw.close();
                f.close();

                Toast.makeText(HomeActivity.this, "Export Successful!\n\n"+exportfile.getAbsolutePath(), Toast.LENGTH_SHORT).show();

                Uri uri = FileProvider.getUriForFile(this, "com.myomer.myomer.fileprovider", exportfile);
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("*/*");
                sharingIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                sharingIntent.putExtra(Intent.EXTRA_STREAM, uri);
                startActivity(sharingIntent);

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }


}
