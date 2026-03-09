<template>
  <footer class="bg-cozy-dark text-cozy-cream">
    <!-- Pixel art top border -->
    <div class="h-3 bg-gradient-to-r from-primary-500 via-cozy-gold to-secondary-500" style="background-size: 12px 12px; image-rendering: pixelated;"></div>

    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-10">
      <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-8">
        <!-- About -->
        <div>
          <h3 class="font-pixel text-xs text-primary-400 mb-4">About</h3>
          <ul class="space-y-2">
            <li>
              <router-link to="/about" class="font-retro text-lg text-cozy-cream/80 hover:text-primary-400 transition-colors">
                About Us
              </router-link>
            </li>
            <li>
              <router-link to="/careers" class="font-retro text-lg text-cozy-cream/80 hover:text-primary-400 transition-colors">
                Careers
              </router-link>
            </li>
            <li>
              <router-link to="/blog" class="font-retro text-lg text-cozy-cream/80 hover:text-primary-400 transition-colors">
                Blog
              </router-link>
            </li>
            <li>
              <router-link to="/press" class="font-retro text-lg text-cozy-cream/80 hover:text-primary-400 transition-colors">
                Press
              </router-link>
            </li>
          </ul>
        </div>

        <!-- Help -->
        <div>
          <h3 class="font-pixel text-xs text-primary-400 mb-4">Help</h3>
          <ul class="space-y-2">
            <li>
              <router-link to="/help/shipping" class="font-retro text-lg text-cozy-cream/80 hover:text-primary-400 transition-colors">
                Shipping Info
              </router-link>
            </li>
            <li>
              <router-link to="/help/returns" class="font-retro text-lg text-cozy-cream/80 hover:text-primary-400 transition-colors">
                Returns &amp; Refunds
              </router-link>
            </li>
            <li>
              <router-link to="/help/faq" class="font-retro text-lg text-cozy-cream/80 hover:text-primary-400 transition-colors">
                FAQ
              </router-link>
            </li>
            <li>
              <router-link to="/contact" class="font-retro text-lg text-cozy-cream/80 hover:text-primary-400 transition-colors">
                Contact Us
              </router-link>
            </li>
          </ul>
        </div>

        <!-- Corporate -->
        <div>
          <h3 class="font-pixel text-xs text-primary-400 mb-4">Corporate</h3>
          <ul class="space-y-2">
            <li>
              <router-link to="/terms" class="font-retro text-lg text-cozy-cream/80 hover:text-primary-400 transition-colors">
                Terms of Service
              </router-link>
            </li>
            <li>
              <router-link to="/privacy" class="font-retro text-lg text-cozy-cream/80 hover:text-primary-400 transition-colors">
                Privacy Policy
              </router-link>
            </li>
            <li>
              <router-link to="/cookies" class="font-retro text-lg text-cozy-cream/80 hover:text-primary-400 transition-colors">
                Cookie Policy
              </router-link>
            </li>
            <li>
              <router-link to="/seller-register" class="font-retro text-lg text-cozy-cream/80 hover:text-primary-400 transition-colors">
                Sell on E-Commerce
              </router-link>
            </li>
          </ul>
        </div>

        <!-- Connect with Us -->
        <div>
          <h3 class="font-pixel text-xs text-primary-400 mb-4">Connect</h3>
          <div class="flex gap-4 mb-4">
            <a
              href="https://twitter.com"
              target="_blank"
              rel="noopener noreferrer"
              class="w-10 h-10 border-3 border-cozy-cream/40 flex items-center justify-center text-cozy-cream/80 hover:text-primary-400 hover:border-primary-400 transition-colors"
              aria-label="Twitter"
            >
              <span class="font-retro text-xl">X</span>
            </a>
            <a
              href="https://instagram.com"
              target="_blank"
              rel="noopener noreferrer"
              class="w-10 h-10 border-3 border-cozy-cream/40 flex items-center justify-center text-cozy-cream/80 hover:text-primary-400 hover:border-primary-400 transition-colors"
              aria-label="Instagram"
            >
              <span class="font-retro text-xl">IG</span>
            </a>
            <a
              href="https://facebook.com"
              target="_blank"
              rel="noopener noreferrer"
              class="w-10 h-10 border-3 border-cozy-cream/40 flex items-center justify-center text-cozy-cream/80 hover:text-primary-400 hover:border-primary-400 transition-colors"
              aria-label="Facebook"
            >
              <span class="font-retro text-xl">FB</span>
            </a>
            <a
              href="https://youtube.com"
              target="_blank"
              rel="noopener noreferrer"
              class="w-10 h-10 border-3 border-cozy-cream/40 flex items-center justify-center text-cozy-cream/80 hover:text-primary-400 hover:border-primary-400 transition-colors"
              aria-label="YouTube"
            >
              <span class="font-retro text-xl">YT</span>
            </a>
          </div>
          <p class="font-retro text-lg text-cozy-cream/60">
            Subscribe to our newsletter for pixel-perfect deals!
          </p>
          <div class="mt-2 flex">
            <input
              type="email"
              placeholder="Email address"
              class="flex-1 px-3 py-1 font-retro text-lg bg-cozy-dark border-3 border-cozy-cream/40 text-cozy-cream placeholder-cozy-cream/30 focus:border-primary-400 focus:outline-none"
            />
            <button
              class="px-3 py-1 bg-primary-500 text-white font-retro text-lg border-3 border-cozy-cream/40 hover:bg-primary-600 transition-colors"
            >
              Join
            </button>
          </div>
        </div>
      </div>

      <!-- Copyright -->
      <div class="mt-10 pt-6 border-t border-cozy-cream/20 text-center">
        <p class="font-pixel text-[10px] text-cozy-cream/50">
          &copy; {{ currentYear }} E-Commerce. All rights reserved.
        </p>
        <p class="font-retro text-sm text-cozy-cream/30 mt-1">
          A Cozy Pixel RPG Shopping Experience
        </p>
      </div>
    </div>
  </footer>
</template>

<script setup lang="ts">
import { computed } from 'vue'

const currentYear = computed(() => new Date().getFullYear())
</script>
