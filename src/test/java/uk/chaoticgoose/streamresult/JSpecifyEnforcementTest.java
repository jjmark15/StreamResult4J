package uk.chaoticgoose.streamresult;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.core.importer.ImportOption.Predefined.DO_NOT_INCLUDE_TESTS;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noMethods;

public class JSpecifyEnforcementTest {
    private static final JavaClasses CLASSES = new ClassFileImporter().withImportOption(DO_NOT_INCLUDE_TESTS).importPackages("uk.chaoticgoose.streamresult");

    @Test
    void annotateClassesWithNullMarked() {
        classes().should().beAnnotatedWith(NullMarked.class).check(CLASSES);
    }

    @Test
    void publicMethodsShouldNotReturnNullable() {
        noMethods().that().arePublic().or().arePackagePrivate().should().beAnnotatedWith(Nullable.class).check(CLASSES);
    }
}
