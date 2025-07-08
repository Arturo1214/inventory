package com.example.auth_service.service;

import com.example.auth_service.domain.User;
import com.example.auth_service.repository.RoleRepository;
import com.example.auth_service.repository.UserRepository;
import com.example.auth_service.service.dto.CreateUserDto;
import com.example.auth_service.service.errors.RoleNotFoundException;
import com.example.auth_service.service.errors.UserExistException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public UserService(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    public User create(CreateUserDto userDto) throws UserExistException, RoleNotFoundException {
        if (userRepository.findByUsername(userDto.getUsername()).isPresent()) {
            throw new UserExistException();
        }

        User user = new User();
        user.setUsername(userDto.getUsername());
        user.setPassword(userDto.getPassword());
        user.setRole(roleRepository.findById(userDto.getRole()).orElseThrow(RoleNotFoundException::new));
        return userRepository.save(user);
    }

    public Page<User> findAll(Pageable pageable) {
        return userRepository.findAll(pageable);
    }
}
