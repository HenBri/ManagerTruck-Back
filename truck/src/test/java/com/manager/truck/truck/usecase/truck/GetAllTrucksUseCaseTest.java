package com.manager.truck.truck.usecase.truck;

import com.manager.truck.truck.domain.Company;
import com.manager.truck.truck.domain.Partner;
import com.manager.truck.truck.domain.Truck;
import com.manager.truck.truck.domain.dto.response.TrucksResponse;
import com.manager.truck.truck.domain.enums.CompanyStatus;
import com.manager.truck.truck.domain.enums.PartnerStatus;
import com.manager.truck.truck.domain.enums.PartnerType;
import com.manager.truck.truck.domain.enums.TruckStatus;
import com.manager.truck.truck.domain.enums.UserStatus;
import com.manager.truck.truck.exception.ForbiddenOperationException;
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
import org.springframework.data.domain.Page;
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
class GetAllTrucksUseCaseTest {

    private static final Long SECRETARY_USER_ID = 10L;
    private static final Long PARTNER_USER_ID = 20L;
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

    private GetAllTrucksUseCase useCase;

    private final Pageable pageable = PageRequest.of(0, 20);

    @BeforeEach
    void setUp() {
        currentUserProvider = new CurrentUserProvider(userService);
        useCase = new GetAllTrucksUseCase(truckService, partnerService, companyService, currentUserProvider);
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

    private Partner ownPartner() {
        Partner partner = new Partner();
        partner.setPartnerId(PARTNER_A_ID);
        partner.setCompanyId(COMPANY_A);
        partner.setUserId(PARTNER_USER_ID);
        partner.setPartnerType(PartnerType.PERSON);
        partner.setPartnerName("Socio Propio");
        partner.setPartnerState(PartnerStatus.ACTIVE);
        return partner;
    }

    private Truck truck(Long truckId, Long ownerPartnerId) {
        Truck truck = new Truck();
        truck.setTruckId(truckId);
        truck.setCompanyId(COMPANY_A);
        truck.setOwnerPartnerId(ownerPartnerId);
        truck.setTruckBoard("TRK-" + truckId);
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
    void secretariaListaTodosLosCamionesDeSuEmpresa() {
        authenticate(SECRETARY_USER_ID);
        when(userService.findPlatformRoleNames(SECRETARY_USER_ID)).thenReturn(List.of());
        when(userService.isActiveMember(SECRETARY_USER_ID, COMPANY_A)).thenReturn(true);
        when(userService.findCompanyRoleNames(SECRETARY_USER_ID, COMPANY_A)).thenReturn(List.of("SECRETARY"));
        when(companyService.findById(COMPANY_A)).thenReturn(company(COMPANY_A));
        when(truckService.findByCompanyId(eq(COMPANY_A), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(truck(1L, PARTNER_A_ID), truck(2L, 999L))));

        TrucksResponse response = useCase.execute(COMPANY_A, pageable);

        assertEquals(2, response.getTrucks().size());
        verify(truckService, never()).findByCompanyIdAndOwnerPartnerId(anyLong(), anyLong(), any());
    }

    @Test
    void socioListaSoloSusPropiosCamiones() {
        stubActiveMemberOf(COMPANY_A);
        stubPartnerRoleOf(COMPANY_A);
        when(companyService.findById(COMPANY_A)).thenReturn(company(COMPANY_A));
        when(partnerService.findByCompanyIdAndUserId(COMPANY_A, PARTNER_USER_ID))
                .thenReturn(Optional.of(ownPartner()));
        when(truckService.findByCompanyIdAndOwnerPartnerId(COMPANY_A, PARTNER_A_ID, pageable))
                .thenReturn(new PageImpl<>(List.of(truck(1L, PARTNER_A_ID))));

        TrucksResponse response = useCase.execute(COMPANY_A, pageable);

        assertEquals(1, response.getTrucks().size());
        assertEquals(PARTNER_A_ID, response.getTrucks().get(0).getOwnerPartnerId());
        verify(truckService, never()).findByCompanyId(anyLong(), any(Pageable.class));
    }

    @Test
    void socioSinCamionesRegistradosDeberiaDevolverListaVacia() {
        stubActiveMemberOf(COMPANY_A);
        stubPartnerRoleOf(COMPANY_A);
        when(companyService.findById(COMPANY_A)).thenReturn(company(COMPANY_A));
        when(partnerService.findByCompanyIdAndUserId(COMPANY_A, PARTNER_USER_ID))
                .thenReturn(Optional.empty());

        TrucksResponse response = useCase.execute(COMPANY_A, pageable);

        assertTrue(response.getTrucks().isEmpty());
        verify(truckService, never()).findByCompanyId(anyLong(), any(Pageable.class));
    }

    @Test
    void conductorDeberiaSerRechazadoAlListarCamiones() {
        stubActiveMemberOf(COMPANY_A);
        when(userService.findCompanyRoleNames(PARTNER_USER_ID, COMPANY_A)).thenReturn(List.of("DRIVER"));
        when(companyService.findById(COMPANY_A)).thenReturn(company(COMPANY_A));

        assertThrows(ForbiddenOperationException.class, () -> useCase.execute(COMPANY_A, pageable));

        verify(truckService, never()).findByCompanyId(anyLong(), any(Pageable.class));
    }

    @Test
    void usuarioSinMembresiaEnLaEmpresaDeberiaSerRechazado() {
        when(userService.findPlatformRoleNames(PARTNER_USER_ID)).thenReturn(List.of());
        when(userService.isActiveMember(PARTNER_USER_ID, COMPANY_B)).thenReturn(false);

        assertThrows(ForbiddenOperationException.class, () -> useCase.execute(COMPANY_B, pageable));

        verify(truckService, never()).findByCompanyId(anyLong(), any(Pageable.class));
    }
}
