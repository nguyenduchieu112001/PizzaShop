package com.workshop.pizza.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.braintreepayments.http.HttpResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.paypal.core.PayPalHttpClient;
import com.workshop.pizza.controller.form.BillDetailRequest;
import com.workshop.pizza.controller.form.BillRequest;
import com.workshop.pizza.controller.form.ReservationRequest;
import com.workshop.pizza.entity.Product;
import com.workshop.pizza.entity.ProductSize;
import com.workshop.pizza.entity.Size;
import com.workshop.pizza.exception.BadRequestException;
import com.workshop.pizza.exception.NotFoundException;
import com.workshop.pizza.repository.IProductRepository;
import com.workshop.pizza.repository.IProductSizeRepository;
import com.workshop.pizza.repository.ISizeRepository;
import com.paypal.orders.*;

import java.io.IOException;

@Service
public class PayPalService {

	@Autowired
	private PayPalHttpClient payPalClient;

	@Autowired
	private IProductRepository productRepository;

	@Autowired
	private ISizeRepository sizeRepository;

	@Autowired
	private IProductSizeRepository productSizeRepository;

	@Value("${API.key}")
	private String apiKey;

	public double convertVNDToUSD(double amountInVND) {
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.set("apikey", apiKey);

		UriComponentsBuilder builder = UriComponentsBuilder
				.fromUriString("https://api.apilayer.com/currency_data/convert").queryParam("to", "USD")
				.queryParam("from", "VND").queryParam("amount", amountInVND);

		HttpEntity<?> entity = new HttpEntity<>(headers);
		ResponseEntity<String> response = restTemplate.exchange(builder.buildAndExpand().toUri(), HttpMethod.GET,
				entity, String.class);

		if (response.getStatusCode() == HttpStatus.OK) {
			String responseBody = response.getBody();
			// Parse the JSON response to extract the converted amount in USD
			Gson gson = new GsonBuilder().create();
			JsonObject jsonObject = gson.fromJson(responseBody, JsonObject.class);
			return jsonObject.get("result").getAsDouble();
		} else {
			throw new BadRequestException("Failed to convert currency");
		}
	}

	public HttpResponse<Order> createOrder(ReservationRequest reservation) throws IOException {
		OrdersCreateRequest request = new OrdersCreateRequest();
		request.prefer("return=representation");

		// Set up the order
		OrderRequest orderRequest = new OrderRequest();
		orderRequest.intent("CAPTURE");

		// Set up the purchase unit
		List<PurchaseUnitRequest> purchaseUnitRequests = new ArrayList<>();
		PurchaseUnitRequest purchaseUnitRequest = new PurchaseUnitRequest();
		long price = reservation.getPartySize() * 10000;
		double amount = convertVNDToUSD(price);
		double roundedAmount = Math.round(amount * 100.0) / 100.0;
		purchaseUnitRequest
				.description("Pre-reservation for " + reservation.getReservationDate() + " at "
						+ reservation.getReservationTime() + " with " + reservation.getPartySize() + " guests")
				.amount(new AmountWithBreakdown().currencyCode("USD").value(Double.toString(roundedAmount)));

		purchaseUnitRequests.add(purchaseUnitRequest);
		orderRequest.purchaseUnits(purchaseUnitRequests);
		request.requestBody(orderRequest);

		// Send the request to PayPal
		return payPalClient.execute(request);
	}

	public HttpResponse<Order> createOrderOnline(BillRequest bill) throws IOException {
		OrdersCreateRequest request = new OrdersCreateRequest();
		request.prefer("return=representation");
		// Set up the order
		OrderRequest orderRequest = new OrderRequest();
		orderRequest.intent("CAPTURE");

		// Set up the purchase unit
		List<PurchaseUnitRequest> purchaseUnitRequests = new ArrayList<>();
		PurchaseUnitRequest purchaseUnitRequest = new PurchaseUnitRequest();

		// Create list Items
		List<Item> items = new ArrayList<>();
		double itemTotal = 0.0;
		for (BillDetailRequest billDetail : bill.getBillDetails()) {

			Product product = productRepository.findById(billDetail.getProduct().getId())
					.orElseThrow(() -> new NotFoundException("Product doesn't exists"));

			Size size = sizeRepository.findById(billDetail.getSize().getId())
					.orElseThrow(() -> new NotFoundException("Size doesn't exists"));

			ProductSize productSize = productSizeRepository.findByProductAndSize(product, size);

			double productPrice = convertVNDToUSD(productSize.getProductPrice());
			double roundedProductPrice = Math.round(productPrice * 100.0) / 100.0;
			Item item = new Item();
			item.name(product.getProductName() + " - Size " + size.getName())
					.quantity(Integer.toString(billDetail.getQuantity()))
					.unitAmount(new Money().currencyCode("USD").value(Double.toString(roundedProductPrice)));
			items.add(item);
			itemTotal += roundedProductPrice * billDetail.getQuantity();
		}

		double roundedItemTotal = Math.round(itemTotal * 100.0) / 100.0;
		purchaseUnitRequest
				.amount(new AmountWithBreakdown().currencyCode("USD").value(Double.toString(roundedItemTotal))
						.breakdown(new AmountBreakdown()
								.itemTotal(new Money().currencyCode("USD").value(Double.toString(roundedItemTotal)))))
				.items(items);

		purchaseUnitRequests.add(purchaseUnitRequest);
		orderRequest.purchaseUnits(purchaseUnitRequests);
		request.requestBody(orderRequest);

		// Send the request to PayPal
		return payPalClient.execute(request);
	}

}
