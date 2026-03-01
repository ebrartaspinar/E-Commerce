package com.trendyolclone.user.domain.event;

import java.util.UUID;

public record UserUpdatedEvent(
        UUID userId,
        String email,
        String firstName,
        String lastName
) {
}
