import crypto.encrypdecrypt.*;
import crypto.utils.Constants;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.security.*;
import java.security.cert.X509Certificate;
import java.util.Base64;

public class Test
{
    public static void main(String[] args) throws Exception
    {
        Security.addProvider(new BouncyCastleProvider());

        //        testCerts();
        //        HashUtil.getAllHashAlgo().forEach(System.out::println);

        //        testDigitalEnvelope();
        //        testingAllSymetricAlgo(originalContent);
//        asymmetrictest();

        String text = "ovo je neki test";

        var base64 = Base64.getEncoder().encode(text.getBytes(StandardCharsets.UTF_8));

        System.out.println(new String(base64));





    }

    private static void asymmetrictest() throws Exception
    {
        X509Certificate cert = CertificateUtil.loadUserCertificate("fipa");

        PublicKey publicKey = KeyPairUtil.loadUserPublicKey(cert);
        PrivateKey privateKey = KeyPairUtil.loadPrivateKey("fipa");

        String test = "asssd";


        var crypted = AsymmetricEncryption.encryptWithKey(test.getBytes(StandardCharsets.UTF_8),publicKey);
        System.out.println((new String(crypted)).length());

        var decrypted = AsymmetricEncryption.decryptWithKey(crypted, privateKey);

        System.out.println("done:");
        System.out.println(new String(decrypted));
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

}
