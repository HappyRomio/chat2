package com.tele2test.security;


import com.tele2test.dao.AppUserDAO;
import com.tele2test.entity.AppUser;
import com.tele2test.services.UserDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionSignUp;

public class ConnectionSignUpImpl implements ConnectionSignUp {
    private UserDataService appUserDAO;

    public ConnectionSignUpImpl(UserDataService appUserDAO) {
        this.appUserDAO = appUserDAO;
    }

    // After logging in social networking.
    // This method will be called to create a corresponding App_User record
    // if it does not already exist.
    @Override
    public String execute(Connection<?> connection) {

        AppUser account = appUserDAO.createAppUser(connection);
        return account.getUserName();
    }
}