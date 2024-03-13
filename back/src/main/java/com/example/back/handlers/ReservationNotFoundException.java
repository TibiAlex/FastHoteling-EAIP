package com.example.back.handlers;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value= HttpStatus.BAD_REQUEST, reason="Rezervarea nu a fost gasita!")  // 400
public class ReservationNotFoundException extends RuntimeException {
}
