package com.manager.truck.truck.domain.dto.response;

import com.manager.truck.truck.domain.Client;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ClientResponse extends CommonResponse {

    private Long clientId;
    private Long companyId;
    private String clientName;
    private String clientCi;
    private String clientEmail;
    private String clientPhone;
    private String clientAddress;
    private Boolean clientState;
    private LocalDateTime clientCreateDate;

    public ClientResponse() {
        super(StatusCodeResponse.SUCCESS_CODE, StatusCodeResponse.SUCCESS_MSG);
    }

    public static ClientResponse from(Client client) {
        ClientResponse response = new ClientResponse();
        response.setClientId(client.getClientId());
        response.setCompanyId(client.getCompanyId());
        response.setClientName(client.getClientName());
        response.setClientCi(client.getClientCi());
        response.setClientEmail(client.getClientEmail());
        response.setClientPhone(client.getClientPhone());
        response.setClientAddress(client.getClientAddress());
        response.setClientState(client.getClientState());
        response.setClientCreateDate(client.getClientCreateDate());
        return response;
    }
}
