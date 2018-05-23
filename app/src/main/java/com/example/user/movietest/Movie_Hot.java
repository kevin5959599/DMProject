package com.example.user.movietest;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
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

public class Movie_Hot extends Activity {

    RecyclerView recyclerView;
    MyAdapter myAdapter;
    Tools tools = new Tools();
    ImageLoader imageLoader;
    private DisplayImageOptions options;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movie_hot);
        init();
        options = getSimpleOptions();
        imageLoader = ImageLoader.getInstance();
        recyclerView = (RecyclerView)findViewById(R.id.main_rv);
        new HttpAsynTask().execute();
    }
    private DisplayImageOptions getSimpleOptions() {
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnLoading(ic_launcher) //设置图片在下载期间显示的图片
                .showImageForEmptyUri(ic_launcher)//设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(ic_launcher)  //设置图片加载/解码过程中错误时候显示的图片
                .cacheInMemory(true)//设置下载的图片是否缓存在内存中
                .cacheOnDisk(true)//设置下载的图片是否缓存在SD卡中
                .imageScaleType(ImageScaleType.IN_SAMPLE_INT)//设置图片以如何的编码方式显示
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)//设置图片的解码类型
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

                            }
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    recyclerView.setAdapter(myAdapter);
                                    recyclerView.addItemDecoration(new DividerItemDecoration(Movie_Hot.this, DividerItemDecoration.VERTICAL));
                                    recyclerView.setLayoutManager(new LinearLayoutManager(Movie_Hot.this));
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
                imageLoader.displayImage(list.get(position).get("link"), holder.link, options, animateFirstListener);

                //
                holder.ll.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        //Toast.makeText(Movie_Hot.this,list.get(position).get("name"), Toast.LENGTH_SHORT).show();


                        ///Intent activity
                        Intent intent = new Intent(Movie_Hot.this,Detail.class);
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
                tools.imageLoading(Movie_Hot.this,ImgURL,img);
            }
        }
    private void init(){
        ImageLoaderConfiguration config = new ImageLoaderConfiguration
                .Builder(this)
                .memoryCacheExtraOptions(480, 800) //保存每個緩存圖片的最大寬高
                .threadPriority(Thread.NORM_PRIORITY - 2) //線池中的緩存數
                .denyCacheImageMultipleSizesInMemory() //禁止緩存多張圖片
                .memoryCache(new FIFOLimitedMemoryCache(2 * 1024 * 1024))//缓存策略
//                .memoryCacheSize(50 * 1024 * 1024) //設置內存緩存的大小
                .diskCacheFileNameGenerator(new Md5FileNameGenerator()) //缓存文件名的保存方式
//                .diskCacheSize(200 * 1024 * 1024) //緩存大小
                .tasksProcessingOrder(QueueProcessingType.LIFO) //工作序列
                .diskCacheFileCount(200) //緩存的文件數量
                .build();
        if (!ImageLoader.getInstance().isInited()) {//偵測如果imagloader已經init，就不再init
            ImageLoader.getInstance().init(config);
        }
    }
    /**
     * 图片加载第一次显示监听器
     * @author Administrator
     *
     */
    private static class AnimateFirstDisplayListener extends SimpleImageLoadingListener {

        static final List<String> displayedImages = Collections.synchronizedList(new LinkedList<String>());

        @Override
        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
            if (loadedImage != null) {
                ImageView imageView = (ImageView) view;
                // 是否第一次显示
                boolean firstDisplay = !displayedImages.contains(imageUri);
                if (firstDisplay) {
                    // 图片淡入效果
                    FadeInBitmapDisplayer.animate(imageView, 200);
                    displayedImages.add(imageUri);
                }
            }
        }
    }
}
