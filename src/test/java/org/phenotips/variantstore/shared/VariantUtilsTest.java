/*
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.phenotips.variantstore.shared;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.ga4gh.GAVariant;
import org.hamcrest.CoreMatchers;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import org.junit.Test;

/**
 * @version $Id$
 */
public class VariantUtilsTest
{

    @Test
    public void testAddInfoToVariant() throws Exception {
        GAVariant variant = new GAVariant();
        String key = "key";
        String value = "value";

        VariantUtils.addInfo(variant, key, value);

        assertNotNull(variant.getInfo());
        assertThat(variant.getInfo(), instanceOf(Map.class));
        assertThat(variant.getInfo().get(key), CoreMatchers.<List<String>>is(Arrays.asList(value)));

        String key2 = "key2";
        String value2 = "value2";

        VariantUtils.addInfo(variant, key2, value2);
        assertThat(variant.getInfo().size(), is(2));
        assertThat(variant.getInfo().get(key), CoreMatchers.<List<String>>is(Arrays.asList(value)));
        assertThat(variant.getInfo().get(key2), CoreMatchers.<List<String>>is(Arrays.asList(value2)));
    }

    @Test
    public void testGetInfoFromVariant() throws Exception {
        /** Setup **/
        GAVariant variant = new GAVariant();
        String key = "key";
        String value = "value";

        assertThat(VariantUtils.getInfo(variant, key), is(nullValue()));

        VariantUtils.addInfo(variant, key, value);

        assertThat(VariantUtils.getInfo(variant, key), is(value));
        assertThat(VariantUtils.getInfo(variant, "not a" + key), is(nullValue()));
    }
}
