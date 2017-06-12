package com.paymentwall.mycardadapter;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by nguyen.anh on 5/16/2017.
 */

public class MycardFragment extends Fragment {

    private EditText etCardId, etCardPassword;
    private Button btnProceed;
    private String transactionId;
    private PsMyCard psMyCard;
    public static String MYCARD_OBJECT = "MYCARD_OBJECT";
    private Object psActivity;

    private Method mthSuccess, mthError, mthCancel, mthShowWait, mthHideWait;

    public static MycardFragment instance;

    public static MycardFragment getInstance() {
        if (instance == null) {
            instance = new MycardFragment();
        }
        return instance;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();

        try {
            Activity activity = getActivity();
            Class<?> PaymentSelectionActivity = Class.forName("com.paymentwall.pwunifiedsdk.core.PaymentSelectionActivity");
            psActivity = PaymentSelectionActivity.cast(activity);

            mthSuccess = PaymentSelectionActivity.getMethod("onPaymentSuccessful");
            mthError = PaymentSelectionActivity.getMethod("onPaymentError");
            mthCancel = PaymentSelectionActivity.getMethod("onPaymentCancel");
            mthShowWait = PaymentSelectionActivity.getMethod("showWaitLayout");
            mthHideWait = PaymentSelectionActivity.getMethod("hideWaitLayout");
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (bundle != null && bundle.containsKey(MYCARD_OBJECT)) {
            psMyCard = (PsMyCard) bundle.getSerializable(MYCARD_OBJECT);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        checkProceedButtonEnable();
    }

    private void checkProceedButtonEnable(){
        if(etCardId.getText().toString().trim().length() > 0 && etCardPassword.getText().toString().trim().length() > 0){
            btnProceed.setEnabled(true);
        }else{
            btnProceed.setEnabled(false);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_mycard, container, false);
        bindView(v);
        return v;
    }

    private void bindView(View v) {
        etCardId = (EditText) v.findViewById(R.id.etCardId);
        etCardPassword = (EditText) v.findViewById(R.id.etCardPassword);
        btnProceed = (Button) v.findViewById(R.id.btnProceed);

        btnProceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initTransaction();
//                processTransaction();
            }
        });

        etCardId.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                checkProceedButtonEnable();
            }
        });

        etCardPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                checkProceedButtonEnable();
            }
        });
    }

    private void initTransaction() {
        PwHttpClient.initTransaction(getActivity(), psMyCard, new PwHttpClient.Callback() {
            @Override
            public void onError(int statusCode, String responseBody, Throwable error) {
                Log.i("RESPONSE", responseBody + "");
                Toast.makeText(getActivity(), "Payment error: " + statusCode, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onSuccess(int statusCode, String responseBody) {
                Log.i("RESPONSE", responseBody + "");
                Toast.makeText(getActivity(), "Payment success" + responseBody, Toast.LENGTH_LONG).show();
                try {
                    JSONObject obj = new JSONObject(responseBody);
                    String id = obj.getString("id");
                    Log.i("Transaction_id", id);
                    processTransaction(id);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onStart() {
                try {
                    mthShowWait.invoke(psActivity);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onStop() {
                try {
                    mthHideWait.invoke(psActivity);
                } catch (Exception e) {
                    e.printStackTrace();
                }
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

        PwHttpClient.processTransaction(getActivity(), transactionId, params, new PwHttpClient.Callback() {
            @Override
            public void onError(int statusCode, String responseBody, Throwable error) {
                try {
                    mthError.invoke(psActivity);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onSuccess(int statusCode, String responseBody) {
                try {
                    mthSuccess.invoke(psActivity);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onStart() {
                try {
                    mthShowWait.invoke(psActivity);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onStop() {
                try {
                    mthHideWait.invoke(psActivity);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


    }
}
