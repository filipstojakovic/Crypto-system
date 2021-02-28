package crypto.utils;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.Provider;
import java.security.Security;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class AlgorithmGrabber
{

    public static List<String> getListOfAlgo(Class<?> typeClass)
    {
//        Security.addProvider(new BouncyCastleProvider()); // optional
        List<String> algoList = new ArrayList<>();
        Provider[] providers = Security.getProviders();
        for (Provider provider : providers)
        {
            if (provider instanceof BouncyCastleProvider) //too many
                continue;

            List<String> providerAlgoList = getProviderAlgoList(provider, typeClass);
            if (providerAlgoList != null && !providerAlgoList.isEmpty())
                algoList.addAll(providerAlgoList);
        }
        return algoList;
    }

    //refleksija je cudo :D
    static List<String> getProviderAlgoList(Provider prov, Class<?> typeClass)
    {
        List<String> providerAlgoList = null;

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
            providerAlgoList = new ArrayList<>();
            for (Provider.Service service : algos)
            {
                String algo = service.getAlgorithm();
                providerAlgoList.add(algo);
            }
        }
        return providerAlgoList;
    }
}
