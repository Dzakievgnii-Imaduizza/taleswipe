public interface FollowRepository extends JpaRepository<Follow, Long> {
    List<Follow> findByUserId(Long userId);
    boolean existsByUserIdAndFollowedUserId(Long userId, Long followedUserId);
}
