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
    private InputStream in;
    private String method;
    private String path;
    private Map<String, String> headers = new HashMap<>();
    private Map<String, String> params;

    private static final Logger log = LoggerFactory.getLogger(HttpRequest.class);


    public HttpRequest(InputStream in) throws Exception {
        this.in = in;
        BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
        String line = br.readLine();
        log.debug("request line: {}", line);

        if (line == null) {
            return;
        }

        String[] token = line.split(" ");

        int contentLength = 0;
        Boolean logined = false;

        while(true) {
            line = br.readLine();
            if (line == null || "".equals(line)) {
                break;
            }
            log.debug("header: {}", line);
            if (line.contains("Content-Length")) {
                contentLength = getContentLength(line);
            }
            if (line.contains("Cookie")) {
                logined = isLogin(line);
            }
            HttpRequestUtils.Pair pair = HttpRequestUtils.parseHeader(line);
            headers.put(pair.getKey(), pair.getValue());
        }

        this.method = token[0].toUpperCase();
        this.path = token[1];

        if ("GET".equals(method)) {
            String[] urlTokens = path.split("[?]");
            this.path = urlTokens[0];
            params = HttpRequestUtils.parseQueryString(urlTokens[1]);
        }
        if("POST".equals(method)){
            String body = IOUtils.readData(br, Integer.parseInt(getHeader("Content-Length")));
            params = HttpRequestUtils.parseQueryString(body);
        }
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
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
