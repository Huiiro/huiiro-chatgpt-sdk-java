package com.huiiro.chatgpt.sdk;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.huiiro.chatgpt.sdk.common.Constants;
import com.huiiro.chatgpt.sdk.domain.billing.Subscription;
import com.huiiro.chatgpt.sdk.domain.chat.ChatCompletionRequest;
import com.huiiro.chatgpt.sdk.domain.chat.ChatCompletionResponse;
import com.huiiro.chatgpt.sdk.domain.chat.Message;
import com.huiiro.chatgpt.sdk.domain.image.ImageEnum;
import com.huiiro.chatgpt.sdk.domain.image.ImageRequest;
import com.huiiro.chatgpt.sdk.domain.image.ImageResponse;
import com.huiiro.chatgpt.sdk.session.Configuration;
import com.huiiro.chatgpt.sdk.session.OpenAiSession;
import com.huiiro.chatgpt.sdk.session.OpenAiSessionFactory;
import com.huiiro.chatgpt.sdk.session.defaults.DefaultOpenAiSessionFactory;
import lombok.extern.slf4j.Slf4j;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;
import org.junit.Before;
import org.junit.Test;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.Collections;
import java.util.concurrent.CountDownLatch;

@Slf4j
public class ApiTest {

    private OpenAiSession openAiSession;


    @Before
    public void test_init() {
        Configuration configuration = new Configuration();
        configuration.setApiHost("https://api.openai.com/");
        configuration.setApiKey("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");
        OpenAiSessionFactory factory = new DefaultOpenAiSessionFactory(configuration);
        Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("127.0.0.1", 7890));
        this.openAiSession = factory.openSession(proxy);
    }

    /**
     * test normal api
     */
    @Test
    public void test_api() throws JsonProcessingException {
        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                .messages(Collections.singletonList(
                        Message.builder().role(
                                Constants.Role.USER.getCode()).content("1+1=").build()))
                .model(ChatCompletionRequest.Model.GPT_3_5_TURBO.getCode())
                .build();

        ChatCompletionResponse chatCompletionResponse = openAiSession.chatCompletionsDefault(chatCompletionRequest);

        chatCompletionResponse.getChoices().forEach(e -> {
            log.info("测试结果：{}", e.getMessage());
        });
    }

    /**
     * test stream api
     */
    @Test
    public void test_stream_api() throws JsonProcessingException, InterruptedException {
        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                .messages(Collections.singletonList(
                        Message.builder().role(
                                Constants.Role.USER.getCode()).content("1+1=").build()))
                .model(ChatCompletionRequest.Model.GPT_3_5_TURBO.getCode())
                .stream(true)
                .build();

        EventSource eventSource = openAiSession.chatCompletions(chatCompletionRequest, new EventSourceListener() {
            @Override
            public void onEvent(EventSource eventSource, String id, String type, String data) {
                log.info("测试结果：{}", data);
            }
        });

        new CountDownLatch(1).await();
    }

    /**
     * test image api
     */
    @Test
    public void test_image_api() throws InterruptedException {
        ImageRequest request = ImageRequest.builder()
                .prompt("画个小猫")
                .model(ImageRequest.Model.DALL_E_3.getCode())
                .size(ImageEnum.Size.size_1024.getCode())
                .build();

        ImageResponse imageResponse = openAiSession.genImages(request);

        log.info("测试结果：{}", JSON.toJSONString(imageResponse.getData()));

        new CountDownLatch(1).await();
    }

    /**
     * test bill api
     */
    @Test
    public void test_bill_api() {
        Subscription subscription = openAiSession.subscription();

        log.info("测试结果：{}", JSON.toJSONString(subscription));
    }
}
