package com.myomer.myomer.util.realm;

/**
 * Created by ahmad on a3/a8/18.
 */

import android.app.Activity;
import android.app.Application;

import androidx.fragment.app.Fragment;

import com.myomer.myomer.helpers.SharedPreferenceHelper;
import com.myomer.myomer.models.JournalQuestionModelNew;
import com.myomer.myomer.models.MyOmerPeriod;
import com.myomer.myomer.models.RecordBlessing;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import io.realm.exceptions.RealmMigrationNeededException;


public class RealmController {

    private static RealmController instance;
    private Realm realm;

    public RealmController(Application application) {

        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder()
                .name(Realm.DEFAULT_REALM_NAME)
                .schemaVersion(1)
                .build();
        try {
            realm =Realm.getDefaultInstance();
        } catch (RealmMigrationNeededException r) {
            Realm.deleteRealm(realmConfiguration);
            SharedPreferenceHelper.setSharedPreferenceBoolean(application, "firstTime", true);
            realm =Realm.getDefaultInstance();
        }

    }

    public static RealmController with(Fragment fragment) {

        if (instance == null) {
            instance = new RealmController(fragment.getActivity().getApplication());
        }
        return instance;
    }

    public static RealmController with(Activity activity) {

        if (instance == null) {
            instance = new RealmController(activity.getApplication());
        }
        return instance;
    }

    public static RealmController with(Application application) {

        if (instance == null) {
            instance = new RealmController(application);
        }
        return instance;
    }

    public static RealmController getInstance() {

        return instance;
    }

    public Realm getRealm() {


        return realm;
    }

    //Refresh the realm istance
    public void refresh() {

        realm.refresh();
    }



    //find all objects in the Book.class
    public RealmResults<RecordBlessing> getBlessingsRecorded() {

        return realm.where(RecordBlessing.class).findAll();
    }

    //query a single item with the given id
    public MyOmerPeriod getPeriodByYear(int startDateYear) {
        int lastPosition = 0;
        try {
            lastPosition = realm.where(MyOmerPeriod.class).equalTo("startYear", startDateYear).findAll().size() - 1;
        } catch (Exception e) {
            e.printStackTrace();
        }
        //return realm.where(MyOmerPeriod.class).equalTo("startYear", startDateYear).findFirst();
        return realm.where(MyOmerPeriod.class).equalTo("startYear", startDateYear).findAll().get(lastPosition);
    }


    public RecordBlessing getRecorderBlessing(int day){
        return realm.where(RecordBlessing.class).equalTo("id",day).findFirst();
    }

    public JournalQuestionModelNew getAnswer(int day, int questionId){

        return realm.where(JournalQuestionModelNew.class).equalTo("id",day).and().equalTo("questionId",questionId).findFirst();
    }


    //find all objects in the JournalQuestionModelNew.class
    public RealmResults<JournalQuestionModelNew> getAllFilledJournals() {

        return realm.where(JournalQuestionModelNew.class).findAll();
    }



    //query example
    public RealmResults<MyOmerPeriod> queryPeriods(String startDate, String endDate) {

        return realm.where(MyOmerPeriod.class)
                .contains("startDate", startDate)
                .or()
                .contains("endDate", endDate)
                .findAll();

    }

}
