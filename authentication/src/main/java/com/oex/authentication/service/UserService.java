package com.oex.authentication.service;

import com.oex.authentication.dto.request.RegisterRequest;
import com.oex.authentication.dto.request.UpdateUserRequest;
import com.oex.authentication.dto.response.UserResponse;

public interface UserService {

    UserResponse registerUser(RegisterRequest request);

    UserResponse getUserById(Long id);

    UserResponse updateUser(Long id, UpdateUserRequest request);

}