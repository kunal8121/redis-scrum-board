package com.redisdemoproject.model;

import com.redisdemoproject.model.spec.Role;
import lombok.Data;

import java.util.UUID;

@Data
public class User {
    private UUID id;
    private String name;
    private String emailId;
    private Integer yearsOfExperience;
    private Role role;

    private void assignRole() {
        switch (yearsOfExperience) {
            case 0, 1 -> role = Role.SWE;
            case 2, 3 -> role = Role.SWE_1;
            case 4, 5 -> role = Role.SWE_2;
            case 6, 7 -> role = Role.SWE_3;
            case 8, 9 -> role = Role.SENIOR_SWE;
            case 10, 11 -> role = Role.STAFF_SWE;
            default -> role = Role.ER_MANAGER;
        }
    }
}
