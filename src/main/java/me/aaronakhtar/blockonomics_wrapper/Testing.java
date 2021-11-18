package me.aaronakhtar.blockonomics_wrapper;

import me.aaronakhtar.blockonomics_wrapper.objects.BlockonomicsCallbackSettings;
import me.aaronakhtar.blockonomics_wrapper.objects.transaction.CallbackTransaction;

public class Testing {

    // todo: remove class upon main commit.

    public static void main(String[] args) {
        final Blockonomics blockonomics = new Blockonomics("0");

        // the below thread is just to emulate new transactions.

        new Thread(){
            @Override
            public void run() {
                while(true){
                    try {
                        Thread.sleep(10000);
                        System.out.println("setting 1");
                        blockonomics.lastTransaction = new CallbackTransaction(null, null, 0, null, true);
                        Thread.sleep(10000);
                        System.out.println("setting 1");
                        blockonomics.lastTransaction = new CallbackTransaction(null, null, 0, null, true);
                        Thread.sleep(10000);
                        System.out.println("setting 1");
                        blockonomics.lastTransaction = new CallbackTransaction(null, null, 0, null, true);
                    }catch (Exception e){

                    }
                }
            }
        }.start();

        boolean listening = true;
        CallbackTransaction transaction = null;
        while(listening) {
            while ((transaction = blockonomics.listenForNewTransaction()) != null) {
                System.out.println(transaction.getNoticedDate());
            }
            listening = false;
        }
        System.out.println(1);




        try {
            Blockonomics.startCallbackServer(new BlockonomicsCallbackSettings[]{blockonomics.getCallbackSettings("/testing", "0")}, 8081);
            try(AutoCloseable autoCloseable = () -> Blockonomics.stopCallbackServer()){
                Thread.sleep(20000);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }


}
