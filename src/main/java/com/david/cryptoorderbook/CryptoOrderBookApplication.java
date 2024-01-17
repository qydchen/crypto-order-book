package com.david.cryptoorderbook;

import book.OrderBook;
import com.google.gson.Gson;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import models.SubscribeToCoinBase;
import web.WebSocketClientEndpoint;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;

@Configuration
@EnableAutoConfiguration
@EnableWebSocket
public class CryptoOrderBookApplication extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(CryptoOrderBookApplication.class, args);
		OrderBook orderBook = new OrderBook();
		try {
			final WebSocketClientEndpoint clientEndPoint = new WebSocketClientEndpoint(new URI(Constants.server_websocket_url));
			clientEndPoint.addMessageHandler(orderBook::receiveTick);

			List<String> productIds = Arrays.asList(args);
			SubscribeToCoinBase subscription = new SubscribeToCoinBase(productIds);
			Gson gson = new Gson();
			clientEndPoint.sendMessage(gson.toJson(subscription));

			Thread.sleep(5000);

		} catch (InterruptedException ex) {
			System.err.println("InterruptedException exception: " + ex.getMessage());
		} catch (URISyntaxException ex) {
			System.err.println("URISyntaxException exception: " + ex.getMessage());
		}

	}

}
