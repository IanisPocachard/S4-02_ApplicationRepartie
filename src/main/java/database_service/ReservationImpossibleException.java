package database_service;

public class ReservationImpossibleException extends RuntimeException {
    public ReservationImpossibleException(String message) {
        super(message);
    }
}
