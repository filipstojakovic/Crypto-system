package crypto.encrypdecrypt;

import crypto.utils.Constants;
import crypto.utils.Utils;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class SymmetricEncryption
{
    public static final String PADDING = "#";
    public static final int KEY_LENGHT = 16;
    public static final int KEY_SIZE = 256;
    public static final String AES = "AES";

    private String symmetricAlgo;

    public SymmetricEncryption(String symmetricAlgo)
    {
        this.symmetricAlgo = symmetricAlgo;
    }

    public byte[] encrypt(String key, byte[] data) throws Exception
    {
        return encrypt(key, data, this.symmetricAlgo);
    }

    public byte[] encrypt(String key, byte[] data, String symmetricAlgo) throws Exception
    {
        SecretKey secretKey = generateSecretKey(key, symmetricAlgo);
        Cipher cipher = Cipher.getInstance(symmetricAlgo);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        return cipher.doFinal(data);
    }

    public byte[] encrypt(SecretKey key, byte[] data, String symmetricAlgo) throws Exception
    {
        Cipher cipher = Cipher.getInstance(symmetricAlgo);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(data);
    }


    public byte[] encrypt(String key, File datafile) throws Exception
    {
        return encrypt(key, Files.readAllBytes(datafile.toPath()));
    }


    public byte[] encrypt(String key, String data) throws Exception
    {
        return encrypt(key, data.getBytes(StandardCharsets.UTF_8));
    }

    public byte[] decrypt(String key, byte[] cryptedData, String symmetricAlgo) throws Exception
    {
        SecretKey secretKey = generateSecretKey(key, symmetricAlgo);
        Cipher cipher = Cipher.getInstance(symmetricAlgo, Constants.BC_PROVIDER);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        return cipher.doFinal(cryptedData);
    }

    public byte[] decrypt(SecretKey key, byte[] cryptedData, String symmetricAlgo) throws Exception
    {
        Cipher cipher = Cipher.getInstance(symmetricAlgo, Constants.BC_PROVIDER);
        cipher.init(Cipher.DECRYPT_MODE, key);
        return cipher.doFinal(cryptedData);
    }

    public byte[] decrypt(String key, byte[] cryptedData) throws Exception
    {
        return decrypt(key, cryptedData, this.symmetricAlgo);
    }

    public String decrtpyToString(String key, byte[] cryptedData) throws Exception
    {
        var decrypt = decrypt(key, cryptedData);
        return new String(decrypt);
    }

    public String decrtpyToString(String key, File file) throws Exception
    {
        var cyptedData = Files.readAllBytes(file.toPath());
        return decrtpyToString(key, cyptedData);
    }

    public static String generateRandomAESkey()
    {
        Random random = new SecureRandom();
        byte[] key = new byte[KEY_LENGHT];
        random.nextBytes(key);
        return Utils.bytesToHex(key);
    }

    public SecretKey generateSecretKey(String key, String symmetricAlgo)
    {
        //        if (key == null)
        //            key = "";
        StringBuilder password = new StringBuilder(key);

        while (password.length() < KEY_LENGHT)
            password.append(PADDING);

        if (password.length() > 16)
            password = new StringBuilder(password.substring(0, KEY_LENGHT));

        return new SecretKeySpec(password.toString().getBytes(), symmetricAlgo);
    }

    public String getSymmetricAlgo()
    {
        return symmetricAlgo;
    }

    public void setSymmetricAlgo(String symmetricAlgo)
    {
        this.symmetricAlgo = symmetricAlgo;
    }

    public static List<String> getAllSymmetricKeyAlgorithms()
    {
        return Arrays.asList("AES", "ARCFOUR", "DESede", "Blowfish", "RC2");
    }

}
