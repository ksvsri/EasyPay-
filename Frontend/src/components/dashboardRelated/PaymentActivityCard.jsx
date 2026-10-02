import { useEffect, useState } from "react";
import { useAuth } from "../../context/AuthContext";
import { getPaymentActivity } from "../../services/dashboardServices";

//  PaymentActivityCard Component

// - Displays monthly transaction summary for a user
// - Shows:
//   • Amount received
//   • Amount spent
//   • Net balance
//   • Credits / Debits count
//   • Visual speedometer for money usage
// - Data updates when:
//   • Account changes
//   • Month/year changes
//   • New transaction happens (refreshKey)

export default function PaymentActivityCard({ activeAccount, refreshKey }) {
    const { auth } = useAuth();

    const now = new Date();
    const [year, setYear] = useState(now.getFullYear());
    const [month, setMonth] = useState(now.getMonth() + 1);

    const [data, setData] = useState({
        received: 0,
        spent: 0,
        credits: 0,
        debits: 0,
    });

    const [loading, setLoading] = useState(false);

    /* -------- Year range based on user registration -------- */
    const registeredYear = auth?.createdAt
        ? new Date(auth.createdAt).getFullYear()
        : now.getFullYear();

    const years = [];
    for (let y = now.getFullYear(); y >= registeredYear; y--) {
        years.push(y);
    }

    /* -------- Fetch payment activity -------- */
    useEffect(() => {
        if (
            !auth?.userId ||
            !activeAccount ||
            !activeAccount.userLinkedAccountId
        ) {
            return;
        }

        //Method to get the payment activity
        const fetchActivity = async () => {
            setLoading(true);

            const dto = {
                userId: auth.userId,
                year,
                month,
                userLinkedAccountId: activeAccount.userLinkedAccountId,
            };

            console.log(dto);

            // API to get the Data
            try {
                const res = await getPaymentActivity(dto);

                //Success response
                if (res?.data) {
                    setData({
                        received: res.data.amountReceived || 0,
                        spent: res.data.amountSpent || 0,
                        credits: res.data.noOfCredits || 0,
                        debits: res.data.noOfDebits || 0,
                    });
                }
            } finally {
                setLoading(false);
            }
        };

        fetchActivity();
    }, [
        auth?.userId,
        activeAccount?.userLinkedAccountId,
        year,
        month,
        refreshKey,
    ]);

    /* -------- Derived values (UI only) -------- */
    const net = data.received - data.spent;
    const totalTransactions = data.credits + data.debits;

    const totalAmount = data.received + data.spent;

    const spentPercent =
        totalAmount === 0 ? 0 : (data.spent / totalAmount) * 100;

    let receivedPercent =
        totalAmount === 0 ? 0 : (data.received / totalAmount) * 100;

    // UX: ensure visibility if credits exist
    if (data.credits > 0 && receivedPercent > 0 && receivedPercent < 3) {
        receivedPercent = 3;
    }
    // UI safety
    const clampedSpentPercent = 100 - receivedPercent;


    /* -------- UI -------- */
    return (
        <div className="bg-white/10 backdrop-blur-xl border border-white/10 rounded-2xl p-6">

            {/* Header */}
            <div className="flex justify-between items-center">
                <h3 className="text-lg text-white font-semibold">
                    Payment Activity
                </h3>

                <div className="flex gap-2">
                    <select
                        value={month}
                        onChange={(e) => setMonth(Number(e.target.value))}
                        className="
                    appearance-none
                    bg-white/10 backdrop-blur
                    text-white text-xs font-medium
                    px-3 py-1.5 pr-8
                    rounded-xl
                    border border-white/10
                    hover:bg-white/20
                    focus:outline-none focus:ring-1 focus:ring-purple-400/60
                    cursor-pointer
                "
                    >
                        {Array.from({ length: 12 }).map((_, i) => (
                            <option
                                key={i}
                                value={i + 1}
                                className="bg-[#0f172a] text-white"
                            >
                                {new Date(0, i).toLocaleString("default", { month: "short" })}
                            </option>
                        ))}
                    </select>

                    {/* Month and year selection */}
                    <select
                        value={year}
                        onChange={(e) => setYear(Number(e.target.value))}
                        className="
    appearance-none
    bg-white/10 backdrop-blur
    text-white text-xs font-medium
    px-3 py-1.5 pr-8
    rounded-xl
    border border-white/10
    hover:bg-white/20
    focus:outline-none focus:ring-1 focus:ring-purple-400/60
    cursor-pointer
  "
                    >
                        {years.map(y => (
                            <option
                                key={y}
                                value={y}
                                className="bg-[#0f172a] text-white"
                            >
                                {y}
                            </option>
                        ))}
                    </select>

                </div>
            </div>

            {/* ===== TOP STATS ===== */}
            <div className="grid grid-cols-3 gap-3 mt-6">
                <div className="bg-white/10 rounded-lg p-3">
                    <p className="text-[11px] text-gray-400">Received</p>
                    <p className="text-base font-semibold text-green-400">
                        ₹{data.received.toLocaleString()}
                    </p>
                </div>

                <div className="bg-white/10 rounded-lg p-3">
                    <p className="text-[11px] text-gray-400">Spent</p>
                    <p className="text-base font-semibold text-red-400">
                        ₹{data.spent.toLocaleString()}
                    </p>
                </div>

                <div className="bg-white/10 rounded-lg p-3">
                    <p className="text-[11px] text-gray-400">Net</p>
                    <p
                        className={`text-base font-semibold ${net >= 0 ? "text-green-400" : "text-red-400"
                            }`}
                    >
                        ₹{net.toLocaleString()}
                    </p>
                </div>
            </div>

            {/* ===== SPEEDOMETER GAUGE ===== */}
            <div className="mt-6 flex flex-col items-center">

                <p className="text-xs text-gray-400 mb-2">Money Usage</p>

                <div className="relative w-48 h-28">

                    {/* SVG Speedometer */}
                    <svg viewBox="0 0 200 120" className="w-full h-full">

                        {/* Background arc */}
                        <path
                            d="M20 100 A80 80 0 0 1 180 100"
                            fill="none"
                            stroke="rgba(255,255,255,0.15)"
                            strokeWidth="12"
                        />

                        {/* Spent arc */}
                        <path
                            d="M20 100 A80 80 0 0 1 180 100"
                            fill="none"
                            stroke="#f87171"
                            strokeWidth="12"
                            strokeDasharray={`${clampedSpentPercent * 2.51} 251`}
                            strokeLinecap="round"
                        />

                        {/* Remaining (Received) arc */}
                        <path
                            d="M20 100 A80 80 0 0 1 180 100"
                            fill="none"
                            stroke="#4ade80"
                            strokeWidth="12"
                            strokeDasharray={`${receivedPercent * 2.51} 251`}
                            strokeDashoffset={`-${clampedSpentPercent * 2.51}`}
                            strokeLinecap="round"
                        />


                        {/* Tick marks */}
                        {Array.from({ length: 11 }).map((_, i) => {
                            const angle = (-90 + i * 18) * (Math.PI / 180);
                            const x1 = 100 + 70 * Math.cos(angle);
                            const y1 = 100 + 70 * Math.sin(angle);
                            const x2 = 100 + 80 * Math.cos(angle);
                            const y2 = 100 + 80 * Math.sin(angle);

                            return (
                                <line
                                    key={i}
                                    x1={x1}
                                    y1={y1}
                                    x2={x2}
                                    y2={y2}
                                    stroke="rgba(255,255,255,0.4)"
                                    strokeWidth="2"
                                />
                            );
                        })}
                    </svg>

                    {/* Needle */}
                    <div
                        className="absolute left-1/2 bottom-2 w-[2px] h-20 bg-orange-400 origin-bottom transition-transform duration-700"
                        style={{
                            transform: `rotate(${clampedSpentPercent * 1.8 - 90}deg)`

                        }}
                    />

                    {/* Center hub */}
                    <div className="absolute left-1/2 bottom-6 w-3 h-3 bg-orange-400 rounded-full -translate-x-1/2" />

                    {/* Center text */}
                    <div className="absolute bottom-0 w-full text-center">
                        <p className="text-lg font-bold text-white">
                            {Math.round(clampedSpentPercent)}%
                        </p>
                        <p className="text-[11px] text-gray-400">
                            Spent
                        </p>
                    </div>
                </div>

                {/* Labels */}
                <div className="flex justify-between w-full mt-2 text-xs">
                    <span className="text-green-400">
                        Received {Math.round(receivedPercent)}%
                    </span>

                    <span className="text-red-400">
                        Spent {Math.round(clampedSpentPercent)}%
                    </span>
                </div>

            </div>

            {/* ===== TRANSACTION STATS ===== */}
            <div className="grid grid-cols-3 gap-3 mt-6 pt-4 border-t border-white/10">
                {/* Credits */}
                <div className="bg-white/10 rounded-lg p-3 text-center">
                    <p className="text-[11px] text-gray-400">Credits</p>
                    <p className="text-base font-semibold text-green-400">
                        {data.credits}
                    </p>
                </div>
                {/* Debts */}
                <div className="bg-white/10 rounded-lg p-3 text-center">
                    <p className="text-[11px] text-gray-400">Debits</p>
                    <p className="text-base font-semibold text-red-400">
                        {data.debits}
                    </p>
                </div>
                {/* Total Transactions */}
                <div className="bg-white/10 rounded-lg p-3 text-center">
                    <p className="text-[11px] text-gray-400">Total Txns</p>
                    <p className="text-base font-semibold text-purple-400">
                        {totalTransactions}
                    </p>
                </div>

            </div>



        </div>
    );

}
