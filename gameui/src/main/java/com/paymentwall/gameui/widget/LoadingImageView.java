package com.paymentwall.gameui.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

/**
 * Created by nguyen.anh on 6/15/2017.
 */

public class LoadingImageView extends ImageView {

    protected RotateAnimation rotateAnim;

    public LoadingImageView(Context context) {
        super(context);
        setFont(context);
    }

    public LoadingImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setFont(context);
    }

    public LoadingImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setFont(context);
    }

    private void setFont(Context context) {
        rotateAnim = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnim.setInterpolator(new LinearInterpolator());
        rotateAnim.setRepeatCount(Animation.INFINITE);
        rotateAnim.setDuration(800);
        startAnimation(rotateAnim);

    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

    }

}
