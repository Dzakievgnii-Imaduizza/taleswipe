@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Like {
    @Id @GeneratedValue
    private Long id;

    @ManyToOne
    private User user;

    @ManyToOne
    private Story story;
}
