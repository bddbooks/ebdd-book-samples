package com.wimp.app.specs.drivers;

import com.wimp.app.models.ServiceResult;
import com.wimp.app.restapi.LoginResponse;
import com.wimp.app.services.AuthenticationService;
import com.wimp.app.specs.support.LambdaAction;
import com.wimp.app.specs.support.TestAction;
import com.wimp.app.specs.support.TestActionFailedException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "test.execution.test-target", havingValue = "service-api")
public class AuthenticationServiceDriver implements AuthenticationDriver {
    private final AuthenticationService authenticationService;

    public AuthenticationServiceDriver(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @Override
    public TestAction<LoginResponse> login(String customerName, String password) {
        return new LambdaAction<>("Login", "%s/%s".formatted(customerName, password), () -> {
            ServiceResult<String> result = authenticationService.login(customerName, password);
            if (!result.successful()) {
                throw new TestActionFailedException(result.errorMessage());
            }
            return new LoginResponse(result.value(), customerName);
        });
    }
}

