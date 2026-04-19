import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { motion } from "framer-motion";

function Register() {
  const [name, setName] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const navigate = useNavigate();

  const handleRegister = async (e) => {
    e.preventDefault();

    if (!name || !email || !password) {
      alert("Please fill all fields ❌");
      return;
    }

    try {
      const res = await fetch("http://localhost:8083/api/auth/register", {
        method: "POST",
        headers: {
          "Content-Type": "application/json"
        },
        body: JSON.stringify({
          name,
          email,
          password
        })
      });

      const data = await res.text();

      if (res.ok) {
        alert("Registration successful ✅");
        navigate("/login"); // login page par redirect
      } else {
        alert(data || "Registration failed ❌");
      }

    } catch (err) {
      console.error(err);
      alert("Server error ❌");
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center 
    bg-gradient-to-br from-pink-100 via-purple-100 to-indigo-100">

      <motion.div
        initial={{ opacity: 0, y: 40 }}
        animate={{ opacity: 1, y: 0 }}
        className="bg-white/70 backdrop-blur-lg p-8 rounded-3xl shadow-xl w-full max-w-md"
      >
        <h2 className="text-2xl font-bold text-center text-gray-800 mb-6">
          Create Account 📝
        </h2>

        <form onSubmit={handleRegister} className="space-y-4">

          <input
            type="text"
            placeholder="Name"
            value={name}
            onChange={(e) => setName(e.target.value)}
            className="w-full p-3 rounded-xl border focus:outline-none focus:ring-2 focus:ring-purple-400"
          />

          <input
            type="email"
            placeholder="Email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            className="w-full p-3 rounded-xl border focus:outline-none focus:ring-2 focus:ring-purple-400"
          />

          <input
            type="password"
            placeholder="Password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            className="w-full p-3 rounded-xl border focus:outline-none focus:ring-2 focus:ring-purple-400"
          />

          <button
            type="submit"
            className="w-full py-3 bg-gradient-to-r from-purple-500 to-pink-500 text-white rounded-xl shadow-md hover:scale-105 transition"
          >
            Register
          </button>

        </form>

        {/* LOGIN LINK */}
        <p className="text-sm text-center mt-4">
          Already have an account?{" "}
          <span
            className="text-purple-600 cursor-pointer font-semibold"
            onClick={() => navigate("/login")}
          >
            Login
          </span>
        </p>

      </motion.div>
    </div>
  );
}

export default Register;