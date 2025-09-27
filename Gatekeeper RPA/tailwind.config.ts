/**
 * Tailwind CSS Configuration for Gatekeeper RPA System
 *
 * Enhanced enterprise-grade design system with professional color palette,
 * typography scale, spacing system, shadow system, border radius system,
 * and animation system for a cohesive design language.
 *
 * Created using Archon UI/UX optimization agent with NLNH principles.
 */

import type { Config } from 'tailwindcss';

const config: Config = {
  content: [
    './src/pages/**/*.{js,ts,jsx,tsx,mdx}',
    './src/components/**/*.{js,ts,jsx,tsx,mdx}',
    './src/app/**/*.{js,ts,jsx,tsx,mdx}',
  ],
  theme: {
    extend: {
      // Enhanced Color Palette with HSL variables for theming
      colors: {
        // Base semantic colors
        border: 'hsl(var(--border))',
        input: 'hsl(var(--input))',
        ring: 'hsl(var(--ring))',
        background: 'hsl(var(--background))',
        foreground: 'hsl(var(--foreground))',

        // Semantic color system
        primary: {
          DEFAULT: 'hsl(var(--primary))',
          foreground: 'hsl(var(--primary-foreground))',
          // Extended primary variants
          50: 'hsl(var(--primary-50))',
          100: 'hsl(var(--primary-100))',
          200: 'hsl(var(--primary-200))',
          300: 'hsl(var(--primary-300))',
          400: 'hsl(var(--primary-400))',
          500: 'hsl(var(--primary-500))',
          600: 'hsl(var(--primary-600))',
          700: 'hsl(var(--primary-700))',
          800: 'hsl(var(--primary-800))',
          900: 'hsl(var(--primary-900))',
          950: 'hsl(var(--primary-950))',
        },
        secondary: {
          DEFAULT: 'hsl(var(--secondary))',
          foreground: 'hsl(var(--secondary-foreground))',
        },
        destructive: {
          DEFAULT: 'hsl(var(--destructive))',
          foreground: 'hsl(var(--destructive-foreground))',
        },
        muted: {
          DEFAULT: 'hsl(var(--muted))',
          foreground: 'hsl(var(--muted-foreground))',
        },
        accent: {
          DEFAULT: 'hsl(var(--accent))',
          foreground: 'hsl(var(--accent-foreground))',
        },
        popover: {
          DEFAULT: 'hsl(var(--popover))',
          foreground: 'hsl(var(--popover-foreground))',
        },
        card: {
          DEFAULT: 'hsl(var(--card))',
          foreground: 'hsl(var(--card-foreground))',
        },

        // Enterprise color system
        enterprise: {
          // Status colors
          success: {
            DEFAULT: 'hsl(var(--success))',
            foreground: 'hsl(var(--success-foreground))',
            50: 'hsl(var(--success-50))',
            100: 'hsl(var(--success-100))',
            500: 'hsl(var(--success-500))',
            600: 'hsl(var(--success-600))',
            700: 'hsl(var(--success-700))',
          },
          warning: {
            DEFAULT: 'hsl(var(--warning))',
            foreground: 'hsl(var(--warning-foreground))',
            50: 'hsl(var(--warning-50))',
            100: 'hsl(var(--warning-100))',
            500: 'hsl(var(--warning-500))',
            600: 'hsl(var(--warning-600))',
            700: 'hsl(var(--warning-700))',
          },
          error: {
            DEFAULT: 'hsl(var(--error))',
            foreground: 'hsl(var(--error-foreground))',
            50: 'hsl(var(--error-50))',
            100: 'hsl(var(--error-100))',
            500: 'hsl(var(--error-500))',
            600: 'hsl(var(--error-600))',
            700: 'hsl(var(--error-700))',
          },
          info: {
            DEFAULT: 'hsl(var(--info))',
            foreground: 'hsl(var(--info-foreground))',
            50: 'hsl(var(--info-50))',
            100: 'hsl(var(--info-100))',
            500: 'hsl(var(--info-500))',
            600: 'hsl(var(--info-600))',
            700: 'hsl(var(--info-700))',
          },
          // Neutral colors
          gray: {
            50: 'hsl(var(--gray-50))',
            100: 'hsl(var(--gray-100))',
            200: 'hsl(var(--gray-200))',
            300: 'hsl(var(--gray-300))',
            400: 'hsl(var(--gray-400))',
            500: 'hsl(var(--gray-500))',
            600: 'hsl(var(--gray-600))',
            700: 'hsl(var(--gray-700))',
            800: 'hsl(var(--gray-800))',
            900: 'hsl(var(--gray-900))',
            950: 'hsl(var(--gray-950))',
          },
        },

        // Custom RPA-specific colors
        security: {
          safe: 'hsl(var(--security-safe))',
          warning: 'hsl(var(--security-warning))',
          danger: 'hsl(var(--security-danger))',
          info: 'hsl(var(--security-info))',
        },
        rpa: {
          blue: 'hsl(var(--rpa-blue))',
          teal: 'hsl(var(--rpa-teal))',
          purple: 'hsl(var(--rpa-purple))',
          orange: 'hsl(var(--rpa-orange))',
        },
      },

      // Enhanced Border Radius System
      borderRadius: {
        'none': '0',
        'xs': 'calc(var(--radius-xs))',
        'sm': 'calc(var(--radius-sm))',
        DEFAULT: 'calc(var(--radius))',
        'md': 'calc(var(--radius-md))',
        'lg': 'calc(var(--radius-lg))',
        'xl': 'calc(var(--radius-xl))',
        '2xl': 'calc(var(--radius-2xl))',
        '3xl': 'calc(var(--radius-3xl))',
        'full': '9999px',
      },

      // Enhanced Typography System
      fontFamily: {
        sans: ['Inter var', 'Inter', 'system-ui', 'sans-serif'],
        serif: ['Merriweather', 'Georgia', 'serif'],
        mono: ['JetBrains Mono', 'Fira Code', 'Consolas', 'monospace'],
        display: ['Inter Display', 'Inter', 'system-ui', 'sans-serif'],
      },
      fontSize: {
        // Display sizes
        'display-xs': ['0.75rem', { lineHeight: '1rem', fontWeight: '700' }],
        'display-sm': ['0.875rem', { lineHeight: '1.25rem', fontWeight: '700' }],
        'display-md': ['1rem', { lineHeight: '1.375rem', fontWeight: '700' }],
        'display-lg': ['1.125rem', { lineHeight: '1.5rem', fontWeight: '700' }],
        'display-xl': ['1.25rem', { lineHeight: '1.75rem', fontWeight: '700' }],
        'display-2xl': ['1.5rem', { lineHeight: '2rem', fontWeight: '700' }],
        'display-3xl': ['1.875rem', { lineHeight: '2.25rem', fontWeight: '700' }],
        'display-4xl': ['2.25rem', { lineHeight: '2.5rem', fontWeight: '700' }],
        'display-5xl': ['3rem', { lineHeight: '1', fontWeight: '700' }],
        'display-6xl': ['3.75rem', { lineHeight: '1', fontWeight: '700' }],
        'display-7xl': ['4.5rem', { lineHeight: '1', fontWeight: '700' }],
        'display-8xl': ['6rem', { lineHeight: '1', fontWeight: '700' }],
        'display-9xl': ['8rem', { lineHeight: '1', fontWeight: '700' }],
        // Text sizes
        'xxs': ['0.625rem', { lineHeight: '0.875rem' }],
        'xs': ['0.75rem', { lineHeight: '1rem' }],
        'sm': ['0.875rem', { lineHeight: '1.25rem' }],
        'base': ['1rem', { lineHeight: '1.5rem' }],
        'lg': ['1.125rem', { lineHeight: '1.75rem' }],
        'xl': ['1.25rem', { lineHeight: '1.75rem' }],
        '2xl': ['1.5rem', { lineHeight: '2rem' }],
        '3xl': ['1.875rem', { lineHeight: '2.25rem' }],
        '4xl': ['2.25rem', { lineHeight: '2.5rem' }],
        '5xl': ['3rem', { lineHeight: '1' }],
        '6xl': ['3.75rem', { lineHeight: '1' }],
        '7xl': ['4.5rem', { lineHeight: '1' }],
        '8xl': ['6rem', { lineHeight: '1' }],
        '9xl': ['8rem', { lineHeight: '1' }],
      },
      fontWeight: {
        thin: '100',
        extralight: '200',
        light: '300',
        normal: '400',
        medium: '500',
        semibold: '600',
        bold: '700',
        extrabold: '800',
        black: '900',
      },
      letterSpacing: {
        tightest: '-0.075em',
        tighter: '-0.05em',
        tight: '-0.025em',
        normal: '0',
        wide: '0.025em',
        wider: '0.05em',
        widest: '0.1em',
      },

      // Enhanced Spacing System (4px base scale)
      spacing: {
        '0': '0',
        '0.5': '0.125rem', // 2px
        '1': '0.25rem',   // 4px
        '1.5': '0.375rem', // 6px
        '2': '0.5rem',    // 8px
        '2.5': '0.625rem', // 10px
        '3': '0.75rem',   // 12px
        '3.5': '0.875rem', // 14px
        '4': '1rem',      // 16px
        '5': '1.25rem',   // 20px
        '6': '1.5rem',    // 24px
        '7': '1.75rem',   // 28px
        '8': '2rem',      // 32px
        '9': '2.25rem',   // 36px
        '10': '2.5rem',   // 40px
        '11': '2.75rem',  // 44px
        '12': '3rem',     // 48px
        '14': '3.5rem',   // 56px
        '16': '4rem',     // 64px
        '18': '4.5rem',   // 72px
        '20': '5rem',     // 80px
        '24': '6rem',     // 96px
        '28': '7rem',     // 112px
        '32': '8rem',     // 128px
        '36': '9rem',     // 144px
        '40': '10rem',    // 160px
        '44': '11rem',    // 176px
        '48': '12rem',    // 192px
        '52': '13rem',    // 208px
        '56': '14rem',    // 224px
        '60': '15rem',    // 240px
        '64': '16rem',    // 256px
        '72': '18rem',    // 288px
        '80': '20rem',    // 320px
        '88': '22rem',    // 352px
        '96': '24rem',    // 384px
        '128': '32rem',   // 512px
        '160': '40rem',   // 640px
        '192': '48rem',   // 768px
      },

      // Enhanced Shadow System
      boxShadow: {
        // Base shadows
        'none': 'none',
        'xs': '0 1px 2px 0 rgba(0, 0, 0, 0.05)',
        'sm': '0 1px 2px 0 rgba(0, 0, 0, 0.05), 0 1px 3px 0 rgba(0, 0, 0, 0.1)',
        DEFAULT: '0 1px 3px 0 rgba(0, 0, 0, 0.1), 0 1px 2px 0 rgba(0, 0, 0, 0.06)',
        'md': '0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06)',
        'lg': '0 10px 15px -3px rgba(0, 0, 0, 0.1), 0 4px 6px -2px rgba(0, 0, 0, 0.05)',
        'xl': '0 20px 25px -5px rgba(0, 0, 0, 0.1), 0 10px 10px -5px rgba(0, 0, 0, 0.04)',
        '2xl': '0 25px 50px -12px rgba(0, 0, 0, 0.25)',
        'inner': 'inset 0 2px 4px 0 rgba(0, 0, 0, 0.06)',

        // Colored shadows
        'primary': '0 4px 14px 0 rgba(var(--primary-rgb), 0.25)',
        'success': '0 4px 14px 0 rgba(var(--success-rgb), 0.25)',
        'warning': '0 4px 14px 0 rgba(var(--warning-rgb), 0.25)',
        'error': '0 4px 14px 0 rgba(var(--error-rgb), 0.25)',
        'info': '0 4px 14px 0 rgba(var(--info-rgb), 0.25)',

        // RPA-specific shadows
        'rpa': '0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06)',
        'rpa-lg': '0 10px 15px -3px rgba(0, 0, 0, 0.1), 0 4px 6px -2px rgba(0, 0, 0, 0.05)',
        'rpa-xl': '0 20px 25px -5px rgba(0, 0, 0, 0.15), 0 10px 10px -5px rgba(0, 0, 0, 0.1)',

        // Security indicator shadows
        'security': '0 0 0 3px rgba(var(--security-safe-rgb), 0.1)',
        'security-warning': '0 0 0 3px rgba(var(--security-warning-rgb), 0.1)',
        'security-danger': '0 0 0 3px rgba(var(--security-danger-rgb), 0.1)',
        'security-info': '0 0 0 3px rgba(var(--security-info-rgb), 0.1)',

        // Elevation shadows for components
        'elevation-1': '0 1px 3px rgba(0, 0, 0, 0.12), 0 1px 2px rgba(0, 0, 0, 0.24)',
        'elevation-2': '0 3px 6px rgba(0, 0, 0, 0.16), 0 3px 6px rgba(0, 0, 0, 0.23)',
        'elevation-3': '0 10px 20px rgba(0, 0, 0, 0.19), 0 6px 6px rgba(0, 0, 0, 0.23)',
        'elevation-4': '0 14px 28px rgba(0, 0, 0, 0.25), 0 10px 10px rgba(0, 0, 0, 0.22)',
        'elevation-5': '0 19px 38px rgba(0, 0, 0, 0.3), 0 15px 12px rgba(0, 0, 0, 0.22)',
      },

      // Enhanced Animation System
      animation: {
        // Base animations
        'none': 'none',
        'spin': 'spin 1s linear infinite',
        'ping': 'ping 1s cubic-bezier(0, 0, 0.2, 1) infinite',
        'pulse': 'pulse 2s cubic-bezier(0.4, 0, 0.6, 1) infinite',
        'bounce': 'bounce 1s infinite',

        // Custom animations
        'slide-in': 'slideIn 0.3s ease-out',
        'slide-out': 'slideOut 0.3s ease-in',
        'fade-in': 'fadeIn 0.3s ease-out',
        'fade-out': 'fadeOut 0.3s ease-in',
        'scale-in': 'scaleIn 0.3s ease-out',
        'scale-out': 'scaleOut 0.3s ease-in',
        'pulse-slow': 'pulse 3s cubic-bezier(0.4, 0, 0.6, 1) infinite',
        'pulse-fast': 'pulse 1s cubic-bezier(0.4, 0, 0.6, 1) infinite',
        'bounce-subtle': 'bounceSubtle 1s ease-in-out infinite',
        'bounce-gentle': 'bounceGentle 2s ease-in-out infinite',
        'glow': 'glow 2s ease-in-out infinite alternate',
        'glow-pulse': 'glowPulse 3s ease-in-out infinite',
        'shimmer': 'shimmer 2s linear infinite',
        'marquee': 'marquee 20s linear infinite',
        'accordion-down': 'accordion-down 0.2s ease-out',
        'accordion-up': 'accordion-up 0.2s ease-out',
        'enter': 'enter 0.2s ease-out',
        'exit': 'exit 0.2s ease-in',

        // Micro-interactions
        'hover-lift': 'hoverLift 0.2s ease-out',
        'press-scale': 'pressScale 0.1s ease-in',
        'shake': 'shake 0.5s ease-in-out',
        'wiggle': 'wiggle 1s ease-in-out infinite',
        'float': 'float 3s ease-in-out infinite',
        'drift': 'drift 6s ease-in-out infinite',

        // Loading animations
        'spin-slow': 'spin 2s linear infinite',
        'spin-reverse': 'spinReverse 1s linear infinite',
        'dash': 'dash 1.5s ease-in-out infinite',

        // Progress animations
        'progress': 'progress 1.5s ease-in-out infinite',
        'progress-pulse': 'progressPulse 2s ease-in-out infinite',
      },

      keyframes: {
        // Standard keyframes
        spin: {
          from: { transform: 'rotate(0deg)' },
          to: { transform: 'rotate(360deg)' },
        },
        spinReverse: {
          from: { transform: 'rotate(360deg)' },
          to: { transform: 'rotate(0deg)' },
        },
        ping: {
          '0%': { transform: 'scale(1)', opacity: '1' },
          '75%, 100%': { transform: 'scale(2)', opacity: '0' },
        },
        pulse: {
          '0%, 100%': { opacity: '1' },
          '50%': { opacity: '0.5' },
        },
        bounce: {
          '0%, 100%': { transform: 'translateY(-25%)', animationTimingFunction: 'cubic-bezier(0.8, 0, 1, 1)' },
          '50%': { transform: 'none', animationTimingFunction: 'cubic-bezier(0, 0, 0.2, 1)' },
        },

        // Custom keyframes
        slideIn: {
          '0%': { transform: 'translateY(-10px)', opacity: '0' },
          '100%': { transform: 'translateY(0)', opacity: '1' },
        },
        slideOut: {
          '0%': { transform: 'translateY(0)', opacity: '1' },
          '100%': { transform: 'translateY(-10px)', opacity: '0' },
        },
        fadeIn: {
          '0%': { opacity: '0' },
          '100%': { opacity: '1' },
        },
        fadeOut: {
          '0%': { opacity: '1' },
          '100%': { opacity: '0' },
        },
        scaleIn: {
          '0%': { transform: 'scale(0.95)', opacity: '0' },
          '100%': { transform: 'scale(1)', opacity: '1' },
        },
        scaleOut: {
          '0%': { transform: 'scale(1)', opacity: '1' },
          '100%': { transform: 'scale(0.95)', opacity: '0' },
        },
        bounceSubtle: {
          '0%, 100%': { transform: 'translateY(0)' },
          '50%': { transform: 'translateY(-5px)' },
        },
        bounceGentle: {
          '0%, 100%': { transform: 'translateY(0)' },
          '50%': { transform: 'translateY(-3px)' },
        },
        glow: {
          '0%': { boxShadow: '0 0 5px rgba(var(--primary-rgb), 0.5)' },
          '100%': { boxShadow: '0 0 20px rgba(var(--primary-rgb), 0.8)' },
        },
        glowPulse: {
          '0%, 100%': { boxShadow: '0 0 5px rgba(var(--primary-rgb), 0.5)' },
          '50%': { boxShadow: '0 0 25px rgba(var(--primary-rgb), 0.8)' },
        },
        shimmer: {
          '0%': { backgroundPosition: '-200% center' },
          '100%': { backgroundPosition: '200% center' },
        },
        marquee: {
          '0%': { transform: 'translateX(0%)' },
          '100%': { transform: 'translateX(-50%)' },
        },
        'accordion-down': {
          from: { height: '0' },
          to: { height: 'var(--radix-accordion-content-height)' },
        },
        'accordion-up': {
          from: { height: 'var(--radix-accordion-content-height)' },
          to: { height: '0' },
        },
        enter: {
          '0%': { opacity: '0', transform: 'scale(0.9)' },
          '100%': { opacity: '1', transform: 'scale(1)' },
        },
        exit: {
          '0%': { opacity: '1', transform: 'scale(1)' },
          '100%': { opacity: '0', transform: 'scale(0.9)' },
        },

        // Micro-interactions
        hoverLift: {
          '0%': { transform: 'translateY(0)' },
          '100%': { transform: 'translateY(-2px)' },
        },
        pressScale: {
          '0%': { transform: 'scale(1)' },
          '100%': { transform: 'scale(0.98)' },
        },
        shake: {
          '0%, 100%': { transform: 'translateX(0)' },
          '10%, 30%, 50%, 70%, 90%': { transform: 'translateX(-2px)' },
          '20%, 40%, 60%, 80%': { transform: 'translateX(2px)' },
        },
        wiggle: {
          '0%, 100%': { transform: 'rotate(0deg)' },
          '25%': { transform: 'rotate(3deg)' },
          '75%': { transform: 'rotate(-3deg)' },
        },
        float: {
          '0%, 100%': { transform: 'translateY(0)' },
          '50%': { transform: 'translateY(-10px)' },
        },
        drift: {
          '0%, 100%': { transform: 'translateX(0)' },
          '50%': { transform: 'translateX(10px)' },
        },

        // Loading animations
        dash: {
          '0%': { strokeDashoffset: '240' },
          '50%': { strokeDashoffset: '60' },
          '100%': { strokeDashoffset: '240' },
        },

        // Progress animations
        progress: {
          '0%': { backgroundPosition: '0% 50%' },
          '50%': { backgroundPosition: '100% 50%' },
          '100%': { backgroundPosition: '0% 50%' },
        },
        progressPulse: {
          '0%, 100%': { opacity: '0.3' },
          '50%': { opacity: '0.8' },
        },
      },

      // Enhanced transitions
      transitionDuration: {
        '0': '0ms',
        '75': '75ms',
        '100': '100ms',
        '150': '150ms',
        '200': '200ms',
        '300': '300ms',
        '400': '400ms',
        '500': '500ms',
        '700': '700ms',
        '1000': '1000ms',
      },
      transitionTimingFunction: {
        'in-out': 'cubic-bezier(0.4, 0, 0.2, 1)',
        'in': 'cubic-bezier(0.4, 0, 1, 1)',
        'out': 'cubic-bezier(0, 0, 0.2, 1)',
        'linear': 'linear',
        'ease': 'ease',
        'ease-in': 'ease-in',
        'ease-out': 'ease-out',
        'ease-in-out': 'ease-in-out',
        'bounce': 'cubic-bezier(0.68, -0.55, 0.265, 1.55)',
      },
      transitionProperty: {
        'none': 'none',
        'all': 'all',
        'common': 'background-color, border-color, color, fill, stroke, opacity, box-shadow, transform',
        'colors': 'background-color, border-color, color, fill, stroke',
        'opacity': 'opacity',
        'shadow': 'box-shadow',
        'transform': 'transform',
      },

      // Enhanced backdrop blur
      backdropBlur: {
        '0': '0',
        'xs': '2px',
        'sm': '4px',
        DEFAULT: '8px',
        'md': '12px',
        'lg': '16px',
        'xl': '24px',
        '2xl': '32px',
        '3xl': '64px',
      },

      // Enhanced aspect ratio
      aspectRatio: {
        'auto': 'auto',
        'square': '1 / 1',
        'video': '16 / 9',
        '4/3': '4 / 3',
        '3/4': '3 / 4',
        '5/4': '5 / 4',
        '4/5': '4 / 5',
        '3/2': '3 / 2',
        '2/3': '2 / 3',
        '5/3': '5 / 3',
        '3/5': '3 / 5',
        '5/2': '5 / 2',
        '2/5': '2 / 5',
        '9/16': '9 / 16',
        '16/9': '16 / 9',
        '1/2': '1 / 2',
        '2/1': '2 / 1',
        '1/3': '1 / 3',
        '3/1': '3 / 1',
        '1/4': '1 / 4',
        '4/1': '4 / 1',
      },

      // Enhanced z-index scale
      zIndex: {
        'hide': '-1',
        'auto': 'auto',
        '0': '0',
        '10': '10',
        '20': '20',
        '30': '30',
        '40': '40',
        '50': '50',
        '60': '60',
        '70': '70',
        '80': '80',
        '90': '90',
        '100': '100',
        'max': '2147483647',
      },

      // Grid template columns
      gridTemplateColumns: {
        '13': 'repeat(13, minmax(0, 1fr))',
        '14': 'repeat(14, minmax(0, 1fr))',
        '15': 'repeat(15, minmax(0, 1fr))',
        '16': 'repeat(16, minmax(0, 1fr))',
      },
    },
  },
  plugins: [
    // Custom utilities plugin
    function({ addUtilities, addComponents, theme }) {
      // Custom utilities
      addUtilities({
        '.text-balance': {
          textWrap: 'balance',
        },
        '.text-pretty': {
          textWrap: 'pretty',
        },
        '.scrollbar-hide': {
          '-ms-overflow-style': 'none',
          'scrollbar-width': 'none',
          '&::-webkit-scrollbar': {
            display: 'none',
          },
        },
        '.scrollbar-thin': {
          'scrollbar-width': 'thin',
          '&::-webkit-scrollbar': {
            width: '6px',
            height: '6px',
          },
        },
        '.gradient-text': {
          background: 'linear-gradient(135deg, var(--tw-gradient-stops))',
          '-webkit-background-clip': 'text',
          '-webkit-text-fill-color': 'transparent',
          'background-clip': 'text',
        },
        '.gradient-border': {
          background: `linear-gradient(${theme('colors.white')}, ${theme('colors.white')}) padding-box,
                      linear-gradient(135deg, var(--tw-gradient-stops)) border-box`,
          'border-style': 'solid',
          'border-width': '1px',
        },
        '.glass': {
          background: 'rgba(255, 255, 255, 0.1)',
          'backdrop-filter': 'blur(10px)',
          '-webkit-backdrop-filter': 'blur(10px)',
          border: '1px solid rgba(255, 255, 255, 0.2)',
        },
        '.glass-dark': {
          background: 'rgba(0, 0, 0, 0.2)',
          'backdrop-filter': 'blur(10px)',
          '-webkit-backdrop-filter': 'blur(10px)',
          border: '1px solid rgba(255, 255, 255, 0.1)',
        },
      });

      // Custom components
      addComponents({
        '.btn': {
          display: 'inline-flex',
          'align-items': 'center',
          'justify-content': 'center',
          'white-space': 'nowrap',
          'border-radius': 'calc(var(--radius) - 2px)',
          'font-size': '0.875rem',
          'font-weight': '500',
          'transition-property': 'all',
          'transition-timing-function': 'cubic-bezier(0.4, 0, 0.2, 1)',
          'transition-duration': '150ms',
          'padding': '0.5rem 1rem',
          'border': '1px solid transparent',
          '&:disabled': {
            'pointer-events': 'none',
            'opacity': '0.5',
          },
        },
        '.btn-sm': {
          'padding': '0.25rem 0.75rem',
          'font-size': '0.75rem',
        },
        '.btn-lg': {
          'padding': '0.75rem 1.5rem',
          'font-size': '1rem',
        },
        '.input': {
          'flex': '1',
          'min-width': '0',
          'border-radius': 'calc(var(--radius) - 2px)',
          'border': '1px solid hsl(var(--input))',
          'background-color': 'hsl(var(--background))',
          'padding': '0.5rem 0.75rem',
          'font-size': '0.875rem',
          'transition-property': 'all',
          'transition-timing-function': 'cubic-bezier(0.4, 0, 0.2, 1)',
          'transition-duration': '150ms',
          '&:focus': {
            'outline': '2px solid transparent',
            'outline-offset': '2px',
            'box-shadow': '0 0 0 2px hsl(var(--ring))',
          },
        },
        '.card': {
          'background-color': 'hsl(var(--card))',
          'border-radius': 'calc(var(--radius))',
          'border': '1px solid hsl(var(--border))',
          'color': 'hsl(var(--card-foreground))',
        },
      });
    },
  ],
  future: {
    hoverOnlyWhenSupported: true,
  },
};

export default config;