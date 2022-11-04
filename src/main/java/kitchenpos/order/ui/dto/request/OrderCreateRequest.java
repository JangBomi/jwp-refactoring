package kitchenpos.order.ui.dto.request;

import java.util.List;

public class OrderCreateRequest {
    private final Long orderTableId;
    private final List<OrderLineItemRequest> orderLineItemRequests;

    public OrderCreateRequest(final Long orderTableId, final List<OrderLineItemRequest> orderLineItemRequests) {
        this.orderTableId = orderTableId;
        this.orderLineItemRequests = orderLineItemRequests;
    }

    public Long getOrderTableId() {
        return orderTableId;
    }

    public List<OrderLineItemRequest> getOrderLineItemRequests() {
        return orderLineItemRequests;
    }
}