package com.manager.truck.truck.usecase.user;

import com.manager.truck.truck.domain.Person;
import com.manager.truck.truck.domain.User;
import com.manager.truck.truck.domain.dto.request.UserUpdateRequest;
import com.manager.truck.truck.domain.dto.response.UserResponse;
import com.manager.truck.truck.exception.ForbiddenOperationException;
import com.manager.truck.truck.security.CurrentUser;
import com.manager.truck.truck.security.CurrentUserProvider;
import com.manager.truck.truck.service.PersonService;
import com.manager.truck.truck.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdateUserUseCase {

    private final UserService userService;
    private final PersonService personService;
    private final CurrentUserProvider currentUserProvider;

    @Transactional
    public UserResponse execute(Long userId, UserUpdateRequest request) {
        CurrentUser currentUser = currentUserProvider.get();
        if (!currentUser.userId().equals(userId) && !currentUserProvider.isAdmin()) {
            throw new ForbiddenOperationException("Solo puede modificar su propia cuenta o ser administrador");
        }
        User user = userService.findById(userId);
        if (user.getPersonId() != null) {
            Person person = personService.findById(user.getPersonId());
            if (request.getFirstName() != null) {
                person.setFirstName(request.getFirstName().trim());
            }
            if (request.getLastName() != null) {
                person.setLastName(request.getLastName().trim());
            }
            if (request.getMaternalLastName() != null) {
                person.setMaternalLastName(request.getMaternalLastName());
            }
            if (request.getDocumentNumber() != null) {
                person.setDocumentNumber(request.getDocumentNumber());
            }
            if (request.getPhone() != null) {
                person.setPhone(request.getPhone());
            }
            personService.update(person);
        }
        return UserResponse.from(userService.findById(userId));
    }
}
