import React from "react";

const MainLayout = () => {
  return (
    <div className="app-container min-h-screen relative">
      {/* Header */}
      <SideBar />

      {/* Main */}
      <main className="pt-12 min-h-screen transition-colors duration-700 w-full">
        {/* Component AppRoutes quản lý tất cả các Routes */}
        <Outlet />
      </main>
    </div>
  );
};

export default MainLayout;
