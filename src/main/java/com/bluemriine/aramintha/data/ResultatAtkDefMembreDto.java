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
@EqualsAndHashCode
public class ResultatAtkDefMembreDto extends ResultatMembreDto implements Serializable {
	private static final long serialVersionUID = 1L;
	private Integer defWin;
	private Integer defDraw;
	private Integer defLoose;
	private Integer atkWin;
	private Integer atkDraw;
	private Integer atkLoose;

	@Builder
	public ResultatAtkDefMembreDto(String pseudo, Integer defWin, Integer defDraw, Integer defLoose, Integer atkWin, Integer atkDraw, Integer atkLoose) {
		super(pseudo);
		this.defWin = defWin;
		this.defDraw = defDraw;
		this.defLoose = defLoose;
		this.atkWin = atkWin;
		this.atkDraw = atkDraw;
		this.atkLoose = atkLoose;
	}

	@Override
	public String toString() {
		return "" + pseudo + "," + atkWin + "," + atkDraw + "," + atkLoose + "," + defWin + "," + defDraw + "," + defLoose;
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