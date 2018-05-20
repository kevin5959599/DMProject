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
 * Created by Blue_bell on 2018/5/20.
 */

public class KoreanDrama_Hot extends Activity {
    RecyclerView recyclerView;
    KoreanDrama_Hot.MyAdapter myAdapter;
    Tools tools = new Tools();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = (RecyclerView)findViewById(R.id.main_rv);
        new KoreanDrama_Hot.HttpAsynTask().execute();
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
                        myAdapter = new KoreanDrama_Hot.MyAdapter(list);

                        Document doc = Jsoup.connect("https://www.y3600.com/hanju/renqi/").get();
                        Elements title = doc.select("div.m-ddone"); //抓取為tr且有class屬性的所有Tag
                        for(int i=0;i<20;i++){ //用FOR個別抓取選定的Tag內容
                            String j = String.valueOf(i);
                            Log.d("number",j);
                            HashMap<String,String> item = new HashMap<String,String>();
                            String name = title.get(i).select("UL").select("B").text();
                            String actor = title.get(i).select("LI.zyy").text();//選擇第i個後選取所有為td的Tag
                            String link = doc.select("IMG").get(i).attr("src");

                            String detail = title.get(i).select("div.description").text();
                            name.split(" ");
                            actor.split(" ");
                            item.put("name",name);
                            item.put("actor",actor);
                            item.put("link",link);
                            item.put("detail",detail);
                            myAdapter.addItem(item);
                            Log.d("878787878787",detail);
                            Log.d("6666666",actor);
                            Log.d("777777777",name);

                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                recyclerView.setAdapter(myAdapter);
                                recyclerView.addItemDecoration(new DividerItemDecoration(KoreanDrama_Hot.this, DividerItemDecoration.VERTICAL));
                                recyclerView.setLayoutManager(new LinearLayoutManager(KoreanDrama_Hot.this));
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
    public class MyAdapter extends RecyclerView.Adapter<KoreanDrama_Hot.MyAdapter.ViewHolder>{

        ArrayList<HashMap<String,String>> list=new ArrayList<HashMap<String,String>>();


        public MyAdapter(ArrayList<HashMap<String,String>> newlist) {
            // TODO 自动生成的构造函数存根
            list = newlist;
        }
        public void addItem(HashMap<String, String> item) {
            list.add(item);
        }

        @Override
        public KoreanDrama_Hot.MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.main_rv,parent,false);
            KoreanDrama_Hot.MyAdapter.ViewHolder viewHolder = new KoreanDrama_Hot.MyAdapter.ViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(KoreanDrama_Hot.MyAdapter.ViewHolder holder, final int position) {

            holder.name.setText(list.get(position).get("name"));
            holder.cls.setText(list.get(position).get("actor"));


            //設定圖片
            holder.link.setTag(list.get(position).get("link"));
            setImg(holder.link,list.get(position).get("link"));

            //
            holder.ll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(KoreanDrama_Hot.this,list.get(position).get("name"), Toast.LENGTH_SHORT).show();


                    ///Intent activity
                    Intent intent = new Intent(KoreanDrama_Hot.this,Detail.class);
                    intent.putExtra("name",list.get(position).get("name"));
                    intent.putExtra("actor",list.get(position).get("actor"));
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
            tools.imageLoading(KoreanDrama_Hot.this,ImgURL,img);
        }
    }
}
