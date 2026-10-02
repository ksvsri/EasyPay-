import { useRef } from "react";

/*
  PinInput Component

  - Handles secure PIN input (UPI PIN or confirmation PIN).
  - Displays multiple single-digit input boxes.
  - Allows only numeric input.
  - Automatically moves focus forward on input.
  - Handles backspace navigation correctly.
*/
function PinInput({ label, pin, setPin }) {

  // Stores references to each PIN input box
  // Used to control focus movement programmatically
  const refs = useRef([]);

  /*
    Handles digit entry in each PIN box
    - Allows only numbers (0–9)
    - Updates the PIN array state
    - Automatically focuses the next input box
  */
  const handleChange = (e, i) => {
    const value = e.target.value;

    // Allow only single digit numeric input
    if (!/^[0-9]?$/.test(value)) return;

    // Update PIN value at current index
    const next = [...pin];
    next[i] = value;
    setPin(next);

    // Move focus to next box if a digit is entered
    if (value && i < pin.length - 1) {
      requestAnimationFrame(() => {
        refs.current[i + 1]?.focus();
      });
    }
  };

  /*
    Handles backspace behavior
    - Clears current digit if present
    - Otherwise clears previous digit and moves focus backward
  */
  const handleKeyDown = (e, i) => {
    if (e.key !== "Backspace") return;

    e.preventDefault();
    const next = [...pin];

    // Clear current digit
    if (next[i]) {
      next[i] = "";
      setPin(next);
    }
    // Move focus to previous input if current is empty
    else if (i > 0) {
      next[i - 1] = "";
      setPin(next);
      requestAnimationFrame(() => {
        refs.current[i - 1]?.focus();
      });
    }
  };

  return (
    // Wrapper for PIN input group
    <div className="mb-3">

      {/* Label for PIN input */}
      <p className="text-white/70 text-sm mb-2">
        {label}
      </p>

      {/* PIN input boxes */}
      <div className="flex gap-2">
        {pin.map((value, i) => (
          <input
            key={i}

            // Assign ref for focus control
            ref={(el) => (refs.current[i] = el)}

            type="password"        // Hide digit input
            maxLength={1}          // Single digit per box
            value={value}          // Controlled input

            // Input handlers
            onChange={(e) => handleChange(e, i)}
            onKeyDown={(e) => handleKeyDown(e, i)}

            // Disable paste for security
            onPaste={(e) => e.preventDefault()}

            // Styling
            className="
              w-10 h-10
              text-center
              rounded-lg
              bg-white/10
              border border-white/10
              text-lg text-white
              focus:outline-none
              focus:ring-2
              focus:ring-violet-500
              focus:border-violet-500
            "
          />
        ))}
      </div>
    </div>
  );
}

export default PinInput;
