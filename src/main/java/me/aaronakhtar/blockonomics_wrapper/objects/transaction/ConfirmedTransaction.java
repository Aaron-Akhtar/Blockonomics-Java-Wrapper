package me.aaronakhtar.blockonomics_wrapper.objects.transaction;

public class ConfirmedTransaction {

    private String[] addr;
    private long time;
    private long value;
    private String txid;

    public String[] getAddr() {
        return addr;
    }

    public long getTime() {
        return time;
    }

    public long getValue() {
        return value;
    }

    public String getTxid() {
        return txid;
    }
}
