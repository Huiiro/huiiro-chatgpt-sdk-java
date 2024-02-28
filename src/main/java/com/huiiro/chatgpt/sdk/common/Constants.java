package com.huiiro.chatgpt.sdk.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class Constants {

    public final static String NULL = "NULL";

    public final static String API_HEADER = "api";

    /**
     * 支持的请求角色类型：system、user、assistant
     * <a href="https://platform.openai.com/docs/guides/chat/introduction">see doc</a>
     */
    @Getter
    @AllArgsConstructor
    public enum Role {
        SYSTEM("system"),
        USER("user"),
        ASSISTANT("assistant");
        private final String code;
    }
}
