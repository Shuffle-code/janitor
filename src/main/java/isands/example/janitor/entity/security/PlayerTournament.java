package isands.example.janitor.entity.security;

import isands.example.janitor.entity.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "player_tournament")
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class PlayerTournament extends BaseEntity {
    @Column(name = "player_id")
    private Long playerId;

    @Column(name = "tournament_id")
    private Long tournamentId;
}
