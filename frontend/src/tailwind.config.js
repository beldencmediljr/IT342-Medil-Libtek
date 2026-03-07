/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        'libtek-maroon': '#800000', // CIT Branding from your SDD 
        'libtek-gold': '#FFD700',
      },
    },
  },
  plugins: [],
}