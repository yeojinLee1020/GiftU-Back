package efub.gift_u.domain.review.domain;

import efub.gift_u.domain.funding.domain.Funding;
import efub.gift_u.domain.review.dto.ReviewRequestDto;
import efub.gift_u.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Review extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false)
    private Long reviewId;


    @ManyToOne
    @JoinColumn(name = "fundingId", updatable = false, nullable = false)
    private Funding funding;

    @Column(nullable = false)
    private String reviewContent;

    @Transient
    private LocalDateTime updatedAt;

    @Builder
    public Review(Funding funding,  String reviewContent) {
        this.funding = funding;
        this.reviewContent = reviewContent;
    }

    public Review update(Review review , ReviewRequestDto requestDto){
        this.reviewId = review.getReviewId();
        this.funding = review.getFunding();
        this.reviewContent = requestDto.getReviewContent();

        return review;
    }
}
