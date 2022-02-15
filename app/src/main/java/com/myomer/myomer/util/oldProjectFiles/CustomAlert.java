package com.myomer.myomer.util.oldProjectFiles;


import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.myomer.myomer.R;
import com.myomer.myomer.plist_parser.PListDict;
import com.myomer.myomer.plist_parser.PListException;
import com.myomer.myomer.plist_parser.PListParser;

import java.io.IOException;
import java.io.InputStream;


public class CustomAlert {

    public static void showWeeklyAlert(Activity activity, int dayCount) {

        Dialog dialog = new Dialog(activity, R.style.autoAdjustDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.weekly_alert_dialog);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        TextView imgClose = dialog.findViewById(R.id.tvClose);
        TextView tvTitleOne = dialog.findViewById(R.id.tvDayLabel);
        TextView tvTitleTwo = dialog.findViewById(R.id.tvTitle);
        TextView tvContent = dialog.findViewById(R.id.tvContent);
        ImageView ivShare = dialog.findViewById(R.id.ivShare);

        AssetManager assetManager = activity.getAssets();
        try {
            InputStream stream = null;

            stream = assetManager.open("weeks/week" + dayCount + ".plist");

            PListDict dict = PListParser.parse(stream);

            String title = dict.getString("title");
            String subTitle = dict.getString("subtitle");
            String content = dict.getString("content");
            tvTitleOne.setText(title);
            tvTitleTwo.setText(subTitle);
            tvContent.setText(content);


            String color = dict.getString("color");
            tvTitleOne.setTextColor(Color.parseColor("#" + color));
            tvTitleTwo.setTextColor(Color.parseColor("#" + color));
            Typeface type = Typeface.createFromAsset(activity.getAssets(), "fonts/Biko_Bold.otf");
            tvTitleOne.setTypeface(type);
            tvTitleTwo.setTypeface(type);
            Typeface type1 = Typeface.createFromAsset(activity.getAssets(), "fonts/Biko-Regular.otf");
            tvContent.setTypeface(type1);

            //Utilty.shareInfo(activity,(LinearLayout) dialog.findViewById(R.id.layoutContent),ivShare,null);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (PListException e) {
            e.printStackTrace();
        }


        ivShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

                try {
                    String text = "";
//                    text = tvContent.getText().toString().trim() + "\n\n" + activity.getResources().getString(R.string.download_now)
//                            + "https://www.meaningfullife.com/myomer";
                    text = tvContent.getText().toString().trim() + "\n\n" + activity.getResources().getString(R.string.download_now)
                            + "http://bit.ly/myomer";
                    Intent i = new Intent(Intent.ACTION_SEND);
                    i.setType("text/plain");
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                    i.putExtra(Intent.EXTRA_SUBJECT, activity.getResources().getString(R.string.app_name));
                    i.putExtra(Intent.EXTRA_TEXT, text);
                    activity.startActivity(Intent.createChooser(i, activity.getResources().getString(R.string.share)));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });


        dialog.show();

    }


}