package com.kiosk.dashboard;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SalesRepository extends JpaRepository<OrderEntity, Integer> {

    @Query(value = "SELECT " +
            "    EXTRACT(HOUR FROM order_time) AS hourTime, " +
            "    COUNT(DISTINCT order_id) AS orderCount, " +
            "    SUM(total_price) AS totalSales " +
            "FROM orders " +
            "GROUP BY EXTRACT(HOUR FROM order_time) " +
            "ORDER BY hourTime ASC", nativeQuery = true)

    List<HoursalesDto> findHourSalesPeak();

    @Query(value = "SELECT " +
            "    TO_CHAR(o.order_time, 'YYYY-MM') AS yearMonth, " +
            "    s.store_name AS storeName, " +
            "    SUM(o.total_price) AS monthSales " +
            "FROM orders o " +
            "JOIN stores s ON o.store_id = s.store_id " +
            "GROUP BY TO_CHAR(o.order_time, 'YYYY-MM'), s.store_name " +
            "ORDER BY yearMonth ASC, monthSales DESC", nativeQuery = true)

    List<MonthStoreSalesDto> findMonthStoreSales();

    @Query(value = "WITH menu_sales AS ( " +
            "    SELECT m.category, m.menu_name, SUM(od.quantity) AS total_qty " +
            "    FROM order_details od " +
            "    JOIN menus m ON od.menu_id = m.menu_id " +
            "    GROUP BY m.category, m.menu_name " +
            ") " +
            "SELECT " +
            "    category, menu_name AS menuName, total_qty AS totalQty, " +
            "    DENSE_RANK() OVER (PARTITION BY category ORDER BY total_qty DESC) AS categoryRank " +
            "FROM menu_sales " +
            "ORDER BY category ASC, categoryRank ASC", nativeQuery = true)

    List<CategoryTopMenuDto> findCategoryTopMenus();
}
