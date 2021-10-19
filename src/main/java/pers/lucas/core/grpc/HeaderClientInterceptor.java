package pers.lucas.core.grpc;

import io.grpc.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Objects;

public class HeaderClientInterceptor implements ClientInterceptor {
    private static final Logger logger = LoggerFactory.getLogger(HeaderClientInterceptor.class.getName());

    // todo: 临时方案，写完metadata header后，重构此部分。
    private HashMap<String, String> requestHeaders = new HashMap<>();
    private static final HashMap<String, String> responseHeaders = new HashMap<>();

    public void setRequestHeaders(HashMap<String, String> requestHeaders){
        this.requestHeaders = requestHeaders;
    }

    @Override
    public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(MethodDescriptor<ReqT, RespT> method,
                                                               CallOptions callOptions, Channel next) {
        return new ForwardingClientCall.SimpleForwardingClientCall<ReqT, RespT>(next.newCall(method, callOptions)) {

            @Override
            public void start(Listener<RespT> responseListener, Metadata headers) {
                for (String key: requestHeaders.keySet()){
                    Metadata.Key<String> k = Metadata.Key.of(key, Metadata.ASCII_STRING_MARSHALLER);
                    headers.put(k, Objects.requireNonNull(requestHeaders.get(key)));
                }
                super.start(new ForwardingClientCallListener.SimpleForwardingClientCallListener<RespT>(responseListener) {
                    @Override
                    public void onHeaders(Metadata headers) {
                        logger.info("header received from server:" + headers);
                        super.onHeaders(headers);
                    }
                }, headers);
            }
        };
    }
}
