package com.myomer.myomer.receiver;

import static android.content.Context.NOTIFICATION_SERVICE;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.myomer.myomer.R;
import com.myomer.myomer.activities.HomeActivity;
import com.myomer.myomer.helpers.SharedPreferenceHelper;
import com.myomer.myomer.models.MyOmerPeriod;
import com.myomer.myomer.plist_parser.PListDict;
import com.myomer.myomer.plist_parser.PListParser;
import com.myomer.myomer.realm.RealmController;
import com.myomer.myomer.utilty.Constants;
import com.myomer.myomer.utilty.Utilty;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.Days;
import org.joda.time.LocalDate;

import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Ahmad on 3/15/2018.
 */

public class AlarmReceiver extends BroadcastReceiver {

    private static int count = 0;


    @Override
    public void onReceive(Context k1, Intent k2) {
        Log.e("TAG", "onReceive: " /*+ k2.getAction()*/);
        if (SharedPreferenceHelper.getSharedPreferenceBoolean(k1, Constants.ENABLE_NOTIFICATIONS, true)) {
            generateNotificationnew(k1, k2);
//            generateNotification(k1, k2);
        }
    }



    /*private void generateNotification(Context c, Intent k2) {
        try {
            int currentYear = Utilty.getYear(new Date());
            MyOmerPeriod myOmerPeriod = RealmController.getInstance().getPeriodByYear(currentYear);
            Date startDateOfOmer = myOmerPeriod.getStartDate();
            Date currentDate = new Date();
            String quoteString = "Today is one of the omer day";
            String title = "Count the Omer";
            AssetManager assetManager = c.getAssets();
            int day = Days.daysBetween(new LocalDate(startDateOfOmer), new LocalDate(currentDate)).getDays();
            Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            List<String> body = Utilty.readDataFromCSV(c);
            if (day > 0 && day <= 49) {
                InputStream stream = null;

                if (day == 1) {
                    title = "Meaningful Life Center";
                } else {
                    title = "Count the Omer";
                }

                try {
                    stream = assetManager.open("days/day" + day + ".plist");
                    PListDict dict = PListParser.parse(stream);
                    PListDict quote = dict.getPListDict("quote");
                    quoteString = quote.getString("en");
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (PListException e) {
                    e.printStackTrace();
                }


            } else {
                quoteString = "Today is one of the omer day";
            }
            try {
                //            InputStream is = assetManager.open("others/MyOmerCalendar2018.plist");
                InputStream is = assetManager.open("others/MyOmerCalendar" + Utilty.getYear(new Date()) + ".plist");
                PListDict dict = PListParser.parse(is);
                PListArray pListArray = dict.getPListArray("reminders");
                if (day > 0 && day <= 49) {
                   *//* PListDict dayNum = pListArray.getPListDict(day);
                    boolean early = dayNum.getBool("early");
                    boolean late = dayNum.getBool("late");
                    boolean dayEarly = dayNum.getBool("dayEarly");
                    boolean twoDaysEarly = dayNum.getBool("2daysEarly");*//*


                    //                if (early == false && late == false && dayEarly == false && twoDaysEarly == false) {
                    Intent intent = new Intent(c, HomeActivity.class);
                    intent.putExtra("DayNumber", day);
                    PendingIntent pIntent = PendingIntent.getActivity(c, (int) System.currentTimeMillis(), intent, PendingIntent.FLAG_UPDATE_CURRENT);

                    // Build notification
                    // Actions are just fake

                    NotificationManager notificationManager = (NotificationManager) c.getSystemService(NOTIFICATION_SERVICE);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O &&
                            !SharedPreferenceHelper.getSharedPreferenceBoolean(c, Constants.CHANNEL_CREATED, false)) {
                        NotificationChannel mChannel = new NotificationChannel(Constants.NOTIFICATION_CHANNEL_ID, Constants.NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
                        if (notificationManager != null) {
                            notificationManager.createNotificationChannel(mChannel);
                            SharedPreferenceHelper.setSharedPreferenceBoolean(c, Constants.CHANNEL_CREATED, true);
                        }
                    }

                    Log.e("TAG", "generateNotification: 1");

                    NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(c, Constants.NOTIFICATION_CHANNEL_ID)
                            .setLargeIcon(BitmapFactory.decodeResource(c.getResources(), R.mipmap.ic_launcher))
                            .setSmallIcon(R.drawable.ic_noti_white)
                            .setContentTitle(title)
                            .setContentText(quoteString)
                            .setStyle(new NotificationCompat.BigTextStyle().bigText(quoteString))
                            .setAutoCancel(true)
                            .setSound(uri)
                            .setAutoCancel(true)
                            .setContentIntent(pIntent);

                    if (notificationManager != null) {
                        notificationManager.notify(day, notificationBuilder.build());
                        count++;
                    }
                    //                }
                } else {

                    Intent intent = new Intent(c, HomeActivity.class);
                    intent.putExtra("DayNumber", day);
                    PendingIntent pIntent = PendingIntent.getActivity(c, (int) System.currentTimeMillis(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
                    Log.e("TAG", "generateNotification: 2");

                    // Build notification
                    // Actions are just fake

                    NotificationManager notificationManager = (NotificationManager) c.getSystemService(NOTIFICATION_SERVICE);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O &&
                            !SharedPreferenceHelper.getSharedPreferenceBoolean(c, Constants.CHANNEL_CREATED, false)) {
                        NotificationChannel mChannel = new NotificationChannel(Constants.NOTIFICATION_CHANNEL_ID, Constants.NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
                        if (notificationManager != null) {
                            notificationManager.createNotificationChannel(mChannel);
                            SharedPreferenceHelper.setSharedPreferenceBoolean(c, Constants.CHANNEL_CREATED, true);
                        }
                    }


                    NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(c, Constants.NOTIFICATION_CHANNEL_ID)
                            .setLargeIcon(BitmapFactory.decodeResource(c.getResources(), R.mipmap.ic_launcher))
                            .setSmallIcon(R.drawable.ic_noti_white)
                            .setContentTitle(title)
                            .setAutoCancel(true)
                            .setSound(uri)
                            .setContentIntent(pIntent);

                    boolean showNotification = true;

                    if (k2.getAction().contains("Welcome")) {
                        notificationBuilder.setContentText(body.get(1));
                    } else {

                        ArrayList<String> notificationsofDay = new ArrayList<>();
                        for (int i = 0; i < body.size(); i++) {
                            if (body.get(i).contains(String.valueOf(day))) {
                                notificationsofDay.add(body.get(i));
                            }
                        }
                        if (notificationsofDay.size() <= 0) {
                            showNotification = false;
                        } else if (notificationsofDay.size() == 2) {
                            if (Calendar.getInstance().get(Calendar.HOUR_OF_DAY) >= 20) {
                                notificationBuilder.setContentText(notificationsofDay.get(2));
                            } else {
                                notificationBuilder.setContentText(notificationsofDay.get(1));
                            }
                        } else {
                            notificationBuilder.setContentText(body.get(day));
                        }

                    }

                    if (notificationManager != null && showNotification) {
                        notificationManager.notify(day, notificationBuilder.build());
                        count++;
                    }


                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }*/

    private void generateNotificationnew(Context context, Intent k2) {
        try {
            String title = "";
            String quoteString = "";
            boolean redirect_toDate = false;
            int openday=1;

            int currentYear = Utilty.getYear(new Date());
            MyOmerPeriod myOmerPeriod = RealmController.getInstance().getPeriodByYear(currentYear);
            Date startDateOfOmer = myOmerPeriod.getStartDate();
            Date currentDate = new Date();
            int day = Days.daysBetween(new LocalDate(startDateOfOmer), new LocalDate(currentDate)).getDays();
//            day = day + 1;
//            Toast.makeText(context, "currentOmerDay: " + day, Toast.LENGTH_SHORT).show();
            if (day == -1/* || day == 0*/ || (k2 !=null &&k2.getAction()!=null &&k2.getAction().contains("Welcome"))) {
                redirect_toDate = false;
                title = "Meaningful Life Center";
                quoteString = "The first night of the Omer is Sunday after nightfall. Check daily for excercises and wisdom to grow as a person and take the 49 day journey to personal refinement.";
            } else if (day >= 0 && day <= 49) {
                redirect_toDate = true;
                // new nightfall logic +1 day
                int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
                int minutes = Calendar.getInstance().get(Calendar.MINUTE);
                int nightFallHours = SharedPreferenceHelper.getSharedPreferenceInt(context, "NightfallHour", 0);
                int nightFallMinutes = SharedPreferenceHelper.getSharedPreferenceInt(context, "NightfallMin", 0);

                title = "Count the Omer";
                if (day == 49) {
                    title = "You Completed the Omer!";
                }
                AssetManager assetManager = context.getAssets();

                if (hour >= nightFallHours && minutes >= nightFallMinutes) {
                    if (day == 0) {
                        day = 2;
                    }
                    openday=(day + 1);
                    InputStream stream = assetManager.open("days/day" + (day + 1) + ".plist");
                    PListDict dict = PListParser.parse(stream);
                    PListDict quote = dict.getPListDict("quote");
                    quoteString = quote.getString("en").replaceAll("Today", "Tonight");
                    quoteString = quoteString + " " + dict.getString("exercise");
                } else {
                    if (day == 0) {
                        day = 1;
                    }
                    openday=day;
                    InputStream stream = assetManager.open("days/day" + day + ".plist");
                    PListDict dict = PListParser.parse(stream);
                    PListDict quote = dict.getPListDict("quote");
                    quoteString = quote.getString("en");
                    quoteString = quoteString + " " + dict.getString("exercise");
                }

            }

            if (StringUtils.isNotEmpty(title) && StringUtils.isNotEmpty(quoteString)) {
                NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O &&
                        !SharedPreferenceHelper.getSharedPreferenceBoolean(context, Constants.CHANNEL_CREATED, false)) {
                    NotificationChannel mChannel = new NotificationChannel(Constants.NOTIFICATION_CHANNEL_ID, Constants.NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
                    if (notificationManager != null) {
                        notificationManager.createNotificationChannel(mChannel);
                        SharedPreferenceHelper.setSharedPreferenceBoolean(context, Constants.CHANNEL_CREATED, true);
                    }
                }

                Intent intent = new Intent(context, HomeActivity.class);
                intent.putExtra("DayNumber", openday);
                PendingIntent pIntent = PendingIntent.getActivity(context, (int) System.currentTimeMillis(), intent, PendingIntent.FLAG_UPDATE_CURRENT);

                NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, Constants.NOTIFICATION_CHANNEL_ID)
                        .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher))
                        .setSmallIcon(R.drawable.ic_noti_white)
                        .setContentTitle(title)
                        .setContentText(quoteString)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(quoteString))
                        .setAutoCancel(true);
                if (redirect_toDate) {
                    notificationBuilder.setContentIntent(pIntent);
                }

                if (notificationManager != null) {
                    notificationManager.notify(day, notificationBuilder.build());
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
