package crypto.encrypdecrypt;

import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.cms.*;
import org.bouncycastle.cms.jcajce.JceCMSContentEncryptorBuilder;
import org.bouncycastle.cms.jcajce.JceKeyTransEnvelopedRecipient;
import org.bouncycastle.cms.jcajce.JceKeyTransRecipientId;
import org.bouncycastle.cms.jcajce.JceKeyTransRecipientInfoGenerator;
import org.bouncycastle.operator.jcajce.JcaAlgorithmParametersConverter;

import javax.crypto.spec.OAEPParameterSpec;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.Iterator;

import static crypto.utils.Constants.BC_PROVIDER;

public class DigitalEnvelope
{
    public static byte[] createKeyTransEnvelope(X509Certificate recipeintCert, byte[] data)
            throws GeneralSecurityException, CMSException, IOException
    {
        CMSEnvelopedDataGenerator envelopedGen = new CMSEnvelopedDataGenerator();
        JcaAlgorithmParametersConverter paramsConverter = new JcaAlgorithmParametersConverter();
        envelopedGen.addRecipientInfoGenerator(new JceKeyTransRecipientInfoGenerator(
                recipeintCert,
                paramsConverter
                        .getAlgorithmIdentifier(PKCSObjectIdentifiers.id_RSAES_OAEP, OAEPParameterSpec.DEFAULT))
                .setProvider(BC_PROVIDER));
        return envelopedGen.generate(
                new CMSProcessableByteArray(data),
                new JceCMSContentEncryptorBuilder(CMSAlgorithm.AES256_CBC)
                        .setProvider(BC_PROVIDER)
                        .build())
                .getEncoded();
    }

    // encryption certificate is used to identify the recipient associated with the private key
    public static byte[] extractKeyTransEnvelope(PrivateKey recipientPrivateKey, X509Certificate recipientCert, byte[] encEnvelopedData)
            throws CMSException
    {
        CMSEnvelopedData envelopedData = new CMSEnvelopedData(encEnvelopedData);
        RecipientInformationStore recipients = envelopedData.getRecipientInfos();
        Collection c = recipients.getRecipients(new JceKeyTransRecipientId(recipientCert));
        Iterator it = c.iterator();
        if (it.hasNext())
        {
            RecipientInformation recipient = (RecipientInformation) it.next();
            return recipient.getContent(new JceKeyTransEnvelopedRecipient(recipientPrivateKey)
                    .setProvider(BC_PROVIDER));
        }
        throw new IllegalArgumentException("recipient for certificate not found");
    }
}
