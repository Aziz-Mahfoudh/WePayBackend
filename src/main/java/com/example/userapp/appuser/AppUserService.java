package com.example.userapp.appuser;

import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class AppUserService implements UserDetailsService {

    private final static String USER_NOT_FOUND_MSG =
            "User with email %s not found";
    private final UserRepository userRepository;
    private final ParticularRepository particularRepository;
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .orElseThrow( () -> new UsernameNotFoundException(String.format(USER_NOT_FOUND_MSG, email)));
    }

    public List<AppUser> loadAllUsers() {
        return userRepository.findAll();
    }

    public AppUser loadUserById(Integer id) {
        return userRepository.findAppUserById(id);
    }

    public AppUser updateUserBalanceById(BalanceUpdateRequest request,Integer id) {
        AppUser user = userRepository.findAppUserById(id);
        user.setBalance(request.getBalance() == null ? user.getBalance() : request.getBalance());
        userRepository.save(user);
        return user;
    }
    public AppUser updateUserById(ParticularUserUpdateRequest request,Integer id) {
        var user = particularRepository.findAppUserById(id);
        user.setEmail(request.getEmail() == null ? user.getEmail() : request.getEmail());
        user.setPassword(request.getPassword() == null ? user.getPassword() : request.getPassword());
        user.setFirstName(request.getFirstName() == null ? user.getFirstName() : request.getFirstName());
        user.setLastName(request.getLastName() == null ? user.getLastName() : request.getLastName());
        user.setPhone(request.getPhone() == null ? user.getPhone() : request.getPhone());

        particularRepository.save(user);

        return particularRepository.findAppUserById(id);
    }
    public String deleteUserById(Integer id) {
        userRepository.deleteById(id);
        return "Done";
    }
    public Double getUserBalance(Integer id) {
        AppUser appUser = userRepository.findAppUserById(id);
        return appUser.getBalance();
    }
}
