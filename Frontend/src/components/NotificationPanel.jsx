import { useEffect, useRef } from "react";
import { createPortal } from "react-dom";
import {
    CheckCircle,
    XCircle,
    Bell,
    CreditCard,
    Repeat
} from "lucide-react";


/*
  Converts a date into a human-readable "time ago" format
  (e.g., Just now, 5 min ago, 2 hr ago)
*/
const timeAgo = (date) => {
    const diff = Math.floor((Date.now() - new Date(date)) / 1000);
    if (diff < 60) return "Just now";
    if (diff < 3600) return `${Math.floor(diff / 60)} min ago`;
    if (diff < 86400) return `${Math.floor(diff / 3600)} hr ago`;
    return `${Math.floor(diff / 86400)} day(s) ago`;
};

/*
  Maps notification type to icon and color
  Used to visually distinguish notification categories
*/
const typeMeta = (type) => {
    switch (type) {
        case "SUCCESS":
            return { icon: CheckCircle, color: "text-green-400" };
        case "FAILED":
            return { icon: XCircle, color: "text-red-400" };
        case "RECEIVED":
            return { icon: CreditCard, color: "text-blue-400" };
        case "COMPLETED":
            return { icon: Repeat, color: "text-violet-400" };
        default:
            return { icon: Bell, color: "text-gray-400" };
    }
};

/*
  NotificationPanel Component

  - Displays notifications in a floating panel anchored to the bell icon.
  - Supports preview mode (latest unread) and full mode (all notifications).
  - Handles outside click, scroll close, and notification dismissal.
*/
function NotificationPanel({
    notifications,   // List of notifications to display
    anchorRef,       // Reference element (bell icon) to position panel
    mode,            // PREVIEW or FULL
    onShowMore,      // Callback to load full notifications
    onDismissOne,    // Callback to dismiss a single notification
    onDismissAll,    // Callback to dismiss all notifications
    onClose,         // Callback to close the panel
}) {
    const panelRef = useRef(null);

    // If anchor is not available, do not render panel
    if (!anchorRef?.current) return null;

    // Get position of anchor element
    const rect = anchorRef.current.getBoundingClientRect();

    /*  SIDE EFFECTS  */

    // Close panel on page scroll
    useEffect(() => {
        const handlePageScroll = () => {
            onClose();
        };

        window.addEventListener("scroll", handlePageScroll, { passive: true });

        return () => {
            window.removeEventListener("scroll", handlePageScroll);
        };
    }, [onClose]);

    // Close panel when clicking outside
    useEffect(() => {
        const handleOutsideClick = (e) => {
            if (
                panelRef.current &&
                !panelRef.current.contains(e.target) &&
                anchorRef?.current &&
                !anchorRef.current.contains(e.target)
            ) {
                onClose();
            }
        };

        document.addEventListener("mousedown", handleOutsideClick);

        return () => {
            document.removeEventListener("mousedown", handleOutsideClick);
        };
    }, [onClose, anchorRef]);

    /*  UI */

    return createPortal(
        <div
            ref={panelRef}

            // Prevent scroll bubbling
            onWheel={(e) => e.stopPropagation()}
            onScroll={(e) => e.stopPropagation()}

            // Position panel relative to bell icon
            style={{
                top: rect.bottom + 12,
                right: window.innerWidth - rect.right,
            }}

            className="fixed w-[420px] max-h-[480px] overflow-y-auto overscroll-contain
               bg-[#0b1220]
               border border-white/10
               rounded-xl shadow-2xl
               z-[9999]"
        >

            {/*  HEADER  */}
            <div className="flex items-center justify-between px-4 py-3 border-b border-white/10">
                <h3 className="text-white font-semibold text-sm">
                    Notifications
                </h3>

                {/* Clear all button (only in FULL mode) */}
                {mode === "FULL" && notifications.length > 0 && (
                    <button
                        onClick={onDismissAll}
                        className="text-xs text-red-400 hover:text-red-300"
                    >
                        Clear all
                    </button>
                )}
            </div>

            {/*  BODY  */}
            {notifications.length === 0 ? (
                // Empty state
                <div className="p-6 text-center text-white/60 text-sm">
                    No notifications yet
                </div>
            ) : (
                <div className="divide-y divide-white/5">
                    {notifications.map((n) => {
                        const MetaIcon = typeMeta(n.type).icon;
                        const color = typeMeta(n.type).color;

                        return (
                            <div
                                key={n.notificationId}
                                className={`flex gap-3 px-4 py-3 text-sm transition
                                    ${!n.isRead
                                        ? "bg-white/5"
                                        : "opacity-70 hover:bg-white/5"}
                                `}
                            >
                                {/* Notification icon */}
                                <MetaIcon
                                    size={18}
                                    className={`${color} mt-1 shrink-0`}
                                />

                                {/* Notification content */}
                                <div className="flex-1">
                                    <p className="text-white leading-snug">
                                        {n.entityType}
                                    </p>

                                    {/* Meta info */}
                                    <div className="flex flex-wrap items-center gap-2 mt-1 text-xs text-white/50">
                                        <span className="uppercase tracking-wide">
                                            {n.message}
                                        </span>
                                        <span>•</span>
                                        <span>{timeAgo(n.createdAt)}</span>

                                        {/* Read timestamp */}
                                        {n.isRead && n.readAt && (
                                            <>
                                                <span>•</span>
                                                <span>
                                                    Read {timeAgo(n.readAt)}
                                                </span>
                                            </>
                                        )}
                                    </div>

                                    {/* Dismiss single notification */}
                                    <div className="flex justify-end mt-2">
                                        <button
                                            onClick={() =>
                                                onDismissOne(n.notificationId)
                                            }
                                            className="text-xs text-red-400 hover:text-red-300"
                                        >
                                            Dismiss
                                        </button>
                                    </div>
                                </div>
                            </div>
                        );
                    })}
                </div>
            )}

            {/*  FOOTER  */}
            {/* Show more button (only in PREVIEW mode) */}
            {mode === "PREVIEW" && notifications.length > 0 && (
                <button
                    onClick={onShowMore}
                    className="w-full py-2 text-sm
                               text-violet-400
                               hover:bg-white/5
                               border-t border-white/10"
                >
                    Show more
                </button>
            )}
        </div>,
        document.body
    );
}

export default NotificationPanel;
