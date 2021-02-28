import crypto.cyptoutil.DigitalEnvelope;
import crypto.cyptoutil.KeyPairUtil;
import crypto.user.User;
import crypto.cyptoutil.CertificateUtil;
import crypto.utils.Constants;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import static crypto.MainApp.initDirs;
import static crypto.cyptoutil.CertificateUtil.X_509;

public class Test
{
    public static void main(String[] args) throws Exception
    {
        Security.addProvider(new BouncyCastleProvider());
        //        testCerts();
        //        HashUtil.getAllHashAlgo().forEach(System.out::println);

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


    private static void testCerts() throws CertificateException, IOException
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
