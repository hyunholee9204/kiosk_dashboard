package com.kiosk.dashboard;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/sales")
@RequiredArgsConstructor
public class SalesController {

    private final SalesRepository salesRepository;

    @GetMapping("/hour")
    public List<HoursalesDto> getHourSales() {
        return salesRepository.findHourSalesPeak();
    }

    @GetMapping("/month-store")
    public List<MonthStoreSalesDto> getMonthStoreSales() {
        return salesRepository.findMonthStoreSales();
    }

    @GetMapping("/category-rank")
    public List<CategoryTopMenuDto> getCategoryRank() {
        return salesRepository.findCategoryTopMenus();
    }
}
