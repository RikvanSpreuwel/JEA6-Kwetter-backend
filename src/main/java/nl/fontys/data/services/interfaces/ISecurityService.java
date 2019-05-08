package nl.fontys.data.services.interfaces;

public interface ISecurityService {
    String findLoggedInUserName();

    void autoLogin(final String userName, final String password);
}
