/** @type {import('tailwindcss').Config} */
module.exports = {
  content: [
    "./src/**/*.{js,jsx,ts,tsx}",
    "./public/index.html"
  ],
  theme: {
    extend: {
      colors: {
        'libtek-maroon': '#800000', 
        'libtek-gold': '#FFD700',
      },
    },
  },
  plugins: [],
}