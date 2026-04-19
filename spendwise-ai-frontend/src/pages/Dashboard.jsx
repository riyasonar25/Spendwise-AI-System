import { useNavigate } from "react-router-dom";
import { motion } from "framer-motion";

function Dashboard() {
  const navigate = useNavigate();

  return (
    // ❌ REMOVE min-h-screen
    <div className="bg-gradient-to-br from-pink-100 via-purple-100 to-indigo-100 flex flex-col items-center p-6 w-full">

      {/* HERO SECTION */}
      <motion.div
        initial={{ opacity: 0, y: -30 }}
        animate={{ opacity: 1, y: 0 }}
        className="w-full max-w-6xl bg-white rounded-3xl shadow-lg p-10 flex flex-col md:flex-row items-center justify-between"
      >
        {/* LEFT TEXT */}
        <div className="text-left max-w-lg">
          <h1 className="text-4xl font-bold text-gray-800 mb-4">
            SpendWise 💸
          </h1>

          <p className="bg-gradient-to-r from-purple-500 to-indigo-500 bg-clip-text text-transparent mb-6">
            Track your spending, split expenses with friends, and manage your finances beautifully.
          </p>

          <button
            onClick={() => navigate("/add-expense")}
            className="px-6 py-3 bg-gradient-to-r from-pink-500 to-purple-500 text-black rounded-xl shadow-md hover:scale-105 transition"
          >
            Get Started
          </button>
        </div>

        {/* RIGHT CARD */}
        <div className="mt-8 md:mt-0">
          <div className="w-64 h-36 bg-gradient-to-r from-pink-500 to-purple-500 rounded-2xl shadow-lg text-black p-4 flex flex-col justify-between">
            <p className="text-sm opacity-80">Total Balance</p>
            <h2 className="text-2xl font-bold">₹45,200</h2>
            <p className="text-xs opacity-70">Updated Today</p>
          </div>
        </div>
      </motion.div>

      {/* FEATURES */}
      <div className="mt-12 grid grid-cols-1 md:grid-cols-4 gap-6 w-full max-w-6xl">

        {[
          { title: "Add Expense", path: "/add-expense", color: "from-pink-500 to-purple-500" },
          { title: "Daily Tracker", path: "/daily-tracker", color: "from-indigo-400 to-blue-400" },
          { title: "Split Expense", path: "/split-expense", color: "from-purple-400 to-indigo-400" },
          { title: "Balance", path: "/balance", color: "from-pink-400 to-red-400" },
        ].map((item, index) => (
          <motion.div
            key={index}
            whileHover={{ scale: 1.07 }}
            whileTap={{ scale: 0.95 }}
            className={`cursor-pointer p-6 rounded-2xl text-black shadow-md bg-gradient-to-r ${item.color}`}
            onClick={() => navigate(item.path)}
          >
            <h2 className="text-lg font-semibold">{item.title}</h2>
            <p className="text-sm opacity-80 mt-2">
              Manage your {item.title.toLowerCase()}
            </p>
          </motion.div>
        ))}
      </div>
      {/* EXTRA */}
      <div className="mt-16 w-full max-w-5xl text-center">
        <h2 className="text-2xl font-bold text-gray-800 mb-4">
          Why Choose SpendWise?
        </h2>
        <p className="text-gray-600 mb-10">
          Clean UI, smart tracking, and easy expense splitting — all in one place.
        </p>

        <div className="grid md:grid-cols-3 gap-6">
          <div className="bg-white p-6 rounded-2xl shadow-sm hover:shadow-md transition">
            <h3 className="font-semibold text-lg">📊 Analytics</h3>
            <p className="text-sm text-gray-600 mt-2">
              Visualize your expenses clearly
            </p>
          </div>

          <div className="bg-white p-6 rounded-2xl shadow-sm hover:shadow-md transition">
            <h3 className="font-semibold text-lg">⚡ Fast</h3>
            <p className="text-sm text-gray-600 mt-2">
              Lightning fast experience
            </p>
          </div>

          <div className="bg-white p-6 rounded-2xl shadow-sm hover:shadow-md transition">
            <h3 className="font-semibold text-lg">🔐 Secure</h3>
            <p className="text-sm text-gray-600 mt-2">
              JWT based secure system
            </p>
          </div>
    </div>
      </div>
       </div>
  );
}
export default Dashboard;