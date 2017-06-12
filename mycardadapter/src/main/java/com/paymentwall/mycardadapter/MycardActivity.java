package com.paymentwall.mycardadapter;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by nguyen.anh on 5/15/2017.
 */

public class MycardActivity extends Activity {

    private EditText etCardId, etCardPassword;
    private Button btnProceed;
    private String transactionId;
    private PsMyCard psMyCard;
    public static String MYCARD_OBJECT = "MYCARD_OBJECT";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mycard);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null && bundle.containsKey(MYCARD_OBJECT)) {
            psMyCard = (PsMyCard) bundle.getSerializable(MYCARD_OBJECT);
        }

        initUI();
        initData();
        initControl();
    }

    private void initUI() {
        etCardId = (EditText) findViewById(R.id.etCardId);
        etCardPassword = (EditText) findViewById(R.id.etCardPassword);
        btnProceed = (Button) findViewById(R.id.btnProceed);
    }

    private void initData() {

    }

    private void initControl() {
        btnProceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initTransaction();
//                processTransaction();
            }
        });
    }

    private void initTransaction() {
        PwHttpClient.initTransaction(this, psMyCard, new PwHttpClient.Callback() {
            @Override
            public void onError(int statusCode, String responseBody, Throwable error) {
                Log.i("RESPONSE", responseBody + "");
                Toast.makeText(MycardActivity.this, "Payment error: " + statusCode, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onSuccess(int statusCode, String responseBody) {
                Log.i("RESPONSE", responseBody + "");
                Toast.makeText(MycardActivity.this, "Payment success" + responseBody, Toast.LENGTH_LONG).show();
                try {
                    JSONObject obj = new JSONObject(responseBody);
                    String id = obj.getString("id");
                    Log.i("Transaction_id", id);
//                    processTransaction(id);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onStart() {
                PwHttpClient.showProgressDialog(MycardActivity.this);
            }

            @Override
            public void onStop() {
                PwHttpClient.closeProgressDialog();
            }
        });
    }

    private void processTransaction(String transactionId) {

        Map<String, String> params = new HashMap<>();
        params.put("key", (String) psMyCard.getBundle().get("PW_PROJECT_KEY"));
        params.put("step", "1");
        params.put("data[card_id]", etCardId.getText().toString().trim());
        params.put("data[card_password]", etCardPassword.getText().toString().trim());
        params.put("ref_id", transactionId);
        params.put("sign_version", "2");

        params = psMyCard.makeTransactionProcessingMap(params);

        params.remove("ref_id");

        PwHttpClient.processTransaction(MycardActivity.this, transactionId, params, new PwHttpClient.Callback() {
            @Override
            public void onError(int statusCode, String responseBody, Throwable error) {

            }

            @Override
            public void onSuccess(int statusCode, String responseBody) {

            }

            @Override
            public void onStart() {

            }

            @Override
            public void onStop() {

            }
        });


    }
}
