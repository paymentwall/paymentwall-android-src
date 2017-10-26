package com.paymentwall.pandapptest.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;

import com.paymentwall.pandapptest.R;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

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
        test();
    }

    private void test() {
        TreeMap<String, String> psAlipay = new TreeMap<>();
        String responseBody = "{\"type\":\"signature\",\"code\":0,\"data\":{\"app_id\":\"2017022705922567\",\"biz_content\":\"{\\\"product_code\\\":\\\"recharge\\\",\\\"total_amount\\\":\\\"19.78\\\",\\\"subject\\\":\\\"w130126489\\\",\\\"body\\\":\\\"Oroo\\\",\\\"out_trade_no\\\":\\\"w130126489\\\"}\",\"charset\":\"utf-8\",\"method\":\"alipay.trade.app.pay\",\"notify_url\":\"https:\\/\\/api.paymentwall.com\\/api\\/paymentpingback\\/alipay\",\"sign_type\":\"RSA\",\"timestamp\":\"2017-10-20 04:39:20\",\"version\":\"1.0\",\"sign\":\"dcC+b95dr9zL8rPao0m87rCaIXAMBOnm6uTrk9Vs9EQ1XpNMbVmVel3aYrIdwwcFdRhVo3dtDUfh3QL1aFag8dtHLGcp2tfNsBEQ3IcfX17CGwacf9xsmz5DKnvnAHtxddIsMkNYdyYkZOZqDGMNAMkPWXitMSoxMGJmHMQMSHk=\"}}";
        try {
            JSONObject rootObj = new JSONObject(responseBody);
            if (rootObj.has("data")) {
                JSONObject dataObject = rootObj.getJSONObject("data");
                Iterator<String> keyIterator = dataObject.keys();
                while (keyIterator.hasNext()) {
                    String key = keyIterator.next();
                    if(!dataObject.isNull(key)) {
                        JSONObject tempObject = dataObject.optJSONObject(key);
                        JSONArray tempArray = dataObject.optJSONArray(key);
                        if(tempObject == null && tempArray == null) {
                            psAlipay.put(key, dataObject.optString(key));
                        }
                    }
                }

            }
            System.out.println("PsAliTest");
            for(Map.Entry<String, String> entry : psAlipay.entrySet()) {
                System.out.println(entry.getKey()+":"+entry.getValue());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
