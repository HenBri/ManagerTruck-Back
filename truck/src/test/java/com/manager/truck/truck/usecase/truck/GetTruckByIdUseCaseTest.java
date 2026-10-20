package com.manager.truck.truck.usecase.truck;

import com.manager.truck.truck.domain.Company;
import com.manager.truck.truck.domain.Partner;
import com.manager.truck.truck.domain.Truck;
import com.manager.truck.truck.domain.dto.response.TruckResponse;
import com.manager.truck.truck.domain.enums.CompanyStatus;
import com.manager.truck.truck.domain.enums.PartnerStatus;
import com.manager.truck.truck.domain.enums.PartnerType;
import com.manager.truck.truck.domain.enums.TruckStatus;
import com.manager.truck.truck.domain.enums.UserStatus;
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
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetTruckByIdUseCaseTest {

    private static final Long SECRETARY_USER_ID = 10L;
    private static final Long PARTNER_USER_ID = 20L;
    private static final Long COMPANY_A = 1L;
    private static final Long COMPANY_B = 2L;
    private static final Long PARTNER_A_ID = 100L;
    private static final Long PARTNER_OTHER_ID = 200L;
    private static final Long TRUCK_ID = 55L;

    @Mock
    private TruckService truckService;
    @Mock
    private PartnerService partnerService;
    @Mock
    private CompanyService companyService;
    @Mock
    private UserService userService;

    private CurrentUserProvider currentUserProvider;

    private GetTruckByIdUseCase useCase;

    @BeforeEach
    void setUp() {
        currentUserProvider = new CurrentUserProvider(userService);
        useCase = new GetTruckByIdUseCase(truckService, partnerService, companyService, currentUserProvider);
        authenticate(PARTNER_USER_ID);
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

    private Partner partner(Long partnerId, Long userId) {
        Partner partner = new Partner();
        partner.setPartnerId(partnerId);
        partner.setCompanyId(COMPANY_A);
        partner.setUserId(userId);
        partner.setPartnerType(PartnerType.PERSON);
        partner.setPartnerName("Socio " + partnerId);
        partner.setPartnerState(PartnerStatus.ACTIVE);
        return partner;
    }

    private Truck truckOf(Long ownerPartnerId) {
        Truck truck = new Truck();
        truck.setTruckId(TRUCK_ID);
        truck.setCompanyId(COMPANY_A);
        truck.setOwnerPartnerId(ownerPartnerId);
        truck.setTruckBoard("TRK-55");
        truck.setTruckState(TruckStatus.ACTIVE);
        return truck;
    }

    private void stubActiveMemberOf(Long companyId) {
        when(userService.findPlatformRoleNames(PARTNER_USER_ID)).thenReturn(List.of());
        when(userService.isActiveMember(PARTNER_USER_ID, companyId)).thenReturn(true);
    }

    private void stubPartnerRoleOf(Long companyId) {
        when(userService.findCompanyRoleNames(PARTNER_USER_ID, companyId)).thenReturn(List.of("PARTNER"));
    }

    @Test
    void socioObtieneSuPropioCamion() {
        stubActiveMemberOf(COMPANY_A);
        stubPartnerRoleOf(COMPANY_A);
        when(companyService.findById(COMPANY_A)).thenReturn(company(COMPANY_A));
        when(truckService.findByCompanyIdAndTruckId(COMPANY_A, TRUCK_ID)).thenReturn(truckOf(PARTNER_A_ID));
        when(partnerService.findByCompanyIdAndUserId(COMPANY_A, PARTNER_USER_ID))
                .thenReturn(Optional.of(partner(PARTNER_A_ID, PARTNER_USER_ID)));

        TruckResponse response = useCase.execute(COMPANY_A, TRUCK_ID);

        assertEquals(TRUCK_ID, response.getTruckId());
        assertEquals(PARTNER_A_ID, response.getOwnerPartnerId());
    }

    @Test
    void socioNoPuedeVerCamionDeOtroSocioDelMismoTenant() {
        stubActiveMemberOf(COMPANY_A);
        stubPartnerRoleOf(COMPANY_A);
        when(companyService.findById(COMPANY_A)).thenReturn(company(COMPANY_A));
        when(truckService.findByCompanyIdAndTruckId(COMPANY_A, TRUCK_ID)).thenReturn(truckOf(PARTNER_OTHER_ID));
        when(partnerService.findByCompanyIdAndUserId(COMPANY_A, PARTNER_USER_ID))
                .thenReturn(Optional.of(partner(PARTNER_A_ID, PARTNER_USER_ID)));

        assertThrows(ForbiddenOperationException.class, () -> useCase.execute(COMPANY_A, TRUCK_ID));
    }

    @Test
    void socioSinPerfilPropioNoPuedeVerCamiones() {
        stubActiveMemberOf(COMPANY_A);
        stubPartnerRoleOf(COMPANY_A);
        when(companyService.findById(COMPANY_A)).thenReturn(company(COMPANY_A));
        when(truckService.findByCompanyIdAndTruckId(COMPANY_A, TRUCK_ID)).thenReturn(truckOf(PARTNER_OTHER_ID));
        when(partnerService.findByCompanyIdAndUserId(COMPANY_A, PARTNER_USER_ID)).thenReturn(Optional.empty());

        assertThrows(ForbiddenOperationException.class, () -> useCase.execute(COMPANY_A, TRUCK_ID));
    }

    @Test
    void secretariaObtieneCualquierCamionDeSuEmpresa() {
        authenticate(SECRETARY_USER_ID);
        when(userService.findPlatformRoleNames(SECRETARY_USER_ID)).thenReturn(List.of());
        when(userService.isActiveMember(SECRETARY_USER_ID, COMPANY_A)).thenReturn(true);
        when(userService.findCompanyRoleNames(SECRETARY_USER_ID, COMPANY_A)).thenReturn(List.of("SECRETARY"));
        when(companyService.findById(COMPANY_A)).thenReturn(company(COMPANY_A));
        when(truckService.findByCompanyIdAndTruckId(COMPANY_A, TRUCK_ID)).thenReturn(truckOf(PARTNER_OTHER_ID));

        TruckResponse response = useCase.execute(COMPANY_A, TRUCK_ID);

        assertEquals(PARTNER_OTHER_ID, response.getOwnerPartnerId());
        verify(partnerService, never()).findByCompanyIdAndUserId(COMPANY_A, SECRETARY_USER_ID);
    }

    @Test
    void camionDeOtraEmpresaDeberiaDevolver404() {
        stubActiveMemberOf(COMPANY_A);
        when(companyService.findById(COMPANY_A)).thenReturn(company(COMPANY_A));
        when(truckService.findByCompanyIdAndTruckId(COMPANY_A, TRUCK_ID))
                .thenThrow(new ResourceNotFoundException("Camión no encontrado con el id 55 en la empresa 1"));

        assertThrows(ResourceNotFoundException.class, () -> useCase.execute(COMPANY_A, TRUCK_ID));
    }

    @Test
    void usuarioDeOtraEmpresaDeberiaSerRechazado() {
        when(userService.findPlatformRoleNames(PARTNER_USER_ID)).thenReturn(List.of());
        when(userService.isActiveMember(PARTNER_USER_ID, COMPANY_B)).thenReturn(false);

        assertThrows(ForbiddenOperationException.class, () -> useCase.execute(COMPANY_B, TRUCK_ID));

        verify(truckService, never()).findByCompanyIdAndTruckId(COMPANY_B, TRUCK_ID);
    }
}
