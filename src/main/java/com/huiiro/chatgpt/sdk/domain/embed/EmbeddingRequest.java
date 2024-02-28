package com.huiiro.chatgpt.sdk.domain.embed;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.List;

@Slf4j
@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@AllArgsConstructor
public class EmbeddingRequest implements Serializable {

    /**
     * 模型
     */
    @NonNull
    @Builder.Default
    private String model = Model.TEXT_EMBEDDING_ADA_002.getCode();

    /**
     * 输入信息
     */
    @NonNull
    private List<String> input;

    @Setter
    private String user;

    /**
     * <a href="https://platform.openai.com/docs/guides/embeddings">doc</a>
     */
    @Getter
    @AllArgsConstructor
    public enum Model {
        TEXT_EMBEDDING_ADA_002("text-embedding-ada-002"),
        TEXT_EMBEDDING_3_SMALL("text-embedding-3-small"),
        TEXT_EMBEDDING_3_LARGE("text-embedding-3-large");
        private final String code;
    }

}
