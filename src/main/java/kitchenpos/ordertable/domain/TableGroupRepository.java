package kitchenpos.ordertable.domain;

import java.util.List;
import java.util.Optional;
import kitchenpos.ordertable.infra.JdbcTemplateOrderTableDao;
import kitchenpos.ordertable.infra.JdbcTemplateTableGroupDao;
import kitchenpos.ordertable.infra.TableGroupDao;
import org.springframework.stereotype.Repository;

@Repository
public class TableGroupRepository implements TableGroupDao {


    private final JdbcTemplateTableGroupDao tableGroupDao;
    private final JdbcTemplateOrderTableDao orderTableDao;

    public TableGroupRepository(final JdbcTemplateTableGroupDao tableGroupDao,
                                final JdbcTemplateOrderTableDao orderTableDao) {
        this.tableGroupDao = tableGroupDao;
        this.orderTableDao = orderTableDao;
    }

    @Override
    public TableGroup save(final TableGroup entity) {
        final List<OrderTable> savedOrderTables = entity.getOrderTables();
        final TableGroup savedTableGroup = tableGroupDao.save(entity);

        final Long tableGroupId = savedTableGroup.getId();
        for (final OrderTable savedOrderTable : savedOrderTables) {
            savedOrderTable.setTableGroupId(tableGroupId);
            savedOrderTable.setEmpty(false);
            orderTableDao.save(savedOrderTable);
        }
        savedTableGroup.setOrderTables(savedOrderTables);

        return savedTableGroup;
    }

    @Override
    public Optional<TableGroup> findById(final Long id) {
        return tableGroupDao.findById(id);
    }

    @Override
    public List<TableGroup> findAll() {
        return tableGroupDao.findAll();
    }
}
