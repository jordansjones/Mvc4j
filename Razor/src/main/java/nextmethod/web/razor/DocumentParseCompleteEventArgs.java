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

package nextmethod.web.razor;

import nextmethod.base.EventArgs;
import nextmethod.web.razor.text.TextChange;

/**
 * Arguments for the DocumentParseComplete event in RazorEditorParser
 */
public class DocumentParseCompleteEventArgs extends EventArgs {

    private boolean treeStructureChanged;
    private GeneratorResults generatorResults;
    private TextChange sourceChange;

    public DocumentParseCompleteEventArgs() {
    }

    public DocumentParseCompleteEventArgs(final boolean treeStructureChanged, final GeneratorResults generatorResults,
                                          final TextChange sourceChange
                                         ) {
        this.treeStructureChanged = treeStructureChanged;
        this.generatorResults = generatorResults;
        this.sourceChange = sourceChange;
    }

    /**
     * Indicates if the tree structure has actually changed since the previous reparse.
     *
     * @return true if the tree structure has changed
     */
    public boolean isTreeStructureChanged() {
        return treeStructureChanged;
    }

    /**
     * The results of the code generation and parsing
     *
     * @return code generation/parsing results
     */
    public GeneratorResults getGeneratorResults() {
        return generatorResults;
    }

    /**
     * The TextChange which triggered the reparse
     *
     * @return
     */
    public TextChange getSourceChange() {
        return sourceChange;
    }
}
