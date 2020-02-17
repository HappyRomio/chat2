package com.tele2test.services;

import com.tele2test.entity.AppUser;
import com.tele2test.security.SocialUserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    UserDataService userDataService;

    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        AppUser appUser = userDataService.findByUserName(userName);

        if (appUser != null) {
            System.out.println("User not found! " + userName);
            throw new UsernameNotFoundException("User " + userName + " allready present in DB");
        }

        if (appUser == null) {
            System.out.println("User not found! " + userName + " Create new user.");
            appUser = userDataService.createSimpleChatUser(userName);
        }

        System.out.println("Found User: " + appUser);
        // [ROLE_USER, ROLE_ADMIN,..]
        List<String> roleNames = userDataService.getRoleNames(appUser);
        List<GrantedAuthority> grantList = new ArrayList<GrantedAuthority>();

        if (roleNames != null) {
            for (String role : roleNames) {
                // ROLE_USER, ROLE_ADMIN,..
                GrantedAuthority authority = new SimpleGrantedAuthority(role);
                grantList.add(authority);
            }
        }

        appUser.setEncrytedPassword("");
        SocialUserDetailsImpl userDetails = new SocialUserDetailsImpl(appUser, roleNames);
        return userDetails;
    }

}
