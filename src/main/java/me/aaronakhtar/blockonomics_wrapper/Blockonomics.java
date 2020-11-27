package me.aaronakhtar.blockonomics_wrapper;

import com.google.gson.JsonObject;
import me.aaronakhtar.blockonomics_wrapper.objects.BitcoinAddress;
import me.aaronakhtar.blockonomics_wrapper.objects.BitcoinAddressHistory;
import me.aaronakhtar.blockonomics_wrapper.objects.transaction.TransactionInformation;

import java.util.HashMap;
import java.util.StringJoiner;

@SuppressWarnings("Duplicates")
public class Blockonomics {
    protected static final String blockonomicsApi = "https://www.blockonomics.co/api/";
    protected static String apiKey = null;

    public static void setApiKey(String apiKey) {
        Blockonomics.apiKey = apiKey;
    }

    /***
     * """
     * You can use this endpoint to fetch the current bitcoin to fiat price. Our prices
     * are same as that of BitPay. We support all major fiat currencies. Here is the list
     * of currency codes supported -> https://www.blockonomics.co/api/currencies
     * """ - 'https://www.blockonomics.co/views/api.html#price'
     *
     * @param currency_code the target fiat currency to check the price on.
     * @return price of bitcoin in the target fiat currency.
     */
    public static double getBitcoinPrice(String currency_code){
        final JsonObject jsonObject = Web.makeRequest("price?currency=" + currency_code.toUpperCase(), new HashMap<>(), false);
        return Double.parseDouble(jsonObject.get("price").toString());
    }

    /***
     * """
     * This will return a new address from your wallet to which the payer must send the
     * payment. This call will increment index on server, so that each time you get a
     * new address. To reset index you can use parameter reset=1. This will not increment
     * index and will keep giving last generated address. It is useful for testing purposes.
     * """ - 'https://www.blockonomics.co/views/api.html#newaddress'
     *
     * @param reset if set to true the function will return the last generated address.
     * @return Bitcoin Address in String Object.
     */
    public static String newAddress(boolean reset){
        final JsonObject jsonObject = Web.makeRequest(((reset == true) ? "new_address?reset=1" : "new_address"), "", true);
        return jsonObject.get("address").toString().replaceAll("\"", "");
    }

    /***
     * """
     * If you have multiple xpubs under same emailid, you can choose the source xpub using
     * the parameter match_account. This will match given string within your xpub to find
     * matching account.
     * """ - 'https://www.blockonomics.co/views/api.html#newaddress'
     *
     * @param account the xpub of the target account to generate address on.
     * @return BitcoinAddressAccount objecting containing the generated address and the corresponding account.
     */
    public static BitcoinAddress.BitcoinAddressAccount newAddress(String account){
        final JsonObject jsonObject = Web.makeRequest("new_address?match_account=" + account, "", true);
        return Web.gson.fromJson(jsonObject, BitcoinAddress.BitcoinAddressAccount.class);
    }

    /***
     * """
     * Returns transaction history of multiple bitcoin addresses considering them part of
     * the same wallet. For each transaction following paramters are returned:unix timestamp,
     * txid, net value transacted from wallet in satoshis and subset of address involved in
     * transaction. Transactions are sorted by latest time and a limit of 200 tx are returned.
     * Pending transactions (having less than 2 confirmations) are returned in pending dict with
     * status . Status codes: 0 - Unconfirmed, 1 - Partially Confirmed.
     * """ - 'https://www.blockonomics.co/views/api.html#history'
     *
     * @param targets Target Bitcoin Addresses.
     * @return BitcoinAddressHistory object.
     */
    public static BitcoinAddressHistory getBitcoinAddressHistory(String[] targets){
        if (targets == null || targets.length == 0) return null;
        StringJoiner stringJoiner = new StringJoiner(" ");
        for (String t : targets){
            if (t == null || t.isEmpty()) continue;
            stringJoiner.add(t);
        }
        final JsonObject jsonObject = Web.makeRequest("searchhistory", "{\"addr\":\""+stringJoiner.toString()+"\"}", true);
        return Web.gson.fromJson(jsonObject, BitcoinAddressHistory.class);
    }

    /***
     * """
     * Transaction receipt is an easy to share permalink for any bitcoin transaction/payment.
     * User addresses' in the tx are highlighted and net amount is calculated accordingly
     * """ - 'https://www.blockonomics.co/views/api.html#txreceipt'
     *
     * @param txid Target Transaction ID.
     * @param addresses Target Bitcoin Addresses.
     * @return Receipt URL.
     */
    public static String getTransactionReceiptUrl(String txid, String[] addresses){
        StringJoiner joiner = new StringJoiner(",");
        for (String s : addresses){
            joiner.add(s);
        }
        return blockonomicsApi + "tx?txid="+txid+"&addr=" + joiner.toString();
    }

    /***
     * """
     * Returns detail of input transaction id. List of transaction inputs and outputs are returned.
     * time is the received unix timestamp of transaction, value is the amount of tx input/output
     * in satoshis, fee is the transaction fees in satoshis, size is the transaction size in bytes.
     * For unconfirmed transactions an rbf attribute may be returned. 1 value of rbf means Opted-In
     * RBF signalling, 2 value means RBF due to inherited signalling (ancestor tx has RBF flag)
     * """ - 'https://www.blockonomics.co/views/api.html#limits'
     *
     * @param txid Target Transaction ID.
     * @return TransactionInformation object.
     */
    public static TransactionInformation getTransactionInformation(String txid){
        final JsonObject json = Web.makeRequest("tx_detail", new HashMap<String, String>(){{put("txid", txid);}}, false);
        return Web.gson.fromJson(json, TransactionInformation.class);
    }

    /***
     * """
     * Returns balance and unconfirmed amount(Amount waiting 2 confirmations) of multiple addresses. Balance units are in satoshis,
     * """ - 'https://www.blockonomics.co/views/api.html#balance'
     * Returns JsonObject for the returned Json String from blockonomics.
     *
     * @param targets Targets you want the balance of ('xpub' or 'address' types).
     * @return BitcoinAddress array.
     */
    public BitcoinAddress[] getBalance(String[] targets){
        if (targets == null || targets.length == 0) return null;
        //{"addr": <Whitespace seperated list of bitcoin addresses/xpubs>}
        StringJoiner stringJoiner = new StringJoiner(" ");
        for (String t : targets){
            if (t == null || t.isEmpty()) continue;
            stringJoiner.add(t);
        }
        final JsonObject jsonObject = Web.makeRequest("balance", "{\"addr\":\""+stringJoiner.toString()+"\"}", true);
        return Web.gson.fromJson(jsonObject.get("response"), BitcoinAddress[].class);
    }

}
