package com.trendyolclone.user.application.service;

import com.trendyolclone.common.exception.ResourceNotFoundException;
import com.trendyolclone.user.application.dto.AddressRequest;
import com.trendyolclone.user.application.dto.AddressResponse;
import com.trendyolclone.user.domain.model.Address;
import com.trendyolclone.user.domain.model.User;
import com.trendyolclone.user.domain.repository.AddressRepository;
import com.trendyolclone.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class AddressService {

    private static final Logger log = LoggerFactory.getLogger(AddressService.class);

    private final AddressRepository addressRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<AddressResponse> getUserAddresses(UUID userId) {
        List<Address> addresses = addressRepository.findByUserId(userId);
        return addresses.stream()
                .map(this::mapToResponse)
                .toList();
    }

    public AddressResponse addAddress(UUID userId, AddressRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        // If the new address is default, unset all other defaults for this user
        if (request.isDefault()) {
            unsetDefaultAddresses(userId);
        }

        Address address = Address.builder()
                .title(request.title())
                .fullAddress(request.fullAddress())
                .city(request.city())
                .district(request.district())
                .postalCode(request.postalCode())
                .isDefault(request.isDefault())
                .user(user)
                .build();

        Address savedAddress = addressRepository.save(address);
        log.info("Address added for userId={}: addressId={}", userId, savedAddress.getId());

        return mapToResponse(savedAddress);
    }

    public AddressResponse updateAddress(UUID userId, UUID addressId, AddressRequest request) {
        Address address = addressRepository.findByUserIdAndId(userId, addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address", "id", addressId));

        // If setting this address as default, unset all other defaults first
        if (request.isDefault() && !address.isDefault()) {
            unsetDefaultAddresses(userId);
        }

        address.setTitle(request.title());
        address.setFullAddress(request.fullAddress());
        address.setCity(request.city());
        address.setDistrict(request.district());
        address.setPostalCode(request.postalCode());
        address.setDefault(request.isDefault());

        Address updatedAddress = addressRepository.save(address);
        log.info("Address updated for userId={}: addressId={}", userId, addressId);

        return mapToResponse(updatedAddress);
    }

    public void deleteAddress(UUID userId, UUID addressId) {
        Address address = addressRepository.findByUserIdAndId(userId, addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address", "id", addressId));

        addressRepository.delete(address);
        log.info("Address deleted for userId={}: addressId={}", userId, addressId);
    }

    private void unsetDefaultAddresses(UUID userId) {
        List<Address> addresses = addressRepository.findByUserId(userId);
        addresses.stream()
                .filter(Address::isDefault)
                .forEach(addr -> {
                    addr.setDefault(false);
                    addressRepository.save(addr);
                });
    }

    private AddressResponse mapToResponse(Address address) {
        return new AddressResponse(
                address.getId(),
                address.getTitle(),
                address.getFullAddress(),
                address.getCity(),
                address.getDistrict(),
                address.getPostalCode(),
                address.isDefault()
        );
    }
}
