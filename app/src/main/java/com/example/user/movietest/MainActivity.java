package com.example.user.movietest;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
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

import com.example.user.movietest.R;
public class MainActivity extends Activity {
    //123
    RecyclerView recyclerView;
    MyAdapter myAdapter;
    Tools tools = new Tools();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = (RecyclerView)findViewById(R.id.main_rv);
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

                            Document doc = Jsoup.connect("https://play.google.com/store/movies/top?hl=zh_TW").get();
                            Elements title = doc.select("div.details"); //抓取為tr且有class屬性的所有Tag
                            for(int i=0;i<20;i++){ //用FOR個別抓取選定的Tag內容
                                HashMap<String,String> item = new HashMap<String,String>();
                                String name = title.get(i).select("a.title").text() ;
                                String type = title.get(i).select("div.subtitle-container").select("span[class=subtitle subtitle-movie-annotation]").text()+
                                        title.get(i).select("div.subtitle-container").select("a[class=subtitle subtitle-movie-category]").text();//選擇第i個後選取所有為td的Tag
                                String link = doc.select("img").get(i).attr("src");

                                String detail = title.get(i).select("div.description").text();

                                item.put("name",name);
                                item.put("type",type);
                                item.put("link",link);
                                item.put("detail",detail);
                                myAdapter.addItem(item);
                                Log.d("878787878787",detail);
                                Log.d("777777777",name);

                            }
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    recyclerView.setAdapter(myAdapter);
                                    recyclerView.addItemDecoration(new DividerItemDecoration(MainActivity.this, DividerItemDecoration.VERTICAL));
                                    recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
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
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.main_rv,parent,false);
                ViewHolder viewHolder = new ViewHolder(view);
                return viewHolder;
            }

            @Override
            public void onBindViewHolder(MyAdapter.ViewHolder holder, final int position) {

                holder.name.setText(list.get(position).get("name"));
                holder.cls.setText(list.get(position).get("type"));




                //設定圖片
                holder.link.setTag(list.get(position).get("link"));
                setImg(holder.link,list.get(position).get("link"));

                //
                holder.ll.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        //Toast.makeText(MainActivity.this,list.get(position).get("name"), Toast.LENGTH_SHORT).show();


                        ///Intent activity
                        Intent intent = new Intent(MainActivity.this,Detail.class);
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
                tools.imageLoading(MainActivity.this,ImgURL,img);
            }
        }
}
