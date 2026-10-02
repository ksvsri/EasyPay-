import { useAuth } from "../context/AuthContext";
import { useAccount } from "../context/AccountContext";
import SendToMany from "../components/sendMoneyToManyComponents/SendToMany";

export default function SendToManyPageProps({
  requireAccount,
  openSendPinModal,
  setBulkSendPayload,
  sendingMoney
}) {
  const { auth } = useAuth();
  const { activeAccount } = useAccount();

  return (
   <div className="relative min-h-screen w-full
                bg-gradient-to-br from-[#0b1220] via-[#0f172a] to-[#020617]">


      {/* Glow Effects – SAME AS DASHBOARD */}
      <div className="absolute -top-40 -right-40 w-[500px] h-[500px]
                      bg-purple-500/30 rounded-full blur-[140px]" />
      <div className="absolute -bottom-40 -left-40 w-[500px] h-[500px]
                      bg-indigo-500/30 rounded-full blur-[140px]" />

      {/* Page Content */}
      <div className="relative z-10 flex justify-center px-6 py-6 w-full">


        <div className="relative z-10 flex justify-center px-6 py-6 w-full">
  <div className="w-full max-w-[500px]">
    <SendToMany
      userId={auth.userId}
      activeAccount={activeAccount}
      accounts={auth.accounts || []}
      requireAccount={requireAccount}
      openSendPinModal={openSendPinModal}
      setBulkSendPayload={setBulkSendPayload}
      sendingMoney={sendingMoney}
    />
  </div>
</div>


      </div>
    </div>
  );
}
