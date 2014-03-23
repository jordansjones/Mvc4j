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

import org.junit.Test;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;

public class RazorCodeLanguageTest {

    @Test
    public void servicesPropertyContainsEntriesForJavaCodeLanguageService() {
        assertEquals(1, RazorCodeLanguage.getLanguages().size());
        assertThat(RazorCodeLanguage.getLanguages().get("rzhtml"), instanceOf(JavaRazorCodeLanguage.class));
    }

    @Test
    public void getServiceByExtensionReturnsEntryMatchingExtensionWithoutPreceedingDot() {
        assertThat(RazorCodeLanguage.getLanguageByExtension("rzhtml"), instanceOf(JavaRazorCodeLanguage.class));
    }

    @Test
    public void getServiceByExtensionReturnsEntryMatchingExtensionWithPreceedingDot() {
        assertThat(RazorCodeLanguage.getLanguageByExtension(".rzhtml"), instanceOf(JavaRazorCodeLanguage.class));
    }

    @Test
    public void getServiceByExtensionReturnsNullIfNoServiceForSpecifiedExtension() {
        assertThat(RazorCodeLanguage.getLanguageByExtension("floozy"), is(nullValue()));
    }

    @Test
    public void multipleCallsToGetServiceWithSameExtensionReturnSameObject() {
        // Arrange
        final RazorCodeLanguage expected = RazorCodeLanguage.getLanguageByExtension("rzhtml");

        // Act
        final RazorCodeLanguage actual = RazorCodeLanguage.getLanguageByExtension("rzhtml");

        // Assert
        assertSame(expected, actual);
    }

}
