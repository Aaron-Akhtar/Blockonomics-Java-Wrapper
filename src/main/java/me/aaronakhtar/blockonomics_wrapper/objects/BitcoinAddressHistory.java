package me.aaronakhtar.blockonomics_wrapper.objects;

import me.aaronakhtar.blockonomics_wrapper.objects.transaction.ConfirmedTransaction;
import me.aaronakhtar.blockonomics_wrapper.objects.transaction.PendingTransaction;

public class BitcoinAddressHistory {

    private PendingTransaction[] pending;
    private ConfirmedTransaction[] history;

    public PendingTransaction[] getPendingTransactions() {
        return pending;
    }

    public ConfirmedTransaction[] getHistory() {
        return history;
    }
}
