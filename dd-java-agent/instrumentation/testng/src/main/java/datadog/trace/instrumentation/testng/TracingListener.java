package datadog.trace.instrumentation.testng;

import static datadog.trace.instrumentation.testng.TestNGDecorator.DECORATE;

import datadog.trace.api.civisibility.InstrumentationBridge;
import datadog.trace.api.civisibility.events.TestEventsHandler;
import java.lang.reflect.Method;
import java.util.List;
import org.testng.IConfigurationListener;
import org.testng.IExecutionListener;
import org.testng.ITestClass;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

public class TracingListener extends TestNGClassListener
    implements ITestListener, IExecutionListener, IConfigurationListener {

  private final TestEventsHandler testEventsHandler;

  private final String version;

  public TracingListener(final String version) {
    this.version = version;
    testEventsHandler = InstrumentationBridge.getTestEventsHandler(DECORATE);
  }

  @Override
  public void onStart(final ITestContext context) {
    // ignore
  }

  @Override
  public void onFinish(final ITestContext context) {
    // ignore
  }

  @Override
  public void onExecutionStart() {
    testEventsHandler.onTestModuleStart(version);
  }

  @Override
  public void onExecutionFinish() {
    testEventsHandler.onTestModuleFinish();
  }

  @Override
  protected void onBeforeClass(ITestClass testClass) {
    String testSuiteName = testClass.getName();
    Class<?> testSuiteClass = testClass.getRealClass();
    List<String> groups = TestNGUtils.getGroups(testClass);
    testEventsHandler.onTestSuiteStart(testSuiteName, testSuiteClass, version, groups);
  }

  @Override
  protected void onAfterClass(ITestClass testClass) {
    String testSuiteName = testClass.getName();
    Class<?> testSuiteClass = testClass.getRealClass();
    testEventsHandler.onTestSuiteFinish(testSuiteName, testSuiteClass);
  }

  @Override
  public void onConfigurationSuccess(ITestResult result) {
    // ignore
  }

  @Override
  public void onConfigurationFailure(ITestResult result) {
    // suite setup or suite teardown failed
    testEventsHandler.onFailure(result.getThrowable());
  }

  @Override
  public void onConfigurationSkip(ITestResult result) {
    // ignore
  }

  @Override
  public void onTestStart(final ITestResult result) {
    String testSuiteName = result.getInstanceName();
    String testName =
        (result.getTestName() != null) ? result.getTestName() : result.getMethod().getMethodName();
    String testParameters = TestNGUtils.getParameters(result);
    List<String> groups = TestNGUtils.getGroups(result);

    Class<?> testClass = TestNGUtils.getTestClass(result);
    Method testMethod = TestNGUtils.getTestMethod(result);

    testEventsHandler.onTestStart(
        testSuiteName, testName, testParameters, groups, version, testClass, testMethod);
  }

  @Override
  public void onTestSuccess(final ITestResult result) {
    final String testSuiteName = result.getInstanceName();
    final Class<?> testClass = TestNGUtils.getTestClass(result);
    testEventsHandler.onTestFinish(testSuiteName, testClass);
  }

  @Override
  public void onTestFailure(final ITestResult result) {
    final Throwable throwable = result.getThrowable();
    testEventsHandler.onFailure(throwable);

    final String testSuiteName = result.getInstanceName();
    final Class<?> testClass = TestNGUtils.getTestClass(result);
    testEventsHandler.onTestFinish(testSuiteName, testClass);
  }

  @Override
  public void onTestFailedButWithinSuccessPercentage(final ITestResult result) {
    onTestFailure(result);
  }

  @Override
  public void onTestSkipped(final ITestResult result) {
    // Typically the way of skipping a TestNG test is throwing a SkipException
    Throwable throwable = result.getThrowable();
    String reason = throwable != null ? throwable.getMessage() : null;
    testEventsHandler.onSkip(reason);

    final String testSuiteName = result.getInstanceName();
    final Class<?> testClass = TestNGUtils.getTestClass(result);
    testEventsHandler.onTestFinish(testSuiteName, testClass);
  }
}
