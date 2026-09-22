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

package com.wimp.app.services;

import com.wimp.app.data.DataRepository;
import com.wimp.app.models.ContactDetails;
import com.wimp.app.models.DeliveryMethod;
import com.wimp.app.models.MenuItem;
import com.wimp.app.models.Order;
import com.wimp.app.models.OrderCollectionDetails;
import com.wimp.app.models.OrderStatus;
import com.wimp.app.models.PizzaItem;
import com.wimp.app.models.PizzaSize;
import com.wimp.app.models.ServiceResult;
import com.wimp.app.config.AppConfigurationProvider;
import com.wimp.app.restapi.PlaceOrderRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
public class OrderService {
    private static final int DELIVERY_TIME_MINUTES = 45;
    private static final int MAX_LARGE_PIZZAS_PER_ORDER = 4;
    private static final Logger log = LoggerFactory.getLogger(OrderService.class);

    private final DataRepository repository;
    private final PromotionService promotionService;
    private final NotificationService notificationService;
    private final EmailService emailService;
    private final TimeService timeService;
    private final MenuService menuService;
    private final MessageService messageService;
    private final AppConfigurationProvider appConfigurationProvider;

    public OrderService(
        DataRepository repository,
        PromotionService promotionService,
        NotificationService notificationService,
        EmailService emailService,
        TimeService timeService,
        MenuService menuService,
        MessageService messageService,
        AppConfigurationProvider appConfigurationProvider
    ) {
        this.repository = repository;
        this.promotionService = promotionService;
        this.notificationService = notificationService;
        this.emailService = emailService;
        this.timeService = timeService;
        this.menuService = menuService;
        this.messageService = messageService;
        this.appConfigurationProvider = appConfigurationProvider;
    }

    public ServiceResult<Order> placeOrder(String customerName, PlaceOrderRequest placeOrderRequest) {
        var items = List.of(placeOrderRequest.items());
        int largePizzaCount = (int) items.stream().filter(item -> item.size() == PizzaSize.LARGE).count();

        LocalDateTime placingTime;
        LocalDateTime expectedDeliveryTime;
        if (appConfigurationProvider.backdoor().allowOverridingOrderTimes()) {
            placingTime = placeOrderRequest.placingTime() != null ? placeOrderRequest.placingTime() : timeService.getCurrentTime();
            expectedDeliveryTime = placeOrderRequest.expectedDeliveryTime() != null
                ? placeOrderRequest.expectedDeliveryTime()
                : placingTime.plusMinutes(DELIVERY_TIME_MINUTES);
        } else {
            placingTime = timeService.getCurrentTime();
            expectedDeliveryTime = placingTime.plusMinutes(DELIVERY_TIME_MINUTES);
        }

        if (largePizzaCount > MAX_LARGE_PIZZAS_PER_ORDER) {
            log.info("Rejecting order because of size limit");
            return ServiceResult.failure(messageService.getMessage("en-US", "cannot-deliver-too-many-large-pizzas", largePizzaCount));
        }

        BigDecimal price;
        if (appConfigurationProvider.backdoor().disableMenuIntegrityCheck()) {
            price = BigDecimal.valueOf(items.size()).multiply(BigDecimal.valueOf(12));
        } else {
            List<? extends MenuItem> menu = menuService.loadMenu();
            price = BigDecimal.ZERO;
            for (PizzaItem orderItem : items) {
                MenuItem menuItem = menu.stream()
                    .filter(mi -> mi.getName().equalsIgnoreCase(orderItem.name()))
                    .findFirst()
                    .orElse(null);
                if (menuItem == null) {
                    return ServiceResult.failure("Could not find pizza '" + orderItem.name() + "' on menu.");
                }
                price = price.add(menuItem.getPrice());
            }
        }

        Order order = new Order();
        order.setCustomerName(customerName);
        order.setCustomerEmail(placeOrderRequest.customerEmail());
        order.setItems(items);
        order.setDeliveryAddress(placeOrderRequest.deliveryAddress());
        order.setStatus(OrderStatus.PLACED);
        order.setPlacingTime(placingTime.toLocalTime());
        order.setExpectedDeliveryTime(expectedDeliveryTime);
        order.setPrice(price);
        order = repository.insertOrder(order);

        subscribeForDelayNotification(expectedDeliveryTime, order.getOrderNo(), customerName);
        if (appConfigurationProvider.simulation().orderRejection() && order.getPlacingTime().getSecond() == 30) {
            log.warn("Order {} has been rejected, because of a simulated error", order.getOrderNo());
            order = repository.updateOrder(order, OrderStatus.REJECTED, null, null, null);
        }
        log.info("Order {} placed", order.getOrderNo());
        return ServiceResult.success(order);
    }

    public Order getOrder(int orderNo) {
        return repository.getOrderByOrderNo(orderNo);
    }

    public ServiceResult<Order> cancelOrder(int orderNo, String customerName) {
        Order order = repository.getOrderByOrderNo(orderNo);
        if (order == null) {
            return ServiceResult.failure("Order " + orderNo + " not found.");
        }
        if (!order.getCustomerName().equalsIgnoreCase(customerName)) {
            return ServiceResult.failure("Order " + orderNo + " belongs to a different customer.");
        }
        if (order.getStatus() == OrderStatus.COMPLETED || order.getStatus() == OrderStatus.CANCELLED || order.getStatus() == OrderStatus.REJECTED) {
            return ServiceResult.failure("Order " + orderNo + " cannot be cancelled in its current status.");
        }

        order = repository.updateOrder(order, OrderStatus.CANCELLED, null, null, null);
        notificationService.sendCancellationNotification(customerName);
        return ServiceResult.success(order);
    }

    public ServiceResult<Order> setForCustomerCollection(int orderNo, String customerName) {
        Order order = repository.getOrderByOrderNo(orderNo);
        if (order == null) {
            return ServiceResult.failure("Order " + orderNo + " not found.");
        }
        if (!order.getCustomerName().equalsIgnoreCase(customerName)) {
            return ServiceResult.failure("Order " + orderNo + " belongs to a different customer.");
        }
        if (order.getDeliveryMethod() == DeliveryMethod.CUSTOMER_COLLECTION) {
            return ServiceResult.failure("Order " + orderNo + " is already set for customer-collection.");
        }
        if (order.getStatus() != OrderStatus.PLACED && order.getStatus() != OrderStatus.IN_PREPARATION) {
            return ServiceResult.failure("Order " + orderNo + " cannot be set to customer-collection in its current status.");
        }

        OrderCollectionDetails collectionDetails = new OrderCollectionDetails();
        collectionDetails.setOrderNo(orderNo);
        collectionDetails.setBoxesToBeCollected(order.getItems().size());
        collectionDetails.setContactDetailsConfirmationRequested(true);

        order = repository.updateOrder(order, null, null, DeliveryMethod.CUSTOMER_COLLECTION, collectionDetails);
        return ServiceResult.success(order);
    }

    public ServiceResult<Order> provideContactDetails(int orderNo, ContactDetails contactDetails) {
        Order order = repository.getOrderByOrderNo(orderNo);
        if (order == null) {
            return ServiceResult.failure("Order " + orderNo + " not found.");
        }

        String validationErrorMessage = validateContactDetails(contactDetails);
        if (validationErrorMessage == null && order.getDeliveryMethod() == DeliveryMethod.CUSTOMER_COLLECTION) {
            validationErrorMessage = validateCustomerCollectionContactDetails(contactDetails);
        }

        if (validationErrorMessage != null) {
            return ServiceResult.failure(validationErrorMessage);
        }

        order.setContactDetails(contactDetails);
        return ServiceResult.success(order);
    }

    private String validateCustomerCollectionContactDetails(ContactDetails contactDetails) {
        if ((contactDetails.getEmail() == null || contactDetails.getEmail().isBlank())
            && (contactDetails.getPhone() == null || contactDetails.getPhone().isBlank())) {
            return "For customer collection email or phone must be specified";
        }
        return null;
    }

    private String validateContactDetails(ContactDetails contactDetails) {
        if (contactDetails.getName() == null || contactDetails.getName().isBlank()) {
            return "Name not specified";
        }
        if (contactDetails.getEmail() != null && !contactDetails.getEmail().isBlank() && !isValidEmail(contactDetails.getEmail())) {
            return "Wrong email format";
        }
        if (contactDetails.getPhone() != null && !contactDetails.getPhone().isBlank() && !isValidPhone(contactDetails.getPhone())) {
            return "Wrong phone number format";
        }
        if ("US".equals(contactDetails.getCountry()) && (contactDetails.getState() == null || contactDetails.getState().isBlank() || "-".equals(contactDetails.getState()))) {
            return "For US country the state must be specified";
        }
        return null;
    }

    private boolean isValidPhone(String phone) {
        return phone.length() >= 6 && phone.chars().allMatch(Character::isDigit);
    }

    private boolean isValidEmail(String email) {
        return email.contains("@") && email.indexOf('@') > 0 && email.lastIndexOf('.') > email.indexOf('@');
    }

    public ServiceResult<Order> deliverOrder(int orderNo) {
        Order order = repository.getOrderByOrderNo(orderNo);
        if (order == null) {
            return ServiceResult.failure("Order " + orderNo + " not found.");
        }

        order = repository.updateOrder(order, OrderStatus.COMPLETED, null, null, null);

        boolean margheritaOrder = order.getItems().stream().anyMatch(item -> item.name().equalsIgnoreCase("Margherita"));
        if (margheritaOrder && promotionService.isPromotionActive("Margherita Friday")) {
            emailService.sendCoupon(order.getCustomerEmail(), "MARGHERITA25");
        }

        return ServiceResult.success(order);
    }

    public ServiceResult<Order> changeDeliveryAddress(int orderNo, String newAddress) {
        Order order = repository.getOrderByOrderNo(orderNo);
        if (order == null) {
            return ServiceResult.failure("Order " + orderNo + " not found.");
        }

        if (order.getStatus().compareTo(OrderStatus.WAITING_FOR_PICKUP) > 0) {
            return ServiceResult.failure("Delivery address can only be changed before the order is picked up.");
        }

        order = repository.updateOrder(order, null, newAddress, null, null);
        return ServiceResult.success(order);
    }

    public ServiceResult<Order> setWaitingForPickup(int orderNo) {
        Order order = repository.getOrderByOrderNo(orderNo);
        if (order == null) {
            return ServiceResult.failure("Order " + orderNo + " not found.");
        }
        if (order.getStatus() != OrderStatus.IN_PREPARATION) {
            return ServiceResult.failure("Order " + orderNo + " must be in preparation before it can wait for pickup.");
        }

        order = repository.updateOrder(order, OrderStatus.WAITING_FOR_PICKUP, null, null, null);
        return ServiceResult.success(order);
    }

    public Order takeNextOrder() {
        Order next = repository.getOrdersByStatus(OrderStatus.PLACED).stream().min(
                appConfigurationProvider.simulation().bug()
                    ? Comparator.comparing(Order::getPlacingTime).reversed()
                    : Comparator.comparing(Order::getPlacingTime))
            .orElse(null);
        if (next == null) {
            return null;
        }
        next = repository.updateOrder(next, OrderStatus.IN_PREPARATION, null, null, null);
        log.info("Taken next order: {}", next.getOrderNo());
        return next;
    }

    private void subscribeForDelayNotification(LocalDateTime expectedDeliveryTime, int orderNo, String customerName) {
        timeService.subscribeToTimeChange(time -> {
            if (time.isBefore(expectedDeliveryTime)) {
                return false;
            }

            Order order = repository.getOrderByOrderNo(orderNo);
            if (order != null && order.getStatus().compareTo(OrderStatus.COMPLETED) < 0) {
                notificationService.sendDelayNotification(customerName);
            }
            return true;
        });
    }
}
