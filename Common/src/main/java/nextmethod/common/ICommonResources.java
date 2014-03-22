/*
 * Copyright 2014 Jordan S. Jones <jordansjones@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package nextmethod.common;

import javax.annotation.Generated;

import nextmethod.i18n.IResourceBundle;
import nextmethod.i18n.annotations.Bundle;

@Generated(value = {
                       "nextmethod.resourcegen.creators.MessagesI18nCreator"
}, date = "Mon Oct 22 22:33:26 MDT 2012")
@Bundle("nextmethod.common.CommonResources")
public interface ICommonResources
    extends IResourceBundle {


    /**
     * Translated "Value cannot be null or an empty string.".
     *
     * @return translated "Value cannot be null or an empty string."
     */
    @nextmethod.i18n.annotations.Key("argument.cannot.be.null.or.empty")
    @nextmethod.i18n.annotations.DefaultMessage("Value cannot be null or an empty string.")
    public String argumentCannotBeNullOrEmpty();

    /**
     * Translated "Value must be between {0} and {1}.".
     *
     * @param arg1
     * @param arg0
     *
     * @return translated "Value must be between {0} and {1}."
     */
    @nextmethod.i18n.annotations.Key("argument.must.be.between")
    @nextmethod.i18n.annotations.DefaultMessage("Value must be between {0} and {1}.")
    public String argumentMustBeBetween(final CharSequence arg0, final CharSequence arg1);

    /**
     * Translated "Value must be between {0} and {1}.".
     *
     * @return translated "Value must be between {0} and {1}."
     */
    @nextmethod.i18n.annotations.Key("argument.must.be.between")
    @nextmethod.i18n.annotations.DefaultMessage("Value must be between {0} and {1}.")
    public String argumentMustBeBetween();

    /**
     * Translated "Value must be a value from the "{0}" enumeration.".
     *
     * @param arg0
     *
     * @return translated "Value must be a value from the "{0}" enumeration."
     */
    @nextmethod.i18n.annotations.Key("argument.must.be.enum.member")
    @nextmethod.i18n.annotations.DefaultMessage("Value must be a value from the \"{0}\" enumeration.")
    public String argumentMustBeEnumMember(final CharSequence arg0);

    /**
     * Translated "Value must be a value from the "{0}" enumeration.".
     *
     * @return translated "Value must be a value from the "{0}" enumeration."
     */
    @nextmethod.i18n.annotations.Key("argument.must.be.enum.member")
    @nextmethod.i18n.annotations.DefaultMessage("Value must be a value from the \"{0}\" enumeration.")
    public String argumentMustBeEnumMember();

    /**
     * Translated "Value must be greater than {0}.".
     *
     * @param arg0
     *
     * @return translated "Value must be greater than {0}."
     */
    @nextmethod.i18n.annotations.Key("argument.must.be.greaterThan")
    @nextmethod.i18n.annotations.DefaultMessage("Value must be greater than {0}.")
    public String argumentMustBeGreaterThan(final CharSequence arg0);

    /**
     * Translated "Value must be greater than {0}.".
     *
     * @return translated "Value must be greater than {0}."
     */
    @nextmethod.i18n.annotations.Key("argument.must.be.greaterThan")
    @nextmethod.i18n.annotations.DefaultMessage("Value must be greater than {0}.")
    public String argumentMustBeGreaterThan();

    /**
     * Translated "Value must be greater than or equal to {0}.".
     *
     * @param arg0
     *
     * @return translated "Value must be greater than or equal to {0}."
     */
    @nextmethod.i18n.annotations.Key("argument.must.be.greaterThanOrEqualTo")
    @nextmethod.i18n.annotations.DefaultMessage("Value must be greater than or equal to {0}.")
    public String argumentMustBeGreaterThanOrEqualTo(final CharSequence arg0);

    /**
     * Translated "Value must be greater than or equal to {0}.".
     *
     * @return translated "Value must be greater than or equal to {0}."
     */
    @nextmethod.i18n.annotations.Key("argument.must.be.greaterThanOrEqualTo")
    @nextmethod.i18n.annotations.DefaultMessage("Value must be greater than or equal to {0}.")
    public String argumentMustBeGreaterThanOrEqualTo();

    /**
     * Translated "Value must be less than {0}.".
     *
     * @param arg0
     *
     * @return translated "Value must be less than {0}."
     */
    @nextmethod.i18n.annotations.Key("argument.must.be.lessThan")
    @nextmethod.i18n.annotations.DefaultMessage("Value must be less than {0}.")
    public String argumentMustBeLessThan(final CharSequence arg0);

    /**
     * Translated "Value must be less than {0}.".
     *
     * @return translated "Value must be less than {0}."
     */
    @nextmethod.i18n.annotations.Key("argument.must.be.lessThan")
    @nextmethod.i18n.annotations.DefaultMessage("Value must be less than {0}.")
    public String argumentMustBeLessThan();

    /**
     * Translated "Value must be less than or equal to {0}.".
     *
     * @param arg0
     *
     * @return translated "Value must be less than or equal to {0}."
     */
    @nextmethod.i18n.annotations.Key("argument.must.be.lessThanOrEqualTo")
    @nextmethod.i18n.annotations.DefaultMessage("Value must be less than or equal to {0}.")
    public String argumentMustBeLessThanOrEqualTo(final CharSequence arg0);

    /**
     * Translated "Value must be less than or equal to {0}.".
     *
     * @return translated "Value must be less than or equal to {0}."
     */
    @nextmethod.i18n.annotations.Key("argument.must.be.lessThanOrEqualTo")
    @nextmethod.i18n.annotations.DefaultMessage("Value must be less than or equal to {0}.")
    public String argumentMustBeLessThanOrEqualTo();

    /**
     * Translated "Value cannot be an empty string. It must either be null or a non-empty string.".
     *
     * @return translated "Value cannot be an empty string. It must either be null or a non-empty string."
     */
    @nextmethod.i18n.annotations.Key("argument.must.be.null.or.non.empty")
    @nextmethod.i18n.annotations.DefaultMessage("Value cannot be an empty string. It must either be null or a non-empty string.")
    public String argumentMustBeNullOrNonEmpty();

}
