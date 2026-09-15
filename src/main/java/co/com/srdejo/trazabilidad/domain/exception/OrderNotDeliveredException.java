package co.com.srdejo.trazabilidad.domain.exception;

public class OrderNotDeliveredException extends DomainException {
    public OrderNotDeliveredException() {
        super("El pedido aún no ha sido entregado, no tiene tiempo de finalización");
    }
}
