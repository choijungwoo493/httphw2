package hw;

import com.beust.jcommander.Parameter;
import java.util.ArrayList;
import java.util.List;

public class ScurlParams {
//    private RequestMethod requestMethod;

    private int cnt = 1;

    @Parameter(description = "else")
    private List<String> parameters = new ArrayList<>();

    @Parameter(names = {"-v", "-verbose"}, hidden = true)
    private boolean verbose = false;

    @Parameter(names = {"-H"}, hidden = true)
    private boolean header = false;

    @Parameter(names = {"-d"}, hidden = true)
    private boolean data = false;

    @Parameter(names = {"-X"}, hidden = true)
    private boolean method = false;

    @Parameter(names = {"-L"},hidden = true)
    private boolean nextRespond = false;

    @Parameter(names = {"-F"},hidden = true)
    private boolean file = false;

    @Parameter(names = {"POST"},hidden = true)
    private boolean post = false;

    @Parameter(names = {"GET"},hidden = true)
    private boolean get = false;


    public boolean isVerbose() {
        return verbose;
    }

    public boolean isHeader() {
        return header;
    }

    public boolean isData() {
        return data;
    }

    public boolean isMethod() {
        return method;
    }

    public boolean isNextRespond() {
        return nextRespond;
    }

    public boolean isFile() {
        return file;
    }

    public boolean isPost() {
        return post;
    }

    public boolean isGet() {
        return get;
    }

//    public String getMethod() {
//        if (isMethod()) {
//            requestMethod = RequestMethod.valueOf(parameters.get(cnt));
//            ++cnt;
//        }
//        return requestMethod.toString();
//    }
    public List<String> getParams(){
        return parameters;
    }

    public String getHeader(){
        String header;
        if(isHeader()){
            header = parameters.get(cnt);
            ++cnt;
        } else {
            return null;
        }
        return header;
    }

    public String getData() {
        String data;
        if (isData()) {
            data = parameters.get(cnt);
            ++cnt;
        } else {
            return null;
        }

        return data;
    }

    public String getLocation() {
        return parameters.get(cnt);
    }

//
//    public String getMethod(){
//        if(isMethod()){
//            RequestMethod.valueOf(parameters.get(cnt));
//            ++cnt;
//            System.out.println(requestMethod.name());
//        }else {
//            return null;
//        }
//        return requestMethod.name();
//    }

}

