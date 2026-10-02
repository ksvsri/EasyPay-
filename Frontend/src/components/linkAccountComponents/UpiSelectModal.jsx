/* shows upiIds to the user to choose one of them */
function UpiSelectModal({
  open,
  upiOptions,
  selectedUpi,
  onSelect,
  onClose,
  onContinue,
}) {
  if (!open) return null;

  return (
    <div className="fixed inset-0 z-[100] flex items-center justify-center">
      {/* Backdrop */}
      <div
        className="absolute inset-0 bg-black/60"
        onClick={onClose}
      />

      {/* Modal */}
      <div
        className="relative bg-[#0f172a] p-6 rounded-xl w-full max-w-sm"
        onClick={(e) => e.stopPropagation()}
      >
        <h4 className="text-white mb-4">Choose your UPI ID</h4>

        <div className="space-y-2">
          {upiOptions.map((upi) => (
            <label key={upi} className="flex items-center gap-2 text-white">
              <input
                type="radio"
                checked={selectedUpi === upi}
                onChange={() => onSelect(upi)}
              />
              {upi}
            </label>
          ))}
        </div>

        <button
          disabled={!selectedUpi}
          onClick={onContinue}
          className="mt-4 w-full bg-violet-600 py-2 rounded disabled:opacity-50"
        >
          Continue
        </button>
      </div>
    </div>
  );
}

export default UpiSelectModal;
