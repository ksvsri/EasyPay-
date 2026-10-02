import api from "../api/axiosInstance";

/*  Global api request to send the request */
export const handleApiRequest = async (
  method,
  url,
  payload = null,
  signal,
  config = {}
) => {
  try {
    const response = await api({
      method,
      url,
      data: payload,
      signal,
      ...config,
    });

   return { data: response.data, error: null };
  } catch (err) {
   console.log(err.response.data.message)
    return {
      data: null,
      error: err.response?.data.message || "Something went wrong",
    };
  }
};
