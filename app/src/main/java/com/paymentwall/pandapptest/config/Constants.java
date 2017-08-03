package com.paymentwall.pandapptest.config;

import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PaymentActivity;

/**
 * Created by nguyen.anh on 6/3/2016.
 */
public class Constants {

//        public static final String PW_PROJECT_KEY = "9afb464faa93811fed34f9815677ae58"; // staging
//    public static final String PW_SECRET_KEY = "99438d36884f255f8853d6e04223128b";
//    public static final String PW_PROJECT_KEY = "f29e7441a54debd44f903a2b7c40b15d"; // live
//    public static final String PW_SECRET_KEY = "d283a4f7768976b2f1511e56ee3e1700";

    public static final String PW_PROJECT_KEY = "7e43822b844fd872abacf67a92fb826c"; // live
    public static final String PW_SECRET_KEY = "af022feeb89f61d036da526213634746";
    public static final String USER_ID = "testuser2";

    public static final String ITEM_MANA_ID = "item1";
    public static final String ITEM_SHIELD_ID = "item2";
    public static final String ITEM_GEM_ID = "item3";


    public static final class ALIPAY {
        public static final String PARTNER_ID = "2088611221573217";
        //        public static final String SELLER_ID = "money@paymentwall.com
        public static final String SELLER_ID = "overseas_test1@alipay.com";
        public static final String APP_ID = "2016090701861811"; // domestic
//        public static final String APP_ID = "external";// international


//        public static final String PRIVATE_KEY = "MIICAdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAPsV+ZV13nvRwVfcbvoVq26CZiUY0W3D1vaQWIE+8kMzTJ5fnZJaoC2NUGLNp/rM3OJvVr3F63Lt2cDEQuJ1AmZSPfPiXBvePk9Gf6XHkV4DIAlZEFKbn4ncDOE6gelK2ABO8RdP8BSd2uB2fkB1+LjmShQHwjMnMZmaT7kNpusjAgMBAAECgYAF3ZaOCknfRKAqqEijaPwvQbaXKURDNy7/ZZHbuxe3wCwLCxrIP3u8dK+rDzjcYArM82LoXgZJEgtjOHKDjc43f93eb9LWJOBIVdyFPICwCPLf3wir+59KP0M6P64jzxdnslW3uMTKKE+V1KHsWS4wppoWQl2w3PbYt8Pc1kgFUQJBAP4j/On467GZbkMZw0MQhcXRuXeaYIC/TjVkEE7kkJiuC+mSh8LpJGU6L4/cE9dg8dmPJTcmndeBokNC8cISRdsCQQD87EPwUMzmLUmpzAjOFr43Il2becns2OXHhUgoP0rHXSgbj2B7uAbx8MLsXB24a6l8+P3JwqwcEVYL4AhQ9IZZAkBKrNZvBZZz7AqLKvHGLf+wvcxKiZEbo5rec1WbaOvYASQ88LJ1oj3whKVfFyK5YaIO9FHSH8W3gj7klrIk/WuLAkAd7qgrrKRTKVDqc2cnXLGuPLbvS36GNTUcY72iZbsn1PKGMHWurpzDzUaESnKJ/NPWInGGiy9rMvIH/b9+NTIhAkEAkPOjmfa1WGxAR7lo2UXlkdO1F7JMyc9HgeB8GoZbJ1wNX9bHuugUtfR1hPvUIwPe+8e11rSp82lB7yczjeUl9w==";

        //money@paymentwall.com
//        public static final String PRIVATE_KEY = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAMykgaF6cS3zn8KG\n" +
//                "JCK7+FSPX9S6C+jb9fCRsz0rVf8VyKOl4+VZQrST0qFbox8/2sjc6zJSKsk5j6n4\n" +
//                "slktysO2Ln4Hd1TS+ohY0rZdJsx4pFD6Tl4mw8ysf+BBomLawvZ2Pw6Bo1y3+2N+\n" +
//                "pxeJKBeml3aDAFIQsZt7xyKq5eujAgMBAAECgYBGtc0I89rpqtWYDGuuCM88wLG3\n" +
//                "OnXlByQK9P0+9JtpYaiLS0XcuKQA61cLZDOQF6tMXnFyWMvp7dbDP2i4wcbejLFg\n" +
//                "8l1NHbpXBFdQ1R7w+etW5aYq8V4R88ih7h+vT4mCuSrAdM3N9SNmxxbBbP2NvJ0P\n" +
//                "/xbQvKpWShORFAFD+QJBAPQ61LkYlY+CxAmYiuVt5LvqSuX0+oqFzvswJ4DrhsaQ\n" +
//                "Y++1i9Xa6xR976OI12jgK98o09BpTMzYF5Es74AMKQcCQQDWgUVZ1JtY9A5ZWkj9\n" +
//                "vqWjju5zy8Ebd7XXFfq4Qzd6CMlS+zg4yU5HCMllDgiN0IhlQzHzr5DGDxcABtxt\n" +
//                "9s2FAkEAkfh/n8mDymx7wsh7a7kM1wunjF2pSRXEqfkUA0fCG/e84058r2rSykKi\n" +
//                "8fkd1j71ucFrJ8tn7Z7SJyDWXI/oDQJBALHI5t/zVOY/MX1hVUnqL4MGPiB/aggI\n" +
//                "ChDAyNLb8ykUCzjZutlmD5jUcYduQCy5/7JeJRKiGs3tR9Tf2ubRPbkCQEDsuddA\n" +
//                "xzgXVzN5WBxchyZmq/zddsMHr7RtuutON1YiXZSWdSJkf1DIBmSExEVJKhOBpOtG\n" +
//                "bQxLqHnusk2rpNg=";

        //fan.liu@paymentwall.com
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
        public static final String PUBLIC_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCEk1UpsSm8a8XedooHxBR0rY9BekgNvQQ7BNPdkHWVp+w54yT56jq8Vy6kLA6q486T1uY8l3HCFcviw5PbwK5nCi8hniaxznTeNg1fczrzUZAJSpUXeMRLUJ+y5dxMH4xFf4EnXvPV90CXR+tPlupl2jNPN9I5SICwOiKSj8cOwwIDAQAB";
    }

    public static final class WECHAT {
        public static final String APP_ID = "wx9a215ca656d8b864";
        public static final String MCH_ID = "1358437202";
        public static final String SECRET = "muse1358437202muse1358437202xhxh";
    }

    public static final class MOLPOINTS {
        public static final String APP_ID = "Rc5HNlCNq0ve3rrMhNqDhtFsGtRg3bdx";
        public static final String SECRET = "o15ZhIUhNxaB3OOgEhNVEQXPYQoD302c";
    }

    public static final class PAYPAL {
        public static final String CONFIG_ENVIRONMENT = PayPalConfiguration.ENVIRONMENT_SANDBOX;
        public static final String CONFIG_CLIENT_ID = "AY355MJzczGgt9tEJp1eiHqnskUhnChlgn0TEQIvFkL6h-QUchW_N6wiLZKJlSmb8_9Mus2KfirHTwLN"; //sandbox
        public static final String CONFIG_RECEIVER_EMAIL = "keylo_test@163.com";//sandbox
//        public static final String CONFIG_CLIENT_ID = "AZ9arqJDsGjHGtklQugRpOsGrLzqfJsSJMnjV21PCfhNGZhOJRpDqUfhCf9W0KnpR4mCfyQIp3Zh9vMF"; //live
//        public static final String CONFIG_RECEIVER_EMAIL = "keylo_test@163.com";//live
    }
}
