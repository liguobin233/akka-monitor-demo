package com.example;

import kamon.Kamon;
import kamon.trace.Span;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Arrays;

public class OkHttpInterceptor implements Interceptor {
    @NotNull
    @Override
    public Response intercept(@NotNull Chain chain) throws IOException {
        Request request = chain.request();
        Span span = Kamon.spanBuilder(request.url().toString())
                .name(request.url().toString())
                .tagMetrics("component", "okhttp3")
                .tagMetrics("http.method", request.method())
                .tagMetrics("operation", request.url().pathSegments().stream().reduce("", (left, right) -> left + "/" + right))
                .tagMetrics("path", request.url().toString()).start().takeSamplingDecision();
        Request traceRequest = request.newBuilder().addHeader("X-Request-ID", span.id().string()).build();
        Response response = chain.proceed(traceRequest);
        span.tagMetrics("http.status_code", response.code());

        span.finish();
        return response;
    }
}
