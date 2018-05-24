package com.example.user.movietest;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class Drama_detail extends Activity {

    String link = "";
    Tools tools = new Tools();
    public TextView name,cls,detail;
    public ImageView detaillink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drama_detail);
        initialView();
        link = getIntent().getStringExtra("detaillink");
        new HttpAsynTask().execute();
    }
    void setImg(ImageView img, String ImgURL){
        tools.imageLoading(Drama_detail.this,ImgURL,img);
    }
    void initialView(){

        name = (TextView)findViewById(R.id.name);
        detaillink = (ImageView) findViewById(R.id.link);
        detail = (TextView) findViewById(R.id.detail);

        name.setText(getIntent().getStringExtra("name"));
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
                        Elements title = doc.select("div[class=intro sizing]"); //抓取為tr且有class屬性的所有Tag
                        final String result = title.get(0).text();

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                detail.setText(result.replaceAll(" ","\n"));
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
}
