package com.huiiro.chatgpt.sdk.domain.image;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class ImageResponse implements Serializable {

    /**
     * 条目数据
     */
    private List<Item> data;

    /**
     * 创建时间
     */
    private long created;
}
