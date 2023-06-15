package Homework2;

import javax.crypto.*;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class User {
    public String Name;
    public byte [] key;
    private  int key_size = 128;
    public String nonce;
    public String timeStamp ;

    public String message;

    HashMap<String, byte[]> verifiedUsers = new HashMap<>();


    public User(String name) {
        Name = name;
        nonce = generateNonce();
        try {
            key = generateKey().getEncoded();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        Date date = Calendar.getInstance().getTime();
        DateFormat dateFormat = new SimpleDateFormat("hhmmss");
        timeStamp = dateFormat.format(date);
    }

    public SecretKey generateKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(key_size);
        SecretKey key = keyGenerator.generateKey();
        return key;
    }
    public String generateNonce() {
        UUID randomUUID = UUID.randomUUID();
        return randomUUID.toString().replaceAll("-", "").substring(0,12);
    }

    public boolean SendRequest(User receiver) throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException, ParseException {
        KDC kdc = new KDC(this,receiver);
        List<byte[]> response = kdc.requestCommunication(this.Name, receiver.Name, nonce);
        List<byte[]> toBeSend = new ArrayList<>();
        byte [] ya = response.get(0);
        byte [] yb = response.get(1);
        toBeSend.add(yb);
        String decYa = EncDec.decrypt(ya, String.valueOf(key));

        String [] parts = decYa.split(" ");
        byte[] sessionKey = parts[0].getBytes(StandardCharsets.UTF_8);
        boolean isVerified = VerifyNonceAndId(parts, receiver);
        if(isVerified){
            StringBuilder sb = new StringBuilder();

            //TimeStamp prae problem
            sb.append(Name + " " + timeStamp);
            String str = sb.toString();
            byte [] yab = str.getBytes(StandardCharsets.UTF_8);
            byte [] yAB = EncDec.encrypt(yab, String.valueOf(sessionKey));
            toBeSend.add(yAB);

            //todo: Verify time

            if(receiver.VerifyRequest(toBeSend)){
                verifiedUsers.put(receiver.Name, sessionKey);
                return true;
            }else {
                return false;
            }

        }else {
            return false;
        }
    }

    private boolean VerifyRequest(List<byte[]> toBeSend) throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException, ParseException {
        byte [] yB = toBeSend.get(0);
        byte [] yAB = toBeSend.get(1);

        String decYB = EncDec.decrypt(yB, String.valueOf(key));
        String decYAB = EncDec.decrypt(yAB, String.valueOf(key));

        String [] yBparts = decYB.split(" ");
        String [] yABparts = decYAB.split(" ");

        byte [] sessionKey = yBparts[0].getBytes(StandardCharsets.UTF_8);

        int lifetime = Integer.parseInt(yBparts[2]);
        Date timeS = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse(yABparts[1]);
        Date date = new Date();
        if(yBparts[1].equals(yABparts[0])){

            //TODO: Verify time
            if((timeS.getTime() - date.getTime()) <= lifetime){
                System.out.println("User is verified, messages can be send");
                verifiedUsers.put(yBparts[1],sessionKey);
                return true;
            }else {
                System.out.println("Key has expired");
                return false;
            }
        }else {
            System.out.println("IDs are not verified");
            return false;
        }
    }

    public void communicate(User receiver, String message) throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        if(verifiedUsers.containsKey(receiver.Name)){
            byte [] sessionKey = verifiedUsers.get(receiver.Name);
            byte [] encriptedMess = EncDec.encrypt(message.getBytes(StandardCharsets.UTF_8), String.valueOf(sessionKey));
            receiver.ReceivedMessage(Name,encriptedMess);
        }else {
            System.out.println("The user you are trying to communicate with it's not verified");
        }
    }

    private void ReceivedMessage(String name, byte[] encriptedMess) throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        String messageReceived = EncDec.decrypt(encriptedMess, String.valueOf(verifiedUsers.get(name)));
        //return messageReceived;
        System.out.println(messageReceived);
    }

    private boolean VerifyNonceAndId(String[] parts, User receiver) {
        int nonceP = 1, id = 3;
        if(!parts[nonceP].equals(nonce)){
            return false;
        }
        if(!parts[id].equals(receiver.Name)){
            return false;
        }
        return true;

    }

    public byte[] getKey() {
        return key;
    }
}
