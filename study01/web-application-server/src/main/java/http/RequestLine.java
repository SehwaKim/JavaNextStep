package http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpRequestUtils;

import java.util.HashMap;
import java.util.Map;

public class RequestLine {
    private static final Logger log = LoggerFactory.getLogger(RequestLine.class);

    private HttpMethod method;
    private String path;
    private Map<String, String> params = new HashMap<>();

    public RequestLine(String requestLine) {
        String[] token = requestLine.split(" ");
        method = HttpMethod.valueOf(token[0]);

        if (method.isPost()) {
            path = token[1];
            return;
        }

        int index = token[1].indexOf("?");
        if (index == -1) {
            path = token[1];
            return;
        }
        path = token[1].substring(0, index);
        params = HttpRequestUtils.parseQueryString(token[1].substring(index + 1));
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public Map<String, String> getParams() {
        return params;
    }
}
