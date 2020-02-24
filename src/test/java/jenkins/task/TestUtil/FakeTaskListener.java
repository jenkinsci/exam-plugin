package jenkins.task.TestUtil;

import hudson.console.ConsoleNote;
import hudson.model.TaskListener;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;

public class FakeTaskListener implements TaskListener {
    @Nonnull
    @Override
    public PrintStream getLogger() {
        return null;
    }

    @Override
    public void annotate(ConsoleNote ann) throws IOException {

    }

    @Override
    public void hyperlink(String url, String text) throws IOException {

    }

    @Nonnull
    @Override
    public PrintWriter error(String msg) {
        return null;
    }

    @Nonnull
    @Override
    public PrintWriter error(String format, Object... args) {
        return null;
    }

    @Nonnull
    @Override
    public PrintWriter fatalError(String msg) {
        return null;
    }

    @Nonnull
    @Override
    public PrintWriter fatalError(String format, Object... args) {
        return null;
    }
}
