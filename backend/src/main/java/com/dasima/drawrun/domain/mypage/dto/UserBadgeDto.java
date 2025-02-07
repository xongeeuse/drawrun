@Builder
@Getter
@AllArgsConstructor
public class UserBadgeDto {

    private int badgeId;
    private int userId;
    private String badgeName;
    private String badgeDes;
    private String badgeImg;
    private LocalDateTime badgeTime;

        return UserBadgeDto.builder()
                .badgeId(badgeInventory.getBadgeId())
                .userId(badgeInventory.getUserId())
                .badgeName(badgeInfoDto.getBadgeName())
                .badgeDes(badgeInfoDto.getBadgeDes())
                .badgeImg(badgeInfoDto.getBadgeImg())
                .badgeTime(badgeInventory.getCollectedDate())
                .build();
    }

