import { StrictMode } from "react";
import { createRoot } from "react-dom/client";
import "./index.css";
import App from "./App.jsx";
import { BrowserRouter } from "react-router";
import { logWebVitals } from "./utils/performanceMonitor";

// Log Web Vitals for performance monitoring
if (process.env.NODE_ENV === 'development') {
  logWebVitals();
}

createRoot(document.getElementById("root")).render(
  <StrictMode>
    <BrowserRouter>
      <App />
    </BrowserRouter>
  </StrictMode>
);
