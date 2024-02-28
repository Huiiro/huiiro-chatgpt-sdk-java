package com.huiiro.chatgpt.sdk.domain.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class Choice implements Serializable {

    private long index;

    private String text;

    private Object logprobs;

    @JsonProperty("finish_reason")
    private String finishReason;

}
