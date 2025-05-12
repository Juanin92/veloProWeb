package com.veloProWeb.exceptions.customer;

public class TicketAlreadyPaidException extends RuntimeException{
    public TicketAlreadyPaidException(String message){ super(message);}
}
