import axios from "axios";
import { getAuthFromToken } from "../utils/authUtils"

// Adds a bearer token to all requests
const api = axios.create({
  baseURL: import.meta.env.VITE_BASE_URL,
});

api.interceptors.request.use(
  (config) => {
    const auth = getAuthFromToken();
    if (auth?.token) {
      config.headers.Authorization = `Bearer ${auth.token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

export default api;
