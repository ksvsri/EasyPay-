/*

 PinModal Component
- Displays a modal to confirm a 6-digit UPI PIN
- Used while:
  • Sending money
  • Viewing balance
- Blocks background interaction using a full-screen overlay

*/
export default function PinModal({
  pin,        // Current PIN value entered by the user
  setPin,     // State updater function to modify PIN
  title,      // Title shown at the top of the modal
  subtitle,   // Subtitle / description text
  onCancel,   // Callback triggered when user cancels
  onConfirm   // Callback triggered when user confirms PIN
}) {
  return (
    /*  FULL SCREEN OVERLAY  */
    // Covers the entire screen and prevents background interaction
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/60">

      {/*  MODAL CONTAINER  */}
      <div className="w-[90%] max-w-sm bg-[#0f172a] rounded-2xl p-6 border border-white/10">

        {/*  TITLE */}
        <h3 className="text-white text-lg font-semibold mb-2">
          {title}
        </h3>

        {/*  SUBTITLE */}
        <p className="text-sm text-gray-400 mb-4">
          {subtitle}
        </p>

        {/* PIN INPUT  */}
        {/*
          - Accepts only numeric input
          - Maximum length is 6 digits
          - Input is masked for security
        */}
        <input
          type="password"
          maxLength={6}
          value={pin}
          onChange={(e) =>
            // Replace non-numeric characters
            setPin(e.target.value.replace(/\D/g, ""))
          }
          className="
            w-full px-4 py-3 rounded-xl
            bg-white/10 border border-white/10
            text-white text-center tracking-widest text-lg
            outline-none
          "
          placeholder="••••••"
        />

        {/*  ACTION BUTTONS  */}
        <div className="flex gap-3 mt-5">

          {/* Cancel Button */}
          {/* Closes modal without performing any action */}
          <button
            onClick={onCancel}
            className="flex-1 py-2 rounded-xl border border-white/20 text-gray-300"
          >
            Cancel
          </button>

          {/* Confirm Button */}
          {/* Enabled only when PIN length is exactly 6 */}
          <button
            disabled={pin.length !== 6}
            onClick={onConfirm}
            className="
              flex-1 py-2 rounded-xl
              bg-purple-500 text-black font-semibold
              disabled:opacity-50
            "
          >
            Confirm
          </button>
        </div>
      </div>
    </div>
  );
}
