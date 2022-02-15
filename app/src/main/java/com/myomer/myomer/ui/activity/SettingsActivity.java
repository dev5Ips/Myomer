package com.myomer.myomer.ui.activity;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;

import com.luckycatlabs.sunrisesunset.SunriseSunsetCalculator;
import com.luckycatlabs.sunrisesunset.dto.Location;
import com.myomer.myomer.R;
import com.myomer.myomer.receiver.AlarmReceiver;
import com.myomer.myomer.util.helper.SharedPreferenceHelper;
import com.myomer.myomer.util.oldProjectFiles.Constants;
import com.myomer.myomer.util.oldProjectFiles.Utilty;
import com.myomer.myomer.util.oldProjectFiles.gota.Gota;
import com.myomer.myomer.util.oldProjectFiles.gota.GotaResponse;

import org.ankit.gpslibrary.MyTracker;

import java.util.Calendar;
import java.util.TimeZone;

public class SettingsActivity extends AppCompatActivity implements Gota.OnRequestPermissionsBack {
    Toolbar toolbar;
    SwitchCompat nightlySwitch,dailySwitch,switchNotifications;
    private int CalendarHour, CalendarMinute;
    String format;
    Calendar calendar;
    LinearLayout llSendEmail;
    TimePickerDialog timepickerdialog;
    TextView tvNightlyModeTime,tvDayModeTime,tvNightFallTime;
    final static int RQS_1 = 1;
    final static int RQS_2 = 2;
    private static final int DYNAMICLOCATION = 9999;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_settings);
        initToolBar();
        nightlySwitch =(SwitchCompat) findViewById(R.id.nightlySwitch);
        switchNotifications =(SwitchCompat) findViewById(R.id.switchNotifications);
        dailySwitch =(SwitchCompat) findViewById(R.id.dailySwitch);
        tvNightlyModeTime = (TextView) findViewById(R.id.nightlyModeTime);
        tvDayModeTime = (TextView) findViewById(R.id.dailyModeTime);
        tvNightFallTime = (TextView) findViewById(R.id.tvNightFallTime);
        llSendEmail = (LinearLayout) findViewById(R.id.sendEmail);

        /*RealmResults<JournalQuestionModelNew> journalQuestionModelNews = RealmController.with(this).getAllFilledJournals();
        for (int i = 0; i <journalQuestionModelNews.size() ; i++) {
            Log.e("TAG", "onCreate: "+journalQuestionModelNews.get(i).getQuestion()+":\n"+journalQuestionModelNews.get(i).getAnswer()+"\n"+
                    journalQuestionModelNews.get(i).getDate()+"  "+journalQuestionModelNews.get(i).getTime());
        }*/
        /**
         * General notification switch to enable or disable all notifications
         */
        if(SharedPreferenceHelper.getSharedPreferenceBoolean(this, Constants.ENABLE_NOTIFICATIONS,true)){
            switchNotifications.setChecked(true);
        }else {
            switchNotifications.setChecked(false);
        }
        switchNotifications.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(!isChecked) {
                    Utilty.resetNotifications(SettingsActivity.this);
                }
                SharedPreferenceHelper.setSharedPreferenceBoolean(SettingsActivity.this,Constants.ENABLE_NOTIFICATIONS,isChecked);

            }
        });

        llSendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent testIntent = new Intent(Intent.ACTION_VIEW);
                Uri data = Uri.parse("mailto:?to=" + Constants.TECH_SUPPORT_EMAIL);
                testIntent.setData(data);
                startActivity(testIntent);
            }
        });
        boolean DailyAlarmEnabled = SharedPreferenceHelper.getSharedPreferenceBoolean(this, "DailyAlarmEnabled", false);
        boolean NightAlarmEnabled = SharedPreferenceHelper.getSharedPreferenceBoolean(this, "NightAlarmEnabled", false);
        boolean NightFallEnabled = SharedPreferenceHelper.getSharedPreferenceBoolean(this, "NightFallEnabled", false);


        if (DailyAlarmEnabled == true){
            dailySwitch.setChecked(true);
            int hour =  SharedPreferenceHelper.getSharedPreferenceInt(this,"DailyAlarmHour",0);
            int min = SharedPreferenceHelper.getSharedPreferenceInt(this,"DailyAlarmMin",0);


                if (hour == 0) {

                    hour += 12;

                    format = "AM";
                }
                else if (hour == 12) {

                    format = "PM";

                }
                else if (hour > 12) {

                    hour -= 12;

                    format = "PM";

                }
                else {

                    format = "AM";
                }


//                tvDayModeTime.setText(hour + ":" + min +"  " +format);
            tvDayModeTime.setText(setTwoDigit(String.valueOf(hour)) + ":" + setTwoDigit(String.valueOf(min)) +"  " +format);


        }else {
            tvDayModeTime.setText("Enable alarm");
        }
        if (NightAlarmEnabled == true){
            nightlySwitch.setChecked(true);
            int hour =  SharedPreferenceHelper.getSharedPreferenceInt(this,"NightAlarmHour",0);
            int min = SharedPreferenceHelper.getSharedPreferenceInt(this,"NightAlarmMin",0);


                if (hour == 0) {

                    hour += 12;

                    format = "AM";
                }
                else if (hour == 12) {

                    format = "PM";

                }
                else if (hour > 12) {

                    hour -= 12;

                    format = "PM";

                }
                else {

                    format = "AM";
                }


//                tvNightlyModeTime.setText(hour + ":" + min +"  " +format);
            tvNightlyModeTime.setText(setTwoDigit(String.valueOf(hour)) + ":" + setTwoDigit(String.valueOf(min)) +"  " +format);


        }
        if (NightFallEnabled == true){
            int hour =  SharedPreferenceHelper.getSharedPreferenceInt(this,"NightfallHour",0);
            int min = SharedPreferenceHelper.getSharedPreferenceInt(this,"NightfallMin",0);
            String formatt = SharedPreferenceHelper.getSharedPreferenceString(this,"NightfallFormat","");

            if (hour == 0) {

                hour += 12;
                format = "AM";
            } else if (hour == 24) {

                format = "AM";
                hour -= 12;
            } else if (hour > 12) {

                hour -= 12;

                format = "PM";

            } else if(hour < 12){
                format = "AM";
            }


//            tvNightFallTime.setText(hour + ":" + min +"  " +formatt);
            tvNightFallTime.setText(setTwoDigit(String.valueOf(hour)) + ":" + setTwoDigit(String.valueOf(min)) +"  " +formatt);


        }

        nightlySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    calendar = Calendar.getInstance();
                    CalendarHour = calendar.get(Calendar.HOUR_OF_DAY);
                    CalendarMinute = calendar.get(Calendar.MINUTE);


                    timepickerdialog = new TimePickerDialog(SettingsActivity.this,
                            new TimePickerDialog.OnTimeSetListener() {

                                @Override
                                public void onTimeSet(TimePicker view, int hourOfDay,
                                                      int minute) {
                                    int hOD = hourOfDay;
                                    if (hourOfDay == 0) {

                                        hourOfDay += 12;

                                        format = "AM";
                                    }
                                    else if (hourOfDay == 12) {

                                        format = "PM";

                                    }
                                    else if (hourOfDay > 12) {

                                        hourOfDay -= 12;

                                        format = "PM";

                                    }
                                    else {

                                        format = "AM";
                                    }


//                                    tvNightlyModeTime.setText(hourOfDay + ":" + minute +"  " +format);
                                    tvNightlyModeTime.setText(setTwoDigit(String.valueOf(hourOfDay)) + ":" + setTwoDigit(String.valueOf(minute)) +"  " +format);

                                    Calendar calNow = Calendar.getInstance();
                                    calNow.setTimeInMillis(System.currentTimeMillis());
                                    Calendar calSet = (Calendar) calNow.clone();
                                    calSet.setTimeInMillis(System.currentTimeMillis());
                                    calSet.set(Calendar.HOUR_OF_DAY, hOD);
                                    calSet.set(Calendar.MINUTE, minute);
                                    calSet.set(Calendar.SECOND, 0);
                                    calSet.set(Calendar.MILLISECOND, 0);

                                    if (calSet.compareTo(calNow) <= 0) {
                                        // Today Set time passed, count to tomorrow
                                        calSet.add(Calendar.DATE, 1);
                                    }
                                    SharedPreferenceHelper.setSharedPreferenceInt(SettingsActivity.this,"NightAlarmHour",hOD);
                                    SharedPreferenceHelper.setSharedPreferenceInt(SettingsActivity.this,"NightAlarmMin",minute);
                                    SharedPreferenceHelper.setSharedPreferenceString(SettingsActivity.this,"NightAlarmFormat",format);
                                    SharedPreferenceHelper.setSharedPreferenceBoolean(SettingsActivity.this,"NightAlarmEnabled",true);
                                    cancelAlarm(RQS_1);
                                    setAlarm(calSet,RQS_1);
                                }
                            }, CalendarHour, CalendarMinute, false);
                    timepickerdialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialogInterface) {
                            cancelAlarm(RQS_1);
                            SharedPreferenceHelper.setSharedPreferenceBoolean(SettingsActivity.this,"NightAlarmEnabled",true);
                            nightlySwitch.setChecked(false);
                        }
                    });
                    timepickerdialog.show();
                }else {
                    cancelAlarm(RQS_1);
                    SharedPreferenceHelper.setSharedPreferenceBoolean(SettingsActivity.this,"NightAlarmEnabled",true);
                    nightlySwitch.setChecked(false);
                }
            }
        });
        dailySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){


                    timepickerdialog = new TimePickerDialog(SettingsActivity.this,
                            new TimePickerDialog.OnTimeSetListener() {

                                @Override
                                public void onTimeSet(TimePicker view, int hourOfDay,
                                                      int minute) {
                                    int hOD = hourOfDay;
                                    if (hourOfDay == 0) {

                                        hourOfDay += 12;

                                        format = "AM";
                                    }
                                    else if (hourOfDay == 12) {

                                        format = "PM";

                                    }
                                    else if (hourOfDay > 12) {

                                        hourOfDay -= 12;

                                        format = "PM";

                                    }
                                    else {

                                        format = "AM";
                                    }


//                                    tvDayModeTime.setText(hourOfDay + ":" + minute +"  " +format);
                                    tvDayModeTime.setText(setTwoDigit(String.valueOf(hourOfDay)) + ":" + setTwoDigit(String.valueOf(minute)) +"  " +format);

                                    Calendar calNow = Calendar.getInstance();
                                    calNow.setTimeInMillis(System.currentTimeMillis());
                                    Calendar calSet = (Calendar) calNow.clone();
                                    calSet.setTimeInMillis(System.currentTimeMillis());
                                    calSet.set(Calendar.HOUR_OF_DAY, hOD);
                                    calSet.set(Calendar.MINUTE, minute);
                                    calSet.set(Calendar.SECOND, 0);
                                    calSet.set(Calendar.MILLISECOND, 0);

                                    if (calSet.compareTo(calNow) <= 0) {
                                        // Today Set time passed, count to tomorrow
                                        calSet.add(Calendar.DATE, 1);
                                    }


                                    SharedPreferenceHelper.setSharedPreferenceInt(SettingsActivity.this,"DailyAlarmHour",hOD);
                                    SharedPreferenceHelper.setSharedPreferenceInt(SettingsActivity.this,"DailyAlarmMin",minute);
                                    SharedPreferenceHelper.setSharedPreferenceString(SettingsActivity.this,"DailyAlarmFormat",format);

                                    SharedPreferenceHelper.setSharedPreferenceBoolean(SettingsActivity.this,"DailyAlarmEnabled",true);
                                    cancelAlarm(RQS_2);
                                    setAlarm(calSet,RQS_2);
                                }
                            }, 24, 0, false);
                    timepickerdialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialogInterface) {
                            cancelAlarm(RQS_2);
                            SharedPreferenceHelper.setSharedPreferenceBoolean(SettingsActivity.this,"DailyAlarmEnabled",false);
                            dailySwitch.setChecked(false);
                        }
                    });
                    timepickerdialog.show();
                }else {
                    cancelAlarm(RQS_2);
                    SharedPreferenceHelper.setSharedPreferenceBoolean(SettingsActivity.this,"DailyAlarmEnabled",false);
                    dailySwitch.setChecked(false);
                }
            }
        });




        findViewById(R.id.llNightFall).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calendar = Calendar.getInstance();
                CalendarHour = calendar.get(Calendar.HOUR_OF_DAY);
                CalendarMinute = calendar.get(Calendar.MINUTE);
                timepickerdialog = new TimePickerDialog(SettingsActivity.this,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {
                        int hOD = hourOfDay;
                        if (hourOfDay == 0) {

                            hourOfDay += 12;

                            format = "AM";
                        } else if (hourOfDay == 12) {

                            format = "PM";

                        } else if (hourOfDay > 12) {

                            hourOfDay -= 12;

                            format = "PM";

                        } else {

                            format = "AM";
                        }


//                        tvNightFallTime.setText(hourOfDay + ":" + minute + "  " + format);
                        tvNightFallTime.setText(setTwoDigit(String.valueOf(hourOfDay)) + ":" + setTwoDigit(String.valueOf(minute)) + "  " + format);


                        SharedPreferenceHelper.setSharedPreferenceInt(SettingsActivity.this, "NightfallHour", hOD);
                        SharedPreferenceHelper.setSharedPreferenceInt(SettingsActivity.this, "NightfallMin", minute);
                        SharedPreferenceHelper.setSharedPreferenceString(SettingsActivity.this, "NightfallFormat", format);
                        SharedPreferenceHelper.setSharedPreferenceBoolean(SettingsActivity.this, "NightFallEnabled", true);

                    }

                }, CalendarHour, CalendarMinute, false);
                timepickerdialog.show();
            }
        });
        findViewById(R.id.ivLocation).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Gota.Builder(SettingsActivity.this)
                        .withPermissions(Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION)
                        .requestId(1)
                        .setListener(SettingsActivity.this)
                        .check();

            }
        });
    }

    public static String setTwoDigit(String val) {
        if (val.length() == 1)
            return "0" + val;
        return val;
    }

    private void getDynamictime() {
        MyTracker tracker=new MyTracker(SettingsActivity.this);
        if(tracker!=null && tracker.getLatitude()==0 && tracker.getLongitude()==0){
            notUsable(true);
            return;
        }
        Location location = new Location(tracker.getLatitude()+"", tracker.getLongitude()+"");
        SunriseSunsetCalculator calculator = new SunriseSunsetCalculator(location, TimeZone.getDefault());
        Calendar c = calculator.getOfficialSunsetCalendarForDate(Calendar.getInstance());

        int hourOfDay =c.get(Calendar.HOUR);
        int minute = c.get(Calendar.MINUTE);
        int AM_PM = c.get(Calendar.AM_PM);

        if (AM_PM == Calendar.AM){
            format = "AM";
        }else {
            format = "PM";
        }
//                if (hourOfDay == 0) {
//
//                    hourOfDay += 12;
//
//                    format = "AM";
//                } else if (hourOfDay == 12) {
//
//                    format = "PM";
//
//                } else if (hourOfDay > 12) {
//
//                    hourOfDay -= 12;
//
//                    format = "PM";
//
//                } else {
//
//                    format = "AM";
//                }

//        tvNightFallTime.setText( hourOfDay+ ":" + minute + "  " + format);
        tvNightFallTime.setText( setTwoDigit(String.valueOf(hourOfDay))+ ":" + setTwoDigit(String.valueOf(minute)) + "  " + format);

        SharedPreferenceHelper.setSharedPreferenceInt(SettingsActivity.this, "NightfallHour", c.get(Calendar.HOUR));
        SharedPreferenceHelper.setSharedPreferenceInt(SettingsActivity.this, "NightfallMin", c.get(Calendar.MINUTE));
        SharedPreferenceHelper.setSharedPreferenceString(SettingsActivity.this, "NightfallFormat", format);
        SharedPreferenceHelper.setSharedPreferenceBoolean(SettingsActivity.this, "NightFallEnabled", true);
    }

    private void setAlarm(Calendar targetCal, int reqCode) {

                    long milis = targetCal.getTimeInMillis();

                    Intent intent = new Intent(getBaseContext(), AlarmReceiver.class);
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(
                            getApplicationContext(), reqCode, intent, 0);
                    AlarmManager alarmManager = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
                    int ALARM_TYPE = AlarmManager.RTC_WAKEUP;

        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, milis,
                AlarmManager.INTERVAL_DAY, pendingIntent);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
//                        alarmManager.setExactAndAllowWhileIdle(ALARM_TYPE, milis, pendingIntent);
//                    else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
//                        alarmManager.setExact(ALARM_TYPE, milis, pendingIntent);
//                    else
//                        alarmManager.set(ALARM_TYPE, milis, pendingIntent);
    }




    private void cancelAlarm( int reqCode) {


        Intent intent = new Intent(getBaseContext(), AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                getApplicationContext(), reqCode, intent, 0);
        AlarmManager alarmManager = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);


    }
    public void initToolBar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar1);
        toolbar.setTitle("Settings");
        toolbar.setTitleTextColor(Color.BLACK);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //setSupportActionBartoolbar.setNavigationIcon(android.);
        toolbar.setNavigationOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SettingsActivity.this.finish();
                    }
                }

        );
    }

    @Override
    public void onRequestBack(int requestId, @NonNull GotaResponse gotaResponse) {
        if(gotaResponse.isAllGranted()){
//            enablegpsprogrametically();
            getDynamictime();

        }else {
            notUsable(false);
        }
    }

    private void notUsable(boolean haspermission) {
        if(haspermission){
            Toast.makeText(SettingsActivity.this, getString(R.string.enable_gps_accurracy), Toast.LENGTH_LONG).show();
        }else {
            Toast.makeText(SettingsActivity.this, getString(R.string.enable_gps), Toast.LENGTH_LONG).show();

        }

    }


    /*public void enablegpsprogrametically() {

            LocationRequest locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                    .addLocationRequest(locationRequest);
            builder.setAlwaysShow(true);
            Task<LocationSettingsResponse> result =
                    LocationServices.getSettingsClient(SettingsActivity.this).checkLocationSettings(builder.build());
            result.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
                @Override
                public void onComplete(Task<LocationSettingsResponse> task) {
                    try {
                        LocationSettingsResponse response = task.getResult(ApiException.class);
                        if (response.getLocationSettingsStates().isGpsUsable()) {
                            getDynamictime();
                        } else {
                            notUsable(true);
                        }
                    } catch (ApiException exception) {
                        switch (exception.getStatusCode()) {
                            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                try {
                                    ResolvableApiException resolvable = (ResolvableApiException) exception;
                                    resolvable.startResolutionForResult(
                                            SettingsActivity.this,
                                            DYNAMICLOCATION);
                                } catch (Exception e) {
                                    notUsable(true);
                                }
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                notUsable(true);
                                break;
                        }
                    }
                }
            });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 9999) {
            final LocationSettingsStates states = LocationSettingsStates.fromIntent(data);
            try {
                    if (states.isGpsUsable()) {
                        getDynamictime();
                    } else {
                        notUsable(true);
                    }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }*/
}
