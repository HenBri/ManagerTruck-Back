package com.manager.truck.truck.service;

import com.manager.truck.truck.domain.Partner;
import com.manager.truck.truck.domain.enums.PartnerType;
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
    public Partner update(Partner partner) {
        return partnerRepository.save(partner);
    }

    @Override
    public Partner findById(Long partnerId) {
        return partnerRepository.findActiveById(partnerId)
                .orElseThrow(() -> new ResourceNotFoundException("Socio no encontrado con el id " + partnerId));
    }

    @Override
    public Optional<Partner> findOptionalById(Long partnerId) {
        return partnerRepository.findActiveById(partnerId);
    }

    @Override
    public Page<Partner> findAll(Pageable pageable) {
        return partnerRepository.findAllActive(pageable);
    }

    @Override
    public Page<Partner> findByType(PartnerType partnerType, Pageable pageable) {
        return partnerRepository.findByType(partnerType, pageable);
    }
}
