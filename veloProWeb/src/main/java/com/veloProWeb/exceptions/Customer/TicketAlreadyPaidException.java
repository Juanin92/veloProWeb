package com.veloProWeb.exceptions.Customer;

public class TicketAlreadyPaidException extends RuntimeException{
    public TicketAlreadyPaidException(String message){ super(message);}
}
