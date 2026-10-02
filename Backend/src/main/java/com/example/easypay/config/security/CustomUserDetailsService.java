package com.example.easypay.config.security;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.easypay.app.entity.AppUser;
import com.example.easypay.app.repository.AppUserRepository;
 
@Service
public class CustomUserDetailsService implements UserDetailsService {
 
	@Autowired
    private AppUserRepository appUserRepository;

    @Override
    public UserDetails loadUserByUsername(String mobileNo) {

        AppUser user = appUserRepository.findByUserMobileNumber(mobileNo)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return new CustomUserDetails(
                user.getAppUserId() ,                           
                user.getUserName() ,                     
                List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole()))
        );
    }

}