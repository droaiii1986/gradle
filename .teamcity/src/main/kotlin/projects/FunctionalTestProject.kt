package projects

import common.Os
import configurations.FunctionalTest
import jetbrains.buildServer.configs.kotlin.v2019_2.Project
import model.CIBuildModel
import model.FunctionalTestBucketProvider
import model.Stage
import model.TestCoverage
import model.TestType
import model.getBucketUuid

const val DEFAULT_FUNCTIONAL_TEST_BUCKET_SIZE = 50
const val DEFAULT_LINUX_FUNCTIONAL_TEST_BUCKET_SIZE = 30

class FunctionalTestProject(
    val model: CIBuildModel,
    functionalTestBucketProvider: FunctionalTestBucketProvider,
    val testCoverage: TestCoverage,
    val stage: Stage
) : Project({
    this.id(testCoverage.asId(model))
    this.name = testCoverage.asName()
}) {
    val functionalTests: List<FunctionalTest> = functionalTestBucketProvider.createFunctionalTestsFor(stage, testCoverage)
    private val dummyLinuxFunctionalTests: List<FunctionalTest> = createDummyBuckets()

    init {
        functionalTests.forEach(this::buildType)
        dummyLinuxFunctionalTests.forEach(this::buildType)
    }

    /**
     * These build configurations are used to retain build histories. For example, we had 50 buckets before,
     * if we remove 20 of them, the histories are not accessible any more. As a workaround, we don't remove
     * the 20 buckets but not dependencies of trigger build.
     */
    private fun createDummyBuckets(): List<FunctionalTest> {
        if (testCoverage.os != Os.LINUX) {
            return emptyList()
        }
        if (testCoverage.testType == TestType.quickFeedbackCrossVersion ||
            testCoverage.testType == TestType.allVersionsCrossVersion ||
            testCoverage.testType == TestType.allVersionsIntegMultiVersion
        ) {
            return emptyList()
        }
        return (functionalTests.size until DEFAULT_FUNCTIONAL_TEST_BUCKET_SIZE).map {
            FunctionalTest(
                model,
                testCoverage.getBucketUuid(model, it),
                "${testCoverage.asName()} (bucket${it + 1})",
                "${testCoverage.asName()} (bucket${it + 1})",
                testCoverage,
                stage,
                listOf("dummy")
            )
        }
    }
}
