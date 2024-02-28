package com.huiiro.chatgpt.sdk.session;

import com.huiiro.chatgpt.sdk.IOpenAiApi;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import okhttp3.OkHttpClient;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSources;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Configuration {

    private IOpenAiApi openAiApi;

    private OkHttpClient okHttpClient;

    @NonNull
    private String apiKey;

    private String apiHost;

    public EventSource.Factory createRequestFactory() {
        return EventSources.createFactory(okHttpClient);
    }
}
