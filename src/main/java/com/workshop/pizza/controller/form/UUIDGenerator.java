package com.workshop.pizza.controller.form;

import java.util.UUID;

public final class UUIDGenerator {

	private UUIDGenerator() {}

    public static String generate() {
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        return uuid.substring(0, 10);
    }
}
