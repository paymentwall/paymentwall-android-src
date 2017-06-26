package com.paymentwall.pwunifiedsdk.brick.core;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.paymentwall.pwunifiedsdk.util.PwUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
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
 * Created by paymentwall on 28/05/15.
 */
public class Brick {
    final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();
    private static final int CONNECTION_TIMEOUT = 30000;
    private static final int READ_TIMEOUT = 30000;
    private static final String POST_URL = "https://pwgateway.com/api/token";
    private static final String POST_URL_TEST = "https://api.paymentwall.com/api/pro/v2/token";
    private static final String PW_GATEWAY_URL = "pwgateway.com";
    public static final String BROADCAST_FILTER_SDK = ".brick.PAYMENT_SDK_BROADCAST_PERMISSION";
    public static final String BROADCAST_FILTER_MERCHANT = ".brick.PAYMENT_MERCHANT_BROADCAST_PERMISSION";
    public static final String KEY_BRICK_TOKEN = "KEY_BRICK_TOKEN";
    public static final String KEY_MERCHANT_SUCCESS = "KEY_MERCHANT_SUCCESS";
    public static final String FILTER_BACK_PRESS_FRAGMENT = "FILTER_BACK_PRESS_FRAGMENT";
    public static final String FILTER_BACK_PRESS_ACTIVITY = "FILTER_BACK_PRESS_ACTIVITY";
    private Handler mHandler;
    private String mPublicKey;

    public Brick(Context context, String publicKey) {
        mHandler = new Handler(context.getMainLooper());
        this.mPublicKey = publicKey;
    }

    public Brick(Handler handler, String publicKey) {
        mHandler = handler;
        this.mPublicKey = publicKey;
    }

    // Static async mode
    public static void createToken(final Context context, final Handler handler, final String publicKey, final BrickCard brickCard, final Callback callback) {
        Thread createTokenThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    postBrickToken(callback, handler, createToken(context, publicKey, brickCard));
                } catch (BrickError brickError) {
                    postBrickError(callback, handler, brickError);
                }
            }
        });
        createTokenThread.start();
    }

    public static void createToken(Context context, final String publicKey, final BrickCard brickCard, final Callback callback) {
        final Handler handler = new Handler(context.getMainLooper());
        createToken(context, handler, publicKey, brickCard, callback);
    }

    // Static sync mode
    public static BrickToken createToken(Context context, String publicKey, BrickCard brickCard) throws BrickError {
        if (!brickCard.isValid()) {
            throw new BrickError("Invalid card", BrickError.Kind.INVALID);
        }
        if (publicKey == null) {
            throw new BrickError("Invalid public key", BrickError.Kind.INVALID);
        }
        // Prepare SSL
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
            // Create HTTP request
            Map<String, String> parameters = brickCard.getParameters();
            parameters.put(BrickCard.Params.KEY, publicKey);
            String queryUrl = BrickHelper.urlEncodeUTF8(parameters);
            URL url = createUrl(publicKey);
            // Connect
            HttpURLConnection conn = createPostRequest(url, queryUrl);
            conn = PwUtils.addExtraHeaders(context, conn);
            // Get message
            String response = getResponseBody(conn.getInputStream());
            Log.i("RESPONSE", response + "");
            try {
                BrickToken token = BrickHelper.createTokenFromJson(response);
                return token;
            } catch (BrickError e) {
                throw e;
            }
        } catch (Exception e) {
            if (e instanceof BrickError) {
                try {
                    throw e;
                } catch (IOException e1) {
                    throw new BrickError("(1) Unknown error", BrickError.Kind.UNEXPECTED);
                }
            } else {
                throw new BrickError("(2) Unknown error", BrickError.Kind.UNEXPECTED);
            }
        } finally {
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

    private static URL createUrl(String publicKey) throws BrickError {
        try {
            if (publicKey.startsWith("t_")) {
                return new URL(POST_URL_TEST);
            } else {
                return new URL(POST_URL);
            }
        } catch (MalformedURLException e) {
            throw new BrickError(e.getMessage(), BrickError.Kind.UNEXPECTED);
        }
    }

    private static HttpURLConnection createPostRequest(URL url, String queryUrl) throws BrickError {
        return createPostRequest(url, queryUrl, CONNECTION_TIMEOUT, READ_TIMEOUT);
    }

    private static HttpURLConnection createPostRequest(URL url, String queryUrl, int connectionTimeout, int readTimeout) throws BrickError {
        HttpURLConnection conn;
        try {
            conn = (HttpURLConnection) url.openConnection();
        } catch (IOException e) {
            throw new BrickError("IOException: Cannot open connection", BrickError.Kind.NETWORK);
        }
        conn.setConnectTimeout(connectionTimeout);
        conn.setReadTimeout(readTimeout);
        conn.setUseCaches(false);
        conn.setDoOutput(true);
        try {
            conn.setRequestMethod("POST");
        } catch (ProtocolException e) {
            throw new BrickError("Cannot send data", BrickError.Kind.UNEXPECTED);
        }
        conn.setRequestProperty("Content-Type", String.format(
                "application/x-www-form-urlencoded;charset=%s",
                new Object[]{"UTF-8"}));
        checkSSLCert(conn);
        OutputStream output = null;
        try {
            output = conn.getOutputStream();
            output.write(queryUrl.getBytes("UTF-8"));
            int statusCode = conn.getResponseCode();
            if (statusCode < 200 || statusCode >= 300) {
                String errorResponse;
                try {
                    errorResponse = getResponseBody(conn.getErrorStream());
                } catch (BrickError e) {
                    errorResponse = null;
                }
                throw new BrickError(errorResponse, BrickError.Kind.HTTP);
            }
        } catch (UnsupportedEncodingException e) {
            throw new BrickError("UnsupportedEncodingException", BrickError.Kind.UNEXPECTED);
        } catch (IOException e) {
            throw new BrickError("IOException (1)", BrickError.Kind.NETWORK);
        } finally {
            if (output != null) {
                try {
                    output.close();
                } catch (IOException e) {
                    throw new BrickError("IOException (2)", BrickError.Kind.NETWORK);
                }
            }
        }
        return conn;
    }

    private static String getResponseBody(InputStream responseStream)
            throws BrickError {
        String rBody = getStringFromInputStream(responseStream);
        try {
            responseStream.close();
        } catch (IOException e) {
            throw new BrickError("IOException: (1) cannot get response", BrickError.Kind.UNEXPECTED);
        }
        return rBody;
    }

    private static String getStringFromInputStream(InputStream is)
            throws BrickError {
        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();
        String line;
        try {
            br = new BufferedReader(new InputStreamReader(is));
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

        } catch (IOException e) {
            throw new BrickError("IOException: (2) cannot get response", BrickError.Kind.UNEXPECTED);
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    throw new BrickError("IOException: (3) cannot get response", BrickError.Kind.UNEXPECTED);
                }
            }
        }
        return sb.toString();

    }

    private static void checkSSLCert(HttpURLConnection con) throws BrickError {
        if ((!con.getURL().getHost().equals(PW_GATEWAY_URL))) {
            return;
        }

        HttpsURLConnection conn = (HttpsURLConnection) con;
        try {
            conn.connect();
        } catch (IOException ioexception) {
            throw new BrickError("Cannot connect", BrickError.Kind.NETWORK);
        }

        Certificate[] certs;
        try {
            certs = conn.getServerCertificates();
        } catch (SSLPeerUnverifiedException e) {
            throw new BrickError("Cannot verify the certificate", BrickError.Kind.NETWORK);
        }

        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            byte[] der = certs[0].getEncoded();
            md.update(der);
            byte[] digest = md.digest();
            byte[] revokedCertDigest = {5, -64, -77, 100, 54, -108, 71, 10,
                    -120, -116, 110, 127, -21, 92, -98, 36, -24, 35, -36, 83};
            if (Arrays.equals(digest, revokedCertDigest)) {
                throw new BrickError("Invalid certificate", BrickError.Kind.NETWORK);
            }
        } catch (NoSuchAlgorithmException e) {
            throw new BrickError("Cannot find SHA-1 MessageDigest", BrickError.Kind.UNEXPECTED);
        } catch (CertificateEncodingException e) {
            throw new BrickError("Unknown certificate", BrickError.Kind.UNEXPECTED);
        }
    }

    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    private static void postBrickToken(final Callback callback, final Handler handler, final BrickToken brickToken) {
        if (callback != null)
            handler.post(new Runnable() {
                @Override
                public void run() {
                    callback.onBrickSuccess(brickToken);
                }
            });
    }

    private static void postBrickError(final Callback callback, final Handler handler, final BrickError brickError) {
        if (callback != null)
            handler.post(new Runnable() {
                @Override
                public void run() {
                    callback.onBrickError(brickError);
                }
            });
    }

    public Brick setHandler(Handler handler) {
        this.mHandler = handler;
        return this;
    }

    public Brick setPublicKey(String publicKey) {
        this.mPublicKey = publicKey;
        return this;
    }

//    public BrickToken createToken(BrickCard brickCard) throws BrickError {
//        return createToken(mPublicKey, brickCard);
//    }
//
//    public void createToken(BrickCard brickCard, Callback callback) throws BrickError {
//        if (mHandler == null) mHandler = new Handler(Looper.getMainLooper());
//        createToken(mHandler, mPublicKey, brickCard, callback);
//    }

    public interface Callback {
        void onBrickSuccess(BrickToken brickToken);
        void onBrickError(BrickError error);
    }
}
