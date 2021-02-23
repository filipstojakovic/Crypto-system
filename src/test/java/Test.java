import crypto.user.User;
import crypto.utils.CertificateUtil;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.io.File;
import java.io.FileInputStream;
import java.security.Security;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import static crypto.utils.CertificateUtil.X_509;

public class Test
{
    public static void main(String[] args) throws Exception
    {
        Security.addProvider(new BouncyCastleProvider());
        CertificateUtil.initRootCertificate(); // make rootCA if needed

        User user = new User();
        user.setUsername("nindza");
        user.setCommonName("black nindza");
        CertificateUtil.generateUserCert(user); // generate with RootCA

        CertificateFactory factory = CertificateFactory.getInstance(X_509);
        X509Certificate userCert = (X509Certificate) factory
                .generateCertificate(new FileInputStream("userCert"+ File.separator + "nindzaCert.cer"));
        System.out.println(CertificateUtil.getCommonNameFromCert(userCert));
    }

}
