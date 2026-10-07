package com.examly.springapp.dto.validation;

import jakarta.validation.groups.Default;

/** Validation group used when an OTP is VERIFIED (the code itself becomes mandatory). */
public interface OnVerify extends Default {
}
