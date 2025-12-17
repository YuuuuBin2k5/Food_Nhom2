import React from "react";
import { Outlet } from "react-router-dom";
import Sidebar from "./Sidebar";

const MainLayout = () => {
  return (
    <div className="app-container min-h-screen relative flex flex-col">
      <Sidebar />
      {/* Main: add top padding matching header height (h-16 / md:h-20) */}
      <main className="min-h-screen transition-colors duration-700 w-full">
        {/* Component AppRoutes quản lý tất cả các Routes */}
        <Outlet />
      </main>
    </div>
  );
};

export default MainLayout;
