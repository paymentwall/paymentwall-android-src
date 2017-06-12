package com.paymentwall.pandapptest.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;

import com.paymentwall.pandapptest.R;
import com.squareup.picasso.Picasso;

/**
 * Created by Hieu.Hoa.Luong on 11/12/2014.
 */
public class IntroActivity extends CommonActivity{
    private static IntroActivity instance;
    public static IntroActivity getInstance()
    {
        return instance;
    }

    public final Handler mHandler = new Handler();

    protected ImageView stats;
    protected ImageView title;
    protected ImageView startButton;
    protected ImageView background;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.intro);
        stats = (ImageView) findViewById(R.id.intro_stats);
        title = (ImageView) findViewById(R.id.intro_title);
        startButton = (ImageView) findViewById(R.id.intro_start);
        background = (ImageView) findViewById(R.id.intro_background);

        Picasso.with(this).load(R.drawable.stats_bar).into(stats);
        Picasso.with(this).load(R.drawable.background).fit().centerCrop().into(background);
        Picasso.with(this).load(R.drawable.game_title).into(title);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(IntroActivity.this, LLandActivity.class);
//                startActivity(intent);
                goToMainActivity();
            }
        });
        instance = this;
    }

    public void goToMainActivity()
    {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    @SuppressLint("NewApi")
    @Override
    protected void onResume() {
        super.onResume();
        goFullscreen();
    }
}
