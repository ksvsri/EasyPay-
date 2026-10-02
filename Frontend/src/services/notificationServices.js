import { handleApiRequest } from "./GlobalApiService"
const notificationUrl=import.meta.env.VITE_NOTIFICATION


// export const getUnreadNotificationCount=(userId)=>{
//     return handleApiRequest(
//         "GET",
//         `${notificationUrl}/getUnreadNotificationCount?userId=${userId}`
//     )
// }

export const getUnreadNotificationCountAndLatestTwoNotifications=(userId)=>{
    return handleApiRequest(
        "GET",
        `${notificationUrl}/getCountAndLatestTwo?userId=${userId}`
    )
}

export const getAllNotifications=(userId)=>{
    return handleApiRequest(
        "GET",
        `${notificationUrl}/getAll?userId=${userId}`
    )
}

export const markOneAsRead=(userId,notificationId)=>{
    return handleApiRequest(
        "PUT",
        `${notificationUrl}/markOneAsRead?userId=${userId}&notificationId=${notificationId}`
    )
}
export const markAllAsRead=(userId)=>{
    return handleApiRequest(
        "PUT",
        `${notificationUrl}/markAllAsRead?userId=${userId}`
    )
}
