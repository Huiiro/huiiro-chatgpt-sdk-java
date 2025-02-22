package com.huiiro.chatgpt.sdk.domain.chat;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Data
@Builder
@Slf4j
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@AllArgsConstructor
public class QACompletionRequest implements Serializable {

    /**
     * 默认模型
     */
    @NonNull
    @Builder.Default
    private String model = Model.TEXT_DAVINCI_003.getCode();

    /**
     * 问题描述
     */
    @NonNull
    private String prompt;

    private String suffix;

    /**
     * 控制温度【随机性】；0到2之间。
     */
    private double temperature = 0.2;

    /**
     * 多样性控制；0.1 意味着只考虑包含前 10% 概率质量的代币
     */
    @JsonProperty("top_p")
    private Double topP = 1d;

    /**
     * 为每个提示生成的完成次数
     */
    private Integer n = 1;

    /**
     * 是否为流式输出；就是一蹦一蹦的，出来结果
     */
    private boolean stream = false;

    /**
     * 停止输出标识
     */
    private List<String> stop;

    /**
     * 输出字符串限制；0 ~ 4096
     */
    @JsonProperty("max_tokens")
    private Integer maxTokens = 2048;

    @Builder.Default
    private boolean echo = false;

    /**
     * 频率惩罚；降低模型重复同一行的可能性
     */
    @JsonProperty("frequency_penalty")
    private double frequencyPenalty = 0;

    /**
     * 存在惩罚；增强模型谈论新话题的可能性
     */
    @JsonProperty("presence_penalty")
    private double presencePenalty = 0;

    /**
     * 生成多个调用结果，只显示最佳的。这样会更多的消耗你的 api token
     */
    @Builder.Default
    @JsonProperty("best_of")
    private Integer bestOf = 1;

    private Integer logprobs;

    @SuppressWarnings("rawtypes")
    @JsonProperty("logit_bias")
    private Map logitBias;

    /**
     * 调用标识，避免重复调用
     */
    private String user;

    @Getter
    @AllArgsConstructor
    public enum Model {
        TEXT_DAVINCI_003("text-davinci-003"),
        TEXT_DAVINCI_002("text-davinci-002"),
        DAVINCI("davinci");
        private final String code;
    }

}
