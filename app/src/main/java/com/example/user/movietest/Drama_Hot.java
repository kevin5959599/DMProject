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

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;

public class Drama_Hot extends Activity {
    RecyclerView recyclerView;
    MyAdapter myAdapter;
    Tools tools = new Tools();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.jpdrama_hot);

        recyclerView = (RecyclerView)findViewById(R.id.jpdrama_rv);
        new HttpAsynTask().execute();
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
                        myAdapter = new MyAdapter(list);

                        Document doc = Jsoup.connect("http://www.dramasq.com/"+getIntent().getStringExtra("type")+"/").get();
                        Elements title = doc.select("div[class=drama sizing]"); //抓取為tr且有class屬性的所有Tag
                        for(int i=0;i<title.size();i++){ //用FOR個別抓取選定的Tag內容
                            HashMap<String,String> item = new HashMap<String,String>();
                            String name = title.get(i).select("div[class=title sizing]").get(0).text();
                            String [] link1 = title.get(i).select("div[class=imgflat imgcover sizing]").attr("style").split("\\(");//選擇第i個後選取所有為td的Tag
                            String [] link2 = link1[1].split("\\)");
                            String detaillink = title.get(i).select("a").attr("href");


                            Log.d("99999999999999",name);
                            Log.d("88888888888888",link2[0]);
                            Log.d("77777777777777",detaillink);
                            item.put("name",name);
                            item.put("link",link2[0]);
                            item.put("detaillink",detaillink);

                            myAdapter.addItem(item);

                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                recyclerView.setAdapter(myAdapter);
                                recyclerView.addItemDecoration(new DividerItemDecoration(Drama_Hot.this, DividerItemDecoration.VERTICAL));
                                recyclerView.setLayoutManager(new LinearLayoutManager(Drama_Hot.this));
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
    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder>{

        ArrayList<HashMap<String,String>> list=new ArrayList<HashMap<String,String>>();


        public MyAdapter(ArrayList<HashMap<String,String>> newlist) {
            // TODO 自动生成的构造函数存根
            list = newlist;
        }
        public void addItem(HashMap<String, String> item) {
            list.add(item);
        }

        @Override
        public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.jpdrama_rv,parent,false);
            MyAdapter.ViewHolder viewHolder = new MyAdapter.ViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(MyAdapter.ViewHolder holder, final int position) {

            holder.name.setText(list.get(position).get("name"));
            holder.cls.setText(list.get(position).get("actor"));


            //設定圖片
            holder.link.setTag(list.get(position).get("link"));
            setImg(holder.link,list.get(position).get("link"));

            //
            holder.ll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ///Intent activity
                    Intent intent = new Intent(Drama_Hot.this,Drama_detail.class);
                    intent.putExtra("name",list.get(position).get("name"));
                    intent.putExtra("link",list.get(position).get("link"));
                    intent.putExtra("detaillink",list.get(position).get("detaillink"));
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
            tools.imageLoading(Drama_Hot.this,ImgURL,img);
        }
    }
}
