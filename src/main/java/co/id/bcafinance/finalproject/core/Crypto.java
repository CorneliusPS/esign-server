package co.id.bcafinance.finalproject.core;

import org.bouncycastle.crypto.BufferedBlockCipher;
import org.bouncycastle.crypto.engines.AESLightEngine;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.util.encoders.Hex;

public class Crypto {

//    private static final String defaultKey = "aafd12f438cae52538b479e3199ddec2f06cb58faafd12f6";
//    private static final String defaultKey = "7117b5ce75785cf379a972232a6b4562f2769ca2fc98d5c3e2508d792955fc10";
    private static final String defaultKey = "1e3c7d624fe822a421f208968c83a1fe9992102ff13ddcf02dd43e09ce16d29c";
    public static String performEncrypt(String keyText, String plainText) {
        try{
            byte[] key = Hex.decode(keyText.getBytes());
            byte[] ptBytes = plainText.getBytes();
            BufferedBlockCipher cipher = new PaddedBufferedBlockCipher(new CBCBlockCipher(new AESLightEngine()));
            cipher.init(true, new KeyParameter(key));
            byte[] rv = new byte[cipher.getOutputSize(ptBytes.length)];
            int oLen = cipher.processBytes(ptBytes, 0, ptBytes.length, rv, 0);
            cipher.doFinal(rv, oLen);
            return new String(Hex.encode(rv));
        } catch(Exception e) {
            return "Error";
        }
    }

    public static String performEncrypt(String cryptoText) {
        return performEncrypt(defaultKey, cryptoText);
    }

    public static String performDecrypt(String keyText, String cryptoText) {
        try {
            byte[] key = Hex.decode(keyText.getBytes());
            byte[] cipherText = Hex.decode(cryptoText.getBytes());
            BufferedBlockCipher cipher = new PaddedBufferedBlockCipher(new CBCBlockCipher(new AESLightEngine()));
            cipher.init(false, new KeyParameter(key));
            byte[] rv = new byte[cipher.getOutputSize(cipherText.length)];
            int oLen = cipher.processBytes(cipherText, 0, cipherText.length, rv, 0);
            cipher.doFinal(rv, oLen);
            return new String(rv).trim();
        } catch(Exception e) {
            return "Error";
        }
    }

    public static String performDecrypt(String cryptoText) {
        return performDecrypt(defaultKey, cryptoText);
    }



    public static void main(String[] args) {

        String strToEncrypt = "ZQji=\\T+rEc\\\"*)Q";//put text to encrypt in here
        String encryptionResult = new Crypto().performEncrypt(strToEncrypt);
        System.out.println("Encryption Result : "+encryptionResult);
        // KEY -> aafd12f438cae52538b479e3199ddec2f06cb58faafd12f6
//        System.out.println("KEY PENTING BANGET !!! "+defaultKey);;
        //ENCRYPT -> 528b01943544a1dcef7a692a0628e46b ->

        //ENCRYPT -> bdcc9507be280e3e5489a5dce01b42ea
        //KEY -> aafd12f438cae52538b479e2089ddec2f06cb58faafd12f6

        String strToDecrypt = "5eb279dd4ab4b51e4c8a70d8728ddb20437eaaa1dd2321d98de012117e1a45bb6646884f997e7611fde6156edeabf1f820d0d3514bb5fa3e66fefc1f55fa554e5de92531065ef941b3152c2451992879b05dd52d51ebf35a005bb074cfa00f44a6e0f9ecb85ae2307757855987806268882d765857d30672c9aa3e0c0f39efa78d93bd70c7226eff5c930461ea4f439122929b3bca3ef8c6730a8f8ef75b8fe2af44366b5e752c1e62d497cc78c5e50ba50a22487473a80a4d30eb4519b769bd3d3d6a6a65f228aa83b12b568d6818c58960f9959abcf79917f8b247687514f43dd5300339c06bb134f06febccbb201cb4e6f1409e0013b272fe9a3121cc3334";//put text to decrypt in here
        String decriptionResult = new Crypto().performDecrypt(strToDecrypt);
        System.out.println("Decryption Result : "+decriptionResult);
        System.out.println("Untuk VIVO X5 DEFAULT AJA BELUM DI SET ".length());
        //585107f50fa1e0649bd32da95d5cf41c
        //585107f50fa1e0649bd32da95d5cf41c
        //585107f50fa1e0649bd32da95d5cf41c
    }
}