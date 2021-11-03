package me.aaronakhtar.blockonomics_wrapper.callback;

public class Callback {

    private String apiKey;
    private String httpContext;
    private String secretKey;

    public Callback(String apiKey, String httpContext, String secretKey) {
        this.apiKey = apiKey;
        this.httpContext = httpContext;
        this.secretKey = secretKey;
    }

    public String getApiKey() {
        return apiKey;
    }

    public String getHttpContext() {
        return httpContext;
    }

    public String getSecretKey() {
        return secretKey;
    }
}
