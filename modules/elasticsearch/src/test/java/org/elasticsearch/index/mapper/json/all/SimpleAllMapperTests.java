/*
 * Licensed to Elastic Search and Shay Banon under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. Elastic Search licenses this
 * file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.elasticsearch.index.mapper.json.all;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.elasticsearch.index.Index;
import org.elasticsearch.index.analysis.AnalysisService;
import org.elasticsearch.index.mapper.json.JsonDocumentMapper;
import org.elasticsearch.index.mapper.json.JsonDocumentMapperParser;
import org.elasticsearch.util.lucene.all.AllEntries;
import org.elasticsearch.util.lucene.all.AllTokenFilter;
import org.testng.annotations.Test;

import static org.elasticsearch.util.io.Streams.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

/**
 * @author kimchy (shay.banon)
 */
@Test
public class SimpleAllMapperTests {

    @Test public void testSimpleAllMappers() throws Exception {
        String mapping = copyToStringFromClasspath("/org/elasticsearch/index/mapper/json/all/mapping.json");
        JsonDocumentMapper docMapper = (JsonDocumentMapper) new JsonDocumentMapperParser(new AnalysisService(new Index("test"))).parse(mapping);
        byte[] json = copyToBytesFromClasspath("/org/elasticsearch/index/mapper/json/all/test1.json");
        Document doc = docMapper.parse(json).doc();
        Field field = doc.getField("_all");
        AllEntries allEntries = ((AllTokenFilter) field.tokenStreamValue()).allEntries();
        assertThat(allEntries.fields().size(), equalTo(2));
        assertThat(allEntries.fields().contains("name.last"), equalTo(true));
        assertThat(allEntries.fields().contains("simple1"), equalTo(true));
    }

    @Test public void testSimpleAllMappersWithReparse() throws Exception {
        String mapping = copyToStringFromClasspath("/org/elasticsearch/index/mapper/json/all/mapping.json");
        JsonDocumentMapper docMapper = (JsonDocumentMapper) new JsonDocumentMapperParser(new AnalysisService(new Index("test"))).parse(mapping);
        String builtMapping = docMapper.buildSource();
//        System.out.println(builtMapping);
        // reparse it
        JsonDocumentMapper builtDocMapper = (JsonDocumentMapper) new JsonDocumentMapperParser(new AnalysisService(new Index("test"))).parse(builtMapping);
        byte[] json = copyToBytesFromClasspath("/org/elasticsearch/index/mapper/json/all/test1.json");
        Document doc = builtDocMapper.parse(json).doc();

        Field field = doc.getField("_all");
        AllEntries allEntries = ((AllTokenFilter) field.tokenStreamValue()).allEntries();
        assertThat(allEntries.fields().size(), equalTo(2));
        assertThat(allEntries.fields().contains("name.last"), equalTo(true));
        assertThat(allEntries.fields().contains("simple1"), equalTo(true));
    }

    @Test public void testSimpleAllMappersWithStore() throws Exception {
        String mapping = copyToStringFromClasspath("/org/elasticsearch/index/mapper/json/all/store-mapping.json");
        JsonDocumentMapper docMapper = (JsonDocumentMapper) new JsonDocumentMapperParser(new AnalysisService(new Index("test"))).parse(mapping);
        byte[] json = copyToBytesFromClasspath("/org/elasticsearch/index/mapper/json/all/test1.json");
        Document doc = docMapper.parse(json).doc();
        Field field = doc.getField("_all");
        AllEntries allEntries = ((AllTokenFilter) field.tokenStreamValue()).allEntries();
        assertThat(allEntries.fields().size(), equalTo(2));
        assertThat(allEntries.fields().contains("name.last"), equalTo(true));
        assertThat(allEntries.fields().contains("simple1"), equalTo(true));

        String text = field.stringValue();
        assertThat(text, equalTo(allEntries.buildText()));
    }

    @Test public void testSimpleAllMappersWithReparseWithStore() throws Exception {
        String mapping = copyToStringFromClasspath("/org/elasticsearch/index/mapper/json/all/store-mapping.json");
        JsonDocumentMapper docMapper = (JsonDocumentMapper) new JsonDocumentMapperParser(new AnalysisService(new Index("test"))).parse(mapping);
        String builtMapping = docMapper.buildSource();
        System.out.println(builtMapping);
        // reparse it
        JsonDocumentMapper builtDocMapper = (JsonDocumentMapper) new JsonDocumentMapperParser(new AnalysisService(new Index("test"))).parse(builtMapping);
        byte[] json = copyToBytesFromClasspath("/org/elasticsearch/index/mapper/json/all/test1.json");
        Document doc = builtDocMapper.parse(json).doc();

        Field field = doc.getField("_all");
        AllEntries allEntries = ((AllTokenFilter) field.tokenStreamValue()).allEntries();
        assertThat(allEntries.fields().size(), equalTo(2));
        assertThat(allEntries.fields().contains("name.last"), equalTo(true));
        assertThat(allEntries.fields().contains("simple1"), equalTo(true));

        String text = field.stringValue();
        assertThat(text, equalTo(allEntries.buildText()));
    }
}
