package me.aaronakhtar.blockonomics_wrapper.objects;

public class BitcoinAddress {

    private String addr;
    private int confirmed;
    private int unconfirmed;

    public String getAddress() {
        return addr;
    }

    public int getConfirmed() {
        return confirmed;
    }

    public int getUnconfirmed() {
        return unconfirmed;
    }

    public class BitcoinAddressAccount {
        private String account;
        private String address;

        public BitcoinAddressAccount(String account, String address) {
            this.account = account;
            this.address = address;
        }

        public String getAccount() {
            return account;
        }

        public String getAddress() {
            return address;
        }
    }

}
