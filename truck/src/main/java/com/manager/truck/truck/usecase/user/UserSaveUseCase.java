package com.manager.truck.truck.usecase.user;

import com.manager.truck.truck.domain.Company;
import com.manager.truck.truck.domain.Roles;
import com.manager.truck.truck.domain.User;
import com.manager.truck.truck.domain.dto.request.UserRequest;
import com.manager.truck.truck.domain.dto.response.UserResponse;
import com.manager.truck.truck.service.CompanyService;
import com.manager.truck.truck.service.RoleService;
import com.manager.truck.truck.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserSaveUseCase {
    @Autowired
    private UserService userService;
    @Autowired
    private CompanyService companyService;
    @Autowired
    private RoleService roleService;

    public UserResponse execute(UserRequest userRequest,Long companyId,Long roleId){
        User userbuild =userBuild(userRequest,companyId,roleId);
        userService.save(userbuild);
        return userResponseBuild(userbuild);
    }

    private User userBuild(UserRequest userRequest, Long companyId, Long roleId){
        User user = new User();
        Roles roles = roleService.findById(roleId);
        Company company = companyService.findById(companyId);
        user.setName(userRequest.getName());
        user.setLastname(userRequest.getLastname());
        user.setEmail(userRequest.getEmail());
        user.setPhone(userRequest.getPhone());
        user.setCi(userRequest.getCi());
        user.setCompany(company);
        user.setRoles(roles);
        return user;
    }
    private UserResponse userResponseBuild(User user){
        return new UserResponse(user);
    }



}
