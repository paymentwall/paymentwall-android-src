package com.paymentwall.pwunifiedsdk.core;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.PhoneNumberUtils;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.paymentwall.pwunifiedsdk.R;
import com.paymentwall.pwunifiedsdk.brick.core.Brick;
import com.paymentwall.pwunifiedsdk.brick.core.BrickCard;
import com.paymentwall.pwunifiedsdk.brick.core.BrickError;
import com.paymentwall.pwunifiedsdk.brick.core.BrickToken;
import com.paymentwall.pwunifiedsdk.brick.message.BrickRequest;
import com.paymentwall.pwunifiedsdk.brick.ui.adapter.ExpireMonthAdapter;
import com.paymentwall.pwunifiedsdk.brick.ui.views.MaskedEditText;
import com.paymentwall.pwunifiedsdk.brick.utils.MiscUtils;
import com.paymentwall.pwunifiedsdk.brick.utils.PaymentMethod;
import com.paymentwall.pwunifiedsdk.ui.WaveHelper;
import com.paymentwall.pwunifiedsdk.util.Key;
import com.paymentwall.pwunifiedsdk.util.PwUtils;
import com.paymentwall.pwunifiedsdk.util.ResponseCode;

import java.io.File;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import glide.Glide;

/**
 * Created by nguyen.anh on 7/19/2016.
 */

public class BrickFragment extends BaseFragment implements Brick.Callback {

    private TextView tvProduct, tvPrice, tvCopyright;
    private ImageView ivProduct;

    private MaskedEditText etCardNumber;
    private EditText etCvv, etExpirationDate;
    private EditText etEmail, etNameOnCard;
    private static String cardNumber, cvv, expDate, email;
    private LinearLayout llScanCard;
    private static int monthSelection = -1;

    private WaveHelper helper;

    private Button btnConfirm;

    private int statusCode;
    private BrickRequest request;

    private boolean nativePaymentSucessfulDialog;
    private int timeout;
    private boolean isBrickError;

    private Calendar calendar;
    private int month = -1, year = -1, currentYear = -1;

    //layout datePicker
    private LinearLayout llExpirationDate;
    private ImageView ivPrevious, ivNext;
    private TextView tvYear;
    private GridView gvMonth;
    private ExpireMonthAdapter expireMonthAdapter;
    private static boolean isDatePickerShowing;

    public static final int RC_SCAN_CARD = 2505;

    private final Integer[] SIMPLIFIED_MONTHS = {R.string.month_1, R.string.month_2, R.string.month_3, R.string.month_4, R.string.month_5, R.string.month_6, R.string.month_7, R.string.month_8, R.string.month_9, R.string.month_10, R.string.month_11, R.string.month_12};

    private final Integer[] FULL_MONTHS = {R.string.month_1_full, R.string.month_2_full, R.string.month_3_full, R.string.month_4_full, R.string.month_5_full, R.string.month_6_full, R.string.month_7_full, R.string.month_8_full, R.string.month_9_full, R.string.month_10_full, R.string.month_11_full, R.string.month_12_full};

    public static BrickFragment instance;

    public static BrickFragment getInstance() {
        if (instance == null)
            instance = new BrickFragment();
        return instance;
    }


    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equalsIgnoreCase(self.getPackageName() + Brick.BROADCAST_FILTER_SDK) && intent.getExtras().containsKey(Brick.KEY_MERCHANT_SUCCESS)) {

                int success = intent.getIntExtra(Brick.KEY_MERCHANT_SUCCESS, -1);
                Log.i("BRICK_RECEIVER", success + "");
                if (getMainActivity().isWaitLayoutShowing) {
                    getMainActivity().handler.removeCallbacks(checkTimeoutTask);
                    if (success == 1) {
                        statusCode = ResponseCode.SUCCESSFUL;
                        displayPaymentSucceeded();
                    } else {
                        isBrickError = true;
                        getMainActivity().paymentError = getString(R.string.payment_error);
                        showErrorLayout(null);
                    }
                }
            } else if (intent.getAction().equalsIgnoreCase(self.getPackageName() + Brick.FILTER_BACK_PRESS_FRAGMENT)) {
                if (isDatePickerShowing) {
                    hideDatePicker();
                } else if (getMainActivity().isUnsuccessfulShowing) {
                    hideErrorLayout();
                } else if (getMainActivity().isSuccessfulShowing || getMainActivity().isWaitLayoutShowing) {
                    return;
                } else {
                    Intent i = new Intent();
                    i.setAction(self.getPackageName() + Brick.FILTER_BACK_PRESS_ACTIVITY);
                    LocalBroadcastManager.getInstance(self).sendBroadcast(i);
                }
            }
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PwUtils.logFabricCustom("Visit-BrickScreen");

        Log.i("BRICK-packagename", self.getPackageName());

        IntentFilter filter = new IntentFilter();
        filter.addAction(self.getPackageName() + Brick.FILTER_BACK_PRESS_FRAGMENT);
        filter.addAction(self.getPackageName() + Brick.BROADCAST_FILTER_SDK);
        LocalBroadcastManager.getInstance(self).registerReceiver(receiver, filter);

        cardNumber = null;
        cvv = null;
        expDate = null;
        email = null;
        monthSelection = -1;
        currentYear = -1;
        month = -1;
        year = -1;

        getMainActivity().isWaitLayoutShowing = false;
        getMainActivity().isUnsuccessfulShowing = false;
        getMainActivity().isSuccessfulShowing = false;
        isDatePickerShowing = false;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(self).unregisterReceiver(receiver);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(PwUtils.getLayoutId(self, "frag_brick"), container, false);
        bindView(v);
        setFonts(v);
        return v;
    }

    private void setFonts(View v) {
        PwUtils.setFontLight(self, new TextView[]{(TextView) v.findViewById(R.id.tvCopyRight)});
        PwUtils.setFontRegular(self, new TextView[]{(TextView) v.findViewById(R.id.tvProduct), (TextView) v.findViewById(R.id.tvTotal), (TextView) v.findViewById(R.id.tvPrice)});
        PwUtils.setFontBold(self, new TextView[]{(Button) v.findViewById(R.id.btnConfirm)});
    }

    private void checkScannerPlugin() {
        try {
            Class<?> CardIOActivity = Class.forName("io.card.payment.CardIOActivity");
            if (CardIOActivity != null) llScanCard.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            llScanCard.setVisibility(View.GONE);
        }
    }

    private void bindView(View v) {


        Bundle extras = getArguments();
        if (extras == null) {
            Intent result = new Intent();
            result.putExtra(Key.SDK_ERROR_MESSAGE, "NULL REQUEST");
            statusCode = ResponseCode.ERROR;
            self.setResult(statusCode, result);
            getMainActivity().backFragment(null);
            return;
        }
        int requestType = extras.getInt(Key.PAYMENT_TYPE,
                PaymentMethod.NULL);
        if (requestType != PaymentMethod.BRICK) {
            Intent result = new Intent();
            result.putExtra(Key.SDK_ERROR_MESSAGE, "NULL REQUEST_TYPE");
            statusCode = ResponseCode.ERROR;
            self.setResult(statusCode, result);
            getMainActivity().backFragment(null);
            return;
        }
        try {
            request = (BrickRequest) extras
                    .getParcelable(Key.REQUEST_MESSAGE);
        } catch (Exception e) {
            Intent result = new Intent();
            result.putExtra(Key.SDK_ERROR_MESSAGE, "NULL REQUEST OBJECT");
            statusCode = ResponseCode.ERROR;
            self.setResult(statusCode, result);
            getMainActivity().backFragment(null);
        }

        if (request == null) {
            Intent result = new Intent();
            result.putExtra(Key.SDK_ERROR_MESSAGE, "NULL REQUEST OBJECT");
            statusCode = ResponseCode.ERROR;
            self.setResult(statusCode, result);
            getMainActivity().backFragment(null);
            return;
        }

        if (!request.validate()) {
            Intent result = new Intent();
            result.putExtra(Key.SDK_ERROR_MESSAGE, "MORE PARAMETER(S) NEEDED");
            statusCode = ResponseCode.ERROR;
            self.setResult(statusCode, result);
            getMainActivity().backFragment(null);
            return;
        }
        statusCode = 0;
        if (request.getAmount() == null || request.getAmount() < 0 || request.getCurrency() == null) {
            Intent result = new Intent();
            result.putExtra(Key.SDK_ERROR_MESSAGE, "MORE PARAMETER(S) NEEDED");
            statusCode = ResponseCode.ERROR;
            self.setResult(statusCode, result);
            getMainActivity().backFragment(null);
            return;
        }

        nativePaymentSucessfulDialog = request.isNativeDialog();
        timeout = request.getTimeout();


        tvProduct = (TextView) v.findViewById(R.id.tvProduct);
        tvPrice = (TextView) v.findViewById(R.id.tvPrice);
        ivProduct = (ImageView) v.findViewById(R.id.ivProduct);

        ivNext = (ImageView) v.findViewById(R.id.ivNext);
        ivPrevious = (ImageView) v.findViewById(R.id.ivPrevious);

        etCardNumber = (MaskedEditText) v.findViewById(R.id.etCardNumber);
        etExpirationDate = (EditText) v.findViewById(R.id.etExpireDate);
        etCvv = (EditText) v.findViewById(R.id.etCvv);
        etEmail = (EditText) v.findViewById(R.id.etEmail);
        etNameOnCard = (EditText) v.findViewById(R.id.etName);
        llScanCard = (LinearLayout) v.findViewById(R.id.llScanCard);

        btnConfirm = (Button) v.findViewById(R.id.btnConfirm);

        llExpirationDate = (LinearLayout) v.findViewById(R.id.llExpirationDate);
        gvMonth = (GridView) v.findViewById(R.id.gvMonth);
        tvYear = (TextView) v.findViewById(R.id.tvYear);

        tvCopyright = (TextView) v.findViewById(R.id.tvCopyRight);
        tvCopyright.setText(String.format(getString(R.string.secured_by_pw), new SimpleDateFormat("yyyy").format(new Date())));

//        tvRedirecting.setText(String.format(getString(R.string.redirecting_to), PwUtils.getApplicationName(self)));


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

        if (request.getName() != null) {
            tvProduct.setText(request.getName());
            if (request.getAmount() != null && request.getCurrency() != null)
                tvPrice.setText(PwUtils.getCurrencySymbol(request.getCurrency()) + request.getAmount());
        }

        etCardNumber.addOnFullFillListener(new MaskedEditText.onFullFillListener() {
            @Override
            public void onFullFill() {

            }
        });

        etExpirationDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayDatePicker();
                return;
            }
        });

        llScanCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    Class<?> CardIOActivity = Class.forName("io.card.payment.CardIOActivity");
                    Intent scanIntent = new Intent(self, CardIOActivity);

                    scanIntent.putExtra("io.card.payment.scanExpiry", true); // default: false

                    self.startActivityForResult(scanIntent, RC_SCAN_CARD);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateBrickCardInfo();
//                showWaitLayout();
                return;
            }
        });

        ivNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onChangeExpirationYear(1);
            }
        });

        ivPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onChangeExpirationYear(-1);
            }
        });

        if (cardNumber != null) {
            etCardNumber.setText(cardNumber);
        }

        if (cvv != null) {
            etCvv.setText(cvv);
        }

        if (expDate != null) {
            etExpirationDate.setText(expDate);
        }

        if (email != null) {
            etEmail.setText(email);
        }

        if (isDatePickerShowing) {
            displayDatePicker();
        }

        init(v);
        checkScannerPlugin();
    }

    private void init(View v) {

        Calendar cal = Calendar.getInstance();
        String currentMonth = String.format("%tm", cal);
        if (month == -1)
            month = Integer.parseInt(currentMonth);

        String strYear = String.format("%tY", cal);
        if (year == -1)
            year = Integer.parseInt(strYear);
        if (currentYear == -1)
            currentYear = Integer.parseInt(strYear);
        tvYear.setText(year + "");
        onChangeExpirationYear(0);

        gvMonth = (GridView) v.findViewById(R.id.gvMonth);
        expireMonthAdapter = new ExpireMonthAdapter(self, Arrays.asList(SIMPLIFIED_MONTHS), new ExpireMonthAdapter.IExpirateMonth() {
            @Override
            public void onClickMonth(int position) {
                monthSelection = position;
                expireMonthAdapter.setSelectedPosition(position);
                expireMonthAdapter.notifyDataSetChanged();
                month = position + 1;
                onChangeExpirationYear(0);
                hideDatePicker();
                etEmail.requestFocus();
                showKeyboard(etEmail);
            }
        });

        if (monthSelection == -1) monthSelection = Integer.parseInt(currentMonth) - 1;
        expireMonthAdapter.setSelectedPosition(monthSelection);
        gvMonth.setAdapter(expireMonthAdapter);

        llExpirationDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideDatePicker();
            }
        });

        etCardNumber.setTag(etCardNumber.getCompoundDrawables()[0]);
        etCardNumber.addTextChangedListener(new TextWatcher() {
            private boolean mFormatting; // this is a flag which prevents the  stack overflow.
            private int mAfter;

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // nothing to do here..
            }

            //called before the text is changed...
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //nothing to do here...
                mAfter = after; // flag to detect backspace..
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Make sure to ignore calls to afterTextChanged caused by the work done below
                if (!mFormatting) {
                    mFormatting = true;
                    // using US formatting...
                    if (mAfter != 0) // in case back space ain't clicked...
                        PhoneNumberUtils.formatNumber(s, PhoneNumberUtils.getFormatTypeForLocale(Locale.US));
                    mFormatting = false;
                }

                if (s.length() > 0) {
                    etCardNumber.setBackgroundResource(R.drawable.saas_bg_input_form);
                    etCardNumber.setHint(getString(R.string.card_number_hint));
                    etCardNumber.setCompoundDrawables((Drawable)etCardNumber.getTag(), null, null, null);
                }
                cardNumber = etCardNumber.getText().toString();
            }
        });

        etExpirationDate.setTag(etExpirationDate.getCompoundDrawables()[0]);
        etExpirationDate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    Drawable drawableRight = getResources().getDrawable(R.drawable.ic_cc_down);
                    etExpirationDate.setCompoundDrawablesWithIntrinsicBounds((Drawable)etExpirationDate.getTag(), null, drawableRight, null);
                    etExpirationDate.setBackgroundResource(R.drawable.saas_bg_input_form);
                    etExpirationDate.setHint("");
                }
                expDate = etCvv.getText().toString();
            }

        });

        etCvv.setTag(etCvv.getCompoundDrawables()[0]);
        etCvv.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    etCvv.setBackgroundResource(R.drawable.saas_bg_input_form);
                    etCvv.setHint("");
                    etCvv.setCompoundDrawables((Drawable)etCvv.getTag(), null, null, null);
                }
                cvv = etCvv.getText().toString();
            }
        });

        etEmail.setTag(etEmail.getCompoundDrawables()[0]);
        etEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    etEmail.setBackgroundResource(R.drawable.saas_bg_input_form);
                    etEmail.setHint("");
                    etEmail.setCompoundDrawables((Drawable)etEmail.getTag(), null, null, null);
                }
                email = etEmail.getText().toString();
            }
        });

        etNameOnCard.setTag(etNameOnCard.getCompoundDrawables()[0]);
        etNameOnCard.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    etNameOnCard.setBackgroundResource(R.drawable.saas_bg_input_form);
                    etNameOnCard.setHint(getString(R.string.name_on_card));
                    etNameOnCard.setCompoundDrawables((Drawable)etNameOnCard.getTag(), null, null, null);
                }
            }
        });

        etCvv.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    displayDatePicker();
                    return true;
                }
                return false;
            }
        });

        etEmail.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    hideKeyboard();
//                    etCardNumber.postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            etCardNumber.clearFocus();
//                        }
//                    },100);
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_SCAN_CARD) {
            if (data != null && data.hasExtra("io.card.payment.scanResult")) {
                try {
                    Object scanResult = data.getParcelableExtra("io.card.payment.scanResult");
                    Class<?> CreditCard = Class.forName("io.card.payment.CreditCard");
                    Object creditCard = CreditCard.cast(scanResult);
                    Method mthGetFormattedCardNumber = CreditCard.getMethod("getFormattedCardNumber");

                    etCardNumber.setText(mthGetFormattedCardNumber.invoke(creditCard) + "");

//                    if (scanResult.isExpiryValid()) {
//                        etExpirationDate.setText(scanResult.expiryMonth + "/" + scanResult.expiryYear);
//                    }
//
//                    if (scanResult.cvv != null) {
//                        etCvv.setText(scanResult.cvv);
//                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else {
            }
        }
    }

    private void onChangeExpirationYear(int increase) {
        year += increase;
        if (year == currentYear) {
            ivPrevious.setVisibility(View.INVISIBLE);
        } else {
            ivPrevious.setVisibility(View.VISIBLE);
        }
        tvYear.setText(year + "");
        etExpirationDate.setText(month + "/" + year);
    }

    private void validateBrickCardInfo() {
        String cardNumber = etCardNumber.getText().toString().replaceAll(" ", "");
        String expMonth = String.valueOf(month);
        String expYear = String.valueOf(year);
        String cvv = etCvv.getText().toString();
        String email = etEmail.getText().toString();

        etCardNumber.clearFocus();

        Log.i("CARD_INFO", cardNumber + "-" + expMonth + "-" + expYear + "-" + cvv);
        // Create a brickcard object
        final BrickCard brickCard = new BrickCard(cardNumber, expMonth, expYear, cvv, email);
        // Check if the card data is possible or not
        if (!brickCard.isValid()) {
            Drawable drawableRight = getResources().getDrawable(R.drawable.ic_form_error);
            if (!brickCard.isNumberValid()) {
//                tvErrCard.setVisibility(View.VISIBLE);
                etCardNumber.setHint(R.string.err_invalid_card_number);
                etCardNumber.setText("");
                etCardNumber.setBackgroundResource(R.drawable.saas_bg_input_form_error);
                etCardNumber.setCompoundDrawablesWithIntrinsicBounds(null, null, drawableRight, null);
            }
            if (!brickCard.isExpValid()) {
                etExpirationDate.setHint(R.string.err_invalid_expiration_date);
                etExpirationDate.setBackgroundResource(R.drawable.saas_bg_input_form_error);
                etExpirationDate.setCompoundDrawablesWithIntrinsicBounds(null, null, drawableRight, null);
            }
            if (!brickCard.isCvcValid()) {
                etCvv.setHint(R.string.error_invalid_cvv);
                etCvv.setBackgroundResource(R.drawable.saas_bg_input_form_error);
                etCvv.setCompoundDrawablesWithIntrinsicBounds(null, null, drawableRight, null);
            }

            if (!brickCard.isEmailValid()) {
                etEmail.setHint(R.string.error_invalid_email);
                etEmail.setBackgroundResource(R.drawable.saas_bg_input_form_error);
                etEmail.setCompoundDrawablesWithIntrinsicBounds(null, null, drawableRight, null);
            }
            if (etNameOnCard.getText().toString().trim().length() == 0) {
                etNameOnCard.setHint(R.string.error_empty_name_on_card);
                etNameOnCard.setBackgroundResource(R.drawable.saas_bg_input_form_error);
                etNameOnCard.setCompoundDrawablesWithIntrinsicBounds(null, null, drawableRight, null);
            }
        } else {
            showWaitLayout();
            Brick.createToken(self, request.getAppKey(), brickCard, this);
        }
    }

    private void displayDatePicker() {
        hideKeyboard();
        llExpirationDate.setVisibility(View.VISIBLE);
        isDatePickerShowing = true;
    }

    private void hideDatePicker() {
        llExpirationDate.setVisibility(View.GONE);
        isDatePickerShowing = false;
    }

    @Override
    public void onBrickSuccess(BrickToken brickToken) {
        String token = brickToken.getToken();
        if (token != null) {
            Intent intent = new Intent();
            intent.setAction(getActivity().getPackageName() + Brick.BROADCAST_FILTER_MERCHANT);
            intent.putExtra(Brick.KEY_BRICK_TOKEN, token);
            LocalBroadcastManager.getInstance(self).sendBroadcast(intent);

            if (!nativePaymentSucessfulDialog) {
                self.setResult(ResponseCode.MERCHANT_PROCESSING);
                self.finish();
            } else {
                getMainActivity().handler.postDelayed(checkTimeoutTask, timeout);
            }
        }
    }

    @Override
    public void onBrickError(BrickError error) {
        showErrorLayout(null);
    }

    private Runnable checkTimeoutTask = new Runnable() {
        @Override
        public void run() {
            isBrickError = true;
            getMainActivity().paymentError = getString(R.string.timeout_connnection);
            getMainActivity().handler.removeCallbacks(this);
            hideWaitLayout();
        }
    };

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        populateViewForOrientation(inflater, (ViewGroup) getView());
    }

    private void populateViewForOrientation(LayoutInflater inflater, ViewGroup viewGroup) {
        viewGroup.removeAllViewsInLayout();
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            View v = inflater.inflate(PwUtils.getLayoutId(self, "frag_brick"), viewGroup);
            bindView(v);
            setFonts(v);
        } else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            View v = inflater.inflate(PwUtils.getLayoutId(self, "frag_brick"), viewGroup);
            bindView(v);
            setFonts(v);
        }
    }
}
