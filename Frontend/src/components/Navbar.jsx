// Icons
import { Bell, LogOut, Menu, X } from "lucide-react";

// React hooks
import { useRef, useState } from "react";

// Routing
import { NavLink, useNavigate } from "react-router-dom";

// App logo
import logo from "../assets/images/logo/final logo.png";

// Notification panel UI
import NotificationPanel from "../components/NotificationPanel";

// Auth context
import { useAuth } from "../context/AuthContext";

// Notification services
import {
    getAllNotifications,
    markAllAsRead,
    markOneAsRead
} from "../services/notificationServices";

// Base URL for profile image
const baseUrl = import.meta.env.VITE_BASE_URL;

/*
  Navbar Component

  - Acts as the centralized navigation bar for the application.
  - Handles navigation, notifications, user profile access, and logout.
  - Supports both desktop and mobile layouts.
*/
function Navbar() {
    const navigate = useNavigate();

    /*  UI STATE  */

    // Controls mobile menu open/close
    const [open, setOpen] = useState(false);

    // Notification panel visibility
    const [showNotifications, setShowNotifications] = useState(false);

    // Notification display mode: PREVIEW (few) or FULL (all)
    const [mode, setMode] = useState("PREVIEW"); // PREVIEW | FULL

    // Stores full notification list when "Show more" is clicked
    const [allNotifications, setAllNotifications] = useState([]);

    // Reference for bell icon (used to anchor notification panel)
    const bellRef = useRef(null);

    /* AUTH & CONTEXT */

    const {
        auth,
        setAuth,
        notificationSummary,
        refreshNotificationSummary
    } = useAuth();

    const userName = auth?.userName;

    /*  DERIVED DATA  */

    // Preview notifications (latest unread only)
    const previewNotifications =
        notificationSummary.unreadCount === 0
            ? []
            : notificationSummary.latestUnread || [];

    // Unread notification count
    const unreadCount = notificationSummary.unreadCount;

    // Decide which notifications to show based on mode
    const notificationsToShow =
        unreadCount === 0
            ? []
            : mode === "FULL"
                ? allNotifications
                : previewNotifications;

    /*  HANDLERS  */

    // Clears auth state and redirects to login
    const handleLogout = () => {
        localStorage.clear();
        setAuth({
            token: null,
            userId: null,
            role: null,
            userName: null,
            profileImagePath: null,
        });
        navigate("/login");
    };

    // Navigation items shown in navbar
    const navItems = [
        { name: "Dashboard", path: "/dashboard" },
        { name: "Transactions", path: "/transactions" },
        { name: "Requests", path: "/requests" },
        { name: "Feedback", path: "/feedback" },
    ];

    // Navigate to user profile
    const handleProfile = () => {
        if (!auth?.userId) return;
        navigate("/userProfile");
    };

    // Toggle notification preview panel
    const handleBellClick = () => {
        if (!auth?.userId) return;
        setMode("PREVIEW");
        setShowNotifications(prev => !prev);
    };

    // Fetch all notifications and switch to FULL mode
    const handleShowMore = async () => {
        if (!auth?.userId) return;

        const res = await getAllNotifications(auth.userId);
        if (res?.data) {
            setAllNotifications(res.data);
            setMode("FULL");
        }
    };

    // Mark a single notification as read
    const handleDismissOne = async (notificationId) => {
        if (!auth?.userId) return;
        await markOneAsRead(auth.userId, notificationId);
        await refreshNotificationSummary(auth.userId);
    };

    // Mark all notifications as read
    const handleDismissAll = async () => {
        if (!auth?.userId) return;
        await markAllAsRead(auth.userId);
        await refreshNotificationSummary(auth.userId);
    };

    /*  UI  */

    return (
        <header className="w-full px-3 sm:px-6 pt-4">
            <div className="mx-auto max-w-7xl">

                {/*  MAIN NAVBAR */}
                <div
                    className="
                        min-h-16
                        flex items-center justify-between
                        rounded-2xl
                        bg-black/80 backdrop-blur-md
                        border border-white/15
                        px-4 sm:px-6 py-2
                        shadow-[0_10px_40px_rgba(15,23,42,0.15)]
                    "
                >
                    {/* App Logo */}
                    <div className="flex items-center">
                        <img
                            src={logo}
                            alt="EasyPay logo"
                            className="h-12 w-auto"
                        />
                    </div>

                    {/* DESKTOP NAVIGATION  */}
                    <nav className="hidden md:flex items-center gap-8">
                        {navItems.map((item) => (
                            <NavLink
                                key={item.path}
                                to={item.path}
                                className={({ isActive }) =>
                                    `relative text-sm transition
                                    ${isActive
                                        ? "text-white"
                                        : "text-white/70 hover:text-white"}`
                                }
                            >
                                {({ isActive }) => (
                                    <>
                                        {item.name}

                                        {/* Active underline & glow */}
                                        {isActive && (
                                            <>
                                                <span className="
                                                    absolute left-1/2 -bottom-3
                                                    h-3 w-10 -translate-x-1/2
                                                    rounded-full
                                                    bg-violet-400/20 blur-md
                                                " />
                                                <span className="
                                                    absolute left-1/2 -bottom-2
                                                    h-[2px] w-12 -translate-x-1/2
                                                    rounded-full bg-violet-400
                                                    shadow-[0_0_12px_rgba(167,139,250,0.9)]
                                                " />
                                            </>
                                        )}
                                    </>
                                )}
                            </NavLink>
                        ))}
                    </nav>

                    {/*  DESKTOP ACTIONS - */}
                    <div className="hidden md:flex items-center gap-3">

                        {/* Notification Bell */}
                        <div className="relative">
                            <div ref={bellRef} className="relative flex items-center h-8">
                                <button
                                    onClick={handleBellClick}
                                    className="relative flex items-center justify-center
                                        h-8 w-8 rounded-lg
                                        text-white/70 hover:text-white transition"
                                >
                                    <Bell size={18} />

                                    {/* Unread count badge */}
                                    {unreadCount > 0 && (
                                        <span className="absolute -top-1 -right-1
                                            bg-red-500 text-[10px]
                                            rounded-full px-1.5">
                                            {unreadCount}
                                        </span>
                                    )}
                                </button>
                            </div>

                            {/* Notification Panel */}
                            {showNotifications && (
                                <NotificationPanel
                                    notifications={notificationsToShow}
                                    anchorRef={bellRef}
                                    mode={mode}
                                    onShowMore={handleShowMore}
                                    onDismissOne={handleDismissOne}
                                    onDismissAll={handleDismissAll}
                                    onClose={() => {
                                        setShowNotifications(false);
                                        setMode("PREVIEW");
                                        setAllNotifications([]);
                                    }}
                                />
                            )}
                        </div>

                        {/* Username */}
                        {userName && (
                            <button
                                onClick={handleProfile}
                                className="cursor-pointer text-sm text-violet-400 hover:text-white transition"
                            >
                                {userName}
                            </button>
                        )}

                        {/* Profile image & logout */}
                        <div className="flex items-center gap-2">
                            <img
                                src={
                                    auth?.profileImagePath
                                        ? `${baseUrl}${auth.profileImagePath}`
                                        : "https://i.pravatar.cc/150"
                                }
                                alt="user"
                                onClick={handleProfile}
                                className="h-8 w-8 rounded-full object-cover cursor-pointer"
                            />

                            <button
                                onClick={handleLogout}
                                className="p-2 rounded-lg text-violet-400
                                           hover:text-violet-500 hover:bg-red-400/10 transition"
                                title="Logout"
                            >
                                <LogOut size={18} />
                            </button>
                        </div>
                    </div>

                    {/*  MOBILE MENU TOGGLE  */}
                    <button
                        onClick={() => setOpen(!open)}
                        className="md:hidden text-white"
                    >
                        {open ? <X size={22} /> : <Menu size={22} />}
                    </button>
                </div>

                {/*  MOBILE MENU  */}
                {open && (
                    <div
                        className="
                            md:hidden mt-3
                            rounded-2xl
                            bg-[#1e293b]/80 backdrop-blur-md
                            border border-white/15
                            shadow-lg
                            p-4
                        "
                    >
                        <nav className="flex flex-col gap-4">
                            {navItems.map((item) => (
                                <NavLink
                                    key={item.path}
                                    to={item.path}
                                    onClick={() => setOpen(false)}
                                    className={({ isActive }) =>
                                        `text-sm transition
                                        ${isActive
                                            ? "text-violet-400"
                                            : "text-white/80 hover:text-white"}`
                                    }
                                >
                                    {item.name}
                                </NavLink>
                            ))}

                            {/* Mobile notifications placeholder */}
                            <div className="pt-3 border-t border-white/10 flex items-center gap-3">
                                <Bell size={16} className="text-white/70" />
                                <span className="text-white/70 text-sm">
                                    Notifications
                                </span>
                            </div>

                            {/* Mobile logout */}
                            <button
                                onClick={handleLogout}
                                className="text-left text-sm text-white/70 hover:text-white pt-2"
                            >
                                Logout
                            </button>
                        </nav>
                    </div>
                )}
            </div>
        </header>
    );
}

export default Navbar;
