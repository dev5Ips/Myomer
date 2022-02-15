package com.myomer.myomer.ui.activity;

import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.myomer.myomer.R;
import com.myomer.myomer.data.local.models.MyOmerPeriod;
import com.myomer.myomer.util.helper.SharedPreferenceHelper;
import com.myomer.myomer.util.oldProjectFiles.Constants;
import com.myomer.myomer.util.oldProjectFiles.Utilty;
import com.myomer.myomer.util.realm.RealmController;

import org.joda.time.Days;
import org.joda.time.LocalDate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class WaitingScreenActivity extends AppCompatActivity {

    public String startDateOfOmer = "31/03/2018";
    TextView tvDaysTillOmer, tvDaysTillOmerLabel;
    int currentYear;
    long daysTillOmer = 0;
    TextView tvBeginOn, tvBegin;
    AppCompatButton btnGetStarted, btnWhatIsOmer;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_waiting_screen);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        );
        initViews();
        currentYear = Utilty.getYear(new Date());
        MyOmerPeriod myOmerPeriod = RealmController.with(this).getPeriodByYear(currentYear);


        SimpleDateFormat format = new SimpleDateFormat("EEEE 'night', MMMM dd, yyyy"/*, Locale.getDefault()*/);
    //    format.setTimeZone(TimeZone.getTimeZone("GMT"));
        startDateOfOmer = format.format(myOmerPeriod.getStartDate());

        /**
         * Show starting data of ommer
         * Added 1 day on starting date because in constantfile starting day is day-1 and here need to display exact day
         * Do not change in constantfile it is working ok in whole app logic
         */
        Date displayStartDate = myOmerPeriod.getStartDate();//new Date(startDateOfOmer);
        Calendar displayStartcal = Calendar.getInstance();
        displayStartcal.setTime(displayStartDate);
        displayStartcal.set(Calendar.DAY_OF_MONTH,displayStartcal.get(Calendar.DAY_OF_MONTH)+1);
        tvBeginOn.setText(format.format(displayStartcal.getTime()));

        // Convert from String to Date
        Date startDate = null;//new Date(startDateOfOmer);
        try {
            startDate = format.parse(startDateOfOmer);
        } catch (ParseException e) {
            e.printStackTrace();
        }
//        Date currentDate = new Date();
//        currentDate.setHours(Calendar.HOUR_OF_DAY,0);
//        currentDate.setMinutes(0);
//        currentDate.setSeconds(0);

        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.HOUR, 0);
        cal.add(Calendar.MINUTE,0);
        cal.add(Calendar.SECOND,0);
        Date currentDate = cal.getTime();

//        daysTillOmer = Utilty.getDaysTillOmer(currentDate,startDate);
        daysTillOmer = Days.daysBetween(new LocalDate(currentDate),new LocalDate(startDate)).getDays();


        // new nightfall logic +1 day
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        int minutes = Calendar.getInstance().get(Calendar.MINUTE);
        int nightFallHours = SharedPreferenceHelper.getSharedPreferenceInt(this,"NightfallHour",0);
        int nightFallMinutes = SharedPreferenceHelper.getSharedPreferenceInt(this,"NightfallMin",0);

        if(hour>=nightFallHours && minutes>=nightFallMinutes){
            daysTillOmer=daysTillOmer-1;
        }


        tvDaysTillOmer.setText(daysTillOmer+"");

/*

//            daysTillOmer = 0;

         if (daysTillOmer <= 0){
             if (String.valueOf(daysTillOmer).contains("-")){
                 daysTillOmer = Integer.parseInt(String.valueOf(daysTillOmer).replace("-",""));
                 if (daysTillOmer >= 49) {
                     myOmerPeriod = RealmController.with(this).getPeriodByYear(currentYear + 1);
                     daysTillOmer = Utilty.getDaysTillOmer(currentDate, myOmerPeriod.getStartDate());
                     startDateOfOmer = format.format(myOmerPeriod.getStartDate());
                     tvBeginOn.setText(startDateOfOmer);
                     tvDaysTillOmer.setText(String.valueOf(daysTillOmer));
//                     startActivity(new Intent(WaitingScreenActivity.this, HomeActivity.class));
//                     finish();
                 }else {
                     startActivity(new Intent(WaitingScreenActivity.this, HomeActivity.class));
                     finish();
                 }
             }else {
                 startActivity(new Intent(WaitingScreenActivity.this, HomeActivity.class));
                 finish();
             }
         }
//         else {
//             startActivity(new Intent(WaitingScreenActivity.this, HomeActivity.class));
//             finish();
//         }
*/



        if (daysTillOmer < 0){
            if (String.valueOf(daysTillOmer).contains("-")){
                daysTillOmer = Integer.parseInt(String.valueOf(daysTillOmer).replace("-",""));
                if (daysTillOmer > 49) {
                    myOmerPeriod = RealmController.with(this).getPeriodByYear(currentYear+1);
                    daysTillOmer = Utilty.getDaysTillOmer(currentDate, myOmerPeriod.getStartDate());
                    startDateOfOmer = format.format(myOmerPeriod.getStartDate());
//                    daysTillOmer=daysTillOmer+1;//for display count porpose
//                    Date date = myOmerPeriod.getStartDate();
//                    Calendar c = Calendar.getInstance();
//                    c.setTime(date);
//                    c.add(Calendar.DATE, 1);
//                    tvBeginOn.setText(format.format(c.getTime()));
            tvBeginOn.setText(startDateOfOmer);
                    tvDaysTillOmer.setText(String.valueOf(daysTillOmer));
                }else {
                    startActivity(new Intent(WaitingScreenActivity.this, HomeActivity.class));
                    finish();
                }
            }else {
                startActivity(new Intent(WaitingScreenActivity.this, HomeActivity.class));
                finish();
            }
        }else {
//            myOmerPeriod = RealmController.with(this).getPeriodByYear(currentYear);
//                    daysTillOmer = Utilty.getDaysTillOmer(currentDate, myOmerPeriod.getStartDate());
            daysTillOmer=daysTillOmer+1;//for display count porpose
//            Date date = myOmerPeriod.getStartDate();
//            Calendar c = Calendar.getInstance();
//            c.setTime(date);
//            c.add(Calendar.DATE, 1);
//            tvBeginOn.setText(format.format(c.getTime()));
            tvBeginOn.setText(startDateOfOmer);
            tvDaysTillOmer.setText(String.valueOf(daysTillOmer));
        }
    }

    private void initViews(){
        tvDaysTillOmer = (TextView) findViewById(R.id.tvDaysTillOmer);
        tvBeginOn = (TextView) findViewById(R.id.tvBeginOn);

        tvDaysTillOmerLabel = (TextView) findViewById(R.id.tvDaysTillOmerLabel);
        tvBegin = (TextView) findViewById(R.id.tvBegin);

        btnGetStarted = (AppCompatButton) findViewById(R.id.btnGetStarted);
        btnWhatIsOmer = (AppCompatButton) findViewById(R.id.btnWhatIsOmer);

        Typeface type1 = Typeface.createFromAsset(getAssets(), "fonts/gillsans_light.otf");
        tvDaysTillOmer.setTypeface(type1);

        Typeface type2 = Typeface.createFromAsset(getAssets(), "fonts/gillsans_regular.otf");
        tvBegin.setTypeface(type2);
        tvDaysTillOmerLabel.setTypeface(type2);

        tvBeginOn.setTypeface(type2);
        btnGetStarted.setTypeface(type2);
        btnWhatIsOmer.setTypeface(type2);

    }

    public void getStarted(View view) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.BOOK_URL)));
    }

    public void aboutUs(View view) {
        startActivity(new Intent(WaitingScreenActivity.this,AboutUsActivity.class));
    }
}
