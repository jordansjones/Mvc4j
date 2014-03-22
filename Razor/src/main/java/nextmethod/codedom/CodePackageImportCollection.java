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
import java.util.HashMap;
import java.util.Set;

import com.google.common.collect.ForwardingCollection;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class CodePackageImportCollection extends ForwardingCollection<CodePackageImport> implements Serializable {

    private static final long serialVersionUID = -3609814966958487242L;

    private final Set<CodePackageImport> data;
    private final HashMap<String, CodePackageImport> keys;

    public CodePackageImportCollection() {
        data = Sets.newLinkedHashSet();
        keys = Maps.newHashMap();
    }

    @Override
    protected Set<CodePackageImport> delegate() {
        return data;
    }


}
