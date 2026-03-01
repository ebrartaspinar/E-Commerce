/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{vue,js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      fontFamily: {
        pixel: ['"Press Start 2P"', 'cursive'],
        retro: ['"VT323"', 'monospace'],
        body: ['"Nunito"', 'sans-serif'],
      },
      colors: {
        primary: {
          50: '#fef7ee',
          100: '#fdedd3',
          200: '#fad7a5',
          300: '#f6b96d',
          400: '#f19333',
          500: '#ee7a0e',
          600: '#df6009',
          700: '#b9480a',
          800: '#933a10',
          900: '#773110',
          950: '#401606',
        },
        secondary: {
          50: '#f4f7f7',
          100: '#e2ebea',
          200: '#c8d9d7',
          300: '#a1bfbc',
          400: '#739d9a',
          500: '#58827f',
          600: '#466a6b',
          700: '#3c5758',
          800: '#344949',
          900: '#2e3f3f',
          950: '#1a2626',
        },
        warm: {
          50: '#fdf8f1',
          100: '#f9ede0',
          200: '#f2d8be',
          300: '#eabd94',
          400: '#e09a68',
          500: '#d87f47',
          600: '#ca693c',
          700: '#a85233',
          800: '#87432f',
          900: '#6d3829',
          950: '#3a1b14',
        },
        cozy: {
          cream: '#FFF8F0',
          peach: '#FFE5CC',
          brown: '#8B6F47',
          dark: '#2D1B0E',
          green: '#4A7C59',
          red: '#C75C5C',
          gold: '#D4A843',
          sky: '#87CEEB',
        },
      },
      boxShadow: {
        'pixel': '4px 4px 0px 0px rgba(45, 27, 14, 0.3)',
        'pixel-lg': '6px 6px 0px 0px rgba(45, 27, 14, 0.3)',
        'pixel-hover': '2px 2px 0px 0px rgba(45, 27, 14, 0.3)',
        'pixel-inset': 'inset 2px 2px 0px 0px rgba(45, 27, 14, 0.1)',
      },
      borderWidth: {
        '3': '3px',
      },
      animation: {
        'bounce-pixel': 'bouncePixel 0.5s steps(3) infinite',
        'fade-in': 'fadeIn 0.3s ease-out',
        'slide-up': 'slideUp 0.3s ease-out',
        'slide-down': 'slideDown 0.3s ease-out',
        'pulse-slow': 'pulse 3s ease-in-out infinite',
      },
      keyframes: {
        bouncePixel: {
          '0%, 100%': { transform: 'translateY(0)' },
          '50%': { transform: 'translateY(-4px)' },
        },
        fadeIn: {
          '0%': { opacity: '0' },
          '100%': { opacity: '1' },
        },
        slideUp: {
          '0%': { transform: 'translateY(10px)', opacity: '0' },
          '100%': { transform: 'translateY(0)', opacity: '1' },
        },
        slideDown: {
          '0%': { transform: 'translateY(-10px)', opacity: '0' },
          '100%': { transform: 'translateY(0)', opacity: '1' },
        },
      },
    },
  },
  plugins: [
    require('@tailwindcss/forms'),
  ],
}
