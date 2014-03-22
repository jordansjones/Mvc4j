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

import nextmethod.base.Strings;

// TODO
public class CodePackageImport extends CodeObject implements Serializable {

    private static final long serialVersionUID = 5451857111922184818L;

    private CodeLinePragma linePragma;
    private String packageName;

    public CodePackageImport() {
    }

    public CodePackageImport(final String packageName) {
        this.packageName = packageName;
    }

    public CodeLinePragma getLinePragma() {
        return linePragma;
    }

    public void setLinePragma(final CodeLinePragma linePragma) {
        this.linePragma = linePragma;
    }

    public String getPackage() {
        return packageName == null
               ? Strings.Empty
               : packageName;
    }

    public void setPackage(final String packageName) {
        this.packageName = packageName;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final CodePackageImport that = (CodePackageImport) o;

        if (packageName != null
            ? !packageName.equals(that.packageName)
            : that.packageName != null) { return false; }

        return true;
    }

    @Override
    public int hashCode() {
        return packageName != null
               ? packageName.hashCode()
               : 0;
    }
}
