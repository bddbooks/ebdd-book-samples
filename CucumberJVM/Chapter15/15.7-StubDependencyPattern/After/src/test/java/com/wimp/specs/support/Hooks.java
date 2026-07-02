package com.wimp.specs.support;

import com.wimp.app.data.InMemoryDataStore;
import io.cucumber.java.Before;
import org.springframework.beans.factory.annotation.Autowired;

public class Hooks {
    private final InMemoryDataStore dataStore;
    private final RestApiContext restApiContext;

    @Autowired
    public Hooks(InMemoryDataStore dataStore, RestApiContext restApiContext) {
        this.dataStore = dataStore;
        this.restApiContext = restApiContext;
    }

    @Before
    public void resetData() {
        dataStore.reset();
        restApiContext.clearBearerToken();
    }
}
