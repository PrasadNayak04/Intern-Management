package com.robosoft.internmanagement.service.JwtSecurity;


import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class JwtUserDetailsService implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
/*
        try {
            User user = Optional.of(jwtUserRepository.findByUsername(username)).orElse(null);
            return new org.springframework.security.core.userdetails.User(user.getUsername(),
                    user.getPassword(),
                    new ArrayList<>());
        } catch (UsernameNotFoundException e){
            throw new UsernameNotFoundException("User not found with username: " + username);
        }*/
        return null;
    }

    public String greet(){
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return "Hello " + userDetails.getUsername();
    }

}
