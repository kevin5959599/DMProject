package com.example.user.movietest;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Blue_bell on 2018/5/21.
 */

public class YahooMovie extends Activity {
    RecyclerView recyclerView;
    YahooMovie.MyAdapter myAdapter;
    Tools tools = new Tools();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movie_hot);

        recyclerView = (RecyclerView)findViewById(R.id.main_rv);
        new YahooMovie.HttpAsynTask().execute();
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
                        myAdapter = null;
                        ArrayList<HashMap<String,String>> list = new ArrayList<HashMap<String,String>>();
                        myAdapter = new YahooMovie.MyAdapter(list);

                        final Document doc = Jsoup.connect("https://movies.yahoo.com.tw/?guccounter=1").get();
                        final Elements title = doc.select("div.title"); //抓取為tr且有class屬性的所有Tag


                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Log.d("777777777","=" + doc);

                            }
                        });
                        for(int i=0;i<title.size();i++){ //用FOR個別抓取選定的Tag內容
                            HashMap<String,String> item = new HashMap<String,String>();
                            String name = title.get(i).select("span").text() ;


                            item.put("name",name);
                            myAdapter.addItem(item);
                            //Log.d("878787878787",detail);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                            Log.d("777777777","=" +String.valueOf(title.size()));

                                }
                            });
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                recyclerView.setAdapter(myAdapter);
                                recyclerView.addItemDecoration(new DividerItemDecoration(YahooMovie.this, DividerItemDecoration.VERTICAL));
                                recyclerView.setLayoutManager(new LinearLayoutManager(YahooMovie.this));
                            }
                        });
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }).start();

        }
    }
    public class MyAdapter extends RecyclerView.Adapter<YahooMovie.MyAdapter.ViewHolder>{

        ArrayList<HashMap<String,String>> list=new ArrayList<HashMap<String,String>>();


        public MyAdapter(ArrayList<HashMap<String,String>> newlist) {
            // TODO 自动生成的构造函数存根
            list = newlist;
        }
        public void addItem(HashMap<String, String> item) {
            list.add(item);
        }

        @Override
        public YahooMovie.MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.main_rv,parent,false);
            YahooMovie.MyAdapter.ViewHolder viewHolder = new YahooMovie.MyAdapter.ViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(YahooMovie.MyAdapter.ViewHolder holder, final int position) {

            holder.name.setText(list.get(position).get("name"));
            holder.cls.setText(list.get(position).get("type"));


            //設定圖片
            holder.link.setTag(list.get(position).get("link"));
            setImg(holder.link,list.get(position).get("link"));

            //
            holder.ll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(YahooMovie.this,list.get(position).get("name"), Toast.LENGTH_SHORT).show();


                    ///Intent activity
                    Intent intent = new Intent(YahooMovie.this,Detail.class);
                    intent.putExtra("name",list.get(position).get("name"));
                    intent.putExtra("type",list.get(position).get("type"));
                    intent.putExtra("link",list.get(position).get("link"));
                    intent.putExtra("detail",list.get(position).get("detail"));
                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return list==null?0:list.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public TextView name,cls;
            public ImageView link;
            public LinearLayout ll;

            public ViewHolder(View itemView) {
                super(itemView);
                name = (TextView) itemView.findViewById(R.id.name);
                cls = (TextView) itemView.findViewById(R.id.cls);
                link = (ImageView) itemView.findViewById(R.id.link);
                ll= (LinearLayout) itemView.findViewById(R.id.ll);
            }
        }
        void setImg(ImageView img, String ImgURL){
            tools.imageLoading(YahooMovie.this,ImgURL,img);
        }
    }
}
