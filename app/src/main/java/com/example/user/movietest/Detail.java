package com.example.user.movietest;

import android.app.Activity;
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

public class Detail extends Activity {

    Tools tools = new Tools();
    public TextView name,cls,detail;
    public ImageView link;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail);

        initialView();
    }
    void setImg(ImageView img, String ImgURL){
        tools.imageLoading(Detail.this,ImgURL,img);
    }
    void initialView(){
        name = (TextView)findViewById(R.id.name);
        cls = (TextView) findViewById(R.id.cls);
        link = (ImageView) findViewById(R.id.link);
        detail = (TextView) findViewById(R.id.detail);

        name.setText(getIntent().getStringExtra("name"));
        cls.setText(getIntent().getStringExtra("type"));
        detail.setText(getIntent().getStringExtra("detail"));
        link.setTag(getIntent().getStringExtra("link"));
        setImg(link,getIntent().getStringExtra("link"));
        Log.d("9999999999",getIntent().getStringExtra("detail"));
    }





}
