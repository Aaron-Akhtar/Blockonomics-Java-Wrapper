package me.aaronakhtar.blockonomics_wrapper;

import me.aaronakhtar.blockonomics_wrapper.objects.BlockonomicsCallbackSettings;
import me.aaronakhtar.blockonomics_wrapper.objects.transaction.CallbackTransaction;
import me.aaronakhtar.blockonomics_wrapper.objects.transaction.TransactionStatus;

import java.util.Date;
import java.util.Locale;
import java.util.Random;

public class Testing {

    // todo: remove class upon main commit.


    // CLASS WILL BE REMOVED UPON MAIN COMMIT, OR ARCHIVED.



    public static void main(String[] args) {
        final Blockonomics blockonomics = new Blockonomics("0");
        final BlockonomicsCallbackSettings callbackSettings = blockonomics.getCallbackSettings("/testing", "0");

        blockonomics.modifyMonitoringAddress("SOME_BTC_ADDRESS", "Bitcoin Address #2");
        blockonomics.deleteMonitoringAddress("SOME_BTC_ADDRESS");


                    // In a production environment, you'd probably
                    // want to create a boolean for that keeps the
                    // callback server alive while its true, this
                    // will allow you to safely close the callback
                    // server when you want to shut it down, to keep the server
                    // alive, run it in a thread.

        try {
            Blockonomics.startCallbackServer(new BlockonomicsCallbackSettings[]{callbackSettings}, 8081);
            try (AutoCloseable autoCloseable = () -> Blockonomics.stopCallbackServer()) {
              //  Thread.sleep(20000);


                // the below thread is just to emulate new transactions.


                new Thread(){
                    @Override
                    public void run() {
                        final Random random = new Random();
                        while(true){
                            try {
                                Thread.sleep(2500);
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

                // if you want to change this, set `10`
                // to however many seconds you want it to stay open for.
                final long stopListeningTime = System.currentTimeMillis() + (100 * 1000);

                CallbackTransaction callbackTransaction = null;
                System.out.println("Listening for transactions.");
                while (System.currentTimeMillis() < stopListeningTime) {
                    final CallbackTransaction transaction = (callbackTransaction = blockonomics.listenForNewTransaction());
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


                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }.start();
                    }

                }
                System.out.println("No longer listening for transactions.");

            }
        }catch (Exception e) {
            e.printStackTrace();
        }

    }


}
