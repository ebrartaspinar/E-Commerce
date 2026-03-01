package com.trendyolclone.user.application.service;

import com.trendyolclone.common.exception.ResourceNotFoundException;
import com.trendyolclone.user.application.dto.AddressResponse;
import com.trendyolclone.user.application.dto.UpdateUserRequest;
import com.trendyolclone.user.application.dto.UserResponse;
import com.trendyolclone.user.domain.event.UserUpdatedEvent;
import com.trendyolclone.user.domain.model.Address;
import com.trendyolclone.user.domain.model.User;
import com.trendyolclone.user.domain.repository.UserRepository;
import com.trendyolclone.user.infrastructure.kafka.UserEventProducer;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final UserEventProducer userEventProducer;

    @Transactional(readOnly = true)
    public UserResponse getCurrentUser(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        return mapToResponse(user);
    }

    @Transactional
    public UserResponse updateUser(UUID userId, UpdateUserRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        if (StringUtils.hasText(request.firstName())) {
            user.setFirstName(request.firstName());
        }
        if (StringUtils.hasText(request.lastName())) {
            user.setLastName(request.lastName());
        }
        if (request.phone() != null) {
            user.setPhone(request.phone());
        }

        User updatedUser = userRepository.save(user);
        log.info("User updated successfully: userId={}", userId);

        // Publish update event
        UserUpdatedEvent event = new UserUpdatedEvent(
                updatedUser.getId(),
                updatedUser.getEmail(),
                updatedUser.getFirstName(),
                updatedUser.getLastName()
        );
        userEventProducer.publishUserUpdated(event);

        return mapToResponse(updatedUser);
    }

    @Transactional(readOnly = true)
    public UserResponse getUserById(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        return mapToResponse(user);
    }

    private UserResponse mapToResponse(User user) {
        List<AddressResponse> addressResponses = user.getAddresses().stream()
                .map(this::mapAddressToResponse)
                .toList();

        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getPhone(),
                user.getRole(),
                user.getStatus(),
                addressResponses,
                user.getCreatedAt()
        );
    }

    private AddressResponse mapAddressToResponse(Address address) {
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
