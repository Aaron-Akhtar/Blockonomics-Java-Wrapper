package me.aaronakhtar.blockonomics_wrapper.objects.wallet_watcher;

public class MonitoredAddress {

    private double balance;
    private String tag;
    private String address;

    public MonitoredAddress(double balance, String tag, String address) {
        this.balance = balance;
        this.tag = tag;
        this.address = address;
    }

    public double getBalance() {
        return balance;
    }

    public String getTag() {
        return tag;
    }

    public String getBitcoinAddress() {
        return address;
    }

}
