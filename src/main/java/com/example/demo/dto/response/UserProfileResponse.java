package com.example.demo.dto.response;

import com.example.demo.constants.UserRole;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileResponse {

    private UUID id;

    private String firstName;

    private String lastName;

    private String email;

    private String profileImage;

    private UserRole role;

    private Boolean active;

    private LocalDateTime createdAt;
}
