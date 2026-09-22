/*
 * NOTE: This application ("WIMP - Where Is My Pizza") is provided solely to
 * demonstrate the Behavior-Driven Development scenario automation patterns
 * described in the book "Effective Behavior-Driven Development" by
 * Gaspar Nagy and Seb Rose.
 *
 * It is NOT a complete or production-ready implementation. It deliberately
 * uses shortcuts and simplifications (e.g. authentication, data storage,
 * error handling, security) that are NOT suitable for a real application.
 * Do not use this code as a basis for production software.
 */

package com.wimp.app.data;

import com.wimp.app.data.entities.ContactDetailsEmbeddable;
import com.wimp.app.data.entities.CouponEntity;
import com.wimp.app.data.entities.CustomerEntity;
import com.wimp.app.data.entities.DailyPizzaSalesEntity;
import com.wimp.app.data.entities.MenuItemEntity;
import com.wimp.app.data.entities.NotificationEntity;
import com.wimp.app.data.entities.OrderCollectionDetailsEmbeddable;
import com.wimp.app.data.entities.OrderEntity;
import com.wimp.app.data.entities.PizzaItemEmbeddable;
import com.wimp.app.data.entities.PromotionEntity;
import com.wimp.app.data.entities.SessionEntity;
import com.wimp.app.models.ContactDetails;
import com.wimp.app.models.Coupon;
import com.wimp.app.models.Customer;
import com.wimp.app.models.DailyPizzaSales;
import com.wimp.app.models.DeliveryMethod;
import com.wimp.app.models.MenuItem;
import com.wimp.app.models.Notification;
import com.wimp.app.models.Order;
import com.wimp.app.models.OrderCollectionDetails;
import com.wimp.app.models.OrderStatus;
import com.wimp.app.models.PizzaItem;
import com.wimp.app.models.Promotion;
import com.wimp.app.models.Session;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

@Repository
public class DataRepository {
    @PersistenceContext
    private EntityManager entityManager;

    public Customer getCustomerByName(String customerName) {
        return entityManager.createQuery(
                "select c from CustomerEntity c where lower(c.name) = :name",
                CustomerEntity.class)
            .setParameter("name", customerName.toLowerCase(Locale.ROOT))
            .getResultStream()
            .findFirst()
            .map(this::toCustomer)
            .orElse(null);
    }

    public void insertCustomer(Customer customer) {
        entityManager.persist(toEntity(customer));
    }

    public void insertSession(Session session) {
        entityManager.persist(toEntity(session));
    }

    public void deleteSession(String token) {
        SessionEntity entity = entityManager.find(SessionEntity.class, token);
        if (entity != null) {
            entityManager.remove(entity);
        }
    }

    public Session getSessionByToken(String token) {
        SessionEntity entity = entityManager.find(SessionEntity.class, token);
        return entity == null ? null : toSession(entity);
    }

    public Order insertOrder(Order order) {
        OrderEntity entity = toEntity(order);
        entityManager.persist(entity);
        entityManager.flush();
        order.setOrderNo(entity.getOrderNo());
        return order;
    }

    public Order getOrderByOrderNo(int orderNo) {
        OrderEntity entity = entityManager.find(OrderEntity.class, orderNo);
        return entity == null ? null : toOrder(entity);
    }

    public Order updateOrder(Order order, OrderStatus status, String deliveryAddress, DeliveryMethod deliveryMethod, OrderCollectionDetails collectionDetails) {
        OrderEntity entity = entityManager.find(OrderEntity.class, order.getOrderNo());
        if (entity == null) {
            return order;
        }

        if (status != null) {
            entity.setStatus(status);
            order.setStatus(status);
        }
        if (deliveryAddress != null) {
            entity.setDeliveryAddress(deliveryAddress);
            order.setDeliveryAddress(deliveryAddress);
        }
        if (deliveryMethod != null) {
            entity.setDeliveryMethod(deliveryMethod);
            order.setDeliveryMethod(deliveryMethod);
        }
        if (collectionDetails != null) {
            entity.setOrderCollectionDetails(toEmbeddable(collectionDetails));
            order.setOrderCollectionDetails(collectionDetails);
        }

        entityManager.merge(entity);
        return order;
    }

    public List<Order> getOrdersByStatus(OrderStatus orderStatus) {
        return entityManager.createQuery(
                "select o from OrderEntity o where o.status = :status order by o.placingTime",
                OrderEntity.class)
            .setParameter("status", orderStatus)
            .getResultList()
            .stream()
            .map(this::toOrder)
            .toList();
    }

    public void insertNotification(Notification notification) {
        entityManager.persist(toEntity(notification));
    }

    public List<Notification> getNotificationsByCustomerName(String customerName) {
        return entityManager.createQuery(
                "select n from NotificationEntity n where lower(n.customerName) = :name",
                NotificationEntity.class)
            .setParameter("name", customerName.toLowerCase(Locale.ROOT))
            .getResultList()
            .stream()
            .map(this::toNotification)
            .toList();
    }

    public void insertCoupon(Coupon coupon) {
        entityManager.persist(toEntity(coupon));
    }

    public List<Coupon> getCouponsByEmail(String customerEmail) {
        return entityManager.createQuery(
                "select c from CouponEntity c where lower(c.customerEmail) = :email",
                CouponEntity.class)
            .setParameter("email", customerEmail.toLowerCase(Locale.ROOT))
            .getResultList()
            .stream()
            .map(this::toCoupon)
            .toList();
    }

    public void insertPromotion(Promotion promotion) {
        entityManager.persist(toEntity(promotion));
    }

    public Promotion getPromotionByName(String name) {
        return entityManager.createQuery(
                "select p from PromotionEntity p where lower(p.name) = :name",
                PromotionEntity.class)
            .setParameter("name", name.toLowerCase(Locale.ROOT))
            .getResultStream()
            .findFirst()
            .map(this::toPromotion)
            .orElse(null);
    }

    public Promotion updatePromotion(Promotion promotion, Boolean isActive) {
        PromotionEntity entity = entityManager.find(PromotionEntity.class, promotion.getName());
        if (entity == null) {
            return promotion;
        }

        if (isActive != null) {
            entity.setActive(isActive);
            promotion.setActive(isActive);
        }

        entityManager.merge(entity);
        return promotion;
    }

    public void insertDailyPizzaSalesEntries(List<DailyPizzaSales> salesEntries) {
        for (DailyPizzaSales salesEntry : salesEntries) {
            entityManager.persist(toEntity(salesEntry));
        }
    }

    public List<DailyPizzaSales> getDailyPizzaSalesBetweenDates(LocalDate startDay, LocalDate endDay) {
        return entityManager.createQuery(
                "select d from DailyPizzaSalesEntity d where d.date between :startDay and :endDay",
                DailyPizzaSalesEntity.class)
            .setParameter("startDay", startDay)
            .setParameter("endDay", endDay)
            .getResultList()
            .stream()
            .map(this::toDailyPizzaSales)
            .toList();
    }

    public void insertMenuItem(MenuItem menuItem) {
        entityManager.persist(toEntity(menuItem));
    }

    public void setMenuItems(List<MenuItem> menuItems) {
        entityManager.createQuery("delete from MenuItemEntity").executeUpdate();
        for (MenuItem menuItem : menuItems) {
            entityManager.persist(toEntity(menuItem));
        }
    }

    public List<MenuItem> getMenuItems() {
        return entityManager.createQuery("select m from MenuItemEntity m order by m.name", MenuItemEntity.class)
            .getResultList()
            .stream()
            .map(this::toMenuItem)
            .toList();
    }

    private CustomerEntity toEntity(Customer customer) {
        CustomerEntity entity = new CustomerEntity();
        entity.setName(customer.name());
        entity.setEmail(customer.email());
        return entity;
    }

    private Customer toCustomer(CustomerEntity entity) {
        return new Customer(entity.getName(), entity.getEmail());
    }

    private SessionEntity toEntity(Session session) {
        SessionEntity entity = new SessionEntity();
        entity.setToken(session.token());
        entity.setCustomerName(session.customerName());
        return entity;
    }

    private Session toSession(SessionEntity entity) {
        return new Session(entity.getToken(), entity.getCustomerName());
    }

    private OrderEntity toEntity(Order order) {
        OrderEntity entity = new OrderEntity();
        entity.setOrderNo(order.getOrderNo() == 0 ? null : order.getOrderNo());
        entity.setCustomerName(order.getCustomerName());
        entity.setCustomerEmail(order.getCustomerEmail());
        entity.setItems(order.getItems().stream().map(this::toEmbeddable).toList());
        entity.setDeliveryAddress(order.getDeliveryAddress());
        entity.setStatus(order.getStatus());
        entity.setStatusMessage(order.getStatusMessage());
        entity.setPlacingTime(order.getPlacingTime());
        entity.setExpectedDeliveryTime(order.getExpectedDeliveryTime());
        entity.setPrice(order.getPrice());
        entity.setDeliveryMethod(order.getDeliveryMethod());
        entity.setOrderCollectionDetails(order.getOrderCollectionDetails() == null ? null : toEmbeddable(order.getOrderCollectionDetails()));
        entity.setContactDetails(order.getContactDetails() == null ? null : toEmbeddable(order.getContactDetails()));
        return entity;
    }

    private Order toOrder(OrderEntity entity) {
        Order order = new Order();
        order.setOrderNo(entity.getOrderNo());
        order.setCustomerName(entity.getCustomerName());
        order.setCustomerEmail(entity.getCustomerEmail());
        order.setItems(entity.getItems().stream().map(this::toDomain).toList());
        order.setDeliveryAddress(entity.getDeliveryAddress());
        order.setStatus(entity.getStatus());
        order.setStatusMessage(entity.getStatusMessage());
        order.setPlacingTime(entity.getPlacingTime());
        order.setExpectedDeliveryTime(entity.getExpectedDeliveryTime());
        order.setPrice(entity.getPrice());
        order.setDeliveryMethod(entity.getDeliveryMethod());
        order.setOrderCollectionDetails(entity.getOrderCollectionDetails() == null ? null : toDomain(entity.getOrderCollectionDetails()));
        order.setContactDetails(entity.getContactDetails() == null ? null : toDomain(entity.getContactDetails()));
        return order;
    }

    private NotificationEntity toEntity(Notification notification) {
        NotificationEntity entity = new NotificationEntity();
        entity.setCustomerName(notification.customerName());
        entity.setMessage(notification.message());
        entity.setSentAt(notification.sentAt());
        return entity;
    }

    private Notification toNotification(NotificationEntity entity) {
        return new Notification(entity.getCustomerName(), entity.getMessage(), entity.getSentAt());
    }

    private CouponEntity toEntity(Coupon coupon) {
        CouponEntity entity = new CouponEntity();
        entity.setCode(coupon.getCode());
        entity.setCustomerEmail(coupon.getCustomerEmail());
        entity.setSentAt(coupon.getSentAt());
        return entity;
    }

    private Coupon toCoupon(CouponEntity entity) {
        Coupon coupon = new Coupon();
        coupon.setCode(entity.getCode());
        coupon.setCustomerEmail(entity.getCustomerEmail());
        coupon.setSentAt(entity.getSentAt());
        return coupon;
    }

    private PromotionEntity toEntity(Promotion promotion) {
        PromotionEntity entity = new PromotionEntity();
        entity.setName(promotion.getName());
        entity.setActive(promotion.isActive());
        return entity;
    }

    private Promotion toPromotion(PromotionEntity entity) {
        Promotion promotion = new Promotion();
        promotion.setName(entity.getName());
        promotion.setActive(entity.isActive());
        return promotion;
    }

    private DailyPizzaSalesEntity toEntity(DailyPizzaSales sales) {
        DailyPizzaSalesEntity entity = new DailyPizzaSalesEntity();
        entity.setDate(sales.getDate());
        entity.setPizza(sales.getPizza());
        entity.setSales(sales.getSales());
        return entity;
    }

    private DailyPizzaSales toDailyPizzaSales(DailyPizzaSalesEntity entity) {
        DailyPizzaSales sales = new DailyPizzaSales();
        sales.setDate(entity.getDate());
        sales.setPizza(entity.getPizza());
        sales.setSales(entity.getSales());
        return sales;
    }

    private MenuItemEntity toEntity(MenuItem menuItem) {
        MenuItemEntity entity = new MenuItemEntity();
        entity.setName(menuItem.getName());
        entity.setPrice(menuItem.getPrice());
        entity.setCalories(menuItem.getCalories());
        entity.setVegetarian(menuItem.isVegetarian());
        return entity;
    }

    private MenuItem toMenuItem(MenuItemEntity entity) {
        MenuItem menuItem = new MenuItem();
        menuItem.setName(entity.getName());
        menuItem.setPrice(entity.getPrice());
        menuItem.setCalories(entity.getCalories());
        menuItem.setVegetarian(entity.isVegetarian());
        return menuItem;
    }

    private PizzaItemEmbeddable toEmbeddable(PizzaItem item) {
        PizzaItemEmbeddable embeddable = new PizzaItemEmbeddable();
        embeddable.setName(item.name());
        embeddable.setSize(item.size());
        embeddable.setStyle(item.style());
        return embeddable;
    }

    private PizzaItem toDomain(PizzaItemEmbeddable item) {
        return new PizzaItem(
            item.getName(),
            item.getSize(),
            item.getStyle());
    }

    private OrderCollectionDetailsEmbeddable toEmbeddable(OrderCollectionDetails details) {
        OrderCollectionDetailsEmbeddable embeddable = new OrderCollectionDetailsEmbeddable();
        embeddable.setOrderNo(details.getOrderNo());
        embeddable.setBoxesToBeCollected(details.getBoxesToBeCollected());
        embeddable.setContactDetailsConfirmationRequested(details.isContactDetailsConfirmationRequested());
        return embeddable;
    }

    private OrderCollectionDetails toDomain(OrderCollectionDetailsEmbeddable details) {
        OrderCollectionDetails collectionDetails = new OrderCollectionDetails();
        collectionDetails.setOrderNo(details.getOrderNo());
        collectionDetails.setBoxesToBeCollected(details.getBoxesToBeCollected());
        collectionDetails.setContactDetailsConfirmationRequested(Boolean.TRUE.equals(details.getContactDetailsConfirmationRequested()));
        return collectionDetails;
    }

    private ContactDetailsEmbeddable toEmbeddable(ContactDetails details) {
        ContactDetailsEmbeddable embeddable = new ContactDetailsEmbeddable();
        embeddable.setName(details.getName());
        embeddable.setEmail(details.getEmail());
        embeddable.setPhone(details.getPhone());
        embeddable.setCountry(details.getCountry());
        embeddable.setState(details.getState());
        return embeddable;
    }

    private ContactDetails toDomain(ContactDetailsEmbeddable details) {
        ContactDetails contactDetails = new ContactDetails();
        contactDetails.setName(details.getName());
        contactDetails.setEmail(details.getEmail());
        contactDetails.setPhone(details.getPhone());
        contactDetails.setCountry(details.getCountry());
        contactDetails.setState(details.getState());
        return contactDetails;
    }
}
