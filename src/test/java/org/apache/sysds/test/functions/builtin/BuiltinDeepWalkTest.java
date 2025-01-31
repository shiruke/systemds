package org.apache.sysds.test.functions.builtin;

import org.apache.sysds.common.Types;
import org.apache.sysds.common.Types.ExecMode;
import org.apache.sysds.common.Types.FileFormat;
import org.apache.sysds.common.Types.ExecType;
import org.apache.sysds.runtime.io.FrameWriter;
import org.apache.sysds.runtime.io.FrameWriterFactory;
import org.apache.sysds.runtime.matrix.data.FrameBlock;
import org.apache.sysds.runtime.matrix.data.MatrixValue;
import org.apache.sysds.test.AutomatedTestBase;
import org.apache.sysds.test.TestConfiguration;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class BuiltinDeepWalkTest extends AutomatedTestBase {

    private final static String TEST_NAME = "deepWalk";
    private final static String TEST_DIR = "functions/builtin/";
    private static final String TEST_CLASS_DIR = TEST_DIR + BuiltinDeepWalkTest.class.getSimpleName() + "/";
    private final static String RESOURCE_DIRECTORY = "./src/test/resources/datasets/";

    @Override
    public void setUp() {
        addTestConfiguration(TEST_NAME, new TestConfiguration(TEST_CLASS_DIR, TEST_NAME, new String[]{"B"}));
    }

    @Test
    public void testRunDeepWalkCP() throws IOException {
        runDeepWalk(5, 2, 5, 10, -1, -1, ExecType.CP);
    }

    // "GC overhead limit exceeded"-error
    @Ignore
    public void testRunDeepWalkSP() throws IOException {
        runDeepWalk(5, 2, 5, 10, -1, -1, ExecType.SPARK);
    }

    private void runDeepWalk(int window_size, int embedding_size, int walks_per_vertex, int walk_length,
                             double alpha, double beta, ExecType execType) throws IOException {
        ExecMode platformOld = setExecMode(execType);

        try {
            loadTestConfiguration(getTestConfiguration(TEST_NAME));
            String HOME = SCRIPT_DIR + TEST_DIR;
            fullDMLScriptName = HOME + TEST_NAME + ".dml";

            programArgs = new String[]{
                    "-nvargs", "GRAPH=" + RESOURCE_DIRECTORY + "caveman_4_20.ijv",
                    "WINDOW_SIZE=" + window_size,
                    "EMBEDDING_SIZE=" + embedding_size,
                    "WALKS_PER_VERTEX=" + walks_per_vertex,
                    "WALK_LENGTH=" + walk_length,
                    "OUT_FILE=" + output("B")
            };

            runTest(true, false, null, -1);

            // for verification plot the output "B" e.g.: in python -> clearly separable clusters for this test
        }
        finally {
            rtplatform = platformOld;
        }
    }
}
