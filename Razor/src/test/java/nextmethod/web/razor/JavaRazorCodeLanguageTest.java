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

import nextmethod.codedom.java.JavaCodeProvider;
import nextmethod.web.razor.generator.JavaRazorCodeGenerator;
import nextmethod.web.razor.generator.RazorCodeGenerator;
import nextmethod.web.razor.parser.JavaCodeParser;
import nextmethod.web.razor.parser.ParserBase;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;

public class JavaRazorCodeLanguageTest {

    private JavaRazorCodeLanguage codeService;

    @Before
    public void setUp()
        throws Exception {
        codeService = new JavaRazorCodeLanguage();
    }

    @Test
    public void createCodeParserReturnsNewJavaCodeParser() {
        // Act
        final ParserBase parser = codeService.createCodeParser();

        // Assert
        assertNotNull(parser);
        assertThat(parser, instanceOf(JavaCodeParser.class));
    }

    @Test
    public void createCodeGeneratedParserListenerReturnsNewJavaCodeGeneratorParserListener() {
        // Act
        final RazorEngineHost host = new RazorEngineHost(codeService);
        final RazorCodeGenerator generator = codeService.createCodeGenerator("Foo", "Bar", "Baz", host);

        // Assert
        assertNotNull(generator);
        assertThat(generator, instanceOf(JavaRazorCodeGenerator.class));
        assertEquals("Foo", generator.getClassName());
        assertEquals("Bar", generator.getRootPackageName());
        assertEquals("Baz", generator.getSourceFileName());
        assertSame(host, generator.getHost());
    }

    @Test
    public void codeDomProviderTypeReturnsJavaCodeProvider() {
        // Assert
        assertEquals(codeService.getCodeDomProviderType(), JavaCodeProvider.class);
    }

}
