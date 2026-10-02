import { useEffect, useState } from "react";

/*
  List of supported image extensions for bank logos.
  The component will try each extension one by one
  until a valid image is found.
*/
const LOGO_EXTS = ["avif", "webp", "png", "jpg", "jpeg"];

/*
  Mapping of bank names to logo file codes.
  Used to convert human-readable bank names
  into standardized logo filenames.
*/
const BANK_NAME_TO_CODE = {
  "hdfc bank": "hdfc",
  "icici bank": "icici",
  "state bank of india": "sbi",
  "axis bank": "axis",
  "kotak mahindra bank": "kotak",
  "punjab national bank": "pnb",
  "bank of baroda": "bob",
  "idfc first bank": "idfc"
};

/*
  Utility function to normalize bank names
  (lowercase + trimmed) for consistent lookup.
*/
function normalize(name = "") {
  return name.toLowerCase().trim();
}

/*
  BankLogo Component

  - Displays the correct bank logo based on bank name.
  - Automatically falls back across multiple image formats.
  - Uses a default logo if no bank logo is found.
*/
function BankLogo({ bankName }) {

  // Index to track which image extension is currently being tried
  const [idx, setIdx] = useState(0);

  /*
    Reset image index whenever bank name changes.
    This ensures logo loading starts from the first extension.
  */
  useEffect(() => {
    setIdx(0);
  }, [bankName]);

  // Get standardized bank code from bank name
  const bankCode = BANK_NAME_TO_CODE[normalize(bankName)];

  /*
    Build logo source path:
    - Try bank-specific logo with current extension
    - Fallback to default logo if bank not found or all extensions fail
  */
  const src =
    bankCode && idx < LOGO_EXTS.length
      ? `/bankLogos/${bankCode}.${LOGO_EXTS[idx]}`
      : "/bankLogos/default.png";

  return (
    <img
      src={src}
      alt={bankName}

      /*
        If logo fails to load:
        - Increment extension index
        - Triggers retry with next image format
      */
      onError={() => setIdx(prev => prev + 1)}

      className="h-10 w-10 object-contain"
    />
  );
}

export default BankLogo;
