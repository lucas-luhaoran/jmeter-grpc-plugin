package pers.lucas.jmeter.functions;

import org.apache.jmeter.engine.util.CompoundVariable;
import org.apache.jmeter.functions.AbstractFunction;
import org.apache.jmeter.functions.InvalidVariableException;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.samplers.Sampler;
import org.apache.jmeter.threads.JMeterVariables;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class FileToBase64 extends AbstractFunction {
    private static final List<String> desc = new LinkedList<>();
    private static final String KEY = "__FileToBase64";

    static {
        desc.add(JMeterUtils.getResString("string_from_file_file_name"));
        desc.add(JMeterUtils.getResString("function_name_paropt"));
    }

    private Object[] values;

    @Override
    public String execute(SampleResult sampleResult, Sampler sampler) throws InvalidVariableException{
        String base64String;
        String varName;
        String filePath = ((CompoundVariable) values[0]).execute().trim();
        if (values.length > 1) {
            varName = ((CompoundVariable) values[1]).execute().trim();
        } else {
            varName = null;
        }
        File file = new File(filePath);
        try {
            base64String = new String(Base64.encodeBase64(FileUtils.readFileToByteArray(file)));
            if (varName != null) {
                JMeterVariables vars = getVariables();
                if (vars != null && varName.length() > 0){
                    vars.put(varName, base64String);
                }
            }
            return base64String;
        } catch (IOException e) {
            e.printStackTrace();
            throw new InvalidVariableException("\"Open file:<\"+filePath+\"> failed, check your file path\"");
        }
    }

    @Override
    public void setParameters(Collection<CompoundVariable> collection) throws InvalidVariableException {
        checkParameterCount(collection, 1, 2);
        values = collection.toArray();
    }

    @Override
    public String getReferenceKey() {
        return KEY;
    }

    @Override
    public List<String> getArgumentDesc() {
        return desc;
    }
}
