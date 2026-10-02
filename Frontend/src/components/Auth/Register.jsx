import { useState, useRef, useEffect } from "react";
import { toast } from "react-toastify";
import { generateRegisterOtp } from "../../services/authServices";
import { register } from "../../services/userService";
import { isValidMobile, isValidEmail, isValidName } from "../../validationUtils/globalValidity"
import { useNavigate } from "react-router-dom";
/*
 Register Component
• Handles user registration
• Collects user details (name, mobile, email)
• Sets 4-digit app lock PIN
• Generates & verifies OTP
• Registers user after OTP verification

*/
function Register() {

    const PIN_LENGTH = 4;
    const OTP_LENGTH = 6;
    const [sendingOtp, setSendingOtp] = useState(false);
    const [otpSent, setOtpSent] = useState(false);
    const [pin, setPin] = useState(Array(PIN_LENGTH).fill(""));
    const [otp, setOtp] = useState(Array(OTP_LENGTH).fill(""));
    const [timer, setTimer] = useState(30);
    const [nameError, setNameError] = useState("");
    const [mobileError, setMobileError] = useState("");
    const [emailError, setEmailError] = useState("");
    const [pinError, setPinError] = useState("");
    const navigate = useNavigate()

    const nameRef = useRef();
    const mobileRef = useRef();
    const emailRef = useRef();
    const referralRef = useRef();

    const pinRefs = useRef([]);
    const otpRefs = useRef([]);

    const isOtpComplete = otp.every((d) => d !== "");


    //Focus first empty box in the pin
    const focusFirstEmptyPin = () => {
        const idx = pin.findIndex((v) => v === "");
        if (idx !== -1) pinRefs.current[idx]?.focus();
    };

    //Handles if there is change in the pin and allows only to enter the digits.
    const handlePinChange = (e, index) => {
        if (!/^[0-9]?$/.test(e.target.value)) return;

        const newPin = [...pin];
        newPin[index] = e.target.value;
        setPin(newPin);

        if (e.target.value && index < pin.length - 1) {
            requestAnimationFrame(() =>
                pinRefs.current[index + 1]?.focus()
            );
        }
    };

    //Validate the pin entered by the user.
    const validatePin = () => {
        const pinValue = pin.join("");

        if (!pinValue) {
            setPinError("App lock PIN is required");
            return false;
        }

        //Length of the pin should be 4
        if (pinValue.length !== 4) {
            setPinError("App lock must be 4 digits");
            return false;
        }

        setPinError("");
        return true;
    };


    //Handles if user press backspace to remove the current pin box
    const handlePinBackspace = (e, index) => {
        if (e.key !== "Backspace") return;
        e.preventDefault();

        const newPin = [...pin];

        //if current box has value in it remove and focus previous
        if (newPin[index]) {
            newPin[index] = "";

            //If current has no value in it. remove previous and focus.
        } else if (index > 0) {
            newPin[index - 1] = "";
            requestAnimationFrame(() =>
                pinRefs.current[index - 1]?.focus()
            );
        }
        setPin(newPin);
    };

    /* ---------------- OTP LOGIC ---------------- */

    //This is to focus the first empty otp field box
    const focusFirstEmptyOtp = () => {
        const idx = otp.findIndex((v) => v === "");
        if (idx !== -1) otpRefs.current[idx]?.focus();
    };

    //handles if there is change in the otp field.
    const handleOtpChange = (e, index) => {
        if (!/^[0-9]?$/.test(e.target.value)) return;

        const newOtp = [...otp];
        newOtp[index] = e.target.value;
        setOtp(newOtp);

        if (e.target.value && index < otp.length - 1) {
            requestAnimationFrame(() =>
                otpRefs.current[index + 1]?.focus()
            );
        }
    };

    //Hadnles when user click on backspace
    const handleOtpBackspace = (e, index) => {
        if (e.key !== "Backspace") return;
        e.preventDefault();

        const newOtp = [...otp];

        //current box has value, remove and focus previous.
        if (newOtp[index]) {
            newOtp[index] = "";

            //Current has no value remove previous and focus.
        } else if (index > 0) {
            newOtp[index - 1] = "";
            requestAnimationFrame(() =>
                otpRefs.current[index - 1]?.focus()
            );
        }
        setOtp(newOtp);
    };

    // Handles pasting the value from the clipboard.
    const handleOtpPaste = (e) => {
        e.preventDefault();
        const pasted = e.clipboardData.getData("text").trim();
        if (!/^[0-9]{6}$/.test(pasted)) return;

        setOtp(pasted.split(""));
        requestAnimationFrame(() => otpRefs.current[5]?.focus());
    };


    // OTP timer
    useEffect(() => {
        if (!otpSent) return;

        if (timer === 0) return;

        const interval = setInterval(() => {
            setTimer((t) => t - 1);
        }, 1000);

        return () => clearInterval(interval);
    }, [otpSent, timer]);




    // Callinf service to send the OTP .
    const handleSendOtp = async () => {
        if (sendingOtp) return;

        handleNameChange({ target: nameRef.current });
        handleMobileChange({ target: mobileRef.current });
        handleEmailChange({ target: emailRef.current });

        //If there is any error dont send OTP.
        if (!validatePin()) return;
        if (nameError || mobileError || emailError || pinError) return;

        //If name or email or mobile any field not filled then dont send OTP.
        if (
            !nameRef.current.value ||
            !mobileRef.current.value ||
            !emailRef.current.value
        ) return;

        try {
            setSendingOtp(true);

            //Calling service to generate the OTP
            const res = await generateRegisterOtp(mobileRef.current.value);

            //Success response
            if (res.data) {
                toast.success(res.data);
                setOtpSent(true);
                setOtp(Array(OTP_LENGTH).fill(""));
                setTimer(30);
                //Once otp send it focuses the first empty field.
                requestAnimationFrame(() => otpRefs.current[0]?.focus());
            } else {
                toast.error(res.error);
            }
        } finally {
            setSendingOtp(false);
        }
    };



    // Allows user to register
    const handleRegister = async () => {

        //If pin not validated return
        if (!validatePin()) return;

        //If any error return
        if (nameError || mobileError || emailError || pinError) return;
        if (!isOtpComplete) return;

        //Payload to send the data(dto)
        const payload = {
            userName: nameRef.current.value,
            userMobileNumber: mobileRef.current.value,
            userEmail: emailRef.current.value,
            referralCode: referralRef.current.value || null,
            appLock: pin.join(""),
            receivedOtp: otp.join("")
        };

        //calling service method to register the user.
        const res = await register(payload);
        if (res.data) {
            toast.success(res.data);
            navigate("/login", {
                state: { mobile: mobileRef.current.value },
            });

        }
        else {
            toast.error(res.error);
        }
    };

    //Handles if there is change in the name field
    const handleNameChange = (e) => {
        const value = e.target.value.trim();

        if (!value) {
            setNameError("");
            return;
        }

        if (!isValidName(value)) {
            setNameError("Enter a valid full name");
        } else {
            setNameError("");
        }
    };

    //Handles if there is change in the mobile field.
    const handleMobileChange = (e) => {
        const value = e.target.value.replace(/\D/g, "");
        e.target.value = value;

        if (!value) {
            setMobileError("Mobile number is required");
        } else if (!isValidMobile(value)) {
            setMobileError("Enter a valid 10-digit mobile number");
        } else {
            setMobileError("");
        }
    };

    //Handles if there is change in the email
    const handleEmailChange = (e) => {
        const value = e.target.value.trim();

        if (!value) {
            setEmailError("");
            return;
        }

        if (!isValidEmail(value)) {
            setEmailError("Enter a valid email address");
        } else {
            setEmailError("");
        }
    };



    //UI

    return (
        // centers the card on screen
        <div className="w-full flex items-center justify-center">
            <div className="rounded-3xl p-6 bg-white/30 backdrop-blur-xl shadow-xl">
                <div
                    className="w-[92vw] max-w-[320px] bg-[#1E2230]/95 rounded-2xl p-4 sm:p-6 text-white"
                >


                    <h2 className="text-lg font-semibold mb-4">
                        Create your account
                    </h2>

                    {/* Full Name */}
                    <div className="mb-3">
                        <label className="text-sm text-white/70">Full Name</label>
                        <input
                            ref={nameRef}
                            onChange={handleNameChange}
                            onBlur={handleNameChange}
                            className="w-full px-3 py-1.5 rounded-lg bg-white/10 border border-white/10"
                        />

                        {nameError && (
                            <p className="mt-1 text-xs text-red-400 min-h-[16px]">
                                {nameError || ""}
                            </p>
                        )}
                    </div>

                    {/* Mobile */}
                    <div className="mb-3">
                        <label className="text-sm text-white/70">Mobile Number</label>
                        <input
                            ref={mobileRef}
                            type="tel"
                            maxLength={10}
                            onChange={handleMobileChange}
                            onBlur={handleMobileChange}
                            className="w-full px-3 py-1.5 rounded-lg bg-white/10 border border-white/10"
                        />

                        {mobileError && (
                            <p className="mt-1 text-xs text-red-400 min-h-[16px]">
                                {mobileError || ""}
                            </p>
                        )}
                    </div>

                    {/* Email */}
                    <div className="mb-3">
                        <label className="text-sm text-white/70">Email</label>
                        <input
                            ref={emailRef}
                            onChange={handleEmailChange}
                            onBlur={handleEmailChange}
                            className="w-full px-3 py-1.5 rounded-lg bg-white/10 border border-white/10"
                        />

                        {emailError && (
                            <p className="mt-1 text-xs text-red-400 min-h-[16px]">
                                {emailError || ""}
                            </p>
                        )}
                    </div>

                    {/* Referral */}
                    <div className="mb-4">
                        <label className="text-sm text-white/70">Referral Code (optional)</label>
                        <input ref={referralRef}
                            className="w-full px-3 py-1.5 rounded-lg bg-white/10 border border-white/10 focus:ring-2 focus:ring-violet-500 outline-none" />
                    </div>

                    {/*  4 digits PIN */}
                    <div className="mb-4">
                        <label className="text-sm text-white/70">
                            4-Digit App Lock
                        </label>
                        {/* PIN input boxes */}
                        <div className="flex gap-2 mt-2">
                            {pin.map((v, i) => (
                                <input
                                    key={i}
                                    ref={(el) => (pinRefs.current[i] = el)}
                                    value={v}
                                    maxLength={1}
                                    type="password"
                                    autoComplete="new-password"
                                    inputMode="numeric"
                                    onFocus={focusFirstEmptyPin}
                                    onChange={(e) => handlePinChange(e, i)}
                                    onKeyDown={(e) => handlePinBackspace(e, i)}
                                    className={`
                                    w-10 h-10 text-center rounded-lg
                                    bg-white/10 border
                                    ${pinError ? "border-red-400" : "border-white/10"}
                                    focus:ring-2
                                    ${pinError ? "focus:ring-red-400" : "focus:ring-violet-500"}
                                    outline-none
                                    `}
                                />
                            ))}
                        </div>
                        {/* PIN validation error */}
                        {pinError && (
                            <p className="mt-1 text-xs text-red-400 min-h-[16px]">
                                {pinError || ""}
                            </p>
                        )}
                    </div>

                    {/* ---------- Before OTP sent ---------- */}

                    {!otpSent && (
                        <button
                            type="button"
                            disabled={
                                sendingOtp || nameError || mobileError || emailError || pinError
                            }
                            onClick={handleSendOtp}
                            className={`w-full py-2 rounded-full bg-gradient-to-r from-violet-500 to-indigo-500
    ${sendingOtp || nameError || mobileError || emailError || pinError
                                    ? "opacity-50 cursor-not-allowed"
                                    : ""
                                }`}
                        >
                            {sendingOtp ? "Sending OTP..." : "Send OTP"}
                        </button>


                    )}

                    {/* ---------- After OTP sent ---------- */}
                    {otpSent && (
                        <>
                            <div className="flex gap-2 my-4" onPaste={handleOtpPaste}>
                                {otp.map((v, i) => (
                                    <input key={i}
                                        ref={(el) => (otpRefs.current[i] = el)}
                                        value={v}
                                        maxLength={1}
                                        inputMode="numeric"
                                        onFocus={focusFirstEmptyOtp}
                                        onChange={(e) => handleOtpChange(e, i)}
                                        onKeyDown={(e) => handleOtpBackspace(e, i)}
                                        className="w-10 h-10 text-center rounded-lg bg-white/10 border border-white/10 focus:ring-2 focus:ring-violet-500 outline-none" />
                                ))}

                            </div>
                            {/* OTP Error */}
                            {!isOtpComplete && otpSent && (
                                <p className="mt-1 text-xs text-red-400 min-h-[16px]">
                                    Please enter the complete 6-digit OTP
                                </p>
                            )}
                            {/* Verify & Register button */}
                            <button type="button"
                                disabled={!isOtpComplete}
                                onClick={handleRegister}
                                className={`w-full py-2 rounded-full bg-gradient-to-r from-violet-500 to-indigo-500
                  ${!isOtpComplete ? "opacity-50 cursor-not-allowed" : ""}`}>
                                Verify & Create Account
                            </button>
                                    {/* Resend OTP  */}
                            <button
                                type="button"
                                disabled={timer > 0 || sendingOtp}
                                onClick={handleSendOtp}
                                className="mt-2 w-full text-xs text-white/70"
                            >
                                {sendingOtp
                                    ? "Sending OTP..."
                                    : timer > 0
                                        ? `Resend OTP in ${timer}s`
                                        : "Resend OTP"}
                            </button>

                        </>
                    )}
                </div>
            </div>
        </div>
    );
}

export default Register;
