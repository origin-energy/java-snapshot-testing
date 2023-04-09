package au.com.origin.snapshots.spock

import au.com.origin.snapshots.Expect
import au.com.origin.snapshots.ReflectionUtils
import au.com.origin.snapshots.SnapshotVerifier
import au.com.origin.snapshots.logging.LoggingHelper
import org.slf4j.LoggerFactory
import org.spockframework.runtime.extension.AbstractMethodInterceptor
import org.spockframework.runtime.extension.IMethodInvocation

import java.lang.reflect.Method

// Based on this issue: https://github.com/spockframework/spock/issues/652
class SnapshotMethodInterceptor extends AbstractMethodInterceptor {
    private log = LoggerFactory.getLogger( SnapshotMethodInterceptor.class )
    private final SnapshotVerifier snapshotVerifier;

    SnapshotMethodInterceptor(SnapshotVerifier snapshotVerifier) {
        this.snapshotVerifier = snapshotVerifier
    }

    @Override
    void interceptFeatureMethod(IMethodInvocation invocation) throws Throwable {
        updateInstanceVariable(invocation.instance, invocation.feature.featureMethod.reflection)

        def parameterCount = invocation.method.reflection.parameterCount
        if (parameterCount > invocation.arguments.length) {
            def newArguments = new Object[parameterCount]
            System.arraycopy invocation.arguments, 0, newArguments, 0, invocation.arguments.length
            invocation.arguments = newArguments
        }
        invocation.method.reflection.parameterTypes.eachWithIndex { type, i ->
            if (Expect.class == type) {
                LoggingHelper.deprecatedV5(log, "Injecting 'Expect' via method a argument is no longer recommended. Consider using instance variable injection instead.")
                invocation.arguments[i] = new Expect(snapshotVerifier, invocation.feature.featureMethod.reflection)
            }
        }
        invocation.proceed()
    }

    private void updateInstanceVariable(Object testInstance, Method testMethod) {
        ReflectionUtils.findFieldByPredicate(testInstance.class, { field -> field.getType() == Expect.class })
            .ifPresent({ field ->
                Expect expect = Expect.of(snapshotVerifier, testMethod);
                ReflectionUtils.makeAccessible(field);
                try {
                    field.set(testInstance, expect);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    @Override
    void interceptCleanupSpecMethod(IMethodInvocation invocation) throws Throwable {
        this.snapshotVerifier.validateSnapshots();
    }
}
