package efub.gift_u.domain.participation.repository;


import efub.gift_u.domain.funding.domain.Funding;
import efub.gift_u.domain.funding.domain.FundingStatus;
import efub.gift_u.domain.participation.domain.Participation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ParticipationRepository extends  JpaRepository<Participation, Long>{
   
    List<Participation> findByFunding(Funding funding);

    @Query("SELECT p.funding FROM Participation p WHERE p.user.userId = :userId ORDER BY p.createdAt DESC")// 최신순으로 정렬 (=참여일 역순)
    List<Funding> findAllFundingByUserId(@Param("userId") Long userId);

    @Query("SELECT p.funding FROM Participation p WHERE p.user.userId = :userId and p.funding.status = :status ORDER BY p.createdAt DESC") // 최신순으로 정렬 (=참여일 역순)
    List<Funding> findAllFundingByUserIdAndStatus(@Param("userId") Long userId, @Param("status") FundingStatus status);

    @Query("SELECT p FROM Participation p WHERE p.participationId = :participationId AND p.user.userId = :userId")
    Optional<Participation> findByParticipationIdAndUserId(@Param("participationId") Long participationId, @Param("userId") Long userId);

    @Query("SELECT p FROM Participation p WHERE  p.user.userId = :userId AND p.funding.fundingId =:fundingId")
    List<Participation> findParticipationByUserIdAndFundingId(@Param("userId") Long userId , @Param("fundingId") Long fundingId);

    @Query("SELECT p FROM Participation p WHERE p.user.userId =:userId AND p.funding.fundingId =:fundingId")
    Optional<Participation> findByUserIdAndFundingId(@Param("userId") Long userId , @Param("fundingId") Long fundingId);

    @Query("select p.createdAt FROM Participation p where p.funding.fundingId =:fundingId ORDER BY  p.createdAt DESC LIMIT 1")
    Optional<LocalDateTime> findCreatedAtByUserIdAndFundingId( @Param("fundingId") Long fundingId);

    
}
