package me.aaronakhtar.blockonomics_wrapper;

import me.aaronakhtar.blockonomics_wrapper.exceptions.BlockonomicsException;
import me.aaronakhtar.blockonomics_wrapper.objects.BitcoinAddress;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.StringJoiner;

public class BlockonomicsUtilities {

    private static final DecimalFormat decimalFormat = new DecimalFormat("#.########");

    public static double fiatToBitcoin(String fiatCurrency, double fiatAmount){
        //todo - values are ever so slightly off sometimes, requires more testing, and perhaps better math.
        return new BigDecimal(decimalFormat.format((fiatAmount * (1 / Blockonomics.getBitcoinPrice(fiatCurrency))))).doubleValue();
    }

    public static long bitcoinToSatoshi(double btcAmount){
        return new BigDecimal(btcAmount).multiply(new BigDecimal(100000000)).setScale(0, RoundingMode.UP).intValue() - 1;
    }

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
