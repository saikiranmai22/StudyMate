interface StudyMateLogoProps {
  collapsed?: boolean;
}

export default function StudyMateLogo({
  collapsed = false,
}: StudyMateLogoProps) {
  return (
    <div className="studymate-logo">

      <svg
        width="38"
        height="38"
        viewBox="0 0 48 48"
        fill="none"
        xmlns="http://www.w3.org/2000/svg"
      >
        {/* Left page */}
        <path
          d="M5 12C12 9 18 11 23 16V38C17 33 11 32 5 35V12Z"
          fill="#8B5CF6"
        />

        {/* Right page */}
        <path
          d="M25 16C30 11 36 9 43 12V35C37 32 31 33 25 38V16Z"
          fill="#22D3EE"
        />

        {/* Book spine */}
        <path
          d="M24 16V39"
          stroke="#F1F5F9"
          strokeWidth="2"
          strokeLinecap="round"
        />

        {/* AI connection nodes */}
        <circle cx="34" cy="7" r="3" fill="#22D3EE" />
        <circle cx="42" cy="5" r="2" fill="#8B5CF6" />
        <path
          d="M36 7L40 5"
          stroke="#22D3EE"
          strokeWidth="1.5"
        />
      </svg>

      {!collapsed && (
        <span className="studymate-wordmark">
          Study<span>Mate</span>
        </span>
      )}

    </div>
  );
}