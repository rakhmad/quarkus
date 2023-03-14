package io.quarkus.devtools.project.extensions;

import static io.quarkus.devtools.project.extensions.ScmInfoProvider.getSourceRepo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import uk.org.webcompere.systemstubs.environment.EnvironmentVariables;
import uk.org.webcompere.systemstubs.jupiter.SystemStub;
import uk.org.webcompere.systemstubs.jupiter.SystemStubsExtension;

@ExtendWith(SystemStubsExtension.class)
public class ScmInfoProviderTest {

    @SystemStub
    private EnvironmentVariables environment;

    @BeforeEach
    public void setUp() {
        environment.set("GITHUB_REPOSITORY", null);
    }

    @Test
    public void shouldReturnNullWhenNoEnvironmentOrBuildConfigIsPresent() {
        Map scm = getSourceRepo(null);
        // We shouldn't throw an exception or get upset, we should just quietly return null
        assertNull(scm);
    }

    // Easy case - we just read the info from the environment
    @Test
    void testGetSourceControlCoordinatesWhenOnlyEnvironmentIsSet() {
        String repoName = "org/place";
        environment.set("GITHUB_REPOSITORY", repoName);
        Map repo = getSourceRepo(null);
        assertNotNull(repo);
        assertEquals(repo.get("url").toString(), "https://github.com/org/place");
    }

    // Easy case - we just read the info from the pom
    @Test
    void testGetSourceControlCoordinatesWhenOnlyPomIsSet() {
        final String scmUrl = "https://github.com/org/frompom";
        Map repo = getSourceRepo(scmUrl);
        assertNotNull(repo);
        assertEquals(repo.get("url").toString(), scmUrl);
    }

    // Case where the pom info conflicts with the environment info; honour the environment info
    void testGetSourceControlCoordinatesWhenEnvironmentAndPomDisagree() {
        final String scmUrl = "https://github.com/org/frompom";
        String repoName = "org/place";
        environment.set("GITHUB_REPOSITORY", repoName);
        Map repo = getSourceRepo(null);
        assertNotNull(repo);
        // We should honour the environment info; out of the box, all quarkiverse extensions generated by the quarkus tooling will report a scm url of quarkiverse-parent/<project-name> in their model
        assertEquals(repo.get("url").toString(), "https://github.com/org/place");
    }

}
