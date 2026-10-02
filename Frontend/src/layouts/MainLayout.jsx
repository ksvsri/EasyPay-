import { Outlet } from "react-router-dom";
import Navbar from "../components/Navbar";

function MainLayout() {
  return (
    <div className="relative min-h-screen flex flex-col
                    bg-gradient-to-br from-[#0b1220] via-[#0f172a] to-[#020617]
                    text-white overflow-hidden">

      <Navbar />

      {/* Glow Effects */}
      <div className="absolute -top-40 -right-40 w-[500px] h-[500px]
                      bg-purple-500/30 rounded-full blur-[140px]" />
      <div className="absolute -bottom-40 -left-40 w-[500px] h-[500px]
                      bg-indigo-500/30 rounded-full blur-[140px]" />

      <main className="relative z-10 flex-1 py-6 flex">
        <div className="max-w-7xl mx-auto px-6 flex-1 flex">
          <Outlet />
        </div>
      </main>

    </div>
  );
}

export default MainLayout;
