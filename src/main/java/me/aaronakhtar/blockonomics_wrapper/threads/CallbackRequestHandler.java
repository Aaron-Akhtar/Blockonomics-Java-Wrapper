package me.aaronakhtar.blockonomics_wrapper.threads;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import me.aaronakhtar.blockonomics_wrapper.Blockonomics;
import me.aaronakhtar.blockonomics_wrapper.objects.BlockonomicsCallbackSettings;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class CallbackRequestHandler implements HttpHandler {

    private final Map<String, String> parameters = new HashMap<>();

    private Blockonomics instance;
    private BlockonomicsCallbackSettings blockonomicsCallbackSettings;

    public CallbackRequestHandler(Blockonomics instance, BlockonomicsCallbackSettings blockonomicsCallbackSettings) {
        this.instance = instance;
        this.blockonomicsCallbackSettings = blockonomicsCallbackSettings;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        if (!httpExchange.getRequestMethod().equalsIgnoreCase("get")) return;

        // converting get parameters to hashmap.
        final String[] query = httpExchange.getRequestURI().getQuery().split("&");
        if (query.length == 0) return;

        for(String q : query){
            final String[] qs = q.split("=");
            if (qs.length != 2) continue;
            parameters.put(qs[0], qs[1]);
        }

        final String
                secretKey = parameters.get("secret"),
                status = parameters.get("status"),
                address = parameters.get("addr"),
                value = parameters.get("value"),
                transactionId = parameters.get("txid"),
                rbf = parameters.get("rbf");

        if ((secretKey != null && secretKey.equals(blockonomicsCallbackSettings.getSecretKey()))
                    && status != null && address != null && value != null && transactionId != null){



            // rbf check: (rbf != null && rbf.equals("1"))  - will be used later

        }





    }
}
