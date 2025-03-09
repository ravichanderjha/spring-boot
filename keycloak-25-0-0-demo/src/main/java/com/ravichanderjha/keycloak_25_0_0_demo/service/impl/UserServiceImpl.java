package com.ravichanderjha.keycloak_25_0_0_demo.service.impl;

import com.ravichanderjha.keycloak_25_0_0_demo.record.UserRecord;
import com.ravichanderjha.keycloak_25_0_0_demo.service.UserService;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    @Value("${app.keycloak.realm}")
    private String realm;

    @Autowired
    private Keycloak keycloak;

    @Override
    public void createUser(UserRecord userRecord) {
        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setEmailVerified(false);
        userRepresentation.setEmail(userRecord.username());
        userRepresentation.setUsername(userRecord.username());
        userRepresentation.setFirstName(userRecord.firstName());
        userRepresentation.setLastName(userRecord.lastName());

        CredentialRepresentation credentialRepresentation = new CredentialRepresentation();
        credentialRepresentation.setValue(userRecord.password());
        credentialRepresentation.setType(CredentialRepresentation.PASSWORD);

        userRepresentation.setCredentials(List.of(credentialRepresentation));

        UsersResource usersResource = getUserResource();
        Response response = usersResource.create(userRepresentation);

        log.info("Status Code: " + response.getStatus());

        if(!Objects.equals(201, response.getStatus())){
            throw new RuntimeException("Status Code: " + response.getStatus());
        }
        log.info("New user has been created");

    }
    private UsersResource getUserResource(){
        return keycloak.realm(realm).users();
    }
}
