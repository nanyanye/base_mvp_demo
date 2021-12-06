package cn.test.demo.http.interceptor;

import android.util.Log;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;


/**
 * Created by Sam on  2021-12-06  17:05
 * Describe:   打印返回的json数据拦截器
 */

public class LoggingInterceptor implements Interceptor {
    private final String TAG = this.getClass().getSimpleName();
    private static LoggingInterceptor instance;

    public static LoggingInterceptor getInstance() {
        if (instance == null) {
            instance = new LoggingInterceptor();
        }
        return instance;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {

        Request request = chain.request();

        Request.Builder requestBuilder = request.newBuilder();
        request = requestBuilder.build();

        final Response response = chain.proceed(request);

        Log.i(TAG, "请求网址: \n" + request.url() + " \n " + "请求头部信息：\n" + request.headers() + "响应头部信息：\n" + response.headers());

        final ResponseBody responseBody = response.body();
        final long contentLength = responseBody.contentLength();

        BufferedSource source = responseBody.source();
        source.request(Long.MAX_VALUE); // Buffer the entire body.
        Buffer buffer = source.buffer();

        Charset charset = Charset.forName("UTF-8");
        MediaType contentType = responseBody.contentType();
        if (contentType != null) {
            try {
                charset = contentType.charset(charset);
            } catch (UnsupportedCharsetException e) {
                Log.i(TAG, "Couldn't decode the response body; charset is likely malformed.");
                return response;
            }
        }

        if (contentLength != 0) {
            Log.i(TAG, "--------------------------------------------开始打印返回数据----------------------------------------------------");
            Log.i(TAG, buffer.clone().readString(charset));
            Log.i(TAG, "--------------------------------------------结束打印返回数据----------------------------------------------------");
        }
        return response;
    }
}
