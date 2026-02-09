/*
 * Engineering Ingegneria Informatica S.p.A.
 *
 * Copyright (C) 2023 Regione Emilia-Romagna
 * <p/>
 * This program is free software: you can redistribute it and/or modify it under the terms of
 * the GNU Affero General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 * <p/>
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU Affero General Public License along with this program.
 * If not, see <https://www.gnu.org/licenses/>.
 */

/**
 *
 */
package it.eng.parer.org.abilitate.beans.validator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;

import it.eng.parer.org.abilitate.beans.utils.Costants.AppNameEnum;
import it.eng.parer.org.abilitate.runner.rest.input.AppNameQuery;
import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;

public class AppNameValidator implements
	ConstraintValidator<it.eng.parer.org.abilitate.beans.validator.AppNameValidator.ValidAppName, AppNameQuery> {

    @Override
    public boolean isValid(AppNameQuery value, ConstraintValidatorContext context) {
	return StringUtils.isNotBlank(value.appName) && Arrays.stream(AppNameEnum.values())
		.anyMatch(app -> app.name().equalsIgnoreCase(value.appName));
    }

    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    @Constraint(validatedBy = {
	    AppNameValidator.class })
    public @interface ValidAppName {
	String message() default "Il nome dell'applicazione non è corretto (valori accettati = any / sacer / sacer_preingest)";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
    }
}
