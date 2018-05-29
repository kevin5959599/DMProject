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
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class Anime_detail extends Activity {

    String link = "",dmname = "";
    Tools tools = new Tools();
    public TextView name,cls,detail,rating;
    public ImageView detaillink;
    private String GET_URL = "https://lifego04.000webhostapp.com/dmcontent.php";
    LinearLayout comment_ll,rating_ll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.anime_detail);
        initialView();
        link = getIntent().getStringExtra("detaillink");

        new HttpAsynTask().execute();
        new RatingAsynTask().execute();

        rating = (TextView) findViewById(R.id.rating);
        rating_ll = (LinearLayout) findViewById(R.id.rating_ll);
        rating_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Anime_detail.this,DMRate.class);
                if(!dmname.equals("")){
                    intent.putExtra("dmname",dmname);
                    intent.putExtra("type","anime");
                }
                startActivity(intent);
            }
        });

        comment_ll = (LinearLayout) findViewById(R.id.comment_ll);
        comment_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(Anime_detail.this)
                        .setTitle("使用者及觀眾權益")
                        .setMessage("前方可能有暴雷情況發生，請評估")
                        .setPositiveButton("謹慎進入", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(Anime_detail.this,Comment.class);
                                if(!dmname.equals("")){
                                    intent.putExtra("dmname",dmname);
                                    intent.putExtra("type","anime");
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
        tools.imageLoading(Anime_detail.this,ImgURL,img);
    }
    void initialView(){

        name = (TextView)findViewById(R.id.name);
        detaillink = (ImageView) findViewById(R.id.link);
        detail = (TextView) findViewById(R.id.detail);

        name.setText(getIntent().getStringExtra("name"));
        dmname = getIntent().getStringExtra("name");
        detaillink.setTag(getIntent().getStringExtra("link"));
        setImg(detaillink,getIntent().getStringExtra("link"));
    }

    private class HttpAsynTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            return null;
        }
        @Override
        protected void onPostExecute(String result) {

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Document doc = Jsoup.connect(link).get();
                        Elements title = doc.select("meta[name=description]"); //抓取為tr且有class屬性的所有Tag

                        String dl = title.attr("content");
                        //String date = title.get(1).text();
                        final String result = dl;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                detail.setText(result);
                            }
                        });

                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                new HttpAsynTask().execute();
                            }
                        });
                        e.printStackTrace();
                    }
                }
            }).start();

        }
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