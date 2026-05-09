package com.example.Endurance.user;

import com.example.Endurance.dto.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public User toDomain(UserEntity user) {
        return new User(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getPassword(),
                user.getRoles()
        );
    }
    public UserEntity toEntity(User user) {
        return new UserEntity(
                user.id(),
                user.name(),
                user.email(),
                user.password(),
                user.roles()
        );
    }
}
