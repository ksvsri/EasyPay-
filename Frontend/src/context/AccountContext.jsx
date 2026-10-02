import { createContext, useContext, useEffect, useState } from "react";
import { useAuth } from "./AuthContext";

/*
  AccountContext

  - Stores and manages the currently active bank account
    across the application.
  - Allows components to access and update the active account
    without prop drilling.
*/
const AccountContext = createContext();

/*
  AccountProvider Component

  - Wraps the app (or part of it) and provides
    activeAccount state and updater.
  - Automatically selects a valid account when auth data changes.
*/
export const AccountProvider = ({ children }) => {

  // Get authenticated user data (including linked accounts)
  const { auth } = useAuth();

  // Currently selected / active bank account
  const [activeAccount, setActiveAccount] = useState(null);

  /*
    Effect:
    - Runs whenever the user's accounts change.
    - Ensures activeAccount is always valid.
    - Automatically selects a default account if needed.
  */
  useEffect(() => {

    // If user has no accounts, reset active account
    if (!auth?.accounts?.length) {
      setActiveAccount(null);
      return;
    }

    // Check whether the current active account still exists
    const stillValid = auth.accounts.some(
      acc => acc.userLinkedAccountId === activeAccount?.userLinkedAccountId
    );

    /*
      Auto-select account only when:
      - There is no active account yet
      - OR the previously selected account is no longer valid
    */
    if (!activeAccount || !stillValid) {
      setActiveAccount(auth.accounts[0]);
    }

  }, [auth?.accounts]);

  return (
    // Provide active account state to all child components
    <AccountContext.Provider value={{ activeAccount, setActiveAccount }}>
      {children}
    </AccountContext.Provider>
  );
};

/*

  - Custom hook for consuming AccountContext.
  - Provides easy access to activeAccount and setActiveAccount.
*/
export const useAccount = () => useContext(AccountContext);
