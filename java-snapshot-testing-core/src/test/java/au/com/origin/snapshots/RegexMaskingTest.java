package au.com.origin.snapshots;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import au.com.origin.snapshots.annotations.SnapshotName;
import au.com.origin.snapshots.config.BaseSnapshotConfig;
import au.com.origin.snapshots.exceptions.SnapshotMatchException;
import au.com.origin.snapshots.serializers.SnapshotSerializer;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.PatternSyntaxException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class RegexMaskingTest {

  @TempDir Path tempDir;

  private SnapshotVerifier snapshotVerifier;
  private Method snapshotMethod;
  private Path snapshotPath;

  @BeforeEach
  void setUp() throws NoSuchMethodException {
    snapshotVerifier =
        new SnapshotVerifier(
            new BaseSnapshotConfig() {
              @Override
              public String getOutputDir() {
                return tempDir.toString();
              }
            },
            RegexMaskingTest.class);
    snapshotMethod = RegexMaskingTest.class.getDeclaredMethod("snapshot");
    snapshotPath = tempDir.resolve("au/com/origin/snapshots/__snapshots__/RegexMaskingTest.snap");
  }

  @SnapshotName("snapshot-2026-10-07")
  private void snapshot() {}

  private Expect expect() {
    return Expect.of(snapshotVerifier, snapshotMethod);
  }

  private Snapshot savedSnapshot() throws Exception {
    return Snapshot.parse(new String(Files.readAllBytes(snapshotPath), StandardCharsets.UTF_8));
  }

  @Test
  void shouldMatchDifferentDatesAfterMasking() throws Exception {
    expect()
        .mask("\\d{2}-\\d{2}-\\d{4}", "**-**-****")
        .toMatchSnapshot("Created on 07-10-2026 and updated on 08-10-2026");

    assertDoesNotThrow(
        () ->
            expect()
                .mask("\\d{2}-\\d{2}-\\d{4}", "**-**-****")
                .toMatchSnapshot("Created on 09-10-2026 and updated on 10-10-2026"));
    assertThat(savedSnapshot().getBody())
        .isEqualTo("[\nCreated on **-**-**** and updated on **-**-****\n]");
  }

  @Test
  void shouldStillFailForChangesOutsideMaskedValues() {
    expect().mask("\\d{4}-\\d{2}-\\d{2}", "DATE").toMatchSnapshot("Created on 2026-10-07");

    assertThrows(
        SnapshotMatchException.class,
        () ->
            expect().mask("\\d{4}-\\d{2}-\\d{2}", "DATE").toMatchSnapshot("Deleted on 2026-10-08"));
  }

  @Test
  void shouldApplyMasksInRegistrationOrder() throws Exception {
    expect().mask("\\d+", "NUMBER").mask("NUMBER", "MASKED").toMatchSnapshot("ID 12, ID 34");

    assertThat(savedSnapshot().getBody()).isEqualTo("[\nID MASKED, ID MASKED\n]");
  }

  @Test
  void shouldApplyDifferentReplacementsForTheSamePattern() throws Exception {
    expect().mask("x", "xx").mask("x", "y").toMatchSnapshot("x");

    assertThat(savedSnapshot().getBody()).isEqualTo("[\nyy\n]");
  }

  @Test
  void shouldMaskEmptyLinesAndTabsWithMultilinePatterns() throws Exception {
    expect()
        .mask("(?m)^$", "EMPTY")
        .mask("(?m)^\\t+$", "TABS_ONLY")
        .toMatchSnapshot("first\n\n\t\t\nlast");

    assertThat(savedSnapshot().getBody()).isEqualTo("[\nfirst\nEMPTY\nTABS_ONLY\nlast\n]");
  }

  @Test
  void shouldLeaveNonMatchingTextUnchanged() throws Exception {
    expect().mask("\\d+", "NUMBER").toMatchSnapshot("Hello World");

    assertThat(savedSnapshot().getBody()).isEqualTo("[\nHello World\n]");
  }

  @Test
  void shouldSupportCaptureGroupsInReplacement() throws Exception {
    expect().mask("(ID: )\\d+", "$1MASKED").toMatchSnapshot("ID: 123");

    assertThat(savedSnapshot().getBody()).isEqualTo("[\nID: MASKED\n]");
  }

  @Test
  void shouldSupportEscapedLiteralReplacement() throws Exception {
    String replacement = "$amount\\path";
    expect().mask("\\d+", Matcher.quoteReplacement(replacement)).toMatchSnapshot("123");

    assertThat(savedSnapshot().getBody()).isEqualTo("[\n" + replacement + "\n]");
  }

  @Test
  void shouldSupportRemovingMatchedText() throws Exception {
    expect().mask("\\d+", "").toMatchSnapshot("ID 123");

    assertThat(savedSnapshot().getBody()).isEqualTo("[\nID \n]");
  }

  @Test
  void shouldPreserveSnapshotMetadataFromCustomSerializer() throws Exception {
    SnapshotSerializer serializer =
        new SnapshotSerializer() {
          @Override
          public Snapshot apply(Object object, SnapshotSerializerContext context) {
            return Snapshot.builder()
                .name("custom-123")
                .scenario("scenario-456")
                .header(context.getHeader())
                .body("[\nID 789\n]")
                .build();
          }

          @Override
          public String getOutputFormat() {
            return "TEXT";
          }
        };

    expect()
        .serializer(serializer)
        .header("version", "123")
        .mask("\\d+", "NUMBER")
        .toMatchSnapshot(new Object());

    Snapshot saved = savedSnapshot();
    assertThat(saved.getName()).isEqualTo("custom-123");
    assertThat(saved.getScenario()).isEqualTo("scenario-456");
    assertThat(saved.getHeader().get("version")).isEqualTo("123");
    assertThat(saved.getBody()).isEqualTo("[\nID NUMBER\n]");
  }

  @Test
  void shouldReuseMasksAcrossScenarios() throws Exception {
    Expect expect = expect().mask("\\d+", "NUMBER");
    expect.scenario("first").toMatchSnapshot("ID 123");
    expect.scenario("second").toMatchSnapshot("ID 456");

    assertDoesNotThrow(() -> expect.scenario("first").toMatchSnapshot("ID 789"));
    assertDoesNotThrow(() -> expect.scenario("second").toMatchSnapshot("ID 987"));
    String saved = new String(Files.readAllBytes(snapshotPath), StandardCharsets.UTF_8);
    assertThat(saved).contains("snapshot-2026-10-07[first]=[\nID NUMBER\n]");
    assertThat(saved).contains("snapshot-2026-10-07[second]=[\nID NUMBER\n]");
  }

  @Test
  void shouldKeepMasksLocalToEachExpectation() {
    expect().mask("\\d+", "NUMBER").toMatchSnapshot("ID 123");

    assertThrows(SnapshotMatchException.class, () -> expect().toMatchSnapshot("ID 123"));
  }

  @Test
  void shouldRejectInvalidRegexWhenRegisteringMask() {
    assertThrows(PatternSyntaxException.class, () -> expect().mask("[", "MASKED"));
  }

  @Test
  void shouldRejectNullPattern() {
    assertThrows(NullPointerException.class, () -> expect().mask(null, "MASKED"));
  }

  @Test
  void shouldRejectNullReplacement() {
    assertThrows(NullPointerException.class, () -> expect().mask("\\d+", null));
  }
}
