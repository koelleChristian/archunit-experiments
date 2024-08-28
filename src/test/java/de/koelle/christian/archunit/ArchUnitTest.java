package de.koelle.christian.archunit;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;
import com.tngtech.archunit.library.Architectures;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

public class ArchUnitTest {

    private static final String PACKAGE_ROOT = "de.koelle.christian.archunit";
    private static final String LAYER1_PATH_ID = PACKAGE_ROOT + ".layer1a..";
    private static final String LAYER2_PATH_ID = PACKAGE_ROOT + ".layer2a..";
    private static final String LAYER3_PATH_ID = PACKAGE_ROOT + ".layer3a..";
    public static final JavaClasses OUR_CLASSES = new ClassFileImporter()
            .importPackages(PACKAGE_ROOT);

    @Test
    public void testLayersArchUnitFeature() {
        Architectures
                .layeredArchitecture()
                .consideringOnlyDependenciesInLayers()
                .layer("layer1").definedBy(LAYER1_PATH_ID)
                .layer("layer2").definedBy(LAYER2_PATH_ID)
                .layer("layer3").definedBy(LAYER3_PATH_ID)
                .whereLayer("layer1").mayNotBeAccessedByAnyLayer()
                .whereLayer("layer2").mayOnlyBeAccessedByLayers("layer1")
                .whereLayer("layer3").mayOnlyBeAccessedByLayers("layer2")
                .check(OUR_CLASSES);
    }

    public static Stream<Arguments> paramsTestLayersSelfmade() {
        return Stream.of(
                Arguments.of(LAYER1_PATH_ID, List.of(LAYER3_PATH_ID)),
                Arguments.of(LAYER2_PATH_ID, List.of(LAYER1_PATH_ID)),
                Arguments.of(LAYER3_PATH_ID, List.of(LAYER1_PATH_ID, LAYER2_PATH_ID))
        );
    }

    @MethodSource("paramsTestLayersSelfmade")
    @ParameterizedTest
    public void testLayersSelfmadeDependOn(String packagePathToCheck, List<String> forbiddenPackagePaths) {
        noClassShouldUse(
                CheckType.DEPEND_ON_CLASSES_THAT,
                packagePathToCheck,
                forbiddenPackagePaths,
                true);
    }

    @MethodSource("paramsTestLayersSelfmade")
    @ParameterizedTest
    public void testLayersSelfmadeAccess(String packagePathToCheck, List<String> forbiddenPackagePaths) {
        // accessClassesThat() is a little weaker than dependOnClassesThat() according to documentation
        noClassShouldUse(
                CheckType.ACCESS_CLASSES_THAT,
                packagePathToCheck,
                forbiddenPackagePaths,
                true);
    }


    public enum CheckType {
        DEPEND_ON_CLASSES_THAT,
        ACCESS_CLASSES_THAT
    }


    private void noClassShouldUse(final CheckType checkType, final String pathToCheck,
                                  final Collection<String> forbiddenPaths,
                                  final boolean assertTheExistenceOfClasses) {
        noClassShouldUse(checkType, pathToCheck, forbiddenPaths.toArray(
                new String[0]), assertTheExistenceOfClasses);
    }

    private void noClassShouldUse(final CheckType checkType, final String pathToCheck,
                                  final String[] forbiddenPaths,
                                  final boolean assertTheExistenceOfClasses) {
        if (checkType == CheckType.DEPEND_ON_CLASSES_THAT) {
            ArchRuleDefinition
                    .noClasses().that().resideInAPackage(pathToCheck)
                    .should().dependOnClassesThat().resideInAnyPackage(forbiddenPaths)
                    .allowEmptyShould(!assertTheExistenceOfClasses)
                    .check(OUR_CLASSES);
        } else if (checkType == CheckType.ACCESS_CLASSES_THAT) {
            ArchRuleDefinition
                    .noClasses().that().resideInAPackage(pathToCheck)
                    .should().accessClassesThat().resideInAnyPackage(forbiddenPaths)
                    .allowEmptyShould(!assertTheExistenceOfClasses)
                    .check(OUR_CLASSES);
        }
    }
}
