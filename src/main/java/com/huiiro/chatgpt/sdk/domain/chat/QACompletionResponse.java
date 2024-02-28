package com.huiiro.chatgpt.sdk.domain.chat;

import com.huiiro.chatgpt.sdk.domain.common.Choice;
import com.huiiro.chatgpt.sdk.domain.common.Usage;
import lombok.Data;

import java.io.Serializable;

@Data
public class QACompletionResponse implements Serializable {

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
    private Choice[] choices;

    /**
     * 创建
     */
    private long created;

    /**
     * 耗材
     */
    private Usage usage;

}
