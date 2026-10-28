package com.manager.truck.truck.domain.dto.response;

import com.manager.truck.truck.domain.User;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
public class UserResponse extends CommonResponse {

    private Long userId;
    private Long personId;
    private String email;
    private String firstName;
    private String lastName;
    private String maternalLastName;
    private String documentNumber;
    private String phone;
    private String status;
    private OffsetDateTime dateCreate;

    public UserResponse() {
        super(StatusCodeResponse.SUCCESS_CODE, StatusCodeResponse.SUCCESS_MSG);
    }

    public static UserResponse from(User user) {
        UserResponse response = new UserResponse();
        response.setUserId(user.getUserId());
        response.setPersonId(user.getPersonId());
        response.setStatus(user.getState());
        response.setDateCreate(user.getDateCreate());
        if (user.getPerson() != null) {
            response.setEmail(user.getPerson().getEmail());
            response.setFirstName(user.getPerson().getFirstName());
            response.setLastName(user.getPerson().getLastName());
            response.setMaternalLastName(user.getPerson().getMaternalLastName());
            response.setDocumentNumber(user.getPerson().getDocumentNumber());
            response.setPhone(user.getPerson().getPhone());
        }
        return response;
    }

    // Compatibilidad con getters anteriores
    public String getName() {
        return firstName;
    }

    public String getLastname() {
        return lastName;
    }

    public String getCi() {
        return documentNumber;
    }
}
