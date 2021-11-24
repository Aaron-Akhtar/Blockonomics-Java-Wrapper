# Blockonomics-Java-Wrapper
This is a Java Wrapper of the Blockonomics RESTful API https://www.blockonomics.co/views/api.html

Example Callback URL when using the Callback Server:
```
http://%YOUR_PUBLIC_IPv4_HERE%:%YOUR_PORT_HERE%/testing?secret=%YOUR_SECRET_KEY_HERE%
```
An Example Link:
```
http://1.2.3.4:8080/api1?secret=myRandomSecretKey
```
_**Be sure to place said key in your blockonomics merchant store.**_
https://www.blockonomics.co/merchants#/stores


## Examples -

Basic implementation of the base functionality:
```java
package me.aaronakhtar.blockonomics_wrapper;

import me.aaronakhtar.blockonomics_wrapper.objects.BitcoinAddress;
import me.aaronakhtar.blockonomics_wrapper.objects.BitcoinAddressHistory;
import me.aaronakhtar.blockonomics_wrapper.objects.transaction.ConfirmedTransaction;
import me.aaronakhtar.blockonomics_wrapper.objects.transaction.PendingTransaction;

import java.util.Arrays;

public class Test {

    public static void main(String[] args) throws Exception {
        final Blockonomics blockonomics = new Blockonomics("API_KEY");
        blockonomics.modifyMonitoringAddress("SOME_BTC_ADDRESS", "Bitcoin Address #2");

        BitcoinAddressHistory bitcoinAddressHistory = blockonomics.getBitcoinAddressHistory(new BitcoinAddress[]{new BitcoinAddress("1FnQjXQc8F6jyjF8L92yLpnMhSWpw8t8jo")});
        for (ConfirmedTransaction confirmedTransaction : bitcoinAddressHistory.getHistory()){
            System.out.println(Arrays.toString(confirmedTransaction.getAddr()));
            System.out.println(confirmedTransaction.getTime());
            System.out.println(confirmedTransaction.getTxid());
            System.out.println(confirmedTransaction.getValue());
        }

        for (PendingTransaction pendingTransaction : bitcoinAddressHistory.getPendingTransactions()){
            System.out.println(Arrays.toString(pendingTransaction.getAddr()));
            System.out.println(pendingTransaction.getTime());
            System.out.println(pendingTransaction.getTxid());
            System.out.println(pendingTransaction.getValue());
            System.out.println(pendingTransaction.getStatus());
        }
        
        blockonomics.deleteMonitoringAddress("SOME_BTC_ADDRESS");
    }

}
```

Receiving LIVE Bitcoin Payment "Alerts" using the Blockonomics Callback Functionality:
**BE SURE TO SET YOUR 'CALLBACK URL' IN THE BLOCKONOMICS MERCHANT AREA**:
https://www.blockonomics.co/merchants#/stores

_There will be an easy-to-use callback URL example below_

```java

package me.aaronakhtar.blockonomics_wrapper;

import me.aaronakhtar.blockonomics_wrapper.objects.BlockonomicsCallbackSettings;
import me.aaronakhtar.blockonomics_wrapper.objects.transaction.CallbackTransaction;
import me.aaronakhtar.blockonomics_wrapper.objects.transaction.TransactionStatus;

import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.Arrays;

public class Test {

    public static void main(String[] args) throws Exception {
        final Blockonomics blockonomics = new Blockonomics("API_KEY");
        
        // set the PAGE and SECRET KEY you want as this blockonomics instance's specific callback.
        // if you have multiple apis, you can create multiple instances,
        // and multiple pages on the same HTTP server for different callbacks
        // and authentication.
        //
        final BlockonomicsCallbackSettings callbackSettings 
                                    // MAKE SURE TO PUT A `/` BEFORE THE PAGE IDENTIFIER.
                = blockonomics.getCallbackSettings("/testing", "0");
        
        
        // In a production environment, you'd probably
        // want to create a boolean for that keeps the
        // callback server alive while its true, this
        // will allow you to safely close the callback
        // server when you want to shut it down, to keep the server
        // alive, run it in a thread.
        
        try {
            Blockonomics.startCallbackServer(new BlockonomicsCallbackSettings[]{callbackSettings}, 8081);
            try (AutoCloseable autoCloseable = () -> Blockonomics.stopCallbackServer()) {



                // the below thread is just to emulate new transactions.


                new Thread(){
                    @Override
                    public void run() {
                        final Random random = new Random();
                        while(true){
                            try {
                                Thread.sleep(2500);
                                
                                // you can also do this through the Blockonomics Merchants Area: https://www.blockonomics.co/merchants#/logs
                                // once you receive an actual payment, blockonomics will send a request the same way
                                // we are sending a test one, however their request will contain valid data.
                                blockonomics.sendTestPaymentToCallback(
                                        callbackSettings,
                                        TransactionStatus.PARTIALLY_CONFIRMED,
                                        "1F1tAaz5x1HUXrCNLbtMDqcw6o5GNn4xqX",
                                        random.nextDouble(),
                                        true);
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    }
                }.start();


                // I've created an example timer system which
                // will be used to only listen for transactions for a
                // specified number of seconds, otherwise it will run in an
                // infinite loop, as it will never return null.

                // if you want to change this, set `100`
                // to however many seconds you want it to stay open for.
                final long stopListeningTime = System.currentTimeMillis() + (100 * 1000);

                CallbackTransaction callbackTransaction = null;
                System.out.println("Listening for transactions.");
                while (System.currentTimeMillis() < stopListeningTime) {
                    
                    // The listenForNewTransaction method is blocking, 
                    // but only for 5 seconds, so if a transaction doesn't 
                    // appear in 5 seconds, it returns null.
                    final CallbackTransaction transaction = 
                            (callbackTransaction = blockonomics.listenForNewTransaction());
                    if (transaction != null) {
                        new Thread() {
                            @Override
                            public void run() {
                                try {
                                    System.out.println("\n---------------" + transaction.getNoticedDate() + "  (NOW:" + CallbackTransaction.sdf.format(new Date()) + ")---------------");
                                    System.out.println("Address: " + transaction.getAddress().getAddress());
                                    System.out.println("Transaction ID: " + transaction.getTransactionId());
                                    System.out.println("Bitcoin Amount (Satoshis): " + transaction.getAmount());
                                    System.out.println("Confirmation Status: " + transaction.getStatus().name().toUpperCase(Locale.ROOT) + " ('" + transaction.getStatus().getI() + "')");
                                    // if you want to create invoicing, so that you  can track payments etc, you would do those checks here,
                                    // however I will persist you use threading here so that you don't miss any transactions.

                                    
                                    
                                    // EXAMPLE CALLBACK URL:
                                    // http://%YOUR_PUBLIC_IPv4_HERE%:%YOUR_PORT_HERE%/testing?secret=%YOUR_SECRET_KEY_HERE%
                                    
                                    

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }.start();
                    }

                }
                System.out.println("No longer listening for transactions.");                
                
                
            }
        } catch (Exception e) {
            e.printStackTrace();
        }      
        
        


    }
}

```
