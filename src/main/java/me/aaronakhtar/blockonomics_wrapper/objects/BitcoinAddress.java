package me.aaronakhtar.blockonomics_wrapper.objects;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.aaronakhtar.blockonomics_wrapper.Blockonomics;
import me.aaronakhtar.blockonomics_wrapper.BlockonomicsUtilities;
import me.aaronakhtar.blockonomics_wrapper.Web;
import me.aaronakhtar.blockonomics_wrapper.exceptions.BlockonomicsException;

public class BitcoinAddress {

    private String addr;
    private int confirmed;
    private int unconfirmed;

    public BitcoinAddress(String addr){
        this.addr = addr;
    }

    /***
     * This function will update the current object with the
     * latest Confirmed and Unconfirmed amounts at the time
     * of execution.
     *
     * @return Current BitcoinAddress object, with updated variables.
     */
    public BitcoinAddress updateObject(Blockonomics apiInstance) throws BlockonomicsException {
        final JsonArray jsonArray = Web.makeRequest("balance", BlockonomicsUtilities.addressArrayToJson(new BitcoinAddress[]{this}, " "), true, apiInstance)
                .get("response").getAsJsonArray();
        final JsonObject response = jsonArray.get(0).getAsJsonObject();
        this.confirmed = Integer.parseInt(response.get("confirmed").toString());
        this.unconfirmed = Integer.parseInt(response.get("unconfirmed").toString());
        return this;
    }

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
