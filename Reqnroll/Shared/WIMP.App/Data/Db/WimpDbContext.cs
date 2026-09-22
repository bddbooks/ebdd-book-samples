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

using Microsoft.EntityFrameworkCore;

using WIMP.App.Data.Db.DbRecords;

namespace WIMP.App.Data.Db;

public sealed class WimpDbContext(DbContextOptions<WimpDbContext> options)
    : DbContext(options)
{
    public DbSet<CustomerDbRecord> CustomerRows => Set<CustomerDbRecord>();
    public DbSet<SessionDbRecord> SessionRows => Set<SessionDbRecord>();
    public DbSet<CouponDbRecord> CouponRows => Set<CouponDbRecord>();
    public DbSet<NotificationDbRecord> NotificationRows => Set<NotificationDbRecord>();
    public DbSet<PromotionDbRecord> PromotionRows => Set<PromotionDbRecord>();
    public DbSet<OrderDbRecord> OrderRows => Set<OrderDbRecord>();
    public DbSet<DailyPizzaSalesDbRecord> DailyPizzaSalesRows => Set<DailyPizzaSalesDbRecord>();
    public DbSet<MenuDbRecord> MenuRows => Set<MenuDbRecord>();

    protected override void OnModelCreating(ModelBuilder modelBuilder)
    {
        modelBuilder.Entity<CustomerDbRecord>(entity =>
        {
            entity.ToTable("CUSTOMER");
            entity.HasKey(customer => customer.Id);
            entity.Property(customer => customer.Id).HasColumnName("ID");
            entity.Property(customer => customer.Name)
                .HasColumnName("NAME")
                .HasMaxLength(100);
            entity.Property(customer => customer.Email)
                .HasColumnName("EMAIL")
                .HasMaxLength(320);
        });

        modelBuilder.Entity<SessionDbRecord>(entity =>
        {
            entity.ToTable("SESSION");
            entity.HasKey(session => session.Token);
            entity.Property(session => session.Token)
                .HasColumnName("TOKEN")
                .HasMaxLength(64);
            entity.Property(session => session.CustomerName)
                .HasColumnName("CUSTOMER_NAME")
                .HasMaxLength(100);
        });

        modelBuilder.Entity<CouponDbRecord>(entity =>
        {
            entity.ToTable("COUPON");
            entity.HasKey(coupon => coupon.Id);
            entity.Property(coupon => coupon.Id).HasColumnName("ID");
            entity.Property(coupon => coupon.Code)
                .HasColumnName("CODE")
                .HasMaxLength(100);
            entity.Property(coupon => coupon.CustomerEmail)
                .HasColumnName("CUSTOMER_EMAIL")
                .HasMaxLength(320);
            entity.Property(coupon => coupon.SentAt)
                .HasColumnName("SENT_AT");
        });

        modelBuilder.Entity<NotificationDbRecord>(entity =>
        {
            entity.ToTable("NOTIFICATION");
            entity.HasKey(notification => notification.Id);
            entity.Property(notification => notification.Id).HasColumnName("ID");
            entity.Property(notification => notification.CustomerName)
                .HasColumnName("CUSTOMER_NAME")
                .HasMaxLength(100);
            entity.Property(notification => notification.Message)
                .HasColumnName("MESSAGE")
                .HasMaxLength(500);
            entity.Property(notification => notification.SentAt)
                .HasColumnName("SENT_AT");
        });

        modelBuilder.Entity<PromotionDbRecord>(entity =>
        {
            entity.ToTable("PROMOTION");
            entity.HasKey(promotion => promotion.Id);
            entity.Property(promotion => promotion.Id).HasColumnName("ID");
            entity.Property(promotion => promotion.Name)
                .HasColumnName("NAME")
                .HasMaxLength(100);
            entity.Property(promotion => promotion.IsActive)
                .HasColumnName("IS_ACTIVE");
        });

        modelBuilder.Entity<OrderDbRecord>(entity =>
        {
            entity.ToTable("ORDER_DATA");
            entity.HasKey(order => order.OrderNo);
            entity.Property(order => order.OrderNo)
                .HasColumnName("ORDER_NO");
            entity.Property(order => order.CustomerName)
                .HasColumnName("CUSTOMER_NAME")
                .HasMaxLength(100);
            entity.Property(order => order.CustomerEmail)
                .HasColumnName("CUSTOMER_EMAIL")
                .HasMaxLength(320);
            entity.Property(order => order.ItemsJson)
                .HasColumnName("ITEMS_JSON")
                .HasColumnType("longtext");
            entity.Property(order => order.DeliveryAddress)
                .HasColumnName("DELIVERY_ADDRESS")
                .HasMaxLength(300);
            entity.Property(order => order.Status)
                .HasColumnName("STATUS");
            entity.Property(order => order.StatusMessage)
                .HasColumnName("STATUS_MESSAGE")
                .HasMaxLength(500);
            entity.Property(order => order.PlacingTime)
                .HasColumnName("PLACING_TIME");
            entity.Property(order => order.Price)
                .HasColumnName("PRICE")
                .HasPrecision(10, 2);
            entity.Property(order => order.DeliveryMethod)
                .HasColumnName("DELIVERY_METHOD");
            entity.Property(order => order.CollectionDetailsJson)
                .HasColumnName("COLLECTION_DETAILS_JSON")
                .HasColumnType("longtext");
        });

        modelBuilder.Entity<DailyPizzaSalesDbRecord>(entity =>
        {
            entity.ToTable("DAILY_PIZZA_SALES");
            entity.HasKey(sales => sales.Id);
            entity.Property(sales => sales.Id).HasColumnName("ID");
            entity.Property(sales => sales.Date)
                .HasColumnName("DATE");
            entity.Property(sales => sales.Pizza)
                .HasColumnName("PIZZA")
                .HasMaxLength(100);
            entity.Property(sales => sales.Sales)
                .HasColumnName("SALES")
                .HasPrecision(10, 2);
        });

        modelBuilder.Entity<MenuDbRecord>(entity =>
        {
            entity.ToTable("MENU");
            entity.HasKey(menuItem => menuItem.Name);
            entity.Property(menuItem => menuItem.Name)
                .HasColumnName("NAME")
                .HasMaxLength(100);
            entity.Property(menuItem => menuItem.Price)
                .HasColumnName("PRICE")
                .HasPrecision(10, 2);
            entity.Property(menuItem => menuItem.Calories)
                .HasColumnName("CAL");
            entity.Property(menuItem => menuItem.Vegetarian)
                .HasColumnName("VEG");
        });
    }
}
