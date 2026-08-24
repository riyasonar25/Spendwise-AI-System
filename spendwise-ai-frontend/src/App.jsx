import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";

import Layout from "./components/Layout";
import Login from "./pages/Login";
import Register from "./pages/Register";

import Dashboard from "./pages/Dashboard";
import AddExpense from "./pages/AddExpense";
import DailyTracker from "./pages/DailyTracker";
import SplitExpense from "./pages/SplitExpense";
import Balance from "./pages/Balance";
import SplitRecord from "./pages/SplitRecord";

function App() {
  const isLoggedIn = !!localStorage.getItem("token");

  return (
    <BrowserRouter>
      <Routes>

        {/* =========================
            PUBLIC ROUTES
        ========================= */}

        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register />} />


        {/* =========================
            PROTECTED ROUTES
        ========================= */}

        <Route
          path="/"
          element={
            isLoggedIn
              ? <Layout />
              : <Navigate to="/login" />
          }
        >

          <Route index element={<Dashboard />} />

          <Route
            path="add-expense"
            element={<AddExpense />}
          />

          <Route
            path="daily-tracker"
            element={<DailyTracker />}
          />

          <Route
            path="split-expense"
            element={<SplitExpense />}
          />

          <Route
            path="split-record"
            element={<SplitRecord />}
          />

          <Route
            path="balance"
            element={<Balance />}
          />

        </Route>


        {/* =========================
            DEFAULT REDIRECT
        ========================= */}

        <Route
          path="*"
          element={<Navigate to="/" />}
        />

      </Routes>
    </BrowserRouter>
  );
}

export default App;