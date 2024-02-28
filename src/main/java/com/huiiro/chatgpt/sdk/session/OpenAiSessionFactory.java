package com.huiiro.chatgpt.sdk.session;

import java.net.Proxy;

public interface OpenAiSessionFactory {

    OpenAiSession openSession();

    OpenAiSession openSession(Proxy proxy);
}
