import React from "react";
import { Outlet } from "react-router-dom";
import Sidebar from "./Sidebar";
import Footer from "./Footer";

const MainLayout = () => {
  return (
    <div className="app-container min-h-screen relative flex flex-col">
      <Sidebar />
      {/* Main: add top padding matching header height */}
      <main className="flex-1 pt-24 transition-all duration-300 w-full">
        {/* Component AppRoutes quản lý tất cả các Routes */}
        <Outlet />
      </main>
      <Footer />
    </div>
  );
};

export default MainLayout;
