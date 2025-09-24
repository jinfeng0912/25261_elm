package cn.edu.tju.elm.service;

import cn.edu.tju.elm.model.DeliveryAddress;
import cn.edu.tju.elm.repository.DeliveryAddressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class DeliveryAddressService {

    @Autowired
    private DeliveryAddressRepository deliveryAddressRepository;

    /**
     * 创建配送地址
     */
    public DeliveryAddress createDeliveryAddress(DeliveryAddress deliveryAddress) {
        deliveryAddress.setCreateTime(LocalDateTime.now());
        deliveryAddress.setUpdateTime(LocalDateTime.now());
        deliveryAddress.setDeleted(false);
        return deliveryAddressRepository.save(deliveryAddress);
    }

    /**
     * 根据ID获取未删除的配送地址（管理员功能，不验证用户权限）
     */
    public DeliveryAddress getDeliveryAddressById(Long id) {
        return deliveryAddressRepository.findByIdAndDeletedFalse(id).orElse(null);
    }

    /**
     * 根据ID获取配送地址（包括已删除的，管理员功能）
     */
    public DeliveryAddress getDeliveryAddressByIdIncludingDeleted(Long id) {
        return deliveryAddressRepository.findById(id).orElse(null);
    }

    /**
     * 根据用户ID和地址ID获取未删除的配送地址
     */
    public DeliveryAddress getDeliveryAddressByCustomerAndId(Long customerId, Long id) {
        return deliveryAddressRepository.findByCustomerIdAndIdAndDeletedFalse(customerId, id).orElse(null);
    }

    /**
     * 根据用户ID获取所有未删除的配送地址
     */
    public List<DeliveryAddress> getDeliveryAddressesByCustomerId(Long customerId) {
        return deliveryAddressRepository.findByCustomerIdAndDeletedFalse(customerId);
    }

    /**
     * 更新配送地址
     */
    public DeliveryAddress updateDeliveryAddress(DeliveryAddress deliveryAddress) {
        deliveryAddress.setUpdateTime(LocalDateTime.now());
        return deliveryAddressRepository.save(deliveryAddress);
    }

    /**
     * 软删除配送地址
     */
    public void deleteDeliveryAddress(Long id) {
        Optional<DeliveryAddress> addressOpt = deliveryAddressRepository.findByIdAndDeletedFalse(id);
        if (addressOpt.isPresent()) {
            DeliveryAddress address = addressOpt.get();
            address.setDeleted(true);
            address.setUpdateTime(LocalDateTime.now());
            deliveryAddressRepository.save(address);
        }
    }

    /**
     * 根据用户ID软删除配送地址
     */
    public void deleteDeliveryAddressByCustomerAndId(Long customerId, Long id) {
        Optional<DeliveryAddress> addressOpt = deliveryAddressRepository.findByCustomerIdAndIdAndDeletedFalse(customerId, id);
        if (addressOpt.isPresent()) {
            DeliveryAddress address = addressOpt.get();
            address.setDeleted(true);
            address.setUpdateTime(LocalDateTime.now());
            deliveryAddressRepository.save(address);
        }
    }

    /**
     * 恢复已删除的配送地址
     */
    public void restoreDeliveryAddress(Long id) {
        Optional<DeliveryAddress> addressOpt = deliveryAddressRepository.findById(id);
        if (addressOpt.isPresent() && addressOpt.get().getDeleted()) {
            DeliveryAddress address = addressOpt.get();
            address.setDeleted(false);
            address.setUpdateTime(LocalDateTime.now());
            deliveryAddressRepository.save(address);
        }
    }

    /**
     * 获取所有地址（包括已删除的）- 用于管理目的
     */
    public List<DeliveryAddress> getAllDeliveryAddressesIncludingDeleted(Long customerId) {
        return deliveryAddressRepository.findByCustomerId(customerId);
    }

    /**
     * 硬删除配送地址 - 谨慎使用
     */
    public void hardDeleteDeliveryAddress(Long id) {
        deliveryAddressRepository.deleteById(id);
    }
}
