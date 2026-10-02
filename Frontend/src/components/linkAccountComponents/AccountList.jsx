/*
  AccountList Component

  - Displays a list of bank accounts for a selected bank.
  - Handles both linked and unlinked accounts.
  - Shows an empty state message if no accounts are available.
  - Delegates individual account rendering to AccountCard.
*/

function AccountList({
  accounts,        // Array of bank accounts for the selected bank
  onLink,          // Callback to link an existing account
  onCreateUpi,     // Callback to create a UPI ID for an account
}) {

  // If there are no accounts for the selected bank,
  // show a friendly empty-state message
  if (accounts.length === 0) {
    return (
      <div className="flex-1 flex items-center justify-center">
        <p className="text-white/60 text-sm">
          No accounts available for this bank
        </p>
      </div>
    );
  }

  // Render scrollable list of accounts
  return (
    <div className="flex-1 overflow-y-auto space-y-3">
      {accounts.map((acc) => (
        <AccountCard
          key={acc.bankAccountId} // Unique key for React rendering
          acc={acc}               // Individual account data
          onLink={onLink}         // Pass link handler
          onCreateUpi={onCreateUpi} // Pass create UPI handler
        />
      ))}
    </div>
  );
}

/*
  AccountCard Component

  - Displays details of a single bank account.
  - Shows account linking status (Linked / Primary).
  - Displays UPI ID if available.
  - Allows user to:
      - Link an account if UPI already exists.
      - Create a UPI ID if it does not exist.
*/

function AccountCard({ acc, onLink, onCreateUpi }) {
  return (
    // Card container for individual account
    <div className="bg-white/5 border border-white/10 rounded-xl p-4 flex justify-between items-center">

      {/* Left section: Account details */}
      <div>
        {/* Bank name and account type */}
        <p className="font-medium text-white">
          {acc.bankName} • {acc.accountType}
        </p>

        {/* Masked account number */}
        <p className="text-sm text-white/60">
          {acc.maskedAccountNo}
        </p>

        {/* Display UPI ID if it exists */}
        {acc.upiExists && (
          <p className="text-xs text-white/40">
            {acc.upiId}
          </p>
        )}

        {/*
          Display account status:
          - "Primary" if it is the main linked account
          - "Linked" if linked but not primary
        */}
        {acc.linked && (
          <span className="text-xs text-green-400">
            {acc.primary ? "Primary" : "Linked"}
          </span>
        )}
      </div>

      {/*
        1. Account is NOT linked but UPI exists → Allow direct linking.
        2. Account is NOT linked and UPI does NOT exist → Allow UPI creation.
      */}

      {/* Link Account button */}
      {!acc.linked && acc.upiExists && (
        <button
          onClick={() => onLink(acc.bankAccountId)}
          className="text-violet-400 text-sm hover:underline"
        >
          Link Account
        </button>
      )}

      {/* Create UPI button */}
      {!acc.linked && !acc.upiExists && (
        <button
          onClick={() => onCreateUpi(acc.bankAccountId)}
          className="text-violet-400 text-sm hover:underline"
        >
          Create UPI
        </button>
      )}
    </div>
  );
}

export default AccountList;
