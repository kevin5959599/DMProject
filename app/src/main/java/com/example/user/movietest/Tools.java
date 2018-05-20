package com.example.user.movietest;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.FIFOLimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import static com.example.user.movietest.R.mipmap.ic_launcher;

/**
 * Created by user on 2018/5/17.
 */

public class Tools {
    private static final String handle = Tools.class.getSimpleName();

    public void imageLoading(Context context, final String url, final ImageView imageView) {
        ImageLoaderConfiguration config = new ImageLoaderConfiguration
                .Builder(context)
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

        DisplayImageOptions ImageUsing = new DisplayImageOptions.Builder()
                .showImageOnLoading(ic_launcher)
                .showImageForEmptyUri(ic_launcher)
                .showImageOnFail(ic_launcher).cacheInMemory(true)//緩存
                .cacheOnDisc(true).bitmapConfig(Bitmap.Config.RGB_565)
                .imageScaleType(ImageScaleType.IN_SAMPLE_INT)
                .build();

        if (!ImageLoader.getInstance().isInited()) {//偵測如果imagloader已經init，就不再init
            Log.d(handle, "CheckPoint init");
            ImageLoader.getInstance().init(config);
        }
        ImageSize mImageSize = new ImageSize(400, 400);
        ImageLoader.getInstance().loadImage(url, mImageSize, ImageUsing, new SimpleImageLoadingListener() {
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                super.onLoadingComplete(imageUri, view, loadedImage);
                if (url.equals(imageView.getTag()))
                    imageView.setImageBitmap(loadedImage);
            }

        });
    }
}
