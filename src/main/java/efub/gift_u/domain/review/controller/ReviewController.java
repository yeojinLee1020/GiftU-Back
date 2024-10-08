package efub.gift_u.domain.review.controller;

import efub.gift_u.domain.oauth.customAnnotation.AuthUser;
import efub.gift_u.domain.review.dto.ReviewRequestDto;
import efub.gift_u.domain.review.dto.ReviewResponseDto;
import efub.gift_u.domain.review.service.ReviewService;
import efub.gift_u.domain.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    /* 리뷰 생성 */
    @PostMapping("/fundings/{fundingId}/review")
    public ResponseEntity<?> createReview(@AuthUser User user ,@PathVariable("fundingId") Long fundingId , @RequestBody ReviewRequestDto requestDto){
        return reviewService.createReview(user ,fundingId , requestDto);
    }

    /* 리뷰 조회 */
    @GetMapping("/fundings/{fundingId}/review")
    public ResponseEntity<?> getReview(@PathVariable("fundingId") Long fundingId){
        return reviewService.findReview(fundingId);
    }

    /* 리뷰 수정 */
    @PatchMapping("/fundings/{fundingId}/review")
    public ResponseEntity<?>  patchReview(@AuthUser User user ,@PathVariable("fundingId") Long fundingId , @RequestBody ReviewRequestDto requestDto){
        return reviewService.updateReview(user ,fundingId , requestDto);
    }

    /* 리뷰 삭제 */
    @DeleteMapping("/fundings/{fundingId}/review")
    public  ResponseEntity<?> deleteReview(@AuthUser User user, @PathVariable("fundingId") Long fundingId){
        return reviewService.deleteReview(user ,fundingId);

    }

}
