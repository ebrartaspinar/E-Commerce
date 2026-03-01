package com.trendyolclone.user.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.trendyolclone.common.exception.GlobalExceptionHandler;
import com.trendyolclone.common.exception.ResourceNotFoundException;
import com.trendyolclone.user.application.dto.AddressRequest;
import com.trendyolclone.user.application.dto.AddressResponse;
import com.trendyolclone.user.application.service.AddressService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import com.trendyolclone.user.infrastructure.config.SecurityConfig;
import com.trendyolclone.user.infrastructure.config.JwtAuthenticationFilter;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        value = AddressController.class,
        excludeAutoConfiguration = SecurityAutoConfiguration.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = {SecurityConfig.class, JwtAuthenticationFilter.class}
        )
)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
@DisplayName("AddressController")
class AddressControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JpaMetamodelMappingContext jpaMetamodelMappingContext;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AddressService addressService;

    private final UUID userId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(userId, null, List.of(new SimpleGrantedAuthority("ROLE_BUYER")));
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Nested
    @DisplayName("GET /api/v1/users/me/addresses")
    class GetUserAddressesEndpoint {

        @Test
        @DisplayName("should return 200 OK with list of addresses")
        void shouldReturn200WithAddressList() throws Exception {
            // given
            UUID addressId1 = UUID.randomUUID();
            UUID addressId2 = UUID.randomUUID();
            List<AddressResponse> addresses = List.of(
                    new AddressResponse(addressId1, "Home", "123 Main St", "Istanbul", "Kadikoy", "34710", true),
                    new AddressResponse(addressId2, "Office", "456 Work Ave", "Istanbul", "Besiktas", "34340", false)
            );

            when(addressService.getUserAddresses(userId)).thenReturn(addresses);

            // when/then
            mockMvc.perform(get("/api/v1/users/me/addresses")
)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data").isArray())
                    .andExpect(jsonPath("$.data.length()").value(2))
                    .andExpect(jsonPath("$.data[0].title").value("Home"))
                    .andExpect(jsonPath("$.data[0].isDefault").value(true))
                    .andExpect(jsonPath("$.data[1].title").value("Office"))
                    .andExpect(jsonPath("$.data[1].isDefault").value(false));
        }

        @Test
        @DisplayName("should return 200 OK with empty list when user has no addresses")
        void shouldReturn200WithEmptyList() throws Exception {
            // given
            when(addressService.getUserAddresses(userId)).thenReturn(Collections.emptyList());

            // when/then
            mockMvc.perform(get("/api/v1/users/me/addresses")
)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data").isArray())
                    .andExpect(jsonPath("$.data.length()").value(0));
        }
    }

    @Nested
    @DisplayName("POST /api/v1/users/me/addresses")
    class AddAddressEndpoint {

        @Test
        @DisplayName("should return 201 CREATED with new address")
        void shouldReturn201WithNewAddress() throws Exception {
            // given
            AddressRequest request = new AddressRequest(
                    "Home", "123 Main Street, Apt 4", "Istanbul", "Kadikoy", "34710", true
            );
            UUID addressId = UUID.randomUUID();
            AddressResponse response = new AddressResponse(
                    addressId, "Home", "123 Main Street, Apt 4", "Istanbul", "Kadikoy", "34710", true
            );

            when(addressService.addAddress(eq(userId), any(AddressRequest.class))).thenReturn(response);

            // when/then
            mockMvc.perform(post("/api/v1/users/me/addresses")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.id").value(addressId.toString()))
                    .andExpect(jsonPath("$.data.title").value("Home"))
                    .andExpect(jsonPath("$.data.fullAddress").value("123 Main Street, Apt 4"))
                    .andExpect(jsonPath("$.data.city").value("Istanbul"))
                    .andExpect(jsonPath("$.data.district").value("Kadikoy"))
                    .andExpect(jsonPath("$.data.postalCode").value("34710"))
                    .andExpect(jsonPath("$.data.isDefault").value(true))
                    .andExpect(jsonPath("$.message").value("Address added successfully"));
        }

        @Test
        @DisplayName("should return 400 BAD_REQUEST when title is blank")
        void shouldReturn400WhenTitleBlank() throws Exception {
            // given
            String requestJson = """
                    {
                        "title": "",
                        "fullAddress": "123 Main Street",
                        "city": "Istanbul",
                        "district": "Kadikoy",
                        "postalCode": "34710",
                        "isDefault": false
                    }
                    """;

            // when/then
            mockMvc.perform(post("/api/v1/users/me/addresses")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestJson))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("should return 400 BAD_REQUEST when fullAddress is missing")
        void shouldReturn400WhenFullAddressMissing() throws Exception {
            // given
            String requestJson = """
                    {
                        "title": "Home",
                        "city": "Istanbul",
                        "district": "Kadikoy",
                        "isDefault": false
                    }
                    """;

            // when/then
            mockMvc.perform(post("/api/v1/users/me/addresses")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestJson))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("should return 400 BAD_REQUEST when city is blank")
        void shouldReturn400WhenCityBlank() throws Exception {
            // given
            String requestJson = """
                    {
                        "title": "Home",
                        "fullAddress": "123 Main Street",
                        "city": "",
                        "district": "Kadikoy",
                        "isDefault": false
                    }
                    """;

            // when/then
            mockMvc.perform(post("/api/v1/users/me/addresses")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestJson))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("should return 400 BAD_REQUEST when district is blank")
        void shouldReturn400WhenDistrictBlank() throws Exception {
            // given
            String requestJson = """
                    {
                        "title": "Home",
                        "fullAddress": "123 Main Street",
                        "city": "Istanbul",
                        "district": "",
                        "isDefault": false
                    }
                    """;

            // when/then
            mockMvc.perform(post("/api/v1/users/me/addresses")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestJson))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("PUT /api/v1/users/me/addresses/{id}")
    class UpdateAddressEndpoint {

        @Test
        @DisplayName("should return 200 OK with updated address")
        void shouldReturn200WithUpdatedAddress() throws Exception {
            // given
            UUID addressId = UUID.randomUUID();
            AddressRequest request = new AddressRequest(
                    "Updated Home", "999 Updated Street", "Ankara", "Cankaya", "06100", true
            );
            AddressResponse response = new AddressResponse(
                    addressId, "Updated Home", "999 Updated Street", "Ankara", "Cankaya", "06100", true
            );

            when(addressService.updateAddress(eq(userId), eq(addressId), any(AddressRequest.class)))
                    .thenReturn(response);

            // when/then
            mockMvc.perform(put("/api/v1/users/me/addresses/{id}", addressId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.title").value("Updated Home"))
                    .andExpect(jsonPath("$.data.city").value("Ankara"))
                    .andExpect(jsonPath("$.message").value("Address updated successfully"));
        }

        @Test
        @DisplayName("should return 404 NOT_FOUND when address does not exist")
        void shouldReturn404WhenAddressNotFound() throws Exception {
            // given
            UUID addressId = UUID.randomUUID();
            AddressRequest request = new AddressRequest(
                    "Home", "123 Street", "City", "District", "00000", false
            );

            when(addressService.updateAddress(eq(userId), eq(addressId), any(AddressRequest.class)))
                    .thenThrow(new ResourceNotFoundException("Address", "id", addressId));

            // when/then
            mockMvc.perform(put("/api/v1/users/me/addresses/{id}", addressId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("should return 400 BAD_REQUEST when validation fails on update")
        void shouldReturn400WhenValidationFailsOnUpdate() throws Exception {
            // given
            UUID addressId = UUID.randomUUID();
            String requestJson = """
                    {
                        "title": "",
                        "fullAddress": "",
                        "city": "",
                        "district": "",
                        "isDefault": false
                    }
                    """;

            // when/then
            mockMvc.perform(put("/api/v1/users/me/addresses/{id}", addressId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestJson))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("DELETE /api/v1/users/me/addresses/{id}")
    class DeleteAddressEndpoint {

        @Test
        @DisplayName("should return 200 OK on successful address deletion")
        void shouldReturn200OnSuccessfulDeletion() throws Exception {
            // given
            UUID addressId = UUID.randomUUID();
            doNothing().when(addressService).deleteAddress(userId, addressId);

            // when/then
            mockMvc.perform(delete("/api/v1/users/me/addresses/{id}", addressId)
)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.message").value("Address deleted successfully"));
        }

        @Test
        @DisplayName("should return 404 NOT_FOUND when deleting non-existent address")
        void shouldReturn404WhenDeletingNonExistentAddress() throws Exception {
            // given
            UUID addressId = UUID.randomUUID();
            doThrow(new ResourceNotFoundException("Address", "id", addressId))
                    .when(addressService).deleteAddress(userId, addressId);

            // when/then
            mockMvc.perform(delete("/api/v1/users/me/addresses/{id}", addressId)
)
                    .andExpect(status().isNotFound());
        }
    }
}
