package pers.lucas.jmeter.protocol.grpc;

import org.apache.jmeter.testbeans.BeanInfoSupport;
import org.apache.jmeter.testbeans.gui.TypeEditor;

import java.beans.PropertyDescriptor;

public class GrpcRequestSamplerBeanInfo extends BeanInfoSupport {
    public GrpcRequestSamplerBeanInfo() {
        super(GrpcRequestSampler.class);
        createPropertyGroup("Basic", new String[]{"domain", "port"});
        createPropertyGroup("Grpc Request", new String[]{"testcase", "requestData"});

        PropertyDescriptor p;

        p = property("domain");
        p.setValue(DEFAULT, "localhost");
        p.setValue(NOT_UNDEFINED, Boolean.TRUE);

        p = property("port");
        p.setValue(DEFAULT, 5005);
        p.setValue(NOT_UNDEFINED, Boolean.TRUE);

        p = property("testcase");
        p.setValue(NOT_UNDEFINED, Boolean.TRUE);
        p.setValue(NOT_OTHER, Boolean.TRUE);
        p.setValue(NOT_EXPRESSION, Boolean.TRUE);
        p.setValue(TAGS, GrpcRequestSampler.testcaseList);
        p.setValue(DEFAULT, GrpcRequestSampler.defaultTestcase);

        p = property("requestData", TypeEditor.TextAreaEditor);
        p.setValue(NOT_UNDEFINED, Boolean.TRUE);
        p.setValue(DEFAULT, "{}");

    }
}
