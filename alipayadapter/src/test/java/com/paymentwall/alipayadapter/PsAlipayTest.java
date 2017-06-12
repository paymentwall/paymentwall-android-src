package com.paymentwall.alipayadapter;

import android.os.Bundle;
import android.util.Log;

import org.junit.Test;

import static org.junit.Assert.*;


import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by nguyen.anh on 3/1/2017.
 */

public class PsAlipayTest {

    public static final String PRIVATE_KEY = "MIICeAIBADANBgkqhkiG9w0BAQEFAASCAmIwggJeAgEAAoGBAMqNdR7fkl9psQYd\n" +
            "VaSkxDJVKdCCSBxFrq6zZsFBUWzbnKeWIF+yLjgdWpsIdLbhI9MTfnTriyvjQCMs\n" +
            "8I77AmslKjecO4vJ0WSWGi9iymEoUluxt0r/5Ki+cu59pklPSqr2MsAqfUPN3mQh\n" +
            "Y+aJPWCVptzwt6jeopbKZaygcycPAgMBAAECgYBIdeSBP+/oLDJFAreBp+P8MZU8\n" +
            "326WlqMQImPPHOPZMpNPLJi2Q1LfhjjEn3jJd69OMnoZr63g7HIkY05HiCprsRVz\n" +
            "FrHiGi3WGCk0dnk9yWdh1aXH8E7DvJByuvLp7hYiZ3XbmQgKuceWXziF32KlhsC1\n" +
            "3WNICiaNh+/aGM1SgQJBAO1zEplma6g9/K7Nl7WyvCvnUQEZ1+bAnaDmYrQbUzed\n" +
            "hThIwLrMHK2K5/gacn2Jg0ug4AcSVWuOkPjVpl6/AcECQQDaYHST1uWJxzQUxjbe\n" +
            "yetBN85wBXsn1I/xjbunEF+BlKlKH9FZAYJ6X4QqyLROWPlkB8AGu55GHhUv/Mdu\n" +
            "d7zPAkEAwmgZjUC+2fNOY2vuTxQ0Xhm4R5d6HfIL3IVjwKQyoPE19lwSd53PyQJW\n" +
            "Y9p7bG7e8VeYtBF9oF8MML7zxNTLwQJBAJ3/tbkVfaQu/c6eDysoWs8oCnPgc1fB\n" +
            "Iph5nLDmVsORhN69oKFOR1I085Jk/pfFJkOqm9QDQSOoNlovATg6598CQQDWKD6f\n" +
            "yOoIqLhgWR6VNsjw5w9KbMDnG2tgdAOJucP8Cgn+GSnOlrhBf889/zHz8ThjAex8\n" +
            "7xBAqJIJeIla+TgO";

    public PsAlipay init() {
        PsAlipay psAlipay = new PsAlipay();
        Map<String, Object> bundle = new HashMap<>();
        bundle.put("ITEM_ID", "item1");
        bundle.put("ITEM_NAME", "Mana booster");
        bundle.put("CURRENCY", "USD");
        bundle.put("PW_PROJECT_KEY", "9afb464faa93811fed34f9815677ae58");
        bundle.put("SIGN_VERSION", 3);
        bundle.put("USER_ID", "testuser2");
        bundle.put("AMOUNT", 0.99);
        bundle.put("PW_PROJECT_SECRET", "99438d36884f255f8853d6e04223128b");
        psAlipay.setBundle(bundle);

        psAlipay.setAppId("2016090701861811");
        psAlipay.setPaymentType("1");
        psAlipay.setPrivateKey(PRIVATE_KEY);
        psAlipay.setSignType("RSA");
        psAlipay.setItbPay("30m");
        psAlipay.setForexBiz("FP");
        psAlipay.setAppenv("system=android^version=3.0.1.2");

        return psAlipay;
    }

    @Test
    public void testPrintWallApiRequest() {
        PsAlipay psAlipay = init();

        String sign = psAlipay.printWallApiMap(psAlipay.getWallApiParameterMap());

        assertEquals("Request map is not correct", "ag_external_id=item1ag_name=Mana boosteramount=0.99app_id=2016090701861811currencyCode=USDkey=9afb464faa93811fed34f9815677ae58sign=e69a3b03a56c396680d52ad59cd0ae9db9ba4715d579dce1094188ece4c0fbe7sign_version=3uid=testuser2", sign);

    }

    @Test
    public void testPrintAlipayMap() {
        PsAlipay psAlipay = init();

        Map<String, String> map = new HashMap<>();

        map.put("service", psAlipay.getService());
        map.put("partner", psAlipay.getPartnerId());
        map.put("seller_id", psAlipay.getSellerId());
        map.put("out_trade_no", psAlipay.getOutTradeNo());
        map.put("_input_charset", psAlipay.getInputCharset());
        map.put("notify_url", psAlipay.getNotifyUrl());
        map.put("subject", psAlipay.getSubject());
        map.put("payment_type", psAlipay.getPaymentType());
        map.put("total_fee", psAlipay.getTotalFee() + "");
        map.put("body", psAlipay.getBody());

        if (psAlipay.getItbPay() != null) {
            map.put("it_b_pay", psAlipay.getItbPay());
        }
        if (psAlipay.getForexBiz() != null) {
            map.put("forex_biz", psAlipay.getForexBiz());
        }
        if (psAlipay.getAppId() != null) {
            map.put("app_id", psAlipay.getAppId());
        }
        if (psAlipay.getAppenv() != null) {
            map.put("appenv", psAlipay.getAppenv());
            if (psAlipay.getBundle() != null) {
                map.put("currency", (String) psAlipay.getBundle().get("CURRENCY"));
            }
            String orderInfo = PsAlipay.printInternationalMap(PsAlipay.sortMap(map));
            String signature = "";

            if (psAlipay.getSignType().equalsIgnoreCase("rsa")) {
                signature = PsAlipay.signRsa(orderInfo, psAlipay.getPrivateKey());
                Log.i("SIGNATURE_BE", psAlipay.getSignature());
            } else if (psAlipay.getSignType().equalsIgnoreCase("md5")) {
                signature = PsAlipay.md5(orderInfo);
            }
            try {
                signature = psAlipay.getSignature();
                signature = URLEncoder.encode(signature, "UTF-8");
                psAlipay.setSignature(signature);

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            orderInfo += "&sign=\"" + psAlipay.getSignature() + "\"" + "&sign_type=\"" + psAlipay.getSignType() + "\"";

            assertEquals("Request string is not correct", "_input_charset=\"utf-8\"&app_id=\"external\"&appenv=\"system=android^version=3.0.1.2\"&body=\"Mana booster\"&currency=\"USD\"&forex_biz=\"FP\"&it_b_pay=\"30m\"&notify_url=\"https://api-trunk.s.stuffio.com/api/paymentpingback/alipay\"&out_trade_no=\"d65542121\"&partner=\"2088601120951045\"&payment_type=\"1\"&seller_id=\"money@paymentwall.com\"&service=\"mobile.securitypay.pay\"&subject=\"d65542121\"&total_fee=\"0.99\"&sign=\"i0C%2FKBDELcxSNJkqykxw3aBbB4KJNqS3r3uVySVsxnvZ9lO51FR%2FBn3EcxpxCDnDWTeQ6a9OOJb5InoDD7MT4Ah9vv3KlB3npbdKZExniejpWHHMiHNDpGhFfOzVuqi%2B7zTbA1ScLO53MvCuH5JchdCHsNde%2FziZbWK7J9r8R4E%3D\"&sign_type=\"RSA\"", orderInfo);
        }
    }
}
