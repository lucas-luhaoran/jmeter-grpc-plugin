package pers.lucas.core.grpc;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import com.google.protobuf.util.JsonFormat;
import io.grpc.Channel;
import io.grpc.ClientInterceptor;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.AbstractStub;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

public class GrpcUtils {

    private GrpcUtils(){}

    public static ManagedChannel getChannel(String host, int port){
        return ManagedChannelBuilder.forAddress(host, port).usePlaintext().build();
    }

    //    todo: headers
    public static ManagedChannel getChannel(String host, int port, ClientInterceptor interceptor){
        return ManagedChannelBuilder.forAddress(host, port).usePlaintext().intercept(interceptor).build();
    }

    public static ManagedChannel getChannel(String host, int port, List<ClientInterceptor> interceptors){
        return ManagedChannelBuilder.forAddress(host, port).usePlaintext().intercept(interceptors).build();
    }

    public static Object doRequest(ManagedChannel channel, Message message, String method, Class<?> serviceGrpcClass, Class<? extends Message> requestClass) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method newBlockingStubMethod = serviceGrpcClass.getMethod("newBlockingStub", Channel.class);
        AbstractStub<?> blockingStub = (AbstractStub<?>) newBlockingStubMethod.invoke(null, channel);
        Method function = blockingStub.getClass().getMethod(method, requestClass);
        return function.invoke(blockingStub, requestClass.cast(message));
    }

    public static Object jsonToMessage(String jsonString, Class<? extends Message> messageClass) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException, InvalidProtocolBufferException {
        Class<?>[] innerClazz = messageClass.getDeclaredClasses();
        Class<?> builderClass = null;
        for (Class<?> x: innerClazz){
            if (x.getSimpleName().equals("Builder")){
                builderClass = x;
                break;
            }
        }
        if (builderClass == null){
            throw new ClassNotFoundException("Builder not found.");
        }
        Method builderMethod = messageClass.getMethod("newBuilder");
        Message.Builder builder = (Message.Builder) builderClass.cast(builderMethod.invoke(null));
        JsonFormat.Parser parser = JsonFormat.parser();
        parser.merge(jsonString, builder);
        return builder.build();
    }

    public static String MessageToJson(Message message, Class<? extends Message> messageClass) throws InvalidProtocolBufferException {
        return JsonFormat.printer().print(messageClass.cast(message));

    }
}
