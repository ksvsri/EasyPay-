import { useEffect, useRef, useState } from "react";
import { useAuth } from "../../context/AuthContext";
import { isValidMobile } from "../../validationUtils/globalValidity.js";

import { useLocation, useNavigate } from "react-router-dom";
import { toast } from "react-toastify";
import { generateLoginOtp } from "../../services/authServices";
import { login } from "../../services/userService.js";
/*
 Login Component
- Handles OTP-based login using mobile number
- Generates OTP
- Verifies OTP
- Authenticates user and stores token
*/
function Login() {

    // Constants
    const [otpSent, setOtpSent] = useState(false);
    const OTP_LENGTH = 6;
    const [otp, setOtp] = useState(Array(OTP_LENGTH).fill(""));
    const [timer, setTimer] = useState(30);
    const otpRefs = useRef([]);
    const [mobileError, setMobileError] = useState("");
    const [mobile, setMobile] = useState("");
    const navigate = useNavigate()
    const location = useLocation();
    const { refreshAuth, hydrateUserProfile } = useAuth();


    // Check whether OTP fields  or not
    const isOtpComplete = otp.every((digit) => digit !== "");

    // Focus first empty OTP box
    const focusFirstEmptyOtp = () => {
        const firstEmptyIndex = otp.findIndex((v) => v === "");
        if (firstEmptyIndex !== -1) {
            otpRefs.current[firstEmptyIndex]?.focus();
        }
    };

    // Check is there any change in the mobile field.
    const handleMobileChange = (e) => {
        const value = e.target.value.replace(/\D/g, "");

        //set mobile if there is any change.
        setMobile(value);
        if (!value) return setMobileError("Mobile number is required");

        // Check wether mobile no is following the requirement or not.
        if (!isValidMobile(value)) return setMobileError("Enter a valid 10-digit mobile number");

        setMobileError("");
    };



    // Handle OTP input
    const handleOtpChange = (e, index) => {
        const value = e.target.value;

        //Box should contain only digits
        if (!/^[0-9]?$/.test(value)) return;

        //Adding box value to the list at that index.
        const newOtp = [...otp];
        newOtp[index] = value;
        setOtp(newOtp);

        //Auto focus next if current is filled
        if (value && index < otp.length - 1) {
            requestAnimationFrame(() => {
                otpRefs.current[index + 1]?.focus();
            });
        }
    };

    // Handle backspace moves left from the right
    const handleKeyDown = (e, index) => {

        //Ignore all the keys except backspace
        if (e.key !== "Backspace") return;

        e.preventDefault();
        const newOtp = [...otp];


        // If current box has a value remove it and move to left This is mainly at last box
        if (newOtp[index]) {
            newOtp[index] = "";
            setOtp(newOtp);

            if (index > 0) {
                requestAnimationFrame(() => {
                    otpRefs.current[index - 1]?.focus();
                });
            }

            // If current has no value but focusing then
        } else if (index > 0) {
            //remove previous box value and focus that
            newOtp[index - 1] = "";
            setOtp(newOtp);
            requestAnimationFrame(() => {
                otpRefs.current[index - 1]?.focus();
            });
        }
    };

    // Allows to paste the copied OTP
    const handlePaste = (e) => {

        // Preventing the default nature.
        e.preventDefault();

        //Allows to copy the data from the clipboard.
        const pasted = e.clipboardData.getData("text").trim();

        //Validate the pasted otp
        if (!new RegExp(`^[0-9]{${OTP_LENGTH}}$`).test(pasted)) return;

        //Setting OTP value fromt the pasted
        setOtp(pasted.split(""));

        //Focus last OTP box after fill
        requestAnimationFrame(() => {
            otpRefs.current[OTP_LENGTH - 1]?.focus();
        });
    };

    //Reset OTP when mobile number changes
    useEffect(() => {
        setOtpSent(false);
        setOtp(Array(OTP_LENGTH).fill(""));
    }, [mobile]);

    // To fill the field, when login comes from another page. Say User registered he/she need to login then from register to login it will naviagte then user dont need to enter the mobile Number again.
    useEffect(() => {
        if (location.state?.mobile) {
            setMobile(location.state.mobile);
        }
    }, [location.state]);


    // Timer to  resend the OTP
    useEffect(() => {

        if (!otpSent || timer === 0) return;

        const interval = setInterval(() => {
            setTimer((t) => t - 1);
        }, 1000);

        return () => clearInterval(interval);

        // When there is change in the timer or otpsent state changed
    }, [otpSent, timer]);


    // Allows user to login by passing the mobileNo and entered OTP.
    const handleLogin = async () => {
        if (!isOtpComplete) return;

        const loginjson = {
            mobileNo: mobile,
            receivedOtp: otp.join("")
        };

        //calling service method to validate the OTP
        const res = await login(loginjson);

        if (res.data) {


            //Storing token in the loacal storage
            localStorage.setItem("token", res.data);

            const authData = refreshAuth();
            await hydrateUserProfile(authData?.userId);
            toast.success("Welcome " + authData.userName);

            navigate("/dashboard");
        }

        if (res.error) {
            toast.error(res.error);
            setOtp(Array(OTP_LENGTH).fill(""));
            focusFirstEmptyOtp();
        }
    };


    //Allows to send OTP
    const handleSendOtp = async () => {

        //Check mobile is valid or not
        if (!isValidMobile(mobile)) {
            setMobileError("Enter a valid 10-digit mobile number");
            return;
        }
        setMobileError("");
        // Calling service method to generate OTP.
        const res = await generateLoginOtp(mobile);

        // Display OTP if success
        if (res.data) {
            toast.success(res.data);
            setOtpSent(true);
            setOtp(Array(OTP_LENGTH).fill(""));
            setTimer(30);
        }
        // Error if not able to generate.
        else {
            toast.error(res.error);
        }
    };



    return (
        <div className=" w-full flex items-center justify-center">
            <div className="rounded-3xl p-4 sm:p-6 bg-white/30 backdrop-blur-xl shadow-xl">
                <div
                    className="
                    w-[92vw]
                    max-w-[280px]
                    sm:max-w-[340px]
                    md:max-w-[400px]
                    bg-[#1E2230]/95
                    rounded-2xl
                    p-3 sm:p-6
                    text-white
                "
                >
                    <h2 className="text-base sm:text-lg font-semibold mb-4">
                        Login to your account
                    </h2>

                    {/* Mobile Number Input field */}
                    <div className="mb-3">
                        <label className="block text-sm text-white/70 mb-1">
                            Mobile Number
                        </label>
                        <input
                            type="tel"
                            maxLength={10}
                            value={mobile}
                            readOnly={Boolean(location.state?.mobile)}
                            onChange={handleMobileChange}
                            placeholder="Enter 10-digit mobile number"
                            className={`
                                w-full px-3 py-1.5 rounded-lg
                                bg-white/10 border
                                ${mobileError ? "border-red-400" : "border-white/10"}
                                placeholder-white/40
                                focus:outline-none focus:ring-2
                                ${mobileError ? "focus:ring-red-400" : "focus:ring-violet-500"}
                            `}
                        />

                        {/* If there is any error display the error */}
                        {mobileError && (
                            <p className="mt-1 text-xs text-red-400 min-h-[16px]">
                                {mobileError || ""}
                            </p>
                        )}

                    </div>

                    {/* Send OTP Field */}
                    {!otpSent && (
                        <button

                            onClick={handleSendOtp}
                            className="
                            w-full py-2 rounded-full
                            bg-gradient-to-r from-violet-500 to-indigo-500
                            font-medium
                        "
                        >
                            Send OTP
                        </button>
                    )}

                    {/* OTP Section To fill the fields*/}
                    {otpSent && (
                        <div className="mt-4">
                            <p className="text-sm text-white/70 mb-2">
                                Enter the OTP sent to your mobile
                            </p>


                            <div
                                className="flex gap-2 mb-3"
                                onPaste={handlePaste}
                            >
                                {otp.map((value, i) => (
                                    <input
                                        key={i}
                                        ref={(el) => (otpRefs.current[i] = el)}
                                        maxLength={1}
                                        inputMode="numeric"
                                        value={value}
                                        onFocus={focusFirstEmptyOtp}
                                        onChange={(e) => handleOtpChange(e, i)}
                                        onKeyDown={(e) => handleKeyDown(e, i)}
                                        className="
                                        w-8 h-8 sm:w-9 sm:h-9 md:w-10 md:h-10
                                        text-center rounded-lg
                                        bg-white/10 border border-white/10
                                        text-lg focus:outline-none
                                        focus:ring-2 focus:ring-violet-500
                                        "
                                    />
                                ))}
                            </div>

                            {/* Verify */}
                            <button
                                type="button"
                                onClick={handleLogin}
                                disabled={!isOtpComplete}
                                className={`
                                w-full py-2 rounded-full font-medium
                                bg-gradient-to-r from-violet-500 to-indigo-500
                                ${!isOtpComplete ? "opacity-50 cursor-not-allowed" : ""}
                                `}
                                 >
                                Verify & Login
                            </button>

                            {/* Resend */}
                            <button
                                disabled={timer > 0}
                                onClick={handleSendOtp}
                                className={`mt-2 w-full text-xs
                                     ${timer > 0
                                        ? "text-white/40 cursor-not-allowed"
                                        : "text-white/70 hover:text-white"
                                    }
                                `}
                            >
                                {timer > 0 ? `Resend OTP in ${timer}s` : "Resend OTP"}
                            </button>
                        </div>
                    )}
                </div>
            </div>
        </div>
    );
}

export default Login;
