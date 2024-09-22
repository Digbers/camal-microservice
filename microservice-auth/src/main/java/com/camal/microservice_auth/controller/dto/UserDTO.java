package com.camal.microservice_auth.controller.dto;
import com.camal.microservice_auth.persistence.entity.RolesEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
public class UserDTO {

    private long id;
    private String usercodigo;
    private String username;
    @JsonIgnore
    private String password;
    private Boolean isEnabled;
    private Boolean accountNoExpired;
    private Boolean accountNoLocked;
    private Boolean credentialsNoExpired;
    private Set<RolesEntity> roles = new HashSet<>();
}
