package com.example.easypay.app.repository.projection;

import java.math.BigDecimal;

/**
 * This is used to map the details of custom query called getting payment activity from ledger.
 */
public interface PaymentActivityProjection {
	
	BigDecimal getAmountReceived();

    BigDecimal getAmountSpent();

    Integer getNoOfCredits();

    Integer getNoOfDebits();

}
