package crypto.utils;

import java.io.File;
import java.nio.file.Paths;

public abstract class Constants
{
    public static final long DAY_IN_MILLS = 1000L * 60 * 60 * 24;
    public static final long YEAR_IN_MILLS = 365 * DAY_IN_MILLS;
    public static final long THIRTY_DAYS = DAY_IN_MILLS * 30;

    public static final String COMMAND_TERMINATOR = " > ";

    public static final String RESOURCES_DIR = Paths.get("src", "main", "resources").toString();

    public static final String USER_DIR = RESOURCES_DIR + File.separator + "users" + File.separator;
    public static final String PRIVATE_KEYS_DIR = RESOURCES_DIR + File.separator + "privateKey" + File.separator;

    public static final String ROOT_CA_DIR = RESOURCES_DIR + File.separator + "rootCA" + File.separator;
    public static final String ROOT_CA_FILE_PATH = ROOT_CA_DIR + "rootCA" + CertificateUtil.CERT_EXTENSION;
    public static final String ROOT_CA_PRIVATE_KEY_FILE = ROOT_CA_DIR + "rootCA" + KeyPairUtil.PRIVATE_KEY_EXTENSION;

    public static final String CERT_DIR = RESOURCES_DIR + File.separator + "userCerts" + File.separator;

    public static final String USERS_JSON_PATH = RESOURCES_DIR + File.separator + "users.json";
}
