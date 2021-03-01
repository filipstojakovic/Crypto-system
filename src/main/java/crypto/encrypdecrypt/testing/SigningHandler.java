package crypto.encrypdecrypt.testing;

import crypto.utils.Constants;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaCertStore;
import org.bouncycastle.cms.*;
import org.bouncycastle.cms.jcajce.JcaSignerInfoGeneratorBuilder;
import org.bouncycastle.cms.jcajce.JcaSimpleSignerInfoVerifierBuilder;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.DigestCalculatorProvider;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.operator.jcajce.JcaDigestCalculatorProviderBuilder;
import org.bouncycastle.util.Store;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import static crypto.encrypdecrypt.CertificateUtil.SHA_256_WITH_RSA;

public class SigningHandler
{

    public static byte[] createSignedObject(PrivateKey signingKey, X509Certificate signingCert, byte[] data)
            throws GeneralSecurityException, OperatorCreationException, CMSException, IOException
    {
        List<X509Certificate> certList = new ArrayList<X509Certificate>();
        certList.add(signingCert);
        Store certs = new JcaCertStore(certList);
        DigestCalculatorProvider digProvider = new JcaDigestCalculatorProviderBuilder()
                .setProvider(Constants.BC_PROVIDER).build();
        JcaSignerInfoGeneratorBuilder signerInfoGeneratorBuilder = new JcaSignerInfoGeneratorBuilder(digProvider);
        ContentSigner signer = new JcaContentSignerBuilder(SHA_256_WITH_RSA)
                .setProvider(Constants.BC_PROVIDER).build(signingKey);
        CMSSignedDataGenerator gen = new CMSSignedDataGenerator();
        gen.addSignerInfoGenerator(signerInfoGeneratorBuilder.build(signer, signingCert));
        gen.addCertificates(certs);
        CMSTypedData msg = new CMSProcessableByteArray(data);
        // true indicates the data used to create the signature is to be included as well
        return gen.generate(msg, true).getEncoded();
    }


    public static boolean verifySignedObject(byte[] cmsSignedData)
            throws GeneralSecurityException, OperatorCreationException, CMSException
    {
        CMSSignedData signedData = new CMSSignedData(cmsSignedData);
        Store certStore = signedData.getCertificates();
        SignerInformationStore signers = signedData.getSignerInfos();
        Collection c = signers.getSigners();
        Iterator it = c.iterator();
        while (it.hasNext())
        {
            SignerInformation signer = (SignerInformation) it.next();
            Collection certCollection = certStore.getMatches(signer.getSID());
            Iterator certIt = certCollection.iterator();
            X509CertificateHolder cert = (X509CertificateHolder) certIt.next();
            if (!signer.verify(new JcaSimpleSignerInfoVerifierBuilder().setProvider(Constants.BC_PROVIDER).build(cert)))
            {
                return false;
            }
        }
        return true;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static byte[] createDetachedSignature(PrivateKey signingKey, X509Certificate signingCert, byte[] data)
            throws GeneralSecurityException, OperatorCreationException, CMSException, IOException
    {
        List<X509Certificate> certList = new ArrayList<X509Certificate>();
        certList.add(signingCert);
        Store certs = new JcaCertStore(certList);
        DigestCalculatorProvider digProvider = new JcaDigestCalculatorProviderBuilder()
                .setProvider(Constants.BC_PROVIDER).build();
        JcaSignerInfoGeneratorBuilder signerInfoGeneratorBuilder =
                new JcaSignerInfoGeneratorBuilder(digProvider);
        ContentSigner signer = new JcaContentSignerBuilder(SHA_256_WITH_RSA)
                .setProvider(Constants.BC_PROVIDER).build(signingKey);

        CMSSignedDataGenerator gen = new CMSSignedDataGenerator();
        gen.addSignerInfoGenerator(signerInfoGeneratorBuilder.build(signer, signingCert));
        gen.addCertificates(certs);
        CMSTypedData msg = new CMSProcessableByteArray(data);
        return gen.generate(msg).getEncoded();
    }

    public static boolean verifyDetachedData(byte[] cmsSignedData, byte[] data)
            throws GeneralSecurityException, OperatorCreationException, CMSException
    {
        CMSSignedData signedData = new CMSSignedData(new CMSProcessableByteArray(data), cmsSignedData);
        Store certStore = signedData.getCertificates();
        SignerInformationStore signers = signedData.getSignerInfos();
        Collection c = signers.getSigners();
        Iterator it = c.iterator();
        while (it.hasNext())
        {
            SignerInformation signer = (SignerInformation) it.next();
            Collection certCollection = certStore.getMatches(signer.getSID());
            Iterator certIt = certCollection.iterator();
            X509CertificateHolder cert = (X509CertificateHolder) certIt.next();
            if (!signer.verify(new JcaSimpleSignerInfoVerifierBuilder().setProvider(Constants.BC_PROVIDER).build(cert)))
            {
                return false;
            }
        }
        return true;
    }


}
