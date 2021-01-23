package com.bluemriine.aramintha.data;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.util.Objects;

/**
 * Retient les résultats d'une GvG pour un membre.
 * @author BlueM
 */
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ResultatContributionMembreDto extends ResultatMembreDto implements Serializable {
	private static final long serialVersionUID = 1L;
	private Integer points;

	@Override
	public String toString() {
		return "" + pseudo + "," + points;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		ResultatMembreDto that = (ResultatMembreDto) o;
		return pseudo.equals(that.pseudo);
	}

	@Override
	public int hashCode() {
		return Objects.hash(pseudo);
	}
}