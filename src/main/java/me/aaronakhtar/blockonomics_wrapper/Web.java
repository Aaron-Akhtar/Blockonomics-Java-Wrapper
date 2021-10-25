package me.aaronakhtar.blockonomics_wrapper;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;

@SuppressWarnings("Duplicates")
public class Web {
    protected static final String blockonomicsApi = "https://www.blockonomics.co/api/";
    private static final String userAgent = "Mozilla/5.0 (X11; Linux x86_64; rv:45.0) Gecko/20100101 Firefox/45.0";

    private Web(){}

    public static final Gson gson = new Gson();
    private static final JsonParser jsonParser = new JsonParser();

            // POST
    public static JsonObject makeRequest(String target, String body, boolean authRequired, Blockonomics apiInstance) {
        try {
            final HttpURLConnection connection = (HttpURLConnection) new URL(blockonomicsApi + target).openConnection();
            try (AutoCloseable autoCloseable = () -> connection.disconnect()) {
                if (authRequired) connection.setRequestProperty("Authorization", "Bearer " + apiInstance.getApiKey());
                connection.addRequestProperty("User-Agent", userAgent);
                connection.setRequestProperty("Content-Type", "application/json; utf-8");
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                if (body.length() != 0) connection.getOutputStream().write(body.getBytes(StandardCharsets.UTF_8));
                final StringBuilder stringBuilder = new StringBuilder();
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                    String i;
                    while ((i = reader.readLine()) != null) {
                        stringBuilder.append(i);
                    }
                }
                if (stringBuilder.length() == 0) stringBuilder.append("{addr:0}");  // null replacement
                return jsonParser.parse(stringBuilder.toString()).getAsJsonObject();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

            // GET
    protected static JsonObject makeRequest(String target, HashMap<String, String> parameters, boolean authRequired, Blockonomics apiInstance) {
        try {
            HttpURLConnection connection;
            if (!parameters.isEmpty()) {
                StringJoiner joiner = new StringJoiner("&");
                for (Map.Entry<String, String> param : parameters.entrySet()) {
                    joiner.add(param.getKey() + "=" + param.getValue());
                }
                connection = (HttpURLConnection) new URL(blockonomicsApi + target + "?" + joiner.toString()).openConnection();
            }else{
                connection = (HttpURLConnection) new URL(blockonomicsApi + target).openConnection();
            }
            try (AutoCloseable autoCloseable = () -> connection.disconnect()) {
                if (authRequired) connection.setRequestProperty("Authorization", "Bearer " + apiInstance.getApiKey());
                connection.addRequestProperty("User-Agent", userAgent);
                connection.setRequestMethod("GET");
                StringBuilder stringBuilder = new StringBuilder();
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                    String i;
                    while ((i = reader.readLine()) != null) {
                        stringBuilder.append(i);
                    }
                }
                final String res = stringBuilder.toString();
                if(apiInstance != null) {
                    apiInstance.lastJsonResponse = res;
                }
                return jsonParser.parse(res).getAsJsonObject();
            }
        } catch (Exception e) {
            if (e instanceof IllegalStateException) return null;
            e.printStackTrace();
        }
        return null;
    }

}
