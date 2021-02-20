package crypto.utils;

import org.bouncycastle.asn1.x509.X509Name;
import org.bouncycastle.jce.PrincipalUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

public class CertificateUtil
{
    public static final String X_509 = "X.509";
    public static final String CERT_EXTENSION = ".pem";

    public static X509Certificate loadCertificate(String username) throws FileNotFoundException
    {
        try
        {
            CertificateFactory factory = CertificateFactory.getInstance(X_509);
            X509Certificate userCert = (X509Certificate) factory.generateCertificate(new FileInputStream(userCertFile(username)));
            return userCert;

        } catch (CertificateException ex)
        {
            ex.printStackTrace();
        }
        return null;
    }

    public static String getCommonNameFromCert(X509Certificate cert) throws CertificateEncodingException
    {
        var principal = PrincipalUtil.getSubjectX509Principal(cert);
        var values = principal.getValues(X509Name.CN);
        return  (String) values.get(0);
    }

    private static File userCertFile(String username)
    {
        String path = PathConsts.CERT_DIR + username + CERT_EXTENSION;
        return Paths.get(path).toFile();
    }



}
