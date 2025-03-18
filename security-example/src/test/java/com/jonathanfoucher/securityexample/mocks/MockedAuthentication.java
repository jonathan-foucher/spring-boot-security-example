package com.jonathanfoucher.securityexample.mocks;

import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@WithSecurityContext(factory = AuthenticatedUserSecurityContextFactory.class)
public @interface MockedAuthentication {
    String jwt() default "eyJhbGciOiJSUzI1NiJ9.eyJ1dWlkIjoiZDA0MTg4MTYtYTBmMi00ZDc4LTgyMmEtNzYxODQwM2JlMzEyIiwiZmly" +
            "c3RfbmFtZSI6IkpvaG4iLCJsYXN0X25hbWUiOiJEb2UiLCJqb2IiOiJkZXZlbG9wZXIiLCJyb2xlcyI6WyJ0b3RvIl0sImlzcyI6Im" +
            "h0dHA6Ly9sb2NhbGhvc3Q6ODA5MSIsImlhdCI6MTc0MTkwODI5NSwiZXhwIjoxNzQxOTE1NDk1fQ.brFn2Iz8tXk-NELbqHwHOl8VW" +
            "5GDGf6JRo4w3Yw6iBHjmtppzuDl8Zj0QusUpky_x9PxOe_OzVy31GWJobpi_62buVN3WtNEP5l6xJyB_L1iH52kE9iVRVw_qSaedou" +
            "WFeVN-k7RB5Y_jdo-JABm8YhuxCaj6BxWkTGeTLs5I_tSV2IVU14OpOybmQaqIMbPSrvbG7gNaOLE6atlSMi5ZwSwiqPnIiGDSLqk7" +
            "JzsSWmjunmJdgu_Inuv7Q1hA49aGYhMwTmytgtSgX462-UA69rubrDi22ukatdhgLpalU5BZiv34gazuJkZzJGHcCsLsfCA7MyyNsU" +
            "LuMDCfxNQPg";

    String issuedAt() default "2025-03-13T15:23:44.00Z";

    String expiresAt() default "2025-03-13T17:23:44.00Z";

    String uuid() default "d0418816-a0f2-4d78-822a-7618403be312";

    String firstName() default "John";

    String lastName() default "Doe";

    String job() default "IT engineer";

    String[] roles() default {};
}
