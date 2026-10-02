import { handleApiRequest } from "./GlobalApiService"
const transactionsUrl=import.meta.env.VITE_TRANSACTIONS;
export const getAllTransactions=(payload)=>{
    return handleApiRequest(
        "POST",
        `${transactionsUrl}/getAllTransactions`,
        payload
    );
}
