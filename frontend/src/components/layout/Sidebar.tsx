import { NavLink } from "react-router-dom";
import {
  LayoutDashboard,
  FolderKanban,
  NotebookPen,
  Settings,
  PanelLeftClose,
  PanelLeftOpen,
} from "lucide-react";

import StudyMateLogo from "../brand/StudyMateLogo";

interface SidebarProps {
  collapsed: boolean;
  onToggle: () => void;
}

const navigationItems = [
  {
    name: "Dashboard",
    path: "/dashboard",
    icon: LayoutDashboard,
  },
  {
    name: "Workspaces",
    path: "/workspaces",
    icon: FolderKanban,
  },
  {
    name: "Notes",
    path: "/notes",
    icon: NotebookPen,
  },
  {
    name: "Settings",
    path: "/settings",
    icon: Settings,
  },
];

export default function Sidebar({
  collapsed,
  onToggle,
}: SidebarProps) {
  return (
    <aside className={`sidebar ${collapsed ? "collapsed" : ""}`}>

      <div className="sidebar-top">

        <div className="sidebar-brand">
          <StudyMateLogo collapsed={collapsed} />
        </div>

        <button
          className="sidebar-toggle"
          onClick={onToggle}
          title={collapsed ? "Expand sidebar" : "Collapse sidebar"}
        >
          {collapsed ? (
            <PanelLeftOpen size={19} />
          ) : (
            <PanelLeftClose size={19} />
          )}
        </button>

        <nav className="sidebar-nav">
          {navigationItems.map((item) => {
            const Icon = item.icon;

            return (
              <NavLink
                key={item.path}
                to={item.path}
                className={({ isActive }) =>
                  `nav-item ${isActive ? "active" : ""}`
                }
                title={collapsed ? item.name : undefined}
              >
                <Icon size={20} />

                {!collapsed && (
                  <span>{item.name}</span>
                )}
              </NavLink>
            );
          })}
        </nav>

      </div>

      <div className="sidebar-footer">
        <div className="user-avatar">S</div>

        {!collapsed && (
          <div className="user-details">
            <strong>Student</strong>
            <span>StudyMate Account</span>
          </div>
        )}
      </div>

    </aside>
  );
}