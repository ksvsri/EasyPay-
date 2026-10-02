package com.example.easypay.util;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.springframework.stereotype.Component;

//import com.example.easypay.app.repository.UserFeedbackRepository;

@Component
public class OtpUtil{

//    private final UserFeedbackRepository userFeedbackRepository;
// 
    private static final int OTP_EXPIRY_MINUTES = 5;
 
    private Map<String, OtpData> otpStorage = new HashMap<>();
    private Random random = new Random();

//    OtpUtil(UserFeedbackRepository userFeedbackRepository) {
//        this.userFeedbackRepository = userFeedbackRepository;
//    }
 
    // Generate OTP
    public String generateOtp(String mobileNo) {
        String otp = String.format("%06d", random.nextInt(999999));
 
        OtpData data = new OtpData();
        data.setOtp(otp);
        data.setExpiryTime(LocalDateTime.now().plusMinutes(OTP_EXPIRY_MINUTES));
        data.setVerified(false);
 
        otpStorage.put(mobileNo, data);
        System.out.println(data);
        return otp;
    }
 
    // Validate OTP
    public boolean validateOtp(String mobileNo, String otp) {
        OtpData data = otpStorage.get(mobileNo);
       
        System.out.println(data);
 
        if (data == null) return false;
 
        if (data.getExpiryTime().isBefore(LocalDateTime.now())) {
            otpStorage.remove(mobileNo);
            return false;
        }
        if (data.getOtp().equals(otp)) {
        	
        	System.out.println("received otp:"+otp+" generated Otp:"+data.getOtp());
            data.setVerified(true);
            return true;
        }
 
        return false;
    }
 
    // Check OTP verified
    public boolean isOtpVerified(String mobileNo) {
    	System.out.println(mobileNo);
        OtpData data = otpStorage.get(mobileNo);
        System.out.println(data);
        return data != null && data.isVerified();
    }
 
    // Clear OTP after reset
    public void clearOtp(String mobileNo) {
        otpStorage.remove(mobileNo);
    }
}