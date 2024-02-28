package com.huiiro.chatgpt.sdk.domain.chat;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChatCompletionRequest implements Serializable {

    /**
     * 默认模型
     */
    private String model = Model.GPT_3_5_TURBO.getCode();

    /**
     * 问题描述
     */
    private List<Message> messages;

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
     * 是否为流式输出
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
    @SuppressWarnings("rawtypes")
    @JsonProperty("logit_bias")
    private Map logitBias;

    /**
     * 调用标识，避免重复调用
     */
    private String user;

    /**
     * <a href="https://platform.openai.com/docs/guides/text-generation">doc</a>
     */
    @Getter
    @AllArgsConstructor
    public enum Model {
        GPT_3_5_TURBO("gpt-3.5-turbo"),
        GPT_4("gpt-4"),
        GPT_4_32K("gpt-4-32k");
        private final String code;
    }
}
