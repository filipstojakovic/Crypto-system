package crypto.encrypdecrypt.testing;

import javax.crypto.*;
import javax.crypto.spec.PBEParameterSpec;
import java.io.*;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class FileEncrypterDecrypter
{
    public static final int ITERATION_COUNT = 100;
    private static final byte[] salt = {
            (byte) 0x43, (byte) 0x76, (byte) 0x95, (byte) 0xc7,
            (byte) 0x5b, (byte) 0xd7, (byte) 0x45, (byte) 0x17
    };

    private SecretKey secretKey;
    private Cipher cipher;
    private PBEParameterSpec pbeParamSpec;

    public FileEncrypterDecrypter(SecretKey secretKey, String cipher) throws NoSuchPaddingException, NoSuchAlgorithmException
    {
        this.secretKey = secretKey;
        this.cipher = Cipher.getInstance(cipher);
        pbeParamSpec = new PBEParameterSpec(salt, ITERATION_COUNT);

    }

    public void encrypt(String content, String fileName) throws InvalidKeyException, IOException, InvalidAlgorithmParameterException
    {
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, pbeParamSpec);

        try (
                FileOutputStream fileOut = new FileOutputStream(fileName);
                CipherOutputStream cipherOut = new CipherOutputStream(fileOut, cipher)
        )
        {
            cipherOut.write(content.getBytes());
        }

    }

    public String decrypt(String fileName) throws InvalidAlgorithmParameterException, InvalidKeyException, IOException
    {

        String content;

        try (FileInputStream fileIn = new FileInputStream(fileName))
        {
            cipher.init(Cipher.DECRYPT_MODE, secretKey, pbeParamSpec);

            try (
                    CipherInputStream cipherIn = new CipherInputStream(fileIn, cipher);
                    InputStreamReader inputReader = new InputStreamReader(cipherIn);
                    BufferedReader reader = new BufferedReader(inputReader)
            )
            {

                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null)
                {
                    sb.append(line);
                }
                content = sb.toString();
            }

        }
        return content;
    }
}