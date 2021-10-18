package pers.lucas.jmeter.protocol.grpc;

import io.grpc.ManagedChannel;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import org.apache.jmeter.samplers.AbstractSampler;
import org.apache.jmeter.samplers.Entry;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.testbeans.TestBean;
import org.apache.commons.lang3.StringUtils;

import pers.lucas.core.grpc.GrpcTestcaseEnum;
import pers.lucas.core.grpc.GrpcUtils;


// todo: metadata的ClientInterceptor的创建，metadata Manager的创建。
// todo: 打包优化
public class GrpcRequestSampler extends AbstractSampler implements TestBean {
    private static final long serialVersionUID = 1L;

    public final static String DOMAIN = "GrpcSampler.domain";
    public final static String PORT = "GrpcSampler.port";
    public final static String REQUEST_DATA = "GrpcSampler.request_data";

    private String testcase;

    public final static String[] testcaseList;
    public final static String defaultTestcase;
    private final static HashMap<String, GrpcTestcaseEnum> index = new HashMap<>();

    static {
        defaultTestcase = "TestService_doTest_v1";
        index.put(defaultTestcase, GrpcTestcaseEnum.TEST);
        testcaseList = index.keySet().toArray(new String[0]);
    }


    public GrpcRequestSampler(){
        setName("Grpc Request Sampler");
    }

    public String getDomain() {
        String domain = getPropertyAsString(DOMAIN);
        if (StringUtils.isEmpty(domain)){
            domain = "localhost";
        }
        return domain;
    }

    public void setDomain(String domain) {
        setProperty(DOMAIN, domain);
    }

    public int getPort() {
        return getPropertyAsInt(PORT);
    }

    public void setPort(int port) {
        setProperty(PORT, port);
    }

    public String getTestcase() {
        return testcase;
    }

    public void setTestcase(String testcase) {
        this.testcase = testcase;
    }

    public String getRequestData() {
        return getPropertyAsString(REQUEST_DATA);
    }

    public void setRequestData(String requestData) {
        setProperty(REQUEST_DATA, requestData);
    }

    @Override
    public SampleResult sample(Entry entry) {
        return sample();
    }

    public SampleResult sample() {
        return runSampler();
    }

    @Override
    public void removed() {
        super.removed();
    }

    public SampleResult runSampler() {

        SampleResult result = new SampleResult();
        result.setSampleLabel(getName());

        result.sampleStart();
        long start = System.currentTimeMillis();
        String responseString = "";
        GrpcTestcaseEnum info = index.get(testcase);

        try{
            // todo: json转proto对象耗时严重，10k需要100ms，待优化
            Object requestMessage = GrpcUtils.jsonToMessage(getRequestData(), info.getRequestMessageClass());
            ManagedChannel channel = GrpcUtils.getChannel(getDomain(), getPort());
            Object responseMessage = GrpcUtils.doRequest(
                    channel,
                    info.getRequestMessageClass().cast(requestMessage),
                    info.getMethod(),
                    info.getGrpcServiceClass(),
                    info.getRequestMessageClass()
            );

            setSuccess(result);
            responseString = GrpcUtils.MessageToJson(
                    info.getResponseMessageClass().cast(responseMessage),
                    info.getResponseMessageClass()
            );
        } catch (Exception e){
            setFailed(result);
            e.printStackTrace();
        }
        result.setLatency(System.currentTimeMillis() - start);
        result.sampleEnd();

        result.setSamplerData(
                "url:" + getDomain() + ":" + getPort() +
                        "\ntestcase:" + this.getTestcase() +
                        "\nRequestData:\n" + this.getRequestData()
        );
        result.setResponseData(responseString, String.valueOf(StandardCharsets.UTF_8));

        return result;
    }

    private void setSuccess(SampleResult result){
        result.setResponseCodeOK();
        result.setSuccessful(true);
    }

    private void setFailed(SampleResult result){
        result.setResponseCodeOK();
        result.setSuccessful(false);
    }

}
