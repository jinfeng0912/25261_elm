package cn.edu.tju.elm.controller;

import cn.edu.tju.core.model.HttpResult;
import cn.edu.tju.core.model.ResultCodeEnum;
import cn.edu.tju.core.security.service.UserService;
import cn.edu.tju.elm.model.DeliveryAddress;
import cn.edu.tju.elm.service.DeliveryAddressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@Tag(name="管理地址", description = "对配送地址的增删改查")
public class AddressController {

    @Autowired
    private DeliveryAddressService deliveryAddressService;

    @Autowired
    private UserService userService;

    /**
     * 新增配送地址
     */
    @PostMapping("/addresses")
    @Operation(summary = "新增配送地址", description = "为当前用户添加新的配送地址")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "创建成功",
                    content = @Content(schema = @Schema(implementation = HttpResult.class))),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "401", description = "未授权"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public HttpResult<DeliveryAddress> addDeliveryAddress(@RequestBody Map<String, Object> payload) {
        try {
            // 创建新的配送地址对象
            DeliveryAddress deliveryAddress = new DeliveryAddress();

            // 设置基本字段
            deliveryAddress.setContactName((String) payload.get("contactName"));
            deliveryAddress.setContactSex((Integer) payload.get("contactSex"));
            deliveryAddress.setContactTel((String) payload.get("contactTel"));
            deliveryAddress.setAddress((String) payload.get("address"));

            // 处理customer字段 - 如果payload中有customer信息，使用当前登录用户覆盖
            // 这样可以确保地址属于当前登录用户，而不是payload中的用户
            deliveryAddress.setCustomer(userService.getUserWithAuthorities().get());

            // 忽略payload中的其他字段（id, createTime, updateTime, creator, updater, deleted等）
            // 这些字段将由服务层自动设置

            DeliveryAddress createdAddress = deliveryAddressService.createDeliveryAddress(deliveryAddress);
            return HttpResult.success(createdAddress);
        } catch (Exception e) {
            return HttpResult.failure(ResultCodeEnum.SERVER_ERROR, "创建配送地址失败: " + e.getMessage());
        }
    }

    /**
     * 获取当前用户的配送地址列表
     */
    @GetMapping("/addresses")
    @Operation(summary = "获取当前用户的配送地址列表", description = "获取当前登录用户的所有配送地址")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "获取成功",
                    content = @Content(schema = @Schema(implementation = HttpResult.class))),
            @ApiResponse(responseCode = "401", description = "未授权"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public HttpResult<List<DeliveryAddress>> getDeliveryAddresses() {
        try {
            Long currentUserId = userService.getUserWithAuthorities().get().getId();
            List<DeliveryAddress> addresses = deliveryAddressService.getDeliveryAddressesByCustomerId(currentUserId);
            return HttpResult.success(addresses);
        } catch (Exception e) {
            return HttpResult.failure(ResultCodeEnum.SERVER_ERROR, "获取配送地址失败: " + e.getMessage());
        }
    }

    /**
     * 根据用户ID获取配送地址列表
     */
    @GetMapping("/addresses/user/{userId}")
    @Operation(summary = "根据用户ID获取配送地址列表", description = "根据指定的用户ID获取该用户的所有配送地址")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "获取成功",
                    content = @Content(schema = @Schema(implementation = HttpResult.class))),
            @ApiResponse(responseCode = "401", description = "未授权"),
            @ApiResponse(responseCode = "404", description = "用户不存在"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public HttpResult<List<DeliveryAddress>> getDeliveryAddressesByUserId(@PathVariable("userId") Long userId) {
        try {
            List<DeliveryAddress> addresses = deliveryAddressService.getDeliveryAddressesByCustomerId(userId);
            return HttpResult.success(addresses);
        } catch (Exception e) {
            return HttpResult.failure(ResultCodeEnum.SERVER_ERROR, "获取配送地址失败: " + e.getMessage());
        }
    }

    /**
     * 根据ID获取配送地址
     */
    @GetMapping("/addresses/{id}")
    @Operation(summary = "根据ID获取配送地址", description = "根据地址ID获取配送地址详情，只能获取当前用户的地址")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "获取成功",
                    content = @Content(schema = @Schema(implementation = HttpResult.class))),
            @ApiResponse(responseCode = "401", description = "未授权"),
            @ApiResponse(responseCode = "403", description = "无权访问该地址"),
            @ApiResponse(responseCode = "404", description = "地址不存在或已被删除"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public HttpResult<DeliveryAddress> getDeliveryAddress(@PathVariable("id") Long id) {
        try {
            // 获取当前用户ID
            Long currentUserId = userService.getUserWithAuthorities().get().getId();

            // 使用更安全的方法：同时验证用户ID和地址ID，确保只能获取自己的地址
            DeliveryAddress address = deliveryAddressService.getDeliveryAddressByCustomerAndId(currentUserId, id);

            if (address != null) {
                return HttpResult.success(address);
            } else {
                return HttpResult.failure(ResultCodeEnum.NOT_FOUND, "配送地址不存在或已被删除");
            }
        } catch (Exception e) {
            return HttpResult.failure(ResultCodeEnum.SERVER_ERROR, "获取配送地址失败: " + e.getMessage());
        }
    }

    /**
     * 更新配送地址
     */
    @PutMapping("/addresses/{id}")
    @Operation(summary = "更新配送地址", description = "更新指定ID的配送地址信息")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "更新成功",
                    content = @Content(schema = @Schema(implementation = HttpResult.class))),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "401", description = "未授权"),
            @ApiResponse(responseCode = "403", description = "无权修改"),
            @ApiResponse(responseCode = "404", description = "地址不存在"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public HttpResult<DeliveryAddress> updateDeliveryAddress(@PathVariable("id") Long id, @RequestBody Map<String, Object> payload) {
        try {
            // 验证地址是否存在且属于当前用户
            DeliveryAddress existingAddress = deliveryAddressService.getDeliveryAddressById(id);
            if (existingAddress == null) {
                return HttpResult.failure(ResultCodeEnum.NOT_FOUND, "配送地址不存在");
            }

            Long currentUserId = userService.getUserWithAuthorities().get().getId();
            if (!existingAddress.getCustomer().getId().equals(currentUserId)) {
                return HttpResult.failure(ResultCodeEnum.SERVER_ERROR, "无权修改该地址");
            }

            // 更新字段（只更新payload中提供的字段）
            if (payload.containsKey("contactName")) {
                existingAddress.setContactName((String) payload.get("contactName"));
            }
            if (payload.containsKey("contactSex")) {
                existingAddress.setContactSex((Integer) payload.get("contactSex"));
            }
            if (payload.containsKey("contactTel")) {
                existingAddress.setContactTel((String) payload.get("contactTel"));
            }
            if (payload.containsKey("address")) {
                existingAddress.setAddress((String) payload.get("address"));
            }

            // 保持原有的customer和ID不变
            // 忽略payload中的其他字段（id, createTime, updateTime, creator, updater, deleted, customer等）

            DeliveryAddress updatedAddress = deliveryAddressService.updateDeliveryAddress(existingAddress);
            return HttpResult.success(updatedAddress);
        } catch (Exception e) {
            return HttpResult.failure(ResultCodeEnum.SERVER_ERROR, "更新配送地址失败: " + e.getMessage());
        }
    }

    /**
     * 删除配送地址
     */
    @DeleteMapping("/addresses/{id}")
    @Operation(summary = "删除配送地址", description = "删除指定ID的配送地址")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "删除成功",
                    content = @Content(schema = @Schema(implementation = HttpResult.class))),
            @ApiResponse(responseCode = "401", description = "未授权"),
            @ApiResponse(responseCode = "403", description = "无权删除"),
            @ApiResponse(responseCode = "404", description = "地址不存在"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public HttpResult<Void> deleteDeliveryAddress(@PathVariable("id") Long id) {
        try {
            // 验证地址是否存在且属于当前用户
            DeliveryAddress existingAddress = deliveryAddressService.getDeliveryAddressById(id);
            if (existingAddress == null) {
                return HttpResult.failure(ResultCodeEnum.NOT_FOUND, "配送地址不存在");
            }

            Long currentUserId = userService.getUserWithAuthorities().get().getId();
            if (!existingAddress.getCustomer().getId().equals(currentUserId)) {
                return HttpResult.failure(ResultCodeEnum.SERVER_ERROR, "无权删除该地址");
            }

            deliveryAddressService.deleteDeliveryAddress(id);
            return HttpResult.success();
        } catch (Exception e) {
            return HttpResult.failure(ResultCodeEnum.SERVER_ERROR, "删除配送地址失败: " + e.getMessage());
        }
    }
}
