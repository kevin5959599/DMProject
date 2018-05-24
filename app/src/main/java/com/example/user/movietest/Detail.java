package com.example.user.movietest;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

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

        String namesplit = getIntent().getStringExtra("name");

        name = (TextView)findViewById(R.id.name);
        cls = (TextView) findViewById(R.id.cls);
        link = (ImageView) findViewById(R.id.link);
        detail = (TextView) findViewById(R.id.detail);

        if(namesplit.contains(". ")){
            String [] newname = namesplit.split(" ");
            name.setText(newname[1]);
        }else{
            name.setText(namesplit);
        }

        cls.setText(getIntent().getStringExtra("type").equals("")?"無分類":getIntent().getStringExtra("type"));
        detail.setText(getIntent().getStringExtra("detail"));
        link.setTag(getIntent().getStringExtra("link"));
        setImg(link,getIntent().getStringExtra("link"));
    }
}
