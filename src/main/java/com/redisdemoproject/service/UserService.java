package com.redisdemoproject.service;

import com.redisdemoproject.model.QueryOptions;
import com.redisdemoproject.model.User;
import com.redisdemoproject.model.spec.Role;
import com.redisdemoproject.model.spec.UserCreateSpec;
import com.redisdemoproject.repository.UserRepository;
import jakarta.inject.Singleton;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import java.util.List;
import java.util.UUID;

@Singleton
@RequiredArgsConstructor
public class UserService {

    private static final int OFFSET = 0;
    private final UserRepository userRepository;

    public User registerUser(@Valid UserCreateSpec spec) {
        UUID id = UUID.randomUUID();
        User user = User.builder()
                .id(id)
                .emailId(spec.emailId())
                .name(spec.name())
                .role(assignRole(spec.yearsOfExperience()))
                .build();

        return userRepository.save(user);
    }

    public User getById(UUID id) {
        return userRepository.getById(id);
    }

    public List<User> findAll() {
        return userRepository.findAll(10, OFFSET);
    }

    public List<User> queryAll(QueryOptions queryOptions) {
        return userRepository.query(queryOptions);
    }

    public User updateUser(UUID id, UserCreateSpec spec) {
        User oldUser = userRepository.getById(id);
        User updatedUser = User.builder()
                .id(id)
                .name(spec.name())
                .role(assignRole(spec.yearsOfExperience()))
                .emailId(spec.emailId())
                .build();

        return userRepository.update(id, updatedUser);
    }

    public void delete(UUID id) {
        userRepository.delete(id);
    }

    private Role assignRole(int yearsOfExperience) {
        return switch (yearsOfExperience) {
            case 0, 1 -> Role.SWE;
            case 2, 3 -> Role.SWE_1;
            case 4, 5 -> Role.SWE_2;
            case 6, 7 -> Role.SWE_3;
            case 8, 9 -> Role.SENIOR_SWE;
            case 10, 11 -> Role.STAFF_SWE;
            default -> Role.ER_MANAGER;
        };
    }
}
