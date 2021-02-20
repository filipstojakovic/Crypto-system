package crypto.utils;

import java.io.File;

public abstract class PathConsts
{
    public static final String COMMAND_TERMINATOR = " > ";

    public static final String USER_DIR = "users" + File.separator;

    public static final String CERT_DIR = "openssl" + File.separator + "certs" + File.separator;
    public static final String PRIVATE_KEYS_DIR = "openssl" + File.separator + "private" + File.separator;

    public static final String USERS_JSON = "users.json";
}
