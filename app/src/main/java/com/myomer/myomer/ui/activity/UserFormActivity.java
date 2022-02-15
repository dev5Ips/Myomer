package com.myomer.myomer.ui.activity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.JsonElement;
import com.myomer.myomer.R;
import com.myomer.myomer.receiver.AlarmReceiver;
import com.myomer.myomer.util.helper.SharedPreferenceHelper;
import com.myomer.myomer.util.oldProjectFiles.Constants;
import com.myomer.myomer.util.oldProjectFiles.Utilty;
import com.myomer.myomer.util.oldProjectFiles.webservice.RxApiRequestHandler;
import com.myomer.myomer.util.oldProjectFiles.webservice.ServiceCallBack;

import org.apache.commons.lang3.StringUtils;

import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class UserFormActivity extends AppCompatActivity implements ServiceCallBack {

    @BindView(R.id.edtEmail)
    TextInputEditText edtEmail;
//    @BindView(R.id.inputEmail)
//    TextInputLayout inputEmail;
    @BindView(R.id.edtFirstname)
    TextInputEditText edtFirstname;
    @BindView(R.id.inputFirstname)
    TextInputLayout inputFirstname;
    @BindView(R.id.edtLastname)
    TextInputEditText edtLastname;
    @BindView(R.id.inputLastname)
    TextInputLayout inputLastname;
    @BindView(R.id.btnContinue)
    AppCompatButton btnContinue;
    @BindView(R.id.rootview)
    LinearLayout rootview;
    @BindView(R.id.infotext)
    TextView infotext;
    @BindView(R.id.txtBottomInfo)
    TextView txtBottomInfo;
    @BindView(R.id.txtSkip)
    TextView txtSkip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_form);
//        getWindow().setBackgroundDrawableResource(R.drawable.gradientbg);
        ButterKnife.bind(this);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        );



        welcomeNotification();

//        Typeface type1 = Typeface.createFromAsset(getAssets(), "fonts/Biko_Bold.otf");
//        btnContinue.setTypeface(type1);

    }

    private void welcomeNotification() {
        Calendar calSet = Calendar.getInstance();
        calSet.set(Calendar.HOUR_OF_DAY, 14);
        calSet.set(Calendar.MINUTE, 30);
        calSet.set(Calendar.SECOND, 0);
        calSet.set(Calendar.MILLISECOND, 0);
        long milis = calSet.getTimeInMillis();
        Intent intent = new Intent(getBaseContext(), AlarmReceiver.class);
        intent.setAction("R Welcome   " + String.valueOf(new Date(calSet.getTimeInMillis())) + "   " + calSet.get(Calendar.DATE));
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                getApplicationContext(), 999, intent, 0);
        AlarmManager alarmManager = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        int ALARM_TYPE = AlarmManager.RTC_WAKEUP;

//        alarmManager.cancel(pendingIntent);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
//            alarmManager.setExactAndAllowWhileIdle(ALARM_TYPE, milis, pendingIntent);
//        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
//            alarmManager.setExact(ALARM_TYPE, milis, pendingIntent);
//        else
//            alarmManager.set(ALARM_TYPE, milis, pendingIntent);
        alarmManager.setExact(ALARM_TYPE, milis, pendingIntent);


    }

    @OnClick({R.id.btnContinue, R.id.rootview, R.id.txtSkip})
    public void onViewClicked(View view) {
        Utilty.hideKeypad(UserFormActivity.this, view);
        switch (view.getId()) {
            case R.id.btnContinue:
                if (validateAll()) {
                    RxApiRequestHandler.postUserData(UserFormActivity.this, this, Utilty.gettext(edtEmail), Utilty.gettext(edtFirstname), Utilty.gettext(edtLastname));
                }
                break;
            case R.id.rootview:
                break;
            case R.id.txtSkip:
                SharedPreferenceHelper.setSharedPreferenceBoolean(UserFormActivity.this, Constants.FIRSTIME_USER, false);
                Intent i = new Intent(UserFormActivity.this, WaitingScreenActivity.class);
                startActivity(i);
                finish();


                break;
        }
    }

    /**
     * Validation of all fields
     *
     * @return
     */
    private boolean validateAll() {
//        Utilty.validateEmail(UserFormActivity.this, inputEmail);
//        return inputEmail.getError() == null /*&& inputFirstname.getError() == null && inputLastname.getError() == null*/;

        String email_temp = Utilty.gettext(edtEmail);
        if (StringUtils.isEmpty(email_temp)) {
            Toast.makeText(this, getResources().getString(R.string.err_email), Toast.LENGTH_SHORT).show();
            return false;
        } else if (!Utilty.isValidEmail(email_temp)) {
            Toast.makeText(this, getResources().getString(R.string.err_valid_email), Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }


    @Override
    public void onSuccess(String requestTag, JsonElement data) {
        try {
            SharedPreferenceHelper.setSharedPreferenceBoolean(UserFormActivity.this, Constants.FIRSTIME_USER, false);
            Intent i = new Intent(UserFormActivity.this, WaitingScreenActivity.class);
            startActivity(i);
            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onFailure(String requestTag, String message, String errorcode) {
        Utilty.setToast(UserFormActivity.this, message);
    }

}
