// React hooks
import { useEffect, useMemo, useState } from "react";

// Icons
import { ArrowLeft } from "lucide-react";

// Toast notifications
import { toast } from "react-toastify";

// Auth context
import { useAuth } from "../../context/AuthContext";

// UI services
import {
    getAccountOfSelectedBank,
    getvalidUpis,
} from "../../services/uiService";

// User services
import { linkAccount, confirmUpi } from "../../services/userService";

// Child components
import AccountList from "./AccountList";
import UpiSelectModal from "./UpiSelectModal";
import PinModal from "./PinModal";
import BankLogo from "../BankLogo";

// Initial empty PIN state (6 digits)
const EMPTY_PIN = ["", "", "", "", "", ""];

/*
  AddBankModal Component

  - Main modal for linking a bank account.
  - Allows user to:
      1. Select a bank
      2. View accounts of that bank
      3. Link an existing account OR
      4. Create a UPI ID and confirm it with PIN
*/
function AddBankModal({ banks, onClose, onLinked }) {

    // Auth details and notification refresh
    const { auth, refreshNotificationSummary } = useAuth();

    /*  STATE MANAGEMENT */

    // Step control: selecting BANK or ACCOUNT
    const [step, setStep] = useState("BANK"); // BANK | ACCOUNT

    // Accounts fetched for selected bank
    const [accounts, setAccounts] = useState([]);

    // Currently selected bank
    const [selectedBank, setSelectedBank] = useState(null);

    // UPI-related states
    const [upiOptions, setUpiOptions] = useState([]);
    const [selectedUpi, setSelectedUpi] = useState(null);
    const [selectedAccount, setSelectedAccount] = useState(null);

    // Modal visibility states
    const [showUpiModal, setShowUpiModal] = useState(false);
    const [showPinModal, setShowPinModal] = useState(false);

    // UPI PIN states
    const [upiPin, setUpiPin] = useState(EMPTY_PIN);
    const [confirmPin, setConfirmPin] = useState(EMPTY_PIN);

    /*  DERIVED DATA  */

    /*
      useMemo is used to avoid recalculating values
      unless accounts array changes
    */
    const { hasAnyLinked, primaryAccount, unlinked } = useMemo(() => ({
        hasAnyLinked: accounts.some(a => a.linked),
        primaryAccount: accounts.find(a => a.linked && a.primary),
        unlinked: accounts.filter(a => !a.linked),
    }), [accounts]);

    /* SIDE EFFECTS */

    // Disable background scroll when modal is open
    useEffect(() => {
        document.body.style.overflow = "hidden";
        return () => (document.body.style.overflow = "");
    }, []);

    // Close modal on ESC key press
    useEffect(() => {
        const esc = e => e.key === "Escape" && onClose();
        window.addEventListener("keydown", esc);
        return () => window.removeEventListener("keydown", esc);
    }, [onClose]);

    /*  HELPERS  */

    // Refresh accounts list after linking or UPI creation
    const refreshAccounts = async () => {
        const res = await getAccountOfSelectedBank({
            bankId: selectedBank.bankId,
            userId: auth.userId,
        });
        if (res?.data) setAccounts(res.data);
    };

    /* ACTION HANDLERS */

    // Fetch accounts when a bank is selected
    const selectBank = async (bank) => {
        setSelectedBank(bank);

        const res = await getAccountOfSelectedBank({
            bankId: bank.bankId,
            userId: auth.userId,
        });

        if (!res?.data) return toast.error("Failed to fetch accounts");

        setAccounts(res.data);
        setStep("ACCOUNT");
    };

    // Link an already existing UPI account
    const handleLinkAccount = async (accountId) => {
        const res = await linkAccount({ userId: auth.userId, accountId });
        if (!res?.data) return toast.error(res?.error);

        toast.success("Account linked");
        onLinked();
        refreshAccounts();
        onClose();
    };

    // Fetch valid UPI IDs for creation
    const handleCreateUpi = async (accountId) => {
        const res = await getvalidUpis({ userId: auth.userId, accountId });
        if (!res?.data?.length) return toast.error("No UPI IDs available");

        setUpiOptions(res.data);
        setSelectedAccount(accountId);
        setSelectedUpi(null);
        setShowUpiModal(true);
    };

    // Confirm and create UPI with PIN
    const handleConfirmUpi = async () => {
        try {
            await confirmUpi({
                userId: auth.userId,
                accountId: selectedAccount,
                upiId: selectedUpi,
                upiPin: upiPin.join(""),
            });

            toast.success("UPI created");

            // Refresh notification badge counts
            await refreshNotificationSummary(auth.userId);

            // Reset states and modals
            setShowPinModal(false);
            setUpiPin(EMPTY_PIN);
            setConfirmPin(EMPTY_PIN);

            refreshAccounts();
        } catch (error) {
            toast.error(error?.message || "Failed to create UPI");
        }
    };


    return (
        // Fullscreen modal wrapper
        <div className="fixed inset-0 z-50 flex items-center justify-center">

            {/* Backdrop overlay */}
            <div
                className="absolute inset-0 bg-black/50 backdrop-blur-md"
                onClick={() => {
                    // Prevent closing when nested modals are open
                    if (showUpiModal || showPinModal) return;
                    onClose();
                }}
            />

            {/* Modal content */}
            <div
                className="relative z-10 w-full max-w-lg h-[70vh] bg-[#0f172a]
                           border border-white/10 rounded-2xl p-6 flex flex-col"
                onClick={(e) => e.stopPropagation()}
            >

                {/* Header with back navigation */}
                <div className="flex items-center gap-3 mb-4">
                    {step === "ACCOUNT" && (
                        <button onClick={() => setStep("BANK")}>
                            <ArrowLeft className="text-white" />
                        </button>
                    )}
                    <h3 className="text-white font-semibold">
                        {step === "BANK" ? "Select Your Bank" : "Select Account"}
                    </h3>
                </div>

                {/* Bank selection step */}
                {step === "BANK" && (
                    <div className="flex-1 overflow-y-auto pr-1">
                        <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
                            {banks.map((bank) => (
                                <button
                                    key={bank.bankId}
                                    onClick={() => selectBank(bank)}
                                    className="flex items-center gap-3 bg-white/5
                                               border border-white/10 rounded-xl p-3
                                               hover:bg-white/10 transition"
                                >
                                    <BankLogo bankName={bank.bankName} />
                                    <span className="text-sm font-medium text-white truncate">
                                        {bank.bankName}
                                    </span>
                                </button>
                            ))}
                        </div>
                    </div>
                )}

                {/* Account selection step */}
                {step === "ACCOUNT" && (
                    <AccountList
                        accounts={accounts}
                        hasAnyLinked={hasAnyLinked}
                        primaryAccount={primaryAccount}
                        unlinkedAccounts={unlinked}
                        onLink={handleLinkAccount}
                        onCreateUpi={handleCreateUpi}
                    />
                )}
            </div>

            {/* UPI selection modal */}
            <UpiSelectModal
                open={showUpiModal}
                upiOptions={upiOptions}
                selectedUpi={selectedUpi}
                onSelect={setSelectedUpi}
                onClose={() => setShowUpiModal(false)}
                onContinue={() => {
                    setShowUpiModal(false);
                    setShowPinModal(true);
                }}
            />

            {/* PIN confirmation modal */}
            <PinModal
                open={showPinModal}
                title="Set UPI PIN"
                confirmText="Confirm & Create"
                upiPin={upiPin}
                confirmPin={confirmPin}
                setUpiPin={setUpiPin}
                setConfirmPin={setConfirmPin}
                onBack={() => {
                    setShowPinModal(false);
                    setShowUpiModal(true);
                }}
                onConfirm={handleConfirmUpi}
            />
        </div>
    );
}

export default AddBankModal;
