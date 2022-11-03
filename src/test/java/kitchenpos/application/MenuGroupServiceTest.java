package kitchenpos.application;

import static kitchenpos.application.fixture.MenuGroupFixture.치킨;
import static kitchenpos.application.fixture.MenuGroupFixture.피자;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;
import kitchenpos.menu.application.MenuGroupService;
import kitchenpos.menu.domain.MenuGroup;
import kitchenpos.menu.ui.dto.request.MenuGroupRequest;
import kitchenpos.menu.ui.dto.response.MenuGroupResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;


class MenuGroupServiceTest extends ServiceTestBase {

    @Autowired
    private MenuGroupService menuGroupService;

    @DisplayName("MenuGroup을 조회한다.")
    @Test
    void findAll() {
        // given
        menuGroupRepository.save(치킨());
        menuGroupRepository.save(피자());

        // when
        List<MenuGroupResponse> menuGroups = menuGroupService.list();

        // then
        assertThat(menuGroups).hasSize(2);
    }

    @DisplayName("MenuGroup을 정상적으로 등록한다.")
    @Test
    void createMenuGroup() {
        // given
        MenuGroupRequest menuGroup = createMenuGroupRequest("치킨");

        // when
        MenuGroupResponse savedMenuGroup = menuGroupService.create(menuGroup);

        //then
        List<MenuGroup> savedMenuGroups = menuGroupRepository.findAll();
        assertAll(() -> assertThat(savedMenuGroups).hasSize(1),
                () -> assertThat(savedMenuGroup).extracting("name").isEqualTo(menuGroup.getName()));
    }
}
