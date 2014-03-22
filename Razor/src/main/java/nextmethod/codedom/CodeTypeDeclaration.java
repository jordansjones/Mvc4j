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

package nextmethod.codedom;

import java.io.Serializable;

public class CodeTypeDeclaration extends CodeTypeMember implements Serializable {

    private static final long serialVersionUID = -3324618160456382787L;

    private CodeTypeReferenceCollection baseTypes;
    private CodeTypeMemberCollection members;
    private TypeAttributes attributes = TypeAttributes.Public;

    private boolean isEnum;
    private boolean isStruct;

    boolean isPartial;

    CodeTypeParameterCollection typeParameters;

    public CodeTypeDeclaration() {}

    public CodeTypeDeclaration(final String name) {
        this.setName(name);
    }

    public CodeTypeReferenceCollection getBaseTypes() {
        if (baseTypes == null) {
            baseTypes = new CodeTypeReferenceCollection();
//			if ()
//			PopulateBaseTypes event
        }
        return baseTypes;
    }


    public boolean isClass() {
        if ((attributes.val & TypeAttributes.Interface.val) != 0) {
            return false;
        }
        if (isEnum) {
            return false;
        }
        if (isStruct) {
            return false;
        }
        return true;
    }

    public void setIsClass(final boolean value) {
        if (value) {
            int att = attributes.val;
            att &= ~TypeAttributes.Interface.val;
            attributes = TypeAttributes.valueOf(att);
            isEnum = false;
            isStruct = false;
        }
    }

    public boolean isEnum() {
        return isEnum;
    }

    public void setIsEnum(final boolean value) {
        if (value) {
            int att = attributes.val;
            att &= ~TypeAttributes.Interface.val;
            attributes = TypeAttributes.valueOf(att);
            isEnum = true;
            isStruct = false;
        }
    }

    public boolean isInterface() {
        return (attributes.val & TypeAttributes.Interface.val) != 0;
    }

    public void setIsInterface(final boolean value) {
        if (value) {
            int att = attributes.val;
            att |= TypeAttributes.Interface.val;
            attributes = TypeAttributes.valueOf(att);
            isEnum = false;
            isStruct = false;
        }
    }

    public boolean isStruct() {
        return isStruct;
    }

    public void setIsStruct(final boolean value) {
        if (value) {
            int att = attributes.val;
            att &= ~TypeAttributes.Interface.val;
            attributes = TypeAttributes.valueOf(att);
            isEnum = false;
            isStruct = true;
        }
    }

    public CodeTypeMemberCollection getMembers() {
        if (members == null) {
            members = new CodeTypeMemberCollection();
//			if
//			PopulateMembers event handling
        }
        return members;
    }

    public TypeAttributes getTypeAttributes() {
        return attributes;
    }

    public void setTypeAttributes(final TypeAttributes attributes) {
        this.attributes = attributes;
    }

    public boolean isPartial() {
        return isPartial;
    }

    public void setIsPartial(final boolean value) {
        isPartial = value;
    }

    public CodeTypeParameterCollection getTypeParameters() {
        if (typeParameters == null) {
            typeParameters = new CodeTypeParameterCollection();
        }
        return typeParameters;
    }
}
