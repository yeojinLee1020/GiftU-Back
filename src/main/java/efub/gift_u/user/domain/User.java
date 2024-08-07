package efub.gift_u.user.domain;

import efub.gift_u.friend.domain.Friend;
import efub.gift_u.funding.domain.Funding;
import efub.gift_u.notice.domain.Notice;
import efub.gift_u.participation.domain.Participation;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "userId")
    private Long userId;

    @Column(length = 50)
    private String nickname;

    @Column(updatable = false)
    private String email;

    @Column
    private Date birthday;

    @Column
    private String userImageUrl;

    @Column
    private String kakaoAccessToken;

    @OneToMany(mappedBy = "friendTableId", cascade = CascadeType.ALL, orphanRemoval = true)
    List<Friend> friendList = new ArrayList<>();

    @OneToMany(mappedBy = "noticeId", cascade = CascadeType.ALL, orphanRemoval = true)
    List<Notice> noticeList = new ArrayList<>();

    @OneToMany(mappedBy = "participationId", cascade = CascadeType.ALL, orphanRemoval = true)
    List<Participation> participationList = new ArrayList<>();

    @OneToMany(mappedBy = "fundingId", cascade = CascadeType.ALL, orphanRemoval = true)
    List<Funding> fundingList = new ArrayList<>();

    @Builder
    public User(String nickname, String email, Date birthday, String userImageUrl, String kakaoAccessToken) {
        this.nickname = nickname;
        this.email = email;
        this.birthday = birthday;
        this.userImageUrl = userImageUrl;
        this.kakaoAccessToken = kakaoAccessToken;
    }

    // 액세스 토큰 업데이트
    public void updateKakaoAccessToken(String kakaoAccessToken) {
        this.kakaoAccessToken = kakaoAccessToken;
        System.out.println(" 카카오 액세스 토큰" + kakaoAccessToken);
    }

    // 회원 정보 수정
    public void updateUser(String nickname, Date birthday, String userImageUrl){
        this.nickname = nickname;
        this.birthday = birthday;
        this.userImageUrl = userImageUrl;
    }

}
