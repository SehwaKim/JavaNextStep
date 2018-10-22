package http;

import util.HttpRequestUtils;

import java.util.HashMap;
import java.util.Map;

public class HttpCookie {
    private Map<String, String> cookies = new HashMap<>();

    public HttpCookie(String cookieString) {
        cookies.putAll(HttpRequestUtils.parseCookies(cookieString));
    }

    public String getCookie(String key) {
        return cookies.get(key);
    }
}
