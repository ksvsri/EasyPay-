import { handleApiRequest } from "./GlobalApiService"
const userUrl=import.meta.env.VITE_USER


// Used to Login
export const login=(otpValidateDto)=>{
    return handleApiRequest(
        "POST",
        `${userUrl}/login`,
        otpValidateDto
    )
}

// Register
export const register=(dto)=>{
    return handleApiRequest(
        "POST",
        `${userUrl}/register`,
        dto
    )
}

//Get the user Profile
export const getUserProfile = (userId) => {
  return handleApiRequest(
    "GET",
    `${userUrl}/getProfile?userId=${userId}`
  );
};

//Update the user Profile
export const updateUserDetails = (dto) => {
  return handleApiRequest(
    "POST",
    `${userUrl}/updateUserDetails`,
    dto
  );
};

//Update the user image(DP)
export const uploadProfileImage = (formData) => {
  return handleApiRequest(
    "POST",
    `${userUrl}/uploadProfileImage`,
    formData,
  );
};

// Link the account to the user
export const linkAccount=(dto)=>{
    return handleApiRequest(
        "POST",
        `${userUrl}/linkAccount`,
        dto
    );
}

// Creates Upi and confirm the upi
export const confirmUpi=(dto)=>{
    return handleApiRequest(
        "POST",
        `${userUrl}/confirmUpi`,
        dto
    );
}

//Reset the UPI PIN

export const resetUpiPin=(dto)=>{
    return handleApiRequest(
        "POST",
        `${userUrl}/resetUpiPin`,
        dto
    );
}

// Get the linked accounts from the user linked accounts
export const getlinkedAccounts=(userId)=>{
    return handleApiRequest(
        "GET",
        `${userUrl}/getlinkedAccounts?userId=${userId}`,
    );
}

// make the account primary
export const makePrimaryAccount = (dto) => {
  return handleApiRequest(
    "POST",
    `${userUrl}/makePrimaryAccount`,
    dto
  );
};

// Remove account from the user linked accounts.
export const removeLinkedAccount = (dto) => {
  return handleApiRequest(
    "POST",
    `${userUrl}/removeLinkedAccount`,
    dto
  );
};
