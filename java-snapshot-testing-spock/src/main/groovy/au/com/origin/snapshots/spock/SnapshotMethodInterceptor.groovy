package au.com.origin.snapshots.spock

import au.com.origin.snapshots.Expect
import au.com.origin.snapshots.SnapshotVerifier
import org.spockframework.runtime.extension.IMethodInterceptor
import org.spockframework.runtime.extension.IMethodInvocation

import java.lang.reflect.Method

// Based on this issue: https://github.com/spockframework/spock/issues/652
class SnapshotMethodInterceptor implements IMethodInterceptor {

    private final SnapshotVerifier snapshotVerifier;

    SnapshotMethodInterceptor(SnapshotVerifier snapshotVerifier) {
        this.snapshotVerifier = snapshotVerifier
    }

    @Override
    void intercept(IMethodInvocation invocation) throws Throwable {
        updateInstanceVariable(invocation.instance, invocation.feature.featureMethod.reflection)

        def parameterCount = invocation.method.reflection.parameterCount
        if (parameterCount > invocation.arguments.length) {
            def newArguments = new Object[parameterCount]
            System.arraycopy invocation.arguments, 0, newArguments, 0, invocation.arguments.length
            invocation.arguments = newArguments
        }
        invocation.method.reflection.parameterTypes.eachWithIndex { type, i ->
            if (Expect.class == type) {
                invocation.arguments[i] = new Expect(snapshotVerifier, invocation.feature.featureMethod.reflection)
            }
        }
        invocation.proceed()
    }

    private void updateInstanceVariable(Object testInstance, Method testMethod) {
        testInstance.class.declaredFields
            .find { it.getType() == Expect.class }
            ?.with {
                Expect expect = Expect.of(snapshotVerifier, testMethod)
                it.setAccessible(true)
                it.set(testInstance, expect)
            }
    }
}
