import { ThemeProvider } from 'next-themes'
import '../styles/globals.css'

function MyApp({ Component, pageProps }) {
  return (
    <ThemeProvider attribute="class">
      <style jsx global>{`
        .dark body {
          background-color: #121212 !important;
          color: white !important;
        }

        .dark input,
        .dark textarea,
        .dark select {
          background-color: #1e1e1e !important;
          color: white !important;
          border: 1px solid #404040 !important;
        }

        .dark button {
          background-color: #2d2d2d !important;
          color: white !important;
          border: 1px solid #404040 !important;
        }

        .dark .product-item {
          background-color: #1e1e1e !important;
          color: white !important;
          border: 1px solid #404040 !important;
        }
      `}</style>
      <Component {...pageProps} />
    </ThemeProvider>
  )
}

export default MyApp 