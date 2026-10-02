import { jwtDecode } from "jwt-decode";

/* Get the token from the local storage and decode it and return as normal json  */
export const getAuthFromToken = () => {
  const token = localStorage.getItem("token");
  if (!token) return null;

  try {
    const decoded = jwtDecode(token);

    return {
      token,
      userId: decoded.appUserId,
      role: decoded.role,
      userName: decoded.sub,
      profileImagePath: "",
    };
  } catch {
    localStorage.clear();
    return null;
  }
};
