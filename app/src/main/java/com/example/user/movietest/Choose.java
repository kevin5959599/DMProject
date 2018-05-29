package com.example.user.movietest;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Choose extends Activity {

    Button mv, kr, jp, anime;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose);

        mv = (Button) findViewById(R.id.mv);
        mv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Choose.this,Movie_Hot.class);
                startActivity(intent);
            }
        });
        kr = (Button) findViewById(R.id.kr);
        kr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Choose.this,Drama_Hot.class);
                intent.putExtra("type","kr");
                startActivity(intent);
            }
        });
        jp = (Button) findViewById(R.id.jp);
        jp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Choose.this,Drama_Hot.class);
                intent.putExtra("type","jp");
                startActivity(intent);
            }
        });
        anime = (Button) findViewById(R.id.anime);
        anime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Choose.this,Anime_Hot.class);
                intent.putExtra("type","jp");
                startActivity(intent);
            }
        });

    }
}
