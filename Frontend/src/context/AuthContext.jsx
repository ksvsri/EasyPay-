import { createContext, useContext, useEffect, useState } from "react";

// Utility to decode auth data from token
import { getAuthFromToken } from "../utils/authUtils";

// User & notification services
import { getUserProfile } from "../services/userService";
import {
  getUnreadNotificationCountAndLatestTwoNotifications
} from "../services/notificationServices";

/*
  AuthContext

  Global context to manage:
  - Authentication state
  - User profile data
  - Linked accounts
  - Notification summary
*/
const AuthContext = createContext();

/*
  Default empty auth state
  Used when user is not logged in or token is invalid
*/
const emptyAuth = {
  token: null,
  userId: null,
  role: null,
  userName: null,
  profileImagePath: null,
};

/*
  AuthProvider Component

  - Initializes auth state from token
  - Fetches and hydrates user profile
  - Manages global notification summary
  - Exposes helpers for refreshing auth & notifications
*/
export const AuthProvider = ({ children }) => {

  /* AUTH STATE */

  // Stores authentication & basic user info
  const [auth, setAuth] = useState(emptyAuth);

  /* NOTIFICATION STATE  */

  // Stores unread count and latest unread notifications
  const [notificationSummary, setNotificationSummary] = useState({
    unreadCount: 0,
    latestUnread: [],
  });

  /*
    Refresh unread notification count and preview list
  */
  const refreshNotificationSummary = async (userId) => {
    if (!userId) return;

    const res =
      await getUnreadNotificationCountAndLatestTwoNotifications(userId);

    if (res?.data) {
      setNotificationSummary(() => ({
        unreadCount: res.data.unreadNotificationCount,
        latestUnread: [...res.data.latestTwoUnread],
      }));
    }
  };

  /*  USER PROFILE HYDRATION  */

  /*
    Fetches full user profile safely and merges it into auth state
    - Called after login or page refresh
    - Ensures linked accounts are always updated
  */
  const hydrateUserProfile = async (userId) => {
    if (!userId) return null;

    try {
      const res = await getUserProfile(userId);
      if (!res?.data) return null;

      setAuth((prev) => ({
        ...prev,
        userName: res.data.userName,
        userEmail: res.data.userEmail,
        profileImagePath: res.data.profileImagePath || null,
        createdAt: res.data.createdAt,
        accounts: res.data.linkedAccounts || [],
      }));

      return res;
    } catch (e) {
      console.error("Failed to hydrate profile", e);
      return null;
    }
  };

  /*  INITIAL AUTH LOAD  */

  /*
    Runs once on app load:
    - Reads auth data from token
    - Hydrates user profile
    - Loads notification summary
  */
  useEffect(() => {
    const authData = getAuthFromToken();

    // No valid token → reset auth
    if (!authData || !authData.userId) {
      setAuth(emptyAuth);
      return;
    }

    setAuth(authData);
    hydrateUserProfile(authData.userId);
    refreshNotificationSummary(authData.userId);
  }, []);

  /*
    Refresh notification summary whenever userId becomes available
    (useful after login or auth refresh)
  */
  useEffect(() => {
    if (auth?.userId) {
      refreshNotificationSummary(auth.userId);
    }
  }, [auth?.userId]);

  /*  AUTH REFRESH */

  /*
    Re-reads auth data from token
    Useful after profile updates or token refresh
  */
  const refreshAuth = () => {
    const authData = getAuthFromToken();

    if (!authData || !authData.userId) {
      setAuth(emptyAuth);
      return emptyAuth;
    }

    setAuth(authData);
    return authData;
  };

  /*  CONTEXT PROVIDER  */

  return (
    <AuthContext.Provider
      value={{
        auth,                         // Auth & user data
        setAuth,                      // Setter for auth
        refreshAuth,                  // Refresh auth from token
        hydrateUserProfile,           // Fetch full profile
        notificationSummary,          // Notification counts & preview
        refreshNotificationSummary,   // Refresh notifications
      }}
    >
      {children}
    </AuthContext.Provider>
  );
};

/*

  Custom hook to consume AuthContext
*/
export const useAuth = () => useContext(AuthContext);
