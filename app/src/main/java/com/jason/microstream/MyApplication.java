package com.jason.microstream;

import android.app.Application;
import android.content.Intent;

import com.zhy.http.okhttp.OkHttpUtils;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import okhttp3.OkHttpClient;

public class MyApplication extends Application {


    @Override
    public void onCreate() {
        super.onCreate();


        startService(new Intent(this, NioPeriodChronicService.class));




        SSLContext sslContext = null;
        try {
            sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, new TrustManager[]{xtm}, new SecureRandom());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
        HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        };
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
//          .addInterceptor(interceptor)
                .sslSocketFactory(sslContext.getSocketFactory())
                .hostnameVerifier(DO_NOT_VERIFY)
                .build();

//            .addInterceptor(new LogInterceptor(BuildConfig.DEBUG))
//            .connectTimeout(40000, TimeUnit.MILLISECONDS)
//            .readTimeout(40000, TimeUnit.MILLISECONDS)
//            .writeTimeout(40000, TimeUnit.MILLISECONDS)
//            //其他配置
//            .build();

        OkHttpUtils.initClient(okHttpClient);











    }




    X509TrustManager xtm = new X509TrustManager() {
        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) {
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            X509Certificate[] x509Certificates = new X509Certificate[0];
            return x509Certificates;
        }
    };



}
