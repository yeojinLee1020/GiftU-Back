package efub.gift_u.domain.funding.service;

import efub.gift_u.domain.funding.dto.*;
import efub.gift_u.domain.funding.repository.FundingRepository;
import efub.gift_u.domain.gift.dto.GiftResponseDto;
import efub.gift_u.domain.gift.repository.GiftRepository;
import efub.gift_u.domain.review.repository.ReviewRepository;
import efub.gift_u.global.exception.CustomException;
import efub.gift_u.global.exception.ErrorCode;
import efub.gift_u.domain.friend.dto.FriendDetailDto;
import efub.gift_u.domain.friend.dto.FriendListResponseDto;
import efub.gift_u.domain.friend.service.FriendService;
import efub.gift_u.domain.funding.domain.Funding;
import efub.gift_u.domain.funding.domain.FundingStatus;
import efub.gift_u.domain.gift.domain.Gift;
import efub.gift_u.domain.gift.service.GiftService;
import efub.gift_u.domain.participation.repository.ParticipationRepository;
import efub.gift_u.domain.participation.service.ParticipationService;
import efub.gift_u.domain.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static efub.gift_u.domain.funding.domain.FundingStatus.IN_PROGRESS;

@Service
@Transactional
@RequiredArgsConstructor
public class FundingService {
    private final FundingRepository fundingRepository;
    private final ParticipationService participationService;
    private final ParticipationRepository participationRepository;
    private final FriendService friendService;
    private final GiftService giftService;
    private final ReviewRepository reviewRepository;
    private final GiftRepository giftRepository;

    //펀딩 개설
    public FundingResponseDto createFunding(User user, FundingRequestDto requestDto, List<MultipartFile> giftImages) {

        if (requestDto.getFundingEndDate() == null || requestDto.getFundingEndDate().isBefore(LocalDate.now())) {
            throw new CustomException(ErrorCode.FUNDING_END_DATE_BEFORE_START);
        }
        if (!requestDto.getVisibility() && (requestDto.getPassword() == null || requestDto.getPassword().length() != 4)) {
            throw new CustomException(ErrorCode.INVALID_PASSWORD_PATTERN);
        }

        Funding funding = requestDto.toEntity(user);
        Funding savedFunding = fundingRepository.save(funding);

        List<Gift> gifts = giftService.createGifts(savedFunding, requestDto.getGifts(), giftImages);
        savedFunding.getGiftList().addAll(gifts);
        giftService.saveAll(gifts);

        Gift mostExpensiveGift = gifts.get(0);
        for (Gift gift : gifts) {
            if (gift.getPrice() > mostExpensiveGift.getPrice()) {
                mostExpensiveGift = gift;
            }
        }
        String fundingImageUrl = mostExpensiveGift.getGiftImageUrl();

        return FundingResponseDto.fromEntity(savedFunding, fundingImageUrl);
    }


    /* 펀딩 상세 조회 */
    public ResponseEntity<FundingResponseDetailDto> getFundingDetail(Long fundingId) {
        Funding funding = fundingRepository.findById(fundingId)
                .orElseThrow(() -> new CustomException(ErrorCode.FUNDING_NOT_FOUND));
        // 펀딩 선물 후기 존재 여부
        boolean isExistedReview = reviewRepository.existsReviewByFunding(funding);
        // 펀딩의 선물 목록
        List<GiftResponseDto> giftResponseDtos = giftRepository.findAllByFunding(funding).stream()
                .map((dtos) -> GiftResponseDto.fromEntity(dtos))
                .collect(Collectors.toList());

        return  ResponseEntity.status(HttpStatus.OK)
                .body(FundingResponseDetailDto.from(funding , participationService.getParticipationDetail(funding) , isExistedReview , giftResponseDtos));
    }


    /* 펀딩 리스트 조회 - 내가 개설한 - 전체 */
    public AllFundingResponseDto getAllFundingByUser(Long userId) {
        List<Funding> fundings = fundingRepository.findAllByUserId(userId);
        List<IndividualFundingResponseDto> dtoList = convertToDtoList(fundings);
        return new AllFundingResponseDto(dtoList);
    }

    /* 펀딩 리스트 조회 - 내가 개설한 - 상태 필터링 */
    public AllFundingResponseDto getAllFundingByUserAndStatus(Long userId, FundingStatus status) {
        List<Funding> fundings = fundingRepository.findAllByUserAndStatus(userId, status);
        List<IndividualFundingResponseDto> dtoList = convertToDtoList(fundings);
        return new AllFundingResponseDto(dtoList);
    }


    /* 펀딩 리스트 조회 - 내가 참여한 - 전체 */
    public AllFundingResponseDto getAllParticipatedFundingByUser(Long userId) {
        List<Funding> fundings = participationRepository.findAllFundingByUserId(userId);
        List<IndividualFundingResponseDto> dtoList = convertToDtoList(fundings);
        return new AllFundingResponseDto(dtoList);
    }

    /* 펀딩 리스트 조회 - 내가 참여한 - 상태 필터링 */
    public AllFundingResponseDto getAllParticipatedFundingByUserAndStatus(Long userId, FundingStatus status) {
        List<Funding> fundings = participationRepository.findAllFundingByUserIdAndStatus(userId, status);
        List<IndividualFundingResponseDto> dtoList = convertToDtoList(fundings);
        return new AllFundingResponseDto(dtoList);
    }


    /* 펀딩 리스트 조회 - 친구가 개설한 펀딩 중 진행중인 펀딩 */
    public AllFundingResponseDto getAllFriendsFundingByUser(User user) {
        FriendListResponseDto friendList = friendService.getFriends(user);
        List<FriendDetailDto> friends = friendList.getFriends();
        List<Funding> fundings = new ArrayList<>();
        for (FriendDetailDto friend : friends) {
            fundings.addAll(fundingRepository.findAllByUserAndStatus(friend.getFriendId(), IN_PROGRESS));
        }
        // 마감일 오름차순으로 정렬
        fundings.sort(Comparator.comparing(Funding::getFundingEndDate));
        // dto로 변환
        List<IndividualFundingResponseDto> dtoList = convertToDtoList(fundings);
        return new AllFundingResponseDto(dtoList);
    }


    /* 해당 마감일 펀딩 목록 조회 - 캘린더 */
    public AllFundingResponseDto getAllFriendsFundingByUserAndDate(User user, LocalDate fundingEndDate) {
        FriendListResponseDto friendList = friendService.getFriends(user);
        List<FriendDetailDto> friends = friendList.getFriends();
        List<Funding> fundings = new ArrayList<>();
        for (FriendDetailDto friend : friends) {
            fundings.addAll(fundingRepository.findAllByUserAndFundingEndDate(friend.getFriendId(), fundingEndDate));
        }
        List<IndividualFundingResponseDto> dtoList = convertToDtoList(fundings);
        return new AllFundingResponseDto(dtoList);
    }


    private List<IndividualFundingResponseDto> convertToDtoList(List<Funding> fundings){
        return fundings.stream()
                .map(IndividualFundingResponseDto::from)
                .collect(Collectors.toList());
    }


    /* 펀딩 비밀번호 확인 */
    public Boolean isAllowed(Long fundingId, FundingPasswordDto fundingPasswordDto) {
        Funding funding = fundingRepository.findById(fundingId)
                .orElseThrow(() -> new CustomException(ErrorCode.FUNDING_NOT_FOUND));
        String compPassword = fundingPasswordDto.getPassword();
        return compPassword.equals(funding.getPassword());
    }
    // 펀딩 삭제
    @Transactional
    public void deleteFunding(Long fundingId, User user) {
        Funding funding = fundingRepository.findById(fundingId)
                .orElseThrow(() -> new CustomException(ErrorCode.FUNDING_NOT_FOUND));
        if (!funding.getUser().getUserId().equals(user.getUserId())) {
            throw new CustomException(ErrorCode.FUNDING_DELETE_ACCESS_DENIED);
        }
        giftService.deleteGifts(funding);
        fundingRepository.delete(funding);
    }
    //펀딩 상태 업데이트
    public void updateFundingStatus(){
        List<Funding> fundings = fundingRepository.findAll();
        for (Funding funding : fundings) {
            funding.terminateIfExpired();
            fundingRepository.save(funding);
        }
    }

}