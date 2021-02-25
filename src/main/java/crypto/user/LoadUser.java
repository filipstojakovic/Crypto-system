package crypto.user;

import crypto.user.jsonhandler.UserJson;
import crypto.utils.CertificateUtil;
import crypto.utils.KeyPairUtil;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.KeyPair;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

public class LoadUser
{
    public LoadUser()
    {
    }

    //TODO: Load real user to work with...
    public User loadUser(UserJson user) throws FileNotFoundException, IOException, CertificateException
    {
        X509Certificate userCert = CertificateUtil.loadCertificate(user.getUsername());
        String commonName = CertificateUtil.getCommonNameFromCert(userCert);
        KeyPair userKeyPair = KeyPairUtil.loadUserKeyPair(user.getUsername());

        if (userCert != null && commonName != null && userKeyPair != null)
        {
//            user.setCommonName(commonName);
//            user.setX509Certificate(userCert);
//            user.setKeyPair(userKeyPair);
        } else
            return null;

        return new User();
//        return user;
    }


}
