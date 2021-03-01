package crypto.encrypdecrypt;

import crypto.utils.AlgorithmGrabber;
import crypto.utils.Utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public class HashUtil
{

    public static String hashedPassword(String password, String salt, String hashAlgo) throws NoSuchAlgorithmException
    {
        MessageDigest messageDigest = MessageDigest.getInstance(hashAlgo);
        messageDigest.reset();
        if (salt == null)
            salt = "";
        messageDigest.update(salt.getBytes());
        byte[] hashedInputPassword = messageDigest.digest(password.getBytes());
        return Utils.bytesToHex(hashedInputPassword);
    }

    public static List<String> getAllHashAlgo()
    {
        return AlgorithmGrabber.getListOfAlgo(MessageDigest.class);
    }
}
