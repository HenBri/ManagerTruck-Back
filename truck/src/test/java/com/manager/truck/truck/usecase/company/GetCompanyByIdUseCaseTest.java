package com.manager.truck.truck.usecase.company;

import com.manager.truck.truck.domain.Company;
import com.manager.truck.truck.domain.dto.response.CompanyResponse;
import com.manager.truck.truck.domain.enums.CompanyStatus;
import com.manager.truck.truck.exception.ForbiddenOperationException;
import com.manager.truck.truck.security.CurrentUserProvider;
import com.manager.truck.truck.service.CompanyService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetCompanyByIdUseCaseTest {

    @Mock
    private CompanyService companyService;

    @Mock
    private CurrentUserProvider currentUserProvider;

    @InjectMocks
    private GetCompanyByIdUseCase getCompanyByIdUseCase;

    private Company company() {
        Company company = new Company();
        company.setCompanyId(1L);
        company.setCompanyName("Transportes Andes");
        company.setCompanyStatus(CompanyStatus.ACTIVE);
        return company;
    }

    @Test
    void debeRechazarAUsuarioQueNoEsMiembroNiSuperAdmin() {
        when(currentUserProvider.isSuperAdmin()).thenReturn(false);
        when(currentUserProvider.isActiveMemberOf(1L)).thenReturn(false);

        assertThrows(ForbiddenOperationException.class, () -> getCompanyByIdUseCase.execute(1L));
    }

    @Test
    void debePermitirALosMiembrosActivos() {
        when(currentUserProvider.isSuperAdmin()).thenReturn(false);
        when(currentUserProvider.isActiveMemberOf(1L)).thenReturn(true);
        when(companyService.findById(1L)).thenReturn(company());

        CompanyResponse response = getCompanyByIdUseCase.execute(1L);

        assertEquals(1L, response.getCompanyId());
        assertEquals("Transportes Andes", response.getName());
    }

    @Test
    void debePermitirALSuperAdminSinSerMiembro() {
        when(currentUserProvider.isSuperAdmin()).thenReturn(true);
        when(companyService.findById(1L)).thenReturn(company());

        CompanyResponse response = getCompanyByIdUseCase.execute(1L);

        assertEquals(1L, response.getCompanyId());
        verify(companyService).findById(1L);
    }
}
