package no.fintlabs.scope;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Table(name = "scope")
@Getter
@Setter
public class Scope {
    @Id()
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "objecttype")
    private String objectType;

    @OneToMany(mappedBy = "scope", fetch = FetchType.LAZY)
    private List<ScopeOrgUnit> scopeOrgUnits;
}
