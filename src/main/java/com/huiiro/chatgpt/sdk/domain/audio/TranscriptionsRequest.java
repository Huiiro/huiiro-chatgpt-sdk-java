package com.huiiro.chatgpt.sdk.domain.audio;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldNameConstants
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TranscriptionsRequest {

    /**
     * 模型
     */
    @Builder.Default
    private String model = SpeechToTextEnum.Model.WHISPER_1.getCode();

    /**
     * 提示语
     */
    private String prompt;
    /**
     * 输出格式
     */
    @JsonProperty("response_format")
    @Builder.Default
    private String responseFormat = SpeechToTextEnum.ResponseFormat.JSON.getCode();

    /**
     * 控制温度
     */
    private double temperature = 0.2;

    /**
     * 音频语言；ISO-639-1
     */
    private String language;

}
