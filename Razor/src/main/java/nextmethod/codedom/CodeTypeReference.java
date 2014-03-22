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
import java.lang.reflect.TypeVariable;
import java.util.Collections;

import nextmethod.base.Strings;
import nextmethod.base.TypeHelpers;

import static com.google.common.base.Preconditions.checkNotNull;
import static nextmethod.base.TypeHelpers.getArrayDimensionCount;

public class CodeTypeReference extends CodeObject implements Serializable {

    private static final long serialVersionUID = 7348697595800892347L;

    private String baseType;
    private CodeTypeReference arrayElementType;
    private int arrayRank;
    private boolean isInterface;

    CodeTypeReferenceCollection typeArguments;
    CodeTypeReferenceOptions referenceOptions;

    // region Constructors

    public CodeTypeReference() {
    }

    public CodeTypeReference(final String baseType) {
        parse(baseType);
    }

    public CodeTypeReference(final Class<?> baseType) {
        checkNotNull(baseType);

//		if (baseType.IsGenericParameter) {
//			this.baseType = baseType.getName();
//			this.referenceOptions = CodeTypeReferenceOptions.GenericTypeParameter;
//		}
        if (TypeHelpers.isGenericType(baseType)) {
            this.baseType = baseType.getName();
            for (TypeVariable<? extends Class<?>> typeVariable : baseType.getTypeParameters()) {
                getTypeArguments().add(new CodeTypeReference(typeVariable.getName()));
            }
        }
        else if (baseType.isArray()) {
            this.arrayRank = getArrayDimensionCount(baseType);
            this.arrayElementType = new CodeTypeReference(baseType.getComponentType());
            this.baseType = this.arrayElementType.getBaseType();
        }
        else {
            parse(baseType.getName());
        }

        isInterface = baseType.isInterface();
    }

    public CodeTypeReference(final CodeTypeReference arrayElementType, final int arrayRank) {
        this.baseType = null;
        this.arrayRank = arrayRank;
        this.arrayElementType = arrayElementType;
    }

    public CodeTypeReference(final String baseType, final int arrayRank) {
        this(new CodeTypeReference(baseType), arrayRank);
    }

    public CodeTypeReference(final String baseType, final CodeTypeReference... typeReferences) {
        this(baseType);
        Collections.addAll(this.getTypeArguments(), typeReferences);
        if (this.baseType.indexOf('`') < 0) {
            this.baseType += "`" + getTypeArguments().size();
        }
    }

    public CodeTypeReference(final CodeTypeParameter typeParameter) {
        this(typeParameter.getName());
        this.referenceOptions = CodeTypeReferenceOptions.GenericTypeParameter;
    }

    public CodeTypeReference(final String typeName, final CodeTypeReferenceOptions referenceOptions) {
        this(typeName);
        this.referenceOptions = referenceOptions;
    }

    // endregion

    // region Properties

    public CodeTypeReference getArrayElementType() {
        return arrayElementType;
    }

    public void setArrayElementType(final CodeTypeReference arrayElementType) {
        this.arrayElementType = arrayElementType;
    }

    public int getArrayRank() {
        return arrayRank;
    }

    public void setArrayRank(final int arrayRank) {
        this.arrayRank = arrayRank;
    }

    public String getBaseType() {
        if (arrayElementType != null && arrayRank > 0) {
            return arrayElementType.getBaseType();
        }
        if (baseType == null) {
            return Strings.Empty;
        }

        return baseType;
    }

    public void setBaseType(final String baseType) {
        this.baseType = baseType;
    }

    public boolean isInterface() {
        return isInterface;
    }

    public CodeTypeReferenceCollection getTypeArguments() {
        if (typeArguments == null) {
            typeArguments = new CodeTypeReferenceCollection();
        }
        return typeArguments;
    }

    // endregion

    private void parse(final String baseType) {
        if (baseType == null || baseType.isEmpty()) {
            this.baseType = Void.class.getName();
            return;
        }

        int array_start = baseType.indexOf('[');
        if (array_start == -1) {
            this.baseType = baseType;
            return;
        }

        int array_end = baseType.lastIndexOf(']');
        if (array_end < array_start) {
            this.baseType = baseType;
            return;
        }

        int lastAngle = baseType.lastIndexOf('>');
        if (lastAngle != -1 && lastAngle > array_end) {
            this.baseType = baseType;
        }

        final String[] args = baseType.substring(array_start + 1, array_end - 1).split(".");

        if ((array_end - array_start) != args.length) {
            this.baseType = baseType.substring(0, array_start);
            int escapeCount = 0;
            int scanPos = array_start;
            final StringBuilder sb = new StringBuilder();
            while (scanPos < baseType.length()) {
                char currentChar = baseType.charAt(scanPos);

                switch (currentChar) {
                    case '[':
                        if (escapeCount > 1 && sb.length() > 0) {
                            sb.append(currentChar);
                        }
                        escapeCount++;
                        break;
                    case ']':
                        escapeCount--;
                        if (escapeCount > 1 && sb.length() > 0) {
                            sb.append(currentChar);
                        }

                        if (sb.length() != 0 && (escapeCount % 2) == 0) {
                            getTypeArguments().add(sb.toString());
                            sb.setLength(0);
                        }
                        break;
                    case ',':
                        if (escapeCount > 1) {
                            // Skip anything after the type name until we
                            // reach the next separator
                            while (scanPos + 1 < baseType.length()) {
                                if (baseType.charAt(scanPos + 1) == ']') {
                                    break;
                                }
                                scanPos++;
                            }
                        }
                        else if (sb.length() > 0) {
                            getTypeArguments().add(new CodeTypeReference(sb.toString()));
                            sb.setLength(0);
                        }
                        break;
                    default:
                        sb.append(currentChar);
                        break;
                }
                scanPos++;
            }
        }
        else {
            arrayElementType = new CodeTypeReference(baseType.substring(0, array_start));
            arrayRank = args.length;
        }
    }

    public CodeTypeReferenceOptions getOptions() {
        return referenceOptions;
    }

    public void setOptions(final CodeTypeReferenceOptions referenceOptions) {
        this.referenceOptions = referenceOptions;
    }


}
