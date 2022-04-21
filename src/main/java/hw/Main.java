package hw;

import com.beust.jcommander.JCommander;

import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class Main {
    static ScurlParams scurlParams = new ScurlParams();

    public static void main(String[] args) throws IOException {

        String juso = "http://httpbin.org";
        if (!args[0].equals("scurl")) {
            throw new IllegalArgumentException();
        }

        Main main = new Main();
        JCommander.newBuilder()
                .addObject(scurlParams)
                .build()
                .parse(args);

        main.socketConnect(juso);


    }

//    DONE 1: (scurl.jar) http://httpbin.org/get
//    DONE 2: (scurl.jar) -X GET http://httpbin.org/get
//    DONE 3: (scurl.jar) -v http://httpbin.org/get
//    DONE 4: (scurl.jar) -v -H "X-Custom-Header: NA" http://httpbin.org/get
//    DONE 6: (scurl.jar) -L http://httpbin.org/status/302

    public void socketConnect(String url) throws IOException {
        int cnt = 0;
        String reDirection = "";
        boolean is200 = false;
        while (cnt < 5 && !is200) {
            String getRequest = "GET ";
            String getRequestSlash = "/get";
            String postRequest = "POST ";
            String postRequestSlash = "/post";
            byte[] bytes = null;
            String message = null;
            bytes = new byte[1000];

            int port = 80;
            URL url1 = new URL(url);
            List<String> list = scurlParams.getParams();

            String method = getMethodFromHTTP(list);
            List<String> customHeader = getCustomHeader(list);
            String jsonBody = getJsonBody(list);
            int jsonBodyLength = 0;
            if (jsonBody != null) {
                jsonBodyLength = jsonBody.length();
            }
            try (Socket socket = new Socket(url1.getHost(), port)) {

                InputStream is = socket.getInputStream();
                PrintStream printStream = new PrintStream(socket.getOutputStream());
                String request = "";
                //POST 가 있으면 request 헤더를 바꾼다. post인데 -X 뒤에 get 이면 오류 발생  지금은 post 와 get 만 있다고 가정
                if (scurlParams.isMethod() && scurlParams.isPost() && method.equals("post") || method.equals("post") && !scurlParams.isGet()) {
                    request += postRequest + postRequestSlash + " HTTP/1.1";
                } else if (scurlParams.isMethod() && scurlParams.isGet() && method.equals("get") || method.equals("get") && !scurlParams.isPost()) {
                    request += getRequest + getRequestSlash + " HTTP/1.1";
                } else if (scurlParams.isNextRespond() && method.startsWith("status/")) {
                    if (cnt >= 1) {
                        request += getRequest + reDirection + " HTTP/1.1";
                    } else {
                        request += getRequest + "/" + method + " HTTP/1.1";
                    }
                } else {
                    throw new IllegalArgumentException();
                }

                request += "\nHost: " + url1.getHost();
                request += "\nUser-Agent: curl/7.79.1" + "\nAccept: */*";

                if (scurlParams.isHeader()) {
                    for (int i = 0; i < customHeader.size(); i++) {
                        request += "\n" + customHeader.get(i);
                    }
                }
                if (scurlParams.isData()) {
                    request += "\nContent-Type: application/json" + "\nContent-Length: " + jsonBodyLength;
                }
                printStream.println(request);
                printStream.println();
                // when post, add json to request body
                if (scurlParams.isData()) {
                    printStream.println(jsonBody);
                }

                printStream.println();
                printStream.println();

                int readByteCount = is.read(bytes);
                message = new String(bytes, 0, readByteCount, StandardCharsets.UTF_8);

                String[] strings = message.split("\r\n\r\n");
                if (!scurlParams.isNextRespond()) {
                    if (scurlParams.isVerbose()) {
                        System.out.println(request + "\n" + message);
                    } else {
                        System.out.println(strings[1]);
                    }
                    cnt = 999;
                }
                if (scurlParams.isNextRespond()) {
                    cnt++;
                    String[] strings1 = message.split("\r\n");
                    String strings2 = strings1[0].split(" ")[1];
                    if (strings2.equals("200")){
                        is200 = true;
                        System.out.println(message);
                    }else{
                        System.out.println(message);
                        for (int i = 0; i < strings1.length; i++) {
                            if (strings1[i].split(" ")[0].startsWith("location") || strings1[i].split(" ")[0].startsWith("Location")){
                                reDirection =  strings1[i].split(" ")[1].split("\r")[0];
                                break;
                            }
                        }
                    }
                }
            }
        }
    }

    private String getJsonBody(List<String> list) {
        for (int i = 0; i < list.size(); i++) {
            if(list.get(i).startsWith("{") && list.get(i).endsWith("}") && list.get(i).contains("\":")){

                return list.get(i);
            }
        }
        return null;
    }

    private List<String> getCustomHeader(List<String> list) {
        List<String> list1 = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            if(list.get(i).contains(": ") && !list.get(i).contains("\":")){
                list1.add(list.get(i));
            }
        }
        return list1;
    }

    private String getMethodFromHTTP(List<String> list) {
        for (int i = 0; i < list.size(); i++) {
            if(list.get(i).startsWith("http://httpbin.org")){
                return list.get(i).replace("http://httpbin.org/","");
            }
        }
        return "get";
    }

}

