import { handleApiRequest } from "./GlobalApiService"
const dashboardUrl=import.meta.env.VITE_DASHBOARD;
export const getAvailableBalance=(userId,accountId,pin)=>{
    return handleApiRequest(
        "GET",
        `${dashboardUrl}/getAvailableBalance?userId=${userId}&accountId=${accountId}&pin=${pin}`
    );
}
//Getting suggestions
export const fetchSuggestions=(userId,query,signal)=>{
    return handleApiRequest(
        "GET",
        `${dashboardUrl}/getReceiverSuggestions?senderId=${userId}&recieverMatchInput=${query}`,
        null,
        signal
    );
}

export const resolveReceiver = (input) => {
  return handleApiRequest(
    "GET",
    `${dashboardUrl}/resolveReceiver?value=${input}`
  );
};

//Send money to one
export const sendMoneyToOne=(dto)=>{
    return handleApiRequest(
        "POST",
        `${dashboardUrl}/sendMoneyToOne`,
        dto
    );
}

export const sendMoneyToMany=(dto)=>{
    return handleApiRequest(
        "POST",
        `${dashboardUrl}/sendMoneyToMany`,
        dto
    );
}


export const getPaymentActivity=(dto)=>{
    return handleApiRequest(
        "POST",
        `${dashboardUrl}/paymentActivity`,
        dto
    );
}
