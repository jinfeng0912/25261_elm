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

    // 已修正: 接收一个通用的 Map 来避免因 body 格式问题导致的反序列化错误
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

    @PutMapping("/{id}")
    public HttpResult<Business> updateBusiness(@PathVariable Long id, @RequestBody Business business) {
        Business updatedBusiness = businessService.updateBusiness(id, business);
        if (updatedBusiness != null) {
            return HttpResult.success(updatedBusiness);
        } else {
            return HttpResult.failure(ResultCodeEnum.NOT_FOUND, "Business not found");
        }
    }

    @DeleteMapping("/{id}")
    public HttpResult<Business> deleteBusiness(@PathVariable Long id) {
        Business deletedBusiness = businessService.deleteBusiness(id);
        if (deletedBusiness != null) {
            return HttpResult.success(deletedBusiness);
        } else {
            return HttpResult.failure(ResultCodeEnum.NOT_FOUND, "Business not found");
        }
    }

    @PatchMapping("/{id}")
    public HttpResult<Business> patchBusiness(@PathVariable Long id, @RequestBody Business business) {
        Business patchedBusiness = businessService.patchBusiness(id, business);
        if (patchedBusiness != null) {
            return HttpResult.success(patchedBusiness);
        } else {
            return HttpResult.failure(ResultCodeEnum.NOT_FOUND, "Business not found");
        }
    }
}