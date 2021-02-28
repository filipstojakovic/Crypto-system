package crypto.cyptoutil;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;

public class SymmetricCtypto
{
    public static byte[] encrypt(String plainText, SecretKey secretKey, byte[] initializationVector) throws Exception
    {
        Cipher cipher = Cipher.getInstance(secretKey.getAlgorithm());
        IvParameterSpec ivParameterSpec = new IvParameterSpec(initializationVector);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParameterSpec);
        return cipher.doFinal(plainText.getBytes());
    }


    public static String do_AESDecryption(byte[] cipherText, SecretKey secretKey, byte[] initializationVector) throws Exception
    {
        Cipher cipher = Cipher.getInstance(secretKey.getAlgorithm());
        IvParameterSpec ivParameterSpec = new IvParameterSpec(initializationVector);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec);

        byte[] result = cipher.doFinal(cipherText);
        return new String(result);
    }


}
