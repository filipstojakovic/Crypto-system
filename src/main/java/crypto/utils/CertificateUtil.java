package crypto.utils;

import crypto.user.User;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.KeyUsage;
import org.bouncycastle.asn1.x509.X509Name;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.cert.CertIOException;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.jcajce.provider.keystore.PKCS12;
import org.bouncycastle.jce.PrincipalUtil;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.joda.time.DateTime;

import java.io.*;
import java.math.BigInteger;
import java.nio.file.Paths;
import java.security.*;
import java.security.cert.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Random;

import static crypto.utils.KeyPairUtil.generateKeyPair;

public class CertificateUtil
{
    public static final String X_509 = "X.509";
    public static final String CERT_EXTENSION = ".pem";

    public static final String ROOT_CER = "rootCA" + File.separator + "rootCA.cer";

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
        String path = PathConsts.CERT_DIR + username + CERT_EXTENSION;
        return Paths.get(path).toFile();
    }

    public static boolean initRootCertificate() // check if there is certificate, init if there is not
    {
        boolean isGood = true;
        //TODO: check if exists CA
        try
        {
            Security.addProvider(new BouncyCastleProvider());
            // Create self signed Root CA certificate
            KeyPair rootCAKeyPair = generateKeyPair();
            X509v3CertificateBuilder builder = new JcaX509v3CertificateBuilder(
                    new X500Name("CN=rootCA"), // issuer authority
                    BigInteger.valueOf(new Random().nextInt()), //serial number of certificate
                    DateTime.now().toDate(), // start of validity
                    new DateTime(2025, 12, 31, 0, 0, 0, 0).toDate(), //end of certificate validity
                    new X500Name("CN=rootCA"), // subject name of certificate
                    rootCAKeyPair.getPublic()); // public key of certificate
            // key usage restrictions
            builder.addExtension(Extension.keyUsage, true, new KeyUsage(KeyUsage.keyCertSign));
            builder.addExtension(Extension.basicConstraints, false, new BasicConstraints(true));
            X509Certificate rootCA = new JcaX509CertificateConverter()
                    .getCertificate(builder
                            .build(new JcaContentSignerBuilder("SHA256withRSA")
                                    .setProvider("BC")
                                    .build(rootCAKeyPair.getPrivate()))); // private key of signing authority , here it is self signed
            saveCertificateToFile(rootCA, "rootCA" + File.separator + "rootCA.cer");
            savePrivateKeyToFile(rootCAKeyPair.getPrivate(), "rootCA" + File.separator + "rootCAprivate.key");
        } catch (Exception ex)
        {
            isGood = false;
            ex.printStackTrace();
        }
        return isGood;
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
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            privateKey = keyFactory.generatePrivate(privateKeySpec);

        } catch (Exception ex)
        {
            ex.printStackTrace();
        }

        return privateKey;
    }

    public static void saveCertificateToFile(X509Certificate certificate, String filePath)
    {
        try (FileOutputStream fileOutputStream = new FileOutputStream(filePath))
        {
            fileOutputStream.write(certificate.getEncoded());
            fileOutputStream.flush();

        } catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public static void generateUserCert(User user)
    {
        //user must have common name here on out...
        try
        {
            CertificateFactory factory = CertificateFactory.getInstance(X_509);
            X509Certificate rootCA = (X509Certificate) factory.generateCertificate(new FileInputStream(ROOT_CER));
            PrivateKey rootPrivateKey = loadPrivateKey("rootCA" + File.separator + "rootCAprivate.key");

            KeyPair endUserCertKeyPair = generateKeyPair();
            X509v3CertificateBuilder builder = new JcaX509v3CertificateBuilder(
                    rootCA, //here rootCA is issuer authority
                    BigInteger.valueOf(new Random().nextInt()), DateTime.now().toDate(),
                    new DateTime(2025, 12, 31, 0, 0, 0, 0).toDate(),
                    new X500Name("CN="+user.getCommonName()), endUserCertKeyPair.getPublic()); //TODO: update common name
            builder.addExtension(Extension.keyUsage, true, new KeyUsage(KeyUsage.digitalSignature));
            builder.addExtension(Extension.basicConstraints, false, new BasicConstraints(false));
            X509Certificate endUserCert = new JcaX509CertificateConverter().getCertificate(builder
                    .build(new JcaContentSignerBuilder("SHA256withRSA").setProvider("BC").
                            build(rootPrivateKey)));// private key of signing authority , here it is signed by intermedCA
            saveCertificateToFile(endUserCert, "userCert" + File.separator + user.getUsername() + "Cert.cer");

        } catch (NoSuchProviderException | CertificateException | NoSuchAlgorithmException | FileNotFoundException | CertIOException | OperatorCreationException ex)
        {
            ex.printStackTrace();
        }
    }
}
