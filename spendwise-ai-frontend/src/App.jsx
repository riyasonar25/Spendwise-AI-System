import { BrowserRouter, Routes, Route } from "react-router-dom";
import Layout from "./components/Layout";

import Login from "./pages/Login";
import Dashboard from "./pages/Dashboard";
import AddExpense from "./pages/AddExpense";
import DailyTracker from "./pages/DailyTracker";
import SplitExpense from "./pages/SplitExpense";
import Balance from "./pages/Balance";

function App() {
  return (
    <BrowserRouter>
      <Routes>

        {/* ✅ LOGIN FIRST */}
        <Route path="/" element={<Login />} />
        <Route path="/login" element={<Login />} />

        {/* ✅ APP ROUTES */}
        <Route path="/dashboard" element={<Layout><Dashboard /></Layout>} />
        <Route path="/add-expense" element={<Layout><AddExpense /></Layout>} />
        <Route path="/daily-tracker" element={<Layout><DailyTracker /></Layout>} />
        <Route path="/split-expense" element={<Layout><SplitExpense /></Layout>} />
        <Route path="/balance" element={<Layout><Balance /></Layout>} />

      </Routes>
    </BrowserRouter>
  );
}

export default App;