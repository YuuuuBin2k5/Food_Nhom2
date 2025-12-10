import Footer from "./components/layouts/Footer";
import SideBar from "./components/layouts/SideBar";
import { AuthProvider } from "./context/AuthContext";
import AppRoutes from "./routes/AppRoutes";

function App() {
  return (
    <>
      {/* Component AppRoutes quản lý tất cả các Routes */}
      <AuthProvider>
        <AppRoutes />
      </AuthProvider>
    </>
  );
}

export default App;
