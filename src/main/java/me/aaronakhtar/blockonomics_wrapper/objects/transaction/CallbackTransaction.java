package me.aaronakhtar.blockonomics_wrapper.objects.transaction;

import me.aaronakhtar.blockonomics_wrapper.objects.BitcoinAddress;

public class CallbackTransaction {

    // status enum is inner due to the fact
    // it likely won't be used elsewhere.


    private TransactionStatus status;
    private BitcoinAddress address;
    private long amount; // satoshi
    private String transactionId;
    private boolean rbf;

    public CallbackTransaction(TransactionStatus status, BitcoinAddress address, long amount, String transactionId, boolean rbf) {
        this.status = status;
        this.address = address;
        this.amount = amount;
        this.transactionId = transactionId;
        this.rbf = rbf;
    }

    public TransactionStatus getStatus() {
        return status;
    }

    public BitcoinAddress getAddress() {
        return address;
    }

    public long getAmount() {
        return amount;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public boolean isRbf() {
        return rbf;
    }
}
