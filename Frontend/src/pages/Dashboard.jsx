import { useEffect, useState, useMemo } from "react";
import { useNavigate } from "react-router-dom";
import { toast } from "react-toastify";
import "../App.css";
import AccountSwitcher from "../components/dashboardRelated/AccountSwitcher";
import BalanceCard from "../components/dashboardRelated/BalanceCard";
import { useAuth } from "../context/AuthContext";
import { getAvailableBalance } from "../services/dashboardServices";
import BankLogo from "../components/BankLogo";
//send money
import SendMoneySection from "../components/dashboardRelated/SendMoneySection";
import PaymentActivityCard from "../components/dashboardRelated/PaymentActivityCard";
import PinModal from "../components/dashboardRelated/PinModal";
import { sendMoneyToOne } from "../services/dashboardServices";
import { sendMoneyToMany } from "../services/dashboardServices";
import { useAccount } from "../context/AccountContext";

/*  DASHBOARD PAGE
   This is the main landing page after login.
   Responsibilities:
   - Account selection
   - View account balance (PIN protected)
   - Send money (PIN protected)
   - Show transaction results
   - Show payment activity
   - Prevent invalid actions when no account exists
 */
import SendToMany from "../components/sendMoneyToManyComponents/SendToMany";
export default function Dashboard() {


    const [showPinModal, setShowPinModal] = useState(false);
    const [pin, setPin] = useState("");
    const [availableBalance, setAvailableBalance] = useState(null);
    const [verifyingPin, setVerifyingPin] = useState(false);
    const { auth, refreshNotificationSummary } = useAuth();
    const accounts = useMemo(() => {
        return auth?.accounts ?? [];
    }, [auth?.accounts]);
    const { activeAccount, setActiveAccount } = useAccount();
    const [showBalance, setShowBalance] = useState(false);
    const [loadingBalance, setLoadingBalance] = useState(false);

    //Sending money
    const [sendKey, setSendKey] = useState(0);

    const [resetSendMoneyForm, setResetSendMoneyForm] = useState(null);

    const navigate = useNavigate();
    const hasAccounts = accounts.length > 0;
    // Send money PIN
    const [showSendPinModal, setShowSendPinModal] = useState(false);
    const [sendMoneyPayload, setSendMoneyPayload] = useState(null);

    const [sendingMoney, setSendingMoney] = useState(false);

    //send money card
    const [transactionResult, setTransactionResult] = useState(null);
    const clearTransactionResult = () => {
        setTransactionResult(null);
    };


    //Payment Activity
    const [activityRefreshKey, setActivityRefreshKey] = useState(0);


    //SINGLE
    const handleSendMoneyWithPin = async () => {
        if (pin.length !== 6 || !sendMoneyPayload) return;

        setSendingMoney(true);
        try {
            const res = await sendMoneyToOne({
                ...sendMoneyPayload,
                pin,
                transactionType: "P2P",
            });

            console.log(res.data)

            if (res?.data) {
                setTimeout(async () => {
                    setSendingMoney(false);

                    //  USE BACKEND RESPONSE DIRECTLY
                    setTransactionResult({
                        status: "SUCCESS",
                        data: res.data
                    });

                    await refreshNotificationSummary(auth?.userId);
                    setActivityRefreshKey(prev => prev + 1);
                }, 2000);
            } else {
                setTimeout(() => {
                    setSendingMoney(false);

                    setTransactionResult({
                        status: "FAILED",
                        message: res.error || "Transaction failed"
                    });
                }, 2000);
            }
        } catch (e) {
            setSendingMoney(false);

        } finally {
            setShowSendPinModal(false);
            setSendMoneyPayload(null);
            setPin("");
            setSendKey(prev => prev + 1);
        }
    };

    //BULK
    const handleSendMoneyToManyWithPin = async () => {
        if (pin.length !== 6 || !sendMoneyPayload) return;

        setSendingMoney(true);

        try {
            const res = await sendMoneyToMany({
                ...sendMoneyPayload, // { userId, fromAccountId, receivers }
                pin
            });

            if (res?.data) {
                setTimeout(async () => {
                    setSendingMoney(false);

                    setTransactionResult({
                        status: "SUCCESS",
                        data: res.data // List<SendMoneyResponseDto>
                    });

                    await refreshNotificationSummary(auth.userId);
                    setActivityRefreshKey(prev => prev + 1);
                }, 2000);
            } else {
                throw new Error(res.error || "Bulk transaction failed");
            }
        } catch (e) {
            setSendingMoney(false);

            setTransactionResult({
                status: "FAILED",
                message: e.message
            });
        } finally {
            setShowSendPinModal(false);
            setSendMoneyPayload(null);
            setPin("");
        }
    };


    const requireAccount = () => {
        toast.info("Please link a bank account to continue");
        navigate("/userProfile");
    };

    useEffect(() => {
        if (accounts.length) {
            setActiveAccount(prev =>
                prev && accounts.find(a => a.bankAccountId === prev.bankAccountId)
                    ? prev
                    : accounts[0]
            );
        }
    }, [accounts]);


    useEffect(() => {
        setShowBalance(false);
        setAvailableBalance(null);
    }, [activeAccount?.bankAccountId]);

    //view balance
    const handleBalanceWithPin = async () => {
        if (pin.length !== 6) return;

        setVerifyingPin(true);
        setLoadingBalance(true);

        try {
            const res = await getAvailableBalance(
                auth?.userId,
                activeAccount.bankAccountId,
                pin
            );

            if (res?.data !== null) {
                setShowPinModal(false);
                setPin("");

                //  KEEP LOADING FOR SMOOTH UX
                setTimeout(() => {
                    setAvailableBalance(res.data);
                    setShowBalance(true);
                    setLoadingBalance(false);
                }, 2000);

            } else {
                setLoadingBalance(false);
                toast.error(res.error);
            }
        } catch (e) {
            setSendingMoney(false);

            const message =
                e?.response?.data?.message || "Transaction failed";

            setTransactionResult({
                status: "FAILED",
                message
            });

            toast.error(message);

        } finally {
            setVerifyingPin(false); // ONLY PIN STATE
        }
    };
    //to avoid scroll when pop up opens
    useEffect(() => {
        if (transactionResult || showPinModal || showSendPinModal) {
            document.body.style.overflow = "hidden";
        } else {
            document.body.style.overflow = "";
        }

        return () => {
            document.body.style.overflow = "";
        };
    }, [transactionResult, showPinModal, showSendPinModal]);

    return (
        <>

            <div className="relative min-h-screen w-full overflow-hidden bg-gradient-to-br from-[#0b1220] via-[#0f172a] to-[#020617]">

                {/* Glow Effects */}
                <div className="absolute -top-40 -right-40 w-[500px] h-[500px] bg-purple-500/30 rounded-full blur-[140px]" />
                <div className="absolute -bottom-40 -left-40 w-[500px] h-[500px] bg-indigo-500/30 rounded-full blur-[140px]" />

                {/* Content */}
                <div className="relative z-10 px-6 py-6 space-y-6">

                    {/* Account Selector */}
                    <div>
                        <label className="text-sm text-gray-300 mb-1 block">
                            Account Selector
                        </label>

                        {hasAccounts && activeAccount ? (
                            <AccountSwitcher
                                accounts={accounts}
                                activeAccount={activeAccount}
                                setActiveAccount={setActiveAccount}
                            />
                        ) : (
                            <div
                                onClick={requireAccount}
                                className="w-72 px-4 py-2 rounded-xl bg-white/10 backdrop-blur
               border border-white/10 text-gray-400 cursor-pointer"
                            >
                                No account linked
                            </div>
                        )}

                    </div>

                    {/* Top Cards */}
                    <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">

                        {/* Account Balance */}
                        <BalanceCard
                            hasAccounts={hasAccounts}
                            availableBalance={availableBalance}
                            showBalance={showBalance}
                            loadingBalance={loadingBalance}
                            onCheckBalance={() => {
                                if (!hasAccounts) {
                                    requireAccount();
                                    return;
                                }

                                if (showBalance) {
                                    setShowBalance(false);
                                    setTimeout(() => {
                                        setAvailableBalance(null);
                                    }, 500);
                                } else {
                                    setShowPinModal(true);
                                }
                            }}
                        />

                        {/* Send Money */}
                        <SendMoneySection
                            key={sendKey}
                            userId={auth.userId}
                            activeAccount={activeAccount}
                            accounts={accounts}
                            requireAccount={requireAccount}
                            openSendPinModal={() => setShowSendPinModal(true)}
                            setSendMoneyPayload={setSendMoneyPayload}
                            onReset={setResetSendMoneyForm}
                            sendingMoney={sendingMoney}
                            setSendingMoney={setSendingMoney}
                            transactionResult={transactionResult}
                            setTransactionResult={setTransactionResult}

                        />


                        {/* Payment Activity */}
                        <PaymentActivityCard refreshKey={activityRefreshKey} activeAccount={activeAccount} />

                    </div>


                    {/* Quick Actions */}

                    <div>
                        <h3 className="text-white font-semibold mb-3">Quick Actions</h3>

                        <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
                            {/* Send to Many */}
                            <div
                                onClick={() => navigate("/dashboard/send-to-many")}
                                className="bg-white/10 backdrop-blur-xl border border-white/10
             rounded-xl p-4 text-center text-white cursor-pointer
             hover:bg-white/20 transition"
                            >
                                Send to Many
                            </div>


                            {/* Request Money */}
                            <div
                                className="bg-white/10 backdrop-blur-xl border border-white/10
                 rounded-xl p-4 text-center text-white"
                            >
                                Request Money
                            </div>

                            {/* Schedule Payment */}
                            <div
                                className="bg-white/10 backdrop-blur-xl border border-white/10
                 rounded-xl p-4 text-center text-white"
                            >
                                Schedule Payment
                            </div>

                            {/* Refer & Earn (can remove later easily) */}
                            <div
                                className="bg-white/10 backdrop-blur-xl border border-white/10
                 rounded-xl p-4 text-center text-white"
                            >
                                Refer & Earn
                            </div>
                        </div>
                    </div>


                    {/* Bottom Section */}
                    <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">

                        {/* Requests */}
                        <div className="bg-white/10 backdrop-blur-xl border border-white/10 rounded-2xl p-6">
                            <h3 className="text-white font-semibold mb-4">
                                Requests Overview
                            </h3>

                            {[
                                { name: "Sarah K.", amount: "₹2,000", note: "Rent" },
                                { name: "Alex B.", amount: "₹500", note: "Dinner" }
                            ].map((req) => (
                                <div
                                    key={req.name}
                                    className="flex justify-between items-center mb-3 px-4 py-3 rounded-xl bg-white/5 border border-white/10"
                                >
                                    <span className="text-sm text-white">
                                        From: {req.name} | {req.amount} | {req.note}
                                    </span>
                                    <div className="flex gap-2">
                                        <button
                                            onClick={() => {
                                                if (!hasAccounts) {
                                                    requireAccount();
                                                    return;
                                                }
                                            }}
                                            className="px-3 py-1 text-xs bg-green-500 rounded"
                                        >
                                            Approve
                                        </button>

                                        <button
                                            onClick={() => {
                                                if (!hasAccounts) {
                                                    requireAccount();
                                                    return;
                                                }
                                            }}
                                            className="px-3 py-1 text-xs bg-red-500 rounded"
                                        >
                                            Reject
                                        </button>


                                    </div>

                                </div>

                            ))}
                        </div>

                        {/* Scheduled Payments */}
                        <div className="bg-white/10 backdrop-blur-xl border border-white/10 rounded-2xl p-6">
                            <h3 className="text-white font-semibold mb-4">
                                Scheduled Payments Overview
                            </h3>

                            <div className="flex justify-between text-sm text-white mb-3">
                                <span>Rent • ₹18,000</span>
                                <span className="text-purple-400">Scheduled</span>
                            </div>

                            <div className="flex justify-between text-sm text-white mb-4">
                                <span>Electricity Bill • ₹2,500</span>
                                <span className="text-yellow-400">Pending</span>
                            </div>

                            <button
                                onClick={() => {
                                    if (!hasAccounts) {
                                        requireAccount();
                                        return;
                                    }
                                }}
                                className="w-full py-2 rounded-xl border border-purple-400 text-purple-400 hover:bg-purple-400 hover:text-black transition">
                                Manage Payments
                            </button>

                        </div>
                    </div>

                </div>


                {showPinModal && (
                    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/60">
                        <div className="w-[90%] max-w-sm bg-[#0f172a] rounded-2xl p-6 border border-white/10">

                            <h3 className="text-white text-lg font-semibold mb-2">
                                Enter UPI PIN
                            </h3>
                            <p className="text-sm text-gray-400 mb-4">
                                Enter your 6-digit PIN to view balance
                            </p>

                            <input
                                type="password"
                                maxLength={6}
                                value={pin}
                                onChange={(e) => setPin(e.target.value.replace(/\D/g, ""))}
                                className="w-full px-4 py-3 rounded-xl bg-white/10 border border-white/10
                   text-white text-center tracking-widest text-lg outline-none"
                                placeholder="••••••"
                            />

                            <div className="flex gap-3 mt-5">
                                <button
                                    onClick={() => {
                                        setShowPinModal(false);
                                        setPin("");
                                    }}
                                    className="flex-1 py-2 rounded-xl border border-white/20 text-gray-300"
                                >
                                    Cancel
                                </button>

                                <button
                                    disabled={pin.length !== 6 || verifyingPin}
                                    onClick={handleBalanceWithPin}
                                    className="flex-1 py-2 rounded-xl bg-purple-500 text-black font-semibold
                     disabled:opacity-50"
                                >
                                    {verifyingPin ? "Verifying..." : "Verify"}
                                </button>
                            </div>
                        </div>
                    </div>
                )}
                {showSendPinModal && (
                    <PinModal
                        pin={pin}
                        setPin={setPin}
                        title="Confirm Payment"
                        subtitle={`Enter UPI PIN to send ₹${sendMoneyPayload?.amount}`}
                        onCancel={() => {
                            setShowSendPinModal(false);
                            setSendMoneyPayload(null);
                            setPin("");

                            resetSendMoneyForm?.();
                            setSendKey(prev => prev + 1);
                        }}
                        onConfirm={() => {
                            if (Array.isArray(sendMoneyPayload?.receivers)) {
                                handleSendMoneyToManyWithPin(); // BULK
                            } else {
                                handleSendMoneyWithPin(); // SINGLE
                            }
                        }}
                    />
                )}

                {transactionResult && (
                    <div className="fixed inset-0 z-[99999] flex items-center justify-center bg-black/70 backdrop-blur-sm">


                        <div
                            className="
                        relative
                        w-full max-w-2xl
                        rounded-3xl
                      bg-gradient-to-r from-[#0b1220] via-[#0f172a] to-[#020617]
                        ring-1 ring-white/10
                        shadow-[0_40px_120px_rgba(0,0,0,0.9)]
                        px-12 py-10
                    "
                        >



                            {/* ================= STATUS HEADER ================= */}
                            <div className="flex flex-col items-center text-center mb-6">

                                <div
                                    className={`w-14 h-14 rounded-full flex items-center justify-center mb-3
          ${transactionResult.status === "SUCCESS"
                                            ? "bg-green-500/30 text-green-400"
                                            : "bg-red-500/30 text-red-400"}`}
                                >
                                    {transactionResult.status === "SUCCESS" ? "✓" : "✕"}
                                </div>

                                <p className="text-white font-semibold text-xl">
                                    {transactionResult.status === "SUCCESS"
                                        ? "Payment Successful"
                                        : "Payment Failed"}
                                </p>

                                {transactionResult.status === "SUCCESS" && (
                                    <>
                                        <p className="text-2xl font-bold text-white mt-1">
                                            ₹{transactionResult.data.amount}
                                        </p>

                                        <p className="text-sm text-purple-300 mt-2">
                                            Ref: {transactionResult.data.transactionRef}
                                        </p>

                                        <p className="text-sm text-indigo-300">
                                            {new Date(transactionResult.data.completedAt).toLocaleString()}
                                        </p>
                                    </>
                                )}
                            </div>

                            {/* ================= SUCCESS DETAILS ================= */}
                            {transactionResult.status === "SUCCESS" && (
                                <div className="flex flex-col md:flex-row items-center justify-center gap-4 mb-6">

                                    {/* FROM */}

                                    <div className="w-full md:w-[40%] bg-white/10 rounded-2xl min-h-[140px] p-6 flex flex-col justify-start">
                                        <p className="text-[10px] tracking-wide text-gray-400 mb-2">FROM</p>

                                        <div className="flex items-start gap-3">
                                            <div className="w-10 h-10 shrink-0 flex items-center justify-center">
                                                <BankLogo bankName={transactionResult.data.sender.bankName} />
                                            </div>

                                            <div className="flex flex-col gap-1.5 leading-relaxed">
                                                <p className="text-sm font-semibold text-white">
                                                    {transactionResult.data.sender.name}
                                                </p>

                                                <p className="text-xs text-gray-300">
                                                    {transactionResult.data.sender.bankName}
                                                </p>

                                                <p className="text-[11px] text-gray-300">
                                                    {transactionResult.data.sender.maskedAccountNo}
                                                </p>
                                            </div>
                                        </div>
                                    </div>


                                    {/* ARROW */}
                                    <div className="text-purple-400 text-3xl hidden md:block">→</div>

                                    {/* TO */}
                                    <div className="w-full md:w-[40%] bg-white/10 rounded-2xl min-h-[140px] p-6 flex flex-col justify-start">
                                        <p className="text-[10px] tracking-wide text-gray-400 mb-2">TO</p>

                                        <div className="flex items-start gap-3">
                                            <div className="w-10 h-10 shrink-0 flex items-center justify-center">
                                                <BankLogo bankName={transactionResult.data.receiver.bankName} />
                                            </div>

                                            <div className="flex flex-col gap-1.5 leading-relaxed">
                                                <p className="text-sm font-semibold text-white">
                                                    {transactionResult.data.receiver.name}
                                                </p>

                                                <p className="text-xs text-gray-300">
                                                    {transactionResult.data.receiver.bankName}
                                                </p>

                                                <p className="text-[11px] text-gray-300">
                                                    {transactionResult.data.receiver.maskedAccountNo}
                                                </p>
                                            </div>
                                        </div>
                                    </div>

                                </div>
                            )}

                            {/* ================= FAILED MESSAGE ================= */}
                            {transactionResult.status === "FAILED" && (
                                <div className="text-center mb-6">
                                    <p className="text-sm text-gray-300">
                                        {transactionResult.message || "Something went wrong. Please try again."}
                                    </p>
                                </div>
                            )}

                            {/* ================= ACTION ================= */}
                            <button
                                onClick={() => {
                                    setTransactionResult(null);
                                    resetSendMoneyForm?.();
                                }}
                                className="w-full py-3 rounded-xl
          bg-purple-500 text-black
          text-lg font-semibold hover:opacity-90"
                            >
                                Back to Dashboard
                            </button>

                        </div>
                    </div>
                )}

            </div>
        </>

    );
}
