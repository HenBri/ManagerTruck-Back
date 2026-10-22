package com.manager.truck.truck.usecase.driver;

import com.manager.truck.truck.domain.Company;
import com.manager.truck.truck.domain.Driver;
import com.manager.truck.truck.domain.User;
import com.manager.truck.truck.domain.dto.request.DriverRequest;
import com.manager.truck.truck.domain.dto.request.TruckDriverAssignmentRequest;
import com.manager.truck.truck.domain.enums.CompanyStatus;
import com.manager.truck.truck.domain.enums.UserStatus;
import com.manager.truck.truck.security.CurrentUserProvider;
import com.manager.truck.truck.security.UserPrincipal;
import com.manager.truck.truck.service.CompanyAccountProvisioningService;
import com.manager.truck.truck.service.CompanyService;
import com.manager.truck.truck.service.DriverService;
import com.manager.truck.truck.service.LicenseValidationServiceImpl;
import com.manager.truck.truck.service.PartnerService;
import com.manager.truck.truck.service.UserService;
import com.manager.truck.truck.usecase.truckdriverassignment.AssignTruckDriverUseCase;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SaveDriverUseCaseTest {

    private static final Long COMPANY_ID = 1L;
    private static final Long ACCOUNT_ID = 30L;

    @Mock private DriverService driverService;
    @Mock private PartnerService partnerService;
    @Mock private CompanyService companyService;
    @Mock private CompanyAccountProvisioningService accountProvisioningService;
    @Mock private UserService userService;
    @Mock private AssignTruckDriverUseCase assignTruckDriverUseCase;

    private SaveDriverUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new SaveDriverUseCase(driverService, partnerService, companyService, accountProvisioningService,
                new LicenseValidationServiceImpl(), new CurrentUserProvider(userService), assignTruckDriverUseCase);
        UserPrincipal principal = new UserPrincipal(10L, "secretary@mail.com", "hash", UserStatus.ACTIVE, List.of());
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities()));
        when(userService.findPlatformRoleNames(10L)).thenReturn(List.of());
        when(userService.findCompanyRoleNames(10L, COMPANY_ID)).thenReturn(List.of("SECRETARY"));
        Company company = new Company();
        company.setCompanyId(COMPANY_ID);
        company.setCompanyStatus(CompanyStatus.ACTIVE);
        when(companyService.findById(COMPANY_ID)).thenReturn(company);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void conductorProvisionaCuentaYReutilizaSusDatos() {
        User account = new User();
        account.setUserId(ACCOUNT_ID);
        account.setUserName("Luis");
        account.setUserLastname("Lopez");
        account.setUserCi("456");
        account.setUserPhone("71111111");
        when(accountProvisioningService.provision(any(), anyString(), anyString(), anyString(), anyString(),
                any(), any(), anyString())).thenReturn(account);
        when(driverService.save(any(Driver.class))).thenAnswer(invocation -> invocation.getArgument(0));

        useCase.execute(COMPANY_ID, request());

        ArgumentCaptor<Driver> driver = ArgumentCaptor.forClass(Driver.class);
        verify(driverService).save(driver.capture());
        assertEquals(ACCOUNT_ID, driver.getValue().getUserId());
        assertEquals("Luis Lopez", driver.getValue().getDriverName());
        assertEquals("456", driver.getValue().getDriverCi());
        verify(accountProvisioningService).provision(COMPANY_ID, "luis@mail.com", "password1", "Luis", "Lopez",
                "456", "71111111", "DRIVER");
        verifyNoInteractions(assignTruckDriverUseCase);
    }

    @Test
    void conductorConCamionCreaAsignacionDesdeLaFechaActual() {
        User account = new User();
        account.setUserId(ACCOUNT_ID);
        account.setUserName("Luis");
        account.setUserLastname("Lopez");
        when(accountProvisioningService.provision(any(), anyString(), anyString(), anyString(), anyString(),
                any(), any(), anyString())).thenReturn(account);
        when(driverService.save(any(Driver.class))).thenAnswer(invocation -> {
            Driver driver = invocation.getArgument(0);
            driver.setDriverId(40L);
            return driver;
        });
        DriverRequest request = request();
        request.setTruckId(50L);

        useCase.execute(COMPANY_ID, request);

        ArgumentCaptor<TruckDriverAssignmentRequest> assignment = ArgumentCaptor.forClass(TruckDriverAssignmentRequest.class);
        verify(assignTruckDriverUseCase).execute(org.mockito.ArgumentMatchers.eq(COMPANY_ID), assignment.capture());
        assertEquals(50L, assignment.getValue().getTruckId());
        assertEquals(40L, assignment.getValue().getDriverId());
        org.junit.jupiter.api.Assertions.assertNotNull(assignment.getValue().getAssignedFrom());
    }

    private DriverRequest request() {
        DriverRequest request = new DriverRequest();
        request.setDriverLicense("LIC-12345");
        request.setEmail("luis@mail.com");
        request.setPassword("password1");
        request.setName("Luis");
        request.setLastname("Lopez");
        request.setCi("456");
        request.setPhone("71111111");
        return request;
    }
}
