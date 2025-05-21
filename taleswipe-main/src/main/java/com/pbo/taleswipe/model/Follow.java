@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Follow {
    @Id @GeneratedValue
    private Long id;

    @ManyToOne
    private User user; // follower

    @ManyToOne
    private User followedUser; // yang diikuti
}
