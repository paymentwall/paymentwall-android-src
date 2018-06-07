package com.hieulh.test.pwsdk_test;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.paymentwall.pwunifiedsdk.core.PaymentSelectionActivity;
import com.paymentwall.pwunifiedsdk.core.UnifiedRequest;
import com.paymentwall.sdk.pwlocal.utils.Const;
import com.paymentwall.sdk.pwlocal.utils.Key;
import com.paymentwall.sdk.pwlocal.utils.ResponseCode;

public class MainActivity extends AppCompatActivity {
    public static final String PW_PROJECT_KEY = "dea07cfb5300badc9b002009facad651"; // Kyle's key
    public static final String PW_SECRET_KEY = "dec31fb340f14b63b42f458431e12d12";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button button = findViewById(R.id.main_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buy2();
            }
        });

    }

    private void buy2() {
        UnifiedRequest localUnifiedRequest = new UnifiedRequest();
        localUnifiedRequest.setPwProjectKey("04fdb738807adfbce2345c92c20c9460");
        localUnifiedRequest.setPwSecretKey("e4cce34b451694d3db4fa2dc0af5c115");
        localUnifiedRequest.setAmount(0.49);
        localUnifiedRequest.setCurrency("USD");
        localUnifiedRequest.setItemName("test item");
        localUnifiedRequest.setItemId("12389123012471");
        localUnifiedRequest.setUserId("user_1230918230129");
        localUnifiedRequest.setTimeout(30000);
        localUnifiedRequest.setSignVersion(3);
        localUnifiedRequest.setTestMode(false);
        localUnifiedRequest.addCustomParam("tID", "120398123");
        localUnifiedRequest.addPwLocal();
        localUnifiedRequest.addPwlocalParams("widget", "p1_1");
        localUnifiedRequest.addPwlocalParams("custom_param", null);

        Intent intent = new Intent(getApplicationContext(), PaymentSelectionActivity.class);
        intent.putExtra(Key.REQUEST_MESSAGE, localUnifiedRequest);
//        intent.putExtra("custom_request_type", 1652078734);
//        intent.putExtra("payment_type", 1652078734);
        startActivityForResult(intent, PaymentSelectionActivity.REQUEST_CODE);
    }

    private void buy() {
        UnifiedRequest request = new UnifiedRequest();
        request.setPwProjectKey(PW_PROJECT_KEY);
        request.setPwSecretKey(PW_SECRET_KEY);
        request.setAmount(0.49);
        request.setCurrency("USD");
        request.setItemName("Broadsword");
        request.setItemId("1203912039");
        request.setUserId("123091284asdbcasd");
        request.setSignVersion(3);
        request.setTimeout(30000);
        request.addPwLocal();
        request.setTestMode(true);
//        request.addMint();
        request.addPwlocalParams(Const.P.AG_TYPE, "fixed");
        request.addPwlocalParams(Const.P.EMAIL, "email@example.com");
        request.addPwlocalParams(Const.P.WIDGET, "m2_1");
        Intent intent = new Intent(getApplicationContext(), PaymentSelectionActivity.class);
        intent.putExtra(Key.REQUEST_MESSAGE, request);
        startActivityForResult(intent, PaymentSelectionActivity.REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PaymentSelectionActivity.REQUEST_CODE) {
            switch (resultCode) {
                case ResponseCode.SUCCESSFUL: Toast.makeText(this, "SUCCESSFUL", Toast.LENGTH_SHORT).show();break;
                case ResponseCode.PROCESSING: Toast.makeText(this, "PROCESSING", Toast.LENGTH_SHORT).show();break;
                case ResponseCode.FAILED: Toast.makeText(this, "FAILED", Toast.LENGTH_SHORT).show();break;
                case ResponseCode.ERROR: Toast.makeText(this, "ERROR", Toast.LENGTH_SHORT).show();break;
                case ResponseCode.CANCEL: Toast.makeText(this, "CANCEL", Toast.LENGTH_SHORT).show();break;
            }
        }
    }
}
