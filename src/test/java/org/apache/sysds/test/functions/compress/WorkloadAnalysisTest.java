/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.sysds.test.functions.compress;

import java.io.File;

import org.apache.sysds.common.Types.ExecMode;
import org.apache.sysds.hops.ipa.InterProceduralAnalysis;
import org.apache.sysds.test.AutomatedTestBase;
import org.apache.sysds.test.TestConfiguration;
import org.apache.sysds.test.TestUtils;
import org.junit.Test;

public class WorkloadAnalysisTest extends AutomatedTestBase
{
	private final static String TEST_NAME1 = "WorkloadAnalysisMlogreg";
	private final static String TEST_NAME2 = "WorkloadAnalysisLm";
	private final static String TEST_DIR = "functions/compress/";
	private final static String TEST_CLASS_DIR = TEST_DIR + WorkloadAnalysisTest.class.getSimpleName() + "/";

	@Override
	public void setUp() {
		TestUtils.clearAssertionInformation();
		addTestConfiguration(TEST_NAME1, new TestConfiguration(TEST_CLASS_DIR, TEST_NAME1, new String[]{"B"}));
		addTestConfiguration(TEST_NAME2, new TestConfiguration(TEST_CLASS_DIR, TEST_NAME2, new String[]{"B"}));
	}

	@Test
	public void testMlogregCP() {
		runWorkloadAnalysisTest(TEST_NAME1, ExecMode.HYBRID);
	}
	
	@Test
	public void testLmCP() {
		runWorkloadAnalysisTest(TEST_NAME2, ExecMode.HYBRID);
	}

	private void runWorkloadAnalysisTest(String testname, ExecMode mode)
	{
		ExecMode oldPlatform = setExecMode(mode);
		boolean oldFlag = InterProceduralAnalysis.CLA_WORKLOAD_ANALYSIS;
		
		try
		{
			loadTestConfiguration(getTestConfiguration(testname));
			
			InterProceduralAnalysis.CLA_WORKLOAD_ANALYSIS = true;
			String HOME = SCRIPT_DIR + TEST_DIR;
			fullDMLScriptName = HOME + testname + ".dml";
			programArgs = new String[]{"-args", input("X"), input("y"), output("B") };

			double[][] X = getRandomMatrix(10000, 20, 0, 1, 1.0, 7);
			writeInputMatrixWithMTD("X", X, false);
			double[][] y = TestUtils.round(getRandomMatrix(10000, 1, 1, 2, 1.0, 3));
			writeInputMatrixWithMTD("y", y, false);

			runTest(true, false, null, -1);
			//TODO check for compressed operations 
			//(right now test only checks that the workload analysis does not crash)
		}
		finally {
			resetExecMode(oldPlatform);
			InterProceduralAnalysis.CLA_WORKLOAD_ANALYSIS = oldFlag;
		}
	}
	
	@Override
	protected File getConfigTemplateFile() {
		return new File(SCRIPT_DIR + TEST_DIR + "force", "SystemDS-config-compress.xml");
	}
}
