//Login validation
export const isValidMobile = (value) => {
  if (!value) return false;

  // Indian mobile number: starts with 6–9 and total 10 digits
  return /^[6-9][0-9]{9}$/.test(value);
};
// Regiser Validations
export const isValidEmail = (value) =>
  /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(value);

//  Validate name is valid or not and should contain only the letters
export const isValidName = (name) => {
  if (!name) return false;
  const regex = /^[A-Za-z ]{2,}$/;
  return regex.test(name.trim());
};
//send money helpers


// UPI ID validation (Send Money)
export const isValidUpi = (value) => {
  if (!value) return false;
  return /^[\w.-]+@[\w.-]+$/.test(value);
};

// Receiver validation (mobile OR UPI)
export const isValidReceiver = (value) => {
  if (!value) return false;
  return isValidMobile(value) || isValidUpi(value);
};

// Amount validation
export const isValidAmount = (amount) => {
  if (!amount) return false;
  return Number(amount) > 0 && !/^0+$/.test(amount);
};

//Send to many validatations

export const isValidBulkReceiver = (receiver) => {
  if (!receiver) return false;

  return (
    isValidUpi(receiver.receiverUpiId) &&
    isValidAmount(receiver.amount)
  );
};

// Send to many - form validation
export const isValidBulkForm = (receivers) => {
  if (!Array.isArray(receivers) || receivers.length === 0) return false;

  const totalAmount = receivers.reduce((sum, r) => {
    const amt = Number(r.amount);
    return sum + (isNaN(amt) ? 0 : amt);
  }, 0);

  return (
    totalAmount > 0 &&
    receivers.every(isValidBulkReceiver)
  );
};
