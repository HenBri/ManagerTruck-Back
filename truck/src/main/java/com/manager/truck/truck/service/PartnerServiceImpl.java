package com.manager.truck.truck.service;

import com.manager.truck.truck.domain.Partner;
import com.manager.truck.truck.exception.ResourceNotFoundException;
import com.manager.truck.truck.repository.PartnerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PartnerServiceImpl implements PartnerService {

    private final PartnerRepository partnerRepository;

    @Override
    public Partner save(Partner partner) {
        return partnerRepository.save(partner);
    }

    @Override
    public Partner findByCompanyIdAndPartnerId(Long companyId, Long partnerId) {
        return partnerRepository.findByCompanyIdAndPartnerId(companyId, partnerId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Socio no encontrado con el id " + partnerId + " en la empresa " + companyId));
    }

    @Override
    public Optional<Partner> findOptionalByCompanyIdAndPartnerId(Long companyId, Long partnerId) {
        return partnerRepository.findByCompanyIdAndPartnerId(companyId, partnerId);
    }

    @Override
    public Optional<Partner> findByCompanyIdAndUserId(Long companyId, Long userId) {
        return partnerRepository.findByCompanyIdAndUserId(companyId, userId);
    }

    @Override
    public boolean existsByCompanyIdAndUserId(Long companyId, Long userId) {
        return partnerRepository.existsByCompanyIdAndUserId(companyId, userId);
    }

    @Override
    public Page<Partner> findByCompanyId(Long companyId, Pageable pageable) {
        return partnerRepository.findByCompanyId(companyId, pageable);
    }
}
