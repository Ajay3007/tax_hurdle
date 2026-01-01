import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.TestExecutionListener;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;
import org.junit.platform.engine.discovery.ClassSelector;
import org.junit.platform.engine.discovery.DiscoverySelectors;

import util.FIFOCalculatorTest;
import util.DataValidatorTest;
import util.QuarterConfigTest;
import util.ConfigFileManagerTest;
import exception.InvalidSecurityExceptionTest;

public class TestRunner {
    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("Running InvestingHurdle Unit Tests");
        System.out.println("========================================\n");
        
        LauncherDiscoveryRequest request = LauncherDiscoveryRequest.builder()
            .selectors(
                DiscoverySelectors.selectClass(FIFOCalculatorTest.class),
                DiscoverySelectors.selectClass(DataValidatorTest.class),
                DiscoverySelectors.selectClass(QuarterConfigTest.class),
                DiscoverySelectors.selectClass(ConfigFileManagerTest.class),
                DiscoverySelectors.selectClass(InvalidSecurityExceptionTest.class)
            )
            .build();
        
        SummaryGeneratingListener listener = new SummaryGeneratingListener();
        Launcher launcher = org.junit.platform.launcher.LauncherFactory.create(request);
        launcher.registerTestExecutionListeners(listener);
        launcher.execute(request);
        
        listener.getSummary().printTo(new org.junit.platform.launcher.listeners.TestExecutionSummaryPrinter());
        
        System.out.println("\n========================================");
        if (listener.getSummary().getFailures().isEmpty()) {
            System.out.println("✅ ALL TESTS PASSED!");
        } else {
            System.out.println("❌ SOME TESTS FAILED!");
            System.out.println("Failures: " + listener.getSummary().getFailures().size());
        }
        System.out.println("========================================");
    }
}
