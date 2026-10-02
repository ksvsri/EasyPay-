import { useEffect, useRef, useState } from "react";
import { toast } from "react-toastify";

// Services
import {
  fetchSuggestions,
  resolveReceiver
} from "../../services/dashboardServices";

// Validation utilities
import {
  isValidAmount,
  isValidReceiver
} from "../../validationUtils/globalValidity";

export default function ReceiverInputBlock({
  userId,
  onResolved,
  showRemove = false,
  onRemove,
    locked = false,
}) {

  /* FORM STATE  */

  const [receiverDisplay, setReceiverDisplay] = useState("");
  const [receiverValue, setReceiverValue] = useState("");
  const [amount, setAmount] = useState("");
  const [note, setNote] = useState("");

  /*  SUGGESTIONS  */

  const [receiverSuggestions, setReceiverSuggestions] = useState([]);
  const debounceRef = useRef(null);
  const abortRef = useRef(null);
  const isSelectingRef = useRef(false);

  /*  RESOLUTION  */

  const [resolvedReceiver, setResolvedReceiver] = useState(null);
  const [resolvingReceiver, setResolvingReceiver] = useState(false);

 const isConfirmed = locked || !!resolvedReceiver;


  /*  VALIDATION  */

  const isReceiverValid = isValidReceiver(receiverValue);
  const isAmountValid = isValidAmount(amount);
  const canSend = isReceiverValid && isAmountValid;

  /*  FETCH SUGGESTIONS  */

  const fetchReceiverSuggestions = async (query) => {
    try {
      abortRef.current?.abort();
      abortRef.current = new AbortController();

      const res = await fetchSuggestions(
        userId,
        query,
        abortRef.current.signal
      );

      setReceiverSuggestions(res?.data || []);
    } catch (err) {
      if (err.name !== "AbortError") {
        console.error("Suggestion fetch failed", err);
      }
    }
  };

  useEffect(() => {
    if (isConfirmed) return;

    if (isSelectingRef.current) {
      isSelectingRef.current = false;
      return;
    }

    if (receiverDisplay.length < 2) {
      abortRef.current?.abort();
      setReceiverSuggestions([]);
      return;
    }

    debounceRef.current && clearTimeout(debounceRef.current);

    debounceRef.current = setTimeout(() => {
      fetchReceiverSuggestions(receiverDisplay);
    }, 300);

    return () => clearTimeout(debounceRef.current);
  }, [receiverDisplay, isConfirmed]);

  useEffect(() => {
    return () => {
      abortRef.current?.abort();
      debounceRef.current && clearTimeout(debounceRef.current);
    };
  }, []);


  return (
 <div
  className={`relative transition-all duration-300 overflow-hidden
    ${isConfirmed ? "min-h-[180px]" : "min-h-[360px]"}`}
>

      {/* Remove block */}
      {showRemove && (
        <button
          onClick={onRemove}
          className="absolute top-3 right-3 text-red-400"
        >
          ✕
        </button>
      )}

      {/*  INPUT MODE  */}
      {!isConfirmed && (
        <>
          {/* Receiver input */}
          <div className="relative mb-4">
            <input
              value={receiverDisplay}
              onChange={(e) => {
                setReceiverDisplay(e.target.value);
                setReceiverValue(e.target.value);
              }}
              className={`w-full px-4 py-3 rounded-xl bg-white/10 border text-white
                focus:outline-none focus:ring-1
                ${receiverDisplay && !isReceiverValid
                  ? "border-red-400 focus:ring-red-400/40"
                  : "border-white/10 focus:border-violet-400 focus:ring-violet-400/60"}`}
              placeholder="Receiver (UPI ID / Mobile)"
            />

            <div className="min-h-[16px] mt-1">
              {receiverDisplay && !isReceiverValid && receiverSuggestions.length === 0 && (
                <p className="text-[11px] text-red-400/80">
                  Enter a valid mobile number or UPI ID
                </p>
              )}
            </div>
          </div>

          {/* Suggestions */}
          {receiverSuggestions.length > 0 && (
            <div className="relative">
              <div className="absolute z-40 mt-1 w-full rounded-xl bg-[#0b1220]/90 backdrop-blur border border-white/5">
                {receiverSuggestions.map((item, index) => {
                  const isUser = item.includes("•");

                  return (
                    <div
                      key={index}
                      onClick={() => {
                        isSelectingRef.current = true;

                        if (isUser) {
                          const [name, mobile] = item.split("•").map(s => s.trim());
                          setReceiverDisplay(name);
                          setReceiverValue(mobile);
                        } else {
                          setReceiverDisplay(item);
                          setReceiverValue(item);
                        }

                        setReceiverSuggestions([]);
                      }}
                      className="flex items-center gap-3 px-4 py-3 cursor-pointer hover:bg-white/5"
                    >
                      <div className={`w-8 h-8 flex items-center justify-center rounded-full text-xs
                        ${isUser ? "bg-purple-500/10 text-purple-400" : "bg-indigo-500/10 text-indigo-400"}`}>
                        {isUser ? "👤" : "🔗"}
                      </div>

                      <div className="flex flex-col leading-tight">
                        <span className="text-sm text-gray-100">
                          {isUser ? item.split("•")[0].trim() : item}
                        </span>
                        <span className="text-xs text-gray-400">
                          {isUser ? item.split("•")[1].trim() : "UPI ID"}
                        </span>
                      </div>
                    </div>
                  );
                })}
              </div>
            </div>
          )}

          {/* Amount */}
          <div className="relative mb-4">
            <input
              value={amount}
              onChange={(e) => {
                const value = e.target.value;
                if (/^\d*\.?\d{0,2}$/.test(value)) {
                  setAmount(value);
                }
              }}
              className={`w-full px-4 py-3 rounded-xl bg-white/10 border text-white
                focus:outline-none focus:ring-1
                ${amount && !isAmountValid
                  ? "border-red-400 focus:ring-red-400/40"
                  : "border-white/10 focus:border-violet-400 focus:ring-violet-400/60"}`}
              placeholder="Amount (₹)"
            />

            <div className="min-h-[16px] mt-1">
              {amount && !isAmountValid && (
                <p className="text-[11px] text-red-400/80">
                  Enter a valid amount
                </p>
              )}
            </div>
          </div>

          {/* Note */}
          <input
            value={note}
            onChange={(e) => setNote(e.target.value)}
            className="w-full mb-4 px-4 py-3 rounded-xl bg-white/10 border border-white/10 text-white
              focus:outline-none focus:border-violet-400 focus:ring-1 focus:ring-violet-400/60"
            placeholder="Note (optional)"
          />

          {/* Send button */}
          <button
            disabled={!canSend || resolvingReceiver}
            onClick={async () => {
              try {
                setResolvingReceiver(true);

                const res = await resolveReceiver(receiverValue);

                if (res.error) {
                  toast.error(res.error);
                  return;
                }

                setResolvedReceiver(res.data);
              } catch {
                toast.error("Unable to fetch receiver details");
              } finally {
                setResolvingReceiver(false);
              }
            }}
            className={`w-full py-3 rounded-xl font-semibold transition
              ${canSend
                ? "bg-purple-500 text-white hover:opacity-90"
                : "bg-gray-600 text-gray-300 cursor-not-allowed"}`}
          >
            {resolvingReceiver ? "Checking..." : "Send Money"}
          </button>
        </>
      )}

      {/*  CONFIRMATION MODE  */}
{isConfirmed && (
  <div className="mb-4 p-4 rounded-xl bg-purple-500/10 border border-purple-400/40">

    <p className="text-xs text-gray-400 mb-1">Paying to</p>

    <p className="text-white font-semibold">
      {resolvedReceiver?.receiverName}
    </p>

    <p className="text-xs text-gray-400">
      {resolvedReceiver?.accounts?.upiId}
    </p>

    <div className="flex justify-between mt-3 text-sm text-gray-300">
      <span>Amount</span>
      <span>₹{amount}</span>
    </div>

    {note && (
      <div className="mt-2 text-xs text-gray-400">
        Note: {note}
      </div>
    )}

    {!locked && (
      <div className="flex gap-3 mt-4">
        <button
          onClick={() => setResolvedReceiver(null)}
          className="flex-1 py-2 rounded-xl border border-white/20 text-gray-300"
        >
          Edit
        </button>

        <button
          onClick={() =>
            onResolved({
              receiverUpiId: resolvedReceiver.accounts.upiId,
              receiverName: resolvedReceiver.receiverName,
              bankName: resolvedReceiver.accounts.bankName,
              maskedAccountNo: resolvedReceiver.accounts.maskedAccountNo,
              amount,
              note
            })
          }
          className="flex-1 py-2 rounded-xl bg-purple-500 text-black font-semibold"
        >
          Confirm
        </button>
      </div>
    )}
  </div>
)}


    </div>
  );
}
