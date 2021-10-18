package pers.lucas.core.grpc;

import com.google.protobuf.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pers.grpc.test.TestRequest;
import pers.grpc.test.TestResponse;
import pers.grpc.test.TestServiceGrpc;


public enum GrpcTestcaseEnum {
    TEST("doTest", TestServiceGrpc.class, TestRequest.class, TestResponse.class);

    private static final Logger log = LoggerFactory.getLogger(GrpcTestcaseEnum.class);

    private final String method;
    private final Class<?> grpcServiceClass;
    private final Class<? extends Message> requestMessageClass;
    private final Class<? extends Message> responseMessageClass;

    public String getMethod() {
        return method;
    }

    public Class<?> getGrpcServiceClass() {
        return grpcServiceClass;
    }

    public Class<? extends Message> getRequestMessageClass() {
        return requestMessageClass;
    }

    public Class<? extends Message> getResponseMessageClass() {
        return responseMessageClass;
    }

    GrpcTestcaseEnum(
            String method,
            Class<?> grpcServiceClass,
            Class<? extends Message> requestMessageClass,
            Class<? extends Message> responseMessageClass
    ){
        this.method = method;
        this.grpcServiceClass = grpcServiceClass;
        this.requestMessageClass = requestMessageClass;
        this.responseMessageClass = responseMessageClass;
    }
}
