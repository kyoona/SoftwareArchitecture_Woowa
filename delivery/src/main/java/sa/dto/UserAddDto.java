package sa.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sa.domain.Location;
import sa.domain.UserRole;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserAddDto {

    private String userName;
    private Location location;
    private UserRole userRole;
}
