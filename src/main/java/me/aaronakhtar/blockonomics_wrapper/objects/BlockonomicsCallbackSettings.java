package me.aaronakhtar.blockonomics_wrapper.objects;

import me.aaronakhtar.blockonomics_wrapper.Blockonomics;

public class BlockonomicsCallbackSettings {

    private Blockonomics blockonomicsInstance;
    private String context;
    private String secretKey;

    public BlockonomicsCallbackSettings(Blockonomics blockonomicsInstance, String context, String secretKey) {
        this.blockonomicsInstance = blockonomicsInstance;
        this.context = context;
        this.secretKey = secretKey;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public Blockonomics getBlockonomicsInstance() {
        return blockonomicsInstance;
    }

    public String getContext() {
        return context;
    }

    public String getSecretKey() {
        return secretKey;
    }
}
