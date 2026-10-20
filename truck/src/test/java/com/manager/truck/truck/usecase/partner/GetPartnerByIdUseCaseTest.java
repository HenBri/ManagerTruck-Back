package com.manager.truck.truck.usecase.partner;

import com.manager.truck.truck.domain.Company;
import com.manager.truck.truck.domain.Partner;
import com.manager.truck.truck.domain.dto.response.PartnerResponse;
import com.manager.truck.truck.domain.enums.CompanyStatus;
import com.manager.truck.truck.domain.enums.PartnerStatus;
import com.manager.truck.truck.domain.enums.PartnerType;
import com.manager.truck.truck.domain.enums.UserStatus;
import com.manager.truck.truck.exception.ForbiddenOperationException;
import com.manager.truck.truck.exception.ResourceNotFoundException;
import com.manager.truck.truck.security.CurrentUserProvider;
import com.manager.truck.truck.security.UserPrincipal;
import com.manager.truck.truck.service.CompanyService;
import com.manager.truck.truck.service.PartnerService;
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

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetPartnerByIdUseCaseTest {

    private static final Long SECRETARY_USER_ID = 10L;
    private static final Long PARTNER_USER_ID = 20L;
    private static final Long COMPANY_A = 1L;
    private static final Long COMPANY_B = 2L;
    private static final Long PARTNER_A_ID = 100L;
    private static final Long PARTNER_OTHER_ID = 200L;

    @Mock
    private PartnerService partnerService;
    @Mock
    private CompanyService companyService;
    @Mock
    private UserService userService;

    private CurrentUserProvider currentUserProvider;

    private GetPartnerByIdUseCase useCase;

    @BeforeEach
    void setUp() {
        currentUserProvider = new CurrentUserProvider(userService);
        useCase = new GetPartnerByIdUseCase(partnerService, companyService, currentUserProvider);
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

    private void stubActiveMemberOf(Long companyId) {
        when(userService.findPlatformRoleNames(PARTNER_USER_ID)).thenReturn(List.of());
        when(userService.isActiveMember(PARTNER_USER_ID, companyId)).thenReturn(true);
    }

    @Test
    void socioConsultaSuPropioPerfil() {
        stubActiveMemberOf(COMPANY_A);
        when(userService.findCompanyRoleNames(PARTNER_USER_ID, COMPANY_A)).thenReturn(List.of("PARTNER"));
        when(companyService.findById(COMPANY_A)).thenReturn(company(COMPANY_A));
        when(partnerService.findByCompanyIdAndPartnerId(COMPANY_A, PARTNER_A_ID))
                .thenReturn(partner(PARTNER_A_ID, PARTNER_USER_ID));

        PartnerResponse response = useCase.execute(COMPANY_A, PARTNER_A_ID);

        org.junit.jupiter.api.Assertions.assertEquals(PARTNER_A_ID, response.getPartnerId());
    }

    @Test
    void socioNoPuedeConsultarElPerfilDeOtroSocio() {
        stubActiveMemberOf(COMPANY_A);
        when(userService.findCompanyRoleNames(PARTNER_USER_ID, COMPANY_A)).thenReturn(List.of("PARTNER"));
        when(companyService.findById(COMPANY_A)).thenReturn(company(COMPANY_A));
        when(partnerService.findByCompanyIdAndPartnerId(COMPANY_A, PARTNER_OTHER_ID))
                .thenReturn(partner(PARTNER_OTHER_ID, 999L));

        assertThrows(ForbiddenOperationException.class,
                () -> useCase.execute(COMPANY_A, PARTNER_OTHER_ID));
    }

    @Test
    void socioDeOtraEmpresaDeberiaSerRechazado() {
        when(userService.findPlatformRoleNames(PARTNER_USER_ID)).thenReturn(List.of());
        when(userService.isActiveMember(PARTNER_USER_ID, COMPANY_B)).thenReturn(false);

        assertThrows(ForbiddenOperationException.class, () -> useCase.execute(COMPANY_B, PARTNER_A_ID));

        verify(partnerService, never()).findByCompanyIdAndPartnerId(COMPANY_B, PARTNER_A_ID);
    }

    @Test
    void socioInexistenteEnElTenantDeberiaDevolver404() {
        stubActiveMemberOf(COMPANY_A);
        when(companyService.findById(COMPANY_A)).thenReturn(company(COMPANY_A));
        when(partnerService.findByCompanyIdAndPartnerId(COMPANY_A, 4044L))
                .thenThrow(new ResourceNotFoundException("Socio no encontrado con el id 4044 en la empresa 1"));

        assertThrows(ResourceNotFoundException.class, () -> useCase.execute(COMPANY_A, 4044L));
    }

    @Test
    void secretariaConsultaCualquierSocioDeSuEmpresa() {
        authenticate(SECRETARY_USER_ID);
        when(userService.findPlatformRoleNames(SECRETARY_USER_ID)).thenReturn(List.of());
        when(userService.isActiveMember(SECRETARY_USER_ID, COMPANY_A)).thenReturn(true);
        when(userService.findCompanyRoleNames(SECRETARY_USER_ID, COMPANY_A)).thenReturn(List.of("SECRETARY"));
        when(companyService.findById(COMPANY_A)).thenReturn(company(COMPANY_A));
        when(partnerService.findByCompanyIdAndPartnerId(COMPANY_A, PARTNER_OTHER_ID))
                .thenReturn(partner(PARTNER_OTHER_ID, 999L));

        PartnerResponse response = useCase.execute(COMPANY_A, PARTNER_OTHER_ID);

        org.junit.jupiter.api.Assertions.assertEquals(PARTNER_OTHER_ID, response.getPartnerId());
    }
}
