package com.huiiro.chatgpt.sdk.session.defaults;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.huiiro.chatgpt.sdk.IOpenAiApi;
import com.huiiro.chatgpt.sdk.common.Constants;
import com.huiiro.chatgpt.sdk.domain.audio.SpeechToTextResponse;
import com.huiiro.chatgpt.sdk.domain.audio.TranscriptionsRequest;
import com.huiiro.chatgpt.sdk.domain.audio.TranslationsRequest;
import com.huiiro.chatgpt.sdk.domain.billing.BillingUsage;
import com.huiiro.chatgpt.sdk.domain.billing.Subscription;
import com.huiiro.chatgpt.sdk.domain.chat.ChatChoice;
import com.huiiro.chatgpt.sdk.domain.chat.ChatCompletionRequest;
import com.huiiro.chatgpt.sdk.domain.chat.ChatCompletionResponse;
import com.huiiro.chatgpt.sdk.domain.chat.Message;
import com.huiiro.chatgpt.sdk.domain.embed.EmbeddingRequest;
import com.huiiro.chatgpt.sdk.domain.embed.EmbeddingResponse;
import com.huiiro.chatgpt.sdk.domain.image.ImageEditRequest;
import com.huiiro.chatgpt.sdk.domain.image.ImageRequest;
import com.huiiro.chatgpt.sdk.domain.image.ImageResponse;
import com.huiiro.chatgpt.sdk.domain.chat.QACompletionRequest;
import com.huiiro.chatgpt.sdk.domain.chat.QACompletionResponse;
import com.huiiro.chatgpt.sdk.session.Configuration;
import com.huiiro.chatgpt.sdk.session.OpenAiSession;
import okhttp3.*;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class DefaultOpenAiSession implements OpenAiSession {

    private final Configuration configuration;

    private final IOpenAiApi openAiApi;

    private final EventSource.Factory factory;

    public DefaultOpenAiSession(Configuration configuration) {
        this.configuration = configuration;
        this.openAiApi = configuration.getOpenAiApi();
        this.factory = configuration.createRequestFactory();
    }

    @Override
    public QACompletionResponse completions(QACompletionRequest qaCompletionRequest) {
        return this.openAiApi.completions(qaCompletionRequest).blockingGet();
    }

    @Override
    public EventSource completions(QACompletionRequest qaCompletionRequest, EventSourceListener eventSourceListener)
            throws JsonProcessingException {
        if (!qaCompletionRequest.isStream()) {
            throw new RuntimeException("illegal parameter： stream is false!");
        }

        Request request = new Request.Builder()
                .url(configuration.getApiHost().concat(IOpenAiApi.v1_completions))
                .post(RequestBody.create(MediaType.parse("application/json"),
                        new ObjectMapper().writeValueAsString(qaCompletionRequest)))
                .build();

        return factory.newEventSource(request, eventSourceListener);
    }

    @Override
    public ChatCompletionResponse chatCompletionsDefault(ChatCompletionRequest chatCompletionRequest) {
        return this.openAiApi.chatCompletions(chatCompletionRequest).blockingGet();
    }

    @Override
    public EventSource chatCompletions(ChatCompletionRequest chatCompletionRequest,
                                       EventSourceListener eventSourceListener)
            throws JsonProcessingException {
        return chatCompletions(Constants.NULL, Constants.NULL, chatCompletionRequest, eventSourceListener);
    }

    @Override
    public CompletableFuture<String> chatCompletions(ChatCompletionRequest chatCompletionRequest)
            throws JsonProcessingException {
        CompletableFuture<String> future = new CompletableFuture<>();
        StringBuffer dataBuffer = new StringBuffer();

        chatCompletions(chatCompletionRequest, new EventSourceListener() {
            @Override
            public void onOpen(EventSource eventSource, Response response) {
                super.onOpen(eventSource, response);
            }

            @Override
            public void onEvent(EventSource eventSource, String id, String type, String data) {
                if ("[DONE]".equalsIgnoreCase(data)) {
                    onClosed(eventSource);
                    future.complete(dataBuffer.toString());
                }
                ChatCompletionResponse chatCompletionResponse = JSON.parseObject(data, ChatCompletionResponse.class);
                List<ChatChoice> choices = chatCompletionResponse.getChoices();
                for (ChatChoice chatChoice : choices) {
                    Message delta = chatChoice.getDelta();
                    if (Constants.Role.ASSISTANT.getCode().equals(delta.getRole())) continue;

                    String finishReason = chatChoice.getFinishReason();
                    if ("stop".equalsIgnoreCase(finishReason)) {
                        onClosed(eventSource);
                        return;
                    }

                    try {
                        dataBuffer.append(delta.getContent());
                    } catch (Exception e) {
                        future.completeExceptionally(new RuntimeException("Request closed before completion"));
                    }

                }
            }

            @Override
            public void onClosed(EventSource eventSource) {
                future.complete(dataBuffer.toString());
            }

            @Override
            public void onFailure(EventSource eventSource, Throwable t, Response response) {
                future.completeExceptionally(new RuntimeException("Request closed before completion"));
            }
        });

        return future;
    }

    @Override
    public EventSource chatCompletions(String apiHostByUser, String apiKeyByUser,
                                       ChatCompletionRequest chatCompletionRequest,
                                       EventSourceListener eventSourceListener) throws JsonProcessingException {
        if (!chatCompletionRequest.isStream()) {
            throw new RuntimeException("illegal parameter： stream is false!");
        }

        String apiHost = Constants.NULL.equals(apiHostByUser) ? configuration.getApiHost() : apiHostByUser;
        String apiKey = Constants.NULL.equals(apiKeyByUser) ? configuration.getApiKey() : apiKeyByUser;

        Request request = new Request.Builder()
                .url(apiHost.concat(IOpenAiApi.v1_chat_completions))
                .addHeader(Constants.API_HEADER, apiKey)
                .post(RequestBody.create(MediaType.parse("application/json"),
                        new ObjectMapper().writeValueAsString(chatCompletionRequest)))
                .build();

        return factory.newEventSource(request, eventSourceListener);
    }

    @Override
    public ImageResponse genImages(String prompt) {
        ImageRequest imageRequest = ImageRequest.builder().prompt(prompt).build();
        return this.genImages(imageRequest);
    }

    @Override
    public ImageResponse genImages(ImageRequest imageRequest) {
        return this.openAiApi.genImages(imageRequest).blockingGet();
    }

    @Override
    public ImageResponse editImages(File image, String prompt) {
        ImageEditRequest imageEditRequest = ImageEditRequest.builder().prompt(prompt).build();
        return this.editImages(image, null, imageEditRequest);
    }

    @Override
    public ImageResponse editImages(File image, ImageEditRequest imageEditRequest) {
        return this.editImages(image, null, imageEditRequest);
    }

    @Override
    public ImageResponse editImages(File image, File mask, ImageEditRequest imageEditRequest) {

        RequestBody imageBody = RequestBody.create(MediaType.parse("multipart/form-data"), image);
        MultipartBody.Part imageMultipartBody = MultipartBody.Part.createFormData("image", image.getName(), imageBody);

        MultipartBody.Part maskMultipartBody = null;
        if (Objects.nonNull(mask)) {
            RequestBody maskBody = RequestBody.create(MediaType.parse("multipart/form-data"), mask);
            maskMultipartBody = MultipartBody.Part.createFormData("mask", mask.getName(), maskBody);
        }

        Map<String, RequestBody> requestBodyMap = new HashMap<>();
        requestBodyMap.put("prompt", RequestBody.create(MediaType.parse("multipart/form-data"),
                imageEditRequest.getPrompt()));
        requestBodyMap.put("n", RequestBody.create(MediaType.parse("multipart/form-data"),
                imageEditRequest.getN().toString()));
        requestBodyMap.put("size", RequestBody.create(MediaType.parse("multipart/form-data"),
                imageEditRequest.getSize()));
        requestBodyMap.put("response_format", RequestBody.create(MediaType.parse("multipart/form-data"),
                imageEditRequest.getResponseFormat()));
        if (!(Objects.isNull(imageEditRequest.getUser()) || "".equals(imageEditRequest.getUser()))) {
            requestBodyMap.put("user", RequestBody.create(MediaType.parse("multipart/form-data"),
                    imageEditRequest.getUser()));
        }
        return this.openAiApi.editImages(imageMultipartBody, maskMultipartBody, requestBodyMap).blockingGet();
    }

    @Override
    public EmbeddingResponse embeddings(String input) {
        EmbeddingRequest embeddingRequest = EmbeddingRequest.builder()
                .input(new ArrayList<String>() {{
                    add(input);
                }}).build();
        return this.embeddings(embeddingRequest);
    }

    @Override
    public EmbeddingResponse embeddings(String... inputs) {
        EmbeddingRequest embeddingRequest = EmbeddingRequest.builder().input(Arrays.asList(inputs)).build();
        return this.embeddings(embeddingRequest);
    }

    @Override
    public EmbeddingResponse embeddings(List<String> inputs) {
        EmbeddingRequest embeddingRequest = EmbeddingRequest.builder().input(inputs).build();
        return this.embeddings(embeddingRequest);
    }

    @Override
    public EmbeddingResponse embeddings(EmbeddingRequest embeddingRequest) {
        return this.openAiApi.embeddings(embeddingRequest).blockingGet();
    }

    @Override
    public SpeechToTextResponse speedToTextTranscriptions(File file, TranscriptionsRequest transcriptionsRequest) {

        RequestBody fileBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part multipartBody = MultipartBody.Part.createFormData("file", file.getName(), fileBody);

        Map<String, RequestBody> requestBodyMap = new HashMap<>();
        if (StringUtils.isNotBlank(transcriptionsRequest.getLanguage())) {
            requestBodyMap.put(TranscriptionsRequest.Fields.language,
                    RequestBody.create(MediaType.parse("multipart/form-data"),
                            transcriptionsRequest.getLanguage()));
        }
        if (StringUtils.isNotBlank(transcriptionsRequest.getModel())) {
            requestBodyMap.put(TranscriptionsRequest.Fields.model,
                    RequestBody.create(MediaType.parse("multipart/form-data"),
                            transcriptionsRequest.getModel()));
        }
        if (StringUtils.isNotBlank(transcriptionsRequest.getPrompt())) {
            requestBodyMap.put(TranscriptionsRequest.Fields.prompt,
                    RequestBody.create(MediaType.parse("multipart/form-data"),
                            transcriptionsRequest.getPrompt()));
        }
        if (StringUtils.isNotBlank(transcriptionsRequest.getResponseFormat())) {
            requestBodyMap.put(TranscriptionsRequest.Fields.responseFormat,
                    RequestBody.create(MediaType.parse("multipart/form-data"),
                            transcriptionsRequest.getResponseFormat()));
        }
        requestBodyMap.put(TranscriptionsRequest.Fields.temperature,
                RequestBody.create(MediaType.parse("multipart/form-data"),
                        String.valueOf(transcriptionsRequest.getTemperature())));

        return this.openAiApi.speedToTextTranscriptions(multipartBody, requestBodyMap).blockingGet();
    }

    @Override
    public SpeechToTextResponse speedToTextTranslations(File file, TranslationsRequest translationsRequest) {

        RequestBody fileBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part multipartBody = MultipartBody.Part.createFormData("file", file.getName(), fileBody);

        Map<String, RequestBody> requestBodyMap = new HashMap<>();
        if (StringUtils.isNotBlank(translationsRequest.getModel())) {
            requestBodyMap.put(TranslationsRequest.Fields.model,
                    RequestBody.create(MediaType.parse("multipart/form-data"),
                            translationsRequest.getModel()));
        }
        if (StringUtils.isNotBlank(translationsRequest.getPrompt())) {
            requestBodyMap.put(TranslationsRequest.Fields.prompt,
                    RequestBody.create(MediaType.parse("multipart/form-data"),
                            translationsRequest.getPrompt()));
        }
        if (StringUtils.isNotBlank(translationsRequest.getResponseFormat())) {
            requestBodyMap.put(TranslationsRequest.Fields.responseFormat,
                    RequestBody.create(MediaType.parse("multipart/form-data"),
                            translationsRequest.getResponseFormat()));
        }
        requestBodyMap.put(TranslationsRequest.Fields.temperature,
                RequestBody.create(MediaType.parse("multipart/form-data"),
                        String.valueOf(translationsRequest.getTemperature())));
        requestBodyMap.put(TranscriptionsRequest.Fields.temperature,
                RequestBody.create(MediaType.parse("multipart/form-data"),
                        String.valueOf(translationsRequest.getTemperature())));

        return this.openAiApi.speedToTextTranslations(multipartBody, requestBodyMap).blockingGet();
    }

    @Override
    public Subscription subscription() {
        return this.openAiApi.subscription().blockingGet();
    }

    @Override
    public BillingUsage billingUsage(LocalDate starDate, LocalDate endDate) {
        if (starDate == null || endDate == null) {
            throw new RuntimeException("starDate or endDate must be not empty!");
        }
        return this.openAiApi.billingUsage(starDate, endDate).blockingGet();
    }
}
