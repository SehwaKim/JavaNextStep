package http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpRequestUtils;
import util.IOUtils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest {
    private Map<String, String> headers = new HashMap<>();
    private Map<String, String> params;
    private RequestLine requestLine;

    private static final Logger log = LoggerFactory.getLogger(HttpRequest.class);

    public HttpRequest(InputStream in) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
        String line = br.readLine();
        if (line == null) {
            return;
        }

        requestLine = new RequestLine(line);

        while(true) {
            line = br.readLine();
            if (line == null || "".equals(line)) {
                break;
            }
            log.debug("header: {}", line);
            HttpRequestUtils.Pair pair = HttpRequestUtils.parseHeader(line);
            headers.put(pair.getKey(), pair.getValue());
        }

        if (requestLine.getMethod() == HttpMethod.POST) {
            String body = IOUtils.readData(br, Integer.parseInt(getHeader("Content-Length")));
            params = HttpRequestUtils.parseQueryString(body);
        } else {
            params = requestLine.getParams();
        }
    }

    public HttpMethod getMethod() {
        return requestLine.getMethod();
    }

    public String getPath() {
        return requestLine.getPath();
    }

    public String getHeader(String header) {
        return headers.get(header);
    }

    public String getParameter(String param) {
        return params.get(param);
    }

    private int getContentLength(String line) {
        String[] headerTokens = line.split(":");
        return Integer.parseInt(headerTokens[1].trim());
    }

    private Boolean isLogin(String line) {
        String[] headerTokens = line.split(":");
        Map<String, String> cookies = HttpRequestUtils.parseCookies(headerTokens[1].trim());
        String val = cookies.get("logined");
        return Boolean.parseBoolean(val);
    }
}
