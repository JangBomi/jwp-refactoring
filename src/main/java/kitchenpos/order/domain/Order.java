package kitchenpos.order.domain;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.util.CollectionUtils;

public class Order {
    private Long id;
    private Long orderTableId;
    private String orderStatus;
    private LocalDateTime orderedTime;
    private List<OrderLineItem> orderLineItems;

    public Order() {
    }

    public Order(final Long orderTableId, final String orderStatus, final LocalDateTime orderedTime,
                 final List<OrderLineItem> orderLineItems) {
        if (CollectionUtils.isEmpty(orderLineItems)) {
            throw new IllegalArgumentException("주문 항목이 비어있습니다.");
        }

        this.orderTableId = orderTableId;
        this.orderStatus = orderStatus;
        this.orderedTime = orderedTime;
        this.orderLineItems = orderLineItems;
    }

    public Order(final Long id, final Long orderTableId, final String orderStatus, final LocalDateTime orderedTime,
                 final List<OrderLineItem> orderLineItems) {
        this(orderTableId, orderStatus, orderedTime, orderLineItems);
        this.id = id;
    }

    public static Order create(final Long orderTableId, final List<OrderLineItem> orderLineItems, long menuSize) {
        Order order = new Order(orderTableId, OrderStatus.COOKING.name(), LocalDateTime.now(), orderLineItems);
        order.validateMenuSize(menuSize);
        return order;
    }

    private void validateMenuSize(final long menuSize) {
        if (orderLineItems.size() != menuSize) {
            throw new IllegalArgumentException("메뉴의 수가 부족합니다.");
        }
    }

    public void changeOrderStatus(final String orderStatus) {
        OrderStatus orderStatusToChange = OrderStatus.valueOf(orderStatus);
        if (OrderStatus.valueOf(this.orderStatus).isCompleted()) {
            throw new IllegalArgumentException("이미 완료된 주문입니다.");
        }

        setOrderStatus(orderStatusToChange.name());
    }

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public Long getOrderTableId() {
        return orderTableId;
    }

    public void setOrderTableId(final Long orderTableId) {
        this.orderTableId = orderTableId;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(final String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public LocalDateTime getOrderedTime() {
        return orderedTime;
    }

    public void setOrderedTime(final LocalDateTime orderedTime) {
        this.orderedTime = orderedTime;
    }

    public List<OrderLineItem> getOrderLineItems() {
        return orderLineItems;
    }

    public void setOrderLineItems(final List<OrderLineItem> orderLineItems) {
        this.orderLineItems = orderLineItems;
    }
}
