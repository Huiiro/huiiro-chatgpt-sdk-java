package com.huiiro.chatgpt.sdk.session.defaults;

import com.huiiro.chatgpt.sdk.IOpenAiApi;
import com.huiiro.chatgpt.sdk.interceptor.OpenAiInterceptor;
import com.huiiro.chatgpt.sdk.session.Configuration;
import com.huiiro.chatgpt.sdk.session.OpenAiSession;
import com.huiiro.chatgpt.sdk.session.OpenAiSessionFactory;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import org.apache.commons.lang3.ObjectUtils;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.net.Proxy;
import java.util.concurrent.TimeUnit;

/**
 * session工厂默认实现类
 *
 * @author huii
 */
public class DefaultOpenAiSessionFactory implements OpenAiSessionFactory {

    private final Configuration configuration;

    public DefaultOpenAiSessionFactory(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public OpenAiSession openSession() {
        return openSession(null);
    }

    @Override
    public OpenAiSession openSession(Proxy proxy) {
        OkHttpClient okHttpClient = buildClient(proxy);

        configuration.setOkHttpClient(okHttpClient);

        IOpenAiApi openAiApi = new Retrofit.Builder()
                .baseUrl(configuration.getApiHost())
                .client(okHttpClient)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(JacksonConverterFactory.create())
                .build().create(IOpenAiApi.class);

        configuration.setOpenAiApi(openAiApi);

        return new DefaultOpenAiSession(configuration);
    }

    private OkHttpClient buildClient(Proxy proxy) {
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder builder = new OkHttpClient
                .Builder()
                .addInterceptor(httpLoggingInterceptor)
                .addInterceptor(new OpenAiInterceptor(configuration.getApiKey()))
                .connectTimeout(500, TimeUnit.SECONDS)
                .writeTimeout(500, TimeUnit.SECONDS)
                .readTimeout(500, TimeUnit.SECONDS);

        if (ObjectUtils.isNotEmpty(proxy)) {
            builder.proxy(proxy);
        }

        return builder.build();
    }

}
