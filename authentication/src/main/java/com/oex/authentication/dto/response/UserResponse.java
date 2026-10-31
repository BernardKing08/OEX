package com.oex.authentication.dto.response;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import com.oex.authentication.model.Role;

@Data
@Builder
public class UserResponse {

    private Long id;

    private String firstName;

    private String lastName;

    private String email;

    private Role role;

    private boolean enabled;

    private LocalDateTime createdAt;

}