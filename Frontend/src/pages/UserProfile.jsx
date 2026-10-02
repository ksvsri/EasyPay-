import { KeyRound, Star, Trash2 } from "lucide-react";
import { useEffect, useState } from "react";
import { toast } from "react-toastify";
import AddBankModal from "../components/linkAccountComponents/AddBankModal";
import PinModal from "../components/linkAccountComponents/PinModal.jsx";
import { useAuth } from "../context/AuthContext";
import { getBankDataForLogos } from "../services/uiService";
import { makePrimaryAccount, removeLinkedAccount, resetUpiPin, updateUserDetails, uploadProfileImage } from "../services/userService";
import { isValidEmail, isValidName } from "../validationUtils/globalValidity.js";

const baseUrl = import.meta.env.VITE_BASE_URL
/*  Bank Logo Resolver  */

/*
 UserProfile Component
• Displays user profile details
• Allows updating name & email
• Handles profile image upload
• Shows linked bank accounts
• Supports:
   - Set primary account
   - Remove account
   - Reset UPI PIN
   - Add new bank account

*/
function UserProfile() {
    const [name, setName] = useState("");
    const [email, setEmail] = useState("");
    const [mobile, setMobile] = useState("");
    const [accounts, setAccounts] = useState([]);
    const [banks, setBanks] = useState([]);
    const [showAddBankModal, setShowAddBankModal] = useState(false);
    const [confirmAction, setConfirmAction] = useState(null);
    const [profileImage, setProfileImage] = useState(null);

    //reset pin
    const EMPTY_PIN = ["", "", "", "", "", ""];

    const [showResetPinModal, setShowResetPinModal] = useState(false);
    const [upiPin, setUpiPin] = useState(EMPTY_PIN);
    const [confirmPin, setConfirmPin] = useState(EMPTY_PIN);
    const [resetAccountId, setResetAccountId] = useState(null);


    //Error for both email and name when user try to change
    const [errors, setErrors] = useState({
        name: "",
        email: "",
    });
    const handleAccountLinked = async () => {
        await handleProfile();
        await refreshNotificationSummary(userId);

    };

    //Handle name change
    const handleNameChange = (e) => {
        const value = e.target.value;
        setName(value);

        setErrors((prev) => ({
            ...prev,
            name: isValidName(value) ? "" : "Enter a valid name",
        }));
    };

    //Handle email change
    const handleEmailChange = (e) => {
        const value = e.target.value;
        setEmail(value);

        setErrors((prev) => ({
            ...prev,
            email: isValidEmail(value) ? "" : "Enter a valid email address",
        }));
    };

    //Checking error is there or not
    const hasErrors = Boolean(errors.name || errors.email);

    // Setting initial profile to check whether user is trying to update the profile or not by comparing the previous one and current field values.
    const [initialProfile, setInitialProfile] = useState({
        name: "",
        email: "",
    });

    //Using context
    const { auth, hydrateUserProfile, refreshNotificationSummary } = useAuth();
    const userId = auth?.userId;

    /* Getting user details */
    const handleProfile = async () => {

        //Calling service to get the profile
        if (!userId) return;
        const res = await hydrateUserProfile(userId);
        if (res?.data) {
            setName(res.data.userName);
            setEmail(res.data.userEmail);
            setMobile(res.data.mobileNo);
            setAccounts(res.data.linkedAccounts || []);
            setProfileImage(res.data.profileImagePath || null);

            await refreshNotificationSummary(userId)
            //Setting the initial values with the profile data
            setInitialProfile({
                name: res.data.userName,
                email: res.data.userEmail,
            });
        }
    };

    // Checking there is change with the current and previous values
    const hasChanges =
        name !== initialProfile.name ||
        email !== initialProfile.email;

    //Gets the user details if there is user id
    useEffect(() => {
        if (userId) handleProfile();
    }, [userId]);

    // Load the bank accounts if any linked
    const handleLoadBanks = async () => {
        const res = await getBankDataForLogos();
        if (res?.data) {
            setBanks(res.data);
        } else {
            toast.error(res?.error || "Failed to load banks");
        }
    };

    //when click on add bank account user will choose one bank that bank id will be stored
    const handleSelectBank = (bank) => {
        console.log("Selected bank:", bank);
        setShowAddBankModal(false);
        // next step → link account / KYC
    };

    // Allows user to update the details when he change the name or email
    const updateProfile = async () => {
        const dto = {
            userId: auth?.userId,
            userName: name,
            userEmail: email,
        };

        //Calling service to update the details
        const res = await updateUserDetails(dto);

        if (res?.data) {
            toast.success("Profile updated successfully");
            setInitialProfile({ name, email });
            setErrors({ name: "", email: "" });
            await hydrateUserProfile(userId);
        }
    };

    // Allows to set the primary  for the selected account
    const handleMakePrimary = async (bankAccountId) => {

        setConfirmAction({
            type: "PRIMARY",
            bankAccountId,
        });



    };

    //Allows to remove the selected account
    const handleRemoveAccount = async (bankAccountId) => {
        setConfirmAction({
            type: "REMOVE",
            bankAccountId,
        });

    };

    //Reset pin
    const handleResetPin = async () => {
        const dto = { userId: userId, bankAccountId: resetAccountId, newPin: upiPin.join("") }
        console.log(dto)
        const res = await resetUpiPin(dto);
        console.log(res)
        if (res.data) {
            toast.success("UPI PIN reset successfully");

        }

        else {
            toast.error(res?.error || "Failed to reset UPI PIN");
        }
        setUpiPin(EMPTY_PIN);
        setConfirmPin(EMPTY_PIN);


    };



    //UI
    return (
        <div className="flex-1 w-full px-6 py-6 space-y-8 text-white bg-gradient-to-br from-[#0b1220] via-[#0f172a] to-[#020617]">

            {/*  Profile Header  */}
            <div className="bg-white/10 backdrop-blur-xl border border-white/10
                rounded-2xl p-6
                flex flex-col sm:flex-row
                items-center sm:items-start
                gap-4 sm:gap-6">
                <div className="relative w-24 h-24">
                    {/* Hidden file input */}
                    <input
                        type="file"
                        accept="image/*"
                        id="profileImageInput"
                        hidden
                        onChange={async (e) => {
                            const file = e.target.files[0];
                            if (!file) return;

                            const formData = new FormData();
                            formData.append("image", file);
                            formData.append("userId", auth?.userId);

                            const res = await uploadProfileImage(formData);
                            await refreshNotificationSummary(auth.userId);

                            if (res?.data) {
                                toast.success("Profile image updated");
                                await handleProfile();
                            } else {
                                toast.error(res?.error || "Upload failed");
                            }

                            e.target.value = "";
                        }}


                    />

                    {/* Clickable Image */}
                    <label htmlFor="profileImageInput" className="cursor-pointer">
                        <img
                            src={
                                profileImage
                                    ? `${baseUrl}${profileImage}`
                                    : "https://i.pravatar.cc/150"
                            }
                            alt="profile"
                            className="w-24 h-24 rounded-full object-cover border border-white/20"
                        />

                    </label>

                    {/* Camera Icon */}
                    <label
                        htmlFor="profileImageInput"
                        className="
            absolute bottom-0 right-0
            w-8 h-8
            rounded-full
            bg-violet-500
            flex items-center justify-center
            text-white
            border-2 border-[#0f172a]
            hover:bg-violet-600
            transition
            cursor-pointer
        "
                    >
                        📷
                    </label>
                </div>


                <div className="text-center sm:text-left break-all">
                    <h2 className="text-xl font-semibold">{name}</h2>
                    <p className="text-white/60 text-sm">{email}</p>
                </div>
            </div>

            {/*  Update Profile  */}
            <div className="bg-white/10 backdrop-blur-xl border border-white/10 rounded-2xl p-6">
                <h3 className="text-lg font-semibold mb-4">Update Profile</h3>

                <div className="grid grid-cols-1 md:grid-cols-2 gap-4">

                    {/* Name */}
                    <div>
                        <input
                            className="bg-white/10 border border-white/10 rounded-xl px-4 py-3 outline-none w-full"
                            value={name}
                            onChange={handleNameChange}
                            placeholder="Name"
                        />
                        <p className="text-red-400 text-xs h-4">
                            {errors.name || ""}
                        </p>
                    </div>

                    {/* Email */}
                    <div>
                        <input
                            className="bg-white/10 border border-white/10 rounded-xl px-4 py-3 outline-none w-full"
                            value={email}
                            onChange={handleEmailChange}
                            placeholder="Email"
                        />
                        <p className="text-red-400 text-xs h-4">
                            {errors.email || ""}
                        </p>
                    </div>

                    {/* Mobile  */}
                    <div>
                        <input
                            value={mobile}
                            disabled
                            className="bg-white/10 border border-white/10 rounded-xl px-4 py-3 outline-none w-full cursor-not-allowed"
                            placeholder="Mobile"
                        />
                        <p className="text-red-400 text-xs h-4"></p>
                    </div>

                </div>


                <button
                    disabled={!hasChanges || hasErrors}
                    className={`mt-4 px-6 py-2 rounded-xl transition
        ${!hasChanges || hasErrors
                            ? "bg-white/20 cursor-not-allowed opacity-50"
                            : "bg-violet-500 hover:bg-violet-600"
                        }
    `}
                    onClick={updateProfile}
                >
                    Save Changes
                </button>


            </div>

            {/*  Linked Accounts  */}
            <div className="bg-white/10 backdrop-blur-xl border border-white/10 rounded-2xl p-6">
                <h3 className="text-lg font-semibold mb-4">Linked Bank Accounts</h3>

                <div className="space-y-3">
                    {accounts.map((acc) => (
                        <div
                            key={acc.bankAccountId}
                            className="flex justify-between items-center bg-white/5 border border-white/10 rounded-xl p-4"
                        >
                            {/* LEFT INFO */}
                            <div>
                                <p className="font-medium">{acc.bankName}</p>

                                <p className="text-sm text-white/60">
                                    {acc.maskedAccountNo} • {acc.accountType}
                                </p>

                                <p className="text-xs text-white/40">{acc.upiId}</p>

                                {acc.primary && (
                                    <span className="mt-1 inline-block text-xs px-2 py-0.5 rounded-full bg-green-500/20 text-green-400">
                                        Primary
                                    </span>
                                )}
                            </div>

                            {/* RIGHT ACTIONS */}

                            <div className="flex flex-col items-end gap-2 min-w-[140px]">
                                {/* Set Primary */}
                                {!acc.primary && (
                                    <button
                                        onClick={() => handleMakePrimary(acc.bankAccountId)}
                                        className="w-full px-3 py-1.5 text-xs font-medium rounded-lg bg-violet-500/20 text-violet-300 border border-violet-500/40 hover:bg-violet-500/30 transition flex items-center justify-center gap-2"
                                    >
                                        <Star size={14} />
                                        Set Primary
                                    </button>
                                )}

                                {/* Reset UPI PIN */}
                                <button
                                    onClick={() => {
                                        setResetAccountId(acc.bankAccountId);
                                        setConfirmAction({ type: "RESET_PIN" });
                                    }}
                                    className=" w-full px-3 py-1.5 text-xs font-medium rounded-lg bg-blue-500/10 text-blue-300 border border-blue-500/30 hover:bg-blue-500/20 transition flex items-center justify-center gap-2"
                                >
                                    <KeyRound size={14} />
                                    Reset UPI PIN
                                </button>

                                {/* Remove */}
                                {!acc.primary && (
                                <button
                                    onClick={() => handleRemoveAccount(acc.bankAccountId)}
                                    className=" w-full px-3 py-1.5 text-xs font-medium rounded-lg bg-red-500/20 text-red-300 border border-red-500/40 hover:bg-red-500/30 transition flex items-center justify-center gap-2"
                                >
                                    <Trash2 size={14} />
                                    Remove
                                </button>)}
                            </div>
                        </div>
                    ))}
                </div>

                <button
                    className="mt-4 px-6 py-2 rounded-xl border border-violet-400 text-violet-400 hover:bg-violet-400 hover:text-black transition"
                    onClick={async () => {
                        await handleLoadBanks();
                        setShowAddBankModal(true);
                    }}
                >
                    + Add New Account
                </button>
            </div>

            {/*  MODAL (CORRECT PLACEMENT)  */}
            {showAddBankModal && (
                <AddBankModal
                    banks={banks}
                    onClose={() => setShowAddBankModal(false)}
                    onLinked={handleAccountLinked}
                    onSelect={handleSelectBank}

                />
            )}
            {confirmAction && (
                <div className="fixed inset-0 z-[90] flex items-center justify-center">
                    {/* Backdrop */}
                    <div
                        className="absolute inset-0 bg-black/60"
                        onClick={() => setConfirmAction(null)}
                    />

                    {/* Card */}
                    <div
                        className="relative bg-[#0f172a] border border-white/10 rounded-2xl
                 p-6 w-full max-w-sm text-white"
                        onClick={(e) => e.stopPropagation()}
                    >
                        {/* Confirmation message header */}
                        <div className="flex items-center justify-between mb-4">
                            <h4 className="text-lg font-semibold">
                                {confirmAction.type === "REMOVE" && "Remove Account"}
                                {confirmAction.type === "PRIMARY" && "Set Primary Account"}
                                {confirmAction.type === "RESET_PIN" && "Reset UPI PIN"}
                            </h4>


                        </div>

                        {/* Message */}
                        <p className="text-sm text-white/70 mb-6">
                            {confirmAction.type === "REMOVE" &&
                                "Are you sure you want to remove this linked bank account?"}

                            {confirmAction.type === "PRIMARY" &&
                                "This account will be set as your primary account."}

                            {confirmAction.type === "RESET_PIN" &&
                                "You are about to reset the UPI PIN for this account."}
                        </p>


                        {/* Actions */}
                        <div className="flex gap-3">
                            <button
                                className="flex-1 h-10 rounded-xl bg-white/10 hover:bg-white/20 transition"
                                onClick={() => setConfirmAction(null)}
                            >
                                Cancel
                            </button>

                            <button
                                className={`flex-1 h-10 rounded-xl font-medium transition
            ${confirmAction.type === "REMOVE"
                                        ? "bg-red-500/30 text-red-300 hover:bg-red-500/40"
                                        : "bg-violet-500 text-white hover:bg-violet-600"
                                    }
          `}
                                onClick={async () => {
                                    try {
                                        let res;

                                        if (confirmAction.type === "REMOVE") {
                                            res = await removeLinkedAccount({
                                                userId,
                                                accountId: confirmAction.bankAccountId,
                                            });

                                            if (!res?.data) throw new Error(res?.error);
                                            toast.success("Account removed");

                                            await refreshNotificationSummary(userId);
                                            setConfirmAction(null);
                                            await handleProfile();
                                        }

                                        else if (confirmAction.type === "PRIMARY") {
                                            res = await makePrimaryAccount({
                                                userId,
                                                newPrimaryAccountId: confirmAction.bankAccountId,
                                            });

                                            if (!res?.data) throw new Error(res?.error);
                                            toast.success("Primary account updated");

                                            await refreshNotificationSummary(userId);
                                            setConfirmAction(null);
                                            await handleProfile();
                                        }

                                        else if (confirmAction.type === "RESET_PIN") {
                                            setConfirmAction(null);
                                            setShowResetPinModal(true);
                                        }

                                    } catch (err) {
                                        toast.error(err.message || "Something went wrong");
                                    }
                                }}


                            >
                                {confirmAction.type === "REMOVE" ? "Remove" : "Confirm"}
                            </button>
                        </div>

                        {/* Confirmation message footer */}
                        <div className="mt-4 text-center text-xs text-white/40">
                            EasyPay • Secure Confirmation
                        </div>
                    </div>
                </div>
            )}
            <PinModal
                open={showResetPinModal}
                title="Reset UPI PIN"
                confirmText="Confirm & Reset"
                upiPin={upiPin}
                confirmPin={confirmPin}
                setUpiPin={setUpiPin}
                setConfirmPin={setConfirmPin}
                onBack={() => setShowResetPinModal(false)}
                onConfirm={handleResetPin}
            />
        </div>
    );
}

export default UserProfile;
