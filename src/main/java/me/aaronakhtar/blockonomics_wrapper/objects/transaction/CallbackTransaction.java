package me.aaronakhtar.blockonomics_wrapper.objects.transaction;

import me.aaronakhtar.blockonomics_wrapper.objects.BitcoinAddress;

import java.text.SimpleDateFormat;
import java.util.Date;

public class CallbackTransaction {

    public static final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

    private TransactionStatus status;
    private BitcoinAddress address;
    private long amount; // satoshi
    private String transactionId;
    private boolean rbf;
    private String noticed;

    public CallbackTransaction(TransactionStatus status, BitcoinAddress address, long amount, String transactionId, boolean rbf) {
        this.status = status;
        this.address = address;
        this.amount = amount;
        this.transactionId = transactionId;
        this.rbf = rbf;
        noticed = sdf.format(new Date());
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

    public String getNoticedDate() {
        return noticed;
    }
}
