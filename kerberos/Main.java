package Homework2;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.Buffer;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;

public class Main {
    public static void main(String[] args) throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException, ParseException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String message = null;
        System.out.println("What message do you want to send to Bob?");
        try {
            message = br.readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        User Alice = new User("Alice");
        User Bob = new User("Bob");


        if(Alice.SendRequest(Bob)){
            Alice.communicate(Bob, message);
        }

       // System.out.println("Message received: " + Bob.ReceivedMessage );
    }
}
