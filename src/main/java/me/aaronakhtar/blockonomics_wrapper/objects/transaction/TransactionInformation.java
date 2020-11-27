package me.aaronakhtar.blockonomics_wrapper.objects.transaction;

import me.aaronakhtar.blockonomics_wrapper.objects.Vin;
import me.aaronakhtar.blockonomics_wrapper.objects.Vout;

public class TransactionInformation {

    private String status;
    private int fee;
    private Vout[] vout;
    private Vin[] vin;
    private long time;
    private int size;

    public String getStatus() {
        return status;
    }

    public int getFee() {
        return fee;
    }

    public Vout[] getVout() {
        return vout;
    }

    public Vin[] getVin() {
        return vin;
    }

    public long getTime() {
        return time;
    }

    public int getSize() {
        return size;
    }
}
