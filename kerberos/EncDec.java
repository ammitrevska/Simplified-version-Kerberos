package Homework2;

import javax.crypto.*;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;


//zoso gi zemam po blokooj i se gubat I guess praznite mesta
public class EncDec {

    static byte [] iv = new byte[16];

    public EncDec() {
        generateIV();
    }

    public static byte[] getSlice(byte[] array, int startIndex, int endIndex)
    {
        // Get the slice of the Array
        byte[] slicedArray = new byte[endIndex - startIndex];
        //copying array elements from the original array to the newly created sliced array
        for (int i = 0; i < slicedArray.length; i++)
        {
            slicedArray[i] = array[startIndex + i];
        }
        //returns the slice of an array
        return slicedArray;
    }

    public static byte[] encrypt(byte[] plainText, String key) {

        //kolku bloka na tekst ima
        int numOfBlocks = 0;
        if (plainText.length % 16 == 0){
            numOfBlocks = plainText.length / 16;
        }else {
            numOfBlocks = (plainText.length / 16) + 1;
        }

        int count = 0;
        byte[][] blocks = new byte[numOfBlocks][16];
        for (int i = 0; i < numOfBlocks; i++) {
            for (int j = 0; j < 16; j++) {
                if(count != plainText.length){
                    blocks[i][j] = plainText[count++];
                }
            }
        }

        byte[] nonce = new byte[13];
        byte[] counter = new byte[3];
        byte[] ctrPreload = new byte[16];

        counter = "000".getBytes();
        nonce = getSlice(iv, 0, 13);

        for (int i = 0; i < 13; i++) {
            ctrPreload[i] = nonce[i];
        }
        int iterator = 13;
        for (int i = 0; i < 3; i++) {
            ctrPreload[iterator++] = counter[i];
        }


        //cipherText
        byte[] encryptedText = new byte[plainText.length];
        int size = 0;
        for (int i = 0; i <numOfBlocks; i++) {
            //counter: 111
            for(int j = 0; j<counter.length; j++){
                counter[j]++;
            }
            iterator = 13;
            for (int j = 0; j < 3; j++) {
                ctrPreload[iterator++] = counter[j];
            }

            ctrPreload = AES.encrypt(ctrPreload, key);
            for (int j = 0; j < 16; j++) {
                if(size != plainText.length) {
                    encryptedText[size++] = (byte) (blocks[i][j] ^ ctrPreload[j]);
                }else{
                    break;
                }
            }

        }
        return encryptedText;

    }

    public static String decrypt(byte[] ya, String key) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {

        byte [] clearText = new byte[ya.length];

        int numOfBlocks = 0;
        if (ya.length % 16 == 0){
            numOfBlocks = ya.length / 16;
        }else {
            numOfBlocks = (ya.length / 16) + 1;
        }

        byte[] nonce = new byte[13];
        byte[] counter = new byte[3];
        byte[] ctrPreload = new byte[16];

        counter = "000".getBytes();
        nonce = getSlice(iv, 0, 13);

        //byte [] data = encryptedFrame.getEncryptedData();

        int count = 0;
        byte[][] blocks = new byte[numOfBlocks][16];
        for (int i = 0; i < numOfBlocks; i++) {
            for (int j = 0; j < 16; j++) {
                if(count != ya.length){
                    blocks[i][j] = ya[count++];
                }
            }
        }

        for (int i = 0; i < 13; i++) {
            ctrPreload[i] = nonce[i];
        }
        int iterator = 13;
        for (int i = 0; i < 3; i++) {
            ctrPreload[iterator++] = counter[i];
        }

        int size = 0;
        for (int i = 0; i <numOfBlocks; i++) {
            //counter: 111
            for(int j = 0; j<counter.length; j++){
                counter[j]++;
            }
            iterator = 13;
            for (int j = 0; j < 3; j++) {
                ctrPreload[iterator++] = counter[j];
            }

            ctrPreload = AES.encrypt(ctrPreload, key);
            for (int j = 0; j < 16; j++) {
                if(size != ya.length) {
                    clearText[size++] = (byte) (blocks[i][j] ^ ctrPreload[j]);
                }else{
                    break;
                }
            }

        }

        StringBuilder sb = new StringBuilder();
        for (byte b : clearText) {
            sb.append(String.format("%02X", b));
        }

        StringBuilder output = new StringBuilder();
        for (int i = 0; i < sb.length(); i+=2) {
            String str = sb.substring(i, i+2);
            output.append((char)Integer.parseInt(str, 16));
        }
        //System.out.println(output);

        return String.valueOf(output);
    }

    public void generateIV(){
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(iv);
    }
}
