package com.manager.truck.truck.usecase.container;

import com.manager.truck.truck.domain.Company;
import com.manager.truck.truck.domain.Container;
import com.manager.truck.truck.domain.dto.request.ContainerUpdateRequest;
import com.manager.truck.truck.domain.dto.response.ContainerResponse;
import com.manager.truck.truck.domain.enums.CompanyStatus;
import com.manager.truck.truck.domain.enums.ContainerState;
import com.manager.truck.truck.domain.enums.UserStatus;
import com.manager.truck.truck.exception.ConflictException;
import com.manager.truck.truck.exception.ForbiddenOperationException;
import com.manager.truck.truck.security.CurrentUserProvider;
import com.manager.truck.truck.security.UserPrincipal;
import com.manager.truck.truck.service.CompanyService;
import com.manager.truck.truck.service.ContainerService;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateContainerUseCaseTest {

    private static final Long SECRETARY_USER_ID = 10L;
    private static final Long COMPANY_A = 1L;
    private static final Long COMPANY_B = 2L;

    @Mock
    private ContainerService containerService;
    @Mock
    private CompanyService companyService;
    @Mock
    private UserService userService;

    private CurrentUserProvider currentUserProvider;
    private UpdateContainerUseCase useCase;

    @BeforeEach
    void setUp() {
        currentUserProvider = new CurrentUserProvider(userService);
        useCase = new UpdateContainerUseCase(containerService, companyService, currentUserProvider);
        UserPrincipal principal = new UserPrincipal(SECRETARY_USER_ID, "cuenta@mail.com", "hash",
                UserStatus.ACTIVE, List.of());
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities()));
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    private void stubSecretaryOf(Long companyId) {
        when(userService.findPlatformRoleNames(SECRETARY_USER_ID)).thenReturn(List.of());
        when(userService.findCompanyRoleNames(SECRETARY_USER_ID, companyId)).thenReturn(List.of("SECRETARY"));
    }

    private Company company(Long id) {
        Company company = new Company();
        company.setCompanyId(id);
        company.setCompanyStatus(CompanyStatus.ACTIVE);
        return company;
    }

    private Container container(Long id, String code) {
        Container container = new Container();
        container.setContainerId(id);
        container.setCompanyId(COMPANY_A);
        container.setChargeId(300L);
        container.setContainerCode(code);
        container.setContainerState(ContainerState.AVAILABLE);
        return container;
    }

    private ContainerUpdateRequest request(String code, String weight) {
        ContainerUpdateRequest request = new ContainerUpdateRequest();
        request.setContainerCode(code);
        if (weight != null) {
            request.setContainerWeight(new BigDecimal(weight));
        }
        return request;
    }

    @Test
    void actualizacionDeberiaFuncionarConElMismoCodigo() {
        stubSecretaryOf(COMPANY_A);
        when(companyService.findById(COMPANY_A)).thenReturn(company(COMPANY_A));
        when(containerService.findByCompanyIdAndContainerId(COMPANY_A, 1L))
                .thenReturn(container(1L, "CONT-01"));
        when(containerService.save(any(Container.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ContainerResponse response = useCase.execute(COMPANY_A, 1L, request("CONT-01", "900.000"));

        ArgumentCaptor<Container> captor = ArgumentCaptor.forClass(Container.class);
        verify(containerService).save(captor.capture());
        assertEquals("CONT-01", captor.getValue().getContainerCode());
        assertEquals(new BigDecimal("900.000"), captor.getValue().getContainerWeight());
        assertEquals(ContainerState.AVAILABLE, captor.getValue().getContainerState());
        assertEquals(response.getContainerState(), captor.getValue().getContainerState());
    }

    @Test
    void cambioDeCodigoDuplicadoDeberiaDevolver409() {
        stubSecretaryOf(COMPANY_A);
        when(companyService.findById(COMPANY_A)).thenReturn(company(COMPANY_A));
        when(containerService.findByCompanyIdAndContainerId(COMPANY_A, 1L))
                .thenReturn(container(1L, "CONT-01"));
        when(containerService.existsByCompanyIdAndContainerCodeNot(COMPANY_A, "CONT-OTRO", 1L)).thenReturn(true);

        assertThrows(ConflictException.class,
                () -> useCase.execute(COMPANY_A, 1L, request("CONT-OTRO", null)));

        verify(containerService, never()).save(any());
    }

    @Test
    void cambioDeCodigoLibreDeberiaFuncionar() {
        stubSecretaryOf(COMPANY_A);
        when(companyService.findById(COMPANY_A)).thenReturn(company(COMPANY_A));
        when(containerService.findByCompanyIdAndContainerId(COMPANY_A, 1L))
                .thenReturn(container(1L, "CONT-01"));
        when(containerService.existsByCompanyIdAndContainerCodeNot(COMPANY_A, "CONT-NUEVO", 1L)).thenReturn(false);
        when(containerService.save(any(Container.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ContainerResponse response = useCase.execute(COMPANY_A, 1L, request("CONT-NUEVO", null));

        assertEquals("CONT-NUEVO", response.getContainerCode());
        verify(containerService).existsByCompanyIdAndContainerCodeNot(eq(COMPANY_A), eq("CONT-NUEVO"), eq(1L));
    }

    @Test
    void secretariaDeEmpresaANoPuedeActualizarEnLaEmpresaB() {
        when(userService.findPlatformRoleNames(SECRETARY_USER_ID)).thenReturn(List.of());
        when(userService.findCompanyRoleNames(SECRETARY_USER_ID, COMPANY_B)).thenReturn(List.of());

        assertThrows(ForbiddenOperationException.class,
                () -> useCase.execute(COMPANY_B, 1L, request("CONT-X", null)));

        verify(containerService, never()).save(any());
        verify(containerService, never()).existsByCompanyIdAndContainerCodeNot(anyLong(), any(), anyLong());
    }
}
