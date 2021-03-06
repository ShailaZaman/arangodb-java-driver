/*
 * Copyright (C) 2012 tamtam180
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.arangodb;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.util.Arrays;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.arangodb.entity.CollectionEntity;
import com.arangodb.entity.IndexEntity;
import com.arangodb.entity.IndexType;
import com.arangodb.entity.IndexesEntity;

/**
 * @author tamtam180 - kirscheless at gmail.com
 * 
 */
public class ArangoDriverIndexTest extends BaseTest {

	private static Logger logger = LoggerFactory.getLogger(ArangoDriverCollectionTest.class);

	private final String collectionName = "unit_test_arango_index"; //
	private final String collectionName404 = "unit_test_arango_404"; // 存在しないコレクション名

	CollectionEntity col1;

	@Before
	public void before() throws ArangoException {

		logger.debug("----------");

		// 事前に消しておく
		try {
			driver.deleteCollection(collectionName);
		} catch (final ArangoException e) {
		}
		try {
			driver.deleteCollection(collectionName404);
		} catch (final ArangoException e) {
		}

		// 1は作る
		col1 = driver.createCollection(collectionName);

		logger.debug("--");

	}

	@After
	public void after() {
		logger.debug("----------");
	}

	@Test
	public void test_create_index() throws ArangoException {

		{
			final IndexEntity entity = driver.createIndex(collectionName, IndexType.GEO, false, "a");

			assertThat(entity, is(notNullValue()));
			assertThat(entity.getCode(), is(201));
			assertThat(entity.isError(), is(false));
			assertThat(entity.isNewlyCreated(), is(true));
			assertThat(entity.isGeoJson(), is(false));
			assertThat(entity.getId(), is(notNullValue()));
			assertThat(entity.getType(), is(IndexType.GEO));
		}

		// 重複して作成する
		{
			final IndexEntity entity = driver.createIndex(collectionName, IndexType.GEO, false, "a");

			assertThat(entity, is(notNullValue()));
			assertThat(entity.getCode(), is(200));
			assertThat(entity.isError(), is(false));
			assertThat(entity.isNewlyCreated(), is(false));
			assertThat(entity.isGeoJson(), is(false));
			assertThat(entity.getId(), is(notNullValue()));
			assertThat(entity.getType(), is(IndexType.GEO));
		}

	}

	@Test
	public void test_create_index_404() throws ArangoException {

		try {
			driver.createIndex(collectionName404, IndexType.GEO, false, "a");
			fail("We expect an Exception here");
		} catch (final ArangoException e) {
			assertThat(e.getErrorNumber(), is(1203)); // FIXME MagicNumber
		}

	}

	@Test
	public void test_create_geo_index_unique() throws ArangoException {

		final IndexEntity entity = driver.createIndex(collectionName, IndexType.GEO, true, "a", "b");

		assertThat(entity, is(notNullValue()));
		assertThat(entity.getCode(), is(201));
		assertThat(entity.isError(), is(false));
		assertThat(entity.isNewlyCreated(), is(true));
		assertThat(entity.isGeoJson(), is(false));
		assertThat(entity.getId(), is(notNullValue()));
		assertThat(entity.getType(), is(IndexType.GEO));

	}

	@Test
	public void test_create_geo_index_over_columnnum() throws ArangoException {

		// GeoIndexは2つまで。だけど3つを指定した場合のエラー確認

		try {
			driver.createIndex(collectionName, IndexType.GEO, true, "a", "b", "c");
			fail("We expect an Exception here");
		} catch (final ArangoException e) {
			assertThat(e.getErrorNumber(), is(ErrorNums.ERROR_BAD_PARAMETER));
		}

	}

	@Test
	public void test_create_hash_index() throws ArangoException {

		final IndexEntity entity = driver.createIndex(collectionName, IndexType.HASH, false, "a", "b", "c", "d", "e",
			"f", "g");

		assertThat(entity, is(notNullValue()));
		assertThat(entity.getCode(), is(201));
		assertThat(entity.isError(), is(false));
		assertThat(entity.isNewlyCreated(), is(true));
		assertThat(entity.isGeoJson(), is(false));
		assertThat(entity.getId(), is(notNullValue()));
		assertThat(entity.getType(), is(IndexType.HASH));

	}

	@Test
	public void test_create_hash_index_404() throws ArangoException {

		try {
			driver.createIndex(collectionName404, IndexType.HASH, false, "a", "b", "c", "d", "e", "f", "g");
			fail("We expect an Exception here");
		} catch (final ArangoException e) {
			assertThat(e.getErrorNumber(), is(ErrorNums.ERROR_ARANGO_COLLECTION_NOT_FOUND));
		}

	}

	@Test
	public void test_create_hash_index_unique() throws ArangoException {

		final IndexEntity entity = driver.createIndex(collectionName, IndexType.HASH, true, "a", "b", "c", "d", "e",
			"f", "g");

		assertThat(entity, is(notNullValue()));
		assertThat(entity.getCode(), is(201));
		assertThat(entity.isError(), is(false));
		assertThat(entity.isNewlyCreated(), is(true));
		assertThat(entity.isGeoJson(), is(false));
		assertThat(entity.getId(), is(notNullValue()));
		assertThat(entity.getType(), is(IndexType.HASH));

	}

	@Test
	public void test_create_skiplist_index() throws ArangoException {

		final IndexEntity entity = driver.createIndex(collectionName, IndexType.SKIPLIST, false, "a", "b", "c", "d",
			"e", "f", "g");

		assertThat(entity, is(notNullValue()));
		assertThat(entity.getCode(), is(201));
		assertThat(entity.isError(), is(false));
		assertThat(entity.isNewlyCreated(), is(true));
		assertThat(entity.isGeoJson(), is(false));
		assertThat(entity.getId(), is(notNullValue()));
		assertThat(entity.getType(), is(IndexType.SKIPLIST));

	}

	@Test
	public void test_create_skiplist_index_unique() throws ArangoException {

		final IndexEntity entity = driver.createIndex(collectionName, IndexType.SKIPLIST, true, "a", "b", "c", "d", "e",
			"f", "g");

		assertThat(entity, is(notNullValue()));
		assertThat(entity.getCode(), is(201));
		assertThat(entity.isError(), is(false));
		assertThat(entity.isNewlyCreated(), is(true));
		assertThat(entity.isGeoJson(), is(false));
		assertThat(entity.getId(), is(notNullValue()));
		assertThat(entity.getType(), is(IndexType.SKIPLIST));

	}

	@Test
	public void test_create_hash_index_with_document() throws ArangoException {

		for (int i = 0; i < 100; i++) {
			final TestComplexEntity01 value = new TestComplexEntity01("user_" + i, "", i);

			assertThat(driver.createDocument(collectionName, value, false), is(notNullValue()));
		}

		final IndexEntity entity = driver.createIndex(collectionName, IndexType.HASH, true, "name", "age");

		assertThat(entity, is(notNullValue()));
		assertThat(entity.getCode(), is(201));
		assertThat(entity.isError(), is(false));
		assertThat(entity.isNewlyCreated(), is(true));
		assertThat(entity.isGeoJson(), is(false));
		assertThat(entity.getId(), is(notNullValue()));
		assertThat(entity.getType(), is(IndexType.HASH));

	}

	@Test
	public void test_create_fulltext_index() throws ArangoException {

		// create test data 100 count.
		for (int i = 0; i < 100; i++) {
			final String desc = i % 2 == 0 ? "寿司" : "天ぷら";
			final TestComplexEntity01 value = new TestComplexEntity01("user_" + i, desc, i);
			assertThat(driver.createDocument(collectionName, value, false), is(notNullValue()));
		}

		// create fulltext index
		final IndexEntity index = driver.createFulltextIndex(collectionName, 1, "desc");

		// {"id":"unit_test_arango_index/6420761720","unique":false,"type":"fulltext","minLength":1,"fields":["desc"],"isNewlyCreated":true,"error":false,"code":201}
		assertThat(index.getCode(), is(201));
		assertThat(index.isError(), is(false));
		assertThat(index.getId(), is(not(nullValue())));
		assertThat(index.isUnique(), is(false));
		assertThat(index.getType(), is(IndexType.FULLTEXT));
		assertThat(index.getMinLength(), is(1));
		assertThat(index.getFields(), is(Arrays.asList("desc")));
		assertThat(index.isNewlyCreated(), is(true));

	}

	@Test
	public void test_create_persistent_index() throws ArangoException {

		final IndexEntity entity = driver.createIndex(collectionName, IndexType.PERSISTENT, false, "a", "b", "c", "d",
			"e", "f", "g");

		assertThat(entity, is(notNullValue()));
		assertThat(entity.getCode(), is(201));
		assertThat(entity.isError(), is(false));
		assertThat(entity.isNewlyCreated(), is(true));
		assertThat(entity.isGeoJson(), is(false));
		assertThat(entity.getId(), is(notNullValue()));
		assertThat(entity.getType(), is(IndexType.PERSISTENT));

	}

	@Test
	public void test_create_persistent_index2() throws ArangoException {

		final IndexEntity entity = driver.createPersistentIndex(collectionName, true, false, "a", "b", "c", "d", "e",
			"f", "g");

		assertThat(entity, is(notNullValue()));
		assertThat(entity.getCode(), is(201));
		assertThat(entity.isError(), is(false));
		assertThat(entity.isNewlyCreated(), is(true));
		assertThat(entity.isGeoJson(), is(false));
		assertThat(entity.getId(), is(notNullValue()));
		assertThat(entity.getType(), is(IndexType.PERSISTENT));

	}

	@Test
	public void test_delete_index() throws ArangoException {

		final IndexEntity entity = driver.createIndex(collectionName, IndexType.HASH, true, "name", "age");
		assertThat(entity, is(notNullValue()));
		assertThat(entity.getId(), is(notNullValue()));

		final String id = entity.getId();

		final IndexEntity entity2 = driver.deleteIndex(id);

		assertThat(entity2, is(notNullValue()));
		assertThat(entity2.getCode(), is(200));
		assertThat(entity2.isError(), is(false));
		assertThat(entity2.getId(), is(id));

	}

	@Test
	public void test_delete_index_pk() throws ArangoException {

		// PKは削除できない
		try {
			driver.deleteIndex(collectionName + "/0");
			fail("例外が飛ばないといけない");
		} catch (final ArangoException e) {
			assertThat(e.getErrorNumber(), is(1212));
		}

	}

	@Test
	public void test_delete_index_404_1() throws ArangoException {

		// コレクションは存在するが、存在しないインデックスを削除しようとした

		try {
			driver.deleteIndex(collectionName + "/1");
			fail("例外が飛ばないといけない");
		} catch (final ArangoException e) {
			assertThat(e.getErrorNumber(), is(1212));
		}

	}

	/**
	 * ユニークインデックスの列が重複した場合。 TODO: あとで
	 * 
	 * @throws ArangoException
	 */
	@Test
	@Ignore
	public void test_create_hash_index_dup_unique() throws ArangoException {

		final IndexEntity entity = driver.createIndex(collectionName, IndexType.HASH, true, "user", "age");

		assertThat(driver.createDocument(collectionName, new TestComplexEntity01("寿司天ぷら", "", 18), false),
			is(notNullValue()));
		assertThat(driver.createDocument(collectionName, new TestComplexEntity01("寿司天ぷら", "", 18), false),
			is(notNullValue()));

		assertThat(entity, is(notNullValue()));
		assertThat(entity.getCode(), is(201));
		assertThat(entity.isError(), is(false));
		assertThat(entity.isNewlyCreated(), is(true));
		assertThat(entity.isGeoJson(), is(false));
		assertThat(entity.getId(), is(notNullValue()));
		assertThat(entity.getType(), is(IndexType.HASH));

	}

	@Test
	public void test_getIndexes() throws ArangoException {

		final IndexEntity entity = driver.createIndex(collectionName, IndexType.HASH, true, "name", "age");
		assertThat(entity, is(notNullValue()));

		final IndexesEntity indexes = driver.getIndexes(collectionName);

		assertThat(indexes, is(notNullValue()));

		assertThat(indexes.getIndexes().size(), is(2));
		assertThat(indexes.getIndexes().get(0).getType(), is(IndexType.PRIMARY));
		assertThat(indexes.getIndexes().get(0).getFields().size(), is(1));
		assertThat(indexes.getIndexes().get(0).getFields().get(0), is("_key"));
		assertThat(indexes.getIndexes().get(1).getType(), is(IndexType.HASH));
		assertThat(indexes.getIndexes().get(1).getFields().size(), is(2));
		assertThat(indexes.getIndexes().get(1).getFields().get(1), is("name"));
		assertThat(indexes.getIndexes().get(1).getFields().get(0), is("age"));

		final String id1 = indexes.getIndexes().get(0).getId();
		final String id2 = indexes.getIndexes().get(1).getId();

		assertThat(indexes.getIdentifiers().size(), is(2));
		assertThat(indexes.getIdentifiers().get(id1).getType(), is(IndexType.PRIMARY));
		assertThat(indexes.getIdentifiers().get(id1).getFields().size(), is(1));
		assertThat(indexes.getIdentifiers().get(id1).getFields().get(0), is("_key"));
		assertThat(indexes.getIdentifiers().get(id2).getType(), is(IndexType.HASH));
		assertThat(indexes.getIdentifiers().get(id2).getFields().size(), is(2));
		assertThat(indexes.getIdentifiers().get(id2).getFields().get(1), is("name"));
		assertThat(indexes.getIdentifiers().get(id2).getFields().get(0), is("age"));

	}

}
