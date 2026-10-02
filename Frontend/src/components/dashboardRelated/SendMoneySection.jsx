// Receiver input component (handles receiver details, validation & resolution)
import ReceiverInputBlock from "../../components/sendMoneyToManyComponents/ReceiverInputBlock";

/*

 SendMoneySection Component

- Handles sending money to a single receiver
- Uses ReceiverInputBlock for receiver + amount input
- Opens PIN modal before final transaction
- Shows full-screen loading overlay while sending money

*/
export default function SendMoneySection({
  userId,                 // Logged-in user ID
  activeAccount,          // Currently selected bank account
  accounts,               // List of linked bank accounts
  requireAccount,         // Callback to force user to link an account
  openSendPinModal,       // Opens PIN confirmation modal
  setSendMoneyPayload,    // Sets payload for send money API
  sendingMoney            // Flag to show loading overlay
}) {

  return (
    /*  MAIN CARD CONTAINER */
    <div className="bg-white/10 backdrop-blur-xl border border-white/10 rounded-2xl p-6">

      {/*  SECTION TITLE  */}
      <h3 className="text-lg text-white font-semibold mb-4">
        Send Money
      </h3>

      {/*  ACTIVE ACCOUNT INFO  */}
      <p className="text-xs text-gray-400 mb-3">
        Paying from:{" "}
        {activeAccount
          ? `${activeAccount.bankName} • ${activeAccount.maskedAccountNo}`
          : "Link an account"}
      </p>

      {/*  RECEIVER INPUT BLOCK */}
      {/*
        - Collects receiver (UPI / mobile), amount & note
        - Resolves receiver details from backend
        - Calls onResolved only after successful validation
      */}
      <ReceiverInputBlock
        userId={userId}
        onResolved={(data) => {

          // If no account is linked, redirect user to profile
          if (!accounts.length) {
            requireAccount();
            return;
          }

          // Prepare payload for send money API
          setSendMoneyPayload({
            userId,
            fromAccountId: activeAccount.bankAccountId,
            ...data
          });

          // Open PIN confirmation modal
          openSendPinModal();
        }}
      />

      {/*  SENDING MONEY LOADING OVERLAY  */}
      {/*
        - Blocks UI interaction
        - Shown while backend transaction is in progress
      */}
      {sendingMoney && (
        <div className="fixed inset-0 z-[999] flex items-center justify-center bg-black/60 backdrop-blur">

          {/* Loader content */}
          <div className="flex flex-col items-center gap-4">

            {/* Animated circular loader */}
            <div className="relative w-16 h-16 flex items-center justify-center">
              <div className="absolute inset-0 rounded-full border-2 border-purple-400/30 animate-ping" />
              <div className="absolute inset-2 rounded-full border border-purple-400/50" />
              <span className="text-purple-400 text-xl font-semibold">₹</span>
            </div>

            {/* Loading message */}
            <p className="text-white text-sm font-medium">
              Sending money
            </p>

            {/* Subtext */}
            <p className="text-xs text-gray-400">
              Please don’t close the app
            </p>
          </div>
        </div>
      )}

    </div>
  );
}
