package com.smartcontact.smartcontactmanager.config;

import com.smartcontact.smartcontactmanager.Dao.UserRepo;
import com.smartcontact.smartcontactmanager.models.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepo userRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepo.getUserByUserName(username);
        if (user == null) {
            throw new UsernameNotFoundException("could not found user!");
        }
        CustomerUserDetail customerUserDetail = new CustomerUserDetail(user);
        return customerUserDetail;
    }

}
