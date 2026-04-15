import { useNavigate } from "react-router-dom";

function Layout({ children }) {
  const navigate = useNavigate();

  return (
    <div className="flex">

      {/* SIDEBAR */}
      <div className="w-64 min-h-screen 
      bg-gradient-to-b from-purple-400 via-violet-400 to-indigo-400 
      text-white p-6 shadow-lg">

        <h2 
          className="text-2xl font-bold mb-8 cursor-pointer"
          onClick={() => navigate("/dashboard")}  // ✅ FIXED
        >
          💰 SpendWise
        </h2>

        <ul className="space-y-4">

          <li 
            onClick={() => navigate("/dashboard")}   // ✅ FIXED
            className="cursor-pointer hover:bg-white/20 p-2 rounded"
          >
            Dashboard
          </li>

          <li 
            onClick={() => navigate("/add-expense")}
            className="cursor-pointer hover:bg-white/20 p-2 rounded"
          >
            Add Expense
          </li>

          <li 
            onClick={() => navigate("/daily-tracker")}
            className="cursor-pointer hover:bg-white/20 p-2 rounded"
          >
            Daily Tracker
          </li>

          <li 
            onClick={() => navigate("/split-expense")}
            className="cursor-pointer hover:bg-white/20 p-2 rounded"
          >
            Split Expense
          </li>

          <li 
            onClick={() => navigate("/balance")}
            className="cursor-pointer hover:bg-white/20 p-2 rounded"
          >
            Balance
          </li>

        </ul>
      </div>

      {/* MAIN CONTENT */}
      <div className="flex-1">
        {children}
      </div>

    </div>
  );
}

export default Layout;