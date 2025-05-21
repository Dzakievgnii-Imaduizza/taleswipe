public interface LikeRepository extends JpaRepository<Like, Long> {
    List<Like> findByStoryId(Long storyId);
    boolean existsByUserIdAndStoryId(Long userId, Long storyId);
}
