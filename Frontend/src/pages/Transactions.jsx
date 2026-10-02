import { useEffect, useState } from "react";
import { useAccount } from "../context/AccountContext";
import { useAuth } from "../context/AuthContext";
import { getAllTransactions } from "../services/transactionServices";
import AccountSwitcher from "../components/dashboardRelated/AccountSwitcher";
import { toast } from "react-toastify";
import jsPDF from "jspdf";
/*

 Transactions Component

• Displays all transactions for the selected bank account
• Supports filtering (type, status, date, amount, search)
• Supports pagination
• Shows detailed transaction modal
• Allows downloading transaction receipt as PDF
*/
function Transactions() {
    const { activeAccount, setActiveAccount } = useAccount();
    const { auth } = useAuth();
    const accounts = auth?.accounts ?? [];


    const [loading, setLoading] = useState(false);
    const [selectedTxn, setSelectedTxn] = useState(null);


    //Filter transactions
    const [filters, setFilters] = useState({
        type: "ALL",
        status: "ALL",
        fromDate: "",
        toDate: "",
        minAmount: "",
        maxAmount: "",
        month: "",
        search: ""
    });


    //Pagination
    // Pagination state (MUST be first)



    // Pagination calculations
    const PAGE_SIZE = 10;

    const [transactions, setTransactions] = useState([]);
    const [currentPage, setCurrentPage] = useState(0); // 0-based
    const [totalPages, setTotalPages] = useState(0);


    //downlaod the reciept


    const downloadReceipt = (txn) => {
        const doc = new jsPDF();

        doc.setFontSize(16);
        doc.text("Transaction Receipt", 20, 20);

        doc.setFontSize(11);
        let y = 40;

        const addRow = (label, value) => {
            doc.text(`${label}:`, 20, y);
            doc.text(String(value || "-"), 80, y);
            y += 8;
        };

        addRow("Status", txn.status);
        addRow("Amount", `Rs. ${txn.amount}`);
        addRow("Type", txn.debit ? "Debit" : "Credit");
        addRow(
            txn.debit ? "Paid To" : "Received From",
            txn.receiverName || txn.upiId
        );
        addRow("UPI ID", txn.upiId);
        addRow("Transaction ID", txn.transactionRef);
        addRow(
            "Date & Time",
            new Date(txn.completedAt || txn.initiatedAt).toLocaleString()
        );

        if (txn.description) {
            y += 4;
            addRow("Note", txn.description);
        }

        doc.save(`Transaction_${txn.transactionRef}.pdf`);
    };

    const [totalDebit, setTotalDebit] = useState(0);
    const [totalCredit, setTotalCredit] = useState(0);
    const [debitCount, setDebitCount] = useState(0);
    const [creditCount, setCreditCount] = useState(0);
    useEffect(() => {
        let debit = 0;
        let credit = 0;
        let dCount = 0;
        let cCount = 0;

        transactions.forEach(txn => {
            if (txn.debit) {
                debit += Number(txn.amount);
                dCount++;
            } else if (txn.credit) {
                credit += Number(txn.amount);
                cCount++;
            }
        });

        setTotalDebit(debit);
        setTotalCredit(credit);
        setDebitCount(dCount);
        setCreditCount(cCount);
    }, [transactions]);


    //Download the monthly statement
   const downloadMonthlyStatement = () => {
  if (!transactions.length) {
    toast.error("No transactions found");
    return;
  }

  const doc = new jsPDF({
    orientation: "p",
    unit: "mm",
    format: "a4",
    compress: true
  });

  // ---------------- COLUMN DEFINITIONS ----------------
  const COL = {
    date:   { x: 20,  w: 25 },
    ref:    { x: 47,  w: 40 },
    type:   { x: 90,  w: 18 },
    party:  { x: 110, w: 45 },
    amount: { x: 175, w: 15 }
  };

  // ---------------- HELPER ----------------
  const drawCell = (text, col, y, options = {}) => {
    const lines = doc.splitTextToSize(text, col.w);
    doc.text(lines, col.x, y, options);
    return lines.length * 5;
  };

  let y = 20;

  // ---------------- HEADER ----------------
  doc.setFont("Helvetica", "normal");
  doc.setFontSize(16);
  doc.text("Monthly Transaction Statement", 105, y, { align: "center" });
  y += 10;

  doc.setFontSize(11);
  doc.text(`Account : ${activeAccount?.bankName}`, 20, y);
  doc.text(
    `Period : ${filters.fromDate} -> ${filters.toDate}`,
    190,
    y,
    { align: "right" }
  );

  y += 8;
  doc.line(20, y, 190, y);
  y += 6;

  // ---------------- SUMMARY ----------------
  doc.text(`Money In  : Rs. ${totalCredit}`, 20, y);
  doc.text(`Credits  : ${creditCount}`, 110, y);
  y += 6;

  doc.text(`Money Out : Rs. ${totalDebit}`, 20, y);
  doc.text(`Debits   : ${debitCount}`, 110, y);
  y += 10;

  // ---------------- TABLE HEADER ----------------
  const drawTableHeader = () => {
    doc.setFont(undefined, "bold");
    doc.setFontSize(11);

    doc.text("Date", COL.date.x, y);
    doc.text("Txn Ref", COL.ref.x, y);
    doc.text("Type", COL.type.x, y);
    doc.text("From / To", COL.party.x, y);
    doc.text("Amount", COL.amount.x, y, { align: "right" });

    y += 4;
    doc.line(20, y, 190, y);
    y += 6;

    doc.setFont(undefined, "normal");
  };

  drawTableHeader();

  // ---------------- TRANSACTIONS ----------------
  transactions.forEach(txn => {
    if (y > 270) {
      doc.addPage();
      y = 20;
      drawTableHeader();
    }

    const date = new Date(txn.completedAt || txn.initiatedAt).toLocaleString();

    const fromName = txn.debit ? auth?.userName : (txn.receiverName || txn.upiId);
    const toName   = txn.debit ? (txn.receiverName || txn.upiId) : "You";

    const rowHeight = Math.max(
      drawCell(date, COL.date, y),
      drawCell(txn.transactionRef, COL.ref, y),
      drawCell(txn.debit ? "DEBIT" : "CREDIT", COL.type, y),
      drawCell(`From: ${fromName}\nTo: ${toName}`, COL.party, y),
      drawCell(
        `${txn.debit ? "-" : "+"}Rs. ${txn.amount}`,
        COL.amount,
        y,
        { align: "right" }
      )
    );

    y += rowHeight;

    // Status line
    doc.setFontSize(9);
    doc.setTextColor(120);
    doc.text(`Status: ${txn.status}`, COL.party.x, y);
    doc.setTextColor(0);
    doc.setFontSize(11);

    y += 6;
  });

  // ---------------- FOOTER ----------------
  doc.line(20, 285, 190, 285);
  doc.setFontSize(8);
  doc.text(
    "This is a system generated statement. No signature required.",
    105,
    292,
    { align: "center" }
  );

  doc.save(`Statement_${filters.month}.pdf`);
};




    // Api to get the transactions
    useEffect(() => {
        if (!activeAccount) return;

        let cancelled = false;


        const fetchTransactions = async () => {
            setLoading(true);

            const payload = {
                userId: auth.userId,
                userLinkedAccountId: activeAccount.userLinkedAccountId,

                page: currentPage,
                size: PAGE_SIZE,

                direction: filters.type !== "ALL" ? filters.type : null,

                status: filters.status !== "ALL" ? filters.status : null,
                disablePagination: !!filters.month,
                search: filters.search || null,
                minAmount: filters.minAmount || null,
                maxAmount: filters.maxAmount || null,
                fromDate: filters.fromDate
                    ? `${filters.fromDate}T00:00:00`
                    : null,
                toDate: filters.toDate
                    ? `${filters.toDate}T23:59:59`
                    : null
            };


            const res = await getAllTransactions(payload);


            if (!cancelled) {
                setTransactions(res?.data?.content ?? []);
                setTotalPages(res?.data?.totalPages ?? 0);
                setLoading(false);
            }
        };

        fetchTransactions();
        return () => (cancelled = true);
    }, [activeAccount, auth?.userId, currentPage, filters]);


    useEffect(() => {
        setCurrentPage(0);
    }, [activeAccount, filters]);



    const DetailRow = ({ label, value }) => (
        <div className="flex justify-between gap-4">
            <span className="text-gray-400">{label}</span>
            <span className="text-white text-right break-all">
                {value || "-"}
            </span>
        </div>
    );

    //prevent from > to data
    useEffect(() => {
        if (!filters.fromDate || !filters.toDate) return;

        if (filters.toDate < filters.fromDate) {
            setFilters(f => ({ ...f, toDate: f.fromDate }));
        }
    }, [filters.fromDate, filters.toDate]);

    //show reset button when there is any active filter
    const hasActiveFilters =
        filters.type !== "ALL" ||
        filters.fromDate ||
        filters.toDate ||
        filters.minAmount ||
        filters.maxAmount ||
        filters.month ||
        filters.search;

    //Autoset from and to date when month chooses
    useEffect(() => {
        if (!filters.month) return;

        const [year, month] = filters.month.split("-");

        const fromDate = `${year}-${month}-01`;
        const lastDay = new Date(year, month, 0).getDate();
        const toDate = `${year}-${month}-${lastDay}`;

        setFilters(f => ({
            ...f,
            fromDate,
            toDate
        }));
    }, [filters.month]);


    return (
        <div className="relative min-h-screen w-full overflow-hidden
      bg-gradient-to-br from-[#0b1220] via-[#0f172a] to-[#020617]">

            {/* ACCOUNT SWITCHER */}
            <div className="relative z-10 px-6 py-6">
                <div className="max-w-4xl mx-auto space-y-4">

                    {/* HEADER ROW */}
                    <div className="flex items-start justify-between gap-4">
                        {/* LEFT: TITLE */}
                        <div>
                            <h2 className="text-lg font-semibold text-white">
                                Transactions
                            </h2>
                            <p className="text-xs text-gray-400 mt-1">
                                Recent activity for selected account
                            </p>
                            <div className="mt-4">
                                <AccountSwitcher
                                    accounts={accounts}
                                    activeAccount={activeAccount}
                                    setActiveAccount={setActiveAccount}
                                />
                            </div>
                        </div>
                    </div>

                    <div className="bg-white/5 border border-white/10 rounded-2xl p-4
                     grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
                        {/* name/upi */}
                        <div className="flex flex-col gap-1">
                            <label className="text-[11px] text-gray-400">
                                Search name / UPI
                            </label>

                            <input
                                type="text"
                                placeholder="Mahesh, tarun@upi…"
                                value={filters.search}
                                onChange={e =>
                                    setFilters(f => ({ ...f, search: e.target.value }))
                                }
                                className="
      bg-white/10 backdrop-blur
      border border-white/10
      rounded-xl px-4 py-2
      text-white text-sm
      placeholder-gray-500
      focus:outline-none focus:ring-1 focus:ring-purple-400/60
    "
                            />
                        </div>
                        {/* category */}
                        <div className="flex flex-col gap-1">
                            <label className="text-[11px] text-gray-400">
                                Category
                            </label>

                            <div className="relative">
                                <select
                                    value={filters.type}
                                    onChange={e =>
                                        setFilters(f => ({ ...f, type: e.target.value }))
                                    }
                                    className="
                                        w-full appearance-none
                                        bg-white/10 backdrop-blur
                                        border border-white/10
                                        rounded-xl px-4 py-2 pr-10
                                        text-white text-sm
                                        focus:outline-none focus:ring-2 focus:ring-purple-500/40
                                    "
                                >
                                    <option value="ALL" className="bg-[#0f172a] text-white">All</option>
                                    <option value="DEBIT" className="bg-[#0f172a] text-white">Debit</option>
                                    <option value="CREDIT" className="bg-[#0f172a] text-white">Credit</option>

                                </select>

                                {/* CUSTOM ARROW */}
                                <div className="pointer-events-none absolute inset-y-0 right-3 flex items-center text-gray-400">
                                    ▾
                                </div>
                            </div>
                        </div>
                        {/* STATUS */}
                        <div className="flex flex-col gap-1">
                            <label className="text-[11px] text-gray-400">
                                Status
                            </label>

                            <div className="relative">
                                <select
                                    value={filters.status}
                                    onChange={e =>
                                        setFilters(f => ({ ...f, status: e.target.value }))
                                    }
                                    className="
                w-full appearance-none
                bg-white/10 backdrop-blur
                border border-white/10
                rounded-xl px-4 py-2 pr-10
                text-white text-sm
                focus:outline-none focus:ring-2 focus:ring-purple-500/40
            "
                                >
                                    <option value="ALL" className="bg-[#0f172a]">All</option>
                                    <option value="SUCCESS" className="bg-[#0f172a]">Success</option>
                                    <option value="FAILED" className="bg-[#0f172a]">Failed</option>
                                </select>

                                <div className="pointer-events-none absolute inset-y-0 right-3 flex items-center text-gray-400">
                                    ▾
                                </div>
                            </div>
                        </div>

                        {/* MONTH */}
                        <div className="flex flex-col gap-1">
                            <label className="text-[11px] text-gray-400">
                                Month
                            </label>

                            <input
                                type="month"
                                value={filters.month}
                                onChange={e =>
                                    setFilters(f => ({ ...f, month: e.target.value }))
                                }
                                className="
      bg-white/10 backdrop-blur
      border border-white/10
      rounded-xl px-4 py-2
      text-white text-sm
      focus:outline-none focus:ring-2 focus:ring-purple-500/40
    "
                            />
                        </div>

                        {/* FROM DATE */}
                        <div className="flex flex-col gap-1">
                            <label className="text-[11px] text-gray-400">
                                From date
                            </label>

                            <input
                                type="date"
                                value={filters.fromDate}
                                onChange={e =>
                                    setFilters(f => ({
                                        ...f,
                                        fromDate: e.target.value,
                                        month: ""
                                    }))
                                }

                                className="
      w-full
      appearance-none
      bg-white/10 backdrop-blur
      border border-white/10
      rounded-xl px-4 py-2
      text-white text-sm
      focus:outline-none focus:ring-2 focus:ring-purple-500/40
    "
                            />
                        </div>

                        {/* TO DATE */}
                        <div className="flex flex-col gap-1">
                            <label className="text-[11px] text-gray-400">
                                To date
                            </label>

                            <input
                                type="date"
                                value={filters.toDate}
                                min={filters.fromDate || undefined}
                                max={new Date().toISOString().split("T")[0]}
                                onChange={e =>
                                    setFilters(f => ({
                                        ...f,
                                        toDate: e.target.value,
                                        month: ""
                                    }))
                                }

                                className="
                                w-full
                                appearance-none
                                bg-white/10 backdrop-blur
                                border border-white/10
                                rounded-xl px-4 py-2
                                text-white text-sm
                                focus:outline-none focus:ring-2 focus:ring-purple-500/40
                                "
                            />
                        </div>

                        {/* MIN AMOUNT */}
                        <div className="flex flex-col gap-1">
                            <label className="text-[11px] text-gray-400">
                                Min amount
                            </label>

                            <input
                                type="number"
                                value={filters.minAmount}
                                min="0"
                                max={filters.maxAmount || undefined}
                                placeholder="₹"
                                onChange={e =>
                                    setFilters(f => ({ ...f, minAmount: e.target.value }))
                                }
                                className="
                                w-full
                                appearance-none
                                bg-white/10 backdrop-blur
                                border border-white/10
                                rounded-xl px-4 py-2
                                text-white text-sm
                                focus:outline-none focus:ring-2 focus:ring-purple-500/40
                                "
                            />
                        </div>
                        {/* MAX AMOUNT */}
                        <div className="flex flex-col gap-1">
                            <label className="text-[11px] text-gray-400">
                                Max amount
                            </label>
                            <input
                                type="number"
                                placeholder="₹"

                                min={filters.minAmount || 0}
                                value={filters.maxAmount}
                                onChange={e =>
                                    setFilters(f => ({ ...f, maxAmount: e.target.value }))
                                }
                                className="
      bg-white/10 backdrop-blur
      border border-white/10
      rounded-xl px-4 py-2
      text-white text-sm
      placeholder-gray-500
      focus:outline-none focus:ring-1 focus:ring-purple-400/60
    "
                            />
                        </div>
                    </div>

                    {/* reset button*/}
                    {hasActiveFilters && (
                        <button
                            onClick={() => {
                                setFilters({
                                    type: "ALL",
                                    status: "ALL",
                                    fromDate: "",
                                    toDate: "",
                                    minAmount: "",
                                    maxAmount: "",
                                    month: "",
                                    search: ""
                                });
                            }}
                            className="
                            inline-flex items-center gap-1.5
                            px-3 py-1.5
                            rounded-xl
                            bg-white/10 backdrop-blur
                            border border-white/10
                            text-xs font-medium text-purple-300
                            hover:bg-white/20 hover:text-purple-200
                            focus:outline-none focus:ring-1 focus:ring-purple-400/60
                            transition
                        "
                        >
                            ⟲ Reset filters
                        </button>

                    )}

                    {/* Calling function to download the monthly statement. */}
                    {filters.month && (
                        <button
                            onClick={downloadMonthlyStatement}
                            className="
      px-4 py-2 rounded-xl
      bg-purple-500 text-black
      font-semibold
      hover:bg-purple-400 transition
    "
                        >
                            ⬇ Download Monthly Statement
                        </button>
                    )}

                    {filters.month && (
                        <div className="grid grid-cols-2 sm:grid-cols-4 gap-4">
                            <div className="bg-white/10 p-4 rounded-xl">
                                <p className="text-xs text-gray-400">Money In</p>
                                <p className="text-lg text-green-400">₹{totalCredit}</p>
                            </div>

                            <div className="bg-white/10 p-4 rounded-xl">
                                <p className="text-xs text-gray-400">Money Out</p>
                                <p className="text-lg text-red-400">₹{totalDebit}</p>
                            </div>

                            <div className="bg-white/10 p-4 rounded-xl">
                                <p className="text-xs text-gray-400">Credits</p>
                                <p className="text-lg text-white">{creditCount}</p>
                            </div>

                            <div className="bg-white/10 p-4 rounded-xl">
                                <p className="text-xs text-gray-400">Debits</p>
                                <p className="text-lg text-white">{debitCount}</p>
                            </div>
                        </div>
                    )}

                    {/* LIST */}
                    <div className="relative space-y-2 min-h-[200px]">

                        {/* LOADING OVERLAY */}
                        {loading && (
                            <div className="absolute inset-0 z-10 flex items-center justify-center
                bg-black/40 backdrop-blur-sm rounded-2xl">
                                <span className="text-gray-300 text-sm">
                                    Updating transactions…
                                </span>
                            </div>
                        )}

                        {/* EMPTY */}
                        {!loading && transactions.length === 0 && (
                            <div className="flex items-center justify-center py-12 text-gray-500">
                                No transactions yet
                            </div>
                        )}
                        {/* TRANSACTIONS */}
                        {transactions.map(txn => {
                            const isDebit = txn.debit;
                            const date = new Date(txn.completedAt || txn.initiatedAt);

                            return (
                                <div
                                    key={txn.transactionRef}
                                    onClick={() => setSelectedTxn(txn)}
                                    className="
                    flex items-center justify-between
                    px-6 py-5 rounded-2xl
                    bg-white/10 backdrop-blur
                    border border-white/10
                    hover:bg-white/15
                    transition cursor-pointer
                  "
                                >
                                    {/* LEFT */}
                                    <div className="flex items-start gap-4">
                                        <div
                                            className={`mt-1 w-11 h-11 rounded-full flex items-center justify-center
                        ${isDebit
                                                    ? "bg-red-500/20 text-red-400"
                                                    : "bg-green-500/20 text-green-400"
                                                }`}
                                        >
                                            <span className="text-lg font-semibold">
                                                {isDebit ? "↑" : "↓"}
                                            </span>
                                        </div>

                                        <div className="space-y-1">
                                            <p className="text-[11px] uppercase tracking-wide text-gray-400">
                                                {isDebit ? "Paid to" : "Received from"}
                                            </p>

                                            <p className="text-sm font-semibold text-white leading-tight">
                                                {txn.receiverName || txn.upiId}
                                            </p>

                                            <p className="text-[11px] text-gray-500">
                                                {date.toLocaleDateString()} • {date.toLocaleTimeString()}
                                            </p>
                                        </div>
                                    </div>

                                    {/* RIGHT */}
                                    <div className="text-right space-y-1">
                                        <p
                                            className={`text-base font-semibold ${isDebit ? "text-red-400" : "text-green-400"
                                                }`}
                                        >
                                            {isDebit ? "-" : "+"}₹{txn.amount}
                                        </p>

                                        <p className="text-[11px] text-gray-400">
                                            {txn.status}
                                        </p>
                                    </div>
                                </div>
                            );
                        })}
                    </div>
                    {!filters.month && totalPages > 1 && (
                        <div className="flex items-center justify-center gap-2 mt-6">
                            {/* PREV */}
                            <button
                                disabled={currentPage === 0}
                                onClick={() => setCurrentPage(p => p - 1)}
                                className="
        px-3 py-1 rounded-lg text-sm
        bg-white/10 border border-white/10
        text-white
        disabled:opacity-40 disabled:cursor-not-allowed
        hover:bg-white/20 transition
      "
                            >
                                Prev
                            </button>

                            {/* PAGE NUMBERS */}
                            {Array.from({ length: totalPages }).map((_, idx) => (
                                <button
                                    key={idx}
                                    onClick={() => setCurrentPage(idx)}
                                    className={`
          px-3 py-1 rounded-lg text-sm
          border border-white/10
          ${idx === currentPage
                                            ? "bg-purple-500 text-black"
                                            : "bg-white/10 text-white hover:bg-white/20"
                                        }
          transition
        `}
                                >
                                    {idx + 1}
                                </button>
                            ))}

                            {/* NEXT */}
                            <button
                                disabled={currentPage === totalPages - 1}
                                onClick={() => setCurrentPage(p => p + 1)}
                                className="
        px-3 py-1 rounded-lg text-sm
        bg-white/10 border border-white/10
        text-white
        disabled:opacity-40 disabled:cursor-not-allowed
        hover:bg-white/20 transition
      "
                            >
                                Next
                            </button>
                        </div>
                    )}


                </div>
            </div>
            {/* DETAILS MODAL */}
            {selectedTxn && (
                <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/60">
                    <div className="w-[90%] max-w-md bg-[#0f172a]
            rounded-2xl border border-white/10 backdrop-blur-xl p-6">

                        <div className="flex justify-between items-center mb-4">
                            <h3 className="text-lg font-semibold text-white">
                                Transaction Details
                            </h3>
                            <button
                                onClick={() => setSelectedTxn(null)}
                                className="text-gray-400 hover:text-white"
                            >
                                ✕
                            </button>
                        </div>

                        <div className="text-center mb-6">
                            <p className={`text-2xl font-bold ${selectedTxn.debit ? "text-red-400" : "text-green-400"
                                }`}>
                                {selectedTxn.debit ? "-" : "+"}₹{selectedTxn.amount}
                            </p>
                            <p className="text-sm text-gray-400">
                                {selectedTxn.status}
                            </p>
                        </div>

                        <div className="space-y-4 text-sm">
                            <DetailRow
                                label={selectedTxn.debit ? "Paid To" : "Received From"}
                                value={selectedTxn.receiverName || selectedTxn.upiId}
                            />
                            <DetailRow label="UPI ID" value={selectedTxn.upiId} />
                            <DetailRow label="Transaction ID" value={selectedTxn.transactionRef} />
                            <DetailRow label="Type" value={selectedTxn.type} />
                            <DetailRow
                                label="Date & Time"
                                value={new Date(
                                    selectedTxn.completedAt || selectedTxn.initiatedAt
                                ).toLocaleString()}
                            />
                            {selectedTxn.description && (
                                <DetailRow label="Note" value={selectedTxn.description} />
                            )}
                        </div>

                        <button
                            onClick={() => downloadReceipt(selectedTxn)}
                            className="w-full mb-2 py-2 rounded-xl
    bg-white/10 border border-white/10
    text-purple-300 hover:bg-white/20 transition mt-3"
                        >
                            ⬇ Download receipt
                        </button>

                        <button
                            onClick={() => setSelectedTxn(null)}
                            className="w-full py-2 rounded-xl
    bg-purple-500 text-black font-semibold
    hover:bg-purple-400 transition"
                        >
                            Done
                        </button>

                    </div>
                </div>
            )}


        </div >
    );
}

export default Transactions;
