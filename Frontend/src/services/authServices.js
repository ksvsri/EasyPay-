import { handleApiRequest } from "./GlobalApiService"
const authUrl=import.meta.env.VITE_AUTH

// Generate OTP for login
export const generateLoginOtp = (mobileNo) => {
  return handleApiRequest(
    "GET",
    `${authUrl}/generateLoginOtp?mobileNo=${mobileNo}`
  );
};

//Generate OTP for register
export const generateRegisterOtp = (mobileNo) => {
  return handleApiRequest(
    "GET",
    `${authUrl}/generateRegisterOtp?mobileNo=${mobileNo}`
  );
};

//Validate the OTP
export const validateOtp=(otpValidateDto)=>{
    return handleApiRequest(
        "POST",
        `${authUrl}/validateOtp`,
        otpValidateDto
    )
}
