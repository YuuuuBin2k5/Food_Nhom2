import React from "react";
import { Outlet } from "react-router-dom";
import Sidebar from "./Sidebar";
import Footer from "./Footer";

const MainLayout = () => {
  return (
    <div className="app-container min-h-screen relative flex flex-col">
      <Sidebar />
      {/* Main: add top padding matching header height (h-20) */}
      <main className="flex-1 pt-20 transition-colors duration-700 w-full">
        {/* Component AppRoutes quản lý tất cả các Routes */}
        <Outlet />
      </main>
      <Footer />
    </div>
  );
};

export default MainLayout;
