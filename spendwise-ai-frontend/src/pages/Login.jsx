import { useState } from "react";
import { useNavigate } from "react-router-dom";

function Login() {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const navigate = useNavigate();

  const handleLogin = async (e) => {
    e.preventDefault();

    if (!email || !password) {
      alert("Enter email & password");
      return;
    }

    try {
      const res = await fetch("http://localhost:8083/api/auth/login", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ email, password })
      });

      const data = await res.json();

      if (res.ok) {
        localStorage.setItem("token", data.token);

        // ✅ FORCE REDIRECT
        window.location.href = "/dashboard";
      } else {
        alert("Invalid credentials");
      }

    } catch (err) {
      alert("Server error");
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-gradient-to-br from-pink-100 via-purple-100 to-indigo-100">

      <form onSubmit={handleLogin} className="bg-white p-8 rounded-2xl shadow-md w-80 space-y-4">
        <h2 className="text-xl font-bold text-center">Login</h2>

        <input
          type="email"
          placeholder="Email"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
          className="w-full p-2 border rounded"
        />

        <input
          type="password"
          placeholder="Password"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          className="w-full p-2 border rounded"
        />

        <button className="w-full bg-purple-500 text-white py-2 rounded">
          Login
        </button>

        <p className="text-center text-sm">
          Don't have account?{" "}
          <span
            onClick={() => navigate("/register")}
            className="text-purple-600 cursor-pointer"
          >
            Register
          </span>
        </p>

      </form>
    </div>
  );
}

export default Login;