package me.aaronakhtar.blockonomics_wrapper.objects.transaction;

public class PendingTransaction {

    private int status; // 0 = unconfirmed | 1 = partially confirmed
    private String[] addr;
    private long time;
    private long value;
    private String txid;

    public int getStatus() {
        return status;
    }

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
