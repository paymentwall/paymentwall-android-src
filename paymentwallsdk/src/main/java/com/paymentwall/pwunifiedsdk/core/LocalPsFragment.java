package com.paymentwall.pwunifiedsdk.core;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.paymentwall.pwunifiedsdk.R;
import com.paymentwall.pwunifiedsdk.brick.core.Brick;
import com.paymentwall.pwunifiedsdk.brick.utils.MiscUtils;
import com.paymentwall.pwunifiedsdk.object.ExternalPs;
import com.paymentwall.pwunifiedsdk.ui.ExtPsLayout;
import com.paymentwall.pwunifiedsdk.util.Key;
import com.paymentwall.pwunifiedsdk.util.PwUtils;
import com.paymentwall.pwunifiedsdk.util.ResponseCode;
import com.paymentwall.sdk.pwlocal.message.CustomRequest;
import com.paymentwall.sdk.pwlocal.message.LocalDefaultRequest;
import com.paymentwall.sdk.pwlocal.message.LocalFlexibleRequest;
import com.paymentwall.sdk.pwlocal.ui.PwLocalActivity;
import com.paymentwall.sdk.pwlocal.utils.ApiType;

import java.io.File;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import glide.Glide;

import static android.content.res.Configuration.ORIENTATION_LANDSCAPE;

//import com.unionpay.UPPayAssistEx;
//import com.unionpay.uppay.PayActivity;

//import com.alipay.sdk.app.PayTask;


/**
 * Created by nguyen.anh on 7/15/2016.
 */

public class LocalPsFragment extends BaseFragment {

    private TextView tvProduct, tvPrice, tvCopyright;
    private ImageView ivProduct;
    private Bundle bundle;
    private UnifiedRequest request;
    private LayoutInflater inflater;

    //    private LinearLayout llAlipay, llUnionpay, llWechat, llOther;
    private LinearLayout llContainer;

    private static final int UNION_PAY_RC = 10;
    private final String mMode = "01";

    private int responseCode = 0;

    private final int RC_READ_PHONE_STATE = 113;

    private static final String TN_URL_01 = "http://101.231.204.84:8091/sim/getacptn";

    private View clickedPs;
    private Object objAdapter;


    private static LocalPsFragment instance;

    public static LocalPsFragment getInstance() {
        if (instance == null) {
            instance = new LocalPsFragment();
        }
        return instance;
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equalsIgnoreCase(self.getPackageName() + Brick.FILTER_BACK_PRESS_FRAGMENT)) {
                Intent i = new Intent();
                i.setAction(self.getPackageName() + Brick.FILTER_BACK_PRESS_ACTIVITY);
                LocalBroadcastManager.getInstance(self).sendBroadcast(i);
            }
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(this.getClass().getSimpleName(), "ONCREATE");

        PwUtils.logFabricCustom("Visit-LocalPsScreen");

        bundle = getArguments();
        if (bundle != null) {
            if (bundle.containsKey(Key.REQUEST_MESSAGE))
                request = (UnifiedRequest) bundle.getParcelable(Key.REQUEST_MESSAGE);
        }

        inflater = (LayoutInflater) self.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LocalBroadcastManager.getInstance(self).registerReceiver(receiver, new IntentFilter(self.getPackageName() + Brick.FILTER_BACK_PRESS_FRAGMENT));
    }

    @Override
    public void onDestroy() {
        LocalBroadcastManager.getInstance(self).unregisterReceiver(receiver);
        super.onDestroy();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(PwUtils.getLayoutId(self, "frag_local_ps"), container, false);
        bindView(v);
        setFonts(v);
        init();
        return v;
    }

    private void setFonts(View v) {
        PwUtils.setFontLight(self, new TextView[]{(TextView) v.findViewById(R.id.tvCopyRight)});
        PwUtils.setFontRegular(self, new TextView[]{(TextView) v.findViewById(R.id.tvProduct), (TextView) v.findViewById(R.id.tvTotal), (TextView) v.findViewById(R.id.tvPrice)});

    }

    private void bindView(View v) {
        llContainer = (LinearLayout) v.findViewById(R.id.llContainer);
        bindContainer();

        tvProduct = (TextView) v.findViewById(R.id.tvProduct);
        tvPrice = (TextView) v.findViewById(R.id.tvPrice);
        ivProduct = (ImageView) v.findViewById(R.id.ivProduct);

        tvCopyright = (TextView) v.findViewById(R.id.tvCopyRight);
        tvCopyright.setText(String.format(getString(R.string.secured_by_pw), new SimpleDateFormat("yyyy").format(new Date())));


        tvProduct.setText(request.getItemName());
        tvPrice.setText(PwUtils.getCurrencySymbol(request.getCurrency()) + request.getAmount());
        ivProduct.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                    ivProduct.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                } else {
                    ivProduct.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
                if (request.getItemUrl() != null) {
                    Glide.with(self).load(request.getItemUrl()).into(ivProduct);
                } else if (request.getItemFile() != null) {
                    Glide.with(self).load(request.getItemFile()).into(ivProduct);
                } else if (request.getItemResID() != 0) {
                    Glide.with(self).load(request.getItemResID()).into(ivProduct);
                } else if (request.getItemContentProvider() != null) {
                    String realPath = MiscUtils.getRealPathFromURI(Uri.parse(request.getItemContentProvider()), self);
                    Glide.with(self).load(new File(realPath)).into(ivProduct);
                } else {
                    ((View) (ivProduct.getParent())).setVisibility(View.GONE);
                }
            }
        });
    }

    private void bindContainer() {
        ArrayList<ExternalPs> list = request.getExternalPsList();
        for (ExternalPs ps : list) {
            if (ps.getId().equalsIgnoreCase("pwlocal"))
                ps.setDisplayName(getString(R.string.title_pwlocal));

            View view = inflater.inflate(PwUtils.getLayoutId(self, "ext_ps_layout"), null);
            ExtPsLayout llPs = (ExtPsLayout) view.findViewById(R.id.llPs);
            llPs.setPsId(ps.getId());
            llPs.setParams(ps.getParams());
            llPs.setOnClickListener(onClickPsLayout);
            ImageView ivPs = (ImageView) view.findViewById(R.id.ivPs);
            ivPs.setImageResource(ps.getIconResId());
            TextView tvPs = (TextView) view.findViewById(R.id.tvPs);
            tvPs.setText(ps.getDisplayName());
            PwUtils.setFontRegular(self, new TextView[]{tvPs});
            llContainer.addView(view);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) PwUtils.dpToPx(self, 64f));
            if (list.indexOf(ps) > 0) {
                params.setMargins(0, (int) PwUtils.dpToPx(self, 2f), 0, 0);
            }
            view.setLayoutParams(params);
        }
    }

    private void init() {
//        api = WXAPIFactory.createWXAPI(self, request.getWechatPayParams().getAppId(), true);
//        api.registerApp(request.getWechatPayParams().getAppId());
//        api.handleIntent(self.getIntent(), this);
    }


    @Override
    public void onActivityResult(int requestCode, final int resultCode, Intent data) {

        if (requestCode == PwLocalActivity.REQUEST_CODE) {
            if (resultCode == ResponseCode.ERROR) {
                self.setResult(ResponseCode.ERROR, data);
            } else if (resultCode == ResponseCode.FAILED) {
                self.setResult(ResponseCode.FAILED, data);
            } else if (resultCode == ResponseCode.CANCEL) {
                self.setResult(ResponseCode.CANCEL, data);
            } else if (resultCode == ResponseCode.SUCCESSFUL) {
                self.setResult(ResponseCode.SUCCESSFUL, data);
                self.finish();

            }
        } else {
            if (clickedPs == null) return;
            String psName = ((ExtPsLayout) clickedPs).getPsId();
            try {
                Class<?> Adapter = Class.forName("com.paymentwall." + (psName + "Adapter.").toLowerCase() + capitalize(psName) + "Adapter");
                Method resultMethod = Adapter.getDeclaredMethod("onActivityResult", int.class, int.class, Intent.class);
                resultMethod.setAccessible(true);
                resultMethod.invoke(objAdapter, requestCode, resultCode, data);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private View.OnClickListener onClickPsLayout = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            clickedPs = v;
            pay(((ExtPsLayout) v).getPsId(), ((ExtPsLayout) v).getParams());
        }
    };

    private void payWithPwLocal() {

        if (request.getPwlocalRequest() != null) {
            Intent intent = new Intent(getActivity(), PwLocalActivity.class);

            if (request.getPwlocalRequest() instanceof LocalDefaultRequest) {
                intent.putExtra(com.paymentwall.sdk.pwlocal.utils.Key.PAYMENT_TYPE, com.paymentwall.sdk.pwlocal.utils.PaymentMethod.PW_LOCAL_DEFAULT);
                intent.putExtra(com.paymentwall.sdk.pwlocal.utils.Key.PWLOCAL_REQUEST_MESSAGE, request.getPwlocalRequest());
            } else if (request.getPwlocalRequest() instanceof LocalFlexibleRequest) {
                intent.putExtra(com.paymentwall.sdk.pwlocal.utils.Key.PAYMENT_TYPE, com.paymentwall.sdk.pwlocal.utils.PaymentMethod.PW_LOCAL_FLEXIBLE);
                intent.putExtra(com.paymentwall.sdk.pwlocal.utils.Key.PWLOCAL_REQUEST_MESSAGE, request.getPwlocalRequest());
            } else if (request.getPwlocalRequest() instanceof CustomRequest) {
                intent.putExtra(com.paymentwall.sdk.pwlocal.utils.Key.CUSTOM_REQUEST_TYPE, ApiType.DIGITAL_GOODS);
                intent.putExtra(com.paymentwall.sdk.pwlocal.utils.Key.CUSTOM_REQUEST_MAP, request.getPwlocalRequest());
            }
            self.startActivityForResult(intent, PwLocalActivity.REQUEST_CODE);
        } else {
            throw new RuntimeException("You must set pwLocalRequest value in unifiedRequest object");
        }

    }

    private void pay(String psName, Serializable params) {
        if (psName.equalsIgnoreCase("pwlocal")) {
            payWithPwLocal();
        }
        try {
            Class<?> Adapter = Class.forName("com.paymentwall." + (psName + "Adapter.").toLowerCase() + capitalize(psName) + "Adapter");
            Constructor constructor = Adapter.getConstructor(Fragment.class);
            objAdapter = constructor.newInstance(this);
            Method payMethod = Adapter.getDeclaredMethod("pay", Context.class, Serializable.class, Map.class);
            payMethod.setAccessible(true);
            payMethod.invoke(objAdapter, self, params, request.getBundle());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case RC_READ_PHONE_STATE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Serializable params = bundle.getSerializable("PsAlipay");
                    pay("Alipay", params);

                } else {

                }
                return;
            }
        }
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        populateViewForOrientation(inflater, (ViewGroup) getView());
    }

    private void populateViewForOrientation(LayoutInflater inflater, ViewGroup viewGroup) {
        viewGroup.removeAllViewsInLayout();
        if (getResources().getConfiguration().orientation == ORIENTATION_LANDSCAPE) {
            View v = inflater.inflate(PwUtils.getLayoutId(self, "frag_local_ps"), viewGroup);
            bindView(v);
            setFonts(v);
        } else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            View v = inflater.inflate(PwUtils.getLayoutId(self, "frag_local_ps"), viewGroup);
            bindView(v);
            setFonts(v);
        }
        // Find your buttons in subview, set up onclicks, set up callbacks to your parent fragment or activity here.
    }

    @Override
    public void onPaymentSuccessful() {
        self.setResult(ResponseCode.SUCCESSFUL);
        self.finish();
    }

    @Override
    public void onPaymentError() {
        Toast.makeText(self, "Payment Error", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPaymentCancel() {
        Toast.makeText(self, "Payment cancelled", Toast.LENGTH_SHORT).show();
    }

    private String capitalize(final String line) {
        return Character.toUpperCase(line.charAt(0)) + line.substring(1);
    }

    public void onNewIntent(Intent intent) {
        try {
            String psName = ((ExtPsLayout) clickedPs).getPsId();
            Class<?> Adapter = Class.forName("com.paymentwall." + (psName + "Adapter.").toLowerCase() + capitalize(psName) + "Adapter");
            Method resultMethod = Adapter.getDeclaredMethod("onHandleIntent", Intent.class);
            resultMethod.setAccessible(true);
            resultMethod.invoke(objAdapter, intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
