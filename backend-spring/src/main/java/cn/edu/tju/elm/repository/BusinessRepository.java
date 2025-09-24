package cn.edu.tju.elm.repository;

import cn.edu.tju.elm.model.Business;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List; // 导入 List

@Repository
public interface BusinessRepository extends JpaRepository<Business, Long> {

    //  ADDED: This method finds all businesses that have not been soft-deleted.
    List<Business> findAllByDeletedFalse();

    List<Business> findByBusinessOwnerId(Long id);
}