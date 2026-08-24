
import { useEffect, useState } from "react";
import { motion } from "framer-motion";

function SplitRecord() {
  const [records, setRecords] = useState([]);
  const [search, setSearch] = useState("");
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [updating, setUpdating] = useState(null);
  const [deleting, setDeleting] = useState(null);

  const token = localStorage.getItem("token");

  // =========================================================
  // GET LOGGED-IN USER EMAIL FROM JWT
  // =========================================================

  const getLoggedInEmail = () => {
    try {
      if (!token) return null;

      const payload = JSON.parse(
        atob(token.split(".")[1])
      );

      return payload.sub;
    } catch (error) {
      console.error("JWT Error:", error);
      return null;
    }
  };

  const loggedInEmail = getLoggedInEmail();

  // =========================================================
  // FETCH ALL SPLIT RECORDS
  // =========================================================

  useEffect(() => {
    const fetchAllRecords = async () => {
      if (!token) {
        setError("Please login first.");
        setLoading(false);
        return;
      }

      if (!loggedInEmail) {
        setError("Unable to identify logged-in user.");
        setLoading(false);
        return;
      }

      try {
        setLoading(true);
        setError("");

        // -----------------------------------------------------
        // GET GROUPS
        // -----------------------------------------------------

        const groupsResponse = await fetch(
          `http://localhost:8083/api/split/groups?createdBy=${encodeURIComponent(
            loggedInEmail
          )}`,
          {
            method: "GET",
            headers: {
              Authorization: `Bearer ${token}`,
              "Content-Type": "application/json",
            },
          }
        );

        if (!groupsResponse.ok) {
          throw new Error("Failed to fetch groups");
        }

        const groups = await groupsResponse.json();

        if (!groups || groups.length === 0) {
          setRecords([]);
          setLoading(false);
          return;
        }

        // -----------------------------------------------------
        // FETCH RECORDS OF EVERY GROUP
        // -----------------------------------------------------

        let allRecords = [];

        for (const group of groups) {
          try {
            const response = await fetch(
              `http://localhost:8083/api/split/record/${group.id}`,
              {
                method: "GET",
                headers: {
                  Authorization: `Bearer ${token}`,
                  "Content-Type": "application/json",
                },
              }
            );

            if (!response.ok) {
              console.warn(
                `Failed to fetch group ${group.id}`
              );
              continue;
            }

            const data = await response.json();

            const recordsWithGroupName = data.map(
              (record) => ({
                ...record,
                groupName: group.groupName,
              })
            );

            allRecords = [
              ...allRecords,
              ...recordsWithGroupName,
            ];
          } catch (groupError) {
            console.error(
              `Error fetching group ${group.id}:`,
              groupError
            );
          }
        }

        // -----------------------------------------------------
        // LATEST FIRST
        // -----------------------------------------------------

        allRecords.sort(
          (a, b) =>
            Number(b.expenseId) -
            Number(a.expenseId)
        );

        setRecords(allRecords);
      } catch (err) {
        console.error(
          "Fetch split records error:",
          err
        );

        setError(
          "Unable to load split records."
        );
      } finally {
        setLoading(false);
      }
    };

    fetchAllRecords();
  }, [token, loggedInEmail]);

  // =========================================================
  // UPDATE PAID / PENDING STATUS
  // =========================================================

  const updateStatus = async (
    expenseId,
    memberEmail,
    currentStatus
  ) => {
    const newStatus =
      currentStatus === "PAID"
        ? "PENDING"
        : "PAID";

    const updateKey =
      `${expenseId}-${memberEmail}`;

    try {
      setUpdating(updateKey);

      const response = await fetch(
        `http://localhost:8083/api/split/record/status?expenseId=${expenseId}&memberEmail=${encodeURIComponent(
          memberEmail
        )}&status=${newStatus}`,
        {
          method: "PUT",
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );

      const message = await response.text();

      if (!response.ok) {
        alert("❌ " + message);
        return;
      }

      // -----------------------------------------------------
      // UPDATE UI
      // -----------------------------------------------------

      setRecords((previousRecords) =>
        previousRecords.map((record) => {
          if (record.expenseId !== expenseId) {
            return record;
          }

          return {
            ...record,

            members: record.members?.map(
              (member) => {
                if (
                  member.email?.toLowerCase() ===
                  memberEmail?.toLowerCase()
                ) {
                  return {
                    ...member,
                    status: newStatus,
                  };
                }

                return member;
              }
            ),
          };
        })
      );
    } catch (error) {
      console.error(
        "Status update error:",
        error
      );

      alert(
        "❌ Unable to update payment status."
      );
    } finally {
      setUpdating(null);
    }
  };

  // =========================================================
  // CHECK WHETHER ALL MEMBERS ARE PAID
  // =========================================================

  const isRecordFullyPaid = (record) => {
    if (
      !record.members ||
      record.members.length === 0
    ) {
      return false;
    }

    return record.members.every(
      (member) =>
        member.status?.toUpperCase() === "PAID"
    );
  };

  // =========================================================
  // DELETE RECORD
  // =========================================================

  const deleteRecord = async (record) => {
    const expenseId = record.expenseId;

    // -------------------------------------------------------
    // SAFETY CHECK
    // -------------------------------------------------------

    if (!isRecordFullyPaid(record)) {
      alert(
        "⚠️ You can delete this record only after all payments are marked as Paid."
      );
      return;
    }

    const confirmed = window.confirm(
      `Are you sure you want to delete Split Expense #${expenseId}?\n\nGroup: ${record.groupName}\nAmount: ₹${Number(
        record.totalAmount || 0
      ).toFixed(2)}`
    );

    if (!confirmed) {
      return;
    }

    try {
      setDeleting(expenseId);

      const response = await fetch(
        `http://localhost:8083/api/split/record/${expenseId}`,
        {
          method: "DELETE",
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );

      const message = await response.text();

      if (!response.ok) {
        alert(
          "❌ Unable to delete record.\n" +
            message
        );
        return;
      }

      // -----------------------------------------------------
      // REMOVE FROM UI
      // -----------------------------------------------------

      setRecords((previousRecords) =>
        previousRecords.filter(
          (record) =>
            record.expenseId !== expenseId
        )
      );

      alert(
        "✅ Split record deleted successfully."
      );
    } catch (error) {
      console.error(
        "Delete record error:",
        error
      );

      alert(
        "❌ Unable to delete split record."
      );
    } finally {
      setDeleting(null);
    }
  };

  // =========================================================
  // SEARCH
  // =========================================================

  const searchText =
    search.toLowerCase().trim();

  const filteredRecords = records.filter(
    (record) => {
      if (!searchText) {
        return true;
      }

      // GROUP
      if (
        record.groupName
          ?.toLowerCase()
          .includes(searchText)
      ) {
        return true;
      }

      // PAID BY EMAIL
      if (
        record.paidBy
          ?.toLowerCase()
          .includes(searchText)
      ) {
        return true;
      }

      // PAID BY NAME
      if (
        record.paidByName
          ?.toLowerCase()
          .includes(searchText)
      ) {
        return true;
      }

      // DESCRIPTION
      if (
        record.description
          ?.toLowerCase()
          .includes(searchText)
      ) {
        return true;
      }

      // MEMBER NAME / EMAIL
      if (
        record.members?.some(
          (member) =>
            member.email
              ?.toLowerCase()
              .includes(searchText) ||
            member.name
              ?.toLowerCase()
              .includes(searchText)
        )
      ) {
        return true;
      }

      return false;
    }
  );

  // =========================================================
  // LOADING
  // =========================================================

  if (loading) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-gradient-to-br from-blue-100 via-white to-purple-100">
        <div className="text-center">

          <div className="text-5xl mb-4">
            ⏳
          </div>

          <p className="text-xl font-semibold text-purple-700">
            Loading Split Records...
          </p>

        </div>
      </div>
    );
  }

  // =========================================================
  // ERROR
  // =========================================================

  if (error) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-gradient-to-br from-blue-100 via-white to-purple-100 p-6">

        <div className="bg-white shadow-xl rounded-2xl p-8 text-center max-w-md">

          <div className="text-5xl mb-4">
            ⚠️
          </div>

          <h2 className="text-xl font-bold text-red-600">
            {error}
          </h2>

          <p className="text-gray-500 mt-2">
            Please try again after adding a split
            expense.
          </p>

        </div>

      </div>
    );
  }

  // =========================================================
  // MAIN UI
  // =========================================================

  return (
    <div className="min-h-screen bg-gradient-to-br from-blue-100 via-white to-purple-100 p-6">

      <div className="max-w-5xl mx-auto">

        {/* ===================================================
            HEADER
        =================================================== */}

        <div className="text-center mb-8">

          <div className="text-5xl mb-3">
            📋
          </div>

          <h1 className="text-3xl font-bold text-purple-700">
            Split Records
          </h1>

          <p className="text-gray-500 mt-2">
            View and manage all your split expenses
          </p>

        </div>

        {/* ===================================================
            SEARCH
        =================================================== */}

        <div className="bg-white rounded-2xl shadow-lg p-4 mb-8">

          <div className="relative">

            <span className="absolute left-4 top-3 text-xl">
              🔍
            </span>

            <input
              type="text"
              value={search}
              onChange={(e) =>
                setSearch(e.target.value)
              }
              placeholder="Search group, name or email..."
              className="w-full border border-gray-300 rounded-xl py-3 pl-12 pr-4 focus:outline-none focus:ring-2 focus:ring-purple-400"
            />

          </div>

          <div className="flex justify-between items-center mt-3">

            <p className="text-sm text-gray-500">

              {search
                ? `Showing ${filteredRecords.length} matching record${
                    filteredRecords.length !== 1
                      ? "s"
                      : ""
                  }`
                : `${records.length} total split record${
                    records.length !== 1
                      ? "s"
                      : ""
                  }`}

            </p>

            {search && (
              <button
                onClick={() => setSearch("")}
                className="text-sm text-purple-600 font-semibold hover:underline"
              >
                Clear Search
              </button>
            )}

          </div>

        </div>

        {/* ===================================================
            NO RECORD
        =================================================== */}

        {filteredRecords.length === 0 ? (

          <div className="bg-white rounded-3xl shadow-xl p-10 text-center">

            <div className="text-5xl mb-4">
              {search ? "🔎" : "📭"}
            </div>

            <h2 className="text-xl font-semibold text-gray-700">
              {search
                ? "No matching records"
                : "No split records yet"}
            </h2>

            <p className="text-gray-500 mt-2">
              {search
                ? "Try searching with another group name, person name or email."
                : "Create your first split expense to see it here."}
            </p>

          </div>

        ) : (

          <div className="space-y-6">

            {filteredRecords.map(
              (record, index) => {

                const allPaid =
                  isRecordFullyPaid(record);

                const isDeleting =
                  deleting === record.expenseId;

                return (
                  <motion.div
                    key={`${record.expenseId}-${record.groupName}`}
                    initial={{
                      opacity: 0,
                      y: 30,
                    }}
                    animate={{
                      opacity: 1,
                      y: 0,
                    }}
                    transition={{
                      duration: 0.35,
                      delay: index * 0.05,
                    }}
                    className="bg-white rounded-3xl shadow-xl p-6"
                  >

                    {/* =====================================
                        GROUP + TOTAL
                    ===================================== */}

                    <div className="flex flex-col md:flex-row md:items-center md:justify-between gap-4 border-b pb-5">

                      <div>

                        <p className="text-sm text-purple-600 font-semibold">
                          GROUP
                        </p>

                        <h2 className="text-2xl font-bold text-gray-800">
                          {record.groupName ||
                            "Unknown Group"}
                        </h2>

                        <p className="text-xs text-gray-400 mt-1">
                          Split Expense #{record.expenseId}
                        </p>

                      </div>

                      <div className="text-left md:text-right">

                        <p className="text-sm text-gray-500">
                          Total Amount
                        </p>

                        <p className="text-2xl font-bold text-purple-700">
                          ₹{" "}
                          {Number(
                            record.totalAmount || 0
                          ).toFixed(2)}
                        </p>

                      </div>

                    </div>

                    {/* =====================================
                        PAID BY
                    ===================================== */}

                    <div className="bg-purple-50 border border-purple-100 rounded-2xl p-4 mt-5">

                      <p className="text-sm text-purple-600 font-semibold">
                        💳 Paid By
                      </p>

                      <p className="text-gray-800 font-bold mt-1">
                        {record.paidByName ||
                          record.paidBy
                            ?.split("@")[0] ||
                          "Unknown"}
                      </p>

                      <p className="text-sm text-gray-500 mt-1 break-all">
                        {record.paidBy}
                      </p>

                    </div>

                    {/* =====================================
                        SPLIT DETAILS
                    ===================================== */}

                    <div className="mt-6">

                      <div className="flex items-center justify-between mb-4">

                        <h3 className="text-lg font-bold text-gray-800">
                          👥 Split Details
                        </h3>

                        <span className="text-sm text-gray-500">
                          {record.members?.length || 0} member
                          {(record.members?.length || 0) !== 1
                            ? "s"
                            : ""}
                        </span>

                      </div>

                      <div className="space-y-3">

                        {record.members?.map(
                          (member, memberIndex) => {

                            const isPaid =
                              member.status?.toUpperCase() ===
                              "PAID";

                            const updateKey =
                              `${record.expenseId}-${member.email}`;

                            const isUpdating =
                              updating === updateKey;

                            return (
                              <div
                                key={`${record.expenseId}-${member.email}-${memberIndex}`}
                                className={`border rounded-2xl p-4 transition ${
                                  isPaid
                                    ? "border-green-200 bg-green-50"
                                    : "border-gray-200 bg-white"
                                }`}
                              >

                                <div className="flex flex-col md:flex-row md:items-center md:justify-between gap-4">

                                  {/* MEMBER */}

                                  <div className="min-w-0">

                                    <p className="font-bold text-gray-800">
                                      👤{" "}
                                      {member.name ||
                                        member.email?.split("@")[0] ||
                                        "Unknown User"}
                                    </p>

                                    <p className="text-sm text-gray-500 mt-1 break-all">
                                      {member.email}
                                    </p>

                                    <p className="text-sm text-gray-500 mt-1">
                                      Share amount
                                    </p>

                                  </div>

                                  {/* AMOUNT + CHECKBOX */}

                                  <div className="flex items-center justify-between md:justify-end gap-5">

                                    <p className="font-bold text-purple-700 whitespace-nowrap">
                                      ₹{" "}
                                      {Number(
                                        member.amount || 0
                                      ).toFixed(2)}
                                    </p>

                                    <label
                                      className={`flex items-center gap-2 cursor-pointer ${
                                        isUpdating
                                          ? "opacity-50 cursor-wait"
                                          : ""
                                      }`}
                                    >

                                      <input
                                        type="checkbox"
                                        checked={isPaid}
                                        disabled={
                                          isUpdating
                                        }
                                        onChange={() =>
                                          updateStatus(
                                            record.expenseId,
                                            member.email,
                                            member.status
                                          )
                                        }
                                        className="w-5 h-5 accent-green-600 cursor-pointer"
                                      />

                                      {isUpdating ? (

                                        <span className="text-sm font-semibold text-gray-500">
                                          Updating...
                                        </span>

                                      ) : isPaid ? (

                                        <span className="px-3 py-1 rounded-full bg-green-100 text-green-700 text-sm font-semibold whitespace-nowrap">
                                          ✅ Paid
                                        </span>

                                      ) : (

                                        <span className="px-3 py-1 rounded-full bg-yellow-100 text-yellow-700 text-sm font-semibold whitespace-nowrap">
                                          ⏳ Pending
                                        </span>

                                      )}

                                    </label>

                                  </div>

                                </div>

                              </div>
                            );
                          }
                        )}

                      </div>

                    </div>

                    {/* =====================================
                        PAYMENT COMPLETION
                    ===================================== */}

                    <div className="mt-6">

                      {allPaid ? (

                        <div className="bg-green-50 border border-green-200 rounded-2xl p-4">

                          <div className="flex flex-col md:flex-row md:items-center md:justify-between gap-4">

                            <div>

                              <p className="font-bold text-green-700">
                                🎉 All Payments Completed
                              </p>

                              <p className="text-sm text-green-600 mt-1">
                                All members have marked their
                                shares as paid.
                              </p>

                            </div>

                            <button
                              type="button"
                              onClick={() =>
                                deleteRecord(record)
                              }
                              disabled={isDeleting}
                              className="bg-red-600 hover:bg-red-700 text-white px-5 py-2.5 rounded-xl font-semibold shadow-md disabled:opacity-60"
                            >

                              {isDeleting
                                ? "Deleting..."
                                : "🗑️ Delete Record"}

                            </button>

                          </div>

                        </div>

                      ) : (

                        <div className="bg-yellow-50 border border-yellow-200 rounded-2xl p-4">

                          <p className="font-semibold text-yellow-700">
                            ⏳ Payment Pending
                          </p>

                          <p className="text-sm text-yellow-600 mt-1">
                            Complete all pending payments
                            before deleting this record.
                          </p>

                        </div>

                      )}

                    </div>

                  </motion.div>
                );
              }
            )}

          </div>
        )}

      </div>
    </div>
  );
}

export default SplitRecord;

