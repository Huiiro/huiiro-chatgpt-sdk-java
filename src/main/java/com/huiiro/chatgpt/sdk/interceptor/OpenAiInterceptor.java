package com.huiiro.chatgpt.sdk.interceptor;

import com.huiiro.chatgpt.sdk.common.Constants;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

/**
 * 拦截器 用于构建请求API KEY
 *
 * @author huii
 */
public class OpenAiInterceptor implements Interceptor {

    private final String apiKeyBySystem;

    public OpenAiInterceptor(String apiKey) {
        this.apiKeyBySystem = apiKey;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request original = chain.request();

        String apiKeyByUser = original.header(Constants.API_HEADER);
        String apiKey = null == apiKeyByUser ? apiKeyBySystem : apiKeyByUser;

        Request request = original.newBuilder()
                .url(original.url())
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .method(original.method(), original.body())
                .build();

        return chain.proceed(request);
    }
}
