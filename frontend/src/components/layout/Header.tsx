import { Bell, Sparkles } from "lucide-react";

interface HeaderProps {
  title: string;
}

export default function Header({ title }: HeaderProps) {
  return (
    <header className="app-header">

      <div>
        <h2>{title}</h2>
        <p>Your AI-powered study workspace</p>
      </div>

      <div className="header-actions">

        <div className="ai-status">
          <Sparkles size={16} />
          <span>AI Workspace</span>
        </div>

        <button className="icon-button" title="Notifications">
          <Bell size={20} />
        </button>

        <div className="header-avatar">S</div>

      </div>

    </header>
  );
}