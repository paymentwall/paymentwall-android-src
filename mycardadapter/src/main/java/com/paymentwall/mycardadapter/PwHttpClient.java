package com.paymentwall.mycardadapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.paymentwall.pwunifiedsdk.util.PwUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.util.Arrays;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLPeerUnverifiedException;

/**
 * Created by nguyen.anh on 10/26/2016.
 */

public class PwHttpClient {


    public static final String URL_INIT_TRANSACTION = "https://api.paymentwall.com/api/direct-payment";
    public static final String URL_PROCESS_TRANSACTION = "https://api.paymentwall.com/api/direct-payment/%s";

    public static final int TIMEOUT_READ = 10000;
    public static final int TIMEOUT_CONNECT = 20000;

    private static ProgressDialog dialog;

    public static void initTransaction(Context context, final PsMycard request, final Callback callback){
        post(context, URL_INIT_TRANSACTION, request.getInitTransactionParameterMap(), callback);
    }

    public static void post(final Context context, final String postUrl,  final Map<String, String> map, final Callback callback) {
        Looper looper;
        if (context != null) {
            looper = context.getMainLooper();
        } else {
            looper = Looper.getMainLooper();
        }
        final Handler handler = new Handler(looper);

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                postOnStart(callback, handler);
                OutputStream outputStream = null;
                //Prepare SSL cache
                String originalDNSCacheTTL = null;
                boolean allowedToSetTTL = true;
                try {
                    originalDNSCacheTTL = Security
                            .getProperty("networkaddress.cache.ttl");
                    Security.setProperty("networkaddress.cache.ttl", "0");
                } catch (SecurityException se) {
                    allowedToSetTTL = false;
                }
                try {
                    URL url = new URL(postUrl);
                    Log.i("REQUEST_URL", postUrl);
                    HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();

                    String query = getQuery(map);
                    Log.i(getClass().getSimpleName(), query);
                    connection.setFixedLengthStreamingMode(query.length());
                    connection.setReadTimeout(TIMEOUT_READ);
                    connection.setConnectTimeout(TIMEOUT_CONNECT);
                    connection.setRequestMethod("POST");
                    connection.setDoInput(true);
                    connection.setDoOutput(true);
                    connection.setUseCaches(false);
                    connection.setRequestProperty(
                            "Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
                    connection = PwUtils.addExtraHeaders(context, connection);
                    checkSSLCert(connection);
                    outputStream = connection.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                    writer.write(query);
                    writer.flush();
                    writer.close();
                    int statusCode = connection.getResponseCode();
                    Log.i("STATUS_CODE", statusCode + "");
                    if (statusCode < HttpURLConnection.HTTP_OK || statusCode >= HttpURLConnection.HTTP_MULT_CHOICE) {
                        try {
                            String errorResponse = getResponseBody(connection.getErrorStream());
                            Log.i("RESPONSE", errorResponse + "");
                            postError(statusCode, errorResponse, null, callback, handler);
                        } catch (IOException e) {
                            postError(statusCode, null, null, callback, handler);
                        }
                    } else {
                        try {
                            String successfulResponse = getResponseBody(connection.getInputStream());
                            Log.i("RESPONSE", successfulResponse + "");
                            postSuccess(statusCode, successfulResponse, callback, handler);
                        } catch (IOException e) {
                            postSuccess(statusCode, null, callback, handler);
                        }
                    }
                } catch (MalformedURLException malformedURLException) {
                    postError(0, null, malformedURLException, callback, handler);
                } catch (IOException e) {
                    postError(0, null, e, callback, handler);
                } catch (Exception e) {
                    e.printStackTrace();
                    postError(0, null, e, callback, handler);
                } catch (Throwable throwable) {
                    postError(0, null, throwable, callback, handler);
                } finally {
                    if (outputStream != null) {
                        try {
                            outputStream.close();
                        } catch (IOException e) {
                            postError(0, null, e, callback, handler);
                        }
                    }

                    if (allowedToSetTTL) {
                        if (originalDNSCacheTTL == null) {
                            Security.setProperty("networkaddress.cache.ttl", "-1");
                        } else {
                            Security.setProperty("networkaddress.cache.ttl",
                                    originalDNSCacheTTL);
                        }
                    }
                }
            }
        });
        thread.start();
    }

    public static void processTransaction(Context context, String transactionId, Map<String, String> params, final Callback callback){
        post(context, String.format(URL_PROCESS_TRANSACTION, transactionId), params, callback);

    }

    private static void postError(final int statusCode, final String responseBody, final Throwable error, final Callback callback, Handler handler) {
//        Log.i("RESPONSE_BODY", responseBody);
        if (handler != null && callback != null) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    callback.onStop();
                    callback.onError(statusCode, responseBody, error);
                }
            });
        }
    }

    private static void postSuccess(final int statusCode, final String responseBody, final Callback callback, Handler handler) {
        if (handler != null && callback != null) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    callback.onStop();
                    callback.onSuccess(statusCode, responseBody);
                }
            });
        }
    }

    private static void postOnStart(final Callback callback, Handler handler) {
        if (handler != null && callback != null) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    callback.onStart();
                }
            });
        }
    }

    private static void checkSSLCert(HttpURLConnection connnection) throws Throwable {
        if (!connnection.getURL().getHost().equals("api.paymentwall.com")) {
            return;
        }
        HttpsURLConnection tempConnection = (HttpsURLConnection) connnection;
        try {
            tempConnection.connect();
        } catch (IOException e) {
            throw e;
        }

        Certificate[] certificates;
        try {
            certificates = tempConnection.getServerCertificates();
        } catch (SSLPeerUnverifiedException e) {
            throw e;
        }

        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            byte[] der = certificates[0].getEncoded();
            md.update(der);
            byte[] digest = md.digest();
            byte[] revokedCertDigest = {5, -64, -77, 100, 54, -108, 71, 10,
                    -120, -116, 110, 127, -21, 92, -98, 36, -24, 35, -36, 83};
            if (Arrays.equals(digest, revokedCertDigest)) {
                throw new SSLPeerUnverifiedException("Revoked certificate");
            }
        } catch (NoSuchAlgorithmException e) {
            throw e;
        } catch (CertificateEncodingException e) {
            throw e;
        }
    }

    private static String getResponseBody(InputStream responseStream)
            throws IOException {
        String responseBody = getStringFromInputStream(responseStream);
        try {
            responseStream.close();
        } catch (IOException e) {
            throw e;
        }
        return responseBody;
    }

    private static String getStringFromInputStream(InputStream is)
            throws IOException {
        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();
        String line;
        try {
            br = new BufferedReader(new InputStreamReader(is));
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

        } catch (IOException e) {
            throw e;
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    throw e;
                }
            }
        }
        return sb.toString();
    }

    private static String getQuery(Map<String, String> params) throws UnsupportedEncodingException {
        StringBuilder query = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (first) first = false;
            else query.append('&');
            query.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            query.append('=');
            query.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }
        return query.toString();
    }

    public interface Callback {
        void onError(int statusCode, String responseBody, Throwable error);

        void onSuccess(int statusCode, String responseBody);

        void onStart();

        void onStop();
    }

    public static void showProgressDialog(Context context){
        dialog = new ProgressDialog(context);
        dialog.show();
    }

    public static void closeProgressDialog(){
        if(dialog != null && dialog.isShowing()){
            dialog.cancel();
        }
    }

}
