package com.huiiro.chatgpt.sdk;

import com.huiiro.chatgpt.sdk.domain.audio.SpeechToTextResponse;
import com.huiiro.chatgpt.sdk.domain.billing.BillingUsage;
import com.huiiro.chatgpt.sdk.domain.billing.Subscription;
import com.huiiro.chatgpt.sdk.domain.chat.ChatCompletionRequest;
import com.huiiro.chatgpt.sdk.domain.chat.ChatCompletionResponse;
import com.huiiro.chatgpt.sdk.domain.embed.EmbeddingRequest;
import com.huiiro.chatgpt.sdk.domain.embed.EmbeddingResponse;
import com.huiiro.chatgpt.sdk.domain.image.ImageRequest;
import com.huiiro.chatgpt.sdk.domain.image.ImageResponse;
import com.huiiro.chatgpt.sdk.domain.chat.QACompletionRequest;
import com.huiiro.chatgpt.sdk.domain.chat.QACompletionResponse;
import io.reactivex.Single;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.http.*;

import java.time.LocalDate;
import java.util.Map;

public interface IOpenAiApi {

    /**
     * text
     */
    String v1_completions = "v1/completions";
    String v1_chat_completions = "v1/chat/completions";
    /**
     * image
     */
    String v1_images_generations = "v1/images/generations";
    String v1_images_edits = "v1/images/edits";
    /**
     * embed
     */
    String v1_embeddings = "v1/embeddings";
    /**
     * audio
     */
    String v1_audio_transcriptions = "v1/audio/transcriptions";
    String v1_audio_translations = "v1/audio/translations";
    /**
     * billing
     */
    String v1_dashboard_billing_subscription = "v1/dashboard/billing/subscription";
    String v1_dashboard_billing_usage = "v1/dashboard/billing/usage";

    /**
     * 文本问答
     *
     * @param qaCompletionRequest 请求信息
     * @return 应答结果
     */
    @POST(v1_completions)
    Single<QACompletionResponse> completions(@Body QACompletionRequest qaCompletionRequest);

    /**
     * 问答模型；默认 GPT-3.5
     *
     * @param chatCompletionRequest 请求信息
     * @return 应答结果
     */
    @POST(v1_chat_completions)
    Single<ChatCompletionResponse> chatCompletions(@Body ChatCompletionRequest chatCompletionRequest);

    /**
     * 生成图片
     *
     * @param imageRequest 图片对象
     * @return 应答结果
     */
    @POST(v1_images_generations)
    Single<ImageResponse> genImages(@Body ImageRequest imageRequest);

    /**
     * 修改图片
     *
     * @param image          图片对象
     * @param mask           图片对象
     * @param requestBodyMap 请求参数
     * @return 应答结果
     */
    @Multipart
    @POST(v1_images_edits)
    Single<ImageResponse> editImages(@Part MultipartBody.Part image,
                                     @Part MultipartBody.Part mask,
                                     @PartMap Map<String, RequestBody> requestBodyMap);

    /**
     * 向量计算
     *
     * @param embeddingRequest 请求对象
     * @return 应答结果
     */
    @POST(v1_embeddings)
    Single<EmbeddingResponse> embeddings(@Body EmbeddingRequest embeddingRequest);

    /**
     * 语音转文字
     *
     * @param file           语音文件
     * @param requestBodyMap 请求信息
     * @return 应答结果
     */
    @Multipart
    @POST(v1_audio_transcriptions)
    Single<SpeechToTextResponse> speedToTextTranscriptions(@Part MultipartBody.Part file, @PartMap() Map<String, RequestBody> requestBodyMap);

    /**
     * 语音翻译
     *
     * @param file           语音文件
     * @param requestBodyMap 请求信息
     * @return 应答结果
     */
    @Multipart
    @POST(v1_audio_translations)
    Single<SpeechToTextResponse> speedToTextTranslations(@Part MultipartBody.Part file, @PartMap() Map<String, RequestBody> requestBodyMap);

    /**
     * 账单查询
     *
     * @return 应答结果
     */
    @GET(v1_dashboard_billing_subscription)
    Single<Subscription> subscription();

    /**
     * 消耗查询
     *
     * @param starDate 开始时间
     * @param endDate  结束时间
     * @return 应答数据
     */
    @GET(v1_dashboard_billing_usage)
    Single<BillingUsage> billingUsage(@Query("start_date") LocalDate starDate, @Query("end_date") LocalDate endDate);
}
