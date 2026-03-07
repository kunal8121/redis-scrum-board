package com.redisdemoproject.model;

import com.redisdemoproject.model.spec.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private UUID id;
    private String name;
    private String emailId;
    private Integer yearsOfExperience;
    private Role role;
}
