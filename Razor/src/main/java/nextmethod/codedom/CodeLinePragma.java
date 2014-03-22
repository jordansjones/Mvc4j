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

public final class CodeLinePragma {

    private String fileName;
    private int lineNumber;

    public CodeLinePragma() {
    }

    public CodeLinePragma(final String fileName, final int lineNumber) {
        this.fileName = fileName;
        this.lineNumber = lineNumber;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CodeLinePragma that = (CodeLinePragma) o;

        if (lineNumber != that.lineNumber) return false;
        if (fileName != null
            ? !fileName.equals(that.fileName)
            : that.fileName != null) { return false; }

        return true;
    }

    @Override
    public int hashCode() {
        int result = fileName != null
                     ? fileName.hashCode()
                     : 0;
        result = 31 * result + lineNumber;
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("CodeLinePragma");
        sb.append("{fileName='").append(fileName).append('\'');
        sb.append(", lineNumber=").append(lineNumber);
        sb.append('}');
        return sb.toString();
    }
}
