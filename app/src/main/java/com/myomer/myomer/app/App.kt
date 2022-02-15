package com.myomer.myomer.app

import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.multidex.BuildConfig
import androidx.multidex.MultiDex
import com.luckycatlabs.sunrisesunset.SunriseSunsetCalculator
import com.luckycatlabs.sunrisesunset.dto.Location
import com.myomer.myomer.data.local.models.MyOmerPeriod
import com.myomer.myomer.data.local.plist_parser.PListDict
import com.myomer.myomer.data.local.plist_parser.PListException
import com.myomer.myomer.data.local.plist_parser.PListParser
import com.myomer.myomer.receiver.AlarmReceiver
import com.myomer.myomer.util.helper.SharedPreferenceHelper
import com.myomer.myomer.util.oldProjectFiles.Constants
import com.myomer.myomer.util.oldProjectFiles.Utilty
import com.myomer.myomer.util.realm.RealmController
import io.realm.Realm
import io.realm.RealmConfiguration
import org.ankit.gpslibrary.MyTracker
import java.io.IOException
import java.io.InputStream
import java.util.*

/**
 * Created by Khush on 2/15/22.
 */
class App : Application() {
    private var realm: Realm? = null
    var format: String? = null
    override fun onCreate() {
        super.onCreate()
        Realm.init(this)
        val realmConfiguration = RealmConfiguration.Builder()
            .name(Realm.DEFAULT_REALM_NAME)
            .schemaVersion(1)
            .build()
        Realm.removeDefaultConfiguration()
        Realm.setDefaultConfiguration(realmConfiguration)

        //get realm instance
        realm = RealmController.with(this).realm
        val tracker = MyTracker(this@App)
        val location = Location(tracker.latitude.toString() + "", tracker.longitude.toString() + "")
        val calculator = SunriseSunsetCalculator(location, TimeZone.getDefault())
        val c = calculator.getOfficialSunsetCalendarForDate(Calendar.getInstance())
        val hourOfDay = c[Calendar.HOUR]
        val minute = c[Calendar.MINUTE]
        val AM_PM = c[Calendar.AM_PM]
        format = if (AM_PM == Calendar.AM) {
            "AM"
        } else {
            "PM"
        }


        // SharedPreferenceHelper.setSharedPreferenceInt(App.this, "NightfallHour", c.get(Calendar.HOUR));
        //   SharedPreferenceHelper.setSharedPreferenceInt(App.this, "NightfallMin", c.get(Calendar.MINUTE));
        //SharedPreferenceHelper.setSharedPreferenceString(App.this, "NightfallFormat", format);
        SharedPreferenceHelper.setSharedPreferenceBoolean(this@App, "NightFallEnabled", true)
        /**
         * Table schema changed in App version 7 so fix for existing database
         */
        if (BuildConfig.VERSION_CODE == 15 && SharedPreferenceHelper.getSharedPreferenceBoolean(
                this@App,
                "AppUpdate",
                true
            )
        ) {
            SharedPreferenceHelper.setSharedPreferenceBoolean(this@App, "firstTime", true)
            SharedPreferenceHelper.setSharedPreferenceBoolean(this@App, "AppUpdate", false)
            //            Toast.makeText(App.this,"settrue",Toast.LENGTH_LONG).show();
        }
        if (BuildConfig.VERSION_CODE == 23 && SharedPreferenceHelper.getSharedPreferenceBoolean(
                this@App,
                "AppUpdateRequired",
                true
            )
        ) {
            SharedPreferenceHelper.setSharedPreferenceBoolean(this@App, "firstTime", true)
            SharedPreferenceHelper.setSharedPreferenceBoolean(
                this@App,
                "AppUpdateRequired",
                false
            )
            //            Toast.makeText(App.this,"settrue",Toast.LENGTH_LONG).show();
        }
        if ((BuildConfig.VERSION_CODE == 24 || BuildConfig.VERSION_CODE == 25) && SharedPreferenceHelper.getSharedPreferenceBoolean(
                this@App,
                "AppUpdateRequired",
                true
            )
        ) {
            SharedPreferenceHelper.setSharedPreferenceBoolean(this@App, "firstTime", true)
            SharedPreferenceHelper.setSharedPreferenceBoolean(
                this@App,
                "AppUpdateRequired",
                false
            )
            //            Toast.makeText(App.this,"settrue",Toast.LENGTH_LONG).show();
        }
        if (Utilty.isFirstTime(this) /*||((BuildConfig.VERSION_CODE==7 && SharedPreferenceHelper.getSharedPreferenceBoolean(App.this, "AppUpdate", true)))*/) {
//            SharedPreferenceHelper.setSharedPreferenceBoolean(App.this, "AppUpdate", false);
            // Toast.makeText(App.this,"inside",Toast.LENGTH_LONG).show();
            SharedPreferenceHelper.setSharedPreferenceBoolean(
                this@App,
                "NightAlarmEnabled",
                true
            )
            SharedPreferenceHelper.setSharedPreferenceInt(this@App, "NightfallHour", 20)
            SharedPreferenceHelper.setSharedPreferenceInt(this@App, "NightfallMin", 0)
            SharedPreferenceHelper.setSharedPreferenceString(this@App, "NightfallFormat", "PM")
            SharedPreferenceHelper.setSharedPreferenceBoolean(this@App, "NightFallEnabled", true)
            val calNow = Calendar.getInstance()
            calNow.timeInMillis = System.currentTimeMillis()
            val calSet = calNow.clone() as Calendar
            calSet.timeInMillis = System.currentTimeMillis()
            calSet[Calendar.HOUR_OF_DAY] = 20
            Log.e("TAG", "daily: 20:$minute")
            calSet[Calendar.MINUTE] = minute
            calSet[Calendar.AM_PM] = Calendar.PM
            calSet[Calendar.SECOND] = 0
            calSet[Calendar.MILLISECOND] = 0
            if (calSet.compareTo(calNow) <= 0) {
                // Today Set time passed, count to tomorrow
                calSet.add(Calendar.DATE, 1)
            }
            setAlarm(calSet, 1)
            val realm = Realm.getDefaultInstance()
            realm.beginTransaction()
            realm.deleteAll()
            for (i in Constants.myOmerStartDates.indices) {
                val cal = Calendar.getInstance()
                cal.time =
                    Utilty.toDate(Constants.myOmerStartDates[i])
                cal[Calendar.HOUR] = hourOfDay
                cal[Calendar.MINUTE] = minute
                if (format == "AM") cal[Calendar.AM_PM] = 0 else cal[Calendar.AM_PM] = 1
                val cal2 = Calendar.getInstance()
                cal2.time =
                    Utilty.toDate(Constants.myOmerEndDates[i])
                cal2[Calendar.HOUR] = hourOfDay
                cal2[Calendar.MINUTE] = minute
                if (format == "AM") cal2[Calendar.AM_PM] = 0 else cal2[Calendar.AM_PM] = 1
                val myOmerPeriod = MyOmerPeriod()
                myOmerPeriod.id = i + System.currentTimeMillis()
                myOmerPeriod.startDate = Date(cal.timeInMillis)
                myOmerPeriod.endDate = Date(cal2.timeInMillis)
                myOmerPeriod.startYear = Utilty.getYear(
                    Utilty.toDate(
                        Constants.myOmerStartDates[i]
                    )
                )
                realm.copyToRealmOrUpdate(myOmerPeriod)
            }
            realm.commitTransaction()
            initiateAlarms()
        }
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    fun initiateAlarms() {
        val assetManager = assets
        var stream: InputStream? = null
        try {
            stream = assetManager.open("others/MyOmerCalendar" + Utilty.getYear(Date()) + ".plist")
            val dict = PListParser.parse<PListDict>(stream)
            //PListArray array = dict.getPListArray();
            val pListArray = dict.getPListArray("reminders")
            val startDate = dict.getDate("start")

            /*Date checkstartDate = dict.getDate("start");
            checkstartDate.setDate(checkstartDate.getDay()+1);
            Date currentDate = new Date();
            long daydiff = Days.daysBetween(new LocalDate(checkstartDate),new LocalDate(currentDate)).getDays();

            if(daydiff<0){
                daydiff=0;
            }
            */
            val allDays = pListArray.size()
            for (i in 0 until allDays) {
                val day = pListArray.getPListDict(i)
                val early = day.getBool("early")
                val late = day.getBool("late")
                val dayEarly = day.getBool("dayEarly")
                val twoDaysEarly = day.getBool("2daysEarly")
                setAlarm(early, late, dayEarly, twoDaysEarly, i, startDate)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: PListException) {
            e.printStackTrace()
        }
    }

    private fun setAlarm(
        early: Boolean,
        late: Boolean,
        dayEarly: Boolean,
        twoDaysEarly: Boolean,
        day: Int,
        startDate: Date
    ) {
        val currentYear = Utilty.getYear(Date())
        val myOmerPeriod = RealmController.with(this).getPeriodByYear(currentYear)
        val startDateOfOmer = myOmerPeriod.startDate
        val calSet = Calendar.getInstance()
        calSet.timeInMillis = startDate.time
        //        calSet.add(Calendar.DATE, day);
        calSet[Calendar.DATE] = calSet[Calendar.DATE] /*+1*/ + day
        if (early == true) {
            calSet[Calendar.HOUR_OF_DAY] = 14
            calSet[Calendar.MINUTE] = 30
            calSet[Calendar.SECOND] = 0
            calSet[Calendar.MILLISECOND] = 0
            Log.e("Alaaaaarm1: ", Date(calSet.timeInMillis).toString())
            val milis = calSet.timeInMillis
            val intent = Intent(applicationContext, AlarmReceiver::class.java)
            intent.action = "R Alaaaaarm1  " + Date(calSet.timeInMillis)
                .toString() + "   " + calSet[Calendar.DATE] + "   " + day
            val pendingIntent = PendingIntent.getBroadcast(
                applicationContext, day + 10, intent, 0
            )
            val alarmManager = applicationContext.getSystemService(ALARM_SERVICE) as AlarmManager
            val ALARM_TYPE = AlarmManager.RTC_WAKEUP
            alarmManager.setExact(ALARM_TYPE, milis, pendingIntent)
        }
        if (late == true) {
            calSet[Calendar.HOUR_OF_DAY] = 22
            calSet[Calendar.MINUTE] = 30
            calSet[Calendar.SECOND] = 0
            calSet[Calendar.MILLISECOND] = 0
            Log.e("Alaaaaarm2: ", Date(calSet.timeInMillis).toString())
            val milis = calSet.timeInMillis
            val intent = Intent(applicationContext, AlarmReceiver::class.java)
            intent.action = "R Alaaaaarm2   " + Date(calSet.timeInMillis)
                .toString() + "   " + calSet[Calendar.DATE] + "   " + day
            val pendingIntent = PendingIntent.getBroadcast(
                applicationContext, day + 10, intent, 0
            )
            val alarmManager = applicationContext.getSystemService(ALARM_SERVICE) as AlarmManager
            val ALARM_TYPE = AlarmManager.RTC_WAKEUP
            alarmManager.setExact(ALARM_TYPE, milis, pendingIntent)
        }
        if (dayEarly == true) {
            calSet[Calendar.HOUR_OF_DAY] = 14
            calSet[Calendar.MINUTE] = 30
            calSet[Calendar.SECOND] = 0
            calSet[Calendar.MILLISECOND] = 0
            calSet.add(Calendar.DATE, -1)
            Log.e("Alaaaaarm3: ", Date(calSet.timeInMillis).toString())
            val milis = calSet.timeInMillis
            val intent = Intent(applicationContext, AlarmReceiver::class.java)
            intent.action = "R Alaaaaarm3   " + Date(calSet.timeInMillis)
                .toString() + "   " + calSet[Calendar.DATE] + "   " + day
            val pendingIntent = PendingIntent.getBroadcast(
                applicationContext, day + 10, intent, 0
            )
            val alarmManager = applicationContext.getSystemService(ALARM_SERVICE) as AlarmManager
            val ALARM_TYPE = AlarmManager.RTC_WAKEUP
            alarmManager.setExact(ALARM_TYPE, milis, pendingIntent)
        }
        if (twoDaysEarly == true) {
            calSet[Calendar.HOUR_OF_DAY] = 14
            calSet[Calendar.MINUTE] = 30
            calSet[Calendar.SECOND] = 0
            calSet[Calendar.MILLISECOND] = 0
            calSet.add(Calendar.DATE, -2)
            Log.e("Alaaaaarm4: ", Date(calSet.timeInMillis).toString())
            val milis = calSet.timeInMillis
            val intent = Intent(baseContext, AlarmReceiver::class.java)
            intent.action = "R Alaaaaarm4   " + Date(calSet.timeInMillis)
                .toString() + "   " + calSet[Calendar.DATE] + "   " + day
            val pendingIntent = PendingIntent.getBroadcast(
                applicationContext, day + 10, intent, 0
            )
            val alarmManager = applicationContext.getSystemService(ALARM_SERVICE) as AlarmManager
            val ALARM_TYPE = AlarmManager.RTC_WAKEUP
            alarmManager.setExact(ALARM_TYPE, milis, pendingIntent)
        }
    }

    private fun setAlarm(targetCal: Calendar, reqCode: Int) {
        Log.e("AlaaaaarmRepeat: ", Date(targetCal.timeInMillis).toString())
        val milis = targetCal.timeInMillis
        val intent = Intent(baseContext, AlarmReceiver::class.java)
        intent.action = "R AlaaaaarmRepeat   " + Date(targetCal.timeInMillis).toString()
        val pendingIntent = PendingIntent.getBroadcast(
            applicationContext, reqCode, intent, 0
        )
        val alarmManager = applicationContext.getSystemService(ALARM_SERVICE) as AlarmManager
        val ALARM_TYPE = AlarmManager.RTC_WAKEUP
        alarmManager.setInexactRepeating(
            ALARM_TYPE, milis,
            AlarmManager.INTERVAL_DAY, pendingIntent
        )
    }
}