package com.paymentwall.pwunifiedsdk.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.EditText;

/**
 * Created by nguyen.anh on 7/5/2017.
 */

public class StoredCardEditText extends EditText {

    private String cardNumber;
    private String permanentToken;

    public StoredCardEditText(Context context) {
        super(context);
        setFont(context);
    }

    public StoredCardEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        setFont(context);
    }

    public StoredCardEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setFont(context);
    }

    private void setFont(Context context) {
        Typeface face = Typeface.createFromAsset(context.getAssets(),
                "fonts/ProximaNova-Regular.otf");
        this.setTypeface(face);
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getPermanentToken() {
        return permanentToken;
    }

    public void setPermanentToken(String permanentToken) {
        this.permanentToken = permanentToken;
    }
}
