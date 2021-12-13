package org.hf.hfhttpclient;


import android.text.TextUtils;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.CookieJar;
import okhttp3.Dns;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**********************************
 * @Name: HfHttpClientUtils
 * @Copyright： CreYond
 * @CreateDate： 2021/12/10 9:18
 * @author: HuangFeng
 * @Version： 1.0
 * @Describe:
 *
 **********************************/
public final class HfHttpClient {


    public static class Builder{

        private String baseUrl;
        private int connectTimeoutSec,readTimeoutSec;

        private Dns dns = hostname -> {
            List<InetAddress> list = Dns.SYSTEM.lookup(hostname);
            List<InetAddress> newlist = new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i) instanceof Inet4Address) {
                    newlist.add(list.get(i));
                }
            }
            return newlist;
        };
        private CookieJar cookieJar;
        private Interceptor logInterceptor = new HfLoggerInterceptor();
        private List<Interceptor> interceptors;
        private List<Interceptor> networkInterceptors;

        public static Builder create(){
            return new Builder();
        }

        public Builder setBaseUrl(String baseUrl){
            this.baseUrl = baseUrl;
            return this;
        }

        public Builder setTimeOut(int connectTimeoutSec,int readTimeoutSec){
            this.readTimeoutSec = readTimeoutSec;
            this.connectTimeoutSec = connectTimeoutSec;
            return this;
        }

        public Builder setDns(Dns dns) {
            this.dns = dns;
            return this;
        }

        public Builder setCookieJar(CookieJar cookieJar){
            this.cookieJar = cookieJar;
            return this;
        }

        public Builder setLogInterceptor(Interceptor logInterceptor) {
            this.logInterceptor = logInterceptor;
            return this;
        }

        public Builder addNetWorkInterceptor(Interceptor interceptor) {
            if(networkInterceptors == null)networkInterceptors = new ArrayList<>();
            networkInterceptors.add(interceptor);
            return this;
        }

        public Builder addInterceptor(Interceptor interceptor) {
            if(interceptors == null)interceptors = new ArrayList<>();
            interceptors.add(interceptor);
            return this;
        }

        public Retrofit build(){
            OkHttpClient.Builder builder = new OkHttpClient.Builder()
                    .connectTimeout(connectTimeoutSec==0?30:connectTimeoutSec,TimeUnit.SECONDS)
                    .readTimeout(readTimeoutSec==0?30:readTimeoutSec,TimeUnit.SECONDS)
                    .dns(dns)
                    .addInterceptor(logInterceptor);
            if(networkInterceptors!=null){
                for (Interceptor interceptor : networkInterceptors){
                    builder.addNetworkInterceptor(interceptor);
                }
            }
            if(interceptors!=null){
                for (Interceptor interceptor : interceptors){
                    builder.addInterceptor(interceptor);
                }
            }
            if(cookieJar!=null){
                builder.cookieJar(cookieJar);
            }
            return new Retrofit.Builder()
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .client(builder.build())
                    .baseUrl(baseUrl)
                    .build();
        }



    }

    public static Retrofit getLink(String baseUrl,
                                   int connectTimeoutSec,
                                   int readTimeoutSec,
                                   CookieJar hfCookieJar,
                                   Interceptor tokenInterceptor,
                                   Interceptor logInterceptor,
                                   Interceptor ... interceptors) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(connectTimeoutSec, TimeUnit.SECONDS)
                .readTimeout(readTimeoutSec, TimeUnit.SECONDS)
                .dns(hostname -> {
                    List<InetAddress> list = Dns.SYSTEM.lookup(hostname);
                    List<InetAddress> newlist = new ArrayList<>();
                    for (int i = 0; i < list.size(); i++) {
                        if (list.get(i) instanceof Inet4Address) {
                            newlist.add(list.get(i));
                        }
                    }
                    return newlist;
                });
        if(tokenInterceptor!=null){
            builder.addNetworkInterceptor(tokenInterceptor);//添加token
        }
        if(hfCookieJar!=null){
            builder.cookieJar(hfCookieJar);
        }
        if(logInterceptor!=null){
            builder.addInterceptor(logInterceptor);
        }else {
            builder.addInterceptor(new HfLoggerInterceptor());
        }
        for(Interceptor interceptor:interceptors){
            builder.addInterceptor(interceptor);
        }
        return new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(builder.build())
                .baseUrl(baseUrl)
                .build();
    }
}
