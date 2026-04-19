import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";

import Layout from "./components/Layout";
import Login from "./pages/Login";
import Register from "./pages/Register";

import Dashboard from "./pages/Dashboard";
import AddExpense from "./pages/AddExpense";
import DailyTracker from "./pages/DailyTracker";
import SplitExpense from "./pages/SplitExpense";
import Balance from "./pages/Balance";

function App() {
  const isLoggedIn = !!localStorage.getItem("token");

  return (
    <BrowserRouter>
      <Routes>

        {/* ✅ PUBLIC ROUTES */}
        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register />} />

        {/* ✅ DEFAULT */}
        <Route path="/" element={<Navigate to="/login" />} />

        {/* ✅ PROTECTED ROUTES */}
        <Route
          path="/dashboard"
          element={isLoggedIn ? <Layout /> : <Navigate to="/login" />}
        >
          <Route index element={<Dashboard />} />
        </Route>

        <Route
          path="/add-expense"
          element={
            isLoggedIn ? (
              <Layout><AddExpense /></Layout>
            ) : (
              <Navigate to="/login" />
            )
          }
        />

        <Route
          path="/daily-tracker"
          element={
            isLoggedIn ? (
              <Layout><DailyTracker /></Layout>
            ) : (
              <Navigate to="/login" />
            )
          }
        />

        <Route
          path="/split-expense"
          element={
            isLoggedIn ? (
              <Layout><SplitExpense /></Layout>
            ) : (
              <Navigate to="/login" />
            )
          }
        />

        <Route
          path="/balance"
          element={
            isLoggedIn ? (
              <Layout><Balance /></Layout>
            ) : (
              <Navigate to="/login" />
            )
          }
        />

      </Routes>
    </BrowserRouter>
  );
}

export default App;