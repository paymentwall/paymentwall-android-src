package com.paymentwall.pwunifiedsdk.core;

import android.Manifest;
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
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.paymentwall.pwunifiedsdk.R;
import com.paymentwall.pwunifiedsdk.brick.core.Brick;
import com.paymentwall.pwunifiedsdk.mint.utils.PaymentMethod;
import com.paymentwall.pwunifiedsdk.mobiamo.core.MobiamoDialogActivity;
import com.paymentwall.pwunifiedsdk.mobiamo.core.MobiamoResponse;
import com.paymentwall.pwunifiedsdk.mobiamo.utils.Const;
import com.paymentwall.pwunifiedsdk.util.Key;
import com.paymentwall.pwunifiedsdk.util.MiscUtils;
import com.paymentwall.pwunifiedsdk.util.PwUtils;
import com.paymentwall.pwunifiedsdk.util.ResponseCode;
import com.paymentwall.sdk.pwlocal.ui.PwLocalActivity;

import java.io.File;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

import glide.Glide;

/**
 * Created by nguyen.anh on 7/18/2016.
 */

public class MainPsFragment extends BaseFragment {

    private UnifiedRequest request;
    private LinearLayout llBrick, llLocalMethods, llMint, llMobiamo;
//    private LinearLayout llPwLocal;

    private ImageView ivProduct;
    private TextView tvProduct, tvPrice, tvCopyright;
    private Bundle bundle;

    private final int RC_SEND_SMS = 114;

    public static MainPsFragment instance;

    public static MainPsFragment getInstance() {
        if (instance == null) {
            instance = new MainPsFragment();
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
        bundle = getArguments();
        if (bundle != null && bundle.containsKey(Key.REQUEST_MESSAGE)) {
            request = (UnifiedRequest) bundle.getParcelable(Key.REQUEST_MESSAGE);
        }
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
        View v = inflater.inflate(PwUtils.getLayoutId(self, "frag_main_ps"), container, false);
        bindView(v);
        setFonts(v);
        init();
        return v;
    }

    private void setFonts(View v) {
        PwUtils.setFontLight(self, new TextView[]{(TextView) v.findViewById(R.id.tvCopyRight)});
        PwUtils.setFontRegular(self, new TextView[]{(TextView) v.findViewById(R.id.tvProduct), (TextView) v.findViewById(R.id.tvTotal), (TextView) v.findViewById(R.id.tvPrice)});

        PwUtils.setFontRegular(self, new TextView[]{(TextView) v.findViewById(R.id.tvBrick), (TextView) v.findViewById(R.id.tvLocalPs), (TextView) v.findViewById(R.id.tvMint), (TextView) v.findViewById(R.id.tvMobiamo), (TextView) v.findViewById(R.id.tvPwLocal)});
    }

    private void bindView(View v) {
        llBrick = (LinearLayout) v.findViewById(R.id.llBrick);
        llBrick.setVisibility(request.isBrickEnabled() ? View.VISIBLE : View.GONE);

        llLocalMethods = (LinearLayout) v.findViewById(R.id.llLocalMethods);

        llMint = (LinearLayout) v.findViewById(R.id.llMint);
        llMint.setVisibility(request.isMintEnabled() ? View.VISIBLE : View.GONE);

        llMobiamo = (LinearLayout) v.findViewById(R.id.llMobiamo);
        llMobiamo.setVisibility(request.isMobiamoEnabled() ? View.VISIBLE : View.GONE);

//        llPwLocal = (LinearLayout) v.findViewById(R.id.llPwLocal);
//        llPwLocal.setVisibility(request.isPwlocalEnabled() ? View.VISIBLE : View.GONE);

        ivProduct = (ImageView) v.findViewById(R.id.ivProduct);
        tvProduct = (TextView) v.findViewById(R.id.tvProduct);
        tvPrice = (TextView) v.findViewById(R.id.tvPrice);

        tvProduct.setText(request.getItemName());
        tvPrice.setText(PwUtils.getCurrencySymbol(request.getCurrency()) + request.getAmount());

        tvCopyright = (TextView) v.findViewById(R.id.tvCopyRight);
        tvCopyright.setText(String.format(getString(R.string.secured_by_pw), new SimpleDateFormat("yyyy").format(new Date())));

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

        llBrick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                payWithBrick();
            }
        });


        llLocalMethods.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putParcelable(Key.REQUEST_MESSAGE, request);
                getMainActivity().replaceContentFragment(LocalPsFragment.getInstance(), bundle);
            }
        });


        llMint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                payWithMint();
            }
        });

        llMobiamo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ContextCompat.checkSelfPermission(self,
                            Manifest.permission.SEND_SMS)
                            != PackageManager.PERMISSION_GRANTED) {

                        requestPermissions(
                                new String[]{Manifest.permission.SEND_SMS},
                                RC_SEND_SMS);
                        return;
                    }
                }
                payWithMobiamo();
            }
        });

        PwUtils.setCustomAttributes(self, v);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case RC_SEND_SMS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    payWithMobiamo();

                } else {

                }
                return;
            }
        }
    }


    private void init() {
        /*if(request != null &&
                request.isBrickEnabled()
                && !request.isMintEnabled()
                && !request.isMobiamoEnabled()
                && !request.isPwlocalEnabled()
                && (request.getExternalPsList() == null || request.getExternalPsList().isEmpty()) ) {
            payWithBrick();
        }*/
    }

    private void payWithMint() {
        if (request.getMintRequest().validate()) {
            Bundle bundle = new Bundle();
            bundle.putInt(Key.PAYMENT_TYPE, PaymentMethod.MINT);
            bundle.putParcelable(Key.REQUEST_MESSAGE, request.getMintRequest());
            getMainActivity().replaceContentFragment(new MintFragment(), bundle);
        }
    }

    private void payWithBrick() {
        if (getMainActivity().isSuccessfulShowing = true) {
            getMainActivity().replaceContentFragment(BrickFragment.getInstance(), bundle);
        }
        if (request.getBrickRequest().validate()) {
            Bundle bundle = new Bundle();
            bundle.putInt(Key.PAYMENT_TYPE, com.paymentwall.pwunifiedsdk.brick.utils.PaymentMethod.BRICK);
            bundle.putParcelable(Key.REQUEST_MESSAGE, request.getBrickRequest());
            getMainActivity().replaceContentFragment(BrickFragment.getInstance(), bundle);
        }
    }

    private void payWithMobiamo() {
//        Bundle bundle = new Bundle();
//        bundle.putSerializable(Const.KEY.REQUEST_MESSAGE, request.getMobiamoRequest());
//        getMainActivity().replaceContentFragment(new MobiamoFragment(), bundle);

        Intent intent = new Intent(self, MobiamoDialogActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        intent.putExtra(Const.KEY.REQUEST_MESSAGE, request.getMobiamoRequest());
        startActivityForResult(intent, MobiamoDialogActivity.MOBIAMO_REQUEST_CODE);
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        populateViewForOrientation(inflater, (ViewGroup) getView());
    }

    private void populateViewForOrientation(LayoutInflater inflater, ViewGroup viewGroup) {
        viewGroup.removeAllViewsInLayout();
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            View v = inflater.inflate(PwUtils.getLayoutId(self, "frag_main_ps"), viewGroup);
            bindView(v);
            setFonts(v);
        } else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            View v = inflater.inflate(PwUtils.getLayoutId(self, "frag_main_ps"), viewGroup);
            bindView(v);
            setFonts(v);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Intent intent = new Intent();
        if (requestCode == MobiamoDialogActivity.MOBIAMO_REQUEST_CODE) {

            if (resultCode == ResponseCode.ERROR) {
                self.setResult(ResponseCode.ERROR, intent);
                self.finish();
            } else if (resultCode == ResponseCode.FAILED) {
                self.setResult(ResponseCode.FAILED, intent);
                self.finish();
            } else if (resultCode == ResponseCode.CANCEL) {
                self.setResult(ResponseCode.CANCEL, intent);
                self.finish();

            } else if (resultCode == ResponseCode.SUCCESSFUL) {
                if (data != null) {
                    MobiamoResponse response = (MobiamoResponse) data.getSerializableExtra(Const.KEY.RESPONSE_MESSAGE);
                    if (response != null && response.isCompleted()) {
                        intent.putExtra(Const.KEY.RESPONSE_MESSAGE, (Serializable) response);
                        self.setResult(ResponseCode.SUCCESSFUL, intent);
                        self.finish();
                    }
                } else {
                }
            }
        } else if (requestCode == PwLocalActivity.REQUEST_CODE) {
            if (resultCode == ResponseCode.ERROR) {
                self.setResult(ResponseCode.ERROR, intent);
                self.finish();
            } else if (resultCode == ResponseCode.FAILED) {
                self.setResult(ResponseCode.FAILED, intent);
                self.finish();
            } else if (resultCode == ResponseCode.CANCEL) {
                self.setResult(ResponseCode.CANCEL, intent);
                self.finish();

            } else if (resultCode == ResponseCode.SUCCESSFUL) {
                self.setResult(ResponseCode.SUCCESSFUL, intent);
                self.finish();

            }
        }
    }
}
