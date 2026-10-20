package com.manager.truck.truck.domain.dto.response;

import com.manager.truck.truck.domain.AuxiliaryProvider;
import com.manager.truck.truck.domain.enums.AuxiliaryProviderKind;
import com.manager.truck.truck.domain.enums.AuxiliaryProviderStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class AuxiliaryProviderResponse extends CommonResponse {

    private Long providerId;
    private Long companyId;
    private AuxiliaryProviderKind providerKind;
    private String providerName;
    private String providerDocument;
    private String providerPhone;
    private String providerEmail;
    private AuxiliaryProviderStatus providerStatus;
    private LocalDateTime createdAt;

    public AuxiliaryProviderResponse() {
        super(StatusCodeResponse.SUCCESS_CODE, StatusCodeResponse.SUCCESS_MSG);
    }

    public static AuxiliaryProviderResponse from(AuxiliaryProvider provider) {
        AuxiliaryProviderResponse response = new AuxiliaryProviderResponse();
        response.setProviderId(provider.getProviderId());
        response.setCompanyId(provider.getCompanyId());
        response.setProviderKind(provider.getProviderKind());
        response.setProviderName(provider.getProviderName());
        response.setProviderDocument(provider.getProviderDocument());
        response.setProviderPhone(provider.getProviderPhone());
        response.setProviderEmail(provider.getProviderEmail());
        response.setProviderStatus(provider.getProviderStatus());
        response.setCreatedAt(provider.getCreatedAt());
        return response;
    }
}
