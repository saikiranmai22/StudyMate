import { useState } from "react";
import { Outlet, useLocation } from "react-router-dom";

import Sidebar from "./Sidebar";
import Header from "./Header";

export default function AppLayout() {

  const [collapsed, setCollapsed] = useState(false);

  const location = useLocation();

  const pageTitles: Record<string, string> = {
    "/dashboard": "Dashboard",
    "/workspaces": "My Workspaces",
    "/notes": "My Notes",
    "/settings": "Settings",
  };

  const title = pageTitles[location.pathname] ?? "StudyMate";

  function toggleSidebar() {
    setCollapsed((previous) => !previous);
  }

  return (
    <div className="app-layout">

      <Sidebar
        collapsed={collapsed}
        onToggle={toggleSidebar}
      />

      <div className={`main-area ${collapsed ? "expanded" : ""}`}>

        <Header title={title} />

        <main className="page-content">
          <Outlet />
        </main>

      </div>

    </div>
  );
}