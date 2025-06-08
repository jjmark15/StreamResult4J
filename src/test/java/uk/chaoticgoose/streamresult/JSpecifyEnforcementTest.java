package uk.chaoticgoose.streamresult;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.core.importer.ImportOption.Predefined.DO_NOT_INCLUDE_TESTS;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

public class JSpecifyEnforcementTest {

    @Test
    void annotateClassesWithNullMarked() {
        JavaClasses classes = new ClassFileImporter().withImportOption(DO_NOT_INCLUDE_TESTS).importPackages("uk.chaoticgoose.streamresult");
        ArchRule rule = classes().should().beAnnotatedWith(NullMarked.class);

        rule.check(classes);
    }
}
