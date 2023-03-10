package finalproject.service;



import finalproject.models.User;
import finalproject.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Optional;

@ComponentScan
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    UserRepository userRepository;
    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

//    @Override
//    @Transactional
//    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
//        User user = userRepository
//                .findByEmail(email)
//                .orElseThrow(() -> new UsernameNotFoundException("User Not Found with email: " + email));
//
//        return UserDetailsImpl.build(user);
//    }
@Override
public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
    return UserDetailsImpl.build(user);
}




}

