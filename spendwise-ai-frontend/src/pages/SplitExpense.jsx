
import { useState } from "react";
import { motion } from "framer-motion";

function SplitExpense() {
  const [form, setForm] = useState({
    groupName: "",
    amount: "",
    paidByName: "",
    paidByEmail: "",
  });

  const [members, setMembers] = useState([
    {
      name: "",
      email: "",
    },
  ]);

  const [splitType, setSplitType] = useState("EQUAL");
  const [customAmounts, setCustomAmounts] = useState({});
  const [loading, setLoading] = useState(false);

  // =========================
  // FORM CHANGE
  // =========================

  const handleChange = (e) => {
    const { name, value } = e.target;

    setForm((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  // =========================
  // MEMBER CHANGE
  // =========================

  const handleMemberChange = (index, field, value) => {
    setMembers((prev) =>
      prev.map((member, i) =>
        i === index
          ? {
              ...member,
              [field]: value,
            }
          : member
      )
    );

    if (field === "email") {
      setCustomAmounts((prev) => {
        const updated = { ...prev };

        const oldEmail = members[index]?.email;

        if (oldEmail) {
          delete updated[oldEmail];
        }

        return updated;
      });
    }
  };

  // =========================
  // ADD MEMBER
  // =========================

  const addMember = () => {
    setMembers((prev) => [
      ...prev,
      {
        name: "",
        email: "",
      },
    ]);
  };

  // =========================
  // REMOVE MEMBER
  // =========================

  const removeMember = (index) => {
    if (members.length === 1) {
      return;
    }

    const removedEmail = members[index]?.email;

    setMembers((prev) =>
      prev.filter((_, i) => i !== index)
    );

    if (removedEmail) {
      setCustomAmounts((prev) => {
        const updated = { ...prev };
        delete updated[removedEmail];
        return updated;
      });
    }
  };

  // =========================
  // TOTAL
  // =========================

  const totalAmount = Number(form.amount) || 0;

  // =========================
  // VALID MEMBERS
  // =========================

  const validMembers = members.filter(
    (member) =>
      member.name.trim() !== "" &&
      member.email.trim() !== ""
  );

  // =========================
  // CUSTOM AMOUNT
  // =========================

  const handleCustomAmountChange = (
    email,
    value
  ) => {
    setCustomAmounts((prev) => ({
      ...prev,
      [email]: value,
    }));
  };

  // =========================
  // SHARE
  // =========================

  const getMemberShare = (email) => {
    if (splitType === "EQUAL") {
      if (validMembers.length === 0) {
        return 0;
      }

      return totalAmount / validMembers.length;
    }

    return Number(customAmounts[email]) || 0;
  };

  // =========================
  // CUSTOM TOTAL
  // =========================

  const customTotal = validMembers.reduce(
    (sum, member) => {
      return (
        sum +
        (Number(customAmounts[member.email]) || 0)
      );
    },
    0
  );

  const customSplitValid =
    splitType === "EQUAL" ||
    Math.abs(customTotal - totalAmount) < 0.01;

  // =========================
  // SUBMIT
  // =========================

  const handleSubmit = async (e) => {
    e.preventDefault();

    const token = localStorage.getItem("token");

    if (!token) {
      alert("Please login first.");
      return;
    }

    // =========================
    // JWT EMAIL
    // =========================

    let loggedInEmail;

    try {
      const payload = JSON.parse(
        atob(token.split(".")[1])
      );

      loggedInEmail = payload.sub;
    } catch (error) {
      console.error(error);

      alert(
        "Invalid login session. Please login again."
      );

      return;
    }

    // =========================
    // BASIC VALIDATION
    // =========================

    if (!form.groupName.trim()) {
      alert("Please enter group name.");
      return;
    }

    if (
      !form.amount ||
      Number(form.amount) <= 0
    ) {
      alert("Please enter a valid amount.");
      return;
    }

    if (!form.paidByName.trim()) {
      alert("Please enter payer name.");
      return;
    }

    if (!form.paidByEmail.trim()) {
      alert("Please enter payer email.");
      return;
    }

    if (validMembers.length === 0) {
      alert(
        "Please add at least one member with name and email."
      );
      return;
    }

    // =========================
    // EXACT VALIDATION
    // =========================

    if (splitType === "EXACT") {
      const hasEmptyAmount = validMembers.some(
        (member) =>
          customAmounts[member.email] === undefined ||
          customAmounts[member.email] === "" ||
          Number(customAmounts[member.email]) < 0
      );

      if (hasEmptyAmount) {
        alert(
          "Please enter amount for every member."
        );
        return;
      }

      if (
        Math.abs(customTotal - totalAmount) >
        0.01
      ) {
        alert(
          `Custom split total must be ₹${totalAmount}. Current total is ₹${customTotal.toFixed(
            2
          )}.`
        );

        return;
      }
    }

    // =========================
    // CREATE SPLITS
    // =========================

    const splits = validMembers.map(
      (member) => ({
        name: member.name.trim(),
        email: member.email.trim(),
        amount:
          splitType === "EQUAL"
            ? 0
            : Number(
                customAmounts[member.email]
              ) || 0,
      })
    );

    // =========================
    // REQUEST BODY
    // =========================

    const requestBody = {
      groupName: form.groupName.trim(),

      createdBy: loggedInEmail,

      description: "Expense",

      totalAmount: totalAmount,

      paidBy: form.paidByEmail.trim(),

      paidByName: form.paidByName.trim(),

      splitType: splitType,

      splits: splits,
    };

    console.log(
      "SENDING REQUEST:",
      requestBody
    );

    // =========================
    // API CALL
    // =========================

    try {
      setLoading(true);

      const response = await fetch(
        "http://localhost:8083/api/split/expense/by-group-name",
        {
          method: "POST",

          headers: {
            "Content-Type":
              "application/json",

            Authorization:
              `Bearer ${token}`,
          },

          body: JSON.stringify(
            requestBody
          ),
        }
      );

      const data =
        await response.text();

      console.log(
        "STATUS:",
        response.status
      );

      console.log(
        "RESPONSE:",
        data
      );

      if (!response.ok) {
        alert(
          "❌ Error: " + data
        );

        return;
      }

      alert(
        "✅ Expense split successfully!"
      );

      // =========================
      // GET GROUP
      // =========================

      try {
        const groupsResponse =
          await fetch(
            `http://localhost:8083/api/split/groups?createdBy=${encodeURIComponent(
              loggedInEmail
            )}`,
            {
              method: "GET",

              headers: {
                Authorization:
                  `Bearer ${token}`,

                "Content-Type":
                  "application/json",
              },
            }
          );

        if (groupsResponse.ok) {
          const groups =
            await groupsResponse.json();

          const currentGroup =
            groups.find(
              (group) =>
                group.groupName
                  ?.trim()
                  .toLowerCase() ===
                form.groupName
                  .trim()
                  .toLowerCase()
            );

          if (currentGroup) {
            localStorage.setItem(
              "splitGroupId",
              currentGroup.id
            );
          }
        }
      } catch (error) {
        console.error(
          "Group Fetch Error:",
          error
        );
      }

      // =========================
      // RESET
      // =========================

      setForm({
        groupName: "",
        amount: "",
        paidByName: "",
        paidByEmail: "",
      });

      setMembers([
        {
          name: "",
          email: "",
        },
      ]);

      setSplitType("EQUAL");

      setCustomAmounts({});
    } catch (error) {
      console.error(error);

      alert(
        "❌ Backend Connection Failed"
      );
    } finally {
      setLoading(false);
    }
  };

  // =========================
  // UI
  // =========================

  return (
    <div className="min-h-screen bg-gradient-to-br from-blue-100 via-white to-purple-100 flex justify-center items-center p-6">

      <motion.div
        initial={{
          opacity: 0,
          y: 40,
        }}
        animate={{
          opacity: 1,
          y: 0,
        }}
        transition={{
          duration: 0.5,
        }}
        className="bg-white rounded-3xl shadow-2xl p-8 w-full max-w-xl"
      >

        {/* HEADER */}

        <div className="text-center mb-7">

          <div className="text-5xl mb-3">
            💸
          </div>

          <h1 className="text-3xl font-bold text-purple-700">
            Split Expense
          </h1>

          <p className="text-gray-500 mt-2">
            Easily split expenses with your friends
          </p>

        </div>

        <form
          onSubmit={handleSubmit}
          className="space-y-5"
        >

          {/* GROUP */}

          <div>

            <label className="block text-sm font-semibold text-gray-700 mb-2">
              Group Name
            </label>

            <input
              type="text"
              name="groupName"
              placeholder="e.g. Goa Trip"
              value={form.groupName}
              onChange={handleChange}
              className="w-full border border-gray-300 rounded-xl p-3 focus:outline-none focus:ring-2 focus:ring-purple-400"
              required
            />

          </div>

          {/* TOTAL */}

          <div>

            <label className="block text-sm font-semibold text-gray-700 mb-2">
              Total Expense
            </label>

            <input
              type="number"
              name="amount"
              placeholder="e.g. 4000"
              value={form.amount}
              onChange={handleChange}
              min="1"
              step="0.01"
              className="w-full border border-gray-300 rounded-xl p-3 focus:outline-none focus:ring-2 focus:ring-purple-400"
              required
            />

          </div>

          {/* PAID BY */}

          <div>

            <label className="block text-sm font-semibold text-gray-700 mb-2">
              Paid By
            </label>

            <div className="space-y-3">

              <input
                type="text"
                name="paidByName"
                placeholder="Payer name (e.g. Riya)"
                value={form.paidByName}
                onChange={handleChange}
                className="w-full border border-gray-300 rounded-xl p-3 focus:outline-none focus:ring-2 focus:ring-purple-400"
                required
              />

              <input
                type="email"
                name="paidByEmail"
                placeholder="Payer email"
                value={form.paidByEmail}
                onChange={handleChange}
                className="w-full border border-gray-300 rounded-xl p-3 focus:outline-none focus:ring-2 focus:ring-purple-400"
                required
              />

            </div>

            <p className="text-xs text-gray-500 mt-1">
              Enter the person who paid the complete bill.
            </p>

          </div>

          {/* MEMBERS */}

          <div>

            <label className="block text-sm font-semibold text-gray-700 mb-3">
              People in Split
            </label>

            <div className="space-y-3">

              {members.map(
                (member, index) => (
                  <div
                    key={index}
                    className="border border-gray-200 rounded-xl p-4 bg-gray-50"
                  >

                    <div className="flex justify-between items-center mb-3">

                      <span className="font-semibold text-gray-700">
                        Person {index + 1}
                      </span>

                      {members.length > 1 && (
                        <button
                          type="button"
                          onClick={() =>
                            removeMember(index)
                          }
                          className="text-red-500 text-sm"
                        >
                          Remove
                        </button>
                      )}

                    </div>

                    <div className="space-y-2">

                      <input
                        type="text"
                        placeholder="Name e.g. Neha"
                        value={member.name}
                        onChange={(e) =>
                          handleMemberChange(
                            index,
                            "name",
                            e.target.value
                          )
                        }
                        className="w-full border border-gray-300 rounded-lg p-3 focus:outline-none focus:ring-2 focus:ring-purple-400"
                      />

                      <input
                        type="email"
                        placeholder="Email e.g. neha@gmail.com"
                        value={member.email}
                        onChange={(e) =>
                          handleMemberChange(
                            index,
                            "email",
                            e.target.value
                          )
                        }
                        className="w-full border border-gray-300 rounded-lg p-3 focus:outline-none focus:ring-2 focus:ring-purple-400"
                      />

                    </div>

                  </div>
                )
              )}

            </div>

            <button
              type="button"
              onClick={addMember}
              className="mt-3 w-full border-2 border-dashed border-purple-300 text-purple-600 py-2 rounded-xl hover:bg-purple-50"
            >
              + Add Person
            </button>

          </div>

          {/* SPLIT TYPE */}

          <div>

            <label className="block text-sm font-semibold text-gray-700 mb-3">
              Split Type
            </label>

            <div className="grid grid-cols-2 gap-3">

              <button
                type="button"
                onClick={() =>
                  setSplitType("EQUAL")
                }
                className={`p-4 rounded-xl border-2 ${
                  splitType === "EQUAL"
                    ? "border-purple-600 bg-purple-50 text-purple-700"
                    : "border-gray-200"
                }`}
              >
                ⚖️
                <div className="font-semibold">
                  Equal Split
                </div>
                <div className="text-xs">
                  Divide equally
                </div>
              </button>

              <button
                type="button"
                onClick={() =>
                  setSplitType("EXACT")
                }
                className={`p-4 rounded-xl border-2 ${
                  splitType === "EXACT"
                    ? "border-purple-600 bg-purple-50 text-purple-700"
                    : "border-gray-200"
                }`}
              >
                ✏️
                <div className="font-semibold">
                  Custom Split
                </div>
                <div className="text-xs">
                  Set each share
                </div>
              </button>

            </div>

          </div>

          {/* SUMMARY */}

          {validMembers.length > 0 &&
            totalAmount > 0 && (

              <div className="bg-purple-50 border border-purple-200 rounded-2xl p-5">

                <h3 className="font-bold text-purple-700 text-lg mb-4">
                  💰 Split Summary
                </h3>

                <div className="flex justify-between mb-3">

                  <span>
                    Total Expense
                  </span>

                  <strong>
                    ₹{totalAmount.toFixed(2)}
                  </strong>

                </div>

                <div className="flex justify-between mb-4">

                  <span>
                    Paid By
                  </span>

                  <strong className="text-purple-700">
                    {form.paidByName}
                  </strong>

                </div>

                <div className="border-t pt-4 space-y-3">

                  {validMembers.map(
                    (member) => {

                      const share =
                        getMemberShare(
                          member.email
                        );

                      const isPayer =
                        member.email.toLowerCase() ===
                        form.paidByEmail
                          .trim()
                          .toLowerCase();

                      return (
                        <div
                          key={member.email}
                          className="bg-white rounded-xl p-4 border"
                        >

                          <div className="flex justify-between items-center">

                            <div>

                              <p className="font-semibold text-gray-800">
                                {isPayer
                                  ? "👑 "
                                  : "👤 "}
                                {member.name}
                              </p>

                              <p className="text-xs text-gray-500 mt-1">
                                {isPayer
                                  ? "Your share"
                                  : `Pays to ${form.paidByName}`}
                              </p>

                            </div>

                            <strong className="text-purple-700 text-lg">
                              ₹{share.toFixed(2)}
                            </strong>

                          </div>

                        </div>
                      );
                    }
                  )}

                </div>

                {/* RECEIVE */}

                {form.paidByName &&
                  validMembers.some(
                    (member) =>
                      member.email.toLowerCase() ===
                      form.paidByEmail
                        .trim()
                        .toLowerCase()
                  ) && (
                    <div className="mt-4 bg-green-50 border border-green-200 rounded-xl p-4">

                      <p className="font-bold text-green-700">
                        💰 {form.paidByName} receives
                      </p>

                      <p className="text-green-600 text-sm mt-1">
                        Other members pay their share to{" "}
                        <strong>
                          {form.paidByName}
                        </strong>
                        .
                      </p>

                    </div>
                  )}

              </div>
            )}

          {/* CUSTOM INPUT */}

          {splitType === "EXACT" &&
            validMembers.length > 0 && (

              <div className="bg-blue-50 border border-blue-200 rounded-xl p-4">

                <h3 className="font-semibold text-blue-700 mb-3">
                  ✏️ Enter Each Person's Share
                </h3>

                <div className="space-y-3">

                  {validMembers.map(
                    (member) => (

                      <div
                        key={member.email}
                        className="flex items-center justify-between gap-3 bg-white p-3 rounded-lg"
                      >

                        <div>

                          <p className="font-semibold">
                            {member.name}
                          </p>

                          <p className="text-xs text-gray-500">
                            {member.email}
                          </p>

                        </div>

                        <div className="w-32">

                          <div className="relative">

                            <span className="absolute left-3 top-1/2 -translate-y-1/2">
                              ₹
                            </span>

                            <input
                              type="number"
                              min="0"
                              step="0.01"
                              placeholder="0"
                              value={
                                customAmounts[
                                  member.email
                                ] ?? ""
                              }
                              onChange={(e) =>
                                handleCustomAmountChange(
                                  member.email,
                                  e.target.value
                                )
                              }
                              className="w-full border rounded-lg p-2 pl-7"
                            />

                          </div>

                        </div>

                      </div>

                    )
                  )}

                </div>

                <div className="mt-4 border-t pt-3 flex justify-between">

                  <span className="font-semibold">
                    Split Total
                  </span>

                  <strong
                    className={
                      customSplitValid
                        ? "text-green-600"
                        : "text-red-600"
                    }
                  >
                    ₹{customTotal.toFixed(2)}
                  </strong>

                </div>

                {!customSplitValid && (
                  <p className="text-red-600 text-xs mt-2">
                    ⚠️ Shares must add up to ₹
                    {totalAmount.toFixed(2)}
                  </p>
                )}

              </div>
            )}

          {/* INFO */}

          <div className="bg-yellow-50 border border-yellow-200 rounded-xl p-4">

            <p className="text-sm text-yellow-800">

              💡 Example: If Riya pays ₹4000 and
              Riya's share is ₹2000 while Neha's
              share is ₹2000, Neha pays ₹2000 to Riya.

            </p>

          </div>

          {/* SUBMIT */}

          <button
            type="submit"
            disabled={
              loading ||
              !customSplitValid ||
              validMembers.length === 0
            }
            className="w-full bg-gradient-to-r from-blue-600 to-purple-600 text-white py-3 rounded-xl font-semibold shadow-lg disabled:opacity-60"
          >

            {loading
              ? "Splitting Expense..."
              : "Split Now 🚀"}

          </button>

        </form>

      </motion.div>

    </div>
  );
}

export default SplitExpense;
