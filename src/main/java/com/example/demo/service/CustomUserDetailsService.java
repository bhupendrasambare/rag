/**
 * author @bhupendrasambare
 * Date   :24/07/26
 * Time   :7:59 am
 * Project:rag
 **/
package com.example.demo.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface CustomUserDetailsService extends UserDetailsService {

    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException;
}
