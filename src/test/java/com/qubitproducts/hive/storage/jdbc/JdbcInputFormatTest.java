/*
Copyright © 2014 QuBitDigital.com
All rights reserved.
 */
package com.qubitproducts.hive.storage.jdbc;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.mapred.InputSplit;
import org.apache.hadoop.mapred.JobConf;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.qubitproducts.hive.storage.jdbc.dao.DatabaseAccessor;
import com.qubitproducts.hive.storage.jdbc.exception.HiveJdbcDatabaseAccessException;

import java.io.IOException;

@RunWith(MockitoJUnitRunner.class)
public class JdbcInputFormatTest {

    @Mock
    private DatabaseAccessor mockDatabaseAccessor;


    @Test
    public void testSplitLogic_noSpillOver() throws HiveJdbcDatabaseAccessException, IOException {
        JdbcInputFormat f = new JdbcInputFormat();
        when(mockDatabaseAccessor.getTotalNumberOfRecords(any(Configuration.class))).thenReturn(15);
        f.setDbAccessor(mockDatabaseAccessor);

        JobConf conf = new JobConf();
        conf.set("mapred.input.dir", "/temp");
        InputSplit[] splits = f.getSplits(conf, 3);

        assertThat(splits, is(notNullValue()));
        assertThat(splits.length, is(3));

        assertThat(splits[0].getLength(), is(5L));
    }


    @Test
    public void testSplitLogic_withSpillOver() throws HiveJdbcDatabaseAccessException, IOException {
        JdbcInputFormat f = new JdbcInputFormat();
        when(mockDatabaseAccessor.getTotalNumberOfRecords(any(Configuration.class))).thenReturn(15);
        f.setDbAccessor(mockDatabaseAccessor);

        JobConf conf = new JobConf();
        conf.set("mapred.input.dir", "/temp");
        InputSplit[] splits = f.getSplits(conf, 6);

        assertThat(splits, is(notNullValue()));
        assertThat(splits.length, is(6));

        for (int i = 0; i < 3; i++) {
            assertThat(splits[i].getLength(), is(3L));
        }

        for (int i = 3; i < 6; i++) {
            assertThat(splits[i].getLength(), is(2L));
        }
    }
}
