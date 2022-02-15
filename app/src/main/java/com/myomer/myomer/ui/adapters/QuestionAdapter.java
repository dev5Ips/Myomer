package com.myomer.myomer.ui.adapters;


import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.viewpager.widget.PagerAdapter;

import com.myomer.myomer.R;
import com.myomer.myomer.data.local.models.JournalQuestionModelNew;
import com.myomer.myomer.data.local.plist_parser.PListArray;
import com.myomer.myomer.data.local.plist_parser.PListDict;
import com.myomer.myomer.data.local.plist_parser.PListException;
import com.myomer.myomer.data.local.plist_parser.PListParser;
import com.myomer.myomer.util.oldProjectFiles.Utilty;
import com.myomer.myomer.util.realm.RealmController;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import io.realm.Realm;


/**
 * Created by Sagar IPS
 */

public class QuestionAdapter extends PagerAdapter {
    Context mContext;
    LayoutInflater mLayoutInflater;
    private PListArray questions;
    int day;
    String color;
    boolean addToDB = false;
    String title, subTitle;

    public QuestionAdapter(Context context, PListArray questions, int day, String color, String title, String subTitle) {
        mContext = context;
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.questions = questions;
        this.day = day;
        this.color = color;
        this.title = title;
        this.subTitle = subTitle;
    }

    @Override
    public int getCount() {
        return questions.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((RelativeLayout) object);
    }

    /**
     * Load all questions of particular day
     *
     * @param container
     * @param position
     * @return
     */
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View itemView = mLayoutInflater.inflate(R.layout.question_item_view, container, false);

        TextView tvQuestion = (TextView) itemView.findViewById(R.id.tvQuestion);
        EditText etAnswer = (EditText) itemView.findViewById(R.id.etAnsuwer);
        TextView tvTitleOne = (TextView) itemView.findViewById(R.id.tvTitleOne);
        TextView tvDayLabel = (TextView) itemView.findViewById(R.id.tvDayLabel);
        TextView tvTitleTwo = (TextView) itemView.findViewById(R.id.tvTitleTwo);
        TextView tvPageNumber = (TextView) itemView.findViewById(R.id.tvPageNumber);


        if (questions != null && questions.size() > 0) {
            tvPageNumber.setVisibility(View.VISIBLE);
            tvPageNumber.setText((position + 1) + " of " + questions.size());
        } else {
            tvPageNumber.setVisibility(View.GONE);
        }
        tvDayLabel.setText("Day " + day);
        tvTitleOne.setText(title);
        tvTitleTwo.setText(subTitle);

        InputStream stream = null;
        AssetManager assetManager = mContext.getAssets();
        String color = null;
        try {
            stream = assetManager.open("days/day" + day + ".plist");

            PListDict dict = PListParser.parse(stream);

            int week = dict.getInt("week");
            InputStream inputStream = null;

            inputStream = assetManager.open("weeks/week" + week + ".plist");

            PListDict weekDict = PListParser.parse(inputStream);
            color = weekDict.getString("color");
            tvTitleOne.setTextColor(Color.parseColor("#" + color));
            tvTitleTwo.setTextColor(Color.parseColor("#" + color));
            Typeface type = Typeface.createFromAsset(mContext.getAssets(), "fonts/Biko_Bold.otf");
            tvTitleOne.setTypeface(type);
            tvTitleTwo.setTypeface(type);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            PListDict question = questions.getPListDict(position);
            final int questionId = question.getInt("id");
            String quest = question.getString("question");


            if (questions.size() != 0)
                tvQuestion.setText(TextUtils.isEmpty(quest) ? "Please check back tomorrow for more journal questions." : quest);
            else
                tvQuestion.setText("Please check back tomorrow for more journal questions.");
//            tvQuestion.setTextColor(Color.parseColor("#" + color));
            tvQuestion.setTextColor(Color.parseColor("#000000"));


            JournalQuestionModelNew JournalQuestionModelNew = RealmController.with((Activity) mContext).getAnswer(day, questionId);
            if (JournalQuestionModelNew != null)
                etAnswer.setText(JournalQuestionModelNew.getAnswer());
            Typeface type = Typeface.createFromAsset(mContext.getAssets(), "fonts/Biko_Bold.otf");
            tvQuestion.setTypeface(type);
            Typeface type1 = Typeface.createFromAsset(mContext.getAssets(), "fonts/Biko_Regular.otf");
            etAnswer.setTypeface(type1);
            /**
             * Store answer in realm for particular day
             */
            etAnswer.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(final Editable editable) {

                    try {
                        Realm realm = RealmController.with((Activity) mContext).getRealm();
                        realm.beginTransaction();
                        JournalQuestionModelNew rb = new JournalQuestionModelNew();

                        rb.setId(day);
                        rb.setAnswer(editable.toString());
                        rb.setQuestionId(questionId);

                        Date c = Calendar.getInstance().getTime();
                        SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");
                        SimpleDateFormat tf = new SimpleDateFormat("hh:mm a");
                        rb.setDate(df.format(c));
                        rb.setTime(tf.format(c));
                        rb.setQuestion(tvQuestion.getText().toString());
                        rb.setYear(Utilty.getYear(new Date()));
                        realm.copyToRealmOrUpdate(rb);
                        realm.commitTransaction();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            });
        } catch (PListException e) {
            e.printStackTrace();
        }
        container.addView(itemView);
        return itemView;
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

