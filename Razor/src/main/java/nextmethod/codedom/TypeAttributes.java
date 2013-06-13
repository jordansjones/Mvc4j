/*
 * Copyright 2013 Jordan S. Jones <jordansjones@gmail.com>
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
import java.lang.reflect.Modifier;

public class TypeAttributes implements Serializable {

	private static final long serialVersionUID = 3283660713469003333L;

	public static final TypeAttributes Abstract = new TypeAttributes(Modifier.ABSTRACT);
	public static final TypeAttributes Final = new TypeAttributes(Modifier.FINAL);
	public static final TypeAttributes Interface = new TypeAttributes(Modifier.INTERFACE);
	public static final TypeAttributes Private = new TypeAttributes(Modifier.PRIVATE);
	public static final TypeAttributes Protected = new TypeAttributes(Modifier.PROTECTED);
	public static final TypeAttributes Public = new TypeAttributes(Modifier.PUBLIC);
	public static final TypeAttributes Static = new TypeAttributes(Modifier.STATIC);

	public static final TypeAttributes VisibilityMask = new TypeAttributes(Modifier.PUBLIC | Modifier.PROTECTED | Modifier.PRIVATE);

	public static TypeAttributes valueOf(final int val) {
		return new TypeAttributes(val);
	}

	public final int val;

	private TypeAttributes(final int val) {
		this.val = val;
	}
}
