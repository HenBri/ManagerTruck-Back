package com.manager.truck.truck.domain.dto.response;

import com.manager.truck.truck.domain.Client;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
public class ClientResponse extends CommonResponse {

    private Long clientId;
    private String clientName;
    private String clientCi;
    private String clientEmail;
    private String clientPhone;
    private String clientAddress;
    private String taxId;
    private String contactPerson;
    private String contactPhone;
    private String state;
    private OffsetDateTime dateCreate;

    public ClientResponse() {
        super(StatusCodeResponse.SUCCESS_CODE, StatusCodeResponse.SUCCESS_MSG);
    }

    public static ClientResponse from(Client client) {
        ClientResponse response = new ClientResponse();
        response.setClientId(client.getClientId());
        response.setClientName(client.getClientName());
        response.setClientCi(client.getClientCi());
        response.setClientEmail(client.getClientEmail());
        response.setClientPhone(client.getClientPhone());
        response.setClientAddress(client.getClientAddress());
        response.setTaxId(client.getTaxId());
        response.setContactPerson(client.getContactPerson());
        response.setContactPhone(client.getContactPhone());
        response.setState(client.getState());
        response.setDateCreate(client.getDateCreate());
        return response;
    }

    public Boolean getClientState() {
        return "ACTIVE".equalsIgnoreCase(state);
    }
}
