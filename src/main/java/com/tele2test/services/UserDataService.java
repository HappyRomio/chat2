package com.tele2test.services;

import java.util.*;

import com.tele2test.dao.*;
import com.tele2test.entity.*;
import com.tele2test.utils.EncrytpedPasswordUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionKey;
import org.springframework.social.connect.UserProfile;
import org.springframework.stereotype.Service;

@Service
public class UserDataService {

    @Autowired
    AppRoleDAO appRoleDao;

    @Autowired
    AppUserDAO appUserDao;

    @Autowired
    UserRoleDAO userRoleDao;

    @Autowired
    UserConnectionDAO userConnectionDAO;

    @Autowired
    MessagesHistoryDAO messagesHistoryDAO;

    @Autowired
    FBService fbService;

    public List<String> getRoleNames(AppUser appUser) {
        List<String> nameRoles = new ArrayList<String>();
        for (UserRole ur : userRoleDao.findByAppUser(appUser)) {
            if (ur.getAppUser().getUserId() == ur.getAppUser().getUserId()) {
                nameRoles.add(ur.getAppRole().getRoleName());
            }
        }
        return nameRoles;
    }

    public boolean saveUser(AppUser user) {
        return false;
    }

    public void updateUser(AppUser user) {
        appUserDao.save(user);

    }

    public AppUser findByUserName(String userName) {
        return appUserDao.findByUserName(userName);
    }

    private String findAvailableUserName(String userName_prefix) {
        AppUser account = appUserDao.findByUserName(userName_prefix);
        if (account == null) {
            return userName_prefix;
        }
        int i = 0;
        while (true) {
            String userName = userName_prefix + "_" + i++;
            account = appUserDao.findByUserName(userName);
            if (account == null) {
                return userName;
            }
        }
    }


    // Auto create App User Account.
    public AppUser createAppUser(Connection<?> connection) {

        ConnectionKey key = connection.getKey();
        // (facebook,12345), (google,123) ...
        System.out.println("key= (" + key.getProviderId() + "," + key.getProviderUserId() + ")");
        UserProfile userProfile = fbService.getProfile("me", connection.createData().getAccessToken());
        // UserProfile userProfile = connection.fetchUserProfile();
        String email = userProfile.getEmail();
        AppUser appUser = appUserDao.findByEmail(email);
        if (appUser != null) {
            return appUser;
        }
        String userName_prefix = userProfile.getFirstName().trim().toLowerCase()//
                + "_" + userProfile.getLastName().trim().toLowerCase();
        String userName = this.findAvailableUserName(userName_prefix);
        // Random Password! TODO: Need send email to User!
        String randomPassword = UUID.randomUUID().toString().substring(0, 5);
        String encrytedPassword = EncrytpedPasswordUtil.encrytePassword(randomPassword);
        appUser = new AppUser();
        appUser.setEnabled(true);
        appUser.setEncrytedPassword(encrytedPassword);
        appUser.setUserName(userName);
        appUser.setEmail(email);
        appUser.setFirstName(userProfile.getFirstName());
        appUser.setLastName(userProfile.getLastName());
        appUserDao.save(appUser);
        // Create default Role
        List<String> roleNames = new ArrayList<String>();
        roleNames.add("ROLLE_USER");
        createRoleFor(appUser, roleNames);
        return appUser;
    }

    // Auto create Chat User Account.
    public AppUser createSimpleChatUser(String userName) {
        //
        AppUser appUser = new AppUser();
        appUser.setEnabled(true);
        appUser.setUserName(userName);
        appUserDao.save(appUser);
        // Create default Role
        List<String> roleNames = new ArrayList<String>();
        roleNames.add("ROLE_USER");
        createRoleFor(appUser, roleNames);
        return appUser;
    }

    public void createRoleFor(AppUser appUser, List<String> roleNames) {
        for (String roleName : roleNames) {
            AppRole role = appRoleDao.findByRoleName(roleName);
            if (role == null) {
                role = new AppRole();
                role.setRoleName(roleName);
                appRoleDao.save(role);
            }
            UserRole userRole = new UserRole();
            userRole.setAppRole(role);
            userRole.setAppUser(appUser);
            userRoleDao.save(userRole);
        }
    }

    public UserConnection findUserConnectionByUserProviderId(String userProviderId) {
        List<UserConnection> list = userConnectionDAO.findByProviderUserId(userProviderId);
        return list.isEmpty() ? null : list.get(0);
    }

    public void dropUser(String userName) {
        AppUser user = appUserDao.findByUserName(userName);
        for (UserRole userRole : userRoleDao.findByAppUser(user)) {
            userRoleDao.delete(userRole);
        }
        appUserDao.delete(user);
    }

    public List<Message> getTop20ofHistory(){
        Pageable pageable = PageRequest.of(0, 20, Sort.by("time").descending());
        LinkedList<Message> result = new LinkedList<>();
        for(Message msg : messagesHistoryDAO.findAll(pageable)) {
            result.addFirst(msg);
        }
        return result;
    }
}
