package Homework2;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class KDC {

    HashMap<String, User> Korisnici = new HashMap<>();

    public KDC(User user, User receiver) {
        Korisnici.put(user.Name, user);
        Korisnici.put(receiver.Name, receiver);
    }

    public List<byte[]> requestCommunication(String requestSender, String receiver, String nonce){
        List<byte []> keys = new ArrayList<>();
        if(Korisnici.containsKey(requestSender) && Korisnici.containsKey(receiver)){
            String sessionKey = String.valueOf(generateSessionKey());
            String lifetime = String.valueOf(generateLifeTime());
            StringBuilder sb = new StringBuilder();

            sb.append(sessionKey +" "+ nonce+ " " + lifetime+" " + receiver);

            String str = sb.toString();
            byte [] toEncrypt1 = str.getBytes(StandardCharsets.UTF_8);
            byte[] ya, yb;

            ya = EncDec.encrypt(toEncrypt1, String.valueOf(Korisnici.get(requestSender).key));


            StringBuilder b = new StringBuilder();
            b.append(sessionKey+" "+requestSender+" "+lifetime);
            String sB = b.toString();
            byte [] toEnc = sB.getBytes(StandardCharsets.UTF_8);

            yb = EncDec.encrypt(toEnc, String.valueOf(Korisnici.get(receiver).key));

            keys.add(ya);
            keys.add(yb);
        }else {
            System.out.println("They are not legitimate users");
        }
        return keys;
    }

    public char[] generateSessionKey(){
        String abc = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random rd = new Random();
        char [] key = new char[16];
        for (int i = 0; i < 16; i++) {
         key[i] = abc.charAt(rd.nextInt(abc.length()));
        }
        return key;
    }

    public int generateLifeTime(){
        //hardcoded kje bide 2 minuti
        return 120;
    }
}
