package com.akhil.authify.service.impl;

import com.akhil.authify.exception.ApiException;
import com.akhil.authify.model.UserEntity;
import com.akhil.authify.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class AppUserDetailsService implements UserDetailsService {
    @Autowired
    private UserRepo userRepo;
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserEntity existingUser = userRepo.findByEmail(email).orElseThrow(()->new ApiException("user not found"));
        return new User(existingUser.getEmail(),existingUser.getPassword(),new ArrayList<>());
    }
}
