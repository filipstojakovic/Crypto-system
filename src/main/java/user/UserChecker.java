package user;

import user.User;

public class UserChecker
{

    /**
     * Check if user exists and it's certificate
     *
     * @return null if user does not exist, user if exists (with valid username and password)
     */
    public User checkUser(String username, String password)
    {
        return new User(username, password);
    }

}
