package cn.edu.tju.elm.controller;

import cn.edu.tju.core.model.HttpResult;
import cn.edu.tju.core.model.ResultCodeEnum;
import cn.edu.tju.core.security.service.UserService;
import cn.edu.tju.elm.model.Order;
import cn.edu.tju.core.model.User;
import cn.edu.tju.elm.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter; // 新增: for date parse
import java.util.List;

@RestController
@RequestMapping("/api/orders")
@Tag(name = "管理订单", description = "对订单进行增删改查")
public class OrderController {
    @Autowired
    private UserService userService;

    @Autowired
    private OrderService orderService;

    @PostMapping(value = "")
    @Operation(summary = "用户新增订单")
    public HttpResult<Order> addOrders(@RequestBody Order order) {
        try {
            Order createdOrder = orderService.createOrder(order);
            return HttpResult.success(createdOrder);
        } catch (Exception e) {
            e.printStackTrace();
            return HttpResult.failure(ResultCodeEnum.SERVER_ERROR, "创建订单失败: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取订单详情")
    public HttpResult<Order> getOrderById(@PathVariable Long id) {
        try {
            Order order = orderService.getOrderById(id);
            return HttpResult.success(order);
        } catch (Exception e) {
            e.printStackTrace();
            return HttpResult.failure(ResultCodeEnum.NOT_FOUND, "订单不存在");
        }
    }

    @GetMapping("")
    @Operation(summary = "用户订单列表")
    public HttpResult<List<Order>> listOrdersByUserId(@RequestParam Long userId) {
        try {
            return HttpResult.success(orderService.listOrdersByUserId(userId));
        } catch (Exception e) {
            e.printStackTrace();
            return HttpResult.failure(ResultCodeEnum.SERVER_ERROR, "获取用户订单失败");
        }
    }

    @PostMapping("/{id}/pay")
    @Operation(summary = "标记订单已支付")
    public HttpResult<Order> markPaid(@PathVariable Long id) {
        try {
            return HttpResult.success(orderService.markPaid(id));
        } catch (Exception e) {
            e.printStackTrace();
            return HttpResult.failure(ResultCodeEnum.SERVER_ERROR, "支付失败");
        }
    }

    @GetMapping("/{id}/pay")
    @Operation(summary = "标记订单已支付 (GET 兼容)")
    public HttpResult<Order> markPaidGet(@PathVariable Long id) {
        return markPaid(id); // 复用
    }

    // 新增：商家订单列表 (fix date parse in existing if – no modify signature; pass parsed to service)
    @GetMapping("/business/{businessId}")
    @Operation(summary = "商家订单列表", description = "status:0-5; startDate/endDate:yyyy-MM-dd; page/size:分页")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'BUSINESS')")
    public HttpResult<List<Order>> listOrdersByBusiness(
            @PathVariable Long businessId,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String startDate,  // String, parse below
            @RequestParam(required = false) String endDate,    // String, parse below
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            LocalDateTime parsedStart = null;
            LocalDateTime parsedEnd = null;
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");  // 新增: parse

            if (startDate != null && !startDate.isEmpty()) {
                parsedStart = LocalDateTime.parse(startDate + "T00:00:00", DateTimeFormatter.ISO_LOCAL_DATE_TIME);  // start day 00:00
            }
            if (endDate != null && !endDate.isEmpty()) {
                parsedEnd = LocalDateTime.parse(endDate + "T23:59:59", DateTimeFormatter.ISO_LOCAL_DATE_TIME);  // end day 23:59
            }

            // 修复: pass parsed (or null) to service – existing if logic now works
            List<Order> orders = orderService.listOrdersByBusinessId(businessId, status, parsedStart, parsedEnd, page, size);
            return HttpResult.success(orders);
        } catch (Exception e) {
            e.printStackTrace();
            return HttpResult.failure(ResultCodeEnum.SERVER_ERROR, "获取商家订单失败: " + e.getMessage());
        }
    }

    // 新增：商家订单计数 (可选，用于前端总条)
    @GetMapping("/business/{businessId}/count")
    @Operation(summary = "商家订单计数")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'BUSINESS')")
    public HttpResult<Long> countOrdersByBusiness(@PathVariable Long businessId,
            @RequestParam(required = false) Integer status) {
        try {
            long count = (status != null) ? orderService.countOrdersByBusinessIdAndOrderState(businessId, status)
                    : orderService.countOrdersByBusinessId(businessId);
            return HttpResult.success(count);
        } catch (Exception e) {
            e.printStackTrace();
            return HttpResult.failure(ResultCodeEnum.SERVER_ERROR, "计数失败");
        }
    }

    // 新增：更新订单状态 (不影响原有 /pay)
    @PutMapping("/{id}/status")
    @Operation(summary = "更新订单状态 (商家用)", description = "newState:2-接单,3-配送,4-完成,5-取消")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'BUSINESS')")
    public HttpResult<Order> updateOrderStatus(@PathVariable Long id, @RequestParam Integer newState) {
        try {
            if (newState < 2 || newState > 5) {
                // 修复：用 SERVER_ERROR (现有 enum) 替换 BAD_REQUEST
                return HttpResult.failure(ResultCodeEnum.SERVER_ERROR, "无效状态: " + newState + " (有效:2-5)");
            }
            User currentUser = userService.getUserWithAuthorities().get();
            Order order = orderService.updateOrderStatus(id, newState, currentUser);
            return HttpResult.success(order);
        } catch (Exception e) {
            e.printStackTrace();
            // 保持原有 catch：SERVER_ERROR
            return HttpResult.failure(ResultCodeEnum.SERVER_ERROR, "更新状态失败: " + e.getMessage());
        }
    }
}