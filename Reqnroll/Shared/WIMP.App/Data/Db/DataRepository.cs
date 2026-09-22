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

using System.Text.Json;

using Microsoft.EntityFrameworkCore;

using WIMP.App.Data.Db.DbRecords;
using WIMP.App.Models;

namespace WIMP.App.Data.Db;

public class DataRepository(IDbContextFactory<WimpDbContext> dbContextFactory) : IDataRepository
{
    public Customer? GetCustomerByName(string customerName)
    {
        using var db = dbContextFactory.CreateDbContext();
        var customer = db.CustomerRows
            .FirstOrDefault(c => c.Name.ToUpper() == customerName.ToUpper());

        return customer is null
            ? null
            : new Customer
            {
                Name = customer.Name,
                Email = customer.Email
            };
    }

    public void InsertCustomer(Customer customer)
    {
        using var db = dbContextFactory.CreateDbContext();
        db.CustomerRows.Add(new CustomerDbRecord
        {
            Id = Guid.NewGuid(),
            Name = customer.Name,
            Email = customer.Email
        });
        db.SaveChanges();
    }

    public void InsertSession(Session session)
    {
        using var db = dbContextFactory.CreateDbContext();
        db.SessionRows.Add(new SessionDbRecord
        {
            Token = session.Token,
            CustomerName = session.CustomerName
        });
        db.SaveChanges();
    }

    public void DeleteSession(string token)
    {
        using var db = dbContextFactory.CreateDbContext();
        var session = db.SessionRows.FirstOrDefault(s => s.Token == token);
        if (session is not null)
        {
            db.SessionRows.Remove(session);
            db.SaveChanges();
        }
    }

    public Session? GetSessionByToken(string token)
    {
        using var db = dbContextFactory.CreateDbContext();
        var session = db.SessionRows.FirstOrDefault(s => s.Token == token);
        return session is null ? null : new Session(session.Token, session.CustomerName);
    }

    public void InsertCoupon(Coupon coupon)
    {
        using var db = dbContextFactory.CreateDbContext();
        db.CouponRows.Add(new CouponDbRecord
        {
            Id = Guid.NewGuid(),
            Code = coupon.Code,
            CustomerEmail = coupon.CustomerEmail,
            SentAt = coupon.SentAt
        });
        db.SaveChanges();
    }

    public IReadOnlyCollection<Coupon> GetCouponsByEmail(string customerEmail)
    {
        using var db = dbContextFactory.CreateDbContext();
        return db.CouponRows
            .Where(c => c.CustomerEmail.ToUpper() == customerEmail.ToUpper())
            .Select(c => new Coupon
            {
                Code = c.Code,
                CustomerEmail = c.CustomerEmail,
                SentAt = c.SentAt
            })
            .ToArray();
    }

    public void InsertNotification(Notification notification)
    {
        using var db = dbContextFactory.CreateDbContext();
        db.NotificationRows.Add(new NotificationDbRecord
        {
            Id = Guid.NewGuid(),
            CustomerName = notification.CustomerName,
            Message = notification.Message,
            SentAt = notification.SentAt
        });
        db.SaveChanges();
    }

    public IReadOnlyCollection<Notification> GetNotificationsByCustomerName(string customerName)
    {
        using var db = dbContextFactory.CreateDbContext();
        return db.NotificationRows
            .Where(c => c.CustomerName.ToUpper() == customerName.ToUpper())
            .Select(c => new Notification
            {
                CustomerName = c.CustomerName,
                Message = c.Message,
                SentAt = c.SentAt
            })
            .ToArray();
    }

    public void InsertPromotion(Promotion promotion)
    {
        using var db = dbContextFactory.CreateDbContext();
        db.PromotionRows.Add(new PromotionDbRecord
        {
            Id = Guid.NewGuid(),
            Name = promotion.Name,
            IsActive = promotion.IsActive
        });
        db.SaveChanges();
    }

    public Promotion? GetPromotionByName(string name)
    {
        using var db = dbContextFactory.CreateDbContext();
        var promotion = db.PromotionRows
            .FirstOrDefault(c => c.Name.ToUpper() == name.ToUpper());

        return promotion is null
            ? null
            : new Promotion
            {
                Name = promotion.Name,
                IsActive = promotion.IsActive
            };
    }

    public Promotion UpdatePromotion(Promotion promotion, bool? isActive = null)
    {
        if (isActive is not null)
        {
            promotion.IsActive = isActive.Value;
        }

        using var db = dbContextFactory.CreateDbContext();
        var dbPromotion = db.PromotionRows
            .FirstOrDefault(c => c.Name.ToUpper() == promotion.Name.ToUpper());

        if (dbPromotion is not null)
        {
            dbPromotion.IsActive = promotion.IsActive;
            db.SaveChanges();
        }

        return promotion;
    }

    public Order InsertOrder(Order order)
    {
        using var db = dbContextFactory.CreateDbContext();
        order.OrderNo = (db.OrderRows.Max(o => (int?)o.OrderNo) ?? 999) + 1;
        db.OrderRows.Add(ToOrderDbRecord(order));
        db.SaveChanges();
        return order;
    }

    public Order? GetOrderByOrderNo(int orderNo)
    {
        using var db = dbContextFactory.CreateDbContext();
        var order = db.OrderRows.FirstOrDefault(o => o.OrderNo == orderNo);
        return order is null ? null : ToOrderModel(order);
    }

    public Order UpdateOrder(Order order, OrderStatus? status = null, string? deliveryAddress = null,
        DeliveryMethod? deliveryMethod = null, OrderCollectionDetails? collectionDetails = null)
    {
        if (status is not null)
        {
            order.Status = status.Value;
        }

        if (deliveryAddress is not null)
        {
            order.DeliveryAddress = deliveryAddress;
        }

        if (deliveryMethod is not null)
        {
            order.DeliveryMethod = deliveryMethod.Value;
        }

        if (collectionDetails is not null)
        {
            order.OrderCollectionDetails = collectionDetails;
        }

        using var db = dbContextFactory.CreateDbContext();
        var dbOrder = db.OrderRows.FirstOrDefault(o => o.OrderNo == order.OrderNo);
        if (dbOrder is not null)
        {
            dbOrder.CustomerName = order.CustomerName;
            dbOrder.CustomerEmail = order.CustomerEmail;
            dbOrder.ItemsJson = JsonSerializer.Serialize(order.Items);
            dbOrder.DeliveryAddress = order.DeliveryAddress;
            dbOrder.Status = order.Status;
            dbOrder.StatusMessage = order.StatusMessage;
            dbOrder.PlacingTime = order.PlacingTime;
            dbOrder.Price = order.Price;
            dbOrder.DeliveryMethod = order.DeliveryMethod;
            dbOrder.CollectionDetailsJson = order.OrderCollectionDetails is null
                ? null
                : JsonSerializer.Serialize(order.OrderCollectionDetails);
            db.SaveChanges();
        }

        return order;
    }

    public IReadOnlyCollection<Order> GetOrdersByStatus(OrderStatus orderStatus)
    {
        using var db = dbContextFactory.CreateDbContext();
        return db.OrderRows
            .Where(o => o.Status == orderStatus)
            .AsEnumerable()
            .Select(ToOrderModel)
            .ToArray();
    }

    public void InsertDailyPizzaSalesEntries(IEnumerable<DailyPizzaSales> salesEntries)
    {
        using var db = dbContextFactory.CreateDbContext();
        db.DailyPizzaSalesRows.AddRange(salesEntries.Select(entry => new DailyPizzaSalesDbRecord
        {
            Id = Guid.NewGuid(),
            Date = entry.Date,
            Pizza = entry.Pizza,
            Sales = entry.Sales
        }));
        db.SaveChanges();
    }

    public IReadOnlyCollection<DailyPizzaSales> GetDailyPizzaSalesBetweenDates(DateOnly startDay, DateOnly endDay)
    {
        using var db = dbContextFactory.CreateDbContext();
        return db.DailyPizzaSalesRows
            .Where(e => e.Date >= startDay && e.Date <= endDay)
            .Select(e => new DailyPizzaSales
            {
                Date = e.Date,
                Pizza = e.Pizza,
                Sales = e.Sales
            })
            .ToArray();
    }

    public void InsertMenuItem(MenuItem item)
    {
        var db = dbContextFactory.CreateDbContext();
        db.MenuRows.Add(new MenuDbRecord
        {
            Name = item.Name,
            Price = item.Price,
            Calories = item.Calories,
            Vegetarian = item.Vegetarian
        });
        db.SaveChanges();
    }

    public void SetMenuItems(IEnumerable<MenuItem> menuItems)
    {
        using var db = dbContextFactory.CreateDbContext();
        db.MenuRows.RemoveRange(db.MenuRows);

        var rowsToInsert = menuItems
            .Select(item => new MenuDbRecord
            {
                Name = item.Name,
                Price = item.Price,
                Calories = item.Calories,
                Vegetarian = item.Vegetarian
            })
            .ToList();
        db.MenuRows.AddRange(rowsToInsert);
        db.SaveChanges();
    }

    public IReadOnlyCollection<MenuItem> GetMenuItems()
    {
        using var db = dbContextFactory.CreateDbContext();
        return db.MenuRows
            .Select(menuDbRecord => new MenuItem
            {
                Name = menuDbRecord.Name,
                Price = menuDbRecord.Price,
                Calories = menuDbRecord.Calories,
                Vegetarian = menuDbRecord.Vegetarian
            })
            .ToArray();
    }

    private static OrderDbRecord ToOrderDbRecord(Order order)
    {
        return new OrderDbRecord
        {
            OrderNo = order.OrderNo,
            CustomerName = order.CustomerName,
            CustomerEmail = order.CustomerEmail,
            ItemsJson = JsonSerializer.Serialize(order.Items),
            DeliveryAddress = order.DeliveryAddress,
            Status = order.Status,
            StatusMessage = order.StatusMessage,
            PlacingTime = order.PlacingTime,
            Price = order.Price,
            DeliveryMethod = order.DeliveryMethod,
            CollectionDetailsJson = order.OrderCollectionDetails is null
                ? null
                : JsonSerializer.Serialize(order.OrderCollectionDetails)
        };
    }

    private static Order ToOrderModel(OrderDbRecord row)
    {
        return new Order
        {
            OrderNo = row.OrderNo,
            CustomerName = row.CustomerName,
            CustomerEmail = row.CustomerEmail,
            Items = JsonSerializer.Deserialize<List<PizzaItem>>(row.ItemsJson) ?? [],
            DeliveryAddress = row.DeliveryAddress,
            Status = row.Status,
            StatusMessage = row.StatusMessage,
            PlacingTime = row.PlacingTime,
            Price = row.Price,
            DeliveryMethod = row.DeliveryMethod,
            OrderCollectionDetails = row.CollectionDetailsJson is null
                ? null
                : JsonSerializer.Deserialize<OrderCollectionDetails>(row.CollectionDetailsJson)
        };
    }
}
