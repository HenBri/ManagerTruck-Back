package com.manager.truck.truck.usecase.partner;

import com.manager.truck.truck.domain.Company;
import com.manager.truck.truck.domain.Partner;
import com.manager.truck.truck.domain.User;
import com.manager.truck.truck.domain.dto.request.PartnerRequest;
import com.manager.truck.truck.domain.enums.CompanyStatus;
import com.manager.truck.truck.domain.enums.PartnerType;
import com.manager.truck.truck.domain.enums.UserStatus;
import com.manager.truck.truck.security.CurrentUserProvider;
import com.manager.truck.truck.security.UserPrincipal;
import com.manager.truck.truck.service.CompanyAccountProvisioningService;
import com.manager.truck.truck.service.CompanyService;
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

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SavePartnerUseCaseTest {

    private static final Long COMPANY_ID = 1L;
    private static final Long ACCOUNT_ID = 30L;

    @Mock private PartnerService partnerService;
    @Mock private CompanyService companyService;
    @Mock private CompanyAccountProvisioningService accountProvisioningService;
    @Mock private UserService userService;

    private SavePartnerUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new SavePartnerUseCase(partnerService, companyService, accountProvisioningService,
                new CurrentUserProvider(userService));
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
    void socioPersonaProvisionaCuentaYReutilizaSusDatos() {
        PartnerRequest request = personRequest();
        User account = account();
        when(accountProvisioningService.provision(any(), anyString(), anyString(), anyString(), anyString(),
                any(), any(), anyString())).thenReturn(account);
        when(partnerService.save(any(Partner.class))).thenAnswer(invocation -> invocation.getArgument(0));

        useCase.execute(COMPANY_ID, request);

        ArgumentCaptor<Partner> partner = ArgumentCaptor.forClass(Partner.class);
        verify(partnerService).save(partner.capture());
        assertEquals(ACCOUNT_ID, partner.getValue().getUserId());
        assertEquals("Ana Perez", partner.getValue().getPartnerName());
        assertEquals("123", partner.getValue().getPartnerDocument());
        verify(accountProvisioningService).provision(COMPANY_ID, "ana@mail.com", "password1", "Ana", "Perez",
                "123", "70000000", "PARTNER");
    }

    @Test
    void socioEmpresaNoProvisionaCuenta() {
        PartnerRequest request = new PartnerRequest();
        request.setPartnerType(PartnerType.BUSINESS);
        request.setPartnerName("Transportes SRL");
        request.setPartnerDocument("NIT-1");
        when(partnerService.save(any(Partner.class))).thenAnswer(invocation -> invocation.getArgument(0));

        useCase.execute(COMPANY_ID, request);

        verify(accountProvisioningService, never()).provision(any(), any(), any(), any(), any(), any(), any(), any());
    }

    private PartnerRequest personRequest() {
        PartnerRequest request = new PartnerRequest();
        request.setPartnerType(PartnerType.PERSON);
        request.setEmail("ana@mail.com");
        request.setPassword("password1");
        request.setName("Ana");
        request.setLastname("Perez");
        request.setCi("123");
        request.setPhone("70000000");
        return request;
    }

    private User account() {
        User user = new User();
        user.setUserId(ACCOUNT_ID);
        user.setUserName("Ana");
        user.setUserLastname("Perez");
        user.setUserCi("123");
        user.setUserPhone("70000000");
        return user;
    }
}
