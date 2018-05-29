package com.example.user.movietest;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;

public class DramaSearch extends Activity {

    RecyclerView recyclerView;
    MyAdapter myAdapter;
    String s = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drama_search);

        recyclerView = (RecyclerView) findViewById(R.id.drama_search_rv);
        s = getIntent().getStringExtra("search");
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
                        int count = 0;

                        final Document doc = Jsoup.connect("https://www.google.com.tw/search?q="+Uri.encode(s)+"+site%3Aplayq.org%2F"+getIntent().getStringExtra("type")+"&num=20&rlz=1C1AVNE_enTW776TW776&oq="+Uri.encode(s)+"&aqs=chrome.1.69i57j35i39l2j69i60l2j0.3749j0j7&sourceid=chrome&ie=UTF-8").get();
                        final Elements title = doc.select("div[class=ZINbbc xpd]"); //抓取為tr且有class屬性的所有Tag

                        for(int i=0;i<title.size();i++){ //用FOR個別抓取選定的Tag內容
                            HashMap<String,String> item = new HashMap<String,String>();
                            final String name = title.get(i).select("div[class=pIpgAc KKgUze]").text();
                            final String detaillink = title.get(i).select("a[class=C8nzq JTuIPc]").attr("href");

                            if(detaillink.contains("/"+getIntent().getStringExtra("type"))){
                                if(detaillink.endsWith("/")&&!detaillink.endsWith(getIntent().getStringExtra("type")+"/")) {
                                    if(!name.contains("- EyesPlay 線上看")) {
                                        item.put("name", name.replaceAll("- PlayQ 線上看", ""));
                                        item.put("detaillink", detaillink);
                                        myAdapter.addItem(item);
                                        count++;
                                    }
                                }
                            }
                        }

                        if (count==0){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(DramaSearch.this, "非常抱歉,找不到您要的劇名", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                recyclerView.setAdapter(myAdapter);
                                recyclerView.addItemDecoration(new DividerItemDecoration(DramaSearch.this, DividerItemDecoration.VERTICAL));
                                recyclerView.setLayoutManager(new LinearLayoutManager(DramaSearch.this));
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
        //private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();

        public MyAdapter(ArrayList<HashMap<String,String>> newlist) {
            // TODO 自动生成的构造函数存根
            list = newlist;
        }
        public void addItem(HashMap<String, String> item) {
            list.add(item);
        }

        @Override
        public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.drama_search_rv,parent,false);
            MyAdapter.ViewHolder viewHolder = new MyAdapter.ViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(MyAdapter.ViewHolder holder, final int position) {

            holder.name.setText(list.get(position).get("name"));

            //設定圖片
            //holder.link.setTag(list.get(position).get("link"));
            // setImg(holder.link,list.get(position).get("link"));

            //imageLoader.displayImage(list.get(position).get("link"), holder.link, options, animateFirstListener);

            //
            holder.ll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ///Intent activity
                    Intent intent = new Intent(DramaSearch.this,Drama_detail.class);
                    intent.putExtra("name",list.get(position).get("name"));
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
            public TextView name;
            public LinearLayout ll;

            public ViewHolder(View itemView) {
                super(itemView);
                name = (TextView) itemView.findViewById(R.id.name);
                ll= (LinearLayout) itemView.findViewById(R.id.ll);
            }
        }
    }
}
