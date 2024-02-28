package com.huiiro.chatgpt.sdk.session;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.huiiro.chatgpt.sdk.domain.audio.SpeechToTextResponse;
import com.huiiro.chatgpt.sdk.domain.audio.TranscriptionsRequest;
import com.huiiro.chatgpt.sdk.domain.audio.TranslationsRequest;
import com.huiiro.chatgpt.sdk.domain.billing.BillingUsage;
import com.huiiro.chatgpt.sdk.domain.billing.Subscription;
import com.huiiro.chatgpt.sdk.domain.chat.ChatCompletionRequest;
import com.huiiro.chatgpt.sdk.domain.chat.ChatCompletionResponse;
import com.huiiro.chatgpt.sdk.domain.embed.EmbeddingRequest;
import com.huiiro.chatgpt.sdk.domain.embed.EmbeddingResponse;
import com.huiiro.chatgpt.sdk.domain.image.ImageEditRequest;
import com.huiiro.chatgpt.sdk.domain.image.ImageRequest;
import com.huiiro.chatgpt.sdk.domain.image.ImageResponse;
import com.huiiro.chatgpt.sdk.domain.chat.QACompletionRequest;
import com.huiiro.chatgpt.sdk.domain.chat.QACompletionResponse;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;

import java.io.File;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface OpenAiSession {

    /**
     * 文本问答
     *
     * @param qaCompletionRequest 请求信息
     * @return 应答结果
     * @deprecated deprecated as gpt3 using chatCompletions instead
     */
    @Deprecated
    QACompletionResponse completions(QACompletionRequest qaCompletionRequest);

    /**
     * 文本问答 & 流式反馈
     *
     * @param qaCompletionRequest 请求信息
     * @param eventSourceListener 实现监听；通过监听的 onEvent 方法接收数据
     * @deprecated deprecated as gpt3 using chatCompletions instead
     */
    @Deprecated
    EventSource completions(QACompletionRequest qaCompletionRequest, EventSourceListener eventSourceListener)
            throws JsonProcessingException;

    /**
     * 问答模型 GPT-3.5/4.0
     *
     * @param chatCompletionRequest 请求信息
     * @return 应答结果
     */
    ChatCompletionResponse chatCompletionsDefault(ChatCompletionRequest chatCompletionRequest);

    /**
     * 问答模型 GPT-3.5/4.0 & 流式反馈
     *
     * @param chatCompletionRequest 请求信息
     * @param eventSourceListener   实现监听；通过监听的 onEvent 方法接收数据
     * @return 应答结果
     */
    EventSource chatCompletions(ChatCompletionRequest chatCompletionRequest, EventSourceListener eventSourceListener)
            throws JsonProcessingException;

    /**
     * 问答模型 GPT-3.5/4.0 & 流式反馈 & 一次反馈
     *
     * @param chatCompletionRequest 请求信息
     * @return 应答结果
     */
    CompletableFuture<String> chatCompletions(ChatCompletionRequest chatCompletionRequest)
            throws JsonProcessingException;

    /**
     * 问答模型 GPT-3.5/4.0 & 流式反馈
     *
     * @param apiHostByUser         自定义host
     * @param apiKeyByUser          自定义Key
     * @param chatCompletionRequest 请求信息
     * @param eventSourceListener   实现监听；通过监听的 onEvent 方法接收数据
     * @return 应答结果
     */
    EventSource chatCompletions(String apiHostByUser, String apiKeyByUser, ChatCompletionRequest chatCompletionRequest,
                                EventSourceListener eventSourceListener) throws JsonProcessingException;

    /**
     * 生成图片
     *
     * @param prompt 图片描述
     * @return 应答结果
     */
    ImageResponse genImages(String prompt);

    /**
     * 生成图片
     *
     * @param imageRequest 图片描述
     * @return 应答结果
     */
    ImageResponse genImages(ImageRequest imageRequest);

    /**
     * 修改图片
     *
     * @param image  图片对象
     * @param prompt 修改描述
     * @return 应答结果
     */
    ImageResponse editImages(File image, String prompt);

    /**
     * 修改图片
     *
     * @param image            图片对象
     * @param imageEditRequest 图片参数
     * @return 应答结果
     */
    ImageResponse editImages(File image, ImageEditRequest imageEditRequest);

    /**
     * 修改图片
     *
     * @param image            图片对象，小于4M的PNG图片
     * @param mask             图片对象，小于4M的PNG图片
     * @param imageEditRequest 图片参数
     * @return 应答结果
     */
    ImageResponse editImages(File image, File mask, ImageEditRequest imageEditRequest);

    /**
     * 向量计算；单个文本
     *
     * @param input 文本信息
     * @return 应答结果
     */
    EmbeddingResponse embeddings(String input);

    /**
     * 向量计算；多个文本
     *
     * @param inputs 多个文本
     * @return 应答结果
     */
    EmbeddingResponse embeddings(String... inputs);


    /**
     * 向量计算；多个文本
     *
     * @param inputs 多个文本
     * @return 应答结果
     */
    EmbeddingResponse embeddings(List<String> inputs);

    /**
     * 向量计算；入参
     *
     * @param embeddingRequest 请求结果
     * @return 应答结果
     */
    EmbeddingResponse embeddings(EmbeddingRequest embeddingRequest);

    /**
     * 语音转文字
     *
     * @param file                  语音文件
     * @param transcriptionsRequest 请求信息
     * @return 应答结果
     */
    SpeechToTextResponse speedToTextTranscriptions(File file, TranscriptionsRequest transcriptionsRequest);

    /**
     * 语音翻译
     *
     * @param file                语音文件
     * @param translationsRequest 请求信息
     * @return 应答结果
     */
    SpeechToTextResponse speedToTextTranslations(File file, TranslationsRequest translationsRequest);

    /**
     * 账单查询
     *
     * @return 应答结果
     */
    Subscription subscription();

    /**
     * 消耗查询
     *
     * @param starDate 开始时间
     * @param endDate  结束时间
     * @return 应答数据
     */
    BillingUsage billingUsage(LocalDate starDate, LocalDate endDate);
}
