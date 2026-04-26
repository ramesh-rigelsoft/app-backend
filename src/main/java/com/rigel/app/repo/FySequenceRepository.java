package com.rigel.app.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.rigel.app.model.FySequence;

import jakarta.persistence.LockModeType;

@Repository
public interface FySequenceRepository extends JpaRepository<FySequence, String> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT f FROM FySequence f WHERE f.fyYear = :fyYear AND f.fyMonth = :fyMonth  AND f.userId = :userId AND f.seqCode = :seqCode")
    Optional<FySequence> findForUpdate(@Param("fyYear") String fyYear,@Param("fyMonth") int fyMonth,@Param("userId") int userId,@Param("seqCode") String seqCode);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT f FROM FySequence f WHERE f.userId = :userId AND f.seqCode = :seqCode")
    Optional<FySequence> findSequence(@Param("userId") int userId,@Param("seqCode") String seqCode);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT f FROM FySequence f WHERE f.fyMonth = :fyMonth AND f.userId = :userId AND f.seqCode = :seqCode")
    Optional<FySequence> findSequenceByMonth(@Param("fyMonth") int fyMonth,@Param("userId") int userId,@Param("seqCode") String seqCode);

}
