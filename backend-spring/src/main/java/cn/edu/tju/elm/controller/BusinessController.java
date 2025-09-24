package cn.edu.tju.elm.controller;

import cn.edu.tju.core.model.HttpResult;
import cn.edu.tju.core.model.ResultCodeEnum;
import cn.edu.tju.elm.model.Business;
import cn.edu.tju.elm.service.BusinessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map; // 导入 Map

@RestController
@RequestMapping("/api/businesses")
public class BusinessController {

    @Autowired
    private BusinessService businessService;

    @GetMapping
    public HttpResult<List<Business>> getBusinesses() {
        List<Business> businesses = businessService.getAllBusinesses();
        return HttpResult.success(businesses);
    }

    @PostMapping
    public HttpResult<Business> addBusiness(@RequestBody Map<String, Object> payload) {
        Business newBusiness = businessService.addBusiness(payload);
        return HttpResult.success(newBusiness);
    }

    @GetMapping("/{id}")
    public HttpResult<Business> getBusiness(@PathVariable Long id) {
        Business business = businessService.getBusiness(id);
        if (business != null) {
            return HttpResult.success(business);
        } else {
            return HttpResult.failure(ResultCodeEnum.NOT_FOUND, "Business not found");
        }
    }

    // FIXED: Changed @RequestBody from Business to Map<String, Object>
    @PutMapping("/{id}")
    public HttpResult<Business> updateBusiness(@PathVariable Long id, @RequestBody Map<String, Object> payload) {
        Business updatedBusiness = businessService.updateBusiness(id, payload);
        if (updatedBusiness != null) {
            return HttpResult.success(updatedBusiness);
        } else {
            return HttpResult.failure(ResultCodeEnum.NOT_FOUND, "Business not found");
        }
    }

    /**
     * 部分更新店铺（PATCH操作）
     */
    @PatchMapping("/{id}")
    public HttpResult<Business> patchBusiness(@PathVariable Long id, @RequestBody Map<String, Object> payload) {
        Business updatedBusiness = businessService.patchBusiness(id, payload);
        if (updatedBusiness != null) {
            return HttpResult.success(updatedBusiness);
        } else {
            return HttpResult.failure(ResultCodeEnum.NOT_FOUND, "店铺不存在");
        }
    }

    /**
     * 删除店铺（软删除）
     */
    @DeleteMapping("/{id}")
    public HttpResult<String> deleteBusiness(@PathVariable Long id) {
        Business business = businessService.getBusiness(id);
        if (business != null) {
            businessService.deleteBusiness(id);
            return HttpResult.success("店铺删除成功");
        } else {
            return HttpResult.failure(ResultCodeEnum.NOT_FOUND, "店铺不存在");
        }
    }

}