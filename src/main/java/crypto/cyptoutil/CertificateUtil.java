package crypto.cyptoutil;

import crypto.utils.Constants;
import crypto.utils.Utils;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.KeyUsage;
import org.bouncycastle.asn1.x509.X509Name;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.jce.PrincipalUtil;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;

import java.io.*;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Random;

import static crypto.cyptoutil.KeyPairUtil.generateKeyPair;

public class CertificateUtil
{
    public static final String X_509 = "X.509";
    public static final String CERT_EXTENSION = "Cert.cer";
    public static final String SHA_256_WITH_RSA = "SHA256withRSA";


    public static X509Certificate loadUserCertificate(String username) throws FileNotFoundException
    {
        try
        {
            CertificateFactory factory = CertificateFactory.getInstance(X_509);
            return (X509Certificate) factory.generateCertificate(new FileInputStream(userCertFile(username)));
        } catch (CertificateException ex)
        {
            ex.printStackTrace();
        }
        return null;
    }

    public static X509Certificate loadRootCertificate() throws FileNotFoundException
    {
        try
        {
            CertificateFactory factory = CertificateFactory.getInstance(X_509);
            return (X509Certificate) factory.generateCertificate(new FileInputStream(Constants.ROOT_CA_FILE_PATH));
        } catch (CertificateException ex)
        {
            ex.printStackTrace();
        }
        return null;
    }

    public static String getCommonNameFromCert(X509Certificate cert)
    {
        try
        {
            var principal = PrincipalUtil.getSubjectX509Principal(cert);
            var values = principal.getValues(X509Name.CN);
            return (String) values.get(0);

        } catch (Exception ex)
        {
            ex.printStackTrace();
        }
        return null;
    }

    private static File userCertFile(String username)
    {
        String path = Constants.CERT_DIR + username + CERT_EXTENSION;
        return Paths.get(path).toFile();
    }

    // check if there is certificate, init if there is not ( self signed cert)
    public static void initRootCertificate()
    {
        if (!Files.exists(Paths.get(Constants.ROOT_CA_FILE_PATH)))
            try
            {
                Security.addProvider(new BouncyCastleProvider());
                // Create self signed Root CA certificate
                KeyPair rootCAKeyPair = generateKeyPair();
                X509v3CertificateBuilder builder = new JcaX509v3CertificateBuilder(
                        new X500Name("CN=rootCA"), // issuer authority
                        BigInteger.valueOf(new Random().nextInt()), //serial number of certificate
                        Utils.getCurrentDate(), // start of validity
                        Utils.getNextYearDate(), //end of certificate validity
                        new X500Name("CN=rootCA"), // subject name of certificate
                        rootCAKeyPair.getPublic()); // public key of certificate
                // key usage restrictions
                builder.addExtension(Extension.keyUsage, true, new KeyUsage(KeyUsage.keyCertSign));
                builder.addExtension(Extension.basicConstraints, false, new BasicConstraints(true));
                X509Certificate rootCA = new JcaX509CertificateConverter()
                        .getCertificate(builder
                                .build(new JcaContentSignerBuilder(SHA_256_WITH_RSA)
                                        .setProvider(Constants.BC_PROVIDER)
                                        .build(rootCAKeyPair.getPrivate()))); // private key of signing authority , here it is self signed
                saveCertificateToFile(rootCA, Constants.ROOT_CA_FILE_PATH);
                KeyPairUtil.savePrivateKeyToFile(rootCAKeyPair.getPrivate(), Constants.ROOT_CA_PRIVATE_KEY_FILE);
            } catch (Exception ex)
            {
                ex.printStackTrace();
            }
    }

    public static void saveCertificateToFile(X509Certificate certificate, String filePath)
    {
        File file = new File(filePath);
        try (FileOutputStream fileOutputStream = new FileOutputStream(file))
        {

            fileOutputStream.write(certificate.getEncoded());
            fileOutputStream.flush();

        } catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public static String getSubjectDn(X509Certificate x509cert)
    {
        Principal principal = x509cert.getSubjectDN();
        return principal.getName();
    }

    public static String getIssuerDn(X509Certificate x509cert)
    {
        Principal principal = x509cert.getIssuerDN();
        return principal.getName();
    }

    public static String getUserCertPath(String username)
    {
        return Constants.CERT_DIR + username + CertificateUtil.CERT_EXTENSION;
    }

    public static void generateSignedUserCert(String commonName, String username)
    {
        try
        {
            X509Certificate rootCA = loadRootCertificate();
            PrivateKey rootPrivateKey = KeyPairUtil.loadPrivateKey(Paths.get(Constants.ROOT_CA_PRIVATE_KEY_FILE));

            KeyPair endUserCertKeyPair = generateKeyPair();
            X509v3CertificateBuilder builder = new JcaX509v3CertificateBuilder(
                    rootCA, //here rootCA is issuer authority
                    BigInteger.valueOf(new Random().nextInt()),
                    Utils.getCurrentDate(),
                    Utils.getNextYearDate(),
                    new X500Name("CN=" + commonName),
                    endUserCertKeyPair.getPublic());
            builder.addExtension(Extension.keyUsage, true, new KeyUsage(KeyUsage.digitalSignature));
            builder.addExtension(Extension.basicConstraints, false, new BasicConstraints(false));
            X509Certificate endUserCert = new JcaX509CertificateConverter()
                    .getCertificate(builder
                            .build(new JcaContentSignerBuilder(SHA_256_WITH_RSA)
                                    .setProvider(Constants.BC_PROVIDER).
                                            build(rootPrivateKey)));// private key of signing authority , here it is signed by rootCA
            saveCertificateToFile(endUserCert, getUserCertPath(username));
            KeyPairUtil.savePrivateKeyToFile(endUserCertKeyPair.getPrivate(), KeyPairUtil.getPrivateKeyPath(username));

        } catch (NoSuchProviderException | CertificateException | NoSuchAlgorithmException | OperatorCreationException | IOException ex)
        {
            ex.printStackTrace();
        }
    }
}
