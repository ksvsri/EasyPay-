import { useMemo, useState } from "react";
import { Plus } from "lucide-react";
import { useNavigate } from "react-router-dom";

import ReceiverInputBlock from "../../components/sendMoneyToManyComponents/ReceiverInputBlock";
import { isValidBulkForm } from "../../validationUtils/globalValidity";

/*
UC-05( SEND MONEY TO MANY)
  Helper to create a new empty receiver block.
  - tempId is used as a stable unique key (even before UPI is confirmed)
  - locked indicates whether the receiver is confirmed or still editable
*/
const emptyReceiver = () => ({
    tempId: crypto.randomUUID(),
    receiverUpiId: "",
    receiverName: "",
    bankName: "",
    maskedAccountNo: "",
    amount: "",
    note: "",
    locked: false
});

export default function SendToMany({
    userId,
    activeAccount,
    accounts,
    requireAccount,
    openSendPinModal,
    setBulkSendPayload,
    sendingMoney,
}) {
    const navigate = useNavigate();

    // List of receivers in the bulk flow
    const [receivers, setReceivers] = useState([emptyReceiver()]);

    /*
      Called when a ReceiverInputBlock is CONFIRMED.
      - Prevents duplicate UPI IDs
      - Locks the receiver once confirmed
    */
    const updateReceiver = (tempId, data) => {
        setReceivers(prev => {
            // Check if same UPI already exists (excluding current block)
            const duplicate = prev.some(
                r =>
                    r.receiverUpiId === data.receiverUpiId &&
                    r.tempId !== tempId
            );

            if (duplicate) {
                alert("This UPI ID is already added");

                // Reset only the current receiver block
                return prev.map(r =>
                    r.tempId === tempId
                        ? {
                            ...r,
                            receiverUpiId: "",
                            receiverName: "",
                            bankName: "",
                            maskedAccountNo: "",
                            amount: "",
                            note: "",
                            locked: false
                        }
                        : r
                );
            }

            // Valid receiver → store data and lock it
            return prev.map(r =>
                r.tempId === tempId
                    ? { ...r, ...data, locked: true }
                    : r
            );
        });
    };

    /*
      Adds a new empty receiver block.
      Only allowed after the previous one is confirmed.
    */
    const addReceiver = () => {
        setReceivers(prev => [...prev, emptyReceiver()]);
    };

    /*
      Removes a specific receiver using tempId
      (not index-based, so removal is always correct)
    */
    const removeReceiver = (tempId) => {
        setReceivers(prev =>
            prev.filter(r => r.tempId !== tempId)
        );
    };

    // Validate full bulk form
    const isFormValid = isValidBulkForm(receivers);

    /*
      Calculate total amount dynamically
      Recomputed only when receivers change
    */
    const totalAmount = useMemo(() => {
        return receivers.reduce((sum, r) => {
            const amt = Number(r.amount);
            return sum + (isNaN(amt) ? 0 : amt);
        }, 0);
    }, [receivers]);

    /*
      Allow adding next receiver only if last one is confirmed
    */
    const canAddReceiver =
        receivers.length === 0 ||
        !!receivers[receivers.length - 1].receiverUpiId;

    return (
        <div className="relative max-w-3xl mx-auto bg-white/10 backdrop-blur-xl border border-white/10 rounded-3xl p-8 space-y-8">

            {/* Header */}
            <div className="flex items-center gap-3">
                <button
                    onClick={() => navigate(-1)}
                    className="text-gray-400 hover:text-white"
                >
                    ←
                </button>
                <h3 className="text-lg text-white font-semibold">
                    Send Money
                </h3>
            </div>

            {/* Active account info */}
            <p className="text-xs text-gray-400">
                Paying from:{" "}
                {activeAccount
                    ? `${activeAccount.bankName} • ${activeAccount.maskedAccountNo}`
                    : "Link an account"}
            </p>

            {/* Receiver blocks */}
            <div className="space-y-8">
                {receivers.map((receiver, index) => (
                    <div
                        key={receiver.tempId}
                        className="rounded-2xl border border-white/10 bg-white/5 p-6 transition-all duration-300"
                    >
                        <div className="flex items-center justify-between">
                            <p className="text-sm text-gray-400">
                                Receiver {index + 1}
                            </p>

                            {/* Remove only shown if more than one receiver exists */}
                            {receivers.length > 1 && (
                                <button
                                    onClick={() => removeReceiver(receiver.tempId)}
                                    className="text-red-400 text-xs"
                                >
                                    Remove
                                </button>
                            )}
                        </div>

                        {/* Receiver input / confirmation block */}
                        <div className="relative z-20">
                            <ReceiverInputBlock
                                userId={userId}
                                locked={receiver.locked}
                                onResolved={(data) =>
                                    updateReceiver(receiver.tempId, data)
                                }
                            />
                        </div>
                    </div>
                ))}
            </div>

            {/* Add another receiver */}
            {canAddReceiver && (
                <button
                    onClick={addReceiver}
                    className="flex items-center gap-2 px-4 py-2 rounded-lg
                        border border-white/10 bg-white/10
                        hover:bg-white/20 transition text-white"
                >
                    <Plus size={16} />
                    Add another receiver
                </button>
            )}

            {/* Sticky footer with summary + send action */}
            <div className="sticky bottom-0 pt-4 space-y-4 bg-[#0b1220] z-10">
                <div className="flex items-center justify-between px-4 py-3 rounded-xl
                        bg-white/10 border border-white/10">
                    <span className="text-sm text-gray-300">
                        Receivers: <b>{receivers.length}</b>
                    </span>
                    <span className="text-sm text-gray-300">
                        Total: <b>₹{totalAmount.toFixed(2)}</b>
                    </span>
                </div>

                {/* Final send button */}
                <button
                    disabled={!isFormValid || sendingMoney}
                    onClick={() => {
                        if (!accounts.length) {
                            requireAccount();
                            return;
                        }

                        // Prepare payload for bulk send
                        setBulkSendPayload({
                            userId,
                            fromAccountId: activeAccount.bankAccountId,
                            receivers
                        });

                        openSendPinModal();
                    }}
                    className={`w-full py-3 rounded-xl font-semibold transition
                        ${isFormValid
                            ? "bg-purple-500 text-white hover:opacity-90"
                            : "bg-gray-600 text-gray-300 cursor-not-allowed"
                        }`}
                >
                    Send ₹{totalAmount.toFixed(2)} to {receivers.length} people
                </button>
            </div>

            {/* Sending overlay */}
            {sendingMoney && (
                <div className="fixed inset-0 z-[999] flex items-center justify-center bg-black/60 backdrop-blur">
                    <div className="flex flex-col items-center gap-4">
                        <div className="relative w-16 h-16 flex items-center justify-center">
                            <div className="absolute inset-0 rounded-full border-2 border-purple-400/30 animate-ping" />
                            <div className="absolute inset-2 rounded-full border border-purple-400/50" />
                            <span className="text-purple-400 text-xl font-semibold">₹</span>
                        </div>
                        <p className="text-white text-sm font-medium">
                            Sending money
                        </p>
                        <p className="text-xs text-gray-400">
                            Please don’t close the app
                        </p>
                    </div>
                </div>
            )}
        </div>
    );
}
