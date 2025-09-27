# Design System Documentation

## Overview

The Gatekeeper RPA Design System is an enterprise-grade design language built with modern UI patterns, accessibility standards, and a focus on consistency across all components. The system provides comprehensive theming support, professional color palettes, typography scales, spacing systems, and interactive patterns.

## 🎨 Color Palette

### Primary Colors

The primary color system uses a professional blue that conveys trust and security.

```css
/* HSL values for theming */
--primary: 221.2 83.2% 53.3%;      /* Main blue */
--primary-foreground: 210 40% 98%;  /* White text */
```

### Extended Primary Scale

| Color | HSL Value | Use Case |
|-------|-----------|----------|
| 50 | 217.2 91.2% 97.8% | Subtle backgrounds |
| 100 | 215.3 88.1% 95.1% | Hover states |
| 200 | 213.2 84.2% 91.2% | Light accents |
| 300 | 211.1 79.2% 86.7% | Borders, dividers |
| 400 | 208.9 73.4% 80.4% | Active states |
| 500 | 205.7 68.0% 73.5% | Primary actions |
| 600 | 221.2 83.2% 53.3% | Brand color |
| 700 | 221.3 83.3% 45.3% | Emphasis |
| 800 | 221.5 83.6% 38.0% | Strong emphasis |
| 900 | 221.7 84.0% 31.8% | Headers, titles |
| 950 | 222.0 84.5% 21.1% | Very strong emphasis |

### Status Colors

#### Success (Green)
- **500**: `142.1 76.2% 36.3%` - Primary success state
- **50**: `150 100% 96.5%` - Subtle success background
- **Foreground**: `355.7 100% 97.3%` - Text on success

#### Warning (Amber)
- **500**: `38.9 92.5% 55.9%` - Primary warning state
- **50**: `54 100% 97.1%` - Subtle warning background
- **Foreground**: `25 25.3% 14.9%` - Text on warning

#### Error (Red)
- **500**: `0 72.2% 50.6%` - Primary error state
- **50**: `0 100% 96.5%` - Subtle error background
- **Foreground**: `210 40% 98%` - Text on error

#### Info (Blue)
- **500**: `217.2 91.2% 59.8%` - Primary info state
- **50**: `214 100% 96.9%` - Subtle info background
- **Foreground**: `222.2 84% 4.9%` - Text on info

### Security Colors

Special colors for RPA security indicators:

```css
--security-safe: 142.1 76.2% 36.3%;    /* Green */
--security-warning: 38.9 92.5% 55.9%;  /* Amber */
--security-danger: 0 72.2% 50.6%;      /* Red */
--security-info: 217.2 91.2% 59.8%;    /* Blue */
```

### Neutral Gray Scale

Complete gray scale for subtle UI elements:

```css
--gray-50: 210 20% 98%;   /* Lightest */
--gray-100: 210 20% 96%;
--gray-200: 210 20% 93%;
--gray-300: 210 20% 88%;
--gray-400: 210 20% 80%;
--gray-500: 210 20% 65%;
--gray-600: 210 20% 46%;
--gray-700: 210 20% 35%;
--gray-800: 210 20% 26%;
--gray-900: 210 20% 18%;
--gray-950: 210 20% 11%;  /* Darkest */
```

## 📏 Spacing System

The spacing system uses a 4px base scale for consistent spacing throughout the interface.

| Value | CSS | Pixels | Use Case |
|-------|-----|--------|----------|
| 0.5 | 0.125rem | 2px | Tight spacing |
| 1 | 0.25rem | 4px | Micro spacing |
| 1.5 | 0.375rem | 6px | Small padding |
| 2 | 0.5rem | 8px | Default padding |
| 2.5 | 0.625rem | 10px | Comfortable padding |
| 3 | 0.75rem | 12px | Medium spacing |
| 4 | 1rem | 16px | Standard spacing |
| 5 | 1.25rem | 20px | Large spacing |
| 6 | 1.5rem | 24px | Section spacing |
| 8 | 2rem | 32px | Component spacing |
| 10 | 2.5rem | 40px | Large components |
| 12 | 3rem | 48px | Page sections |
| 16 | 4rem | 64px | Major sections |
| 20 | 5rem | 80px | Hero sections |

## 🎭 Border Radius System

Consistent corner radii for different UI elements:

| Radius | CSS | Pixels | Use Case |
|--------|-----|--------|----------|
| xs | 0.125rem | 2px | Small buttons, badges |
| sm | 0.25rem | 4px | Input fields, small cards |
| md | 0.375rem | 6px | Default radius |
| lg | 0.5rem | 8px | Cards, buttons |
| xl | 0.75rem | 12px | Large cards, modals |
| 2xl | 1rem | 16px | Popovers, tooltips |
| 3xl | 1.5rem | 24px | Pill shapes |
| full | 9999px | 100% | Circular elements |

## 🎨 Typography System

### Font Families

- **Sans**: Inter var, Inter, system-ui, sans-serif
- **Serif**: Merriweather, Georgia, serif
- **Mono**: JetBrains Mono, Fira Code, Consolas, monospace
- **Display**: Inter Display, Inter, system-ui, sans-serif

### Display Sizes (Bold)

| Size | rem | px | Line Height | Use Case |
|-------|-----|----|-------------|----------|
| display-xs | 0.75rem | 12px | 1rem | Small display |
| display-sm | 0.875rem | 14px | 1.25rem | Card titles |
| display-md | 1rem | 16px | 1.375rem | Section headers |
| display-lg | 1.125rem | 18px | 1.5rem | Subheadings |
| display-xl | 1.25rem | 20px | 1.75rem | Large headings |
| display-2xl | 1.5rem | 24px | 2rem | Page titles |
| display-3xl | 1.875rem | 30px | 2.25rem | Feature titles |
| display-4xl | 2.25rem | 36px | 2.5rem | Hero titles |
| display-5xl | 3rem | 48px | 1 | Marketing |
| display-6xl | 3.75rem | 60px | 1 | Big statements |
| display-7xl | 4.5rem | 72px | 1 | Impact text |
| display-8xl | 6rem | 96px | 1 | Hero impact |
| display-9xl | 8rem | 128px | 1 | Massive display |

### Text Sizes

| Size | rem | px | Line Height | Use Case |
|-------|-----|----|-------------|----------|
| xxs | 0.625rem | 10px | 0.875rem | Captions |
| xs | 0.75rem | 12px | 1rem | Labels, footers |
| sm | 0.875rem | 14px | 1.25rem | Small text |
| base | 1rem | 16px | 1.5rem | Body text |
| lg | 1.125rem | 18px | 1.75rem | Lead text |
| xl | 1.25rem | 20px | 1.75rem | Subheadings |
| 2xl | 1.5rem | 24px | 2rem | Headings |
| 3xl | 1.875rem | 30px | 2.25rem | Large headings |
| 4xl | 2.25rem | 36px | 2.5rem | Page titles |
| 5xl | 3rem | 48px | 1 | Display text |
| 6xl | 3.75rem | 60px | 1 | Hero text |
| 7xl | 4.5rem | 72px | 1 | Impact |
| 8xl | 6rem | 96px | 1 | Massive |
| 9xl | 8rem | 128px | 1 | Ultra massive |

### Font Weights

| Weight | Value | Use Case |
|--------|-------|----------|
| Thin | 100 | Light display |
| ExtraLight | 200 | Subtle text |
| Light | 300 | Large headings |
| Normal | 400 | Body text |
| Medium | 500 | Emphasis |
| SemiBold | 600 | Strong emphasis |
| Bold | 700 | Headings |
| ExtraBold | 800 | Strong headings |
| Black | 900 | Impact text |

### Letter Spacing

| Spacing | Value | Use Case |
|---------|-------|----------|
| tightest | -0.075em | Dense text |
| tighter | -0.05em | Tight headings |
| tight | -0.025em | Slightly tight |
| normal | 0 | Default |
| wide | 0.025em | Slightly loose |
| wider | 0.05em | Loose text |
| widest | 0.1em | Very loose |

## 🌟 Shadow System

### Base Shadows

| Shadow | Description | Use Case |
|--------|-------------|----------|
| none | No shadow | Flat elements |
| xs | 0 1px 2px 0 rgba(0, 0, 0, 0.05) | Subtle elevation |
| sm | 0 1px 2px 0 rgba(0, 0, 0, 0.05), 0 1px 3px 0 rgba(0, 0, 0, 0.1) | Cards, buttons |
| md | 0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06) | Elevated cards |
| lg | 0 10px 15px -3px rgba(0, 0, 0, 0.1), 0 4px 6px -2px rgba(0, 0, 0, 0.05) | Popovers, modals |
| xl | 0 20px 25px -5px rgba(0, 0, 0, 0.1), 0 10px 10px -5px rgba(0, 0, 0, 0.04) | Dropdowns |
| 2xl | 0 25px 50px -12px rgba(0, 0, 0, 0.25) | Heavy elevation |
| inner | inset 0 2px 4px 0 rgba(0, 0, 0, 0.06) | Inset elements |

### Colored Shadows

| Shadow | Description |
|--------|-------------|
| primary | Soft blue glow for primary elements |
| success | Soft green glow for success states |
| warning | Soft amber glow for warnings |
| error | Soft red glow for errors |
| info | Soft blue glow for info elements |

### Elevation System

Material Design-inspired elevation levels:

| Elevation | Description | Use Case |
|-----------|-------------|----------|
| 1 | 0 1px 3px rgba(0, 0, 0, 0.12), 0 1px 2px rgba(0, 0, 0, 0.24) | FABs, buttons |
| 2 | 0 3px 6px rgba(0, 0, 0, 0.16), 0 3px 6px rgba(0, 0, 0, 0.23) | Cards, menus |
| 3 | 0 10px 20px rgba(0, 0, 0, 0.19), 0 6px 6px rgba(0, 0, 0, 0.23) | Dialogs |
| 4 | 0 14px 28px rgba(0, 0, 0, 0.25), 0 10px 10px rgba(0, 0, 0, 0.22) | Bottom sheets |
| 5 | 0 19px 38px rgba(0, 0, 0, 0.3), 0 15px 12px rgba(0, 0, 0, 0.22) | Modal dialogs |

## 🎬 Animation System

### Base Animations

| Animation | Duration | Easing | Use Case |
|-----------|----------|--------|----------|
| spin | 1s | linear | Loading spinners |
| ping | 1s | cubic-bezier(0, 0, 0.2, 1) | Notifications |
| pulse | 2s | cubic-bezier(0.4, 0, 0.6, 1) | Attention states |
| bounce | 1s | cubic-bezier | Playful elements |

### Custom Animations

#### Transitions
- **slide-in**: Elements slide from top
- **slide-out**: Elements slide to top
- **fade-in**: Fade from transparent
- **fade-out**: Fade to transparent
- **scale-in**: Scale from small
- **scale-out**: Scale to small

#### Micro-interactions
- **hover-lift**: Subtle lift on hover
- **press-scale**: Scale down on press
- **shake**: Horizontal shake motion
- **wiggle**: Gentle rotation
- **float**: Vertical floating
- **drift**: Horizontal drifting

#### Loading States
- **spin-slow**: Slow rotation
- **spin-reverse**: Reverse rotation
- **dash**: Dashed circle animation
- **shimmer**: Shimmer loading effect

#### Progress
- **progress**: Animated progress bar
- **progress-pulse**: Pulsing progress

### Animation Controls

```css
/* Iteration control */
.animate-once      /* Play once */
.animate-infinite  /* Repeat forever */
.animate-alternate /* Reverse direction */
.animate-reverse   /* Play backwards */
.animate-paused    /* Pause animation */
```

## 🧩 Component Styles

### Buttons

```html
<button class="btn-base">Base Button</button>
<button class="btn-primary">Primary</button>
<button class="btn-secondary">Secondary</button>
<button class="btn-success">Success</button>
<button class="btn-warning">Warning</button>
<button class="btn-error">Error</button>
<button class="btn-info">Info</button>
<button class="btn-ghost">Ghost</button>
<button class="btn-link">Link</button>
```

### Cards

```html
<div class="card-base">Basic Card</div>
<div class="card-base card-elevated">Elevated Card</div>
<div class="card-base card-security">Security Card</div>
<div class="card-base card-hover">Hover Card</div>
```

### Status Indicators

```html
<span class="status-indicator status-indicator-online">Online</span>
<span class="status-indicator status-indicator-offline">Offline</span>
<span class="status-indicator status-indicator-warning">Warning</span>
<span class="status-indicator status-indicator-busy">Busy</span>
```

### Security Indicators

```html
<span class="security-indicator security-indicator-secure">Secure</span>
<span class="security-indicator security-indicator-warning">Warning</span>
<span class="security-indicator security-indicator-error">Danger</span>
<span class="security-indicator security-indicator-info">Info</span>
```

### Loading States

```html
<div class="loading-spinner"></div>
<div class="loading-dots">
  <span></span>
</div>
<div class="skeleton">Loading...</div>
```

## 🎨 Utility Classes

### Typography

```html
<p class="text-balance">Balanced text</p>
<p class="text-pretty">Pretty text</p>
<h1 class="gradient-text-primary">Gradient heading</h1>
```

### Containers

```html
<div class="container-safe">Safe container</div>
<div class="container-compact">Compact container</div>
<div class="container-narrow">Narrow container</div>
<div class="container-wide">Wide container</div>
```

### Responsive

```html
<div class="mobile-stack">Stacks on mobile</div>
<div class="mobile-full-width">Full width on mobile</div>
<div class="mobile-text-center">Centered text on mobile</div>
```

## 🌙 Dark Mode

The design system includes full dark mode support. Use the `dark:` prefix in Tailwind classes or manually toggle the `.dark` class on the `html` element.

```html
<!-- Dark mode specific styles -->
<div class="bg-white dark:bg-gray-900">
  <p class="text-gray-900 dark:text-gray-100">Adaptive text</p>
</div>
```

## ♿ Accessibility Features

### Focus Management

- All interactive elements have visible focus states
- Skip to content link for keyboard navigation
- Proper ARIA labels and descriptions

### Color Contrast

- All color combinations meet WCAG AA standards
- Tested for both light and dark modes
- Semantic color usage ensures accessibility

### Screen Reader Support

- `.sr-only` class for screen reader only content
- `.sr-only-focusable` for hidden content that becomes visible on focus
- Proper heading hierarchy structure

## 📱 Responsive Breakpoints

- **sm**: 640px
- **md**: 768px
- **lg**: 1024px
- **xl**: 1280px
- **2xl**: 1536px

## 🎯 Best Practices

1. **Use semantic colors** - Always use semantic color classes (success, warning, error, info) instead of direct color values
2. **Maintain contrast** - Ensure text meets WCAG AA contrast ratios
3. **Be consistent** - Follow the spacing and sizing scales strictly
4. **Consider motion** - Use `prefers-reduced-motion` for animations
5. **Test accessibility** - Regularly test with screen readers and keyboard navigation
6. **Use proper hierarchy** - Maintain clear visual hierarchy with typography scale

## 🔄 Version History

- **v2.0.0** - Enhanced design system with professional color palette, comprehensive spacing, animation system, and accessibility improvements