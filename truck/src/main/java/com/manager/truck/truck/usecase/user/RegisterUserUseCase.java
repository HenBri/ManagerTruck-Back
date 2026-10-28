package com.manager.truck.truck.usecase.user;

import com.manager.truck.truck.domain.Person;
import com.manager.truck.truck.domain.User;
import com.manager.truck.truck.domain.dto.request.UserRegisterRequest;
import com.manager.truck.truck.domain.dto.response.UserResponse;
import com.manager.truck.truck.exception.ConflictException;
import com.manager.truck.truck.service.PersonService;
import com.manager.truck.truck.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RegisterUserUseCase {

    private final UserService userService;
    private final PersonService personService;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserResponse execute(UserRegisterRequest request) {
        String email = request.getEmail().trim().toLowerCase();
        if (userService.existsByEmail(email)) {
            throw new ConflictException("El correo ya está registrado para un usuario: " + email);
        }
        Person person = personService.findByEmail(email).orElseGet(() -> {
            Person p = new Person();
            p.setFirstName(request.getFirstName().trim());
            p.setLastName(request.getLastName().trim());
            p.setMaternalLastName(request.getMaternalLastName());
            p.setDocumentNumber(request.getDocumentNumber());
            p.setEmail(email);
            p.setPhone(request.getPhone());
            return personService.save(p);
        });

        User user = new User();
        user.setPersonId(person.getPersonId());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setState("ACTIVE");
        User saved = userService.save(user);

        return UserResponse.from(userService.findById(saved.getUserId()));
    }
}
