import { useState } from "react";
import BankLogo from "../BankLogo";

/*

 AccountSwitcher Component

- Used in Dashboard & Transactions pages
- Allows user to switch between linked bank accounts
- Shows currently active account
- Displays dropdown list of all available accounts

*/
export default function AccountSwitcher({
    accounts,        // List of all linked bank accounts
    activeAccount,   // Currently selected (active) account
    setActiveAccount // Setter to update active account
}) {

    // Controls dropdown open/close state
    const [open, setOpen] = useState(false);

    // If no active account exists, do not render component
    if (!activeAccount) return null;

    return (
        <div className="relative w-80">

            {/*  ACTIVE ACCOUNT BUTTON  */}
            <button
                onClick={() => setOpen(prev => !prev)} // Toggle dropdown
                className="
                  w-full px-4 py-3 rounded-xl
                  bg-white/10 backdrop-blur
                  border border-white/10
                  hover:bg-white/15 transition
                "
            >
                <div className="flex items-center justify-between">

                    {/* Left side: Bank logo + account details */}
                    <div className="flex items-center gap-3">

                        {/* Bank Logo */}
                        <div className="h-10 w-10 flex items-center justify-center">
                            <BankLogo bankName={activeAccount.bankName} />
                        </div>

                        {/* Active Bank Details */}
                        <div className="text-left">
                            <p className="text-sm font-semibold text-white">
                                {activeAccount.bankName}
                            </p>
                            <p className="text-xs text-gray-400">
                                {activeAccount.maskedAccountNo} • {activeAccount.accountType}
                            </p>
                        </div>
                    </div>

                    {/* Dropdown arrow */}
                    <span className="text-gray-400">⌄</span>
                </div>
            </button>

            {/*  DROPDOWN ACCOUNT LIST  */}
            {open && (
                <div
                    className="
                      absolute z-20 mt-2 w-full rounded-xl
                      bg-[#0f172a]
                      border border-white/10
                      shadow-xl
                      overflow-hidden
                    "
                >
                    {accounts.map(acc => (
                        <button
                            key={acc.bankAccountId}
                            onClick={() => {
                                // Set selected account as active
                                setActiveAccount(acc);

                                // Close dropdown after selection
                                setOpen(false);
                            }}
                            className={`
                              w-full px-4 py-3
                              flex items-center gap-3
                              text-left
                              hover:bg-white/10 transition
                              ${
                                acc.bankAccountId === activeAccount.bankAccountId
                                  ? "bg-white/10" // Highlight active account
                                  : ""
                              }
                            `}
                        >
                            {/* Bank Logo */}
                            <BankLogo bankName={acc.bankName} />

                            {/* Bank Account Details */}
                            <div className="flex flex-col">
                                <p className="text-sm text-white font-medium">
                                    {acc.bankName}
                                </p>
                                <p className="text-xs text-gray-400">
                                    {acc.maskedAccountNo} • {acc.accountType}
                                </p>
                            </div>
                        </button>
                    ))}
                </div>
            )}
        </div>
    );
}
