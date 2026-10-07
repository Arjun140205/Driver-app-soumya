package com.examly.springapp.dto.validation;

import jakarta.validation.groups.Default;

/**
 * Validation group used when a record is CREATED (POST). It also includes every constraint of the
 * default group, so a create validates "everything" while an update (PUT, default group only)
 * can be partial - for example approving a request only sends {@code {"status": "Approved"}}.
 */
public interface OnCreate extends Default {
}
