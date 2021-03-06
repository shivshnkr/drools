package org.drools.compiler.integrationtests;

import static org.junit.Assert.fail;

import java.io.Serializable;
import java.io.StringReader;
import java.util.Arrays;

import org.drools.compiler.CommonTestMethodBase;
import org.drools.core.RuleBase;
import org.drools.core.RuleBaseFactory;
import org.drools.core.WorkingMemory;
import org.drools.compiler.compiler.PackageBuilder;
import org.drools.core.definitions.impl.KnowledgePackageImp;
import org.junit.Test;
import org.kie.api.runtime.KieSession;
import org.kie.internal.KnowledgeBase;
import org.kie.internal.KnowledgeBaseFactory;
import org.kie.internal.definition.KnowledgePackage;

public class DroolsTest extends CommonTestMethodBase  {
    private final static int NUM_FACTS = 20;

    private static int       counter;

    public static class Foo implements Serializable {
        private final int id;

        public Foo(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }
    }

    public static class Bar implements Serializable {
        private final int id;

        public Bar(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }
    }

    @Test
    public void test1() throws Exception {
        String str = "package org.drools.compiler.integrationtests;\n";
        str += "import " + DroolsTest.class.getName() + ";\n";
        str += "import " + DroolsTest.class.getName() + ".Foo;\n";
        str += "import " + DroolsTest.class.getName() + ".Bar;\n";
        str += "rule test\n";
        str += "when\n";
        str += "      Foo($p : id, id < " + Integer.toString( NUM_FACTS ) + ")\n";
        str += "      Bar(id == $p)\n";
        str += "then\n";
        str += "   DroolsTest.incCounter();\n";
        str += "end\n";

        counter = 0;

        KnowledgeBase kbase = loadKnowledgeBaseFromString(str);

        KieSession wm = createKnowledgeSession(kbase);

        for ( int i = 0; i < NUM_FACTS; i++ ) {
            wm.insert( new Foo( i ) );
            wm.insert( new Bar( i ) );
        }
        PackageBuilder bld = new PackageBuilder();
        bld.addPackageFromDrl( new StringReader( str ) );
        if ( bld.hasErrors() ) {
            fail( bld.getErrors().toString() );
        }

        kbase.addKnowledgePackages(Arrays.asList(new KnowledgePackage[]{new KnowledgePackageImp(bld.getPackage())}) );

        wm.fireAllRules();
        System.out.println( counter + ":" + (counter == NUM_FACTS ? "passed" : "failed" ));
    }

    public static void incCounter() {
        ++counter;
    }
}
