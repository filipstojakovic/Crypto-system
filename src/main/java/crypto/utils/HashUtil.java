package crypto.utils;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.Security;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class HashUtil
{

    public static String hashedPassword(String password, String salt, String hashAlgo) throws NoSuchAlgorithmException
    {
        MessageDigest messageDigest = MessageDigest.getInstance(hashAlgo);
        messageDigest.reset();
        messageDigest.update(salt.getBytes());
        byte[] hashedInputPassword = messageDigest.digest(password.getBytes());
        return Utils.bytesToHex(hashedInputPassword);
    }

    public static List<String> getAllHashAlgo()
    {
        List<String> hashAlgoList = new ArrayList<>();
        Provider[] providers = Security.getProviders();
        for (Provider provider : providers)
        {
            if (provider instanceof BouncyCastleProvider) //ima ih previse
                continue;

            List<String> algos = showHashAlgorithms(provider, MessageDigest.class);
            if (algos != null && !algos.isEmpty())
                hashAlgoList.addAll(algos);
        }
        return hashAlgoList;
    }

    //refleksija je cudo :D
    private static List<String> showHashAlgorithms(Provider prov, Class<?> typeClass)
    {
        List<String> providerHashList = null;

        String type = typeClass.getSimpleName();
        List<Provider.Service> algos = new ArrayList<>();

        Set<Provider.Service> services = prov.getServices();
        for (Provider.Service service : services)
        {
            if (service.getType().equalsIgnoreCase(type))
                algos.add(service);
        }

        if (!algos.isEmpty())
        {
            providerHashList = new ArrayList<>();
            for (Provider.Service service : algos)
            {
                String algo = service.getAlgorithm();
                providerHashList.add(algo);
            }
        }
        return providerHashList;
    }
}
