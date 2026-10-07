package com.examly.springapp.service;

import com.examly.springapp.dto.UserDTO;

public interface UserService {

    /**
     * Public sign-up. The account is ALWAYS created as a Customer (whatever role the body says) and
     * only when the e-mail and mobile number were verified by OTP.
     * Returns null when the e-mail is already registered.
     */
    UserDTO createUser(UserDTO user);

    /** Creates an Admin account. Only called for the predefined admin and from the admin-only endpoint. Null when the e-mail exists. */
    UserDTO createAdmin(UserDTO user);

    UserDTO loginUser(UserDTO user);

    boolean emailExists(String email);
}
