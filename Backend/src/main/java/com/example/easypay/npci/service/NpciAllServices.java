package com.example.easypay.npci.service;

import java.math.BigDecimal;
import java.util.List;

public interface NpciAllServices {
	
	public List<String> returnValidUpis(List<String> upis);

	public void routeToReceiver(String receiverUpiId, BigDecimal amount);
}
