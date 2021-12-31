package me.aaronakhtar.blockonomics_wrapper;

import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpServer;
import me.aaronakhtar.blockonomics_wrapper.exceptions.BlockonomicsException;
import me.aaronakhtar.blockonomics_wrapper.objects.BitcoinAddress;
import me.aaronakhtar.blockonomics_wrapper.objects.BitcoinAddressHistory;
import me.aaronakhtar.blockonomics_wrapper.objects.BlockonomicsCallbackSettings;
import me.aaronakhtar.blockonomics_wrapper.objects.transaction.CallbackTransaction;
import me.aaronakhtar.blockonomics_wrapper.objects.transaction.ConfirmedTransaction;
import me.aaronakhtar.blockonomics_wrapper.objects.transaction.TransactionInformation;
import me.aaronakhtar.blockonomics_wrapper.objects.transaction.TransactionStatus;
import me.aaronakhtar.blockonomics_wrapper.objects.wallet_watcher.MonitoredAddress;
import me.aaronakhtar.blockonomics_wrapper.threads.CallbackRequestHandler;
import sun.security.ssl.HandshakeOutStream;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

public class Blockonomics {
    private static boolean isCallbackServerOnline = false;
    private static HttpServer callbackServer = null;
    protected String lastJsonResponse = "";
    private String apiKey;
    public Blockonomics(String apiKey) {
        this.apiKey = apiKey;
    }

    public URL getCallbackURL() throws BlockonomicsException, MalformedURLException {
        if (!isCallbackServerOnline) throw new BlockonomicsException("Please execute 'startCallbackServer()' method before attempting to fetch the callback URL.");
        final InetSocketAddress inetSocketAddress = callbackServer.getAddress();
        String hostAddress = inetSocketAddress.getAddress().getHostAddress();
        if (hostAddress.split("\\.").length != 4 || hostAddress.contains(":")) hostAddress = "127.0.0.1";
        return new URL("http://" + hostAddress + ":" + inetSocketAddress.getPort());
    }

    public String getCallbackURL(BlockonomicsCallbackSettings callbackSettings) throws MalformedURLException, BlockonomicsException{
        return this.getCallbackURL().toString() + callbackSettings.getContext() + "?secret=" + callbackSettings.getSecretKey();
    }

    public BlockonomicsCallbackSettings getCallbackSettings(String context, String secretKey){
        return new BlockonomicsCallbackSettings(this, context, secretKey);
    }

    public void sendTestPaymentToCallback(BlockonomicsCallbackSettings callbackSettings, TransactionStatus paymentStatus, String bitcoinAddress, double bitcoinAmount, boolean replaceByFee){
        try{
            final URL url = new URL(
                    getCallbackURL().toString() + callbackSettings.getContext() +
                            "?status="+paymentStatus.getI()+
                            "&addr=" + bitcoinAddress +
                            "&value="+BlockonomicsUtilities.bitcoinToSatoshi(bitcoinAmount)+
                            "&txid=THIS-IS-NOT-A-REAL-PAYMENT" +
                            "&rbf=" + ((replaceByFee) ? 1 : 0) +
                            "&secret=" + callbackSettings.getSecretKey());

            final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            try(AutoCloseable autoCloseable = () -> connection.disconnect()){
                connection.setConnectTimeout(10000);
                connection.addRequestProperty("User-Agent", Web.userAgent);
                connection.setRequestMethod("GET");
                connection.getResponseCode(); // wait for request to complete.
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static synchronized void startCallbackServer(BlockonomicsCallbackSettings[] blockonomicsCallbackSettings, int port) throws IOException, BlockonomicsException {
        if (isCallbackServerOnline){
            throw new BlockonomicsException("Please execute 'stopCallbackServer()' method before attempting to start a new callback server.");
        }

        try {
            final HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
            final BlockonomicsCallbackSettings[] callbackSettings = blockonomicsCallbackSettings;
            for (BlockonomicsCallbackSettings bcs : callbackSettings){
                server.createContext(bcs.getContext(), new CallbackRequestHandler(bcs.getBlockonomicsInstance(), bcs));
            }
            server.start();
            callbackServer = server;
            isCallbackServerOnline = true;

        }catch (IOException e){
            e.printStackTrace();
        }

    }

    public static synchronized void stopCallbackServer() throws BlockonomicsException {
        if (!isCallbackServerOnline){
            throw new BlockonomicsException("Please execute 'startCallbackServer()' method before attempting to stop the running callback server.");
        }
        callbackServer.stop(0);
        isCallbackServerOnline = false;
    }

    public volatile CallbackTransaction lastTransaction = null;
    public volatile CallbackTransaction lastListenedTransaction = null;
    // an alternative method to handling transactions like this could be a LOCAL SOCKET to allow for better communication between threads, however this felt like a less intrusive way of doing things.
    public CallbackTransaction listenForNewTransaction(){
                                                                // if nothing happens in 5 seconds it returns null.
        final long stopWaitingTime = System.currentTimeMillis() + (5 * 1000);
        while(lastTransaction == null ||
                (lastListenedTransaction != null && lastListenedTransaction.getNoticedDate().equalsIgnoreCase(lastTransaction.getNoticedDate()) && lastListenedTransaction.getAddress().getAddress().equals(lastTransaction.getAddress().getAddress()))){

            try {
                if (System.currentTimeMillis() > stopWaitingTime) return null;
                Thread.sleep(20);
            }catch (InterruptedException e){

            }

        }
        lastListenedTransaction = lastTransaction;
        return lastTransaction;
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
        final JsonObject jsonObject = Web.makeRequest("price?currency=" + currency_code.toUpperCase(), new HashMap<>(), false, null);
        return Double.parseDouble(jsonObject.get("price").toString());
    }
    /***
     * <P>WALLET WATCHER FUNCTION</P>
     *
     * <P>Use this function to insert a new bitcoin address to monitor or
     * modify one that already exists.</P>
     *
     * @param address target Bitcoin Address to modify/create.
     * @param tag new tag for the target Bitcoin Address.
     * @return boolean.
     */
    public boolean modifyMonitoringAddress(String address, String tag){
        Web.makeRequest("address", "{\"addr\":\""+address+"\", \"tag\":\""+tag+"\"}", true, this);
        return true;
    }

    /***
     * <P>WALLET WATCHER FUNCTION</P>
     *
     * <P>Use this function to delete an existing Bitcoin Address from your
     * wallet watcher.</P>
     *
     * @param address target Bitcoin Address to delete.
     * @return boolean.
     */
    public boolean deleteMonitoringAddress(String address){
        Web.makeRequest("delete_address", "{\"addr\":\""+address+"\"}", true, this);
        return true;
    }

    /***
     * <P>WALLET WATCHER FUNCTION</P>
     *
     * <P>Use this function to get all monitoring addresses.</P>
     *
     * @return MonitoredAddress object array.
     */
    public MonitoredAddress[] getMonitoringAddresses(){
        try {
            Web.makeRequest("address", new HashMap<>(), true, this);
        }catch (IllegalStateException e){}
        return Web.gson.fromJson(lastJsonResponse, MonitoredAddress[].class);
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
    public String newAddress(boolean reset){
        final JsonObject jsonObject = Web.makeRequest(((reset) ? "new_address?reset=1" : "new_address"), "", true, this);
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
    public BitcoinAddress.BitcoinAddressAccount newAddress(String account){
        final JsonObject jsonObject = Web.makeRequest("new_address?match_account=" + account, "", true, this);
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
     * @param targetBitcoinAddresses Target Bitcoin Addresses.
     * @return BitcoinAddressHistory object.
     */
    public BitcoinAddressHistory getBitcoinAddressHistory(BitcoinAddress[] targetBitcoinAddresses) throws BlockonomicsException{
        final JsonObject jsonObject = Web.makeRequest("searchhistory", BlockonomicsUtilities.addressArrayToJson(targetBitcoinAddresses, " "), true, this);
        return Web.gson.fromJson(jsonObject, BitcoinAddressHistory.class);
    }

    /***
     * """
     * Transaction receipt is an easy to share permalink for any bitcoin transaction/payment.
     * User addresses' in the tx are highlighted and net amount is calculated accordingly
     * """ - 'https://www.blockonomics.co/views/api.html#txreceipt'
     *
     * @param txid Target Transaction ID.
     * @param targetBitcoinAddresses Target Bitcoin Addresses.
     * @return Receipt URL.
     */
    public static String getTransactionReceiptUrl(String txid, BitcoinAddress[] targetBitcoinAddresses) throws BlockonomicsException {
        return Web.blockonomicsApi + "tx?txid="+txid+"&addr=" + BlockonomicsUtilities.addressArrayToJson(targetBitcoinAddresses, ",");
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
        final JsonObject json = Web.makeRequest("tx_detail", new HashMap<String, String>(){{put("txid", txid);}}, false, null);
        return Web.gson.fromJson(json, TransactionInformation.class);
    }

    /***
     * """
     * Returns balance and unconfirmed amount(Amount waiting 2 confirmations) of multiple addresses. Balance units are in satoshis,
     * """ - 'https://www.blockonomics.co/views/api.html#balance'
     * Returns JsonObject for the returned Json String from blockonomics.
     *
     * @param targetBitcoinAddresses Targets you want the balance of ('xpub' or 'address' types).
     * @return BitcoinAddress array.
     */
    public BitcoinAddress[] getBalance(BitcoinAddress[] targetBitcoinAddresses) throws BlockonomicsException {
        final JsonObject jsonObject = Web.makeRequest("balance", BlockonomicsUtilities.addressArrayToJson(targetBitcoinAddresses, " "), true, this);
        return Web.gson.fromJson(jsonObject.get("response"), BitcoinAddress[].class);
    }

    public static boolean isIsCallbackServerOnline() {
        return isCallbackServerOnline;
    }

    public static HttpServer getCallbackServer() {
        return callbackServer;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getApiKey() {
        return apiKey;
    }

}
