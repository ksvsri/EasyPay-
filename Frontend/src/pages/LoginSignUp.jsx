import { useEffect, useState } from "react";
import { useLocation, useNavigate } from "react-router-dom";

/* App logo */
import logo from "../assets/images/logo/final logo.png";

/* Auth components */
import Login from "../components/Auth/Login";
import Register from "../components/Auth/Register";

/*
 LoginSignUp
- Acts as the entry page for authentication
- Handles:
  • Login UI
  • Register UI
  • URL-based switching (/login, /register)
  • Redirects logged-in users to dashboard

*/
function LoginSignUp() {

  /* ROUTER HOOKS */

  // Used to read current URL path
  const location = useLocation();

  // Used to programmatically navigate
  const navigate = useNavigate();

  /*  AUTH MODE  */

  // Determines which screen to show: login | register
  const [authMode, setAuthMode] = useState(null);

  /* AUTO REDIRECT IF LOGGED IN  */

  /*
    If token already exists, user is authenticated
    → Redirect directly to dashboard
  */
  useEffect(() => {
    const token = localStorage.getItem("token");

    if (token) {
      navigate("/dashboard", { replace: true });
    }
  }, [navigate]);

  /*  SYNC UI WITH URL  */

  /*
    Keeps UI in sync with route:
    /login    → Login component
    /register → Register component
  */
  useEffect(() => {
    if (location.pathname === "/register") {
      setAuthMode("register");
    } else if (location.pathname === "/login") {
      setAuthMode("login");
    } else {
      setAuthMode(null);
    }
  }, [location.pathname]);



  return (
    <div className="min-h-screen w-full flex flex-col overflow-hidden">

      {/* TOP NAVBAR  */}
      <nav className="
        min-h-16
        flex items-center justify-between
        px-2 sm:px-6 py-2
        bg-[#1E2230]/95 backdrop-blur-md
        border-b border-white/10
        text-white shadow-lg
      ">

        {/* App Logo */}
        <img
          src={logo}
          alt="EasyPay logo"
          className="h-12 w-auto"
        />

        {/* Auth Navigation Buttons */}
        <div className="flex gap-2 sm:gap-4">
          <button
            onClick={() => navigate("/login")}
            className="
              px-3 py-1.5
              rounded-full
              bg-gradient-to-r from-violet-500 to-indigo-500
              text-white
              text-xs sm:text-sm
            "
          >
            Login
          </button>

          <button
            onClick={() => navigate("/register")}
            className="
              px-3 py-1.5
              rounded-full
              bg-violet-500 hover:bg-violet-600
              text-white
              text-xs sm:text-sm
            "
          >
            Sign Up
          </button>
        </div>
      </nav>

      {/*  MAIN CONTENT  */}
      <div className="relative flex-1 flex">

        {/*  BACKGROUND IMAGE  */}
        <div
          className="fixed inset-0 -z-10 bg-cover bg-no-repeat"
          style={{
            backgroundImage:
              "url('https://t3.ftcdn.net/jpg/05/48/00/12/360_F_548001279_YgrSQXQqBgGjKYH0epN8Iill9m9JYlPS.jpg')",
            backgroundPosition: "top",
          }}
        />

        {/*  AUTH CONTENT CONTAINER  */}
        <div className="relative z-10 flex-1 overflow-y-auto">
          <div className="min-h-full flex justify-center">

            {/* Centers Login/Register card */}
            <div className="pt-12 pb-12 flex justify-center w-full">

              {/* Conditionally render Login or Register */}
              {authMode === "login" && <Login />}
              {authMode === "register" && <Register />}

            </div>
          </div>
        </div>
      </div>
    </div>
  );
}

export default LoginSignUp;
