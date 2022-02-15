package com.myomer.myomer.util.oldProjectFiles;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Handler;
import android.os.StrictMode;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.CycleInterpolator;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomnavigation.LabelVisibilityMode;
import com.google.android.material.textfield.TextInputLayout;
import com.myomer.myomer.R;
import com.myomer.myomer.util.helper.SharedPreferenceHelper;
import com.myomer.myomer.util.oldProjectFiles.webservice.TransparentProgressDialog;

import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by ahmad on a3/a7/18.
 */

public class Utilty {
    static Toast toast;
    private static TransparentProgressDialog mProgressDialog;


    public static boolean isOmerDay(final Date startDate, final Date endDate, final Date currentDate) {
        return !(currentDate.after(startDate) || currentDate.before(endDate));
    }

    /**
     * Calculate days remaining count to start omer
     * Added 1 day on starting date because in constantfile starting day is day-1 and here need to display exact day
     * Do not change in constantfile it is working ok in whole app logic
     */
    public static long getDaysTillOmer(Date d1, Date d2) {

        long daysdiff = 0;
        /*long diff = d2.getTime() - d1.getTime();
        long diffDays = diff / (24 * 60 * 60 * 1000) + 1;
        daysdiff = (int) diffDays;*/
        Calendar cald1 = Calendar.getInstance();
        cald1.setTime(d1);
        cald1.set(Calendar.MILLISECOND, 0);
        Calendar cald2 = Calendar.getInstance();
        cald2.setTime(d2);
        cald2.set(Calendar.DAY_OF_MONTH, cald2.get(Calendar.DAY_OF_MONTH) + 1);
        daysdiff = (long) (cald2.getTimeInMillis() - cald1.getTimeInMillis()) / (24 * 60 * 60 * 1000);
//        daysdiff=cald2.get(Calendar.DAY_OF_MONTH)+1-cald1.get(Calendar.DAY_OF_MONTH);
        return daysdiff;

    }

    public static Date toDate(String dateString) {
        Date date = null;
        DateFormat formatter = new SimpleDateFormat("MM/dd/yy");
        try {
            date = formatter.parse(dateString);

            Log.e("Print result: ", String.valueOf(date));

        } catch (ParseException e1) {
            e1.printStackTrace();
        }
        return date;
    }

    public static int getYear(Date date) {
        int year = Integer.parseInt(new SimpleDateFormat("yyyy").format(date));
        return year;
    }

    @SuppressLint("RestrictedApi")
    public static void removeShiftMode(BottomNavigationView view) {
        BottomNavigationMenuView menuView = (BottomNavigationMenuView) view.getChildAt(0);
        try {
            Field shiftingMode = menuView.getClass().getDeclaredField("mShiftingMode");
            shiftingMode.setAccessible(true);
            shiftingMode.setBoolean(menuView, false);
            shiftingMode.setAccessible(false);
            for (int i = 0; i < menuView.getChildCount(); i++) {
                BottomNavigationItemView item = (BottomNavigationItemView) menuView.getChildAt(i);
                item.setLabelVisibilityMode(LabelVisibilityMode.LABEL_VISIBILITY_LABELED);
                // set once again checked value, so view will be updated
                item.setChecked(item.getItemData().isChecked());
            }
        } catch (NoSuchFieldException e) {
            Log.e("ERROR NO SUCH FIELD", "Unable to get shift mode field");
        } catch (IllegalAccessException e) {
            Log.e("ERROR ILLEGAL ALG", "Unable to change value of shift mode");
        }

    }

    /**
     * Check internet connection
     * @param mContext
     * @return
     */
    public static boolean isConnectingToInternet(Context mContext) {

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnected()) {
            return true;
        } else {
            setToast(mContext, mContext.getResources().getString(R.string.no_internet));
            return false;
        }
    }


    /**
     * Global toask logic
     * @param context
     * @param msg
     */
    public static void setToast(Context context, String msg) {
        try {
            if (toast != null) {
                toast.cancel();
            }
            toast = Toast.makeText(context, msg.trim(), Toast.LENGTH_SHORT);
            toast.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void dismissToast() {
        try {
            if (toast != null) {
                toast.cancel();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Replace screen (fragment) in home
     * @param activity
     * @param newFragment
     * @param containerId
     */
    public static void replaceFragment(AppCompatActivity activity, Fragment newFragment, int containerId) {
        FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();
        transaction.replace(containerId, newFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public static boolean isFirstTime(Context context) {
        Boolean firstTime = null;
        if (firstTime == null) {
            firstTime = SharedPreferenceHelper.getSharedPreferenceBoolean(context, "firstTime", true);
            if (firstTime) {
                SharedPreferenceHelper.setSharedPreferenceBoolean(context, "firstTime", false);
            }
        }
        return firstTime;
    }

    public static List<String> readDataFromCSV(Context c) {
        InputStream is = c.getResources().openRawResource(R.raw.reminders3);
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(is, Charset.forName("UTF-8")));
        String line = "";
        String[] tokens = null;
        List<String> bodies = new ArrayList<>();
        try {
            while ((line = reader.readLine()) != null) {
                // Split the line into different tokens (using the comma as a separator).
                tokens = line.split(",");
                bodies.add(tokens[3]);
            }
        } catch (IOException e1) {
            Log.e("MainActivity", "Error" + line, e1);
            e1.printStackTrace();
        }
        return bodies;
    }

    /**
     * Share functionality to share content text from any screen
     * @param context
     * @param view
     * @param share
     * @param videoview
     * @param videolink
     * @param <T>
     */
    public static <T extends ViewGroup> void shareInfo(final Context context, final T view, ImageView share, RelativeLayout videoview, String... videolink) {
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String text = "";

                    final int childCount = view.getChildCount();
                    for (int i = 0; i < childCount; i++) {
                        View child = view.getChildAt(i);
                        if (child instanceof TextView && child.getVisibility() == View.VISIBLE) {

                            if (!TextUtils.isEmpty(text)) {
                                text = text + "\n\n";
                            }
                            text = text + ((TextView) child).getText().toString();
                        }
                        if (videoview != null && child instanceof ViewGroup && child == videoview) {
                            text = text + "\n\n" + videolink[0];
                        }
                    }

//                    text = text + "\n\n" + context.getResources().getString(R.string.download_now)
//                            + "https://www.meaningfullife.com/myomer";
                    text = text + "\n\n" + context.getResources().getString(R.string.download_now)
                            + "http://bit.ly/myomer";
                    Intent i = new Intent(Intent.ACTION_SEND);
                    i.setType("text/plain");
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                    i.putExtra(Intent.EXTRA_SUBJECT, context.getResources().getString(R.string.app_name));
                    i.putExtra(Intent.EXTRA_TEXT, text);
                    context.startActivity(Intent.createChooser(i, context.getResources().getString(R.string.share)));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }


    public static void showProgressTimer(final Context ctx, final int time) {

        try {
            if (mProgressDialog == null) {
                mProgressDialog = new TransparentProgressDialog(ctx, R.drawable.loading_img);
                mProgressDialog.setCancelable(false);
                mProgressDialog.show();

            } else {

                mProgressDialog.show();
            }
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    hideProgressDialog(ctx);
                }
            }, time);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public static void hideProgressDialog(final Context ctx) {

        try {
            if (mProgressDialog != null) {
                mProgressDialog.dismiss();
                mProgressDialog = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Hide keyboard function
     * @param context
     * @param currentFocus
     */
    public static void hideKeypad(Context context, View currentFocus) {
        try {
            ((InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);

            currentFocus.clearFocus();
            currentFocus.setFocusable(false);


            if (currentFocus.getRootView() != null) {
                currentFocus.getRootView().clearFocus();
                currentFocus.getRootView().setFocusable(false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Email validation
     * @param context
     * @param editText
     */
    public static void validateEmail(Context context, TextInputLayout editText) {
        String email_temp = editText.getEditText().getText().toString().trim();

        if (StringUtils.isEmpty(email_temp)) {
            editText.setError(context.getResources().getString(R.string.err_email));
            editText.startAnimation(shakeError());
        } else if (!isValidEmail(email_temp)) {
            editText.setError(context.getResources().getString(R.string.err_valid_email));
            editText.startAnimation(shakeError());
        } else {
            editText.setError(null);
            editText.setErrorEnabled(false);
        }
    }

    public static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }


    /**
     * Textfields validation checking functions
     * @param context
     * @param editText
     * @param errortext
     */
    public static void validationErrorNEW(Context context, TextInputLayout editText, String errortext) {

        if (StringUtils.isEmpty(editText.getEditText().getText())) {
            editText.setError(errortext);
            editText.startAnimation(shakeError());
        } else {
            editText.setError(null);
            editText.setErrorEnabled(false);
        }
    }

    /**
     * Shake animation on error in editext
     * @return
     */
    public static TranslateAnimation shakeError() {
        TranslateAnimation shake = new TranslateAnimation(0, 7, 0, 0);
        shake.setDuration(400);
        shake.setInterpolator(new CycleInterpolator(7));
        return shake;
    }

    /**
     * Get text of edittext or textview
     * @param t
     * @param <T>
     * @return
     */
    public static <T extends TextView> String gettext(T t) {
        return t.getText().toString().trim();
    }


    /**
     * Clear all notifications and badges
     * @param context
     */
    public static void resetNotifications(Context context) {
        try {
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            if (notificationManager != null) {
                notificationManager.cancelAll();
            }

            ContentValues cv = new ContentValues();
            cv.put("badgecount", 0);
            context.getContentResolver().update(Uri.parse("content://com.sec.badge/apps"), cv, "package=?", new String[]{context.getPackageName()});
        } catch (Exception e) {
        }
    }

}
