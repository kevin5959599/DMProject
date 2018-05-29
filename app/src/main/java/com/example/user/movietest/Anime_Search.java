package com.example.user.movietest;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.FIFOLimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import static com.example.user.movietest.R.mipmap.ic_launcher;

public class Anime_Search extends Activity {

    RecyclerView recyclerView;
    MyAdapter myAdapter;
    Tools tools = new Tools();
    ImageLoader imageLoader;
    String movie = "",urlStr = "";
    DisplayImageOptions options;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.anime_search);
        init();
        movie = getIntent().getStringExtra("movie");
        urlStr = Uri.encode(movie);
        options = getSimpleOptions();
        imageLoader = ImageLoader.getInstance();
        recyclerView = (RecyclerView)findViewById(R.id.anime_search_rv);

        new HttpAsynTask().execute();

    }

    private DisplayImageOptions getSimpleOptions() {
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnLoading(ic_launcher) //?置?片在下?期??示的?片
                .showImageForEmptyUri(ic_launcher)//?置?片Uri?空或是??的?候?示的?片
                .showImageOnFail(ic_launcher)  //?置?片加?/解??程中???候?示的?片
                .cacheInMemory(true)//?置下?的?片是否?存在?存中
                .cacheOnDisk(true)//?置下?的?片是否?存在SD卡中
                .imageScaleType(ImageScaleType.IN_SAMPLE_INT)//?置?片以如何的??方式?示
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)//?置?片的解??型
                .build();//构建完成
        return options;
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
                        ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
                        myAdapter = new MyAdapter(list);

                        String url = "https://www.myvideo.net.tw/TWM_Video/Portal/servlet_results.jsp?keyword="+urlStr+"&categoryId=cartoon";
                        Document doc = Jsoup.connect(url).get();

                        Elements title = doc.select("div[id=subContainer01_1]").select("li"); //抓取為tr且有class屬性的所有Tag
                        if(title.size()>0) {
                            for (int i = 0; i < title.size(); i++) { //用FOR個別抓取選定的Tag內容
                                HashMap<String, String> item = new HashMap<String, String>();
                                String name = title.select("h3").get(i).text();
                                String link = title.select("img").get(i).attr("src");
                                String detaillink = title.select("a").get(i).attr("href");
                                item.put("name", name);

                                item.put("link", link);
                                item.put("detaillink", "https://www.myvideo.net.tw/TWM_Video/Portal/" + detaillink);

                                myAdapter.addItem(item);
                            }
                        }else{
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(Anime_Search.this, "非常抱歉,找不到您要的動畫", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                recyclerView.setAdapter(myAdapter);
                                recyclerView.addItemDecoration(new DividerItemDecoration(Anime_Search.this, DividerItemDecoration.VERTICAL));
                                recyclerView.setLayoutManager(new LinearLayoutManager(Anime_Search.this));
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
        private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();

        public MyAdapter(ArrayList<HashMap<String,String>> newlist) {
            // TODO 自?生成的构造函?存根
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
            imageLoader.displayImage(list.get(position).get("link"), holder.link, options, animateFirstListener);
            //
            holder.ll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ///Intent activity
                    Intent intent = new Intent(Anime_Search.this,Anime_detail.class);
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
            tools.imageLoading(Anime_Search.this,ImgURL,img);
        }
    }
    private void init(){
        ImageLoaderConfiguration config = new ImageLoaderConfiguration
                .Builder(this)
                .memoryCacheExtraOptions(480, 800) //保存每個緩存圖片的最大寬高
                .threadPriority(Thread.NORM_PRIORITY - 2) //線池中的緩存數
                .denyCacheImageMultipleSizesInMemory() //禁止緩存多張圖片
                .memoryCache(new FIFOLimitedMemoryCache(2 * 1024 * 1024))//?存策略
//                .memoryCacheSize(50 * 1024 * 1024) //設置內存緩存的大小
                .diskCacheFileNameGenerator(new Md5FileNameGenerator()) //?存文件名的保存方式
//                .diskCacheSize(200 * 1024 * 1024) //緩存大小
                .tasksProcessingOrder(QueueProcessingType.LIFO) //工作序列
                .diskCacheFileCount(200) //緩存的文件數量
                .build();
        if (!ImageLoader.getInstance().isInited()) {//偵測如果imagloader已經init，就不再init
            ImageLoader.getInstance().init(config);
        }
    }
    /**
     * ?片加?第一次?示?听器
     * @author Administrator
     *
     */
    private static class AnimateFirstDisplayListener extends SimpleImageLoadingListener {

        static final List<String> displayedImages = Collections.synchronizedList(new LinkedList<String>());

        @Override
        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
            if (loadedImage != null) {
                ImageView imageView = (ImageView) view;
                // 是否第一次?示
                boolean firstDisplay = !displayedImages.contains(imageUri);
                if (firstDisplay) {
                    // ?片淡入效果
                    FadeInBitmapDisplayer.animate(imageView, 200);
                    displayedImages.add(imageUri);
                }
            }
        }
    }
}

