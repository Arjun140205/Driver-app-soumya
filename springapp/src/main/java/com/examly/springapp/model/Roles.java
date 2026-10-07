package com.examly.springapp.model;

/** The two roles of the application, exactly as they are stored in {@code User.userRole}. */
public final class Roles {

    private Roles() {
    }

    public static final String ADMIN = "Admin";
    public static final String CUSTOMER = "Customer";
}
