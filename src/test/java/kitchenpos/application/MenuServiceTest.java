package kitchenpos.application;

import static kitchenpos.application.fixture.MenuFixture.양념_치킨;
import static kitchenpos.application.fixture.MenuFixture.포테이토_피자;
import static kitchenpos.application.fixture.MenuFixture.후라이드_치킨;
import static kitchenpos.application.fixture.MenuGroupFixture.여러마리_메뉴_그룹;
import static kitchenpos.application.fixture.MenuGroupFixture.치킨;
import static kitchenpos.application.fixture.MenuGroupFixture.피자;
import static kitchenpos.application.fixture.ProductFixture.양념_치킨;
import static kitchenpos.application.fixture.ProductFixture.포테이토_피자;
import static kitchenpos.application.fixture.ProductFixture.후라이드_치킨;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Product;
import kitchenpos.dto.MenuRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;


class MenuServiceTest extends ServiceTestBase {

    @Autowired
    private MenuService menuService;


    @DisplayName("전체 메뉴를 조회한다")
    @Test
    void findAll() {
        // given
        Product productChicken1 = productDao.save(후라이드_치킨());
        Product productChicken2 = productDao.save(양념_치킨());
        MenuGroup chickenMenuGroup = menuGroupDao.save(치킨());

        Menu menuChicken1 = menuDao.save(후라이드_치킨(chickenMenuGroup));
        Menu menuChicken2 = menuDao.save(양념_치킨(chickenMenuGroup));

        MenuProduct menuProductChicken1 = 메뉴_상품_생성(menuChicken1, productChicken1, 1);
        MenuProduct menuProductChicken2 = 메뉴_상품_생성(menuChicken2, productChicken2, 1);

        Product productPizza = productDao.save(포테이토_피자());
        MenuGroup pizzaMenuGroup = menuGroupDao.save(피자());

        Menu menuPizza = menuDao.save(포테이토_피자(pizzaMenuGroup));
        MenuProduct menuProductPizza = 메뉴_상품_생성(menuPizza, productPizza, 1);

        // when
        List<Menu> menus = menuService.list();

        //then
        assertAll(
                () -> assertThat(menus).hasSize(3),
                () -> assertThat(menus).extracting("menuProducts").isNotNull()
        );
    }


    @DisplayName("메뉴의 가격이 null이면 예외가 발생한다.")
    @Test
    void createMenePriceNull() {
        // given
        MenuGroup chickenMenuGroup = menuGroupDao.save(여러마리_메뉴_그룹());
        Product chicken = productDao.save(후라이드_치킨());
        Product seasonedChicken = productDao.save(양념_치킨());
        List<MenuProduct> menuProducts = Arrays.asList(
                new MenuProduct(chicken.getId(), 1),
                new MenuProduct(seasonedChicken.getId(), 2));
        MenuRequest menuRequest = createMenuRequest("세마리 메뉴", null, chickenMenuGroup.getId(), menuProducts);

        // when & then
        assertThatThrownBy(
                () -> menuService.create(menuRequest)
        ).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("메뉴의 가격은 비어있거나 0보다 작을 수 없습니다.");
    }

    @DisplayName("메뉴의 가격이 0보다 작으면 예외가 발생한다.")
    @Test
    void createMenuPrice0() {
        // given
        MenuGroup chickenMenuGroup = menuGroupDao.save(치킨());
        Product chicken = productDao.save(후라이드_치킨());
        Product seasonedChicken = productDao.save(양념_치킨());
        List<MenuProduct> menuProducts = Arrays.asList(
                new MenuProduct(chicken.getId(), 1),
                new MenuProduct(seasonedChicken.getId(), 2));
        MenuRequest menuRequest = createMenuRequest("세마리 메뉴", BigDecimal.valueOf(-1000), chickenMenuGroup.getId(),
                menuProducts);

        // when & then
        assertThatThrownBy(
                () -> menuService.create(menuRequest)
        ).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("메뉴의 가격은 비어있거나 0보다 작을 수 없습니다.");
    }

    @DisplayName("메뉴 그룹의 아이디가 존재하지 않으면 예외를 발생한다.")
    @Test
    void createNoProduct() {
        // given
        Product chicken = productDao.save(후라이드_치킨());
        Product seasonedChicken = productDao.save(양념_치킨());
        List<MenuProduct> menuProducts = Arrays.asList(
                new MenuProduct(chicken.getId(), 1),
                new MenuProduct(seasonedChicken.getId(), 2));
        MenuRequest menuRequest = createMenuRequest("세마리 메뉴", BigDecimal.valueOf(40000), 0L, menuProducts);

        // when & then
        assertThatThrownBy(
                () -> menuService.create(menuRequest)
        ).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("존재하지 않는 메뉴 그룹입니다.");
    }

    @DisplayName("MenuProduct의 product가 존재하지 않으면 예외를 발생한다.")
    @Test
    void createMenuGroupId() {
        // given
        MenuGroup chickenMenuGroup = menuGroupDao.save(치킨());
        List<MenuProduct> menuProducts = Arrays.asList(
                new MenuProduct(0L, 1));
        MenuRequest menuRequest = createMenuRequest("세마리 메뉴", BigDecimal.valueOf(40000), chickenMenuGroup.getId(),
                menuProducts);

        // when & then
        assertThatThrownBy(
                () -> menuService.create(menuRequest)
        ).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("존재하지 않는 상품입니다.");
    }

    @DisplayName("가격이 구매할 수 있는 product의 금액보다 크면 예외를 발생한다.")
    @Test
    void createInvalidMenuPrice() {
        // given
        MenuGroup chickenMenuGroup = menuGroupDao.save(여러마리_메뉴_그룹());
        Product chicken = productDao.save(후라이드_치킨());
        Product seasonedChicken = productDao.save(양념_치킨());
        List<MenuProduct> menuProducts = Arrays.asList(
                new MenuProduct(chicken.getId(), 1),
                new MenuProduct(seasonedChicken.getId(), 2));
        MenuRequest menuRequest = createMenuRequest("세마리 메뉴", BigDecimal.valueOf(1000000), chickenMenuGroup.getId(),
                menuProducts);

        // when & then
        assertThatThrownBy(
                () -> menuService.create(menuRequest)
        ).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("가격이 유효하지 않습니다.");
    }

    @DisplayName("가격이 구매할 수 있는 product의 금액보다 같거나 작으면 예외를 발생하지 않는다.")
    @ParameterizedTest
    @ValueSource(ints = {56000, 55000})
    void createInvalidMenuPrice(int price) {
        // given
        MenuGroup chickenMenuGroup = menuGroupDao.save(여러마리_메뉴_그룹());
        Product chicken = productDao.save(후라이드_치킨());
        Product seasonedChicken = productDao.save(양념_치킨());
        List<MenuProduct> menuProducts = Arrays.asList(
                new MenuProduct(chicken.getId(), 1),
                new MenuProduct(seasonedChicken.getId(), 2));
        MenuRequest menuRequest = createMenuRequest("세마리 메뉴", BigDecimal.valueOf(price), chickenMenuGroup.getId(),
                menuProducts);

        // when & then
        assertDoesNotThrow(
                () -> menuService.create(menuRequest)
        );
    }

    @DisplayName("Menu를 올바르게 저장한다.")
    @Test
    void createMenu() {
        // given
        MenuGroup chickenMenuGroup = menuGroupDao.save(여러마리_메뉴_그룹());
        Product chicken = productDao.save(후라이드_치킨());
        Product seasonedChicken = productDao.save(양념_치킨());
        List<MenuProduct> menuProducts = Arrays.asList(
                new MenuProduct(chicken.getId(), 1),
                new MenuProduct(seasonedChicken.getId(), 2));
        MenuRequest menuRequest = createMenuRequest("세마리 메뉴", BigDecimal.valueOf(40000), chickenMenuGroup.getId(),
                menuProducts);

        // when
        Menu menu = menuService.create(menuRequest);

        List<Menu> menus = menuDao.findAll();
        Optional<Menu> foundMenu = menuDao.findById(menu.getId());
        //then
        assertAll(
                () -> assertThat(menus).hasSize(1),
                () -> assertThat(foundMenu).isPresent()
        );
    }

    @DisplayName("Menu를 올바르게 저장할 때 menuProduct도 올바르게 저장됨을 확인한다.")
    @Test
    void createMenuAndCheckMenuProduct() {
        // given
        MenuGroup chickenMenuGroup = menuGroupDao.save(여러마리_메뉴_그룹());
        Product chicken = productDao.save(후라이드_치킨());
        Product seasonedChicken = productDao.save(양념_치킨());
        List<MenuProduct> menuProducts = Arrays.asList(
                new MenuProduct(chicken.getId(), 1),
                new MenuProduct(seasonedChicken.getId(), 2));
        MenuRequest menuRequest = createMenuRequest("세마리 메뉴", BigDecimal.valueOf(40_000L), chickenMenuGroup.getId(),
                menuProducts);

        // when
        Menu menu = menuService.create(menuRequest);

        Optional<Menu> foundMenu = menuDao.findById(menu.getId());
        List<MenuProduct> foundMenuProducts = menuProductDao.findAllByMenuId(menu.getId());
        //then
        assertAll(
                () -> assertThat(foundMenu).isPresent(),
                () -> assertThat(foundMenuProducts).hasSize(2),
                () -> assertThat(foundMenuProducts).extracting("productId")
                        .contains(chicken.getId(), seasonedChicken.getId())
        );
    }
}
