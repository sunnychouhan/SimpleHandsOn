package com.chousun.tablelocktest.repo;

import com.chousun.tablelocktest.model.entity.SharedLockHolder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface SharedLockeHolderRepo extends JpaRepository<SharedLockHolder, Long> {


//    @Lock(LockModeType.PESSIMISTIC_WRITE)
//    @QueryHints({@QueryHint(name = "javax.persistence.lock.timeout", value = "5000")})
    @Query("select c from SharedLockHolder c")
    public List<SharedLockHolder> fetchAll();
}
