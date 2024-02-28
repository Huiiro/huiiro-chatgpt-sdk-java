package com.huiiro.chatgpt.sdk.domain.audio;

import lombok.Data;

import java.io.Serializable;

@Data
public class SpeechToTextResponse implements Serializable {

    private String text;
}
