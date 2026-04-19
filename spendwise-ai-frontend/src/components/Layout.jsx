import { useNavigate, Outlet } from "react-router-dom";

function Layout() {
  const navigate = useNavigate();

  return (
    <div className="flex">

      {/* SIDEBAR */}
      <div className="w-64 min-h-screen bg-gradient-to-b from-purple-400 to-indigo-400 p-6 text-black">

        <h2 className="text-2xl font-bold mb-8 cursor-pointer"
            onClick={() => navigate("/dashboard")}>
          💰 SpendWise
        </h2>

        <ul className="space-y-4">
          <li onClick={() => navigate("/dashboard")} className="cursor-pointer">Dashboard</li>
          <li onClick={() => navigate("/add-expense")} className="cursor-pointer">Add Expense</li>
          <li onClick={() => navigate("/daily-tracker")} className="cursor-pointer">Daily Tracker</li>
          <li onClick={() => navigate("/split-expense")} className="cursor-pointer">Split Expense</li>
          <li onClick={() => navigate("/balance")} className="cursor-pointer">Balance</li>
        </ul>

        {/* ✅ LOGOUT */}
        <button
          onClick={() => {
            localStorage.removeItem("token");
            window.location.href = "/login";
          }}
          className="mt-10 w-full bg-gradient-to-r from-red-400 to-pink-500 py-2 rounded-xl"
        >
          Logout
        </button>

      </div>

      {/* MAIN CONTENT */}
      <div className="flex-1 p-6">
        <Outlet />
      </div>

    </div>
  );
}

export default Layout;