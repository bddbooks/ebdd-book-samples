package com.wimp.app.services;

import com.wimp.app.data.InMemoryDataStore;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthenticationService {
    private final InMemoryDataStore dataStore;

    public AuthenticationService(InMemoryDataStore dataStore) {
        this.dataStore = dataStore;
    }

    public String login(String customerName, String password) {
        if (!dataStore.validatePassword(customerName, password)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }
        return "token-" + customerName;
    }
}
