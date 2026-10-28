package com.manager.truck.truck.domain.dto.response;

import com.manager.truck.truck.domain.AuxiliaryProvider;
import com.manager.truck.truck.domain.enums.AuxiliaryProviderKind;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
public class AuxiliaryProviderResponse extends CommonResponse {

    private Long providerId;
    private AuxiliaryProviderKind providerKind;
    private String providerName;
    private String providerDocument;
    private String providerPhone;
    private String providerEmail;
    private String state;
    private OffsetDateTime dateCreate;

    public AuxiliaryProviderResponse() {
        super(StatusCodeResponse.SUCCESS_CODE, StatusCodeResponse.SUCCESS_MSG);
    }

    public static AuxiliaryProviderResponse from(AuxiliaryProvider provider) {
        AuxiliaryProviderResponse response = new AuxiliaryProviderResponse();
        response.setProviderId(provider.getProviderId());
        response.setProviderKind(provider.getProviderKind());
        response.setProviderName(provider.getProviderName());
        response.setProviderDocument(provider.getProviderDocument());
        response.setProviderPhone(provider.getProviderPhone());
        response.setProviderEmail(provider.getProviderEmail());
        response.setState(provider.getState());
        response.setDateCreate(provider.getDateCreate());
        return response;
    }

    public String getProviderStatus() {
        return state;
    }
}
