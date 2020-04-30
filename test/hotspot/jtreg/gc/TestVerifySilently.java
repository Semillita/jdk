/*
 * Copyright (c) 2014, 2019, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

package gc;

/* @test TestVerifySilently.java
 * @key gc
 * @bug 8032771
 * @summary Test silent verification.
 * @requires vm.gc != "Z"
 * @library /test/lib
 * @modules java.base/jdk.internal.misc
 * @run main gc.TestVerifySilently
 */

import jdk.test.lib.process.ProcessTools;
import jdk.test.lib.process.OutputAnalyzer;
import java.util.ArrayList;
import java.util.Collections;
import jdk.test.lib.Utils;

class TestVerifySilentlyRunSystemGC {
  public static void main(String args[]) throws Exception {
    System.gc();
  }
}


public class TestVerifySilently {

  private static OutputAnalyzer runTest(boolean verifySilently) throws Exception {
    ArrayList<String> vmOpts = new ArrayList<>();

    Collections.addAll(vmOpts, Utils.getFilteredTestJavaOpts("-Xlog.*"));
    Collections.addAll(vmOpts, new String[] {"-XX:+UnlockDiagnosticVMOptions",
                                             "-XX:+VerifyDuringStartup",
                                             "-XX:+VerifyBeforeGC",
                                             "-XX:+VerifyAfterGC",
                                             (verifySilently ? "-Xlog:gc":"-Xlog:gc+verify=debug"),
                                             TestVerifySilentlyRunSystemGC.class.getName()});
    ProcessBuilder pb = ProcessTools.createJavaProcessBuilder(vmOpts);
    OutputAnalyzer output = new OutputAnalyzer(pb.start());

    System.out.println("Output:\n" + output.getOutput());
    return output;
  }


  public static void main(String args[]) throws Exception {

    OutputAnalyzer output;

    output = runTest(false);
    output.shouldContain("Verifying");
    output.shouldHaveExitValue(0);

    output = runTest(true);
    output.shouldNotContain("Verifying");
    output.shouldHaveExitValue(0);
  }
}
