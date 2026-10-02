import { useNavigate } from "react-router-dom";

// 404 illustration image
import pageNotFoundUrl from "../assets/images/page not found.webp";

/*
  PageNotFound Component

  - Displays a user-friendly 404 page when a route is not found.
  - Shows a background illustration with a dark overlay.
  - Provides a button to navigate back to the dashboard.
*/
function PageNotFound() {
  const navigate = useNavigate();

  return (
    // Root container
    <div className="flex-1 relative overflow-hidden">

      {/*  BACKGROUND IMAGE */}
      <img
        src={pageNotFoundUrl}
        alt="Page not found"
        className="absolute inset-0 w-full h-full object-cover"
      />

      {/*  DARK OVERLAY  */}
      {/* Improves text readability over the image */}
      <div className="absolute inset-0 bg-black/40" />

      {/*  CONTENT */}
      {/* Center-top aligned content */}
      <div className="relative z-10 flex flex-col items-center justify-start
                      h-full pt-16 sm:pt-24 text-center px-4 gap-6">

        {/* Error heading */}
        <h1 className="text-red-600 text-3xl sm:text-4xl font-bold">
          Page Not Found
        </h1>

        {/* Navigation button */}
        <button
          onClick={() => navigate("/dashboard")}
          className="px-6 py-2 bg-violet-600 hover:bg-violet-700
                     rounded-lg text-white cursor-pointer"
        >
          Go to Dashboard
        </button>
      </div>

    </div>
  );
}

export default PageNotFound;
