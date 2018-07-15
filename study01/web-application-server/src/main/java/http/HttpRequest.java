package http;

import util.HttpRequestUtils;
import util.IOUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest {
    private String method;
    private String path;
    private Map<String, String> headers;
    private Map<String, String> parameters;
    private Map<String, String> cookies;

    public HttpRequest(InputStream in) {
        headers = new HashMap<>();
        parameters = new HashMap<>();

        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            String line = br.readLine();
            if (line == null) {
                return;
            }

            processRequestLine(line);
            
            while ((line = br.readLine()) != null) {
                if ("".equals(line)) {
                    break;
                }
                if (line.startsWith("Cookie")) {
                    cookies = HttpRequestUtils.parseCookies(line);
                }
                HttpRequestUtils.Pair p = HttpRequestUtils.parseHeader(line);
                headers.put(p.getKey(), p.getValue());
            }

            if ("POST".equals(method.toUpperCase())) {
                String body = IOUtils.readData(br, getContentLength());
                parameters = HttpRequestUtils.parseQueryString(body);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void processRequestLine(String line) {
        String[] token = line.split(" ");
        method = token[0];

        if ("POST".equals(method)) {
            path = token[1];
            return;
        }

        int index = token[1].indexOf("?");
        if (index == -1) {
            path = token[1];
        } else {
            path = token[1].substring(0, index);
            parameters = HttpRequestUtils.parseQueryString(token[1].substring(index + 1));
        }
    }

    private int getContentLength() {
        if(requestBodyExist()){
            return Integer.parseInt(headers.get("Content-Length"));
        }
        return 0;
    }

    private boolean requestBodyExist() {
        return headers.get("Content-Length") != null;
    }

    public String getHeader(String key) {
        return headers.get(key);
    }

    public String getParameter(String key) {
        return parameters.get(key);
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }
}
