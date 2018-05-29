package com.example.user.movietest;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by Blue_bell on 2018/5/20.
 */

public class Detail extends Activity {

    LinearLayout comment_ll,rating_ll;
    Tools tools = new Tools();
    public TextView name,cls,detail,rating;
    private String GET_URL = "https://lifego04.000webhostapp.com/dmcontent.php";
    String dmname = "";
    public ImageView link;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail);

        initialView();
        new RatingAsynTask().execute();

        rating = (TextView) findViewById(R.id.rating);
        rating_ll = (LinearLayout) findViewById(R.id.rating_ll);
        rating_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Detail.this,DMRate.class);
                if(!dmname.equals("")){
                    intent.putExtra("dmname",dmname);
                    intent.putExtra("type","mv");
                }
                startActivity(intent);
            }
        });

        comment_ll = (LinearLayout) findViewById(R.id.comment_ll);
        comment_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(Detail.this)
                        .setTitle("使用者及觀眾權益")
                        .setMessage("前方可能有暴雷情況發生，請評估")
                        .setPositiveButton("謹慎進入", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(Detail.this,Comment.class);
                                if(!dmname.equals("")){
                                    intent.putExtra("dmname",dmname);
                                    intent.putExtra("type","mv");
                                }
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton("我在想想", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .show();
            }
        });

    }
    void setImg(ImageView img, String ImgURL){
        tools.imageLoading(Detail.this,ImgURL,img);
    }
    void initialView(){

        String namesplit = getIntent().getStringExtra("name");

        name = (TextView)findViewById(R.id.name);
        cls = (TextView) findViewById(R.id.cls);
        link = (ImageView) findViewById(R.id.link);
        detail = (TextView) findViewById(R.id.detail);

        if(namesplit.contains(". ")){
            String [] newname = namesplit.split(" ");
            name.setText(newname[1]);
            dmname = newname[1];
        }else{
            name.setText(namesplit);
            dmname = namesplit;
        }

        cls.setText(getIntent().getStringExtra("type").equals("")?"無分類":getIntent().getStringExtra("type"));
        detail.setText(getIntent().getStringExtra("detail"));
        link.setTag(getIntent().getStringExtra("link"));
        setImg(link,getIntent().getStringExtra("link"));
    }
    private class RatingAsynTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String result = DownloadImg.executeQuery(GET_URL, "SELECT avg(average_score) AS avg FROM dm_score WHERE dmname='" + dmname + "' AND dmtype = 'mv'");
            return result;
        }

        @Override
        protected void onPostExecute(final String result) {

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        JSONArray jsonArray = new JSONArray(result);
                        final int num = jsonArray.length();
                        JSONObject jsonData = jsonArray.getJSONObject(0);
                        final double avg_f = jsonData.getDouble("avg")+0.05;
                        String avg = String.valueOf(avg_f);
                        final String avg_2 = avg.substring(0, avg.indexOf(".") + 2);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(num==0){
                                    rating.setText("尚無評分");
                                }else{
                                    rating.setText("綜合評分 : "+avg_2);
                                }
                            }
                        });

                    }catch(Exception e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                rating.setText("尚無評分");
                            }
                        });
                        Log.e("log_tag", e.toString());
                    }
                }
            }).start();
        }
    }
}
