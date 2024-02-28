package com.huiiro.chatgpt.sdk.domain.embed;

import com.huiiro.chatgpt.sdk.domain.common.Usage;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class EmbeddingResponse implements Serializable {

    private String object;

    private List<Item> data;

    private String model;

    private Usage usage;

}
