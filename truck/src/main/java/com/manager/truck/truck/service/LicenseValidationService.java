package com.manager.truck.truck.service;

import java.time.LocalDate;

public interface LicenseValidationService {

    void validate(String licenseCategory, LocalDate licenseExpiration);

    String normalizeCategory(String licenseCategory);
}
