package com.manager.truck.truck.service;

import com.manager.truck.truck.exception.BadRequestException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Set;

@Service
public class LicenseValidationServiceImpl implements LicenseValidationService {

    private static final Set<String> LICENSE_CATEGORIES = Set.of(
            "A1", "A2", "B1", "B2", "C1", "C2", "D1", "D2");

    @Override
    public void validate(String licenseCategory, LocalDate licenseExpiration) {
        String category = normalizeCategory(licenseCategory);
        if (category != null && !LICENSE_CATEGORIES.contains(category)) {
            throw new BadRequestException(
                    "license_category '" + category + "' no pertenece al catálogo de categorías: " + LICENSE_CATEGORIES);
        }
        if (licenseExpiration != null && licenseExpiration.isBefore(LocalDate.now())) {
            throw new BadRequestException("license_expiration no puede ser una fecha anterior a hoy");
        }
    }

    @Override
    public String normalizeCategory(String licenseCategory) {
        if (licenseCategory == null || licenseCategory.isBlank()) {
            return null;
        }
        return licenseCategory.trim().toUpperCase();
    }
}
