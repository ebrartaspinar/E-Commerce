package com.ecommerce.user.application.service;

import com.ecommerce.common.exception.ResourceNotFoundException;
import com.ecommerce.user.application.dto.AddressRequest;
import com.ecommerce.user.application.dto.AddressResponse;
import com.ecommerce.user.domain.model.Address;
import com.ecommerce.user.domain.model.User;
import com.ecommerce.user.domain.model.UserRole;
import com.ecommerce.user.domain.model.UserStatus;
import com.ecommerce.user.domain.repository.AddressRepository;
import com.ecommerce.user.domain.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AddressService")
class AddressServiceTest {

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AddressService addressService;

    private User testUser;
    private UUID userId;
    private Address testAddress;
    private UUID addressId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        addressId = UUID.randomUUID();

        testUser = User.builder()
                .email("test@example.com")
                .password("encoded-password")
                .firstName("John")
                .lastName("Doe")
                .role(UserRole.BUYER)
                .status(UserStatus.ACTIVE)
                .addresses(new ArrayList<>())
                .build();
        testUser.setId(userId);

        testAddress = Address.builder()
                .title("Home")
                .fullAddress("123 Main Street, Apt 4")
                .city("Istanbul")
                .district("Kadikoy")
                .postalCode("34710")
                .isDefault(true)
                .user(testUser)
                .build();
        testAddress.setId(addressId);
    }

    @Nested
    @DisplayName("getUserAddresses()")
    class GetUserAddresses {

        @Test
        @DisplayName("should return list of address responses for user")
        void shouldReturnAddressesForUser() {
            // given
            Address secondAddress = Address.builder()
                    .title("Office")
                    .fullAddress("456 Business Ave")
                    .city("Istanbul")
                    .district("Besiktas")
                    .postalCode("34340")
                    .isDefault(false)
                    .user(testUser)
                    .build();
            secondAddress.setId(UUID.randomUUID());

            when(addressRepository.findByUserId(userId)).thenReturn(List.of(testAddress, secondAddress));

            // when
            List<AddressResponse> responses = addressService.getUserAddresses(userId);

            // then
            assertThat(responses).hasSize(2);
            assertThat(responses.get(0).title()).isEqualTo("Home");
            assertThat(responses.get(0).isDefault()).isTrue();
            assertThat(responses.get(1).title()).isEqualTo("Office");
            assertThat(responses.get(1).isDefault()).isFalse();

            verify(addressRepository).findByUserId(userId);
        }

        @Test
        @DisplayName("should return empty list when user has no addresses")
        void shouldReturnEmptyListWhenNoAddresses() {
            // given
            when(addressRepository.findByUserId(userId)).thenReturn(Collections.emptyList());

            // when
            List<AddressResponse> responses = addressService.getUserAddresses(userId);

            // then
            assertThat(responses).isEmpty();
        }
    }

    @Nested
    @DisplayName("addAddress()")
    class AddAddress {

        @Test
        @DisplayName("should add a new address successfully")
        void shouldAddNewAddressSuccessfully() {
            // given
            AddressRequest request = new AddressRequest(
                    "Home", "123 Main Street", "Istanbul", "Kadikoy", "34710", false
            );

            when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
            when(addressRepository.save(any(Address.class))).thenAnswer(invocation -> {
                Address saved = invocation.getArgument(0);
                saved.setId(UUID.randomUUID());
                return saved;
            });

            // when
            AddressResponse response = addressService.addAddress(userId, request);

            // then
            assertThat(response.title()).isEqualTo("Home");
            assertThat(response.fullAddress()).isEqualTo("123 Main Street");
            assertThat(response.city()).isEqualTo("Istanbul");
            assertThat(response.district()).isEqualTo("Kadikoy");
            assertThat(response.postalCode()).isEqualTo("34710");
            assertThat(response.isDefault()).isFalse();

            verify(userRepository).findById(userId);
            verify(addressRepository).save(any(Address.class));
        }

        @Test
        @DisplayName("should unset existing default addresses when adding a new default address")
        void shouldUnsetExistingDefaultsWhenAddingNewDefault() {
            // given
            AddressRequest request = new AddressRequest(
                    "New Home", "789 New Street", "Ankara", "Cankaya", "06100", true
            );

            Address existingDefault = Address.builder()
                    .title("Old Home")
                    .isDefault(true)
                    .user(testUser)
                    .build();
            existingDefault.setId(UUID.randomUUID());

            when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
            when(addressRepository.findByUserId(userId)).thenReturn(List.of(existingDefault));
            when(addressRepository.save(any(Address.class))).thenAnswer(invocation -> {
                Address saved = invocation.getArgument(0);
                if (saved.getId() == null) {
                    saved.setId(UUID.randomUUID());
                }
                return saved;
            });

            // when
            AddressResponse response = addressService.addAddress(userId, request);

            // then
            assertThat(response.isDefault()).isTrue();
            assertThat(existingDefault.isDefault()).isFalse();

            // save called twice: once for unsetting old default, once for new address
            verify(addressRepository, times(2)).save(any(Address.class));
        }

        @Test
        @DisplayName("should not unset defaults when adding a non-default address")
        void shouldNotUnsetDefaultsWhenAddingNonDefault() {
            // given
            AddressRequest request = new AddressRequest(
                    "Office", "456 Work Street", "Istanbul", "Besiktas", "34340", false
            );

            when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
            when(addressRepository.save(any(Address.class))).thenAnswer(invocation -> {
                Address saved = invocation.getArgument(0);
                saved.setId(UUID.randomUUID());
                return saved;
            });

            // when
            addressService.addAddress(userId, request);

            // then
            verify(addressRepository, never()).findByUserId(userId);
            verify(addressRepository, times(1)).save(any(Address.class));
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException when user does not exist")
        void shouldThrowResourceNotFoundWhenUserDoesNotExist() {
            // given
            UUID nonExistentUserId = UUID.randomUUID();
            AddressRequest request = new AddressRequest(
                    "Home", "123 Street", "City", "District", "00000", false
            );

            when(userRepository.findById(nonExistentUserId)).thenReturn(Optional.empty());

            // when/then
            assertThatThrownBy(() -> addressService.addAddress(nonExistentUserId, request))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("User");

            verify(addressRepository, never()).save(any(Address.class));
        }

        @Test
        @DisplayName("should associate the new address with the correct user")
        void shouldAssociateAddressWithCorrectUser() {
            // given
            AddressRequest request = new AddressRequest(
                    "Home", "123 Street", "City", "District", "00000", false
            );

            when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
            when(addressRepository.save(any(Address.class))).thenAnswer(invocation -> {
                Address saved = invocation.getArgument(0);
                saved.setId(UUID.randomUUID());
                return saved;
            });

            // when
            addressService.addAddress(userId, request);

            // then
            ArgumentCaptor<Address> captor = ArgumentCaptor.forClass(Address.class);
            verify(addressRepository).save(captor.capture());
            assertThat(captor.getValue().getUser()).isEqualTo(testUser);
        }
    }

    @Nested
    @DisplayName("updateAddress()")
    class UpdateAddress {

        @Test
        @DisplayName("should update an existing address successfully")
        void shouldUpdateAddressSuccessfully() {
            // given
            AddressRequest request = new AddressRequest(
                    "Updated Home", "999 Updated Street", "Izmir", "Konak", "35000", false
            );

            when(addressRepository.findByUserIdAndId(userId, addressId)).thenReturn(Optional.of(testAddress));
            when(addressRepository.save(any(Address.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // when
            AddressResponse response = addressService.updateAddress(userId, addressId, request);

            // then
            assertThat(response.title()).isEqualTo("Updated Home");
            assertThat(response.fullAddress()).isEqualTo("999 Updated Street");
            assertThat(response.city()).isEqualTo("Izmir");
            assertThat(response.district()).isEqualTo("Konak");
            assertThat(response.postalCode()).isEqualTo("35000");

            verify(addressRepository).save(testAddress);
        }

        @Test
        @DisplayName("should unset other defaults when setting address as default")
        void shouldUnsetOtherDefaultsWhenSettingAsDefault() {
            // given
            testAddress.setDefault(false); // this address is not currently default

            Address otherDefault = Address.builder()
                    .title("Other")
                    .isDefault(true)
                    .user(testUser)
                    .build();
            otherDefault.setId(UUID.randomUUID());

            AddressRequest request = new AddressRequest(
                    "Home", "123 Main Street", "Istanbul", "Kadikoy", "34710", true
            );

            when(addressRepository.findByUserIdAndId(userId, addressId)).thenReturn(Optional.of(testAddress));
            when(addressRepository.findByUserId(userId)).thenReturn(List.of(otherDefault));
            when(addressRepository.save(any(Address.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // when
            AddressResponse response = addressService.updateAddress(userId, addressId, request);

            // then
            assertThat(response.isDefault()).isTrue();
            assertThat(otherDefault.isDefault()).isFalse();
        }

        @Test
        @DisplayName("should not unset defaults when address is already default and request keeps it default")
        void shouldNotUnsetDefaultsWhenAlreadyDefault() {
            // given - testAddress.isDefault() is true by default from setUp
            AddressRequest request = new AddressRequest(
                    "Home Updated", "123 Main Street", "Istanbul", "Kadikoy", "34710", true
            );

            when(addressRepository.findByUserIdAndId(userId, addressId)).thenReturn(Optional.of(testAddress));
            when(addressRepository.save(any(Address.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // when
            addressService.updateAddress(userId, addressId, request);

            // then
            verify(addressRepository, never()).findByUserId(userId); // should not query for other defaults
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException when address does not exist")
        void shouldThrowResourceNotFoundWhenAddressDoesNotExist() {
            // given
            UUID nonExistentAddressId = UUID.randomUUID();
            AddressRequest request = new AddressRequest(
                    "Home", "123 Street", "City", "District", "00000", false
            );

            when(addressRepository.findByUserIdAndId(userId, nonExistentAddressId)).thenReturn(Optional.empty());

            // when/then
            assertThatThrownBy(() -> addressService.updateAddress(userId, nonExistentAddressId, request))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Address");

            verify(addressRepository, never()).save(any(Address.class));
        }
    }

    @Nested
    @DisplayName("deleteAddress()")
    class DeleteAddress {

        @Test
        @DisplayName("should delete an existing address successfully")
        void shouldDeleteAddressSuccessfully() {
            // given
            when(addressRepository.findByUserIdAndId(userId, addressId)).thenReturn(Optional.of(testAddress));

            // when
            addressService.deleteAddress(userId, addressId);

            // then
            verify(addressRepository).delete(testAddress);
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException when address to delete does not exist")
        void shouldThrowResourceNotFoundWhenAddressToDeleteDoesNotExist() {
            // given
            UUID nonExistentAddressId = UUID.randomUUID();

            when(addressRepository.findByUserIdAndId(userId, nonExistentAddressId)).thenReturn(Optional.empty());

            // when/then
            assertThatThrownBy(() -> addressService.deleteAddress(userId, nonExistentAddressId))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Address");

            verify(addressRepository, never()).delete(any(Address.class));
        }
    }
}
