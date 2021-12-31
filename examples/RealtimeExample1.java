import me.aaronakhtar.blockonomics_wrapper.objects.BlockonomicsCallbackSettings;
import me.aaronakhtar.blockonomics_wrapper.objects.transaction.CallbackTransaction;
import me.aaronakhtar.blockonomics_wrapper.objects.transaction.TransactionStatus;

import java.util.Date;
import java.util.Locale;
import java.util.Random;

public class Testing {

    public static void main(String[] args) {
        final Blockonomics blockonomics = new Blockonomics("");
        final BlockonomicsCallbackSettings callbackSettings = blockonomics.getCallbackSettings("/testing", "0");

        try {
            Blockonomics.startCallbackServer(new BlockonomicsCallbackSettings[]{callbackSettings}, 8081);
            try (AutoCloseable autoCloseable = () -> Blockonomics.stopCallbackServer()) {
                final long stopListeningTime = System.currentTimeMillis() + (60 * 1000);

                System.out.println("Listening for Bitcoin Transactions for 100 seconds:");
                System.out.println(blockonomics.getCallbackURL(callbackSettings));
                System.out.println();

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

                                    System.out.println();

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
