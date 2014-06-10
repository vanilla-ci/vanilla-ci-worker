package com.vanillaci.internal.annotations;

/**
 * Used to annotate a declaration as unused. This is so you can ignore, for example, a class that is used reflectively.
 * Instead of using @SuppressWarnings("UnusedDeclaration"), which would cause all unused declarations inside the whole class (including fields and parameters),
 * you can set up your IDE to understand that anything with this annotation should not give the "UnusedDeclaration" compiler warning.
 *
 * @author Joel Johnson
 */

public @interface ReflectivelyUsed {
}
