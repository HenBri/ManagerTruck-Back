package com.manager.truck.truck.usecase.driver;

import com.manager.truck.truck.domain.Company;
import com.manager.truck.truck.domain.CompanyUser;
import com.manager.truck.truck.domain.CompanyUserId;
import com.manager.truck.truck.domain.Driver;
import com.manager.truck.truck.domain.Partner;
import com.manager.truck.truck.domain.User;
import com.manager.truck.truck.domain.dto.request.DriverRequest;
import com.manager.truck.truck.domain.dto.response.DriverResponse;
import com.manager.truck.truck.domain.enums.CompanyStatus;
import com.manager.truck.truck.domain.enums.DriverStatus;
import com.manager.truck.truck.domain.enums.MembershipStatus;
import com.manager.truck.truck.domain.enums.PartnerStatus;
import com.manager.truck.truck.domain.enums.PartnerType;
import com.manager.truck.truck.domain.enums.UserStatus;
import com.manager.truck.truck.exception.BadRequestException;
import com.manager.truck.truck.exception.ConflictException;
import com.manager.truck.truck.exception.ForbiddenOperationException;
import com.manager.truck.truck.exception.ResourceNotFoundException;
import com.manager.truck.truck.security.CurrentUserProvider;
import com.manager.truck.truck.security.UserPrincipal;
import com.manager.truck.truck.service.CompanyService;
import com.manager.truck.truck.service.CompanyUserService;
import com.manager.truck.truck.service.DriverService;
import com.manager.truck.truck.service.LicenseValidationService;
import com.manager.truck.truck.service.LicenseValidationServiceImpl;
import com.manager.truck.truck.service.PartnerService;
import com.manager.truck.truck.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SaveDriverUseCaseTest {

    private static final Long SECRETARY_USER_ID = 10L;
    private static final Long LINKED_USER_ID = 30L;
    private static final Long COMPANY_A = 1L;
    private static final Long COMPANY_B = 2L;
    private static final Long PERSON_PARTNER_ID = 100L;
    private static final Long BUSINESS_PARTNER_ID = 200L;
    private static final Long OTHER_TENANT_PARTNER_ID = 999L;

    @Mock
    private DriverService driverService;
    @Mock
    private PartnerService partnerService;
    @Mock
    private CompanyService companyService;
    @Mock
    private CompanyUserService companyUserService;
    @Mock
    private UserService userService;

    private CurrentUserProvider currentUserProvider;
    private final LicenseValidationService licenseValidationService = new LicenseValidationServiceImpl();

    private SaveDriverUseCase useCase;

    @BeforeEach
    void setUp() {
        currentUserProvider = new CurrentUserProvider(userService);
        useCase = new SaveDriverUseCase(driverService, partnerService, companyService, companyUserService,
                userService, licenseValidationService, currentUserProvider);
        authenticate(SECRETARY_USER_ID);
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

    private void stubSecretaryOf(Long companyId) {
        when(userService.findPlatformRoleNames(SECRETARY_USER_ID)).thenReturn(List.of());
        when(userService.findCompanyRoleNames(SECRETARY_USER_ID, companyId)).thenReturn(List.of("SECRETARY"));
    }

    private void stubActiveCompany(Long companyId) {
        when(companyService.findById(companyId)).thenReturn(company(companyId, CompanyStatus.ACTIVE));
    }

    private Company company(Long id, CompanyStatus status) {
        Company company = new Company();
        company.setCompanyId(id);
        company.setCompanyName("Empresa " + id);
        company.setCompanyStatus(status);
        return company;
    }

    private CompanyUser membership(Long companyId, Long userId, MembershipStatus status) {
        CompanyUser membership = new CompanyUser();
        membership.setId(new CompanyUserId(companyId, userId));
        membership.setMembershipStatus(status);
        return membership;
    }

    private User user(Long userId, UserStatus status) {
        User user = new User();
        user.setUserId(userId);
        user.setUserEmail("cuenta@mail.com");
        user.setUserName("Cuenta");
        user.setUserLastname("Prueba");
        user.setUserStatus(status);
        return user;
    }

    private Partner partner(Long partnerId, PartnerType type) {
        Partner partner = new Partner();
        partner.setPartnerId(partnerId);
        partner.setCompanyId(COMPANY_A);
        partner.setPartnerType(type);
        partner.setPartnerName("Socio");
        partner.setPartnerState(PartnerStatus.ACTIVE);
        return partner;
    }

    private DriverRequest request() {
        DriverRequest request = new DriverRequest();
        request.setDriverName("Conductor Nuevo");
        request.setDriverLicense("LIC-12345");
        return request;
    }

    private void stubActiveMembership() {
        when(companyUserService.findByCompanyIdAndUserId(COMPANY_A, LINKED_USER_ID))
                .thenReturn(Optional.of(membership(COMPANY_A, LINKED_USER_ID, MembershipStatus.ACTIVE)));
        when(userService.findById(LINKED_USER_ID)).thenReturn(user(LINKED_USER_ID, UserStatus.ACTIVE));
    }

    private void stubSavedDriver() {
        when(driverService.save(any(Driver.class))).thenAnswer(invocation -> {
            Driver driver = invocation.getArgument(0);
            driver.setDriverId(88L);
            return driver;
        });
    }

    @Test
    void secretariaCreaConductorEnSuEmpresa() {
        stubSecretaryOf(COMPANY_A);
        stubActiveCompany(COMPANY_A);
        stubSavedDriver();

        DriverResponse response = useCase.execute(COMPANY_A, request());

        ArgumentCaptor<Driver> captor = ArgumentCaptor.forClass(Driver.class);
        verify(driverService).save(captor.capture());
        assertEquals(COMPANY_A, captor.getValue().getCompanyId());
        assertEquals(DriverStatus.ACTIVE, captor.getValue().getDriverState());
        assertEquals(88L, response.getDriverId());
    }

    @Test
    void secretariaDeEmpresaANoPuedeCrearConductorEnLaEmpresaB() {
        when(userService.findPlatformRoleNames(SECRETARY_USER_ID)).thenReturn(List.of());
        when(userService.findCompanyRoleNames(SECRETARY_USER_ID, COMPANY_B)).thenReturn(List.of());

        assertThrows(ForbiddenOperationException.class, () -> useCase.execute(COMPANY_B, request()));

        verify(driverService, never()).save(any());
    }

    @Test
    void socioPuedeSerSimultaneamenteConductor() {
        stubSecretaryOf(COMPANY_A);
        stubActiveCompany(COMPANY_A);
        DriverRequest request = request();
        request.setUserId(LINKED_USER_ID);
        request.setPersonPartnerId(PERSON_PARTNER_ID);
        stubActiveMembership();
        when(driverService.existsByCompanyIdAndUserId(COMPANY_A, LINKED_USER_ID)).thenReturn(false);
        when(partnerService.findByCompanyIdAndPartnerId(COMPANY_A, PERSON_PARTNER_ID))
                .thenReturn(partner(PERSON_PARTNER_ID, PartnerType.PERSON));
        when(driverService.existsByCompanyIdAndPersonPartnerId(COMPANY_A, PERSON_PARTNER_ID)).thenReturn(false);
        stubSavedDriver();

        DriverResponse response = useCase.execute(COMPANY_A, request);

        ArgumentCaptor<Driver> captor = ArgumentCaptor.forClass(Driver.class);
        verify(driverService).save(captor.capture());
        assertEquals(LINKED_USER_ID, captor.getValue().getUserId());
        assertEquals(PERSON_PARTNER_ID, captor.getValue().getPersonPartnerId());
        assertEquals(88L, response.getDriverId());
    }

    @Test
    void licenciaVencidaDeberiaDevolver400() {
        stubSecretaryOf(COMPANY_A);
        stubActiveCompany(COMPANY_A);
        DriverRequest request = request();
        request.setLicenseExpiration(LocalDate.now().minusDays(1));

        assertThrows(BadRequestException.class, () -> useCase.execute(COMPANY_A, request));

        verify(driverService, never()).save(any());
    }

    @Test
    void categoriaDeLicenciaInvalidaDeberiaDevolver400() {
        stubSecretaryOf(COMPANY_A);
        stubActiveCompany(COMPANY_A);
        DriverRequest request = request();
        request.setLicenseCategory("XYZ-9");

        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> useCase.execute(COMPANY_A, request));

        assertEquals(true, exception.getMessage().contains("license_category"));
        verify(driverService, never()).save(any());
    }

    @Test
    void cuentaYaVinculadaComoConductorDeberiaDevolver409() {
        stubSecretaryOf(COMPANY_A);
        stubActiveCompany(COMPANY_A);
        DriverRequest request = request();
        request.setUserId(LINKED_USER_ID);
        stubActiveMembership();
        when(driverService.existsByCompanyIdAndUserId(COMPANY_A, LINKED_USER_ID)).thenReturn(true);

        assertThrows(ConflictException.class, () -> useCase.execute(COMPANY_A, request));

        verify(driverService, never()).save(any());
    }

    @Test
    void personPartnerDeOtraEmpresaDeberiaDevolver404() {
        stubSecretaryOf(COMPANY_A);
        stubActiveCompany(COMPANY_A);
        DriverRequest request = request();
        request.setPersonPartnerId(OTHER_TENANT_PARTNER_ID);
        when(partnerService.findByCompanyIdAndPartnerId(COMPANY_A, OTHER_TENANT_PARTNER_ID))
                .thenThrow(new ResourceNotFoundException(
                        "Socio no encontrado con el id 999 en la empresa 1"));

        assertThrows(ResourceNotFoundException.class, () -> useCase.execute(COMPANY_A, request));

        verify(driverService, never()).save(any());
    }

    @Test
    void personPartnerDeTipoBusinessDeberiaDevolver400() {
        stubSecretaryOf(COMPANY_A);
        stubActiveCompany(COMPANY_A);
        DriverRequest request = request();
        request.setPersonPartnerId(BUSINESS_PARTNER_ID);
        when(partnerService.findByCompanyIdAndPartnerId(COMPANY_A, BUSINESS_PARTNER_ID))
                .thenReturn(partner(BUSINESS_PARTNER_ID, PartnerType.BUSINESS));

        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> useCase.execute(COMPANY_A, request));

        assertEquals(true, exception.getMessage().contains("PERSON"));
        verify(driverService, never()).save(any());
    }

    @Test
    void personPartnerYaVinculadoAOtroConductorDeberiaDevolver409() {
        stubSecretaryOf(COMPANY_A);
        stubActiveCompany(COMPANY_A);
        DriverRequest request = request();
        request.setPersonPartnerId(PERSON_PARTNER_ID);
        when(partnerService.findByCompanyIdAndPartnerId(COMPANY_A, PERSON_PARTNER_ID))
                .thenReturn(partner(PERSON_PARTNER_ID, PartnerType.PERSON));
        when(driverService.existsByCompanyIdAndPersonPartnerId(COMPANY_A, PERSON_PARTNER_ID)).thenReturn(true);

        assertThrows(ConflictException.class, () -> useCase.execute(COMPANY_A, request));

        verify(driverService, never()).save(any());
    }

    @Test
    void licenciaValidaConCategoriaNormalizadaDeberiaGuardarse() {
        stubSecretaryOf(COMPANY_A);
        stubActiveCompany(COMPANY_A);
        DriverRequest request = request();
        request.setLicenseCategory(" b2 ");
        request.setLicenseExpiration(LocalDate.now().plusYears(2));
        stubSavedDriver();

        useCase.execute(COMPANY_A, request);

        ArgumentCaptor<Driver> captor = ArgumentCaptor.forClass(Driver.class);
        verify(driverService).save(captor.capture());
        assertEquals("B2", captor.getValue().getLicenseCategory());
        assertNull(captor.getValue().getUserId());
    }
}
