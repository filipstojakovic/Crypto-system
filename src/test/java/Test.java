import crypto.MainApp;
import crypto.user.User;
import crypto.utils.CertificateUtil;
import crypto.utils.Constants;
import crypto.utils.HashUtil;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.Security;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import static crypto.MainApp.initDirs;
import static crypto.utils.CertificateUtil.X_509;

public class Test
{
    public static void main(String[] args) throws Exception
    {
        Security.addProvider(new BouncyCastleProvider());

        //        testCerts();

        //                MainApp.initDirs();
        HashUtil.getAllHashAlgo().forEach(System.out::println);

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

        System.out.println(CertificateUtil.getCommonNameFromCert(userCert));
    }

}
