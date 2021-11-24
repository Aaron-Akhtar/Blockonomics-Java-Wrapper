package me.aaronakhtar.blockonomics_wrapper.objects.transaction;

public enum TransactionStatus {
    UNCONFIRMED(0),
    PARTIALLY_CONFIRMED(1),
    CONFIRMED(2);

    int i;

    TransactionStatus(int i) {
        this.i = i;
    }

    public int getI() {
        return i;
    }

}
