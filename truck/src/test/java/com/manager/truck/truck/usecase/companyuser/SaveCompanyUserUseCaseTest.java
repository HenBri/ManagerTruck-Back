package com.manager.truck.truck.usecase.companyuser;

import com.manager.truck.truck.domain.Company;
import com.manager.truck.truck.domain.User;
import com.manager.truck.truck.domain.dto.request.CompanyMembershipRequest;
import com.manager.truck.truck.domain.dto.response.CompanyMembershipResponse;
import com.manager.truck.truck.domain.enums.CompanyStatus;
import com.manager.truck.truck.domain.enums.MembershipStatus;
import com.manager.truck.truck.domain.enums.UserStatus;
import com.manager.truck.truck.exception.ConflictException;
import com.manager.truck.truck.exception.ForbiddenOperationException;
import com.manager.truck.truck.exception.ResourceNotFoundException;
import com.manager.truck.truck.security.CurrentUserProvider;
import com.manager.truck.truck.security.UserPrincipal;
import com.manager.truck.truck.service.CompanyService;
import com.manager.truck.truck.service.CompanyUserService;
import com.manager.truck.truck.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SaveCompanyUserUseCaseTest {

    private static final Long SECRETARY_USER_ID = 10L;
    private static final Long COMPANY_A = 1L;
    private static final Long COMPANY_B = 2L;

    @Mock
    private CompanyUserService companyUserService;
    @Mock
    private CompanyService companyService;
    @Mock
    private UserService userService;

    private CurrentUserProvider currentUserProvider;
    private SaveCompanyUserUseCase useCase;

    @BeforeEach
    void setUp() {
        currentUserProvider = new CurrentUserProvider(userService);
        useCase = new SaveCompanyUserUseCase(companyUserService, companyService, userService, currentUserProvider);
        UserPrincipal principal = new UserPrincipal(SECRETARY_USER_ID, "secretaria@mail.com", "hash",
                UserStatus.ACTIVE, List.of());
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities()));
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    private Company company(Long id, CompanyStatus status) {
        Company company = new Company();
        company.setCompanyId(id);
        company.setCompanyName("Empresa " + id);
        company.setCompanyStatus(status);
        return company;
    }

    private User user(Long id, String email, UserStatus status) {
        User user = new User();
        user.setUserId(id);
        user.setUserEmail(email);
        user.setUserName("Nombre");
        user.setUserLastname("Apellido");
        user.setUserStatus(status);
        return user;
    }

    private CompanyMembershipRequest request(String email) {
        CompanyMembershipRequest request = new CompanyMembershipRequest();
        request.setUserEmail(email);
        return request;
    }

    @Test
    void secretariaDeLaEmpresaPuedeCrearMembresiaActiva() {
        when(userService.findPlatformRoleNames(SECRETARY_USER_ID)).thenReturn(List.of());
        when(userService.findCompanyRoleNames(SECRETARY_USER_ID, COMPANY_A)).thenReturn(List.of("SECRETARY"));
        when(companyService.findById(COMPANY_A)).thenReturn(company(COMPANY_A, CompanyStatus.ACTIVE));
        when(userService.findByEmail("nuevo@mail.com"))
                .thenReturn(Optional.of(user(7L, "nuevo@mail.com", UserStatus.ACTIVE)));
        when(companyUserService.existsByCompanyIdAndUserId(COMPANY_A, 7L)).thenReturn(false);
        when(companyUserService.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        CompanyMembershipResponse response = useCase.execute(COMPANY_A, request("nuevo@mail.com"));

        assertEquals(COMPANY_A, response.getCompanyId());
        assertEquals(7L, response.getUserId());
        assertEquals(MembershipStatus.ACTIVE, response.getMembershipStatus());
        verify(companyUserService).save(any());
    }

    @Test
    void secretariaDeEmpresaANoPuedeCrearMembresiaEnLaEmpresaB() {
        when(userService.findPlatformRoleNames(SECRETARY_USER_ID)).thenReturn(List.of());
        when(userService.findCompanyRoleNames(SECRETARY_USER_ID, COMPANY_B)).thenReturn(List.of());

        assertThrows(ForbiddenOperationException.class,
                () -> useCase.execute(COMPANY_B, request("nuevo@mail.com")));

        verify(companyUserService, never()).save(any());
    }

    @Test
    void usuarioNoAdminNoPuedeCrearMembresias() {
        when(userService.findPlatformRoleNames(SECRETARY_USER_ID)).thenReturn(List.of());
        when(userService.findCompanyRoleNames(SECRETARY_USER_ID, COMPANY_A)).thenReturn(List.of("PARTNER"));

        assertThrows(ForbiddenOperationException.class,
                () -> useCase.execute(COMPANY_A, request("nuevo@mail.com")));

        verify(companyUserService, never()).save(any());
    }

    @Test
    void membresiaDuplicadaDeberiaDevolver409() {
        when(userService.findPlatformRoleNames(SECRETARY_USER_ID)).thenReturn(List.of());
        when(userService.findCompanyRoleNames(SECRETARY_USER_ID, COMPANY_A)).thenReturn(List.of("SECRETARY"));
        when(companyService.findById(COMPANY_A)).thenReturn(company(COMPANY_A, CompanyStatus.ACTIVE));
        when(userService.findByEmail("duplicado@mail.com"))
                .thenReturn(Optional.of(user(7L, "duplicado@mail.com", UserStatus.ACTIVE)));
        when(companyUserService.existsByCompanyIdAndUserId(COMPANY_A, 7L)).thenReturn(true);

        assertThrows(ConflictException.class,
                () -> useCase.execute(COMPANY_A, request("duplicado@mail.com")));

        verify(companyUserService, never()).save(any());
    }

    @Test
    void correoInexistenteDeberiaDevolver404() {
        when(userService.findPlatformRoleNames(SECRETARY_USER_ID)).thenReturn(List.of());
        when(userService.findCompanyRoleNames(SECRETARY_USER_ID, COMPANY_A)).thenReturn(List.of("SECRETARY"));
        when(companyService.findById(COMPANY_A)).thenReturn(company(COMPANY_A, CompanyStatus.ACTIVE));
        when(userService.findByEmail("fantasma@mail.com")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> useCase.execute(COMPANY_A, request("fantasma@mail.com")));
    }

    @Test
    void cuentaInactivaNoPuedeRecibirMembresia() {
        when(userService.findPlatformRoleNames(SECRETARY_USER_ID)).thenReturn(List.of());
        when(userService.findCompanyRoleNames(SECRETARY_USER_ID, COMPANY_A)).thenReturn(List.of("SECRETARY"));
        when(companyService.findById(COMPANY_A)).thenReturn(company(COMPANY_A, CompanyStatus.ACTIVE));
        when(userService.findByEmail("inactivo@mail.com"))
                .thenReturn(Optional.of(user(7L, "inactivo@mail.com", UserStatus.INACTIVE)));

        assertThrows(ConflictException.class,
                () -> useCase.execute(COMPANY_A, request("inactivo@mail.com")));

        verify(companyUserService, never()).save(any());
    }

    @Test
    void empresaInactivaNoAdmiteMembresiasNuevas() {
        when(userService.findPlatformRoleNames(SECRETARY_USER_ID)).thenReturn(List.of());
        when(userService.findCompanyRoleNames(SECRETARY_USER_ID, COMPANY_A)).thenReturn(List.of("SECRETARY"));
        when(companyService.findById(COMPANY_A)).thenReturn(company(COMPANY_A, CompanyStatus.INACTIVE));

        assertThrows(ConflictException.class,
                () -> useCase.execute(COMPANY_A, request("nuevo@mail.com")));
    }
}
