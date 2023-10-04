package no.fintlabs.user;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;

@Jacksonized
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Table(name = "accessuser")
@Getter
public class AccessUser {
    @Id
    private String resourceId;

    private String userId;

    private String userName;

    private String firstName;

    private String lastName;
}
