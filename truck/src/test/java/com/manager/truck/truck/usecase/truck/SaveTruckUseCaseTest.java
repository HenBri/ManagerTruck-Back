package com.manager.truck.truck.usecase.truck;

import com.manager.truck.truck.domain.Company;
import com.manager.truck.truck.domain.Partner;
import com.manager.truck.truck.domain.Truck;
import com.manager.truck.truck.domain.dto.request.TruckRequest;
import com.manager.truck.truck.domain.dto.response.TruckResponse;
import com.manager.truck.truck.domain.enums.CompanyStatus;
import com.manager.truck.truck.domain.enums.PartnerStatus;
import com.manager.truck.truck.domain.enums.PartnerType;
import com.manager.truck.truck.domain.enums.TruckStatus;
import com.manager.truck.truck.domain.enums.UserStatus;
import com.manager.truck.truck.exception.BadRequestException;
import com.manager.truck.truck.exception.ConflictException;
import com.manager.truck.truck.exception.ForbiddenOperationException;
import com.manager.truck.truck.exception.ResourceNotFoundException;
import com.manager.truck.truck.security.CurrentUserProvider;
import com.manager.truck.truck.security.UserPrincipal;
import com.manager.truck.truck.service.CompanyService;
import com.manager.truck.truck.service.PartnerService;
import com.manager.truck.truck.service.TruckService;
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

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SaveTruckUseCaseTest {

    private static final Long SECRETARY_USER_ID = 10L;
    private static final Long COMPANY_A = 1L;
    private static final Long COMPANY_B = 2L;
    private static final Long PARTNER_A_ID = 100L;

    @Mock
    private TruckService truckService;
    @Mock
    private PartnerService partnerService;
    @Mock
    private CompanyService companyService;
    @Mock
    private UserService userService;

    private CurrentUserProvider currentUserProvider;

    private SaveTruckUseCase useCase;

    @BeforeEach
    void setUp() {
        currentUserProvider = new CurrentUserProvider(userService);
        useCase = new SaveTruckUseCase(truckService, partnerService, companyService, currentUserProvider);
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

    private void stubSuperAdmin() {
        when(userService.findPlatformRoleNames(SECRETARY_USER_ID)).thenReturn(List.of("SUPER_ADMIN"));
    }

    private Company company(Long id, CompanyStatus status) {
        Company company = new Company();
        company.setCompanyId(id);
        company.setCompanyName("Empresa " + id);
        company.setCompanyStatus(status);
        return company;
    }

    private Partner partner(Long partnerId, Long companyId, PartnerStatus state) {
        Partner partner = new Partner();
        partner.setPartnerId(partnerId);
        partner.setCompanyId(companyId);
        partner.setPartnerType(PartnerType.PERSON);
        partner.setPartnerName("Socio");
        partner.setPartnerState(state);
        return partner;
    }

    private TruckRequest request(Long ownerPartnerId, String board) {
        TruckRequest request = new TruckRequest();
        request.setOwnerPartnerId(ownerPartnerId);
        request.setTruckBoard(board);
        request.setTruckCapacity(new BigDecimal("10.500"));
        return request;
    }

    @Test
    void secretariaCreaCamionConSocioDeSuEmpresa() {
        stubSecretaryOf(COMPANY_A);
        when(companyService.findById(COMPANY_A)).thenReturn(company(COMPANY_A, CompanyStatus.ACTIVE));
        when(partnerService.findByCompanyIdAndPartnerId(COMPANY_A, PARTNER_A_ID))
                .thenReturn(partner(PARTNER_A_ID, COMPANY_A, PartnerStatus.ACTIVE));
        when(truckService.existsByCompanyIdAndTruckBoard(COMPANY_A, "ABC-123")).thenReturn(false);
        when(truckService.save(any(Truck.class))).thenAnswer(invocation -> {
            Truck truck = invocation.getArgument(0);
            truck.setTruckId(55L);
            return truck;
        });

        TruckResponse response = useCase.execute(COMPANY_A, request(PARTNER_A_ID, "ABC-123"));

        ArgumentCaptor<Truck> captor = ArgumentCaptor.forClass(Truck.class);
        verify(truckService).save(captor.capture());
        assertEquals(COMPANY_A, captor.getValue().getCompanyId());
        assertEquals(PARTNER_A_ID, captor.getValue().getOwnerPartnerId());
        assertEquals(TruckStatus.ACTIVE, captor.getValue().getTruckState());
        assertEquals(55L, response.getTruckId());
    }

    @Test
    void secretariaDeEmpresaANoPuedeCrearCamionEnLaEmpresaB() {
        when(userService.findPlatformRoleNames(SECRETARY_USER_ID)).thenReturn(List.of());
        when(userService.findCompanyRoleNames(SECRETARY_USER_ID, COMPANY_B)).thenReturn(List.of());

        assertThrows(ForbiddenOperationException.class,
                () -> useCase.execute(COMPANY_B, request(PARTNER_A_ID, "ABC-123")));

        verify(truckService, never()).save(any());
    }

    @Test
    void superAdminPuedeCrearCamionEnCualquierEmpresa() {
        stubSuperAdmin();
        when(companyService.findById(COMPANY_B)).thenReturn(company(COMPANY_B, CompanyStatus.ACTIVE));
        when(partnerService.findByCompanyIdAndPartnerId(COMPANY_B, PARTNER_A_ID))
                .thenReturn(partner(PARTNER_A_ID, COMPANY_B, PartnerStatus.ACTIVE));
        when(truckService.existsByCompanyIdAndTruckBoard(COMPANY_B, "XYZ-999")).thenReturn(false);
        when(truckService.save(any(Truck.class))).thenAnswer(invocation -> invocation.getArgument(0));

        useCase.execute(COMPANY_B, request(PARTNER_A_ID, "XYZ-999"));

        verify(truckService).save(any(Truck.class));
    }

    @Test
    void camionSinSocioPropietarioDeberiaDevolver400() {
        stubSecretaryOf(COMPANY_A);
        when(companyService.findById(COMPANY_A)).thenReturn(company(COMPANY_A, CompanyStatus.ACTIVE));

        assertThrows(BadRequestException.class,
                () -> useCase.execute(COMPANY_A, request(null, "ABC-123")));

        verify(truckService, never()).save(any());
    }

    @Test
    void camionConSocioDeOtraEmpresaDeberiaDevolver404() {
        stubSecretaryOf(COMPANY_A);
        when(companyService.findById(COMPANY_A)).thenReturn(company(COMPANY_A, CompanyStatus.ACTIVE));
        when(partnerService.findByCompanyIdAndPartnerId(COMPANY_A, 999L))
                .thenThrow(new ResourceNotFoundException(
                        "Socio no encontrado con el id 999 en la empresa 1"));

        assertThrows(ResourceNotFoundException.class,
                () -> useCase.execute(COMPANY_A, request(999L, "ABC-123")));

        verify(truckService, never()).save(any());
    }

    @Test
    void camionConPlacaDuplicadaEnElTenantDeberiaDevolver409() {
        stubSecretaryOf(COMPANY_A);
        when(companyService.findById(COMPANY_A)).thenReturn(company(COMPANY_A, CompanyStatus.ACTIVE));
        when(partnerService.findByCompanyIdAndPartnerId(COMPANY_A, PARTNER_A_ID))
                .thenReturn(partner(PARTNER_A_ID, COMPANY_A, PartnerStatus.ACTIVE));
        when(truckService.existsByCompanyIdAndTruckBoard(COMPANY_A, "ABC-123")).thenReturn(true);

        assertThrows(ConflictException.class,
                () -> useCase.execute(COMPANY_A, request(PARTNER_A_ID, "ABC-123")));

        verify(truckService, never()).save(any());
    }

    @Test
    void camionParaSocioInactivoDeberiaDevolver409() {
        stubSecretaryOf(COMPANY_A);
        when(companyService.findById(COMPANY_A)).thenReturn(company(COMPANY_A, CompanyStatus.ACTIVE));
        when(partnerService.findByCompanyIdAndPartnerId(COMPANY_A, PARTNER_A_ID))
                .thenReturn(partner(PARTNER_A_ID, COMPANY_A, PartnerStatus.INACTIVE));

        assertThrows(ConflictException.class,
                () -> useCase.execute(COMPANY_A, request(PARTNER_A_ID, "ABC-123")));

        verify(truckService, never()).save(any());
    }

    @Test
    void camionEnEmpresaInactivaDeberiaDevolver409() {
        stubSecretaryOf(COMPANY_A);
        when(companyService.findById(COMPANY_A)).thenReturn(company(COMPANY_A, CompanyStatus.INACTIVE));

        assertThrows(ConflictException.class,
                () -> useCase.execute(COMPANY_A, request(PARTNER_A_ID, "ABC-123")));

        verify(truckService, never()).save(any());
    }
}
