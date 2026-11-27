export default function Dashboard() {
  return (
    <div>
      <h1 className="page-title">Welcome to StudyMate</h1>

      <p className="page-description">
        Continue learning and explore your study materials.
      </p>

      <div className="dashboard-grid">
        <div className="card">
          <h3>My Workspaces</h3>
          <p>Organize your study materials.</p>
        </div>

        <div className="card">
          <h3>My Documents</h3>
          <p>Access your uploaded PDFs.</p>
        </div>

        <div className="card">
          <h3>AI Conversations</h3>
          <p>Continue learning with AI.</p>
        </div>
      </div>
    </div>
  );
}