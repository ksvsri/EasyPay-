/*

 BalanceCard Component

- Displays the user’s available account balance
- Balance is hidden by default (shown as dots)
- Balance is revealed only after PIN verification
- Handles three states:
  • Hidden
  • Loading
  • Visible

*/
export default function BalanceCard({
    // hasAccounts,        // Boolean: whether user has any linked accounts
    availableBalance,  // Balance value returned from backend
    showBalance,       // Controls whether balance is visible
    loadingBalance,    // Loader state while fetching balance
    onCheckBalance,    // Callback to trigger PIN modal / hide balance
}) {
    return (
        <div className="bg-white/10 backdrop-blur-xl border border-white/10 rounded-2xl p-6">

            {/* HEADER */}
            <h3 className="text-lg text-white font-semibold">
                Account Balance
            </h3>
            <p className="text-sm text-gray-400">
                Available Balance
            </p>

            {/*  BALANCE DISPLAY AREA */}
            <div className="mt-4 relative h-10 overflow-hidden">

                {/*  ACTUAL BALANCE (VISIBLE STATE)  */}
                <p
                    className={`
                        absolute inset-0
                        text-3xl font-bold text-purple-400
                        transition-all duration-500 ease-in-out
                        ${showBalance
                            ? "opacity-100 translate-y-0"
                            : "opacity-0 translate-y-2"}
                    `}
                >
                    {availableBalance !== null
                        ? `₹${Number(availableBalance).toLocaleString()}`
                        : ""}
                </p>

                {/* HIDDEN BALANCE (DOTS STATE)  */}
                <p
                    className={`
                        absolute inset-0
                        text-3xl font-bold text-purple-400 tracking-widest
                        transition-all duration-500 ease-in-out
                        ${!showBalance && !loadingBalance
                            ? "opacity-100 translate-y-0"
                            : "opacity-0 -translate-y-2"}
                    `}
                >
                    ••••••
                </p>

                {/*  LOADING STATE  */}
                <div
                    className={`
                        absolute inset-0
                        flex items-center
                        transition-opacity duration-300 ease-in-out
                        ${loadingBalance ? "opacity-100" : "opacity-0"}
                    `}
                >
                    {/* Animated "Loading" text */}
                    <div className="loading-text text-3xl font-bold text-purple-400">
                        {"Loading".split("").map((char, i) => (
                            <span
                                key={i}
                                style={{ animationDelay: `${i * 0.1}s` }}
                            >
                                {char}
                            </span>
                        ))}
                    </div>
                </div>
            </div>

            {/*  ACTION BUTTON  */}
            <button
                onClick={onCheckBalance}
                className="
                    mt-4 px-4 py-2 rounded-lg
                    border border-purple-400 text-purple-400
                    hover:bg-purple-400 hover:text-black
                    transition text-sm font-medium
                "
            >
                {/* Button text changes based on visibility */}
                {showBalance ? "Hide balance" : "Check balance"}
            </button>

            {/*  DECORATIVE GRAPH / GLOW */}
            <div className="mt-6 h-28 rounded-xl
                bg-gradient-to-r from-purple-500/40 to-transparent"
            />

            {/*  DAY LABELS  */}
            <div className="flex justify-between text-xs text-gray-400 mt-3">
                {["Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"].map(day => (
                    <span key={day}>{day}</span>
                ))}
            </div>
        </div>
    );
}
