package com.wimp.app.specs.drivers;

import com.wimp.app.restapi.LoginResponse;
import com.wimp.app.specs.support.TestAction;

public interface AuthenticationDriver {
    TestAction<LoginResponse> login(String customerName, String password);
}
