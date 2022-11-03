package kitchenpos.application;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Order;
import kitchenpos.domain.OrderLineItem;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.Product;
import kitchenpos.domain.TableGroup;
import kitchenpos.domain.repository.MenuGroupRepository;
import kitchenpos.domain.repository.MenuRepository;
import kitchenpos.domain.repository.OrderRepository;
import kitchenpos.domain.repository.OrderTableRepository;
import kitchenpos.domain.repository.ProductRepository;
import kitchenpos.domain.repository.TableGroupRepository;
import kitchenpos.dto.MenuGroupRequest;
import kitchenpos.dto.MenuProductRequest;
import kitchenpos.dto.MenuRequest;
import kitchenpos.dto.OrderCreateRequest;
import kitchenpos.dto.OrderLineItemRequest;
import kitchenpos.dto.OrderTableCreateRequest;
import kitchenpos.dto.OrderTableEmptyStatusRequest;
import kitchenpos.dto.OrderTableGuestRequest;
import kitchenpos.dto.ProductRequest;
import kitchenpos.dto.TableGroupCreateRequest;
import kitchenpos.dto.TableGroupOrderTableIdRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

@Sql("/truncate.sql")
@SpringBootTest
public abstract class ServiceTestBase {

    @Autowired
    protected OrderRepository orderRepository;

    @Autowired
    protected ProductRepository productRepository;

    @Autowired
    protected MenuGroupRepository menuGroupRepository;

    @Autowired
    protected MenuRepository menuRepository;

    @Autowired
    protected OrderTableRepository orderTableRepository;

    @Autowired
    protected TableGroupRepository tableGroupRepository;

    protected ProductRequest createProductRequest(String name, int price) {
        return new ProductRequest(name, BigDecimal.valueOf(price));
    }

    protected Product createProduct(final String name, final BigDecimal price) {
        return new Product(name, price);
    }

    protected MenuRequest createMenuRequest(final String name, final BigDecimal price,
                                            final Long menuGroupId, final List<MenuProductRequest> menuProducts) {

        return new MenuRequest(name, price, menuGroupId, menuProducts);
    }

    protected Menu createMenu(final String name, final BigDecimal price,
                              final Long menuGroupId, final List<MenuProduct> menuProducts) {
        return new Menu(name, price, menuGroupId, menuProducts);
    }

    protected MenuProduct createMenuProduct(final Long productId, final long quantity, final BigDecimal price) {
        return new MenuProduct(productId, quantity, price);
    }

    protected MenuGroupRequest createMenuGroupRequest(final String name) {
        return new MenuGroupRequest(name);
    }

    protected OrderTable 주문_테이블_생성() {
        return new OrderTable(2, false);
    }

    protected OrderTable 빈_주문_테이블_생성() {
        return new OrderTable(0, true);
    }

    protected OrderTableCreateRequest createOrderTableCreateRequest(final int guest) {
        return new OrderTableCreateRequest(guest, guest == 0);
    }

    protected OrderTableEmptyStatusRequest createOrderTableEmptyStatusRequest(final boolean empty) {
        return new OrderTableEmptyStatusRequest(empty);
    }

    protected OrderTableGuestRequest createOrderTableGuestRequest(final int guest) {
        return new OrderTableGuestRequest(guest);
    }

    protected TableGroup 단체_지정_생성(final OrderTable... orderTables) {
        List<OrderTable> orderTableList = Arrays.stream(orderTables)
                .collect(Collectors.toList());

        return new TableGroup(LocalDateTime.now(), orderTableList);
    }

    protected TableGroupCreateRequest createTableGroupCreateRequest(final OrderTable... orderTables) {
        List<TableGroupOrderTableIdRequest> orderTableIds = Arrays.stream(orderTables)
                .map(orderTable -> new TableGroupOrderTableIdRequest(orderTable.getId()))
                .collect(Collectors.toList());
        return new TableGroupCreateRequest(orderTableIds);
    }

    protected Order createOrder(Long orderTableId, List<OrderLineItem> orderLineItems) {
        return new Order(orderTableId, OrderStatus.COOKING.name(), LocalDateTime.now(), orderLineItems);
    }

    protected OrderCreateRequest createOrderCreateRequest(Long orderTableId,
                                                          List<OrderLineItemRequest> orderLineItemRequests) {
        return new OrderCreateRequest(orderTableId, orderLineItemRequests);
    }

    protected OrderLineItem createOrderLineItem(Long menuId, long quantity) {
        return new OrderLineItem(menuId, quantity);
    }

    protected OrderLineItemRequest createOrderLineItemRequest(final Long menuId, final long quantity) {
        return new OrderLineItemRequest(menuId, quantity);
    }


    protected Order 주문_생성_및_저장(final OrderTable orderTable, final Menu menu, final long quantity) {
        List<OrderLineItem> orderLineItems = Collections.singletonList(createOrderLineItem(menu.getId(), quantity));
        Order order = createOrder(orderTable.getId(), orderLineItems);

        return orderRepository.save(order);
    }
}
