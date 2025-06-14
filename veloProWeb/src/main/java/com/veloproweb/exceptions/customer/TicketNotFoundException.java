package com.veloproweb.exceptions.customer;

public class TicketNotFoundException extends RuntimeException{
    public TicketNotFoundException(String message){ super(message);}
}
