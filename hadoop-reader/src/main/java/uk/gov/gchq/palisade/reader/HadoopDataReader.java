/*
 * Copyright 2018-2021 Crown Copyright
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

package uk.gov.gchq.palisade.reader;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.gov.gchq.palisade.Generated;
import uk.gov.gchq.palisade.reader.common.SerialisedDataReader;
import uk.gov.gchq.palisade.reader.exception.ReadResourceException;
import uk.gov.gchq.palisade.resource.LeafResource;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.StringJoiner;

import static java.util.Objects.requireNonNull;

/**
 * An HadoopDataReader is an implementation of {@link SerialisedDataReader} for Hadoop that opens a file and returns
 * a single {@link InputStream} containing all the records.
 */
public class HadoopDataReader extends SerialisedDataReader {
    private static final Logger LOGGER = LoggerFactory.getLogger(HadoopDataReader.class);

    private FileSystem fs;

    /**
     * Creates a new {@link HadoopDataReader} object
     *
     * @throws IOException  the {@link Exception} thrown when there is an issue getting the {@link FileSystem} from the {@link Configuration}
     */
    public HadoopDataReader() throws IOException {
        this.fs = FileSystem.get(new Configuration());
    }

    /**
     * Creates a new {@link HadoopDataReader} object
     *
     * @param conf A {@link Map} of {@link String}s used as the configuration
     * @throws IOException the {@link Exception} thrown when there is an issue getting the {@link FileSystem} from the created {@link Configuration}
     */
    public HadoopDataReader(final Map<String, String> conf) throws IOException {
        this.fs = FileSystem.get(createConfig(conf));
    }

    private static Configuration createConfig(final Map<String, String> conf) {
        final Configuration config = new Configuration();
        if ((conf != null)) {
            for (final Entry<String, String> entry : conf.entrySet()) {
                config.set(entry.getKey(), entry.getValue());
            }
        }
        return config;
    }

    /**
     * Creates a {@link Configuration} using the {@link Map} of {@link String}s
     *
     * @param conf          A {@link Map} of {@link String}s used as the configuration
     * @return              the current {@link HadoopDataReader} object
     * @throws IOException  the {@link Exception} thrown when there is an issue inside the {@link #conf(Configuration)} method.
     */
    @Generated
    public HadoopDataReader conf(final Map<String, String> conf) throws IOException {
        return conf(createConfig(conf));
    }

    /**
     * Sets the {@link FileSystem} value
     *
     * @param conf          A Hadoop {@link Configuration} object
     * @return              the current {@link HadoopDataReader} object
     * @throws IOException  the {@link Exception} thrown when there is an issue getting the {@link FileSystem} from the {@link Configuration}
     */
    @Generated
    public HadoopDataReader conf(final Configuration conf) throws IOException {
        this.setFs(FileSystem.get(conf));
        return this;
    }

    /**
     * Sets the {@link FileSystem} value
     *
     * @param fs    the {@link FileSystem} value
     * @return      the current {@link HadoopDataReader} object
     */
    @Generated
    public HadoopDataReader fs(final FileSystem fs) {
        this.setFs(fs);
        return this;
    }


    @Override
    protected InputStream readRaw(final LeafResource resource) {
        requireNonNull(resource, "resource is required");

        InputStream inputStream;
        try {
            //1st attempt: process this as a URI
            inputStream = fs.open(new Path(new URI(resource.getId())));
        } catch (URISyntaxException e) {
            LOGGER.debug("Issue encountered while reading resource {} as a URI: {}", resource.getId(), e);
            //2nd attempt: process as a string
            try {
                inputStream = fs.open(new Path(resource.getId()));
            } catch (final IOException ex) {
                throw new ReadResourceException("Unable to read resource: " + resource.getId(), ex);
            }
        } catch (final IOException e) {
            throw new ReadResourceException("Unable to read resource: " + resource.getId(), e);
        }

        LOGGER.debug("Successfully created stream to resource {}", resource);
        return inputStream;
    }

    @Generated
    public FileSystem getFs() {
        return this.fs;
    }

    @Generated
    public void setFs(final FileSystem fs) {
        requireNonNull(fs);
        this.fs = fs;
    }

    @Generated
    public Configuration getConf() {
        return this.fs.getConf();
    }

    Map<String, String> getConfMap() {
        Map<String, String> rtn = new HashMap<>();
        Map<String, String> plainJobConfWithoutResolvingValues = getPlainJobConfWithoutResolvingValues();

        for (Entry<String, String> entry : getConf()) {
            final String plainValue = plainJobConfWithoutResolvingValues.get(entry.getKey());
            final String thisValue = entry.getValue();
            if (plainValue == null || !plainValue.equals(thisValue)) {
                rtn.put(entry.getKey(), entry.getValue());
            }
        }
        return rtn;
    }

    private Map<String, String> getPlainJobConfWithoutResolvingValues() {
        Map<String, String> plainMapWithoutResolvingValues = new HashMap<>();
        for (Entry<String, String> entry : new Configuration()) {
            plainMapWithoutResolvingValues.put(entry.getKey(), entry.getValue());
        }
        return plainMapWithoutResolvingValues;
    }

    @Override
    @Generated
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof HadoopDataReader)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        final HadoopDataReader that = (HadoopDataReader) o;
        return Objects.equals(fs, that.fs);
    }

    @Override
    @Generated
    public int hashCode() {
        return Objects.hash(super.hashCode(), fs);
    }

    @Override
    @Generated
    public String toString() {
        return new StringJoiner(", ", HadoopDataReader.class.getSimpleName() + "[", "]")
                .add("fs=" + fs)
                .toString();
    }
}
