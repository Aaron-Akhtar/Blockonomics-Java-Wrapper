package me.aaronakhtar.blockonomics_wrapper;

import me.aaronakhtar.blockonomics_wrapper.objects.BlockonomicsCallbackSettings;

public class Testing {

    // todo: remove class upon main commit.

    // at the moment will throw 500 err - due to no handlers.

    public static void main(String[] args) {
        final Blockonomics blockonomics = new Blockonomics("0");
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
