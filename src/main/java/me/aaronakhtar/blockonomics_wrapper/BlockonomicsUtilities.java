package me.aaronakhtar.blockonomics_wrapper;

import me.aaronakhtar.blockonomics_wrapper.exceptions.BlockonomicsException;
import me.aaronakhtar.blockonomics_wrapper.objects.BitcoinAddress;

import java.util.StringJoiner;

public class BlockonomicsUtilities {

    public static String addressArrayToJson(BitcoinAddress[] bitcoinAddresses, String delimiter) throws BlockonomicsException {
        if (bitcoinAddresses == null || bitcoinAddresses.length == 0){
            throw new BlockonomicsException("The BitcoinAddress array is null or empty.");
        }
        final StringJoiner stringJoiner = new StringJoiner(delimiter);
        for (BitcoinAddress bitcoinAddress : bitcoinAddresses){
            if (bitcoinAddress == null || bitcoinAddress.getAddress() == null || bitcoinAddress.getAddress().isEmpty()) continue;
            stringJoiner.add(bitcoinAddress.getAddress());
        }
        return "{\"addr\":\""+stringJoiner.toString()+"\"}";
    }

}
