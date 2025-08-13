package com.shopespher.dto;

import com.shopespher.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserResponseDTO {

    private Long id;
    private String email;
    private Boolean isActive;
    private String firstName;
    private String lastName;
    private String phone;
    private String dob;
    private String gender;
    private String profileImageUrl;
    private String address;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private List<Role> roles;
}
