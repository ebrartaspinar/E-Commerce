package com.stardrop.controller;

import com.stardrop.model.Favorite;
import com.stardrop.model.User;
import com.stardrop.repository.UserRepository;
import com.stardrop.service.FavoriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/favorites")
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteService favoriteService;
    private final UserRepository userRepository;

    @GetMapping
    public ResponseEntity<List<Favorite>> getFavorites(Authentication auth) {
        User user = getUser(auth);
        return ResponseEntity.ok(favoriteService.getUserFavorites(user.getId()));
    }

    @PostMapping("/{productId}")
    public ResponseEntity<Favorite> addFavorite(@PathVariable Long productId, Authentication auth) {
        User user = getUser(auth);
        return ResponseEntity.ok(favoriteService.addFavorite(user.getId(), productId));
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> removeFavorite(@PathVariable Long productId, Authentication auth) {
        User user = getUser(auth);
        favoriteService.removeFavorite(user.getId(), productId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{productId}/check")
    public ResponseEntity<Map<String, Boolean>> checkFavorite(@PathVariable Long productId, Authentication auth) {
        User user = getUser(auth);
        boolean isFavorite = favoriteService.isFavorite(user.getId(), productId);
        return ResponseEntity.ok(Map.of("isFavorite", isFavorite));
    }

    private User getUser(Authentication auth) {
        return userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
