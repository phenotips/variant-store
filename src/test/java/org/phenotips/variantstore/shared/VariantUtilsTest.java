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
 * Created by meatcar on 4/27/15.
 */
public class VariantUtilsTest
{

    @Test
    public void testAddInfoToVariant() throws Exception {
        GAVariant variant = new GAVariant();
        String key = "key";
        String value = "value";

        VariantUtils.addInfoToVariant(variant, key, value);

        assertNotNull(variant.getInfo());
        assertThat(variant.getInfo(), instanceOf(Map.class));
        assertThat(variant.getInfo().get(key), CoreMatchers.<List<String>>is(Arrays.asList(value)));

        String key2 = "key2";
        String value2 = "value2";

        VariantUtils.addInfoToVariant(variant, key2, value2);
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

        assertThat(VariantUtils.getInfoFromVariant(variant, key), is(nullValue()));

        VariantUtils.addInfoToVariant(variant, key, value);

        assertThat(VariantUtils.getInfoFromVariant(variant, key), is(value));
        assertThat(VariantUtils.getInfoFromVariant(variant, "not a" + key), is(nullValue()));
    }
}
