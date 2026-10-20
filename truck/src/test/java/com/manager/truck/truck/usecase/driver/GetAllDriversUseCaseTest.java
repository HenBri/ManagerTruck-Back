package com.manager.truck.truck.usecase.driver;

import com.manager.truck.truck.domain.Company;
import com.manager.truck.truck.domain.Driver;
import com.manager.truck.truck.domain.dto.response.DriversResponse;
import com.manager.truck.truck.domain.enums.CompanyStatus;
import com.manager.truck.truck.domain.enums.DriverStatus;
import com.manager.truck.truck.domain.enums.UserStatus;
import com.manager.truck.truck.exception.ForbiddenOperationException;
import com.manager.truck.truck.security.CurrentUserProvider;
import com.manager.truck.truck.security.UserPrincipal;
import com.manager.truck.truck.service.CompanyService;
import com.manager.truck.truck.service.DriverService;
import com.manager.truck.truck.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetAllDriversUseCaseTest {

    private static final Long SECRETARY_USER_ID = 10L;
    private static final Long DRIVER_USER_ID = 40L;
    private static final Long COMPANY_A = 1L;
    private static final Long COMPANY_B = 2L;

    @Mock
    private DriverService driverService;
    @Mock
    private CompanyService companyService;
    @Mock
    private UserService userService;

    private CurrentUserProvider currentUserProvider;

    private GetAllDriversUseCase useCase;

    private final Pageable pageable = PageRequest.of(0, 20);

    @BeforeEach
    void setUp() {
        currentUserProvider = new CurrentUserProvider(userService);
        useCase = new GetAllDriversUseCase(driverService, companyService, currentUserProvider);
        authenticate(DRIVER_USER_ID);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    private void authenticate(Long userId) {
        UserPrincipal principal = new UserPrincipal(userId, "cuenta@mail.com", "hash", UserStatus.ACTIVE, List.of());
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities()));
    }

    private Company company(Long id) {
        Company company = new Company();
        company.setCompanyId(id);
        company.setCompanyName("Empresa " + id);
        company.setCompanyStatus(CompanyStatus.ACTIVE);
        return company;
    }

    private Driver ownDriver() {
        Driver driver = new Driver();
        driver.setDriverId(66L);
        driver.setCompanyId(COMPANY_A);
        driver.setUserId(DRIVER_USER_ID);
        driver.setDriverName("Chofer Propio");
        driver.setDriverLicense("LIC-1");
        driver.setDriverState(DriverStatus.ACTIVE);
        return driver;
    }

    private void stubActiveMemberOf(Long companyId) {
        when(userService.findPlatformRoleNames(DRIVER_USER_ID)).thenReturn(List.of());
        when(userService.isActiveMember(DRIVER_USER_ID, companyId)).thenReturn(true);
    }

    @Test
    void secretariaListaTodosLosConductores() {
        authenticate(SECRETARY_USER_ID);
        when(userService.findPlatformRoleNames(SECRETARY_USER_ID)).thenReturn(List.of());
        when(userService.isActiveMember(SECRETARY_USER_ID, COMPANY_A)).thenReturn(true);
        when(userService.findCompanyRoleNames(SECRETARY_USER_ID, COMPANY_A)).thenReturn(List.of("SECRETARY"));
        when(companyService.findById(COMPANY_A)).thenReturn(company(COMPANY_A));
        when(driverService.findByCompanyId(eq(COMPANY_A), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(ownDriver())));

        DriversResponse response = useCase.execute(COMPANY_A, pageable);

        assertEquals(1, response.getDrivers().size());
        verify(driverService, never()).findByCompanyIdAndUserId(anyLong(), anyLong());
    }

    @Test
    void conductorListaSoloSuPropioRegistro() {
        stubActiveMemberOf(COMPANY_A);
        when(userService.findCompanyRoleNames(DRIVER_USER_ID, COMPANY_A)).thenReturn(List.of("DRIVER"));
        when(companyService.findById(COMPANY_A)).thenReturn(company(COMPANY_A));
        when(driverService.findByCompanyIdAndUserId(COMPANY_A, DRIVER_USER_ID))
                .thenReturn(Optional.of(ownDriver()));

        DriversResponse response = useCase.execute(COMPANY_A, pageable);

        assertEquals(1, response.getDrivers().size());
        assertEquals(DRIVER_USER_ID, response.getDrivers().get(0).getUserId());
        verify(driverService, never()).findByCompanyId(anyLong(), any(Pageable.class));
    }

    @Test
    void conductorSinRegistroPropioDevuelveListaVacia() {
        stubActiveMemberOf(COMPANY_A);
        when(userService.findCompanyRoleNames(DRIVER_USER_ID, COMPANY_A)).thenReturn(List.of("DRIVER"));
        when(companyService.findById(COMPANY_A)).thenReturn(company(COMPANY_A));
        when(driverService.findByCompanyIdAndUserId(COMPANY_A, DRIVER_USER_ID)).thenReturn(Optional.empty());

        DriversResponse response = useCase.execute(COMPANY_A, pageable);

        assertTrue(response.getDrivers().isEmpty());
        verify(driverService, never()).findByCompanyId(anyLong(), any(Pageable.class));
    }

    @Test
    void usuarioSinMembresiaDeberiaSerRechazado() {
        when(userService.findPlatformRoleNames(DRIVER_USER_ID)).thenReturn(List.of());
        when(userService.isActiveMember(DRIVER_USER_ID, COMPANY_B)).thenReturn(false);

        assertThrows(ForbiddenOperationException.class, () -> useCase.execute(COMPANY_B, pageable));

        verify(driverService, never()).findByCompanyId(anyLong(), any(Pageable.class));
    }
}
