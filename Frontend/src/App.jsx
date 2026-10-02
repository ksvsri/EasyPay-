import { Route, Routes } from "react-router-dom";
import { ToastContainer } from "react-toastify";

import Dashboard from "./pages/Dashboard";
import Transactions from "./pages/Transactions";
import Requests from "./pages/Requests";
import Feedback from "./pages/Feedback";
import LoginSignUp from "./pages/LoginSignUp";
import MainLayout from "./layouts/MainLayout";
import UserProfile from "./pages/UserProfile";
import PageNotFound from "./components/PageNotFound";
import { AccountProvider } from "./context/AccountContext";
import SendToManyPageProps from "./pages/SendToManyPageProps";



function App() {
    return (
        <>

            <AccountProvider>
                <Routes>
                    <Route path="/" element={<LoginSignUp />} />
                    <Route path="/login" element={<LoginSignUp />} />
                    <Route path="/register" element={<LoginSignUp />} />
                    <Route element={<MainLayout />}>
                        <Route path="/dashboard" element={<Dashboard />} />
                        <Route path="/userProfile" element={<UserProfile />} />
                        <Route path="/transactions" element={<Transactions />} />
                        <Route path="/requests" element={<Requests />} />
                        <Route path="/feedback" element={<Feedback />} />
                        <Route path="*" element={<PageNotFound />} />
                        <Route
                            path="/dashboard/send-to-many"
                            element={<SendToManyPageProps />}
                        />
                    </Route>

                </Routes>
            </AccountProvider>


            <ToastContainer autoClose={2000} />
        </>
    );
}

export default App;
