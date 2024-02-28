package com.huiiro.chatgpt.sdk.domain.image;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Item implements Serializable {

    private String url;

    @JsonProperty("b64_json")
    private String b64Json;

    @JsonProperty("revised_prompt")
    private String revisedPrompt;
}
