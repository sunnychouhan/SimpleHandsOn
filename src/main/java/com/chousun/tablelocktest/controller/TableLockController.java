package com.chousun.tablelocktest.controller;

import com.chousun.tablelocktest.model.entity.SharedLockHolder;
import com.chousun.tablelocktest.repo.SharedLockeHolderRepo;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.LockOptions;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.LockTimeoutException;
import javax.persistence.NoResultException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@RestController
@AllArgsConstructor
@Slf4j
public class TableLockController {

    SharedLockeHolderRepo sharedLockeHolderRepo;

    //    @PersistenceContext
    EntityManager em;

    @GetMapping("/getTableStatus")
    public List<SharedLockHolder> getTableStatus() throws InterruptedException {
         log.info("getTableStatus");
        List<SharedLockHolder> all = sharedLockeHolderRepo.fetchAll();
        SharedLockHolder build = SharedLockHolder.builder()
                .lockedAt(LocalDateTime.now())
                .lockedBy("Cron Job")
                .lockedTill(LocalDateTime.now().plusMinutes(1L))
                .retryStatus("In Progress")
                .build();
        SharedLockHolder save = sharedLockeHolderRepo.save(build);
        return Arrays.asList(save);
    }

    @GetMapping("/getTableStatus1/{node}/{pk}")
    @Transactional
    public String getTableStatus1(@PathVariable String node, @PathVariable Long pk) throws InterruptedException {
        log.info("/getTableStatus1/" + node + "/" + pk);
        //https://vladmihalcea.com/database-job-queue-skip-locked/

        try {
            SharedLockHolder result = null;
            try {
                result = em.createQuery(
                        "select c from SharedLockHolder c where c.id = :pk", SharedLockHolder.class)
                        .setParameter("pk", pk)
                        .setLockMode(LockModeType.PESSIMISTIC_WRITE)
                        .setHint(
                                "javax.persistence.lock.timeout",
                                LockOptions.NO_WAIT
                        ).getSingleResult();
            } catch (NoResultException noResultException) {
                SharedLockHolder build = SharedLockHolder.builder()
                        .lockedAt(LocalDateTime.now())
                        .lockedBy("Initial Entry")
                        .retryStatus("In Progress")
                        .build();
                em.persist(build);
                return "Initial Entry";
            }


            Thread.sleep(3000);
            log.info(Thread.currentThread().getName() + " : Thread Sleeping for 3 second");
            result.setLockedAt(LocalDateTime.now());
            result.setLockedBy(node);
            em.persist(result);
        } catch (LockTimeoutException exception) {
            log.info("exception = " + exception);
            return "Retry Already In Progress";

        }
        return "Retry Completed";
    }
}
