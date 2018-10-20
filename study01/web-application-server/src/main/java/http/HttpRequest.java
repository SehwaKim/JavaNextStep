package http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpRequestUtils;
import util.IOUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest {
    private Map<String, String> headers = new HashMap<>();
    private Map<String, String> params;
    private RequestLine requestLine;
    private HttpCookie httpCookie;

    private static final Logger log = LoggerFactory.getLogger(HttpRequest.class);

    public HttpRequest(InputStream in) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
        String firstLine = br.readLine();

        if (isBlank(firstLine)) {
            throw new IllegalArgumentException();
        }

        requestLine = new RequestLine(firstLine);

        addHeader(br);

        httpCookie = new HttpCookie(headers.get("Cookie"));

        if (getMethod().isPost()) {
            String body = IOUtils.readData(br, Integer.parseInt(getHeader("Content-Length")));
            params = HttpRequestUtils.parseQueryString(body);
        } else {
            params = requestLine.getParams();
        }
    }

    private boolean isBlank(String firstLine) {
        return firstLine == null || "".equals(firstLine.trim());
    }

    private void addHeader(BufferedReader br) throws IOException {
        String line;
        while(true) {
            line = br.readLine();
            if (isBlank(line)) {
                break;
            }
            log.debug("header: {}", line);
            HttpRequestUtils.Pair pair = HttpRequestUtils.parseHeader(line);
            headers.put(pair.getKey(), pair.getValue());
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

    public HttpCookie getCookies() {
        return httpCookie;
    }
}
