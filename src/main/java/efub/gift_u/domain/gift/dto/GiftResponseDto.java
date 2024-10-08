package efub.gift_u.domain.gift.dto;

import efub.gift_u.domain.gift.domain.Gift;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GiftResponseDto {
    private Long giftId;
    private String giftName;
    private Long price;
    private String giftUrl;
    private String giftImageUrl;

    @Builder
    public GiftResponseDto(Long giftId, String giftName, Long price, String giftUrl, String giftImageUrl) {
        this.giftId = giftId;
        this.giftName = giftName;
        this.price = price;
        this.giftUrl = giftUrl;
        this.giftImageUrl = giftImageUrl;
    }

    public static GiftResponseDto fromEntity(Gift gift) {
        return GiftResponseDto.builder()
                .giftId(gift.getGiftId())
                .giftName(gift.getGiftName())
                .price(gift.getPrice())
                .giftUrl(gift.getGiftUrl())
                .giftImageUrl(gift.getGiftImageUrl())
                .build();
    }
}
