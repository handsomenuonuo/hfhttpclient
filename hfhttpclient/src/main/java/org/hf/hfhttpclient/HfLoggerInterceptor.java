package org.hf.hfhttpclient;

import android.util.Log;

import java.io.IOException;

import io.reactivex.annotations.NonNull;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;


public class HfLoggerInterceptor implements Interceptor {

    private final String TAG = "LoggerInterceptor";
    
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request request = chain.request();

        //请求前--打印请求信息
        long t1 = System.nanoTime();
        Log.d(TAG, String.format("Sending request %s on %s%n%s",
                request.url(), chain.connection(), request.headers()));

        //网络请求
        Response response = chain.proceed(request);
        ResponseBody responseBody = response.peekBody(1024 * 1024);
        String respContent = responseBody.string();
        //网络响应后--打印响应信息
        long t2 = System.nanoTime();
        String logMsg = String.format("接收响应: [%s] %n返回json:【%s】 %.1fms%n%s",
                response.request().url(),
                respContent,
                (t2 - t1) / 1e6d,
                response.headers());
        i(TAG,logMsg);
        return response;

    }

    /**
     * 成功信息进行i日志输出
     * @param tag
     * @param msg
     */
    public void i(String tag, String msg) {  //信息太长,分段打印
        //因为String的length是字符数量不是字节数量所以为了防止中文字符过多，
        //  把4*1024的MAX字节打印长度改为2001字符数
        int max_str_length = 2001 - tag.length();
        //大于4000时
        while (msg.length() > max_str_length) {
            Log.i(tag, msg.substring(0, max_str_length));
            msg = msg.substring(max_str_length);
        }
        //剩余部分
        Log.i(tag, msg);
    }

}
