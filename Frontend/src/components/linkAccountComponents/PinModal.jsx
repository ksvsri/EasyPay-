// Back arrow icon
import { ArrowLeft } from "lucide-react";

// Reusable PIN input component
import PinInput from "./PinInput";

/*
  PinModal Component

  - Displays a modal for setting and confirming a UPI PIN.
  - Uses two PinInput components:
      1. Enter PIN
      2. Confirm PIN
  - Validates that both PINs match before allowing confirmation.
*/
function PinModal({
    open,            // Controls modal visibility
    title,           // Modal title text
    confirmText,     // Text for confirm button
    upiPin,          // Entered UPI PIN array
    confirmPin,      // Confirmed UPI PIN array
    setUpiPin,       // Setter for UPI PIN
    setConfirmPin,   // Setter for confirm PIN
    onBack,          // Callback when user clicks back or backdrop
    onConfirm,       // Callback when user confirms PIN
}) {

    // Do not render modal if it is closed
    if (!open) return null;

    /*
      Check if PINs mismatch:
      - Confirm PIN has at least one digit entered
      - AND both PIN values do not match
    */
    const mismatch =
        confirmPin.some(d => d !== "") &&
        upiPin.join("") !== confirmPin.join("");

    return (
        // Fullscreen modal wrapper
        <div className="fixed inset-0 z-[100] flex items-center justify-center">

            {/*  BACKDROP  */}
            {/* Clicking backdrop navigates back / closes modal */}
            <div
                className="absolute inset-0 bg-black/60"
                onClick={onBack}
            />

            {/*  MODAL CONTENT  */}
            <div
                className="relative z-[110] bg-[#0f172a] p-6 rounded-xl w-full max-w-sm"
                onClick={(e) => e.stopPropagation()} // Prevent backdrop click
            >

                {/* Header with back button and title */}
                <div className="flex gap-3 mb-4">
                    <button onClick={onBack}>
                        <ArrowLeft className="text-white" />
                    </button>

                    <h4 className="text-white">
                        {title}
                    </h4>
                </div>

                {/* PIN input for first entry */}
                <PinInput
                    label="Enter PIN"
                    pin={upiPin}
                    setPin={setUpiPin}
                />

                {/* PIN input for confirmation */}
                <PinInput
                    label="Confirm PIN"
                    pin={confirmPin}
                    setPin={setConfirmPin}
                />

                {/* Mismatch error message */}
                {mismatch && (
                    <p className="text-red-400 text-xs">
                        PIN should match
                    </p>
                )}

                {/* Confirm button */}
                <button
                    disabled={
                        upiPin.includes("") ||
                        confirmPin.includes("") ||
                        mismatch
                    }
                    onClick={onConfirm}
                    className="mt-4 w-full bg-violet-600 py-2 rounded disabled:opacity-50"
                >
                    {confirmText}
                </button>

            </div>
        </div>
    );
}

export default PinModal;
