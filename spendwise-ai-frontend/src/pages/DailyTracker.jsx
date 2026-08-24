import { useState } from "react";
import { motion } from "framer-motion";

function DailyTracker() {
  const [date, setDate] = useState("");
  const [expenses, setExpenses] = useState([]);
  const [total, setTotal] = useState(0);

  const fetchByDate = async () => {
    if (!date) {
      alert("Please select date ❌");
      return;
    }

    try {
      const token = localStorage.getItem("token");

      const res = await fetch(
        `http://localhost:8083/api/expenses/date/${date}`,
        {
          headers: {
            "Authorization": `Bearer ${token}`
          }
        }
      );

      const data = await res.json();
      console.log(data);
      setExpenses(data);

      // total calculate
      const totalAmount = data.reduce((sum, item) => sum + item.amount, 0);
      setTotal(totalAmount);

    } catch (err) {
      console.error(err);
      alert("Error fetching data ❌");
    }
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-pink-100 via-purple-100 to-indigo-100 p-6 flex justify-center">

      <motion.div
        initial={{ opacity: 0, y: 40 }}
        animate={{ opacity: 1, y: 0 }}
        className="w-full max-w-4xl"
      >

        {/* TITLE */}
        <h2 className="text-3xl font-bold text-center text-gray-800 mb-6">
          Daily Tracker 📅
        </h2>

        {/* DATE SELECT */}
        <div className="bg-white p-6 rounded-2xl shadow-md mb-6 flex gap-4 items-center justify-center">
          
          <input
            type="date"
            value={date}
            onChange={(e) => setDate(e.target.value)}
            className="p-3 border rounded-xl focus:outline-none focus:ring-2 focus:ring-purple-400"
          />

          <button
            onClick={fetchByDate}
            className="px-6 py-3 bg-gradient-to-r from-purple-500 to-pink-500 text-white rounded-xl hover:scale-105 transition"
          >
            Get Data
          </button>

        </div>

        {/* TOTAL */}
        {total > 0 && (
          <div className="bg-gradient-to-r from-purple-500 to-pink-500 text-white p-5 rounded-2xl shadow-lg text-center mb-6">
            <p className="text-sm opacity-80">Total for Selected Date</p>
            <h2 className="text-2xl font-bold">₹ {total}</h2>
          </div>
        )}

        {/* EXPENSE LIST */}
        <div className="bg-white rounded-2xl shadow-md p-6">

          {expenses.length === 0 ? (
            <p className="text-gray-500 text-center">
              No data found for selected date
            </p>
          ) : (
            <div className="space-y-3">
              {expenses.map((exp, index) => (
                <div
                  key={index}
                  className="flex justify-between items-center p-3 border rounded-lg hover:shadow-sm"
                >
                  <div>
                    <p className="font-semibold">{exp.title}</p>
                    <p className="text-sm text-gray-500">{exp.category}</p>
                  </div>

                  <div className="text-right">
                    <p className="font-bold text-purple-600">
                      ₹ {exp.amount}
                    </p>
                    <p className="text-xs text-gray-400">
                      {exp.date}
                    </p>
                  </div>
                </div>
              ))}
            </div>
          )}

        </div>

      </motion.div>
    </div>
  );
}

export default DailyTracker;