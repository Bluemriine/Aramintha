package com.bluemriine.aramintha.data;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

/**
 * Retient les résultats d'une GvG pour un membre.
 * @author BlueM
 */
@FieldDefaults(level = AccessLevel.PROTECTED)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public abstract class ResultatMembreDto implements Serializable {
	private static final long serialVersionUID = 1L;
	protected String pseudo;
}