import { useEffect, useState } from "react";
import { motion } from "framer-motion";

function Balance() {
  const [expenses, setExpenses] = useState([]);
  const [total, setTotal] = useState(0);

  useEffect(() => {
    fetchExpenses();
  }, []);

  const fetchExpenses = async () => {
    try {
      const token = localStorage.getItem("token");

      const res = await fetch("http://localhost:8083/api/expenses", {
        headers: {
          "Authorization": `Bearer ${token}`
        }
      });

      const data = await res.json();
      setExpenses(data);

      // calculate total
      const totalAmount = data.reduce((sum, item) => sum + item.amount, 0);
      setTotal(totalAmount);

    } catch (err) {
      console.error(err);
    }
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-pink-100 via-purple-100 to-indigo-100 p-6">

      <motion.div
        initial={{ opacity: 0, y: 40 }}
        animate={{ opacity: 1, y: 0 }}
        className="max-w-5xl mx-auto"
      >

        {/* TITLE */}
        <h2 className="text-3xl font-bold text-gray-800 mb-6 text-center">
          Your Balance 💰
        </h2>

        {/* TOTAL CARD */}
        <div className="bg-gradient-to-r from-pink-500 to-purple-500 text-white p-6 rounded-2xl shadow-lg text-center mb-10">
          <p className="text-sm opacity-80">Total Expenses</p>
          <h1 className="text-3xl font-bold mt-2">₹ {total}</h1>
        </div>

        {/* EXPENSE LIST */}
        <div className="bg-white rounded-2xl shadow-md p-6">

          <h3 className="text-xl font-semibold mb-4 text-gray-700">
            Recent Expenses
          </h3>

          {expenses.length === 0 ? (
            <p className="text-gray-500">No expenses found</p>
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

export default Balance;