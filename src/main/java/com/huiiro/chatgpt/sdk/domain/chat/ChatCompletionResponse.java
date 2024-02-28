package com.huiiro.chatgpt.sdk.domain.chat;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.huiiro.chatgpt.sdk.domain.common.Usage;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class ChatCompletionResponse implements Serializable {

    /**
     * ID
     */
    private String id;

    /**
     * 对象
     */
    private String object;

    /**
     * 模型
     */
    private String model;

    /**
     * 对话
     */
    private List<ChatChoice> choices;

    /**
     * 创建
     */
    private long created;

    /**
     * 耗材
     */
    private Usage usage;

    /**
     * 该指纹代表模型运行时使用的后端配置。
     * <a href="https://platform.openai.com/docs/api-reference/chat">see doc</a>
     */
    @JsonProperty("system_fingerprint")
    private String systemFingerprint;
}
