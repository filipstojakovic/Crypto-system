import crypto.encrypdecrypt.CertificateUtil;
import crypto.encrypdecrypt.DigitalEnvelope;
import crypto.encrypdecrypt.KeyPairUtil;
import crypto.encrypdecrypt.testing.FileEncrypterDecrypter;
import crypto.encrypdecrypt.SymmetricEncryption;
import crypto.exception.NoCertificateException;
import crypto.user.User;
import crypto.utils.Constants;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import static crypto.MainApp.initDirs;
import static crypto.encrypdecrypt.CertificateUtil.X_509;

public class Test
{
    public static void main(String[] args) throws Exception
    {
        //        testCerts();
        //        HashUtil.getAllHashAlgo().forEach(System.out::println);

        //        testDigitalEnvelope();
        //        testingAllSymetricAlgo(originalContent);
        final Path path = Paths.get(Constants.USER_DIR + "fipa");
        System.out.println(path);
        try (final WatchService watchService = FileSystems.getDefault().newWatchService())
        {
            final WatchKey watchKey = path.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);
            while (true)
            {
                final WatchKey wk = watchService.take();
                for (WatchEvent<?> event : wk.pollEvents())
                {
                    //we only register "ENTRY_MODIFY" so the context is always a Path.
                    final Path changed = (Path) event.context();
                    System.out.println(changed);
                    if (changed.endsWith("sad.txt"))
                    {
                        System.out.println("My file has changed");
                    }
                }
                // reset the key
                boolean valid = wk.reset();
                if (!valid)
                {
                    System.out.println("Key has been unregisterede");
                }
            }
        }


    }

    private static void testingAllSymetricAlgo()
    {
        String originalContent = "foobar";
        SymmetricEncryption.getAllSymmetricKeyAlgorithms().forEach(algo ->
        {
            try
            {
                String pass = "123";
                PBEKeySpec keySpec = new PBEKeySpec(pass.toCharArray());
                SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBEWithMD5AndDES"); //PBEWithMD5AndDES
                SecretKey secretKey = keyFactory.generateSecret(keySpec);

                //        SecretKey secretKey = KeyGenerator.getInstance("AES").generateKey();
                //        SecretKey secretKey = new SecretKeySpec("0123456789012345".getBytes(StandardCharsets.UTF_8), "AES");


                FileEncrypterDecrypter fileEncrypterDecrypter = new FileEncrypterDecrypter(secretKey, algo); //PBEWithMD5AndDES
                fileEncrypterDecrypter.encrypt(originalContent, Constants.RESOURCES_DIR + File.separator + "test.txt");

                String decryptedContent = fileEncrypterDecrypter.decrypt(Constants.RESOURCES_DIR + File.separator + "test.txt");
                System.out.println(decryptedContent);

                Thread.sleep(1000);

            } catch (Exception ex)
            {
                ex.printStackTrace();
            }

        });
    }

    private static void testDigitalEnvelope() throws GeneralSecurityException, CMSException, IOException
    {
        var text = "ovo je test string".getBytes(StandardCharsets.UTF_8);
        System.out.println(new String(text));
        System.out.println();
        System.out.println();
        X509Certificate rootCA = CertificateUtil.loadRootCertificate();
        X509Certificate userCert = CertificateUtil.loadUserCertificate("fipa");

        //TODO: sta je sa userCert
        var enveloped = DigitalEnvelope.createKeyTransEnvelope(rootCA, text);

        String env = new String(enveloped);
        System.out.println("enveloped: ");
        System.out.println(env);
        System.out.println();
        System.out.println();

        PrivateKey privatekey = KeyPairUtil.loadPrivateKey(Paths.get(Constants.ROOT_CA_PRIVATE_KEY_FILE));

        var extracted = DigitalEnvelope.extractKeyTransEnvelope(privatekey, rootCA, enveloped);
        System.out.println("extracted:");
        System.out.println(new String(extracted));
    }


    private static void testCerts() throws CertificateException, IOException, NoCertificateException
    {
        initDirs();
        Security.addProvider(new BouncyCastleProvider());
        CertificateUtil.initRootCertificate(); // make rootCA if needed

        User user = new User();
        user.setUsername("coki");
        user.setCommonName("Dragan Jovic");
        CertificateUtil.generateSignedUserCert(user.getCommonName(), user.getUsername()); // generate userCert signed with RootCA

        CertificateFactory factory = CertificateFactory.getInstance(X_509);
        X509Certificate userCert = (X509Certificate) factory
                .generateCertificate(new FileInputStream(Constants.CERT_DIR + "coki" + CertificateUtil.CERT_EXTENSION));

    }

}
