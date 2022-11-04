package kitchenpos.order.ui.dto.request;

public class OrderLineItemRequest {

    private final Long menuId;
    private final long quantity;

    public OrderLineItemRequest(final Long menuId, final long quantity) {
        this.menuId = menuId;
        this.quantity = quantity;
    }

    public Long getMenuId() {
        return menuId;
    }

    public long getQuantity() {
        return quantity;
    }
}