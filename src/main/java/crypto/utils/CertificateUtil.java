package crypto.utils;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.KeyUsage;
import org.bouncycastle.asn1.x509.X509Name;
import org.bouncycastle.cert.CertIOException;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.jce.PrincipalUtil;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.math.BigInteger;
import java.nio.file.Paths;
import java.security.*;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Random;

import static crypto.utils.KeyPairUtil.RSA_ALGO;
import static crypto.utils.KeyPairUtil.generateKeyPair;

public class CertificateUtil
{
    public static final String X_509 = "X.509";
    public static final String CERT_EXTENSION = "Cert.cer";
    public static final String SHA_256_WITH_RSA = "SHA256withRSA";


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
        return (String) values.get(0);
    }

    private static File userCertFile(String username)
    {
        String path = Constants.CERT_DIR + username + CERT_EXTENSION;
        return Paths.get(path).toFile();
    }

    // check if there is certificate, init if there is not
    public static void initRootCertificate()
    {
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
                                    .setProvider("BC")
                                    .build(rootCAKeyPair.getPrivate()))); // private key of signing authority , here it is self signed
            saveCertificateToFile(rootCA, Constants.ROOT_CA_FILE_PATH);
            savePrivateKeyToFile(rootCAKeyPair.getPrivate(), Constants.ROOT_CA_PRIVATE_KEY_FILE);
        } catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    private static void savePrivateKeyToFile(PrivateKey privateKey, String path)
    {
        try (FileOutputStream fileOutputStream = new FileOutputStream(path))
        {
            PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(
                    privateKey.getEncoded());
            fileOutputStream.write(pkcs8EncodedKeySpec.getEncoded());
            fileOutputStream.flush();
        } catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    private static PrivateKey loadPrivateKey(String path) // maybe add algorithm for KeyFactory.getInstance
    {
        PrivateKey privateKey = null;
        File filePrivateKey = new File(path);
        try (FileInputStream fis = new FileInputStream(filePrivateKey))
        {
            byte[] encodedPrivateKey = new byte[(int) filePrivateKey.length()];
            fis.read(encodedPrivateKey);
            PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(
                    encodedPrivateKey);
            KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGO);
            privateKey = keyFactory.generatePrivate(privateKeySpec);

        } catch (Exception ex)
        {
            ex.printStackTrace();
        }

        return privateKey;
    }

    public static File saveCertificateToFile(X509Certificate certificate, String filePath)
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
        return file;
    }

    public static String getUserCertPath(String username)
    {
        return Constants.CERT_DIR + username + CertificateUtil.CERT_EXTENSION;
    }

    public static X509Certificate generateSignedUserCert(String commonName, String username)
    {
        X509Certificate endUserCert = null;
        try
        {
            CertificateFactory factory = CertificateFactory.getInstance(X_509);
            X509Certificate rootCA = (X509Certificate) factory.generateCertificate(new FileInputStream(Constants.ROOT_CA_FILE_PATH));
            PrivateKey rootPrivateKey = loadPrivateKey(Constants.ROOT_CA_PRIVATE_KEY_FILE);

            KeyPair endUserCertKeyPair = generateKeyPair();
            X509v3CertificateBuilder builder = new JcaX509v3CertificateBuilder(
                    rootCA, //here rootCA is issuer authority
                    BigInteger.valueOf(new Random().nextInt()),
                    Utils.getCurrentDate(),
                    Utils.getNextYearDate(),
                    new X500Name("CN=" + commonName), endUserCertKeyPair.getPublic());
            builder.addExtension(Extension.keyUsage, true, new KeyUsage(KeyUsage.digitalSignature));
            builder.addExtension(Extension.basicConstraints, false, new BasicConstraints(false));
            endUserCert = new JcaX509CertificateConverter().getCertificate(builder
                    .build(new JcaContentSignerBuilder(SHA_256_WITH_RSA).setProvider("BC").
                            build(rootPrivateKey)));// private key of signing authority , here it is signed by intermedCA
            saveCertificateToFile(endUserCert, getUserCertPath(username));
            savePrivateKeyToFile(endUserCertKeyPair.getPrivate(), Constants.CERT_DIR + username + KeyPairUtil.PRIVATE_KEY_EXTENSION);

        } catch (NoSuchProviderException | CertificateException | NoSuchAlgorithmException | FileNotFoundException | CertIOException | OperatorCreationException ex)
        {
            ex.printStackTrace();
        }

        return endUserCert;
    }
}
