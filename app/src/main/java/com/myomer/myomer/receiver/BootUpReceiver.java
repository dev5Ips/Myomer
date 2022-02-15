package com.myomer.myomer.receiver;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.util.Log;

import com.luckycatlabs.sunrisesunset.SunriseSunsetCalculator;
import com.luckycatlabs.sunrisesunset.dto.Location;
import com.myomer.myomer.data.local.plist_parser.PListArray;
import com.myomer.myomer.data.local.plist_parser.PListDict;
import com.myomer.myomer.data.local.plist_parser.PListException;
import com.myomer.myomer.data.local.plist_parser.PListParser;
import com.myomer.myomer.util.oldProjectFiles.Utilty;

import org.ankit.gpslibrary.MyTracker;

import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by ahmad on 3/16/18.
 */

public class BootUpReceiver extends BroadcastReceiver {

    final static int RQS_1 = 1;
    final static int RQS_2 = 2;

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            Log.e("TAG", "onReceive: boot");
            /*setupAppAlarm(context);


            boolean DailyAlarmEnabled = SharedPreferenceHelper.getSharedPreferenceBoolean(context, "DailyAlarmEnabled", false);
            boolean NightAlarmEnabled = SharedPreferenceHelper.getSharedPreferenceBoolean(context, "NightAlarmEnabled", false);
            if (DailyAlarmEnabled == true){
                int hour =  SharedPreferenceHelper.getSharedPreferenceInt(context,"DailyAlarmHour",0);
                int min = SharedPreferenceHelper.getSharedPreferenceInt(context,"DailyAlarmMin",0);

                if (hour != 0 && min != 0){
                    Calendar calNow = Calendar.getInstance();
                    calNow.setTimeInMillis(System.currentTimeMillis());
                    Calendar calSet = (Calendar) calNow.clone();
                    calSet.setTimeInMillis(System.currentTimeMillis());
                    calSet.set(Calendar.HOUR_OF_DAY, hour);
                    calSet.set(Calendar.MINUTE, min);
                    calSet.set(Calendar.SECOND, 0);
                    calSet.set(Calendar.MILLISECOND, 0);

                    if (calSet.compareTo(calNow) <= 0) {
                        // Today Set time passed, count to tomorrow
                        calSet.add(Calendar.DATE, 1);
                    }

                    setAlarm(calSet,RQS_2,context);
                }
            }
            if (NightAlarmEnabled == true){
               int hour =  SharedPreferenceHelper.getSharedPreferenceInt(context,"NightAlarmHour",0);
                int min = SharedPreferenceHelper.getSharedPreferenceInt(context,"NightAlarmMin",0);

                if (hour != 0 && min != 0){
                    Calendar calNow = Calendar.getInstance();
                    calNow.setTimeInMillis(System.currentTimeMillis());
                    Calendar calSet = (Calendar) calNow.clone();
                    calSet.setTimeInMillis(System.currentTimeMillis());
                    calSet.set(Calendar.HOUR_OF_DAY, hour);
                    calSet.set(Calendar.MINUTE, min);
                    calSet.set(Calendar.SECOND, 0);
                    calSet.set(Calendar.MILLISECOND, 0);

                    if (calSet.compareTo(calNow) <= 0) {
                        // Today Set time passed, count to tomorrow
                        calSet.add(Calendar.DATE, 1);
                    }

                    setAlarm(calSet,RQS_1,context);
                }
            }*/
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void setupAppAlarm(Context context) {
        MyTracker tracker = new MyTracker(context);
        Location location = new Location(tracker.getLatitude() + "", tracker.getLongitude() + "");
        SunriseSunsetCalculator calculator = new SunriseSunsetCalculator(location, TimeZone.getDefault());
        Calendar c = calculator.getOfficialSunsetCalendarForDate(Calendar.getInstance());

        int hourOfDay = c.get(Calendar.HOUR);
        int minute = c.get(Calendar.MINUTE);

        Calendar calNow = Calendar.getInstance();
        calNow.setTimeInMillis(System.currentTimeMillis());
        Calendar calSet = (Calendar) calNow.clone();
        calSet.setTimeInMillis(System.currentTimeMillis());
        calSet.set(Calendar.HOUR_OF_DAY, 20);
        Log.e("TAG", "daily: 20:"+minute );
        calSet.set(Calendar.MINUTE, minute);
        calSet.set(Calendar.AM_PM, Calendar.PM);
        calSet.set(Calendar.SECOND, 0);
        calSet.set(Calendar.MILLISECOND, 0);

        if (calSet.compareTo(calNow) <= 0) {
            // Today Set time passed, count to tomorrow
            calSet.add(Calendar.DATE, 1);
        }
        setAlarm(calSet, 1,context);
        initiateAlarms(context);
    }

    private void setAlarm(Calendar targetCal, int reqCode,Context context) {
        Log.e("AlaaaaarmRepeat: ", String.valueOf(new Date(targetCal.getTimeInMillis())));

        long milis = targetCal.getTimeInMillis();

        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.setAction("R AlaaaaarmRepeat   "+String.valueOf(new Date(targetCal.getTimeInMillis())));
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context, reqCode, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        int ALARM_TYPE = AlarmManager.RTC_WAKEUP;
//        alarmManager.cancel(pendingIntent);
        alarmManager.setInexactRepeating(ALARM_TYPE, milis,
                AlarmManager.INTERVAL_DAY, pendingIntent);


//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
//                        alarmManager.setExactAndAllowWhileIdle(ALARM_TYPE, milis, pendingIntent);
//                    else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
//                        alarmManager.setExact(ALARM_TYPE, milis, pendingIntent);
//                    else
//                        alarmManager.set(ALARM_TYPE, milis, pendingIntent);

    }


    public void initiateAlarms(Context context) {
        AssetManager assetManager = context.getAssets();

        InputStream stream = null;

        try {
            stream = assetManager.open("others/MyOmerCalendar" + Utilty.getYear(new Date()) + ".plist");
            PListDict dict = PListParser.parse(stream);
            //PListArray array = dict.getPListArray();

            PListArray pListArray = dict.getPListArray("reminders");
            Date startDate = dict.getDate("start");

            /*Date checkstartDate = dict.getDate("start");
            checkstartDate.setDate(checkstartDate.getDay()+1);
            Date currentDate = new Date();
            long daydiff = Days.daysBetween(new LocalDate(checkstartDate),new LocalDate(currentDate)).getDays();

            if(daydiff<0){
                daydiff=0;
            }
            */
            int allDays = pListArray.size();

            for (int i = 0; i < allDays; i++) {
                PListDict day = pListArray.getPListDict(i);
                boolean early = day.getBool("early");
                boolean late = day.getBool("late");
                boolean dayEarly = day.getBool("dayEarly");
                boolean twoDaysEarly = day.getBool("2daysEarly");

                setAlarm(early, late, dayEarly, twoDaysEarly, i, startDate,context);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (PListException e) {
            e.printStackTrace();
        }


    }

    private void setAlarm(boolean early, boolean late, boolean dayEarly, boolean twoDaysEarly, int day, Date startDate,Context context) {
//        int currentYear = Utilty.getYear(new Date());
//        MyOmerPeriod myOmerPeriod = RealmController.with(context).getPeriodByYear(currentYear);
//        Date startDateOfOmer = myOmerPeriod.getStartDate();
        Calendar calSet = Calendar.getInstance();
        calSet.setTimeInMillis(startDate.getTime());
//        calSet.add(Calendar.DATE, day);
        calSet.set(Calendar.DATE,calSet.get(Calendar.DATE)/*+1*/+day);
        if (early == true) {
            calSet.set(Calendar.HOUR_OF_DAY, 14);
            calSet.set(Calendar.MINUTE, 30);
            calSet.set(Calendar.SECOND, 0);
            calSet.set(Calendar.MILLISECOND, 0);
            Log.e("Alaaaaarm1: ", String.valueOf(new Date(calSet.getTimeInMillis())));
//            if (calSet.compareTo(calNow) <= 0) {
//                // Today Set time passed, count to tomorrow
//                calSet.add(Calendar.DATE, 1);
//            }
            long milis = calSet.getTimeInMillis();
            Intent intent = new Intent(context, AlarmReceiver.class);
            intent.setAction("R Alaaaaarm1  "+ String.valueOf(new Date(calSet.getTimeInMillis()))+"   "+calSet.get(Calendar.DATE)+"   "+day);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    context, day + 10, intent, 0);
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            int ALARM_TYPE = AlarmManager.RTC_WAKEUP;

//            alarmManager.cancel(pendingIntent);
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
//                alarmManager.setExactAndAllowWhileIdle(ALARM_TYPE, milis, pendingIntent);
//            else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
//                alarmManager.setExact(ALARM_TYPE, milis, pendingIntent);
//            else
//                alarmManager.set(ALARM_TYPE, milis, pendingIntent);
            alarmManager.setExact(ALARM_TYPE, milis, pendingIntent);


        }
        if (late == true) {
            calSet.set(Calendar.HOUR_OF_DAY, 22);
            calSet.set(Calendar.MINUTE, 30);
            calSet.set(Calendar.SECOND, 0);
            calSet.set(Calendar.MILLISECOND, 0);
            Log.e("Alaaaaarm2: ", String.valueOf(new Date(calSet.getTimeInMillis())));
//            if (calSet.compareTo(calNow) <= 0) {
//                // Today Set time passed, count to tomorrow
//                calSet.add(Calendar.DATE, 1);
//            }
            long milis = calSet.getTimeInMillis();
            Intent intent = new Intent(context, AlarmReceiver.class);
            intent.setAction("R Alaaaaarm2   "+ String.valueOf(new Date(calSet.getTimeInMillis()))+"   "+calSet.get(Calendar.DATE)+"   "+day);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    context, day + 10, intent, 0);
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            int ALARM_TYPE = AlarmManager.RTC_WAKEUP;
//            alarmManager.cancel(pendingIntent);

//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
//                alarmManager.setExactAndAllowWhileIdle(ALARM_TYPE, milis, pendingIntent);
//            else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
//                alarmManager.setExact(ALARM_TYPE, milis, pendingIntent);
//            else
//                alarmManager.set(ALARM_TYPE, milis, pendingIntent);
            alarmManager.setExact(ALARM_TYPE, milis, pendingIntent);


        }
        if (dayEarly == true) {
            calSet.set(Calendar.HOUR_OF_DAY, 14);
            calSet.set(Calendar.MINUTE, 30);
            calSet.set(Calendar.SECOND, 0);
            calSet.set(Calendar.MILLISECOND, 0);
            calSet.add(Calendar.DATE, -1);
            Log.e("Alaaaaarm3: ", String.valueOf(new Date(calSet.getTimeInMillis())));
            long milis = calSet.getTimeInMillis();
            Intent intent = new Intent(context, AlarmReceiver.class);
            intent.setAction("R Alaaaaarm3   "+String.valueOf(new Date(calSet.getTimeInMillis()))+"   "+calSet.get(Calendar.DATE)+"   "+day);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    context, day + 10, intent, 0);
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            int ALARM_TYPE = AlarmManager.RTC_WAKEUP;

//            alarmManager.cancel(pendingIntent);

//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
//                alarmManager.setExactAndAllowWhileIdle(ALARM_TYPE, milis, pendingIntent);
//            else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
//                alarmManager.setExact(ALARM_TYPE, milis, pendingIntent);
//            else
//                alarmManager.set(ALARM_TYPE, milis, pendingIntent);
            alarmManager.setExact(ALARM_TYPE, milis, pendingIntent);


        }
        if (twoDaysEarly == true) {
            calSet.set(Calendar.HOUR_OF_DAY, 14);
            calSet.set(Calendar.MINUTE, 30);
            calSet.set(Calendar.SECOND, 0);
            calSet.set(Calendar.MILLISECOND, 0);
            calSet.add(Calendar.DATE, -2);
            Log.e("Alaaaaarm4: ", String.valueOf(new Date(calSet.getTimeInMillis())));
            long milis = calSet.getTimeInMillis();
            Intent intent = new Intent(context, AlarmReceiver.class);
            intent.setAction("R Alaaaaarm4   "+String.valueOf(new Date(calSet.getTimeInMillis()))+"   "+calSet.get(Calendar.DATE)+"   "+day);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    context, day + 10, intent, 0);
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            int ALARM_TYPE = AlarmManager.RTC_WAKEUP;

//            alarmManager.cancel(pendingIntent);

//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
//                alarmManager.setExactAndAllowWhileIdle(ALARM_ TYPE, milis, pendingIntent);
//            else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
//                alarmManager.setExact(ALARM_TYPE, milis, pendingIntent);
//            else
//                alarmManager.set(ALARM_TYPE, milis, pendingIntent);
            alarmManager.setExact(ALARM_TYPE, milis, pendingIntent);


        }

//        SharedPreferenceHelper.setSharedPreferenceBoolean(this, "FirstRun", true);
    }

}
