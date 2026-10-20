package com.manager.truck.truck.controller;

import com.manager.truck.truck.domain.dto.request.PartnerRequest;
import com.manager.truck.truck.domain.dto.request.PartnerStatusRequest;
import com.manager.truck.truck.domain.dto.request.DriverRequest;
import com.manager.truck.truck.domain.dto.request.TruckRequest;
import com.manager.truck.truck.domain.dto.response.DriverResponse;
import com.manager.truck.truck.domain.dto.response.DriversResponse;
import com.manager.truck.truck.domain.dto.response.PartnerResponse;
import com.manager.truck.truck.domain.dto.response.PartnersResponse;
import com.manager.truck.truck.domain.dto.response.TruckResponse;
import com.manager.truck.truck.domain.dto.response.TrucksResponse;
import com.manager.truck.truck.domain.enums.PartnerStatus;
import com.manager.truck.truck.domain.enums.PartnerType;
import com.manager.truck.truck.domain.enums.TruckStatus;
import com.manager.truck.truck.security.SecurityConfig;
import com.manager.truck.truck.usecase.driver.GetAllDriversUseCase;
import com.manager.truck.truck.usecase.driver.GetDriverByIdUseCase;
import com.manager.truck.truck.usecase.driver.SaveDriverUseCase;
import com.manager.truck.truck.usecase.driver.UpdateDriverStatusUseCase;
import com.manager.truck.truck.usecase.driver.UpdateDriverUseCase;
import com.manager.truck.truck.usecase.partner.GetAllPartnersUseCase;
import com.manager.truck.truck.usecase.partner.GetPartnerByIdUseCase;
import com.manager.truck.truck.usecase.partner.SavePartnerUseCase;
import com.manager.truck.truck.usecase.partner.UpdatePartnerStatusUseCase;
import com.manager.truck.truck.usecase.partner.UpdatePartnerUseCase;
import com.manager.truck.truck.usecase.truck.GetAllTrucksUseCase;
import com.manager.truck.truck.usecase.truck.GetTruckByIdUseCase;
import com.manager.truck.truck.usecase.truck.SaveTruckUseCase;
import com.manager.truck.truck.usecase.truck.UpdateTruckStatusUseCase;
import com.manager.truck.truck.usecase.truck.UpdateTruckUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {PartnerController.class, DriverController.class, TruckController.class})
@Import(SecurityConfig.class)
class PartnerDriverTruckSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GetAllPartnersUseCase getAllPartnersUseCase;
    @MockitoBean
    private GetPartnerByIdUseCase getPartnerByIdUseCase;
    @MockitoBean
    private SavePartnerUseCase savePartnerUseCase;
    @MockitoBean
    private UpdatePartnerUseCase updatePartnerUseCase;
    @MockitoBean
    private UpdatePartnerStatusUseCase updatePartnerStatusUseCase;
    @MockitoBean
    private GetAllDriversUseCase getAllDriversUseCase;
    @MockitoBean
    private GetDriverByIdUseCase getDriverByIdUseCase;
    @MockitoBean
    private SaveDriverUseCase saveDriverUseCase;
    @MockitoBean
    private UpdateDriverUseCase updateDriverUseCase;
    @MockitoBean
    private UpdateDriverStatusUseCase updateDriverStatusUseCase;
    @MockitoBean
    private GetAllTrucksUseCase getAllTrucksUseCase;
    @MockitoBean
    private GetTruckByIdUseCase getTruckByIdUseCase;
    @MockitoBean
    private SaveTruckUseCase saveTruckUseCase;
    @MockitoBean
    private UpdateTruckUseCase updateTruckUseCase;
    @MockitoBean
    private UpdateTruckStatusUseCase updateTruckStatusUseCase;

    private static SecurityMockMvcRequestPostProcessors.UserRequestPostProcessor withRole(String role) {
        return SecurityMockMvcRequestPostProcessors.user("tester")
                .authorities(new SimpleGrantedAuthority("ROLE_" + role));
    }

    // --- Partners ---

    @Test
    void listarSociosSinAutenticacionDeberiaDevolver401() throws Exception {
        mockMvc.perform(get("/api/companies/1/partners"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("401"));
    }

    @Test
    void listarSociosConPartnerDeberiaDevolver200() throws Exception {
        when(getAllPartnersUseCase.execute(anyLong(), any()))
                .thenReturn(new PartnersResponse(List.of(), 0, 20, 0, 0));

        mockMvc.perform(get("/api/companies/1/partners").with(withRole("PARTNER")))
                .andExpect(status().isOk());
    }

    @Test
    void listarSociosConDriverDeberiaDevolver200() throws Exception {
        when(getAllPartnersUseCase.execute(anyLong(), any()))
                .thenReturn(new PartnersResponse(List.of(), 0, 20, 0, 0));

        mockMvc.perform(get("/api/companies/1/partners").with(withRole("DRIVER")))
                .andExpect(status().isOk());
    }

    @Test
    void crearSocioConPartnerDeberiaDevolver403() throws Exception {
        mockMvc.perform(post("/api/companies/1/partners").with(withRole("PARTNER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"partnerType\":\"PERSON\",\"partnerName\":\"Socio\"}"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("403"));
    }

    @Test
    void crearSocioConDriverDeberiaDevolver403() throws Exception {
        mockMvc.perform(post("/api/companies/1/partners").with(withRole("DRIVER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"partnerType\":\"PERSON\",\"partnerName\":\"Socio\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void crearSocioConSecretaryDeberiaDevolver201() throws Exception {
        PartnerResponse response = new PartnerResponse();
        response.setPartnerId(1L);
        when(savePartnerUseCase.execute(anyLong(), any(PartnerRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/companies/1/partners").with(withRole("SECRETARY"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"partnerType\":\"PERSON\",\"partnerName\":\"Socio\"}"))
                .andExpect(status().isCreated());
    }

    @Test
    void actualizarSocioConPartnerDeberiaDevolver403() throws Exception {
        mockMvc.perform(put("/api/companies/1/partners/5").with(withRole("PARTNER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"partnerName\":\"Socio Editado\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void cambiarEstadoDeSocioConSecretaryDeberiaDevolver200() throws Exception {
        PartnerResponse response = new PartnerResponse();
        response.setPartnerId(5L);
        response.setPartnerState(PartnerStatus.INACTIVE);
        when(updatePartnerStatusUseCase.execute(anyLong(), anyLong(), any(PartnerStatusRequest.class)))
                .thenReturn(response);

        mockMvc.perform(put("/api/companies/1/partners/5/status").with(withRole("SECRETARY"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"INACTIVE\"}"))
                .andExpect(status().isOk());
    }

    // --- Drivers ---

    @Test
    void listarConductoresSinAutenticacionDeberiaDevolver401() throws Exception {
        mockMvc.perform(get("/api/companies/1/drivers"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("401"));
    }

    @Test
    void listarConductoresConDriverDeberiaDevolver200() throws Exception {
        when(getAllDriversUseCase.execute(anyLong(), any()))
                .thenReturn(new DriversResponse(List.of(), 0, 20, 0, 0));

        mockMvc.perform(get("/api/companies/1/drivers").with(withRole("DRIVER")))
                .andExpect(status().isOk());
    }

    @Test
    void verConductorPorIdConDriverDeberiaDevolver200() throws Exception {
        DriverResponse response = new DriverResponse();
        response.setDriverId(9L);
        when(getDriverByIdUseCase.execute(anyLong(), anyLong())).thenReturn(response);

        mockMvc.perform(get("/api/companies/1/drivers/9").with(withRole("DRIVER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.driverId").value(9));
    }

    @Test
    void crearConductorConDriverDeberiaDevolver403() throws Exception {
        mockMvc.perform(post("/api/companies/1/drivers").with(withRole("DRIVER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"driverName\":\"Chofer\",\"driverLicense\":\"LIC-1\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void crearConductorConSecretaryDeberiaDevolver201() throws Exception {
        DriverResponse response = new DriverResponse();
        response.setDriverId(9L);
        when(saveDriverUseCase.execute(anyLong(), any(DriverRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/companies/1/drivers").with(withRole("SECRETARY"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"driverName\":\"Chofer\",\"driverLicense\":\"LIC-1\"}"))
                .andExpect(status().isCreated());
    }

    @Test
    void actualizarConductorConPartnerDeberiaDevolver403() throws Exception {
        mockMvc.perform(put("/api/companies/1/drivers/9").with(withRole("PARTNER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"driverName\":\"Chofer\",\"driverLicense\":\"LIC-1\"}"))
                .andExpect(status().isForbidden());
    }

    // --- Trucks ---

    @Test
    void listarCamionesSinAutenticacionDeberiaDevolver401() throws Exception {
        mockMvc.perform(get("/api/companies/1/trucks"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("401"));
    }

    @Test
    void listarCamionesConDriverDeberiaDevolver403() throws Exception {
        mockMvc.perform(get("/api/companies/1/trucks").with(withRole("DRIVER")))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("403"));
    }

    @Test
    void verCamionPorIdConDriverDeberiaDevolver403() throws Exception {
        mockMvc.perform(get("/api/companies/1/trucks/7").with(withRole("DRIVER")))
                .andExpect(status().isForbidden());
    }

    @Test
    void listarCamionesConPartnerDeberiaDevolver200() throws Exception {
        when(getAllTrucksUseCase.execute(anyLong(), any()))
                .thenReturn(new TrucksResponse(List.of(), 0, 20, 0, 0));

        mockMvc.perform(get("/api/companies/1/trucks").with(withRole("PARTNER")))
                .andExpect(status().isOk());
    }

    @Test
    void listarCamionesConSecretaryDeberiaDevolver200() throws Exception {
        when(getAllTrucksUseCase.execute(anyLong(), any()))
                .thenReturn(new TrucksResponse(List.of(), 0, 20, 0, 0));

        mockMvc.perform(get("/api/companies/1/trucks").with(withRole("SECRETARY")))
                .andExpect(status().isOk());
    }

    @Test
    void crearCamionConPartnerDeberiaDevolver403() throws Exception {
        mockMvc.perform(post("/api/companies/1/trucks").with(withRole("PARTNER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"ownerPartnerId\":1,\"truckBoard\":\"ABC-123\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void crearCamionConDriverDeberiaDevolver403() throws Exception {
        mockMvc.perform(post("/api/companies/1/trucks").with(withRole("DRIVER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"ownerPartnerId\":1,\"truckBoard\":\"ABC-123\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void crearCamionConSecretaryDeberiaDevolver201() throws Exception {
        TruckResponse response = new TruckResponse();
        response.setTruckId(7L);
        response.setTruckState(TruckStatus.ACTIVE);
        when(saveTruckUseCase.execute(anyLong(), any(TruckRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/companies/1/trucks").with(withRole("SECRETARY"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"ownerPartnerId\":1,\"truckBoard\":\"ABC-123\"}"))
                .andExpect(status().isCreated());
    }

    @Test
    void actualizarCamionConPartnerDeberiaDevolver403() throws Exception {
        mockMvc.perform(put("/api/companies/1/trucks/7").with(withRole("PARTNER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"truckBoard\":\"ABC-123\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void cambiarEstadoDeCamionConPartnerDeberiaDevolver403() throws Exception {
        mockMvc.perform(put("/api/companies/1/trucks/7/status").with(withRole("PARTNER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"MAINTENANCE\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void crearCamionConSecretarySinSocioPropietarioDeberiaDevolver400() throws Exception {
        mockMvc.perform(post("/api/companies/1/trucks").with(withRole("SECRETARY"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"truckBoard\":\"ABC-123\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"));
    }

    @Test
    void crearConductorConDriverSinLicenciaDeberiaDevolver400() throws Exception {
        mockMvc.perform(post("/api/companies/1/drivers").with(withRole("SECRETARY"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"driverName\":\"Chofer\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"));
    }
}
