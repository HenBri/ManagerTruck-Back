package com.manager.truck.truck.usecase.container;

import com.manager.truck.truck.domain.Company;
import com.manager.truck.truck.domain.Container;
import com.manager.truck.truck.domain.dto.request.ContainerStatusRequest;
import com.manager.truck.truck.domain.dto.response.ContainerResponse;
import com.manager.truck.truck.domain.enums.CompanyStatus;
import com.manager.truck.truck.domain.enums.ContainerState;
import com.manager.truck.truck.domain.enums.UserStatus;
import com.manager.truck.truck.exception.ConflictException;
import com.manager.truck.truck.exception.ForbiddenOperationException;
import com.manager.truck.truck.exception.ResourceNotFoundException;
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

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateContainerStatusUseCaseTest {

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
    private UpdateContainerStatusUseCase useCase;

    @BeforeEach
    void setUp() {
        currentUserProvider = new CurrentUserProvider(userService);
        useCase = new UpdateContainerStatusUseCase(containerService, companyService, currentUserProvider);
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

    private Container container(Long id, ContainerState state) {
        Container container = new Container();
        container.setContainerId(id);
        container.setCompanyId(COMPANY_A);
        container.setChargeId(300L);
        container.setContainerCode("CONT-" + id);
        container.setContainerState(state);
        return container;
    }

    private ContainerStatusRequest request(ContainerState state) {
        ContainerStatusRequest request = new ContainerStatusRequest();
        request.setStatus(state);
        return request;
    }

    private void stubContainer(ContainerState current) {
        stubSecretaryOf(COMPANY_A);
        when(companyService.findById(COMPANY_A)).thenReturn(company(COMPANY_A));
        when(containerService.findByCompanyIdAndContainerId(COMPANY_A, 1L))
                .thenReturn(container(1L, current));
        when(containerService.save(any(Container.class))).thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void availablePuedeCargarse() {
        stubContainer(ContainerState.AVAILABLE);

        ContainerResponse response = useCase.execute(COMPANY_A, 1L, request(ContainerState.LOADED));

        ArgumentCaptor<Container> captor = ArgumentCaptor.forClass(Container.class);
        verify(containerService).save(captor.capture());
        assertEquals(ContainerState.LOADED, captor.getValue().getContainerState());
        assertEquals(ContainerState.LOADED, response.getContainerState());
    }

    @Test
    void loadedPuedeIniciarTransito() {
        stubContainer(ContainerState.LOADED);

        ContainerResponse response = useCase.execute(COMPANY_A, 1L, request(ContainerState.IN_TRANSIT));

        assertEquals(ContainerState.IN_TRANSIT, response.getContainerState());
    }

    @Test
    void inTransitPuedeEntregarse() {
        stubContainer(ContainerState.IN_TRANSIT);

        ContainerResponse response = useCase.execute(COMPANY_A, 1L, request(ContainerState.DELIVERED));

        assertEquals(ContainerState.DELIVERED, response.getContainerState());
    }

    @Test
    void availableNoPuedeSaltarseACargarEnTransito() {
        stubSecretaryOf(COMPANY_A);
        when(companyService.findById(COMPANY_A)).thenReturn(company(COMPANY_A));
        when(containerService.findByCompanyIdAndContainerId(COMPANY_A, 1L))
                .thenReturn(container(1L, ContainerState.AVAILABLE));

        assertThrows(ConflictException.class,
                () -> useCase.execute(COMPANY_A, 1L, request(ContainerState.IN_TRANSIT)));

        verify(containerService, never()).save(any());
    }

    @Test
    void deliveredEsEstadoTerminal() {
        stubSecretaryOf(COMPANY_A);
        when(companyService.findById(COMPANY_A)).thenReturn(company(COMPANY_A));
        when(containerService.findByCompanyIdAndContainerId(COMPANY_A, 1L))
                .thenReturn(container(1L, ContainerState.DELIVERED));

        assertThrows(ConflictException.class,
                () -> useCase.execute(COMPANY_A, 1L, request(ContainerState.AVAILABLE)));

        verify(containerService, never()).save(any());
    }

    @Test
    void mismoEstadoDeberiaDevolver409() {
        stubSecretaryOf(COMPANY_A);
        when(companyService.findById(COMPANY_A)).thenReturn(company(COMPANY_A));
        when(containerService.findByCompanyIdAndContainerId(COMPANY_A, 1L))
                .thenReturn(container(1L, ContainerState.AVAILABLE));

        assertThrows(ConflictException.class,
                () -> useCase.execute(COMPANY_A, 1L, request(ContainerState.AVAILABLE)));

        verify(containerService, never()).save(any());
    }

    @Test
    void contenedorDeOtraEmpresaDeberiaDevolver404() {
        stubSecretaryOf(COMPANY_A);
        when(companyService.findById(COMPANY_A)).thenReturn(company(COMPANY_A));
        when(containerService.findByCompanyIdAndContainerId(COMPANY_A, 99L))
                .thenThrow(new ResourceNotFoundException("Contenedor no encontrado"));

        assertThrows(ResourceNotFoundException.class,
                () -> useCase.execute(COMPANY_A, 99L, request(ContainerState.LOADED)));
    }

    @Test
    void secretariaDeEmpresaANoPuedeCambiarEstadoEnLaEmpresaB() {
        when(userService.findPlatformRoleNames(SECRETARY_USER_ID)).thenReturn(List.of());
        when(userService.findCompanyRoleNames(SECRETARY_USER_ID, COMPANY_B)).thenReturn(List.of());

        assertThrows(ForbiddenOperationException.class,
                () -> useCase.execute(COMPANY_B, 1L, request(ContainerState.LOADED)));

        verify(containerService, never()).save(any());
    }
}
