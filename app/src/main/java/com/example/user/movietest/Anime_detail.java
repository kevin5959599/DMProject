package com.example.user.movietest;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class Anime_detail extends Activity {

    String link = "";
    Tools tools = new Tools();
    public TextView name,cls,detail;
    public ImageView detaillink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.anime_detail);
        initialView();
        link = getIntent().getStringExtra("detaillink");
        new HttpAsynTask().execute();
    }
    void setImg(ImageView img, String ImgURL){
        tools.imageLoading(Anime_detail.this,ImgURL,img);
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
                        Elements title = doc.select("div[class=data_intro]"); //抓取為tr且有class屬性的所有Tag
                        final String result = title.get(0).select("p").text();
                        String result2 = "";
                        Elements title2 = doc.select("ul[class=data_type]").get(0).select("li");
                        for(int i=0;i<title2.size();i++){
                            result2 += ( "【" + title2.get(i).select("span").text() + "】: " + title2.get(i).text().replace(title2.get(i).select("span").text(),"") + "\n");
                        }

                        final String finalResult = result2;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                detail.setText(finalResult +"\n"+result + " ...");
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
