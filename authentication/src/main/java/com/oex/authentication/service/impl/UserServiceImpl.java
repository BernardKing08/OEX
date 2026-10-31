package com.oex.authentication.service.impl;

import com.oex.authentication.dto.request.RegisterRequest;
import com.oex.authentication.dto.request.UpdateUserRequest;
import com.oex.authentication.dto.response.UserResponse;
import com.oex.authentication.exception.EmailAlreadyExistsException;
import com.oex.authentication.exception.ResourceNotFoundException;
import com.oex.authentication.mapper.UserMapper;
import com.oex.authentication.model.User;
import com.oex.authentication.model.Role;
import com.oex.authentication.repository.UserRepository;
import com.oex.authentication.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserResponse registerUser(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException("Email already exists.");
        }

        User user = userMapper.toEntity(request);

        User savedUser = userRepository.save(user);

        return userMapper.toResponse(savedUser);
    }

    @Override
    public UserResponse getUserById(Long id){

        User user = userRepository.findById(id)
                .orElseThrow(
                    () -> new ResourceNotFoundException("User not found")
                );


        return mapToResponse(user);
    }


    @Override
    public UserResponse updateUser(Long id, UpdateUserRequest request){

        User user = userRepository.findById(id)
                .orElseThrow(
                    () -> new RuntimeException("User not found")
                );


        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());


        User updatedUser = userRepository.save(user);


        return mapToResponse(updatedUser);
    }



    private UserResponse mapToResponse(User user){

        return UserResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .role(user.getRole())
                .createdAt(user.getCreatedAt())
                .build();
    }

}