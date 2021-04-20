# Blockonomics-Java-Wrapper
This is a Java Wrapper of the Blockonomics RESTful API https://www.blockonomics.co/views/api.html

Example:

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
    }

}
```
